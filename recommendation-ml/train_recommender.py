import os
from datetime import datetime

import numpy as np
import pandas as pd
from dotenv import load_dotenv
from implicit.als import AlternatingLeastSquares
from scipy.sparse import csr_matrix
from sqlalchemy import URL, create_engine, text


TOP_N = 8

EVENT_WEIGHTS = {
    "VIEW": 1,
    "ADD_TO_CART": 3,
    "PURCHASE": 5
}

EVENT_LIMITS = {
    "VIEW": 5,
    "ADD_TO_CART": 3,
    "PURCHASE": 100
}


def create_database_engine():
    load_dotenv()

    database_url = URL.create(
        drivername="mysql+pymysql",
        username=os.getenv("DB_USER"),
        password=os.getenv("DB_PASSWORD"),
        host=os.getenv("DB_HOST", "localhost"),
        port=int(os.getenv("DB_PORT", "3306")),
        database=os.getenv("DB_NAME")
    )

    return create_engine(database_url)


def load_events(engine):
    sql = """
        SELECT event.user_id, event.product_id, event.event_type
        FROM user_product_events event
        JOIN products product ON product.id = event.product_id
        WHERE product.status = 'ACTIVE'
    """

    return pd.read_sql(sql, engine)


def prepare_interactions(events):
    events = events[events["event_type"].isin(EVENT_WEIGHTS.keys())].copy()

    grouped = events.groupby(
        ["user_id", "product_id", "event_type"]
    ).size().reset_index(name="event_count")

    grouped["event_count"] = grouped.apply(
        lambda row: min(
            row["event_count"],
            EVENT_LIMITS[row["event_type"]]
        ),
        axis=1
    )

    grouped["strength"] = grouped.apply(
        lambda row: row["event_count"]
        * EVENT_WEIGHTS[row["event_type"]],
        axis=1
    )

    interactions = grouped.groupby(
        ["user_id", "product_id"],
        as_index=False
    )["strength"].sum()

    return interactions


def train_and_generate(interactions):
    user_ids = sorted(interactions["user_id"].unique())
    product_ids = sorted(interactions["product_id"].unique())

    if len(user_ids) < 2 or len(product_ids) < 2:
        raise RuntimeError(
            "Cần ít nhất 2 người dùng và 2 sản phẩm có hành vi để train ALS"
        )

    user_to_index = {
        user_id: index
        for index, user_id in enumerate(user_ids)
    }

    product_to_index = {
        product_id: index
        for index, product_id in enumerate(product_ids)
    }

    index_to_product = {
        index: product_id
        for product_id, index in product_to_index.items()
    }

    rows = interactions["user_id"].map(user_to_index).to_numpy()
    columns = interactions["product_id"].map(product_to_index).to_numpy()
    values = interactions["strength"].astype(np.float32).to_numpy()

    user_item_matrix = csr_matrix(
        (values, (rows, columns)),
        shape=(len(user_ids), len(product_ids)),
        dtype=np.float32
    )

    model = AlternatingLeastSquares(
        factors=16,
        regularization=0.1,
        iterations=20,
        random_state=42
    )

    model.fit(user_item_matrix)

    popularity = (
        interactions.groupby("product_id")["strength"]
        .sum()
        .sort_values(ascending=False)
        .index
        .tolist()
    )

    results = []
    generated_at = datetime.now()

    for user_id in user_ids:
        user_index = user_to_index[user_id]

        recommended_indices, scores = model.recommend(
            userid=user_index,
            user_items=user_item_matrix[user_index],
            N=min(TOP_N, len(product_ids)),
            filter_already_liked_items=True
        )

        recommended_products = []

        for product_index, score in zip(
            recommended_indices,
            scores
        ):
            if product_index < 0:
                continue
            if not np.isfinite(score):
                continue
            if score <= -1e20:
                continue


            product_id = index_to_product[int(product_index)]

            recommended_products.append(
                (product_id, float(score))
            )

        selected_ids = {
            product_id
            for product_id, _ in recommended_products
        }

        for product_id in popularity:
            if len(recommended_products) >= TOP_N:
                break

            if product_id not in selected_ids:
                recommended_products.append((product_id, 0.0))
                selected_ids.add(product_id)

        for rank, (product_id, score) in enumerate(
            recommended_products[:TOP_N],
            start=1
        ):
            results.append({
                "user_id": int(user_id),
                "product_id": int(product_id),
                "recommendation_score": score,
                "rank_number": rank,
                "generated_at": generated_at
            })

    return pd.DataFrame(results)


def save_recommendations(engine, recommendations):
    with engine.begin() as connection:
        connection.execute(
            text("DELETE FROM product_recommendations")
        )

        if not recommendations.empty:
            recommendations.to_sql(
                name="product_recommendations",
                con=connection,
                if_exists="append",
                index=False
            )


def main():
    engine = create_database_engine()

    events = load_events(engine)

    if events.empty:
        print("Chưa có dữ liệu hành vi để huấn luyện.")
        return

    interactions = prepare_interactions(events)

    print("Dữ liệu dùng để train:")
    print(interactions)

    recommendations = train_and_generate(interactions)
    save_recommendations(engine, recommendations)

    print("Đã tạo", len(recommendations), "kết quả gợi ý.")
    print(recommendations.head(20))


if __name__ == "__main__":
    main()