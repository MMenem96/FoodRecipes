package mitchcourses.com.myapplication.requests

import android.util.Log
import mitchcourses.com.myapplication.util.Constants
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ServiceGenerator {
    val getRecipeApi: RecipeApi by lazy {
        Log.d("ServiceGenerator", "Creating retrofit client")
        val retrofit = Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return@lazy retrofit.create(RecipeApi::class.java)
    }
}