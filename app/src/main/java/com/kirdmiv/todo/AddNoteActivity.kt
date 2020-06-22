package com.kirdmiv.todo

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.View
import androidx.core.text.buildSpannedString
import kotlinx.android.synthetic.main.activity_add_note.*

class AddNoteActivity : AppCompatActivity() {
    private var note = Note()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_note)

        if (intent.extras != null && intent.extras!!.containsKey(NoteHolder.NOTE_KEY))
            note = Note.fromJson(intent.getStringExtra(NoteHolder.NOTE_KEY))

        noteEt.text = SpannableStringBuilder(note.msg)

        submitBtn.setOnClickListener {
            collectNote()
            if (note.msg != null && note.msg!!.isNotBlank()){
                val pos = intent.getIntExtra(NoteHolder.POS_KEY, 0)
                val noteTg = intent.getStringExtra(NoteHolder.TAG_KEY)
                val intent = Intent()
                intent.putExtra(NOTE_KEY, Note.toJson(note))
                intent.putExtra(POS_KEY, pos)
                intent.putExtra(TAG_KEY, noteTg)
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
        }
    }

    private fun collectNote(){
        note.msg = noteEt.text.toString().trim()
    }

    companion object {
        const val NOTE_KEY = "NOTE_KEY"
        const val POS_KEY = "POS_KEY"
        const val TAG_KEY = "TAG_KEY"
    }
}