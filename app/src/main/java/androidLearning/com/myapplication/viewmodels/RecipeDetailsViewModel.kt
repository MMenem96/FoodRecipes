package mitchcourses.com.myapplication.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import mitchcourses.com.myapplication.models.Recipe
import mitchcourses.com.myapplication.repositories.RecipeRepository

class RecipeDetailsViewModel() : ViewModel() {
    var recipeId: String = ""
    var didRetrieveRecipe: Boolean = false

    val getRecipe: LiveData<Recipe> by lazy {
        return@lazy RecipeRepository.getRecipe
    }

    fun isRecipeRequestTimeOut(): LiveData<Boolean> = RecipeRepository.isRecipeRequestTimeOut()

    fun getRecipeById(recipeId: String) {
        this.recipeId = recipeId
        RecipeRepository.getRecipeApi(recipeId)
    }

}