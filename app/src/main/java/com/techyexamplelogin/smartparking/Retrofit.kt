package com.techyexamplelogin.smartparking

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
data class UserLoggedIn(
    val success: Boolean,
    val message: String,
    val data:UserData? ,  // Change Objects to Any? (nullable)
    val error: Any?  // Change Objects to Any? (nullable)
)
data class UserData(
    val name: String?,
    val email: String?,
    val phone: String?,
    val password: String?,
    val otp: String?
)
data class LoginData(
    val email: String,
    val password: String
)
data class Data(
    val token: String?,
    val user:UserData
    // Change Objects to Any? (nullable)
)
data class UserResponseAuth(
    val success: Boolean,
    val message: String,
    val data:Data? ,  // Change Objects to Any? (nullable)
    val error: Any?  // Change Objects to Any? (nullable)
)
data class UserResponse(
    val success: Boolean,
    val message: String,
    val data: Data?,  // Change Objects to Any? (nullable)
    val error: Any?  // Change Objects to Any? (nullable)
)
data class OTPresponse(
    val success: Boolean,
    val message: String,
    val data: String ,  // Change Objects to Any? (nullable)
    val error: Any?  // Change Objects to Any? (nullable)
)
data class Email(
    val email: String
)

interface ApiService {
    @POST("auth/signup/send-otp") // Replace with the actual signup API endpoint
    fun sendOtp(@Body email:Email ): Call<OTPresponse>
    @POST("auth/check")
    fun checkLoggedIn( @Header("Authorization") authToken: String): Call<UserLoggedIn>
    @POST("auth/signup") // Replace with the actual signup API endpoint
    fun signUpUserData(@Body userData: UserData): Call<UserResponseAuth>
    @POST("auth/signin")
    fun loginUser(@Body loginData:LoginData) :Call<UserResponse>
}


object RetrofitInstance {
    private const val BASE_URL = "http://192.168.232.109:3001/api/v1/"

    private val interceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(interceptor)
        .build()

    val instance: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }
}

