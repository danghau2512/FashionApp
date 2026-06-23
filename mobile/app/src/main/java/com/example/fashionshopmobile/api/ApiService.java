package com.example.fashionshopmobile.api;

import com.example.fashionshopmobile.model.AdminDashboard;
import com.example.fashionshopmobile.model.CartItem;
import com.example.fashionshopmobile.model.Category;
import com.example.fashionshopmobile.model.OrderResponse;
import com.example.fashionshopmobile.model.OrderSummary;
import com.example.fashionshopmobile.model.Product;
import com.example.fashionshopmobile.model.ProductVariant;
import com.example.fashionshopmobile.model.StoreLocation;
import com.example.fashionshopmobile.model.User;
import com.example.fashionshopmobile.model.UserAddress;
import com.example.fashionshopmobile.request.AddCartRequest;
import com.example.fashionshopmobile.request.AddressRequest;
import com.example.fashionshopmobile.request.CreateOrderRequest;
import com.example.fashionshopmobile.request.UpdateCartItemRequest;
import com.example.fashionshopmobile.request.UpdateUserRequest;
import com.example.fashionshopmobile.request.UserSyncRequest;
import com.example.fashionshopmobile.model.shipping.GhnDistrict;
import com.example.fashionshopmobile.model.shipping.GhnProvince;
import com.example.fashionshopmobile.model.shipping.GhnWard;
import java.util.List;

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
    @GET("api/admin/dashboard")
    Call<AdminDashboard> getAdminDashboard();
}
