package mitchcourses.com.myapplication.requests

import android.util.Log
import androidx.lifecycle.MutableLiveData
import mitchcourses.com.myapplication.AppExecutors
import mitchcourses.com.myapplication.models.Recipe
import mitchcourses.com.myapplication.requests.responses.RecipeResponse
import mitchcourses.com.myapplication.requests.responses.RecipeSearchResponse
import mitchcourses.com.myapplication.util.Constants
import retrofit2.Call
import java.io.IOException
import java.util.concurrent.TimeUnit

object RecipeApiClient {

    private val TAG = "RecipeApiClient"
    private var retrieveRecipesRunnable: RetrieveRecipesRunnable? = null
    private var retrieveRecipeRunnable: RetrieveRecipeRunnable? = null
    val recipeRequestTimeOut: MutableLiveData<Boolean> = MutableLiveData()

    val mRecipes: MutableLiveData<MutableList<Recipe>> by lazy {

        return@lazy MutableLiveData<MutableList<Recipe>>()
    }

    val mRecipe: MutableLiveData<Recipe> by lazy {

        return@lazy MutableLiveData<Recipe>()
    }


    //Get all recipes
    fun searchRecipesApi(query: String, pageNumber: Int) {
        if (retrieveRecipesRunnable != null) {
            retrieveRecipesRunnable = null
        }
        retrieveRecipesRunnable = RetrieveRecipesRunnable(query, pageNumber)
        val futureHandler = AppExecutors.mNetworkIO.submit(retrieveRecipesRunnable)

        AppExecutors.mNetworkIO.schedule(object : Runnable {
            override fun run() {
                //let the user know it's timed out
                futureHandler.cancel(true)
            }

        }, Constants.NETWORK_TIMEOUT, TimeUnit.MILLISECONDS)

    }

    //Get one recipe
    fun getRecipeApi(recipeId: String) {
        if (retrieveRecipesRunnable != null) {
            retrieveRecipesRunnable = null
        }
        retrieveRecipeRunnable = RetrieveRecipeRunnable(recipeId)

        val futureHandler = AppExecutors.mNetworkIO.submit(retrieveRecipeRunnable)

        recipeRequestTimeOut.value = false

        AppExecutors.mNetworkIO.schedule(object : Runnable {
            override fun run() {
                //let the user know it's timed out
                recipeRequestTimeOut.postValue(true)
                futureHandler.cancel(true)
            }

        }, Constants.NETWORK_TIMEOUT, TimeUnit.MILLISECONDS)

    }

    //Runnable to receive all Recipes
    private class RetrieveRecipesRunnable(
        var query: String,
        var pageNumber: Int,
        var cancelRequest: Boolean = false
    ) : Runnable {
        override fun run() {
            try {
                val response = getRecipes(query, pageNumber).execute()

                if (cancelRequest)
                    return

                if (response.code() == 200) {
                    val recipesList: MutableList<Recipe> = ArrayList(response.body()!!.recipes)

                    if (pageNumber == 1) {
                        mRecipes.postValue(recipesList)
                    } else {
                        val currentRecipes: MutableList<Recipe> =
                            mRecipes.value as MutableList<Recipe>
                        currentRecipes.addAll(recipesList)
                        mRecipes.postValue(currentRecipes)
                    }
                } else {
                    Log.e(TAG, "run: ${response.errorBody().toString()}")
                    mRecipes.postValue(null)
                }
            } catch (e: IOException) {
                e.printStackTrace()
                mRecipes.postValue(null)
            }
        }

        private fun getRecipes(query: String, pageNumber: Int): Call<RecipeSearchResponse> {
            return ServiceGenerator.getRecipeApi.searchRecipe(query, pageNumber)
        }

        fun cancelRequest() {
            Log.d(TAG, "cancelRequest: canceling the search request")
            cancelRequest = true

        }
    }

    //Runnable to receive only one Recipe
    private class RetrieveRecipeRunnable(
        var recipeId: String,
        var cancelRequest: Boolean = false
    ) : Runnable {
        override fun run() {
            try {
                val response = getRecipe(recipeId).execute()

                if (cancelRequest)
                    return

                if (response.code() == 200) {
                    val recipe: Recipe? = response.body()?.recipe
                    mRecipe.postValue(recipe)

                } else {
                    Log.e(TAG, "run: ${response.errorBody().toString()}")
                    mRecipe.postValue(null)
                }
            } catch (e: IOException) {
                e.printStackTrace()
                mRecipe.postValue(null)
            }
        }

        private fun getRecipe(recipeId: String): Call<RecipeResponse> {
            return ServiceGenerator.getRecipeApi.getRecipe(recipeId)
        }

        fun cancelRequest() {
            Log.d(TAG, "cancelRequest: canceling the search request")
            cancelRequest = true

        }
    }

    fun cancelRequest() {
        if (retrieveRecipesRunnable != null)
            (retrieveRecipesRunnable as RetrieveRecipesRunnable).cancelRequest()
        if (retrieveRecipeRunnable != null)
            (retrieveRecipeRunnable as RetrieveRecipeRunnable).cancelRequest()

    }
}