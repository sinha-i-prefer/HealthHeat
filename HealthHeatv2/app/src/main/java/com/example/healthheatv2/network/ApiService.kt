package com.example.healthheatv2.network

import  retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import com.google.gson.annotations.SerializedName

data class IngredientAnalysis(
    @SerializedName("name") val name: String?,
    @SerializedName("quantity") val quantity: String?,
    @SerializedName("status") val status: String?, // e.g., "Good", "Bad", "Neutral"
    @SerializedName("reason") val reason: String?
)

// 2. The main response class
data class FoodResponse(
    @SerializedName("verdict") val verdict: String?,
    @SerializedName("health_score") val healthScore: Int?, // Added
    @SerializedName("summary") val summary: String?, // Added

    // Links to the new data class above
    @SerializedName("ingredients_analysis") val ingredientsAnalysis: List<IngredientAnalysis>?,

    @SerializedName("barcode") val barcode: String?,
    @SerializedName("name") val name: String?,
    @SerializedName("brand") val brand: String?,
    @SerializedName("image_url") val imageUrl: String?,
    @SerializedName("quantity") val quantity: String?,

    @SerializedName("ingredients_text") val ingredientsText: String?,
    @SerializedName("ingredients") val ingredients: List<String>?,

    @SerializedName("nutrients") val nutrients: Map<String, Any>?,

    @SerializedName("nutri_score") val nutriScore: String?,
    @SerializedName("nova_group") val novaGroup: Int?,
    @SerializedName("nova_tags") val novaTags: List<String>?,
    @SerializedName("categories") val categories: String?,
    @SerializedName("countries") val countries: String?,
    @SerializedName("allergens") val allergens: String?,
    @SerializedName("additives_tags") val additivesTags: List<String>?,
    @SerializedName("serving_size") val servingSize: String?,
    @SerializedName("ecoscore_grade") val ecoscoreGrade: String?,

    @SerializedName("nutrient_levels") val nutrientLevels: Map<String, String>?, // Changed to String, as the JSON values are "high", "low", etc.

    @SerializedName("packaging") val packaging: String?,
    @SerializedName("alternatives") val alternatives: List<String>?,
    @SerializedName("verdict_color") val verdictColor: String?
)
interface ApiService {
    // 1. Changed to @POST
    // 2. The {barcode} in the path matches the @Path variable below
    @POST("/api/scan/{barcode}")
    suspend fun getFoodData(
        @Path("barcode") barcode: String,
        @Query("user_profile") userProfile: String = "General Health" // Optional, matches your default
    ): FoodResponse
}

// 3. Create the Retrofit Singleton
object RetrofitClient {
    private const val BASE_URL = "https://nutri-scanner-api.onrender.com/"
    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}