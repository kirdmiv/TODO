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

        //noteEt.text = SpannableStringBuilder(intent.getStringExtra("note_msg"))

        submitBtn.setOnClickListener {
            val note_msg = noteEt.text.toString()
            if (note_msg.isNotBlank()){
                val intent = Intent()
                intent.putExtra("msg", note_msg.trim())
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
        }
    }
}