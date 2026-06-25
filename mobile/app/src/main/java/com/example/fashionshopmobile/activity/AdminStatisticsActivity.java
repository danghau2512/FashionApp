package com.example.fashionshopmobile.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fashionshopmobile.R;
import com.example.fashionshopmobile.adapter.ProductStatisticAdapter;
import com.example.fashionshopmobile.api.ApiClient;
import com.example.fashionshopmobile.model.AdminStatistics;
import com.example.fashionshopmobile.model.ProductStatistic;
import com.example.fashionshopmobile.utils.SessionManager;
import com.example.fashionshopmobile.view.MonthlyRevenueChartView;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminStatisticsActivity extends AppCompatActivity {

    private TextView btnBackDashboard;
    private EditText edtYear;
    private Button btnViewStatistics;
    private TextView txtRevenueTitle;
    private TextView txtTotalRevenue;
    private MonthlyRevenueChartView monthlyRevenueChart;
    private Spinner spBestSellerMonths;
    private Spinner spNoSaleMonths;
    private RecyclerView rvBestSellers;
    private RecyclerView rvNoSaleProducts;
    private TextView txtBestSellerEmpty;
    private TextView txtNoSaleEmpty;
    private ProgressBar progressBar;
    private TextView txtError;

    private SessionManager sessionManager;
    private ProductStatisticAdapter bestSellerAdapter;
    private ProductStatisticAdapter noSaleAdapter;

    private final List<Integer> monthValues = Arrays.asList(1, 3, 6, 12);
    private boolean spinnerReady = false;

    private final NumberFormat priceFormatter = NumberFormat.getInstance(new Locale("vi", "VN"));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_statistics);

        sessionManager = new SessionManager(this);

        initViews();
        setupRecyclerViews();
        setupSpinners();
        setupClickEvents();

        edtYear.setText(String.valueOf(getCurrentYear()));

        spinnerReady = true;
        loadStatistics();
    }

    private void initViews() {
        btnBackDashboard = findViewById(R.id.btnBackDashboard);
        edtYear = findViewById(R.id.edtYear);
        btnViewStatistics = findViewById(R.id.btnViewStatistics);
        txtRevenueTitle = findViewById(R.id.txtRevenueTitle);
        txtTotalRevenue = findViewById(R.id.txtTotalRevenue);
        monthlyRevenueChart = findViewById(R.id.monthlyRevenueChart);
        spBestSellerMonths = findViewById(R.id.spBestSellerMonths);
        spNoSaleMonths = findViewById(R.id.spNoSaleMonths);
        rvBestSellers = findViewById(R.id.rvBestSellers);
        rvNoSaleProducts = findViewById(R.id.rvNoSaleProducts);
        txtBestSellerEmpty = findViewById(R.id.txtBestSellerEmpty);
        txtNoSaleEmpty = findViewById(R.id.txtNoSaleEmpty);
        progressBar = findViewById(R.id.progressBar);
        txtError = findViewById(R.id.txtError);
    }

    private void setupRecyclerViews() {
        bestSellerAdapter = new ProductStatisticAdapter();
        noSaleAdapter = new ProductStatisticAdapter();

        rvBestSellers.setLayoutManager(new LinearLayoutManager(this));
        rvBestSellers.setAdapter(bestSellerAdapter);
        rvBestSellers.setNestedScrollingEnabled(true);

        rvNoSaleProducts.setLayoutManager(new LinearLayoutManager(this));
        rvNoSaleProducts.setAdapter(noSaleAdapter);
        rvNoSaleProducts.setNestedScrollingEnabled(true);
    }

    private void setupSpinners() {
        List<String> labels = Arrays.asList("1 tháng", "3 tháng", "6 tháng", "12 tháng");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                labels
        );

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spBestSellerMonths.setAdapter(adapter);
        spNoSaleMonths.setAdapter(adapter);

        AdapterView.OnItemSelectedListener listener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (spinnerReady) {
                    loadStatistics();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        };

        spBestSellerMonths.setOnItemSelectedListener(listener);
        spNoSaleMonths.setOnItemSelectedListener(listener);
    }

    private void setupClickEvents() {
        btnBackDashboard.setOnClickListener(v -> finish());
        btnViewStatistics.setOnClickListener(v -> loadStatistics());
    }

    private void loadStatistics() {
        Long adminId = sessionManager.getUserId();

        if (adminId == null) {
            Toast.makeText(this, "Không tìm thấy adminId, vui lòng đăng nhập lại", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        int year = getSelectedYear();
        int bestSellerMonths = getSelectedBestSellerMonths();
        int noSaleMonths = getSelectedNoSaleMonths();

        showLoading(true);
        txtError.setText("");

        ApiClient.getApiService()
                .getAdminStatistics(adminId, year, bestSellerMonths, noSaleMonths)
                .enqueue(new Callback<AdminStatistics>() {
                    @Override
                    public void onResponse(Call<AdminStatistics> call, Response<AdminStatistics> response) {
                        showLoading(false);

                        if (response.isSuccessful() && response.body() != null) {
                            showStatistics(response.body());
                        } else if (response.code() == 403) {
                            Toast.makeText(AdminStatisticsActivity.this, "Bạn không có quyền xem thống kê", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            String message = "Không lấy được dữ liệu thống kê. Mã lỗi: " + response.code();
                            txtError.setText(message);
                            Toast.makeText(AdminStatisticsActivity.this, message, Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<AdminStatistics> call, Throwable t) {
                        showLoading(false);

                        String message = "Lỗi API thống kê: " + t.getMessage();
                        txtError.setText(message);
                        Toast.makeText(AdminStatisticsActivity.this, message, Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void showStatistics(AdminStatistics statistics) {
        Integer year = statistics.getYear() != null ? statistics.getYear() : getSelectedYear();

        txtRevenueTitle.setText("Doanh thu năm " + year);
        txtTotalRevenue.setText(formatPrice(statistics.getTotalRevenue()));
        monthlyRevenueChart.setMonthlyRevenue(statistics.getMonthlyRevenue());

        showProductList(
                statistics.getBestSellers(),
                bestSellerAdapter,
                txtBestSellerEmpty,
                "Chưa có sản phẩm bán chạy"
        );

        showProductList(
                statistics.getNoSaleProducts(),
                noSaleAdapter,
                txtNoSaleEmpty,
                "Không có sản phẩm nào trong danh sách"
        );
    }

    private void showProductList(List<ProductStatistic> products,
                                 ProductStatisticAdapter adapter,
                                 TextView emptyText,
                                 String emptyMessage) {
        adapter.setData(products);

        if (products == null || products.isEmpty()) {
            emptyText.setText(emptyMessage);
            emptyText.setVisibility(View.VISIBLE);
        } else {
            emptyText.setVisibility(View.GONE);
        }
    }

    private int getSelectedYear() {
        String yearText = edtYear.getText().toString().trim();

        if (yearText.isEmpty()) {
            return getCurrentYear();
        }

        try {
            return Integer.parseInt(yearText);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Năm không hợp lệ, dùng năm hiện tại", Toast.LENGTH_SHORT).show();
            edtYear.setText(String.valueOf(getCurrentYear()));
            return getCurrentYear();
        }
    }

    private int getSelectedBestSellerMonths() {
        int position = spBestSellerMonths.getSelectedItemPosition();

        if (position < 0 || position >= monthValues.size()) {
            return 1;
        }

        return monthValues.get(position);
    }

    private int getSelectedNoSaleMonths() {
        int position = spNoSaleMonths.getSelectedItemPosition();

        if (position < 0 || position >= monthValues.size()) {
            return 1;
        }

        return monthValues.get(position);
    }

    private int getCurrentYear() {
        return Calendar.getInstance().get(Calendar.YEAR);
    }

    private void showLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        btnViewStatistics.setEnabled(!isLoading);
    }

    private String formatPrice(BigDecimal price) {
        if (price == null) {
            return "0đ";
        }

        return priceFormatter.format(price) + "đ";
    }
}