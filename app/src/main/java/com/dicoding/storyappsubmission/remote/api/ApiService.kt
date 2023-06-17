package com.dicoding.storyappsubmission.remote.api

import com.dicoding.storyappsubmission.remote.response.login.LoginResponse
import com.dicoding.storyappsubmission.remote.response.register.RegisterResponse
import com.dicoding.storyappsubmission.remote.response.story.addstory.AddStoryResponse
import com.dicoding.storyappsubmission.remote.response.story.getstory.StoryResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @FormUrlEncoded
    @POST("login")
    fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<LoginResponse>

    @FormUrlEncoded
    @POST("register")
    fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<RegisterResponse>

    @Multipart
    @POST("stories")
    fun addStories(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("lat") lat: Float,
        @Part("lon") lon: Float
    ): Call<AddStoryResponse>

    @GET("stories?location=1")
    suspend fun getStories(
        @Header("Authorization") token: String
    ): StoryResponse

    @GET("stories")
    suspend fun getStory(
        @Header("Authorization") token: String,
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("location") location: Int
    ): StoryResponse
}