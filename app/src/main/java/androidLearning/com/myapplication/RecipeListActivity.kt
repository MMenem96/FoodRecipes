package mitchcourses.com.myapplication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidLearning.com.myapplication.R
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_recipe_list.*
import mitchcourses.com.myapplication.adapters.OnRecipeListener
import mitchcourses.com.myapplication.adapters.RecipeRecyclerAdapter
import mitchcourses.com.myapplication.models.Recipe
import mitchcourses.com.myapplication.util.VerticalSpacingItemDecorator
import mitchcourses.com.myapplication.viewmodels.RecipeListViewModel

class RecipeListActivity : BaseActivity(), OnRecipeListener {

    private lateinit var searchView: SearchView
    private lateinit var recipeRecyclerViewList: RecyclerView
    private var recipeRecyclerViewAdapter: RecipeRecyclerAdapter? = null

    private val mRecipeListViewModel: RecipeListViewModel by lazy {
        ViewModelProvider(this@RecipeListActivity).get(RecipeListViewModel::class.java)
    }

    private val TAG = "RecipeListActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_list)
        findingAllViews()
        initRecyclerView()
        initSearchView()
        subscribeObservers()
        if (!mRecipeListViewModel.isViewingRecipes) {
            //Display search categories
            displaySearchCategories()
        }

    }

    private fun initSearchView() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                searchView.clearFocus()
                recipeRecyclerViewAdapter?.displayLoading()
                mRecipeListViewModel.searchRecipesApi(query, 1)
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }

        })

    }


    private fun findingAllViews() {
        setSupportActionBar(toolbar)
        searchView = findViewById(R.id.search_view)
        recipeRecyclerViewList = findViewById(R.id.recipe_list_recycler_view)
    }

    private fun initRecyclerView() {
        recipeRecyclerViewAdapter = RecipeRecyclerAdapter(onRecipeListener = this)
        recipeRecyclerViewList.adapter = recipeRecyclerViewAdapter
        recipeRecyclerViewList.layoutManager = LinearLayoutManager(this)
        recipeRecyclerViewList.addItemDecoration(VerticalSpacingItemDecorator(5))
        recipeRecyclerViewList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState);
                if (!recipeRecyclerViewList.canScrollVertically(1)) {
                    //Search the next page
                    mRecipeListViewModel.nextSearchRecipesApi()
                }
            }
        })

    }

    private fun subscribeObservers() {
        mRecipeListViewModel.getRecipes().observe(this,
            Observer<MutableList<Recipe>> { recipes ->
                if (recipes != null) {
                    mRecipeListViewModel.isPreformingQuery = false
                    recipeRecyclerViewAdapter?.setRecipesList(recipes)
                }
            })
        mRecipeListViewModel.isQueryExhausted().observe(this, Observer { queryExhausted ->
            if (queryExhausted) {
                Log.d(TAG, "QueryIsExhausted")
                recipeRecyclerViewAdapter?.displayExhausting()
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
        recipeRecyclerViewAdapter?.displayLoading()
        mRecipeListViewModel.searchRecipesApi(category, 1)
    }

    fun displaySearchCategories() {
        mRecipeListViewModel.isViewingRecipes = false
        recipeRecyclerViewAdapter?.displaySearchCategories()
    }


    override fun onBackPressed() {
        if (mRecipeListViewModel.onBackPressed())
            super.onBackPressed()
        else displaySearchCategories()

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.recipe_search_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_categories -> {
                displaySearchCategories()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
