package mitchcourses.com.myapplication.models


import com.google.gson.annotations.SerializedName
import java.io.Serializable

//Implementing serializable to send objects from this class between intents
data class Recipe(
    @SerializedName("_id")
    val id: String,
    @SerializedName("image_url")
    val imageUrl: String,
    @SerializedName("ingredients")
    val ingredients: List<String>,
    @SerializedName("publisher")
    val publisher: String,
    @SerializedName("publisher_url")
    val publisherUrl: String,
    @SerializedName("recipe_id")
    val recipeId: String,
    @SerializedName("social_rank")
    val socialRank: Double,
    @SerializedName("source_url")
    val sourceUrl: String,
    @SerializedName("title")
    val title: String
) : Serializable