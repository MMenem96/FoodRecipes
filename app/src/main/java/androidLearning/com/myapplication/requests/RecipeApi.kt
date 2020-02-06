package mitchcourses.com.myapplication.requests

import androidLearning.com.myapplication.requests.responses.ApiResponse
import androidx.lifecycle.LiveData
import mitchcourses.com.myapplication.requests.responses.RecipeResponse
import mitchcourses.com.myapplication.requests.responses.RecipeSearchResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface RecipeApi {

    //Search for a recipe
    @GET("api/search")
    fun searchRecipe(
        @Query("q") query: String
        , @Query("page") pageNumber: Int
    ): LiveData<ApiResponse<RecipeSearchResponse>>

    //Search for a recipe
    @GET("api/get")
    fun getRecipe(
        @Query("rId") recipe_id: String
    ): LiveData<ApiResponse<RecipeResponse>>
}