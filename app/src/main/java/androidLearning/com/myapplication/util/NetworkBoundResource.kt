package androidLearning.com.myapplication.util

import android.util.Log
import androidLearning.com.myapplication.requests.responses.ApiResponse
import androidLearning.com.myapplication.requests.responses.ApiResponse.ApiSuccessResponse
import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Observer
import mitchcourses.com.myapplication.AppExecutors


// CacheType: Type for the Resource data(Database cache).
// RequestObject: Type for the API response(Network Request).
abstract class NetworkBoundResource<CacheType, RequestObject> {
    private val TAG = "NetworkBoundResource"


    //Data that will be observed in the UI
    private val results: MediatorLiveData<Resource<CacheType>> = MediatorLiveData()


    init {
        //Update liveData for loading status
        results.value = Resource.Loading(null)

        //Observe liveData source for local db
        val dbSource: LiveData<CacheType> = this.loadFromDb()

        results.addSource(dbSource, Observer {
            // Remove observer from local db. Need to decide if read local db or network
            results.removeSource(dbSource)
            if (shouldFetch(it)) {
                //Get data from the network
                fetchFromNetwork(dbSource)
            } else {
                // Otherwise read data from local db
                results.addSource(dbSource, Observer { cachedObject ->
                    setValue(Resource.Success(cachedObject, "Successful"))
                })
            }
        })
    }

    /**
     * observe local db
     * if <condition> query network
     * stop observing the local db
     * insert new data into local db
     * begin observing the local db again to see the refreshed data from the network
     */
    private fun fetchFromNetwork(dbSource: LiveData<CacheType>) {
        Log.d(TAG, "fetchFromNetwork: Called.")
        //Update LiveData for loading status
        results.addSource(dbSource, Observer {
            //View the cached data and load the new data
            setValue(Resource.Loading(it))
        })

        val apiResponse: LiveData<ApiResponse<RequestObject>> = createCall()
        results.addSource(apiResponse, Observer {
            results.removeSource(dbSource);
            results.removeSource(apiResponse);

            Log.d(TAG, "run: attempting to refresh data from network...");
            /*
            3 Cases:
            1)ApiSuccessResponse
            2)ApiErrorResponse
            3)ApiEmptyResponse
             */

            if (it is ApiSuccessResponse) {
                //Save the new data into local db
                AppExecutors.diskIO.execute(Runnable {
                    Log.d(TAG, "onChanged: ApiSuccessResponse")

                    //Save the new data into the local db in the backgroundThread

                    saveCallResult(processResponse(it) as RequestObject)

                    // observe local db again since new result from network will have been saved
                    AppExecutors.mainThreadExecutor.execute(Runnable {
                        results.addSource(loadFromDb(), Observer { cacheResponse ->
                            // we specially request a new live data,
                            // otherwise we will get immediately last cached value,
                            // which may not be updated with latest results received from network.
                            // as opposed to use the @dbSource variable passed as input
                            setValue(Resource.Success(cacheResponse, "Successful"))
                        })
                    })
                })


            } else if (it is ApiResponse.ApiEmptyResponse) {
                Log.d(TAG, "onChanged: ApiEmptyResponse")
                AppExecutors.mainThreadExecutor.execute(Runnable {
                    results.addSource(loadFromDb(), Observer { cachedResponse ->
                        setValue(Resource.Success(cachedResponse, "Successful"))
                    })
                })

            } else if (it is ApiResponse.ApiErrorResponse) {
                Log.d(TAG, "onChanged: ApiError")

                results.addSource(dbSource, Observer { cachedObject ->
                    setValue(
                        Resource.Error(
                            it.errorMessage,
                            Resource.Status.ERROR,
                            cachedObject
                        )
                    )
                })
            }
        })


    }

    private fun processResponse(response: ApiSuccessResponse<*>): CacheType {

        return response.body as CacheType
    }

    fun setValue(newValue: Resource<CacheType>) {
        if (results.value != newValue) {
            results.value = newValue
        }
    }

    // Called to save the result of the API response into the database
    @WorkerThread
    protected abstract fun saveCallResult(item: RequestObject?)

    // Called with the data in the database to decide whether to fetch
    // potentially updated data from the network.
    @MainThread
    protected abstract fun shouldFetch(data: CacheType?): Boolean

    // Called to get the cached data from the database.
    @MainThread
    protected abstract fun loadFromDb(): LiveData<CacheType>

    // Called to create the API call.
    @MainThread
    protected abstract fun createCall(): LiveData<ApiResponse<RequestObject>>

    // Called when the fetch fails. The child class may want to reset components
    // like rate limiter.
    protected open fun onFetchFailed() {}

    // Returns a LiveData object that represents the resource that's implemented
    // in the base class.
    fun asLiveData(): LiveData<Resource<CacheType>> = results
}
