package com.sko.manifestmanagement.Retrofit

import com.sko.manifestmanagement.model.AddGuestDto
import com.sko.manifestmanagement.model.ApiResponse
import com.sko.manifestmanagement.model.Crew
import com.sko.manifestmanagement.model.CrewMember
import com.sko.manifestmanagement.model.Crewoperator
import com.sko.manifestmanagement.model.Guest
import com.sko.manifestmanagement.model.GuestDetailDto
import com.sko.manifestmanagement.model.GuestProfileDTO
import com.sko.manifestmanagement.model.LoginRequest
import com.sko.manifestmanagement.model.LoginResponse
import com.sko.manifestmanagement.model.UpdateCrewoperatorDTO
import com.sko.manifestmanagement.model.UpdateProfileImageDTO
import com.sko.manifestmanagement.model.UpdateRequest
import com.sko.manifestmanagement.model.VerifyPassword
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiServices {

    @POST("Crews")
    fun registerCrew(@Body crew: Crew): Call<ResponseBody>

    @POST("Crews/login")
    fun loginCrew(@Body loginRequest: LoginRequest): Call<LoginResponse>

    @POST("Guests") // Replace with the correct endpoint
    fun addGuest(@Body guest: AddGuestDto): Call<ResponseBody>

    @POST("otp/verify")
    @Headers("Content-Type: application/json") // If server expects JSON
    fun verifyOtp(@Body otpRequest: Map<String, String>): Call<ResponseBody>

    @POST("otp/send")
    //@Headers("Content-Type: text/plain") // If server expects plain text
    fun sendOtp(@Body email: String): Call<ResponseBody>

    @GET("Crews/{id}")
    fun getCrewByIdWithToken(
        @Path("id") crewId: Int,
        @Header("Authorization") token: String
    ): Call<CrewMember>

    @GET("Guests/{id}")
    fun getGuestById(@Path("id") id: String): Call<Guest>

    @GET("Guests/{id}")
    fun getGuestByIDtoProfile(@Path("id") id: String): Call<GuestProfileDTO>

    @GET("guests")
    fun getGuest():Call<List<GuestDetailDto>>

    @GET("Crews")
    fun getItem():Call<List<Crew>>

    @PUT("guests/{id}")
    fun updateGuest(
        @Path("id") id: Int,
        @Body updatedGuest: Guest
    ): Call<Void>
    @DELETE("items/delete")
    fun deleteItems(@Body ids:List<Int>):Call<Void>

    @DELETE("Guests/SoftDelete/{id}")
    fun deleteGuest(@Path("id") guestId: Int): Call<Void>

    @PUT("Crews/update-password")
    fun updatePassword(@Body updateRequest: UpdateRequest): Call<ResponseBody>

    @GET("guests/search-by-embark-date/{embarkDate}")
    fun searchByEmbarkDate(
        @Path("embarkDate") embarkDate: String // Pass the formatted date string
    ): Call<List<GuestDetailDto>>

    // API call to search guests by debark (dBark) date
    @GET("guests/search-by-debark-date/{debarkDate}")
    fun searchByDebarkDate(
        @Path("debarkDate") debarkDate: String
    ): Call<List<GuestDetailDto>>

    @PUT("Crews/{id}")
    fun updateCrewDetails(
        @Header("Authorization") token: String,  // Bearer token for authorization
        @Path("id") id: Int,  // Path parameter for the crew operator ID
        @Body updatedCrew: UpdateCrewoperatorDTO  // The updated crew data in the body
    ): Call<CrewMember>  // Response type (CrewMember is your model for the response)

    @PUT("Crews/update-email/{id}")
    fun updateEmail(
        @Path("id") crewId: Int,
        @Body request: String
    ): Call<ApiResponse<Crewoperator>>

    @POST("Crews/verify-old-password")
    fun verifyOldPassword(@Body verifyPassword: VerifyPassword): Call<ResponseBody>

    @PUT("Crews/update-crew-image/{id}")
    fun updateProfileImage(
        @Header("Authorization") token: String,
        @Path("id") crewId: Int,
        @Body request: UpdateProfileImageDTO
    ): Call<Void>

    @POST("Guests/checkin")
    fun checkInGuest(@Query("barcode") barcode: Int): Call<ResponseBody>
    @POST("Guests/checkout") // Check-out Guest endpoint
    fun checkOutGuest(@Query("barcode") barcode: Int): Call<ResponseBody>

}
