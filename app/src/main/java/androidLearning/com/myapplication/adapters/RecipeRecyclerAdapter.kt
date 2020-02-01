package mitchcourses.com.myapplication.adapters

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidLearning.com.myapplication.R
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import mitchcourses.com.myapplication.models.Recipe
import mitchcourses.com.myapplication.util.Constants

class RecipeRecyclerAdapter(val onRecipeListener: OnRecipeListener) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

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
                    onRecipeListener
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
                    onRecipeListener
                )
            }
        }

    }

    override fun getItemCount(): Int = recipeList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == RECIPE_TYPE) {
            Glide.with(holder.itemView.context)
                .load(recipeList.get(position).imageUrl)
                .placeholder(R.drawable.ic_launcher_background)
                .into((holder as RecipeViewHolder).image)
            holder.title.text = recipeList.get(position).title
            holder.publisher.text = recipeList.get(position).publisher
            holder.socialScore.text =
                Math.round(recipeList.get(position).socialRank ?: 0.0).toString()
        } else if (getItemViewType(position) == CATEGORY_TYPE) {
            val imagePath: Uri = Uri.parse(
                "android.resource://mitchcourses.com.myapplication/drawable/${recipeList.get(
                    position
                ).title.toLowerCase()}"
            )
            Glide.with(holder.itemView.context)
                .load(imagePath)
                .apply(RequestOptions.circleCropTransform())
                .placeholder(R.drawable.ic_launcher_background)
                .into((holder as CategoryViewHolder).categoryImage)
            holder.categoryTitle.text = recipeList.get(position).title
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (recipeList.get(position).socialRank.toInt() == -1)
            return CATEGORY_TYPE
        else if (recipeList.get(position).title.equals("LOADING..."))
            return LOADING_TYPE
        else if (position == recipeList.size - 1 && position != 0 && !recipeList.get(position).title.equals(
                "EXHAUSTED..."
            )
        )
            return LOADING_TYPE
        else if (recipeList.get(position).title.equals("EXHAUSTED..."))
            return EXHUASTED_TYPE
        else
            return RECIPE_TYPE
    }

    fun displaySearchCategories() {
        val categoriesRecipeList: MutableList<Recipe> = ArrayList()
        for (i in Constants.DEFAULT_SEARCH_CATEGORY_IMAGES.indices) {
            val categoryRecipe: Recipe = Recipe(
                "", Constants.DEFAULT_SEARCH_CATEGORY_IMAGES[i], listOf(""),
                "", "", "", -1.0, "", Constants.DEFAULT_SEARCH_CATEGORY_IMAGES[i]
            )
            categoriesRecipeList.add(categoryRecipe)
        }
        recipeList = categoriesRecipeList
        notifyDataSetChanged()
    }

    fun displayLoading() {
        if (!isLoading()) {
            val recipe =
                Recipe("", "", listOf(""), "", "", "", 0.0, "", title = "LOADING...")
            val loadingRecipeList: MutableList<Recipe> = ArrayList()
            loadingRecipeList.add(recipe)
            recipeList = loadingRecipeList
            notifyDataSetChanged()
        }
    }

    fun displayExhausting() {
        hideLoading()
        val exhaustedRecipe = Recipe(
            "", "", listOf(""), "",
            "", "", 0.0, "", "EXHAUSTED..."
        )
        recipeList.add(exhaustedRecipe)
        notifyDataSetChanged()
    }


    private fun hideLoading() {
        if (isLoading()) {
            for (recipe in recipeList) {
                if (recipe.title.equals("LOADING..."))
                    recipeList.remove(recipe)
            }
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


}