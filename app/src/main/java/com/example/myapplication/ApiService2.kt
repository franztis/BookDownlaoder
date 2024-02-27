package com.example.myapplication
import retrofit2.http.GET
import retrofit2.http.Body
import retrofit2.Call
interface ApiService2 {



    @GET("/Access/Books")
    fun getBooks(): Call<List<Book>>
}