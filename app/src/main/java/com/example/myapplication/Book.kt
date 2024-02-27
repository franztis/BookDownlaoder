package com.example.myapplication
import com.google.gson.annotations.SerializedName

data class Book(
    val id: Int,
    val title: String,
    val img_url: String,
    val date_released: String,
    val pdf_url: String
)
