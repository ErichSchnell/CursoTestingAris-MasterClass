package com.erichschnell.testingapp.productList.data.remote

import com.erichschnell.testingapp.productList.data.remote.reponse.ProductsResponse
import com.erichschnell.testingapp.productList.data.remote.reponse.PromotionsResponse
import com.erichschnell.testingapp.productList.domain.models.Product
import retrofit2.http.GET

interface MiniMarketApiService {

    @GET("data/products.json")
    suspend fun getProducts(): ProductsResponse

    @GET("data/promotion.json")
    suspend fun getPromotions(): PromotionsResponse
}