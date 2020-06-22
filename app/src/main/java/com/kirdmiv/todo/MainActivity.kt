package com.kirdmiv.todo

import android.content.Intent
import android.graphics.Canvas
import android.os.Bundle
import android.os.PersistableBundle
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
                    val pos: Int = (viewHolder as NoteHolder).index
                    val tag: String = (viewHolder as NoteHolder).tag

                    Log.d("POS AND TAG", "$pos $tag")

                    var nt: Note = Note()
                    for (ntg in noteTags){
                        if (ntg.noteTag == tag){
                            nt = ntg.items.removeAt(pos)
                            break
                        }
                    }

                    when (direction){
                        ItemTouchHelper.RIGHT -> {
                            nt.completed = true
                            for (ntg in noteTags){
                                if (ntg.noteTag == "Completed"){
                                    ntg.items.add(nt)
                                    break
                                }
                            }
                            Log.d("Swiped", "+")
                            //noteAdapter.notifyItemChanged(pos)
                            noteAdapter.notifyDataSetChanged()
                        }
                        ItemTouchHelper.LEFT -> {
                            noteAdapter.notifyDataSetChanged()
                            Snackbar.make(notesLv, nt.msg.toString(), Snackbar.LENGTH_LONG)
                                .setAction("UNDO") {
                                    for (ntg in noteTags){
                                        if (ntg.noteTag == tag){
                                            ntg.items.add(pos, nt)
                                            break
                                        }
                                    }
                                    Log.d("Swiped", nt.msg)
                                    noteAdapter.notifyDataSetChanged()
                                }.show()
                        }
                    }
                }

                override fun getSwipeDirs(recyclerView: RecyclerView, viewHolder: ViewHolder): Int {
                    if (viewHolder is TagHolder) return 0
                    return super.getSwipeDirs(recyclerView, viewHolder)
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
        val noteData = data.getStringExtra(AddNoteActivity.NOTE_KEY) ?: return
        val note: Note = Note.fromJson(noteData)
        if (requestCode == 1) {
            runOnUiThread {
                for (ntg in noteTags){
                    if (ntg.noteTag == note.tag){
                        ntg.items.add(note)
                        break
                    }
                }
                //notes.add(Note(note.msg, false))
                //noteTags[1].items.add(notes[notes.lastIndex])
                noteAdapter.notifyDataSetChanged()
                //noteAdapter.notifyItemChanged(1)
            }
            Log.d("NOTE ADDED", note.msg)
        } else if (requestCode == 2){
            val pos: Int = data.getIntExtra(AddNoteActivity.POS_KEY, 0)
            val noteTg: String = data.getStringExtra(AddNoteActivity.TAG_KEY)
            runOnUiThread {
                for (ntg in noteTags){
                    if (ntg.noteTag == noteTg){
                        ntg.items.removeAt(pos)
                    }
                    if (ntg.noteTag == note.tag){
                        ntg.items.add(note)
                    }
                }
                noteAdapter.notifyDataSetChanged()
            }
            Log.d("NOTE CHANGED", pos.toString())
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Note.saveNotes(this, notes)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        noteAdapter.onRestoreInstanceState(savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
        noteAdapter.onSaveInstanceState(outState)
    }
}