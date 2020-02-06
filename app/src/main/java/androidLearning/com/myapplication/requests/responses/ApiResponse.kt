package androidLearning.com.myapplication.requests.responses

import retrofit2.Response
import java.io.IOException

open class ApiResponse<T>() {

    fun create(error: Throwable): ApiResponse<T> {
        return ApiErrorResponse(error.message ?: "Unknown Error, check internet connection")
    }

    fun create(response: Response<T>): ApiResponse<T> {
        if (response.isSuccessful) {
            val body: T? = response.body()
            if (body == null || response.code() == 204) {
                return ApiEmptyResponse()
            } else {

                return ApiSuccessResponse(body)
            }
        } else {
            var errorMessage = ""
            try {
                errorMessage = response.errorBody().toString()
            } catch (ex: IOException) {
                ex.printStackTrace()
                errorMessage = response.message()
            }
            return ApiErrorResponse(errorMessage)
        }
    }

    class ApiSuccessResponse<T>(val body: T) : ApiResponse<T>() {}
    class ApiErrorResponse<T>(val errorMessage: String) : ApiResponse<T>() {}
    class ApiEmptyResponse<T>() : ApiResponse<T>() {}

}