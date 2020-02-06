package androidLearning.com.myapplication.models


import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.io.Serializable

//Implementing serializable to send objects from this class between intents
@Entity(tableName = "recipes")
data class Recipe(
    @SerializedName("_id")
    val id: String,
    @ColumnInfo(name = "image_url")
    @SerializedName("image_url")
    val imageUrl: String,
    @SerializedName("ingredients")
    var ingredients: List<String>,
    @ColumnInfo(name = "publisher")
    @SerializedName("publisher")
    val publisher: String,
    @SerializedName("publisher_url")
    val publisherUrl: String,
    @PrimaryKey
    @SerializedName("recipe_id")
    val recipeId: String,
    @ColumnInfo(name = "social_rank")
    @SerializedName("social_rank")
    val socialRank: Double,
    @SerializedName("source_url")
    val sourceUrl: String,
    @ColumnInfo(name = "title")
    @SerializedName("title")
    val title: String,
    @ColumnInfo(name = "timestamp")
    @SerializedName("timeStamp")
    var timeStamp: Int
) : Serializable