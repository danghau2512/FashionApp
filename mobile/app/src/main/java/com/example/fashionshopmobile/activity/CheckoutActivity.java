package com.example.fashionshopmobile.activity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fashionshopmobile.R;
import com.example.fashionshopmobile.adapter.CheckoutProductAdapter;
import com.example.fashionshopmobile.api.ApiClient;
import com.example.fashionshopmobile.model.CartItem;
import com.example.fashionshopmobile.model.OrderResponse;
import com.example.fashionshopmobile.model.UserAddress;
import com.example.fashionshopmobile.model.VnPayPaymentResponse;
import com.example.fashionshopmobile.model.shipping.ShippingQuote;
import com.example.fashionshopmobile.request.CreateOrderRequest;
import com.example.fashionshopmobile.request.ShippingQuoteRequest;
import com.example.fashionshopmobile.utils.SessionManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CheckoutActivity extends AppCompatActivity {

    private TextView btnBackCheckout;
    private TextView tvCheckoutReceiver;
    private TextView tvCheckoutAddress;
    private TextView tvChangeAddress;
    private TextView tvShippingFee;
    private TextView tvProductTotal;
    private TextView tvSummaryShippingFee;
    private TextView tvDiscount;
    private TextView tvCheckoutTotal;
    private TextView tvBottomTotal;
    private TextView tvShippingMethod;
    private TextView tvEstimatedDelivery;
    private RadioButton rbCod;
    private RadioButton rbVnPay;
    private EditText edtCheckoutNote;
    private RecyclerView rvCheckoutProducts;
    private MaterialButton btnPlaceOrder;
    private MaterialCardView cardCheckoutAddress;
    private ProgressBar progressCheckout;

    private CheckoutProductAdapter productAdapter;
    private SessionManager sessionManager;

    private long[] selectedCartItemIds;
    private final List<CartItem> selectedItems = new ArrayList<>();

    private UserAddress selectedAddress;

    private boolean cartLoaded = false;
    private boolean addressLoaded = false;
    private boolean reloadAddressOnResume = false;
    private boolean shippingLoading = false;
    private boolean shippingAvailable = false;

    private BigDecimal productTotal = BigDecimal.ZERO;
    private BigDecimal shippingFee = BigDecimal.ZERO;
    private BigDecimal discountAmount = BigDecimal.ZERO;
    private BigDecimal totalAmount = BigDecimal.ZERO;

    private final NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        sessionManager = new SessionManager(this);
        selectedCartItemIds = getIntent().getLongArrayExtra("cart_item_ids");

        if (selectedCartItemIds == null || selectedCartItemIds.length == 0) {
            Toast.makeText(this, "Không có sản phẩm để thanh toán", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        setupRecyclerView();
        setupClickEvents();

        loadCheckoutData();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (reloadAddressOnResume) {
            reloadAddressOnResume = false;
            loadAddresses();
        }
    }

    private void initViews() {
        btnBackCheckout = findViewById(R.id.btnBackCheckout);
        tvCheckoutReceiver = findViewById(R.id.tvCheckoutReceiver);
        tvCheckoutAddress = findViewById(R.id.tvCheckoutAddress);
        tvChangeAddress = findViewById(R.id.tvChangeAddress);
        tvShippingMethod = findViewById(R.id.tvShippingMethod);
        tvEstimatedDelivery = findViewById(R.id.tvEstimatedDelivery);
        tvShippingFee = findViewById(R.id.tvShippingFee);
        tvProductTotal = findViewById(R.id.tvProductTotal);
        tvSummaryShippingFee = findViewById(R.id.tvSummaryShippingFee);
        tvDiscount = findViewById(R.id.tvDiscount);
        tvCheckoutTotal = findViewById(R.id.tvCheckoutTotal);
        tvBottomTotal = findViewById(R.id.tvBottomTotal);

        edtCheckoutNote = findViewById(R.id.edtCheckoutNote);
        rvCheckoutProducts = findViewById(R.id.rvCheckoutProducts);
        btnPlaceOrder = findViewById(R.id.btnPlaceOrder);
        cardCheckoutAddress = findViewById(R.id.cardCheckoutAddress);
        progressCheckout = findViewById(R.id.progressCheckout);
        rbCod = findViewById(R.id.rbCod);
        rbVnPay = findViewById(R.id.rbVnPay);
    }

    private void setupRecyclerView() {
        productAdapter = new CheckoutProductAdapter();

        rvCheckoutProducts.setLayoutManager(new LinearLayoutManager(this));
        rvCheckoutProducts.setAdapter(productAdapter);
        rvCheckoutProducts.setNestedScrollingEnabled(false);
    }

    private void setupClickEvents() {
        btnBackCheckout.setOnClickListener(view -> finish());

        cardCheckoutAddress.setOnClickListener(view -> openAddressScreen());
        tvChangeAddress.setOnClickListener(view -> openAddressScreen());

        btnPlaceOrder.setOnClickListener(view -> placeOrder());
    }

    private void loadCheckoutData() {
        progressCheckout.setVisibility(View.VISIBLE);

        loadSelectedCartItems();
        loadAddresses();
    }

    private void loadSelectedCartItems() {
        Long userId = sessionManager.getUserId();

        if (userId == null) {
            Toast.makeText(this, "Phiên đăng nhập không hợp lệ", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        cartLoaded = false;

        ApiClient.getApiService().getCartByUserId(userId).enqueue(new Callback<List<CartItem>>() {
            @Override
            public void onResponse(Call<List<CartItem>> call, Response<List<CartItem>> response) {
                cartLoaded = true;

                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(CheckoutActivity.this, "Không lấy được sản phẩm thanh toán", Toast.LENGTH_SHORT).show();
                    updateLoadingState();
                    return;
                }

                selectedItems.clear();

                for (CartItem item : response.body()) {
                    if (isSelectedItem(item.getId())) {
                        selectedItems.add(item);
                    }
                }

                if (selectedItems.size() != selectedCartItemIds.length) {
                    Toast.makeText(CheckoutActivity.this, "Có sản phẩm trong giỏ đã thay đổi", Toast.LENGTH_SHORT).show();
                    btnPlaceOrder.setEnabled(false);
                    updateLoadingState();
                    return;
                }

                productAdapter.setData(selectedItems);
                calculateSummary();
                tryLoadShippingQuote();
                updateLoadingState();
            }

            @Override
            public void onFailure(Call<List<CartItem>> call, Throwable t) {
                cartLoaded = true;
                updateLoadingState();

                Toast.makeText(CheckoutActivity.this, "Lỗi tải sản phẩm: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void loadAddresses() {
        Long userId = sessionManager.getUserId();

        if (userId == null) {
            return;
        }

        addressLoaded = false;

        ApiClient.getApiService().getAddressesByUserId(userId).enqueue(new Callback<List<UserAddress>>() {
            @Override
            public void onResponse(Call<List<UserAddress>> call, Response<List<UserAddress>> response) {
                addressLoaded = true;

                if (!response.isSuccessful() || response.body() == null) {
                    showNoAddress();
                    updateLoadingState();
                    return;
                }

                selectedAddress = findDefaultAddress(response.body());

                if (selectedAddress == null && !response.body().isEmpty()) {
                    selectedAddress = response.body().get(0);
                }

                if (selectedAddress == null) {
                    showNoAddress();
                } else {
                    showSelectedAddress();
                    tryLoadShippingQuote();
                }

                updateLoadingState();
            }

            @Override
            public void onFailure(Call<List<UserAddress>> call, Throwable t) {
                addressLoaded = true;
                showNoAddress();
                updateLoadingState();
            }
        });
    }

    private UserAddress findDefaultAddress(List<UserAddress> addresses) {
        for (UserAddress address : addresses) {
            if (Boolean.TRUE.equals(address.getDefaultAddress())) {
                return address;
            }
        }

        return null;
    }

    private void showSelectedAddress() {
        tvCheckoutReceiver.setText(selectedAddress.getReceiverName() + " | " + selectedAddress.getReceiverPhone());
        tvCheckoutAddress.setText(selectedAddress.getFullAddress());
        tvChangeAddress.setText("Thay đổi >");
    }

    private void showNoAddress() {
        selectedAddress = null;
        tvCheckoutReceiver.setText("Chưa có địa chỉ nhận hàng");
        tvCheckoutAddress.setText("Nhấn để thêm địa chỉ");
        tvChangeAddress.setText("Thêm địa chỉ >");
    }

    private void openAddressScreen() {
        reloadAddressOnResume = true;

        Intent intent = new Intent(CheckoutActivity.this, AddressActivity.class);
        startActivity(intent);
    }

    private boolean isSelectedItem(Long cartItemId) {
        if (cartItemId == null) {
            return false;
        }

        for (long selectedId : selectedCartItemIds) {
            if (cartItemId == selectedId) {
                return true;
            }
        }

        return false;
    }
    private void tryLoadShippingQuote() {
        if (!cartLoaded || !addressLoaded) {
            return;
        }

        if (selectedItems.isEmpty() || selectedAddress == null) {
            return;
        }

        if (selectedAddress.getDistrictId() == null
                || selectedAddress.getWardCode() == null
                || selectedAddress.getWardCode().trim().isEmpty()) {
            shippingAvailable = false;
            tvEstimatedDelivery.setText("Địa chỉ chưa có mã GHN");
            tvShippingFee.setText("Không thể tính phí");
            updateLoadingState();
            return;
        }

        loadShippingQuote();
    }

    private void loadShippingQuote() {
        Long userId = sessionManager.getUserId();

        if (userId == null) {
            return;
        }

        shippingLoading = true;
        shippingAvailable = false;

        tvShippingFee.setText("Đang tính...");
        tvSummaryShippingFee.setText("Phí vận chuyển: Đang tính...");
        tvEstimatedDelivery.setText("Đang tính thời gian giao...");
        updateLoadingState();

        ShippingQuoteRequest request = new ShippingQuoteRequest(
                userId,
                getSelectedCartItemIds(),
                selectedAddress.getDistrictId(),
                selectedAddress.getWardCode()
        );

        ApiClient.getApiService().getShippingQuote(request)
                .enqueue(new Callback<ShippingQuote>() {
                    @Override
                    public void onResponse(Call<ShippingQuote> call,
                                           Response<ShippingQuote> response) {
                        shippingLoading = false;

                        if (!response.isSuccessful()
                                || response.body() == null
                                || response.body().getShippingFee() == null) {
                            showShippingError();
                            return;
                        }

                        ShippingQuote quote = response.body();

                        shippingFee = quote.getShippingFee();
                        shippingAvailable = true;

                        if (quote.getServiceName() != null
                                && !quote.getServiceName().trim().isEmpty()) {
                            tvShippingMethod.setText(
                                    "Giao hàng " + quote.getServiceName()
                            );
                        }

                        tvEstimatedDelivery.setText(
                                "Dự kiến giao: " + quote.getEstimatedDelivery()
                        );

                        calculateSummary();
                        updateLoadingState();
                    }

                    @Override
                    public void onFailure(Call<ShippingQuote> call, Throwable t) {
                        shippingLoading = false;
                        showShippingError();
                    }
                });
    }

    private void showShippingError() {
        shippingFee = BigDecimal.ZERO;
        shippingAvailable = false;

        tvShippingFee.setText("Không thể tính phí");
        tvSummaryShippingFee.setText("Phí vận chuyển: Chưa xác định");
        tvEstimatedDelivery.setText(
                "Không lấy được thời gian giao dự kiến"
        );

        updateLoadingState();

        Toast.makeText(
                CheckoutActivity.this,
                "Không thể tính phí vận chuyển GHN",
                Toast.LENGTH_SHORT
        ).show();
    }

    private List<Long> getSelectedCartItemIds() {
        List<Long> cartItemIds = new ArrayList<>();

        for (long id : selectedCartItemIds) {
            cartItemIds.add(id);
        }

        return cartItemIds;
    }

    private void calculateSummary() {
        productTotal = BigDecimal.ZERO;

        for (CartItem item : selectedItems) {
            BigDecimal subtotal = item.getSubtotal();

            if (subtotal == null && item.getPrice() != null && item.getQuantity() != null) {
                subtotal = item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            }

            if (subtotal != null) {
                productTotal = productTotal.add(subtotal);
            }
        }

        totalAmount = productTotal.add(shippingFee).subtract(discountAmount);

        tvProductTotal.setText("Tổng tiền hàng: " + formatPrice(productTotal));
        tvShippingFee.setText(formatPrice(shippingFee));
        tvSummaryShippingFee.setText("Phí vận chuyển: " + formatPrice(shippingFee));
        tvDiscount.setText("Giảm giá: " + formatPrice(discountAmount));
        tvCheckoutTotal.setText("Tổng thanh toán: " + formatPrice(totalAmount));
        tvBottomTotal.setText(formatPrice(totalAmount));
    }

    private void updateLoadingState() {
        boolean loading = !cartLoaded || !addressLoaded || shippingLoading;

        progressCheckout.setVisibility(loading ? View.VISIBLE : View.GONE);

        btnPlaceOrder.setEnabled(!loading
                        && !selectedItems.isEmpty()
                        && selectedAddress != null
                        && shippingAvailable
        );
    }

    private void placeOrder() {
        Long userId = sessionManager.getUserId();

        if (userId == null) {
            Toast.makeText(this, "Phiên đăng nhập không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedAddress == null) {
            Toast.makeText(this, "Vui lòng chọn địa chỉ nhận hàng", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!shippingAvailable) {
            Toast.makeText(this, "Chưa tính được phí vận chuyển", Toast.LENGTH_SHORT).show();
            return;
        }

        List<Long> cartItemIds = getSelectedCartItemIds();
        String paymentMethod = rbVnPay.isChecked() ? "VNPAY" : "COD";
        CreateOrderRequest request = new CreateOrderRequest(
                userId,
                cartItemIds,
                selectedAddress.getReceiverName(),
                selectedAddress.getReceiverPhone(),
                selectedAddress.getFullAddress(),
                selectedAddress.getLatitude(),
                selectedAddress.getLongitude(),
                selectedAddress.getDistrictId(),
                selectedAddress.getWardCode(),
                shippingFee,
                paymentMethod,
                edtCheckoutNote.getText().toString().trim()
        );

        btnPlaceOrder.setEnabled(false);
        btnPlaceOrder.setText("Đang đặt hàng...");

        ApiClient.getApiService().createOrder(request).enqueue(new Callback<OrderResponse>() {
            @Override
            public void onResponse(Call<OrderResponse> call, Response<OrderResponse> response) {
                btnPlaceOrder.setText("Đặt hàng");

                if (response.isSuccessful() && response.body() != null) {
                    OrderResponse order = response.body();

                    if ("VNPAY".equals(paymentMethod)) {
                        createVnPayPayment(order.getId());
                    } else {
                        openOrderSuccess(order);
                    }

                    return;
                }

                btnPlaceOrder.setEnabled(true);
                Toast.makeText(CheckoutActivity.this, "Đặt hàng thất bại. Vui lòng kiểm tra lại giỏ hàng", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<OrderResponse> call, Throwable t) {
                btnPlaceOrder.setEnabled(true);
                btnPlaceOrder.setText("Đặt hàng");

                Toast.makeText(CheckoutActivity.this, "Lỗi đặt hàng: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void openOrderSuccess(OrderResponse order) {
        Intent intent = new Intent(CheckoutActivity.this, OrderSuccessActivity.class);
        intent.putExtra("order_id", order.getId());
        intent.putExtra("total_amount", order.getTotalAmount() == null ? "0" : order.getTotalAmount().toPlainString());

        startActivity(intent);
        finish();
    }

    private String formatPrice(BigDecimal value) {
        if (value == null) {
            return "0đ";
        }

        return formatter.format(value) + "đ";
    }
    private void createVnPayPayment(Long orderId) {
        btnPlaceOrder.setEnabled(false);
        btnPlaceOrder.setText("Đang mở VNPAY...");

        ApiClient.getApiService().createVnPayPayment(orderId).enqueue(new Callback<VnPayPaymentResponse>() {
            @Override
            public void onResponse(Call<VnPayPaymentResponse> call, Response<VnPayPaymentResponse> response) {
                if (!response.isSuccessful() || response.body() == null || response.body().getPaymentUrl() == null) {
                    btnPlaceOrder.setEnabled(true);
                    btnPlaceOrder.setText("Đặt hàng");
                    Toast.makeText(CheckoutActivity.this, "Không tạo được đường dẫn VNPAY", Toast.LENGTH_LONG).show();
                    return;
                }

                openVnPayUrl(response.body().getPaymentUrl());
            }

            @Override
            public void onFailure(Call<VnPayPaymentResponse> call, Throwable throwable) {
                btnPlaceOrder.setEnabled(true);
                btnPlaceOrder.setText("Đặt hàng");
                Toast.makeText(CheckoutActivity.this, "Lỗi tạo thanh toán: " + throwable.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
    private void openVnPayUrl(String paymentUrl) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(paymentUrl));
            startActivity(intent);
            finish();
        } catch (ActivityNotFoundException exception) {
            btnPlaceOrder.setEnabled(true);
            btnPlaceOrder.setText("Đặt hàng");
            Toast.makeText(this, "Không tìm thấy trình duyệt", Toast.LENGTH_LONG).show();
        }
    }
}