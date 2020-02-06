package mitchcourses.com.myapplication.adapters

import android.net.Uri
import android.view.View
import android.widget.TextView
import androidLearning.com.myapplication.R
import androidLearning.com.myapplication.models.Recipe
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.RequestOptions

class CategoryViewHolder(
    itemView: View,
    val onRecipeListener: OnRecipeListener,
    val requestManager: RequestManager
) :
    RecyclerView.ViewHolder(itemView), View.OnClickListener {

    var categoryTitle: TextView
    var categoryImage: AppCompatImageView

    init {
        categoryTitle = itemView.findViewById(R.id.category_title)
        categoryImage = itemView.findViewById(R.id.category_image)
        itemView.setOnClickListener(this)
    }

    fun onBind(recipe: Recipe) {
        val imagePath: Uri = Uri.parse(
            "android.resource://mitchcourses.com.myapplication/drawable/${recipe.title.toLowerCase()}"
        )
        requestManager.load(imagePath).apply(RequestOptions.circleCropTransform())
            .into(categoryImage)
        categoryTitle.text = recipe.title
    }

    override fun onClick(v: View?) {
        onRecipeListener.onCategoryClick(categoryTitle.text.toString())
    }
}