package mitchcourses.com.myapplication.requests

import mitchcourses.com.myapplication.requests.responses.RecipeResponse
import mitchcourses.com.myapplication.requests.responses.RecipeSearchResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface RecipeApi {

    //Search for a recipe
    @GET("api/search")
    fun searchRecipe(
        @Query("q") query: String
        , @Query("page") pageNumber: Int
    ): Call<RecipeSearchResponse>

    //Search for a recipe
    @GET("api/get")
    fun getRecipe(
        @Query("rId") recipe_id: String
    ): Call<RecipeResponse>
}