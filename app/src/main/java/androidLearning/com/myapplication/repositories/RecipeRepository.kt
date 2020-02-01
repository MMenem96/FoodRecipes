package mitchcourses.com.myapplication.repositories

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import mitchcourses.com.myapplication.models.Recipe
import mitchcourses.com.myapplication.requests.RecipeApiClient


object RecipeRepository {
    private val TAG = "RecipeRepository"


    val getRecipe: LiveData<Recipe> by lazy {

        return@lazy RecipeApiClient.mRecipe
    }


    lateinit var query: String
    var pageNumber: Int = 0

    val mIsQueryExhausted: MutableLiveData<Boolean> =
        MutableLiveData()
    private val mRecipes = MediatorLiveData<MutableList<Recipe>>()

    private val recipesList by lazy {
        return@lazy RecipeApiClient.mRecipes
    }

    fun getRecipes(): LiveData<MutableList<Recipe>> {
        mRecipes.addSource(recipesList, Observer { t ->
            if (t != null) {
                Log.d(TAG, "Recipes ${recipesList.value?.size}")
                Log.d(TAG, "T is not null")
                mRecipes.value = t
                doneQuery(t)
            } else {
                //Search database cache
                doneQuery(null)
            }
        })
        return mRecipes
    }

    private fun doneQuery(list: List<Recipe>?) {
        if (list != null) {
            if (list.size % 30 != 0) {
                Log.d(TAG, "LIST SIZE IS ${list.size}")
                mIsQueryExhausted.value = true
            }
        } else {
            mIsQueryExhausted.value = true
        }
    }


    fun searchRecipesApi(query: String, pageNumber: Int) {
        this.query = query
        this.pageNumber = pageNumber
        mIsQueryExhausted.value = false
        RecipeApiClient.searchRecipesApi(query, pageNumber)
    }

    fun getRecipeApi(recipeId: String) {
        RecipeApiClient.getRecipeApi(recipeId)
    }

    fun isRecipeRequestTimeOut(): LiveData<Boolean> = RecipeApiClient.recipeRequestTimeOut

    fun getNextRecipesPage() {
        searchRecipesApi(query, pageNumber + 1)
    }

    fun cancelRequest() {
        RecipeApiClient.cancelRequest()
    }
}