package androidLearning.com.myapplication.util


// A generic class that contains data and status about loading this data.
sealed class Resource<T>(
    val data: T? = null,
    val status: Status = Status.LOADING,
    val message: String? = null
) {
    class Success<T>(data: T, message: String) :
        Resource<T>(data, Status.SUCCESS, message)

    class Loading<T>(data: T? = null) : Resource<T>(data, Status.LOADING, "Loading...")
    class Error<T>(message: String, status: Status, data: T? = null) :
        Resource<T>(data, Status.ERROR, message)


    enum class Status {
        SUCCESS, ERROR, LOADING
    }
}