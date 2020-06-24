package com.kirdmiv.todo

import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import com.google.gson.Gson

class Note(
    var msg: String? = "",
    var completed: Boolean = false,
    var color: Int = -1,
    private var title: String? = "",
    var tag: String? = "Uncompleted",
    var notificationId: Int = 0
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readByte() != 0.toByte(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readString()
    )

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

        fun toJson(note: Note): String {
            return Gson().toJson(note)
        }

        fun fromJson(json: String): Note {
            return Gson().fromJson(json, Note::class.java)
        }
    }
}