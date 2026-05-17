package com.servicerca.app.ai

import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface PerspectiveApi {
    @POST("v1alpha1/comments:analyze")
    suspend fun analyzeComment(
        @Query("key") apiKey: String,
        @Body request: PerspectiveRequest,
    ): PerspectiveResponse
}
