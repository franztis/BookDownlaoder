package com.example.myapplication
import retrofit2.http.POST
import retrofit2.http.Body
import retrofit2.Call
import com.example.myapplication.LoginRequest
import com.example.myapplication.LoginResponse
interface ApiService {
    @POST("Access/Login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>
}