package com.example.fashionshopmobile.api;

import com.example.fashionshopmobile.model.Product;
import com.example.fashionshopmobile.model.ProductVariant;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ApiService {

    @GET("api/products")
    Call<List<Product>> getProducts();
    @GET("api/products/{id}")
    Call<Product> getProductById(@Path("id") Long id);

    @GET("api/products/{productId}/variants")
    Call<List<ProductVariant>> getProductVariants(@Path("productId") Long productId);
}