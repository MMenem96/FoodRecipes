package mitchcourses.com.myapplication.adapters

import android.view.View
import android.widget.TextView
import androidLearning.com.myapplication.R
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView

class CategoryViewHolder(itemView: View, val onRecipeListener: OnRecipeListener) :
    RecyclerView.ViewHolder(itemView), View.OnClickListener {

    var categoryTitle: TextView
    var categoryImage: AppCompatImageView

    init {
        categoryTitle = itemView.findViewById(R.id.category_title)
        categoryImage = itemView.findViewById(R.id.category_image)
        itemView.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        onRecipeListener.onCategoryClick(categoryTitle.text.toString())
    }
}