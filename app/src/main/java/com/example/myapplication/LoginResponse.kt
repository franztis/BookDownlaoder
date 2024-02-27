package com.example.myapplication

data class LoginResponse( val expires_in: Int,
                          val token_type: String,
                          val refresh_token: String,
                          val access_token: String)
