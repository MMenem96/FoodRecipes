package androidLearning.com.myapplication.presistence

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidLearning.com.myapplication.models.Recipe

@Database(entities = [Recipe::class], version = 1)
@TypeConverters(IngredientsConverters::class)
abstract class RecipeDatabase() : RoomDatabase() {
    companion object {
        val DATABASE_NAME = "recipes_db"
        lateinit var instance: RecipeDatabase

        fun getRecipeDatabase(context: Context): RecipeDatabase {
                synchronized(RecipeDatabase::class) {
                    instance = Room.databaseBuilder(
                        context.getApplicationContext(),
                        RecipeDatabase::class.java, DATABASE_NAME
                    )
                        .build()


            }
            return instance

        }
    }

    abstract fun getRecipeDao(): RecipeDao

}