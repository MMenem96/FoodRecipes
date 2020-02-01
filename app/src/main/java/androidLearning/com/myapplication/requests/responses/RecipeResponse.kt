package mitchcourses.com.myapplication.requests.responses


import com.google.gson.annotations.SerializedName
import mitchcourses.com.myapplication.models.Recipe

data class RecipeResponse(
    @SerializedName("recipe")
    val recipe: Recipe
)