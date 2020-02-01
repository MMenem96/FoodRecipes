package mitchcourses.com.myapplication.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import mitchcourses.com.myapplication.models.Recipe
import mitchcourses.com.myapplication.repositories.RecipeRepository

class RecipeListViewModel() : ViewModel() {
    var isViewingRecipes: Boolean = false
    var isPreformingQuery: Boolean = false

    private val TAG = "RecipeListViewModel"


    fun getRecipes(): LiveData<MutableList<Recipe>> = RecipeRepository.getRecipes()


    fun searchRecipesApi(query: String, pageNumber: Int) {
        isPreformingQuery = true
        isViewingRecipes = true
        RecipeRepository.searchRecipesApi(query, pageNumber)
    }

    fun nextSearchRecipesApi() {

        if (!isPreformingQuery && isViewingRecipes && isQueryExhausted().value!! == false) {
            RecipeRepository.getNextRecipesPage()
            Log.d(TAG, "searchNextPage: called.")
        }
    }

    fun isQueryExhausted(): LiveData<Boolean> {
        return RecipeRepository.mIsQueryExhausted
    }

    fun onBackPressed(): Boolean {
        if (isPreformingQuery) {
            //Cancel the query
            RecipeRepository.cancelRequest()
            isPreformingQuery = false
        }
        if (isViewingRecipes) {
            isViewingRecipes = false
            return false
        }
        return true

    }
}