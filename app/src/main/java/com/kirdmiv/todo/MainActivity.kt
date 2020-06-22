package com.kirdmiv.todo

import android.content.Intent
import android.graphics.Canvas
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity() {

    var notes: MutableList<Note> = mutableListOf()

    var importantNotes = NoteTag("Important", "imp", mutableListOf())
    var completedNotes = NoteTag("Completed", "com", mutableListOf())
    var uncompletedNotes = NoteTag("Uncompleted", "unc", mutableListOf())

    var noteTags: MutableList<NoteTag> = mutableListOf(importantNotes, uncompletedNotes, completedNotes)

    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var noteAdapter: NoteAdapter

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

        for (note in notes){
            if (note.completed) noteTags[2].items.add(note)
            else noteTags[1].items.add(note)
        }

        noteAdapter = NoteAdapter(noteTags)
        linearLayoutManager = LinearLayoutManager(this)
        notesLv.layoutManager = linearLayoutManager
        notesLv.adapter = noteAdapter

        val mIth = ItemTouchHelper(
            object : ItemTouchHelper.SimpleCallback(
                0,
                ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
            ) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: ViewHolder, target: ViewHolder
                ): Boolean {
                    return false
                }

                override fun onSwiped(
                    viewHolder: ViewHolder,
                    direction: Int
                ) {
                    val pos: Int = viewHolder.adapterPosition

                    when (direction){
                        ItemTouchHelper.RIGHT -> {
                            notes[pos].completed = true
                            Log.d("Swiped", notes[pos].completed.toString())
                            noteAdapter.notifyItemChanged(pos)
                        }
                        ItemTouchHelper.LEFT -> {
                            val deletedNote = notes.removeAt(pos)
                            noteAdapter.notifyItemRemoved(pos)
                            Snackbar.make(notesLv, deletedNote.msg.toString(), Snackbar.LENGTH_LONG)
                                .setAction("UNDO") {
                                    notes.add(pos, deletedNote)
                                    Log.d("Swiped", notes[pos].msg)
                                    noteAdapter.notifyItemInserted(pos)
                                }.show()
                        }
                    }
                }

                override fun onChildDraw(
                    c: Canvas,
                    recyclerView: RecyclerView,
                    viewHolder: ViewHolder,
                    dX: Float,
                    dY: Float,
                    actionState: Int,
                    isCurrentlyActive: Boolean
                ) {
                    RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        .addSwipeLeftBackgroundColor(ContextCompat.getColor(applicationContext, R.color.bad))
                        .addSwipeLeftActionIcon(R.drawable.ic_baseline_delete_24)
                        .addSwipeRightBackgroundColor(ContextCompat.getColor(applicationContext, R.color.good))
                        .addSwipeRightActionIcon(R.drawable.ic_baseline_check_24)
                        .create()
                        .decorate();

                    super.onChildDraw(
                        c,
                        recyclerView,
                        viewHolder,
                        dX,
                        dY,
                        actionState,
                        isCurrentlyActive
                    )
                }
            })
            mIth.attachToRecyclerView(notesLv)
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
                noteTags[1].items.add(notes[notes.lastIndex])
                //noteAdapter.notifyDataSetChanged()
                noteAdapter.notifyItemChanged(1)
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