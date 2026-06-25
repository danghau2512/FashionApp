package com.example.fashionshopmobile.api;

import com.example.fashionshopmobile.model.AdminDashboard;
import com.example.fashionshopmobile.model.AdminOrderDetail;
import com.example.fashionshopmobile.model.AdminOrderSummary;
import com.example.fashionshopmobile.model.AdminProductResponse;
import com.example.fashionshopmobile.model.AdminProductVariantResponse;
import com.example.fashionshopmobile.model.AdminStatistics;
import com.example.fashionshopmobile.model.CartItem;
import com.example.fashionshopmobile.model.Category;
import com.example.fashionshopmobile.model.OrderResponse;
import com.example.fashionshopmobile.model.OrderSummary;
import com.example.fashionshopmobile.model.Product;
import com.example.fashionshopmobile.model.ProductReview;
import com.example.fashionshopmobile.model.ProductVariant;
import com.example.fashionshopmobile.model.ReviewEligibility;
import com.example.fashionshopmobile.model.StoreLocation;
import com.example.fashionshopmobile.model.User;
import com.example.fashionshopmobile.model.UserAddress;
import com.example.fashionshopmobile.model.shipping.GhnDistrict;
import com.example.fashionshopmobile.model.shipping.GhnProvince;
import com.example.fashionshopmobile.model.shipping.GhnWard;
import com.example.fashionshopmobile.model.VnPayPaymentResponse;
import com.example.fashionshopmobile.model.shipping.ShippingQuote;
import com.example.fashionshopmobile.request.AddCartRequest;
import com.example.fashionshopmobile.request.AddressRequest;
import com.example.fashionshopmobile.request.AdminOrderActionRequest;
import com.example.fashionshopmobile.request.AdminProductRequest;
import com.example.fashionshopmobile.request.AdminProductVariantRequest;
import com.example.fashionshopmobile.request.CreateOrderRequest;
import com.example.fashionshopmobile.request.CreateProductReviewRequest;
import com.example.fashionshopmobile.request.ShippingQuoteRequest;
import com.example.fashionshopmobile.request.UpdateCartItemRequest;
import com.example.fashionshopmobile.request.UpdateProductStatusRequest;
import com.example.fashionshopmobile.request.UpdateProductVariantStatusRequest;
import com.example.fashionshopmobile.request.UpdateUserRequest;
import com.example.fashionshopmobile.request.UserSyncRequest;
import com.example.fashionshopmobile.model.AdminUser;
import com.example.fashionshopmobile.request.AdminUserRequest;
import com.example.fashionshopmobile.request.AdminUserStatusRequest;

import java.math.BigDecimal;
import java.util.List;

import com.example.fashionshopmobile.model.ImageUploadResponse;

import okhttp3.MultipartBody;
import retrofit2.http.Multipart;
import retrofit2.http.Part;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    @GET("api/products")
    Call<List<Product>> getProducts();

    @GET("api/products/search")
    Call<List<Product>> searchProducts(
            @Query("keyword") String keyword,
            @Query("minPrice") BigDecimal minPrice,
            @Query("maxPrice") BigDecimal maxPrice,
            @Query("categoryIds") List<Long> categoryIds,
            @Query("genders") List<String> genders,
            @Query("onlySale") Boolean onlySale
    );

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

    @GET("api/users/{id}")
    Call<User> getUserById(@Path("id") Long id);

    @GET("api/orders/user/{userId}")
    Call<List<OrderSummary>> getOrdersByUserId(@Path("userId") Long userId);

    @GET("api/orders/{orderId}")
    Call<OrderResponse> getOrderById(@Path("orderId") Long orderId);

    @PUT("api/orders/{orderId}/cancel")
    Call<OrderResponse> cancelOrder(@Path("orderId") Long orderId);

    @PUT("api/users/{id}")
    Call<User> updateUser(
            @Path("id") Long id,
            @Body UpdateUserRequest request
    );

    @GET("api/addresses/user/{userId}")
    Call<List<UserAddress>> getAddressesByUserId(@Path("userId") Long userId);

    @POST("api/addresses")
    Call<UserAddress> createAddress(@Body AddressRequest request);

    @PUT("api/addresses/{addressId}")
    Call<UserAddress> updateAddress(
            @Path("addressId") Long addressId,
            @Body AddressRequest request
    );

    @DELETE("api/addresses/{addressId}")
    Call<Void> deleteAddress(
            @Path("addressId") Long addressId,
            @Query("userId") Long userId
    );

    @PUT("api/addresses/{addressId}/default")
    Call<UserAddress> setDefaultAddress(
            @Path("addressId") Long addressId,
            @Query("userId") Long userId
    );

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

    @GET("api/shipping/provinces")
    Call<List<GhnProvince>> getGhnProvinces();

    @GET("api/shipping/districts")
    Call<List<GhnDistrict>> getGhnDistricts(@Query("provinceId") Integer provinceId);

    @GET("api/shipping/wards")
    Call<List<GhnWard>> getGhnWards(@Query("districtId") Integer districtId);

    @POST("api/orders")
    Call<OrderResponse> createOrder(@Body CreateOrderRequest request);

    @POST("api/shipping/quote")
    Call<ShippingQuote> getShippingQuote(@Body ShippingQuoteRequest request);

    @GET("api/admin/dashboard")
    Call<AdminDashboard> getAdminDashboard();

    @GET("api/admin/products")
    Call<List<AdminProductResponse>> getAdminProducts(
            @Query("keyword") String keyword,
            @Query("status") String status,
            @Query("categoryId") Long categoryId
    );

    @GET("api/admin/products/{id}")
    Call<AdminProductResponse> getAdminProductById(@Path("id") Long id);

    @POST("api/admin/products")
    Call<AdminProductResponse> createAdminProduct(
            @Query("adminId") Long adminId,
            @Body AdminProductRequest request
    );

    @PUT("api/admin/products/{id}")
    Call<AdminProductResponse> updateAdminProduct(
            @Path("id") Long id,
            @Query("adminId") Long adminId,
            @Body AdminProductRequest request
    );

    @PUT("api/admin/products/{id}/status")
    Call<AdminProductResponse> updateAdminProductStatus(
            @Path("id") Long id,
            @Query("adminId") Long adminId,
            @Body UpdateProductStatusRequest request
    );

    @GET("api/admin/products/{productId}/variants")
    Call<List<AdminProductVariantResponse>> getAdminProductVariants(
            @Path("productId") Long productId
    );

    @GET("api/admin/variants/{id}")
    Call<AdminProductVariantResponse> getAdminProductVariantById(
            @Path("id") Long id
    );

    @POST("api/admin/products/{productId}/variants")
    Call<AdminProductVariantResponse> createAdminProductVariant(
            @Path("productId") Long productId,
            @Query("adminId") Long adminId,
            @Body AdminProductVariantRequest request
    );

    @PUT("api/admin/variants/{id}")
    Call<AdminProductVariantResponse> updateAdminProductVariant(
            @Path("id") Long id,
            @Query("adminId") Long adminId,
            @Body AdminProductVariantRequest request
    );

    @PUT("api/admin/variants/{id}/status")
    Call<AdminProductVariantResponse> updateAdminProductVariantStatus(
            @Path("id") Long id,
            @Query("adminId") Long adminId,
            @Body UpdateProductVariantStatusRequest request
    );

    @GET("api/admin/statistics")
    Call<AdminStatistics> getAdminStatistics(
            @Query("adminId") Long adminId,
            @Query("year") Integer year,
            @Query("bestSellerMonths") Integer bestSellerMonths,
            @Query("noSaleMonths") Integer noSaleMonths
    );

    @GET("api/reviews/product/{productId}")
    Call<List<ProductReview>> getProductReviews(@Path("productId") Long productId);

    @GET("api/reviews/product/{productId}/eligibility")
    Call<ReviewEligibility> getReviewEligibility(
            @Path("productId") Long productId,
            @Query("userId") Long userId
    );

    @POST("api/reviews")
    Call<ProductReview> createProductReview(@Body CreateProductReviewRequest request);

    @GET("api/admin/orders")
    Call<List<AdminOrderSummary>> getAdminOrders(
            @Query("keyword") String keyword,
            @Query("status") String status
    );

    @GET("api/admin/orders/{orderId}")
    Call<AdminOrderDetail> getAdminOrderDetail(
            @Path("orderId") Long orderId
    );

    @PUT("api/admin/orders/{orderId}/ship")
    Call<AdminOrderDetail> shipAdminOrder(
            @Path("orderId") Long orderId,
            @Body AdminOrderActionRequest request
    );

    @PUT("api/admin/orders/{orderId}/confirm")
    Call<AdminOrderDetail> confirmAdminOrder(
            @Path("orderId") Long orderId,
            @Body AdminOrderActionRequest request
    );

    @PUT("api/admin/orders/{orderId}/cancel-by-admin")
    Call<AdminOrderDetail> cancelAdminOrder(
            @Path("orderId") Long orderId,
            @Body AdminOrderActionRequest request
    );

    @PUT("api/admin/orders/{orderId}/complete")
    Call<AdminOrderDetail> completeAdminOrder(
            @Path("orderId") Long orderId,
            @Body AdminOrderActionRequest request
    );

    @POST("api/payments/vnpay/create/{orderId}")
    Call<VnPayPaymentResponse> createVnPayPayment(@Path("orderId") Long orderId);



    @Multipart
    @POST("api/admin/uploads/images")
    Call<ImageUploadResponse> uploadAdminImage(
            @Query("adminId") Long adminId,
            @Part MultipartBody.Part file

    );
    @PUT("api/orders/{orderId}/complete")
    Call<OrderResponse> completeOrder(
            @Path("orderId") Long orderId,
            @Query("userId") Long userId
    );
    @GET("api/admin/users")
    Call<List<AdminUser>> getAdminUsers(
            @Query("keyword") String keyword,
            @Query("role") String role,
            @Query("status") String status
    );

    @GET("api/admin/users/{id}")
    Call<AdminUser> getAdminUserById(
            @Path("id") Long id
    );

    @POST("api/admin/users")
    Call<AdminUser> createAdminUser(
            @Query("adminId") Long adminId,
            @Body AdminUserRequest request
    );

    @PUT("api/admin/users/{id}")
    Call<AdminUser> updateAdminUser(
            @Path("id") Long id,
            @Query("adminId") Long adminId,
            @Body AdminUserRequest request
    );

    @PUT("api/admin/users/{id}/status")
    Call<AdminUser> updateAdminUserStatus(
            @Path("id") Long id,
            @Query("adminId") Long adminId,
            @Body AdminUserStatusRequest request
    );
}
