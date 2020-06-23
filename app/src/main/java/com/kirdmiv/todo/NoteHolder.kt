package com.kirdmiv.todo

import android.app.Activity
import android.content.Intent
import android.util.Log
import android.view.View
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.recyclerview.widget.RecyclerView
import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder
import kotlinx.android.synthetic.main.note.view.*

class NoteHolder(
    v: View
) : ChildViewHolder(v), View.OnClickListener {
    private var note = Note()
    private var view = v
    var index: Int = -1
    var tag: String = ""

    override fun onClick(p0: View?) {
        Log.d("click", index.toString())
        val context = itemView.context
        val editNoteIntent = Intent(context, AddNoteActivity::class.java)
        editNoteIntent.putExtra(NOTE_KEY, Note.toJson(note))
        editNoteIntent.putExtra(POS_KEY, index)
        editNoteIntent.putExtra(TAG_KEY, tag)
        (context as Activity).startActivityForResult(editNoteIntent, 2)
    }

    fun bindNote(note: Note, pos: Int, tag: String){
        this.note = note
        view.noteTv.text = note.msg
        this.index = pos
        this.tag = tag
        view.setBackgroundColor(note.color)
    }

    init {
        v.setOnClickListener(this)
    }

    companion object {
        const val NOTE_KEY = "NOTE"
        const val POS_KEY = "POS"
        const val TAG_KEY = "TAG"
    }
}