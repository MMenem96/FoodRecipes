package mitchcourses.com.myapplication.requests

import android.util.Log
import androidLearning.com.myapplication.util.LiveDataCallAdapterFactory
import mitchcourses.com.myapplication.util.Constants
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ServiceGenerator {
    private val okHttpClient: OkHttpClient by lazy {
        val client: OkHttpClient =
            OkHttpClient().newBuilder()
                //Establish connection to the server
                .connectTimeout(Constants.NETWORK_CONNECTION_TIMEOUT, TimeUnit.SECONDS)
                //time between each byte read from the server
                .readTimeout(Constants.NETWORK_READ_TIMEOUT, TimeUnit.SECONDS)
                //time between each byte sent to the server
                .writeTimeout(Constants.NETWORK_WRITE_TIMEOUT, TimeUnit.SECONDS)
                .retryOnConnectionFailure(false)
                .build()
        return@lazy client
    }
    val getRecipeApi: RecipeApi by lazy {
        Log.d("ServiceGenerator", "Creating retrofit client")
        val retrofit = Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .client(okHttpClient)
            .addCallAdapterFactory(LiveDataCallAdapterFactory())
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return@lazy retrofit.create(RecipeApi::class.java)
    }
}