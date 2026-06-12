package com.erichschnell.testingapp.data.remote

import com.erichschnell.testingapp.data.remote.reponse.ProductsResponse
import com.erichschnell.testingapp.data.remote.reponse.PromotionsResponse
import retrofit2.http.GET

interface MiniMarketApiService {
    @GET("data/products.json")
    suspend fun getProducts(): ProductsResponse

    @GET("data/promotions.json")
    suspend fun getPromotions(): PromotionsResponse
}
