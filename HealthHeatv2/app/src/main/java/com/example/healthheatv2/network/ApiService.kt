package com.example.healthheatv2.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import com.google.gson.annotations.SerializedName

data class FoodResponse(
    @SerializedName("barcode") val barcode: String?,
    @SerializedName("name") val name: String?,
    @SerializedName("brand") val brand: String?,
    @SerializedName("image_url") val imageUrl: String?,
    @SerializedName("quantity") val quantity: String?,

    @SerializedName("ingredients_text") val ingredientsText: String?,
    // If ingredients is a list of strings, use List<String>.
    // If it's a list of objects, you'll need to create an Ingredient data class.
    @SerializedName("ingredients") val ingredients: List<String>?,

    // Maps dynamic keys to their values (handles the "additionalProp1": {} structure)
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

    @SerializedName("nutrient_levels") val nutrientLevels: Map<String, Any>?,

    @SerializedName("packaging") val packaging: String?,
    @SerializedName("verdict") val verdict: String?,
    @SerializedName("roast_or_toast") val roastOrToast: String?,
    @SerializedName("reasoning") val reasoning: String?,

    // Adjust type if alternatives are objects instead of strings
    @SerializedName("alternatives") val alternatives: List<String>?,
    @SerializedName("verdict_color") val verdictColor: String?
)

// 2. Define your API endpoints
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
    private const val BASE_URL = "https://debra-cuneatic-unprintably.ngrok-free.dev/"
    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}