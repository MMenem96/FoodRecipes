package androidLearning.com.myapplication.repositories

import android.content.Context
import android.util.Log
import androidLearning.com.myapplication.models.Recipe
import androidLearning.com.myapplication.presistence.RecipeDao
import androidLearning.com.myapplication.presistence.RecipeDatabase
import androidLearning.com.myapplication.requests.responses.ApiResponse
import androidLearning.com.myapplication.util.NetworkBoundResource
import androidLearning.com.myapplication.util.Resource
import androidx.lifecycle.LiveData
import mitchcourses.com.myapplication.requests.ServiceGenerator
import mitchcourses.com.myapplication.requests.responses.RecipeResponse
import mitchcourses.com.myapplication.requests.responses.RecipeSearchResponse
import mitchcourses.com.myapplication.util.Constants

object RecipeRepository {
    private val TAG = "RecipeRepository"
    lateinit var context: Context
    val recipeDao: RecipeDao by lazy {
        return@lazy RecipeDatabase.getRecipeDatabase(context).getRecipeDao()
    }

    fun searchRecipesApis(query: String, pageNumber: Int): LiveData<Resource<MutableList<Recipe>>> {

        return object : NetworkBoundResource<MutableList<Recipe>, RecipeSearchResponse>() {
            override fun saveCallResult(item: RecipeSearchResponse?) {
                Log.d(TAG, "ItemsSize is ${item?.recipes?.size ?: 0}")
                if (item?.recipes != null) {
                    for (i in item.recipes.indices) {
                        item.recipes[i].ingredients = listOf("")
                    }

                    var index: Int = 0
                    for (rowId in recipeDao.insertRecipes((item.recipes))) {
                        if (rowId.toInt() == -1) {
                            //If the recipe already exists...I don't want to set the ingredients or timestamp to not
                            //be erased
                            Log.d(TAG, "SaveCallResult: CONFLICT...this recipe is already in cache")
                            recipeDao.updateRecipe(
                                item.recipes[index].recipeId,
                                item.recipes[index].title,
                                item.recipes[index].publisher,
                                item.recipes[index].imageUrl,
                                item.recipes[index].socialRank.toFloat()
                            )
                        }
                        index++
                    }
                }
            }

            override fun shouldFetch(data: MutableList<Recipe>?): Boolean {
                return true
            }

            override fun loadFromDb(): LiveData<MutableList<Recipe>> {
                return recipeDao.searchRecipes(query, pageNumber)
            }

            override fun createCall(): LiveData<ApiResponse<RecipeSearchResponse>> {
                return ServiceGenerator.getRecipeApi.searchRecipe(query, pageNumber)
            }

        }.asLiveData()
    }

    fun searchRecipeApi(recipeId: String): LiveData<Resource<Recipe>> {
        return object : NetworkBoundResource<Recipe, RecipeResponse>() {
            override fun saveCallResult(item: RecipeResponse?) {
                if (item?.recipe != null) {
                    Log.d(TAG, "saveCallResult: ingredients ${item.recipe.ingredients}")
                    item.recipe.timeStamp = (System.currentTimeMillis() / 1000).toInt()
                    recipeDao.insertRecipe(item.recipe)
                }
            }

            override fun shouldFetch(data: Recipe?): Boolean {
                Log.d(TAG, "shouldFetch: recipe ${data?.toString()}")
                val currentTime = (System.currentTimeMillis() / 1000).toInt()
                Log.d(TAG, "shouldFetch: currentTime $currentTime")
                val lastRefresh = data?.timeStamp ?: 0
                Log.d(TAG, "shouldFetch: lastRefresh $lastRefresh")
                val daysDifference = (currentTime - lastRefresh) / (60 * 60 * 24)
                Log.d(
                    TAG,
                    "shouldFetch: daysDifference $daysDifference day"
                )

                if (daysDifference >= Constants.RECIPE_REFRESH_TIME) {
                    Log.d(TAG, "shouldFetch: shouldRefresh")
                    return true
                }
                return true
            }

            override fun loadFromDb(): LiveData<Recipe> {
                return recipeDao.getRecipe(recipeId)
            }

            override fun createCall(): LiveData<ApiResponse<RecipeResponse>> {
                return ServiceGenerator.getRecipeApi.getRecipe(recipeId)
            }

        }.asLiveData()
    }

}