package mitchcourses.com.myapplication

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidLearning.com.myapplication.R
import androidx.appcompat.widget.AppCompatImageView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import mitchcourses.com.myapplication.models.Recipe
import mitchcourses.com.myapplication.viewmodels.RecipeDetailsViewModel

class RecipeDetailsActivity : BaseActivity() {
    private val TAG = "RecipeDetailsActivity"


    lateinit var recipeImage: AppCompatImageView
    lateinit var recipeTitle: TextView
    lateinit var recipeRank: TextView
    lateinit var recipeIngredientsContainer: LinearLayout
    lateinit var scrollView: ScrollView

    private val recipeDetailsViewModel: RecipeDetailsViewModel by lazy {
        ViewModelProvider(this@RecipeDetailsActivity).get(RecipeDetailsViewModel::class.java)

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe)
        initViews()
        showProgressBar(View.VISIBLE)
        subScribeObservers()
        getIncomingIntent()
    }

    private fun subScribeObservers() {
        recipeDetailsViewModel.getRecipe.observe(this,
            Observer<Recipe> { t ->
                if (t?.recipeId.equals(recipeDetailsViewModel.recipeId))
                    setRecipeProperties(t)
                recipeDetailsViewModel.didRetrieveRecipe = true
            })

        recipeDetailsViewModel.isRecipeRequestTimeOut()
            .observe(this, Observer { requestTimeOut ->
                if (requestTimeOut) {
                    Log.e(TAG, "Request time out $requestTimeOut")
                    displayErrorScreen("Error retrieving data. Check network connection")
                }
            })

    }

    private fun setRecipeProperties(recipe: Recipe?) {
        if (recipe != null) {
            showProgressBar(View.GONE)
            scrollView.visibility = View.VISIBLE
            Glide.with(this@RecipeDetailsActivity)
                .load(recipe.imageUrl)
                .into(recipeImage)
            recipeTitle.text = recipe.title
            recipeRank.text = recipe.socialRank.toInt().toString()

            recipeIngredientsContainer.removeAllViews()
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
            val recipe: Recipe = intent.getSerializableExtra("recipe") as Recipe
            recipeDetailsViewModel.getRecipeById(recipe.recipeId)
        }

    }

    private fun displayErrorScreen(errorMessage: String) {
        recipeTitle.text = "Error retrieving recipe..."
        recipeRank.text = ""
        val textView = TextView(this)
        if (!errorMessage.equals("")) {
            textView.text = errorMessage
        } else {
            textView.text = "Error"
        }
        textView.textSize = 15f

        textView.layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        recipeIngredientsContainer.addView(textView)
        scrollView.visibility = View.VISIBLE
        showProgressBar(View.GONE)
        recipeDetailsViewModel.didRetrieveRecipe = false
    }
}
