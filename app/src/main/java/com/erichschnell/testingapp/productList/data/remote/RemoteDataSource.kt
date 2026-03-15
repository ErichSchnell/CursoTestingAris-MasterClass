package com.erichschnell.testingapp.productList.data.remote

import com.erichschnell.testingapp.core.domain.model.AppError
import com.erichschnell.testingapp.productList.data.remote.reponse.ProductResponse
import com.erichschnell.testingapp.productList.data.remote.reponse.ProductsResponse
import com.erichschnell.testingapp.productList.data.remote.reponse.PromotionResponse
import com.erichschnell.testingapp.productList.data.remote.reponse.PromotionsResponse
import com.erichschnell.testingapp.productList.domain.models.Product
import retrofit2.HttpException
import java.io.IOError
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject

class RemoteDataSource @Inject constructor(private val miniMarketApiService: MiniMarketApiService) {

    suspend fun getProducts(): Result<List<ProductResponse>>{
        return try {
            val response = miniMarketApiService.getProducts()
            Result.success(response.products)
        }
        catch (e: Exception){
            Result.failure(mapToDomainError(e))
        }
    }

    suspend fun getPromotions(): Result< List<PromotionResponse>> {
        return try {
            val response = miniMarketApiService.getPromotions()
            Result.success(response.promotions)
        } catch (e: Exception) {
            Result.failure(mapToDomainError(e))
        }
    }


    private fun mapToDomainError(e: Exception): AppError{
        return when(e){
            is UnknownHostException -> AppError.NetworkError
            is SocketTimeoutException -> AppError.NetworkError
            is IOException -> AppError.NetworkError
            is HttpException -> {
                when(e.code()) {
                    404 -> AppError.NotFoundError
                    else -> AppError.NetworkError
                }
            }
            else -> AppError.UnknownError(e.message)
        }
    }

}