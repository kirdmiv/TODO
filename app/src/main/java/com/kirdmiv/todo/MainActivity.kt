package com.kirdmiv.todo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity() {

    var notes: List<Note> = emptyList()
    val noteAdapter = NoteAdapter(notes, this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener { view ->
            val intent: Intent = Intent(this, AddNoteActivity::class.java)
            startActivityForResult(intent, 1)
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }

        notes = Note.getNotes(this)
        Log.d("NOTES", notes.size.toString())
        for (note in notes) noteAdapter.add(note)

        notesLv.adapter = noteAdapter
        notesLv.setOnItemClickListener(object: AdapterView.OnItemClickListener {
            override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                Log.d("clicked", p2.toString())
                val intent: Intent = Intent(applicationContext, AddNoteActivity::class.java)
                intent.putExtra("note_msg", notes[p2].msg)
                intent.putExtra("completed", notes[p2].completed)
                startActivityForResult(intent, 2)
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data == null) return
        Log.d("request code", requestCode.toString())
        Log.d("result code", resultCode.toString())
        val noteMsg = data.getStringExtra("msg") ?: return
        notes = notes.plus(Note(noteMsg, false))
        noteAdapter.add(notes[notes.lastIndex])
        Log.d("NOTE ADDED", noteMsg)
    }

    override fun onDestroy() {
        super.onDestroy()
        Note.saveNotes(this, notes)
    }
}