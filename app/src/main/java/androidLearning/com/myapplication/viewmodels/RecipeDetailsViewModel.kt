package mitchcourses.com.myapplication.viewmodels

import android.app.Application
import androidLearning.com.myapplication.models.Recipe
import androidLearning.com.myapplication.repositories.RecipeRepository
import androidLearning.com.myapplication.util.Resource
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData

class RecipeDetailsViewModel(application: Application) : AndroidViewModel(application) {


    fun getRecipeById(recipeId: String): LiveData<Resource<Recipe>> {
        return RecipeRepository.searchRecipeApi(recipeId)
    }

}