package mitchcourses.com.myapplication.util

class Constants {

    companion object {
        val BASE_URL = "https://recipesapi.herokuapp.com/"
        const val NETWORK_CONNECTION_TIMEOUT = 10L //10 SEC
        const val NETWORK_READ_TIMEOUT = 5L //10 SEC
        const val NETWORK_WRITE_TIMEOUT = 5L //10 SEC
        const val RECIPE_REFRESH_TIME = 60 * 60 * 24 * 30//30 days to refresh cache
        val DEFAULT_SEARCH_CATEGORY_IMAGES = arrayOf(
            "Barbecue", "Breakfast", "Chicken",
            "Beef", "Brunch", "Dinner", "Wine", "Italian"
        )
    }
}