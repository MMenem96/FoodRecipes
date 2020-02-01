package mitchcourses.com.myapplication.adapters

import android.view.View
import android.widget.TextView
import androidLearning.com.myapplication.R
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView

class RecipeViewHolder(itemView: View, val onRecipeListener: OnRecipeListener) :
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


    override fun onClick(v: View?) {
        onRecipeListener.onRecipeClick(adapterPosition)
    }
}