package com.dicoding.storyappsubmission.remote.api

import com.dicoding.storyappsubmission.remote.response.login.LoginResponse
import com.dicoding.storyappsubmission.remote.response.register.RegisterResponse
import com.dicoding.storyappsubmission.remote.response.story.addstory.AddStoryResponse
import com.dicoding.storyappsubmission.remote.response.story.getstory.StoryResponse
import okhttp3.MultipartBody
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

    @GET("stories?location=1")
    fun getStories(
        @Header("Authorization") token: String
    ): Call<StoryResponse>

    @Multipart
    @POST("stories")
    fun addStories(
        @Part("Content") Content: MultipartBody.Part,
        @Header("Authorization") token: String,
        @Part("description") description: String,
        @Part("lat") lat: Float,
        @Part("lon") lon: Float,
    ): Call<AddStoryResponse>
}