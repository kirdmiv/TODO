package com.kirdmiv.todo

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.CheckBox
import android.widget.RadioButton
import android.widget.TextView
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.ContextCompat.startActivity


class NoteAdapter(
    var notes: List<Note>,
    val context: Context
): BaseAdapter() {
    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        val inflater: LayoutInflater = LayoutInflater.from(context)
        var view: View
        if (p1 == null) {
            view = inflater.inflate(R.layout.note, p2, false)
        } else {
            view = p1
        }

        val note: Note = getItem(p0) as Note

        (view.findViewById<View>(R.id.noteTv) as TextView).text = note.msg

        val rb = view.findViewById<View>(R.id.rb) as RadioButton
        rb.tag = p0
        rb.isChecked = note.completed
        rb.setOnClickListener {
            Log.d("CHECKED", rb.isChecked.toString())
            rb.isChecked = !rb.isChecked
            (getItem(rb.tag as Int) as Note).completed = rb.isChecked
        }
        return view
    }

    override fun getItem(p0: Int): Any {
        return notes[p0]
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getCount(): Int {
        return notes.size
    }

    fun add(note: Note){
        notes = notes.plus(note)
    }
}

