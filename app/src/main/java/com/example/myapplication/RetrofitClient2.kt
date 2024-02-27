package com.example.myapplication
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.OkHttpClient
import okhttp3.Request

object RetrofitClient2 {
    @Volatile
    private var bearerToken: String? = null

    fun setBearerToken(newToken: String) {
        bearerToken = newToken
    }

    private val okHttpClient: OkHttpClient
        get() = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val originalRequest = chain.request()
                val newRequestBuilder = originalRequest.newBuilder()

                bearerToken?.let {
                    newRequestBuilder.header("Authorization", "Bearer $it")
                }

                val newRequest = newRequestBuilder
                    .method(originalRequest.method, originalRequest.body)
                    .build()
                chain.proceed(newRequest)
            }
            .build()

    private val retrofit: Retrofit
        get() = Retrofit.Builder()
            .baseUrl("https://3nt-demo-backend.azurewebsites.net")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    val apiService: ApiService2 by lazy { retrofit.create(ApiService2::class.java) }

}
