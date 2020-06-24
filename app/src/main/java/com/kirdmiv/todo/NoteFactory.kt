package com.kirdmiv.todo

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import java.io.File
import java.io.InputStream

class NoteFactory {
    companion object {
        fun saveNotes(context: Context, notes: MutableList<NoteTag>, fileName: String = "notes.json") {
            val path = context.filesDir
            val file = File(path, fileName)

            Log.d("FILE", file.absolutePath)

            val gsonPretty = GsonBuilder().setPrettyPrinting().create()

            val jsonNotesListPretty: String = gsonPretty.toJson(notes)
            file.writeText(jsonNotesListPretty)
            Log.d("SAVED NOTES", jsonNotesListPretty)
        }

        fun getNotes(context: Context, fileName: String = "notes.json") : MutableList<NoteTag> {
            var json: String = ""
            try {
                val  inputStream: InputStream = File(context.filesDir, fileName).inputStream()
                json = inputStream.bufferedReader().use{it.readText()}
                inputStream.close()
            } catch (ex: Exception) {
                Log.d("EXydsydsb", "sad")
                ex.printStackTrace()
                val importantNotes = NoteTag("Important", "imp", mutableListOf())
                val completedNotes = NoteTag("Completed", "com", mutableListOf())
                val uncompletedNotes = NoteTag("Uncompleted", "unc", mutableListOf())

                return mutableListOf(importantNotes, uncompletedNotes, completedNotes)
            }
            Log.d("dflsDATA GOT FROM JSON", json + "sss")
            val jsonArray = Gson().fromJson(json, JsonArray::class.java)
            return jsonArray.map { it.toString() }.map { Gson().fromJson(it, NoteTag::class.java) }.toMutableList()
        }
    }
}