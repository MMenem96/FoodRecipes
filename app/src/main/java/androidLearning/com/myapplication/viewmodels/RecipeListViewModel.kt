package mitchcourses.com.myapplication.viewmodels

import android.app.Application
import android.util.Log
import androidLearning.com.myapplication.models.Recipe
import androidLearning.com.myapplication.repositories.RecipeRepository
import androidLearning.com.myapplication.util.Resource
import androidLearning.com.myapplication.util.Resource.Status
import androidx.lifecycle.*

class RecipeListViewModel(application: Application) : AndroidViewModel(application) {

    private val TAG = "RecipeListViewModel"
    lateinit var viewStates: MutableLiveData<ViewState>
    private val recipes: MediatorLiveData<Resource<MutableList<Recipe>>> = MediatorLiveData()

    var isQueryExhausted = false
    var isPreformingQuery = false
    var query = ""
    var pageNumber = 1
    val QUERY_EXHAUSTED_MESSAGE = "No more results..."
    private var cancelRequest = false

    fun getViewState(): LiveData<ViewState> {
        if (!::viewStates.isInitialized) {
            viewStates = MutableLiveData()
            viewStates.value = ViewState.CATEGORIES
            RecipeRepository.context = getApplication()
        }

        return viewStates
    }

    fun getRecipes(): LiveData<Resource<MutableList<Recipe>>> = recipes

    fun searchRecipes(query: String, pageNumber: Int) {
        this.query = query
        this.pageNumber = pageNumber
        if (!isPreformingQuery) {
            isQueryExhausted = false
            executeSearchQuery()
        }
    }

    fun searchNextPage() {
        Log.d(TAG, "nextPage called")
        if (!isQueryExhausted && !isPreformingQuery) {
            pageNumber++
            executeSearchQuery()
        }
    }

    private fun executeSearchQuery() {
        cancelRequest = false
        isPreformingQuery = true
        viewStates.value = ViewState.RECIPES
        val repositorySource = RecipeRepository.searchRecipesApis(query, pageNumber)
        recipes.addSource(repositorySource, Observer {
            //React to the data before sending it to the UI
            if (!cancelRequest) {
                if (it != null) {
                    recipes.postValue(it)
                    if (it.status == Status.SUCCESS) {
                        isPreformingQuery = false
                        if (it.data != null) {
                            if (it.data.isEmpty()) {
                                Log.d(TAG, "onChange: Query Exhausted")
                                recipes.value = Resource.Error(
                                    QUERY_EXHAUSTED_MESSAGE,
                                    Status.ERROR,
                                    it.data
                                )
                            }
                        }
                        recipes.removeSource(repositorySource)
                    } else if (it.status == Status.ERROR) {
                        isPreformingQuery = false
                        recipes.removeSource(repositorySource)
                    }

                } else {
                    recipes.removeSource(repositorySource)
                }
            } else {
                recipes.removeSource(repositorySource)
            }
        })
    }

    fun cancelRequest() {
        if (isPreformingQuery) {
            Log.d(TAG, "Cancelling the search request")
            //    pageNumber=1
            cancelRequest = true
            isPreformingQuery = false
        }

    }

    enum class ViewState {
        CATEGORIES, RECIPES
    }
}