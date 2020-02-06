package androidLearning.com.myapplication.util

import androidLearning.com.myapplication.requests.responses.ApiResponse
import androidx.lifecycle.LiveData
import retrofit2.CallAdapter
import retrofit2.Retrofit
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class LiveDataCallAdapterFactory : CallAdapter.Factory() {
    /**
     * This method preforms a number of checks and then returns the response type for retrofit requests.
     * @bodyType is the response type, it can be RecipeResponse or RecipeSearchResponse
     * CHECK #1 returnType returns LiveData
     * CHECK #2 LiveData<T> is of ApiResponse.class
     * CHECK #3 Make sure that ApiResponse is parameterized. AKA: ApiResponse exists
     */

    override fun get(
        returnType: Type,
        annotations: Array<Annotation>,
        retrofit: Retrofit
    ): CallAdapter<*, *>? {
        //CHECK #1 Make sure the return type is live data
        if (getRawType(returnType) != LiveData::class.java) {
            return null
        }
        //CHECK #2 Type the live data is wrapping
        val observableType = getParameterUpperBound(0, returnType as ParameterizedType)

        //check if it' of type ApiResponse
        val rawObservableType = getRawType(observableType)
        if (rawObservableType != ApiResponse::class.java) {
            throw IllegalArgumentException("Type Must be a defined resource")
        }
        //CHECK #3 check if ApiResponse is parameterized. AKA: Does ApiResponse<T> exists? must wrap around T
        if (observableType !is ParameterizedType) {
            throw IllegalArgumentException("Resource must be parameterized")
        }
        val bodyType = getParameterUpperBound(0, observableType as ParameterizedType)

        return LiveDataCallAdapter<Type>(bodyType)
    }

}