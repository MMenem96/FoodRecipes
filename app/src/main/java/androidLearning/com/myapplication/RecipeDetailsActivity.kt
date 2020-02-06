package mitchcourses.com.myapplication

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidLearning.com.myapplication.R
import androidLearning.com.myapplication.models.Recipe
import androidLearning.com.myapplication.util.Resource
import androidx.appcompat.widget.AppCompatImageView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import mitchcourses.com.myapplication.viewmodels.RecipeDetailsViewModel
import org.jetbrains.anko.toast

class RecipeDetailsActivity : BaseActivity() {
    private var recipe: Recipe? = null
    private val TAG = "RecipeDetailsActivity"


    lateinit var recipeImage: AppCompatImageView
    lateinit var recipeTitle: TextView
    lateinit var recipeRank: TextView
    lateinit var recipeIngredientsContainer: LinearLayout
    lateinit var scrollView: ScrollView

    private val recipeDetailsViewModel: RecipeDetailsViewModel by lazy {
        ViewModelProvider(
            this@RecipeDetailsActivity,
            ViewModelProvider.AndroidViewModelFactory(this.application)
        ).get(RecipeDetailsViewModel::class.java)

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe)
        initViews()
        getIncomingIntent()
        subScribeObservers()
    }

    private fun subScribeObservers() {
        recipeDetailsViewModel.getRecipeById(recipe?.recipeId ?: "0").observe(this, Observer {
            if (it != null) {
                if (it.data != null) {
                    when (it.status) {
                        Resource.Status.LOADING -> {
                            Log.d(TAG, "onChanged: status :Loading ")
                            showParent(View.GONE)
                            showProgressBar(View.VISIBLE)

                        }
                        Resource.Status.ERROR -> {
                            showParent(View.VISIBLE)
                            showProgressBar(View.GONE)
                            toast(it.message.toString())
                            Log.d(TAG, "onChanged: status :ErrorMessage ${it.message}")
                            setRecipeProperties(it.data)
                        }
                        Resource.Status.SUCCESS -> {
                            Log.d(TAG, "onChanged: status :Success ${it.status}")
                            showParent(View.VISIBLE)
                            showProgressBar(View.GONE)
                            setRecipeProperties(it.data)

                        }
                    }
                }
            }
        })


    }

    private fun setRecipeProperties(recipe: Recipe?) {
        if (recipe != null) {
            Log.d(TAG, "Recipe is not Empty")
            Glide.with(this@RecipeDetailsActivity)
                .load(recipe.imageUrl)
                .into(recipeImage)
            recipeTitle.text = recipe.title
            recipeRank.text = recipe.socialRank.toInt().toString()
            recipeIngredientsContainer.removeAllViews()
            if (recipe.ingredients.size > 1) {
                Log.d(TAG, "Ingredients is not empty ${recipe.ingredients.size}")
                for (ingredient in recipe.ingredients) {
                    val textView = TextView(this)
                    textView.text = ingredient
                    textView.textSize = 15f
                    textView.layoutParams = LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                    recipeIngredientsContainer.addView(textView)
                }
            } else {
                Log.d(TAG, "Ingredients is empty")
                val textView = TextView(this)
                textView.text = "Error retrieving Ingredients...\nCheck internet connection"
                textView.textSize = 15f
                textView.layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                recipeIngredientsContainer.addView(textView)
            }

        }
    }

    private fun initViews() {
        recipeImage = findViewById(R.id.recipe_image)
        recipeTitle = findViewById(R.id.recipe_title)
        recipeRank = findViewById(R.id.recipe_social_score)
        recipeIngredientsContainer = findViewById(R.id.ingredients_container)
        scrollView = findViewById(R.id.parent)

    }

    private fun getIncomingIntent() {
        if (intent.hasExtra("recipe")) {
            recipe = intent.getSerializableExtra("recipe") as Recipe
        }

    }


    fun showParent(visibility: Int) {
        scrollView.visibility = visibility

    }
}
