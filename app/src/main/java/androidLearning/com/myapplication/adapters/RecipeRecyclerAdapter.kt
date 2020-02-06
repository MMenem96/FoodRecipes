package mitchcourses.com.myapplication.adapters

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import androidLearning.com.myapplication.R
import androidLearning.com.myapplication.models.Recipe
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.ListPreloader
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.RequestManager
import com.bumptech.glide.util.ViewPreloadSizeProvider
import mitchcourses.com.myapplication.util.Constants
import java.util.*
import kotlin.collections.ArrayList

class RecipeRecyclerAdapter(
    val onRecipeListener: OnRecipeListener,
    val requestManager: RequestManager,
    val glideViewPreLoader: ViewPreloadSizeProvider<String>
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(), ListPreloader.PreloadModelProvider<String> {

    companion object {
        val RECIPE_TYPE = 1
        val LOADING_TYPE = 2
        val CATEGORY_TYPE = 3
        val EXHUASTED_TYPE = 4
    }

    private var recipeList: MutableList<Recipe> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            RECIPE_TYPE -> {
                return RecipeViewHolder(

                    LayoutInflater.from(parent.context).inflate(
                        R.layout.layout_recipe_list_item,
                        parent,
                        false
                    ),
                    onRecipeListener, requestManager, glideViewPreLoader
                )
            }
            LOADING_TYPE -> {
                return GenericViewHolder(

                    LayoutInflater.from(parent.context).inflate(
                        R.layout.layout_loading_list_item,
                        parent,
                        false
                    )

                )
            }
            EXHUASTED_TYPE -> {
                return GenericViewHolder(

                    LayoutInflater.from(parent.context).inflate(
                        R.layout.layout_search_exhausted,
                        parent,
                        false
                    )

                )
            }
            else -> {
                return CategoryViewHolder(

                    LayoutInflater.from(parent.context).inflate(
                        R.layout.layout_category_list_item,
                        parent,
                        false
                    ),
                    onRecipeListener, requestManager
                )
            }
        }

    }

    override fun getItemCount(): Int = recipeList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == RECIPE_TYPE) {
            (holder as RecipeViewHolder).onBind(recipeList[position])
        } else if (getItemViewType(position) == CATEGORY_TYPE) {
            (holder as CategoryViewHolder).onBind(recipeList[position])
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (recipeList.get(position).socialRank.toInt() == -1)
            return CATEGORY_TYPE
        else if (recipeList[position].title == "LOADING...")
            return LOADING_TYPE
        else if (recipeList[position].title == "EXHAUSTED...")
            return EXHUASTED_TYPE
        else
            return RECIPE_TYPE
    }

    //Display Loading along search process
    fun displayOnlyLoading() {
        recipeList.clear()
        val recipe =
            Recipe(
                "",
                "",
                listOf(""),
                "",
                "",
                "",
                0.0,
                "",
                title = "LOADING...",
                timeStamp =0
            )
        recipeList.add(recipe)
        notifyDataSetChanged()
    }


    fun displaySearchCategories() {
        val categoriesRecipeList: MutableList<Recipe> = ArrayList()
        for (i in Constants.DEFAULT_SEARCH_CATEGORY_IMAGES.indices) {
            val categoryRecipe: Recipe = Recipe(
                "", Constants.DEFAULT_SEARCH_CATEGORY_IMAGES[i], listOf(""),
                "", "", "", -1.0, "", Constants.DEFAULT_SEARCH_CATEGORY_IMAGES[i],
                0
            )
            categoriesRecipeList.add(categoryRecipe)
        }
        recipeList = categoriesRecipeList
        notifyDataSetChanged()
    }

    //Pagination Loading
    fun displayLoading() {
        if (!isLoading()) {
            val recipe =
                Recipe(
                    "",
                    "",
                    listOf(""),
                    "",
                    "",
                    "",
                    0.0,
                    "",
                    title = "LOADING...",
                    timeStamp = 0
                )
            recipeList.add(recipe)
            notifyDataSetChanged()
        }
    }

    fun displayExhausting() {
        hideLoading()
        val exhaustedRecipe = Recipe(
            "", "", listOf(""), "",
            "", "", 0.0, "", "EXHAUSTED...",
            0
        )
        recipeList.add(exhaustedRecipe)
        notifyDataSetChanged()
    }


    fun hideLoading() {
        if (isLoading()) {
            if (recipeList[0].title == "LOADING...")
                recipeList.removeAt(0)

            if (recipeList.size > 1 && recipeList[recipeList.size - 1].title == "LOADING...")
                recipeList.removeAt(recipeList.size - 1)

            notifyDataSetChanged()
        }
    }


    private fun isLoading(): Boolean {
        if (recipeList.size > 0) {
            if (recipeList.get(recipeList.size - 1).title.equals("LOADING..."))
                return true
        }
        return false
    }

    fun setRecipesList(recipeList: MutableList<Recipe>) {
        this.recipeList = recipeList
        notifyDataSetChanged()
    }

    fun getRecipe(position: Int): Recipe? {
        if (recipeList.size > 0) {
            return recipeList.get(position)
        }
        return null
    }

    override fun getPreloadItems(position: Int): MutableList<String> {
        val url = recipeList[position].imageUrl
        if (TextUtils.isEmpty(url)) {
            return Collections.emptyList()
        }
        return Collections.singletonList(url)
    }

    override fun getPreloadRequestBuilder(item: String): RequestBuilder<*>? {
        return requestManager.load(item)
    }


}