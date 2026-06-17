package com.example.fashionshopmobile.api;

import com.example.fashionshopmobile.model.Category;
import com.example.fashionshopmobile.model.Product;
import com.example.fashionshopmobile.model.ProductVariant;
import com.example.fashionshopmobile.model.StoreLocation;
import com.example.fashionshopmobile.model.User;
import com.example.fashionshopmobile.request.UserSyncRequest;
import com.example.fashionshopmobile.model.CartItem;
import com.example.fashionshopmobile.request.UpdateCartItemRequest;
import com.example.fashionshopmobile.model.CartItem;
import com.example.fashionshopmobile.request.AddCartRequest;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.DELETE;
import retrofit2.http.PUT;

public interface ApiService {

    @GET("api/products")
    Call<List<Product>> getProducts();
    @GET("api/products/{id}")
    Call<Product> getProductById(@Path("id") Long id);

    @GET("api/products/{productId}/variants")
    Call<List<ProductVariant>> getProductVariants(@Path("productId") Long productId);

    @POST("api/users/sync")
    Call<User> syncUser(@Body UserSyncRequest request);

    @GET("api/categories")
    Call<List<Category>> getCategories();
    @GET("api/products/category/{categoryId}")
    Call<List<Product>> getProductsByCategory(@Path("categoryId") Long categoryId);
    @GET("api/cart/user/{userId}")
    Call<List<CartItem>> getCartByUserId(@Path("userId") Long userId);

    @PUT("api/cart/{cartItemId}")
    Call<CartItem> updateCartItem(
            @Path("cartItemId") Long cartItemId,
            @Body UpdateCartItemRequest request
    );

    @DELETE("api/cart/{cartItemId}")
    Call<Void> deleteCartItem(@Path("cartItemId") Long cartItemId);
    @POST("api/cart")
    Call<CartItem> addToCart(@Body AddCartRequest request);


    @GET("api/stores")
    Call<List<StoreLocation>> getStores();

    @GET("api/stores/{id}")
    Call<StoreLocation> getStoreById(@Path("id") Long id);
}
