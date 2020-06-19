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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_note)

        if (intent.extras != null && intent.extras!!.containsKey(NoteHolder.MSG_KEY))
            noteEt.text = SpannableStringBuilder(intent.getStringExtra(NoteHolder.MSG_KEY))

        submitBtn.setOnClickListener {
            val note_msg = noteEt.text.toString()
            if (note_msg.isNotBlank()){
                val pos = intent.getIntExtra(NoteHolder.POS_KEY, 0)
                val intent = Intent()
                intent.putExtra(MSG_KEY, note_msg.trim())
                intent.putExtra(POS_KEY, pos)
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
        }
    }

    companion object {
        val MSG_KEY = "MSG_KEY"
        val POS_KEY = "POS_KEY"
    }
}