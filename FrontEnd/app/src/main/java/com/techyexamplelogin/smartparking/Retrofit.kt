package com.techyexamplelogin.smartparking

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.Date

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
    val otp: String?,
    val _id:String?,
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
data class parkingResponse(
    val success: Boolean,
    val message: String,
    val data: List<AddParking>?,  // Change Objects to Any? (nullable)
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
data class Addvehicle(
    val companyName:String,
    val model:String,
    val vehicleNumber:String,
    val type:String,


)
data class AddParking(
    val street:String,
    val city:String,
    val pincode:String,
    val coordinates:Coordinates,
    val dimensions:Dimensions,
    val startDate:String,
    val endDate:String,
    val startTime:String,
    val endTime:String,

)
data class Coordinates(
    val longitude:String?,
    val latitude:String?
)
data class Dimensions(
    val length_mm:Double?,
    val width_mm:Double?
)
data class getParking(
    val longitude:String?,
    val latitude:String?,
    val startDate:String?,
    val startTime:String?
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
    @GET("cars/get")
    fun fetchVeh(@Query("user") userId: String?):Call<NavigationActivity.VehileResponse> // Optional userId
    @POST("cars/add")
    fun Addvehicle(@Header("Authorization") authToken: String?,@Body vehicle:Addvehicle):Call<Any?>
    @POST("parking/add")
    fun Addparking(@Header("Authorization") authToken: String?,@Body parking:AddParking):Call<Any?>
    @POST("parking/get")
    fun getParking(@Body coordinates: getParking):Call<parkingResponse?>
}


object RetrofitInstance {
    private const val BASE_URL = "https://smart-park-navy.vercel.app/api/v1/"

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

