package mitchcourses.com.myapplication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidLearning.com.myapplication.R
import androidLearning.com.myapplication.util.Resource
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.integration.recyclerview.RecyclerViewPreloader
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.util.ViewPreloadSizeProvider
import kotlinx.android.synthetic.main.activity_recipe_list.*
import mitchcourses.com.myapplication.adapters.OnRecipeListener
import mitchcourses.com.myapplication.adapters.RecipeRecyclerAdapter
import mitchcourses.com.myapplication.util.VerticalSpacingItemDecorator
import mitchcourses.com.myapplication.viewmodels.RecipeListViewModel

class RecipeListActivity : BaseActivity(), OnRecipeListener {

    private lateinit var searchView: SearchView
    private lateinit var recipeRecyclerViewList: RecyclerView
    private var recipeRecyclerViewAdapter: RecipeRecyclerAdapter? = null
    private val mRecipeListViewModel: RecipeListViewModel by lazy {
        ViewModelProvider(
            this@RecipeListActivity,
            ViewModelProvider.AndroidViewModelFactory(this.application)
        ).get(RecipeListViewModel::class.java)
    }

    private val TAG = "RecipeListActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_list)
        subscribeObservers()
        findingAllViews()
        initRecyclerView()
        initSearchView()
    }

    private fun initSearchView() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                recipeRecyclerViewList.smoothScrollToPosition(0)
                searchView.clearFocus()
                recipeRecyclerViewAdapter?.displayLoading()
                mRecipeListViewModel.searchRecipes(query, 1)
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }

        })

    }

   private fun initGlide(): RequestManager {
        val requestOptions = RequestOptions().placeholder(R.drawable.white_background)
            .error(R.drawable.ic_launcher_background)
        return Glide.with(this).setDefaultRequestOptions(requestOptions)
    }

    private fun findingAllViews() {
        setSupportActionBar(toolbar)
        searchView = findViewById(R.id.search_view)
        recipeRecyclerViewList = findViewById(R.id.recipe_list_recycler_view)
    }

    private fun initRecyclerView() {
        val glideViewPreLoader: ViewPreloadSizeProvider<String> = ViewPreloadSizeProvider()
        recipeRecyclerViewAdapter =
            RecipeRecyclerAdapter(this, initGlide(), glideViewPreLoader)
        recipeRecyclerViewList.adapter = recipeRecyclerViewAdapter
        recipeRecyclerViewList.layoutManager = LinearLayoutManager(this)
        val recyclerViewPreLoader = RecyclerViewPreloader<String>(
            Glide.with(this),
            recipeRecyclerViewAdapter as RecipeRecyclerAdapter,
            glideViewPreLoader,
            30
        )
        recipeRecyclerViewList.addOnScrollListener(recyclerViewPreLoader)
        recipeRecyclerViewList.addItemDecoration(VerticalSpacingItemDecorator(5))
        recipeRecyclerViewList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!recipeRecyclerViewList.canScrollVertically(1) && mRecipeListViewModel.getViewState().value!!.equals(
                        RecipeListViewModel.ViewState.RECIPES
                    )
                ) {
                    //Search the next page
                    mRecipeListViewModel.searchNextPage()
                }
            }
        })

    }

    private fun subscribeObservers() {
        mRecipeListViewModel.getRecipes().observe(this, Observer {
            if (it.data != null) {
                when (it.status) {
                    Resource.Status.LOADING -> {
                        if (mRecipeListViewModel.pageNumber > 1) {
                            recipeRecyclerViewAdapter?.displayLoading()
                        } else {
                            recipeRecyclerViewAdapter?.displayOnlyLoading()
                        }
                    }
                    Resource.Status.ERROR -> {
                        Log.d(
                            TAG,
                            "onChanged: can't refresh the cache ${it.message} and the data size is ${it.data.size}"
                        )
                        recipeRecyclerViewAdapter?.hideLoading()
                        recipeRecyclerViewAdapter?.setRecipesList(it.data)
                        Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                        if (it.message.equals(mRecipeListViewModel.QUERY_EXHAUSTED_MESSAGE)) {
                            recipeRecyclerViewAdapter?.displayExhausting()
                        }
                    }
                    Resource.Status.SUCCESS -> {
                        Log.d(
                            TAG,
                            "onChanged:the cache is refreshed  ${it.message} and the data size is ${it.data.size}"
                        )
                        recipeRecyclerViewAdapter?.hideLoading()
                        recipeRecyclerViewAdapter?.setRecipesList(it.data)

                    }
                }
            }
        })


        mRecipeListViewModel.getViewState().observe(this,
            Observer { viewState ->
                if (viewState != null) {
                    when (viewState) {
                        RecipeListViewModel.ViewState.RECIPES -> {
                            //Recipes shows automatically from other observer
                        }
                        RecipeListViewModel.ViewState.CATEGORIES -> {
                            recipeRecyclerViewAdapter?.displaySearchCategories()
                        }
                    }
                }
            })

    }


    override fun onRecipeClick(position: Int) {
        val recipeIntent = Intent(this@RecipeListActivity, RecipeDetailsActivity::class.java)
        recipeIntent.putExtra("recipe", recipeRecyclerViewAdapter?.getRecipe(position))
        startActivity(recipeIntent)
    }

    override fun onCategoryClick(category: String) {
        searchView.clearFocus()
        mRecipeListViewModel.searchRecipes(category, 1)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.recipe_search_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_categories -> {
                recipeRecyclerViewAdapter?.displaySearchCategories()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (mRecipeListViewModel.getViewState().value!! == RecipeListViewModel.ViewState.RECIPES) {
            mRecipeListViewModel.cancelRequest()
            mRecipeListViewModel.viewStates.value = RecipeListViewModel.ViewState.CATEGORIES
        } else {
            super.onBackPressed()
        }
    }
}
