package com.example.data.db

import androidx.room.TypeConverter
import com.example.data.model.PatternItem
import org.json.JSONArray
import org.json.JSONObject

class Converters {
    @TypeConverter
    fun fromStringList(value: List<String>?): String {
        if (value == null) return "[]"
        val array = JSONArray()
        for (item in value) {
            array.put(item)
        }
        return array.toString()
    }

    @TypeConverter
    fun toStringList(value: String?): List<String> {
        if (value.isNullOrEmpty()) return emptyList()
        val list = mutableListOf<String>()
        try {
            val array = JSONArray(value)
            for (i in 0 until array.length()) {
                list.add(array.getString(i))
            }
        } catch (_: Exception) {
            // fallback
        }
        return list
    }

    @TypeConverter
    fun fromFloatList(value: List<Float>?): String {
        if (value == null) return "[]"
        val array = JSONArray()
        for (item in value) {
            array.put(item.toDouble())
        }
        return array.toString()
    }

    @TypeConverter
    fun toFloatList(value: String?): List<Float> {
        if (value.isNullOrEmpty()) return listOf(0.3f, 0.9f, 0.5f, 0.4f, 0.6f, 0.2f, 0.4f)
        val list = mutableListOf<Float>()
        try {
            val array = JSONArray(value)
            for (i in 0 until array.length()) {
                list.add(array.getDouble(i).toFloat())
            }
        } catch (_: Exception) {
            return listOf(0.3f, 0.9f, 0.5f, 0.4f, 0.6f, 0.2f, 0.4f)
        }
        return list
    }

    @TypeConverter
    fun fromPatternItemList(value: List<PatternItem>?): String {
        if (value == null) return "[]"
        val array = JSONArray()
        for (item in value) {
            val obj = JSONObject()
            obj.put("title", item.title)
            obj.put("countOrLabel", item.countOrLabel)
            array.put(obj)
        }
        return array.toString()
    }

    @TypeConverter
    fun toPatternItemList(value: String?): List<PatternItem> {
        if (value.isNullOrEmpty()) return emptyList()
        val list = mutableListOf<PatternItem>()
        try {
            val array = JSONArray(value)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    PatternItem(
                        title = obj.optString("title", ""),
                        countOrLabel = obj.optString("countOrLabel", "")
                    )
                )
            }
        } catch (_: Exception) {
            // fallback
        }
        return list
    }
}
