package androidLearning.com.myapplication.presistence

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class IngredientsConverters {

    @TypeConverter
    fun fromStringToArray(value: String): List<String> {
        val listType: Type = object : TypeToken<List<String>>() {}.type

       return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromArrayToString(value: List<String>): String {
        return Gson().toJson(value)
    }
}


