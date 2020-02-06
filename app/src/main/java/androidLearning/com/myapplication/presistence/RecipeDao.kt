package androidLearning.com.myapplication.presistence

import androidLearning.com.myapplication.models.Recipe
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface RecipeDao {

    //Insert Recipes without replacing existing ones, it will return -1 when it find identical
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertRecipes(recipes: List<Recipe>): Array<Long>

    //Insert Recipe and replace the matching
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRecipe(recipe: Recipe)

    //Update Recipe by recipe id
    @Query("UPDATE recipes SET title=:title,publisher=:publisher,image_url=:imageUrl,social_rank=:socialRank WHERE recipeId=:recipeId ")
    fun updateRecipe(
        recipeId: String,
        title: String,
        publisher: String,
        imageUrl: String,
        socialRank: Float
    )

    //Search for recipes
    @Query("SELECT * FROM recipes WHERE title LIKE '%' || :query || '%' OR ingredients LIKE '%' || :query || '%' ORDER BY social_rank DESC LIMIT(:pageNumber*30)")
    fun searchRecipes(query: String, pageNumber: Int): LiveData<MutableList<Recipe>>


    //Get a recipe by it's id
    @Query("SELECT * FROM recipes WHERE recipeId=:recipeId")
    fun getRecipe(recipeId: String): LiveData<Recipe>

}