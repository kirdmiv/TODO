package com.kirdmiv.todo

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import java.io.File
import java.io.InputStream

class Note(
    var msg: String? = "",
    var completed: Boolean = false,
    var color: Int = -1,
    var title: String? = "",
    var tag: String? = "Uncompleted",
    var notificationId: Int = 0
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readByte() != 0.toByte(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(msg)
        parcel.writeByte(if (completed) 1 else 0)
        parcel.writeInt(color)
        parcel.writeString(title)
        parcel.writeString(tag)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Note> {
        override fun createFromParcel(parcel: Parcel): Note {
            return Note(parcel)
        }

        override fun newArray(size: Int): Array<Note?> {
            return arrayOfNulls(size)
        }

        fun saveNotes(context: Context, notes: MutableList<Note>, fileName: String = "notes.json") {
            val path = context.filesDir
            val file = File(path, fileName)

            Log.d("FILE", file.absolutePath)

            val gsonPretty = GsonBuilder().setPrettyPrinting().create()

            val jsonNotesListPretty: String = gsonPretty.toJson(notes)
            file.writeText(jsonNotesListPretty)
            Log.d("SAVED NOTES", jsonNotesListPretty)
        }

        fun getNotes(context: Context, fileName: String = "notes.json") : MutableList<Note> {
            var json: String = ""
            try {
                val  inputStream: InputStream = File(context.filesDir, fileName).inputStream()
                json = inputStream.bufferedReader().use{it.readText()}
            } catch (ex: Exception) {
                Log.d("EX", "sad")
                ex.printStackTrace()
                return mutableListOf()
            }
            Log.d("DATA GOT FROM JSON", json)
            val jsonArray = Gson().fromJson(json, JsonArray::class.java)
            return jsonArray.map { it.toString() }.map { Gson().fromJson(it, Note::class.java) }.toMutableList()
        }

        fun toJson(note: Note): String {
            Log.d("OUTPUT NOTE", Gson().toJson(note))
            return Gson().toJson(note)
        }

        fun fromJson(json: String): Note {
            Log.d("INPUT JSON", json)
            return Gson().fromJson(json, Note::class.java)
        }
    }
}