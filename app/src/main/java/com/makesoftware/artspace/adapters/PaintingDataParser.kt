package com.makesoftware.artspace.adapters

import android.content.Context
import com.makesoftware.artspace.R
import org.json.JSONArray
import org.json.JSONObject
import java.lang.IllegalArgumentException

class PaintingDataParser(private var context: Context) {

    private var numberOfPaintings: Int = 0

    private var paintingDataArray: JSONArray = JSONArray()

    init {
        val jsonString = parsePaintingsJson()

        paintingDataArray = JSONArray(jsonString)

        numberOfPaintings = paintingDataArray.length()
    }

    fun getPaintingData(paintingIndex: Int): JSONObject {
        if (paintingIndex >= paintingDataArray.length()) {
            // TODO: Make an empty painting data to return here.
            throw IllegalArgumentException("There is no painting data at this index.")
        }

        return paintingDataArray[paintingIndex] as JSONObject
    }

    fun getNumberOfPaintings(): Int {
        return numberOfPaintings
    }

    private fun parsePaintingsJson(): String {
        return context.resources.openRawResource(R.raw.paintings_data).bufferedReader().use {
            it.readText()
        }
    }
}