package com.kirdmiv.todo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity() {

    var notes: MutableList<Note> = mutableListOf()
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var noteAdapter: NoteAdapter
    private val lastVisibleItemPosition: Int
        get() = linearLayoutManager.findLastVisibleItemPosition()

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

        noteAdapter = NoteAdapter(notes)
        linearLayoutManager = LinearLayoutManager(this)
        notesLv.layoutManager = linearLayoutManager
        notesLv.adapter = noteAdapter
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
        val noteMsg = data.getStringExtra(AddNoteActivity.MSG_KEY) ?: return
        if (requestCode == 1) {
            runOnUiThread {
                notes.add(Note(noteMsg, false))
                noteAdapter.notifyItemInserted(notes.lastIndex)
            }
            Log.d("NOTE ADDED", noteMsg)
        } else if (requestCode == 2){
            val pos: Int = data.getIntExtra(AddNoteActivity.POS_KEY, 0)
            runOnUiThread {
                notes[pos].msg = noteMsg
                noteAdapter.notifyItemChanged(pos)
            }
            Log.d("NOTE CHANGED", pos.toString())
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Note.saveNotes(this, notes)
    }
}