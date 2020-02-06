package mitchcourses.com.myapplication.adapters

import android.view.View
import android.widget.TextView
import androidLearning.com.myapplication.R
import androidLearning.com.myapplication.models.Recipe
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.bumptech.glide.util.ViewPreloadSizeProvider

class RecipeViewHolder(
    itemView: View,
    private val onRecipeListener: OnRecipeListener,
    private val requestManager: RequestManager,
    private val viewPreloadSizeProvider: ViewPreloadSizeProvider<String>
) :
    RecyclerView.ViewHolder(itemView),
    View.OnClickListener {


    var title: TextView
    var publisher: TextView
    var socialScore: TextView
    var image: AppCompatImageView

    init {
        title = itemView.findViewById(R.id.recipe_title)
        publisher = itemView.findViewById(R.id.recipe_publisher)
        socialScore = itemView.findViewById(R.id.recipe_social_score)
        image = itemView.findViewById(R.id.recipe_image)
        itemView.setOnClickListener(this)
    }

    fun onBind(recipe: Recipe) {
        requestManager.load(recipe.imageUrl).into(image)
        title.text = recipe.title
        publisher.text = recipe.publisher
        socialScore.text =
            Math.round(recipe.socialRank ?: 0.0).toString()
        viewPreloadSizeProvider.setView(image)
    }

    override fun onClick(v: View?) {
        onRecipeListener.onRecipeClick(adapterPosition)
    }
}