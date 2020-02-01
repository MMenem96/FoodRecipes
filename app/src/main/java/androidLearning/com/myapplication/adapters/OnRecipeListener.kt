package mitchcourses.com.myapplication.adapters

interface OnRecipeListener {

    fun onRecipeClick(position: Int)
    fun onCategoryClick(category: String)

}