package com.kirdmiv.todo

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import java.io.File
import java.io.InputStream

class Note(
    var msg: String,
    var completed: Boolean
) {

    companion object {
        fun saveNotes(context: Context, notes: List<Note>, fileName: String = "notes.json") {
            val path = context.filesDir
            val file = File(path, fileName)

            Log.d("FILE", file.absolutePath)

            val gsonPretty = GsonBuilder().setPrettyPrinting().create()

            val jsonNotesListPretty: String = gsonPretty.toJson(notes)
            file.writeText(jsonNotesListPretty)
            Log.d("SAVED NOTES", jsonNotesListPretty)
        }

        fun getNotes(context: Context, fileName: String = "notes.json") : List<Note> {
            var json: String = ""
            try {
                val  inputStream: InputStream = File(context.filesDir, fileName).inputStream()
                json = inputStream.bufferedReader().use{it.readText()}
            } catch (ex: Exception) {
                Log.d("EX", "sad")
                ex.printStackTrace()
                return emptyList()
            }
            Log.d("DATA GOT FROM JSON", json)
            val jsonArray = Gson().fromJson(json, JsonArray::class.java)
            return jsonArray.map { it.toString() }.map { Gson().fromJson(it, Note::class.java) }
        }
    }
}