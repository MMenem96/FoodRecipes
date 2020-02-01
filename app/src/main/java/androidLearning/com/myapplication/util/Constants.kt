package mitchcourses.com.myapplication.util

class Constants {

    companion object {
        val BASE_URL = "https://recipesapi.herokuapp.com/"
        val NETWORK_TIMEOUT = 10000L
        val DEFAULT_SEARCH_CATEGORY_IMAGES = arrayOf(
            "Barbecue", "Breakfast", "Chicken",
            "Beef", "Brunch", "Dinner", "Wine", "Italian"
        )
    }
}