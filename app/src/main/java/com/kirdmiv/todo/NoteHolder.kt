package com.kirdmiv.todo

import android.app.Activity
import android.content.Intent
import android.util.Log
import android.view.View
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.note.view.*

class NoteHolder(
    v: View
) : RecyclerView.ViewHolder(v), View.OnClickListener {
    private var msg: String? = null
    private var view = v
    private var index: Int = -1

    override fun onClick(p0: View?) {
        Log.d("click", index.toString())
        val context = itemView.context
        val editNoteIntent = Intent(context, AddNoteActivity::class.java)
        editNoteIntent.putExtra(MSG_KEY, msg)
        editNoteIntent.putExtra(POS_KEY, index)
        (context as Activity).startActivityForResult(editNoteIntent, 2)
    }

    fun bindNote(note: Note, pos: Int){
        this.msg = note.msg
        view.noteTv.text = msg
        this.index = pos
    }

    init {
        v.setOnClickListener(this)
    }

    companion object {
        val MSG_KEY = "NOTE_MSG"
        val POS_KEY = "NOTE_POS"
    }
}