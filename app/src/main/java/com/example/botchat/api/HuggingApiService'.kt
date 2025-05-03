package com.example.botchat.api


import com.example.botchat.data.HuggingFaceRequest
import com.example.botchat.data.HuggingFaceResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Url

interface HuggingFaceApiServic{
    @POST
    suspend fun query(
        @Header("Authorization") authorization: String,
        @Body payload: HuggingFaceRequest,
        @Url url: String // Add url as parameter
    ): Response<List<HuggingFaceResponse>>
}
//huggingfaceapi