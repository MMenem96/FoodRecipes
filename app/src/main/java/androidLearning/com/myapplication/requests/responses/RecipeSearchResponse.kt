package mitchcourses.com.myapplication.requests.responses


import com.google.gson.annotations.SerializedName
import androidLearning.com.myapplication.models.Recipe

data class RecipeSearchResponse(
    @SerializedName("count")
    val count: Int,
    @SerializedName("recipes")
    val recipes: List<Recipe>
)