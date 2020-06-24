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
    var noteTags: MutableList<NoteTag> = mutableListOf()

    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var noteAdapter: NoteAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener {
            val intent = Intent(this, AddNoteActivity::class.java)
            startActivityForResult(intent, 1)
        }

        noteTags = NoteFactory.getNotes(this)

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
                    val tag: String = viewHolder.tag

                    Log.d("POS AND TAG", "$pos $tag")
                    val nt: Note = removeNote(tag, pos)

                    when (direction) {
                        ItemTouchHelper.RIGHT -> {
                            nt.completed = true
                            nt.tag = "Completed"
                            insertNote(nt, -1)
                            noteAdapter.notifyDataSetChanged()
                        }
                        ItemTouchHelper.LEFT -> {
                            noteAdapter.notifyDataSetChanged()
                            Snackbar.make(notesLv, nt.msg.toString(), Snackbar.LENGTH_LONG)
                                .setAction("UNDO") {
                                    insertNote(nt, pos)
                                    noteAdapter.notifyDataSetChanged()
                                }.show()
                        }
                    }
                    NoteFactory.saveNotes(applicationContext, noteTags)
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
                    RecyclerViewSwipeDecorator.Builder(
                        c,
                        recyclerView,
                        viewHolder,
                        dX,
                        dY,
                        actionState,
                        isCurrentlyActive
                    )
                        .addSwipeLeftBackgroundColor(
                            ContextCompat.getColor(
                                applicationContext,
                                R.color.bad
                            )
                        )
                        .addSwipeLeftActionIcon(R.drawable.ic_baseline_delete_24)
                        .addSwipeRightBackgroundColor(
                            ContextCompat.getColor(
                                applicationContext,
                                R.color.good
                            )
                        )
                        .addSwipeRightActionIcon(R.drawable.ic_baseline_check_24)
                        .create()
                        .decorate()

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
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)

        }
    }

    @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data == null) return
        val noteData = data.getStringExtra(AddNoteActivity.NOTE_KEY) ?: return
        val note: Note = Note.fromJson(noteData)
        if (requestCode == 1) {
            runOnUiThread {
                insertNote(note, -1)
                noteAdapter.notifyDataSetChanged()
            }
        } else if (requestCode == 2) {
            val pos: Int = data.getIntExtra(AddNoteActivity.POS_KEY, 0)
            val noteTg: String = data.getStringExtra(AddNoteActivity.TAG_KEY)
            runOnUiThread {
                insertNote(note, -1)
                removeNote(noteTg, pos)
                noteAdapter.notifyDataSetChanged()
            }
        }
        NoteFactory.saveNotes(applicationContext, noteTags)
    }

    private fun insertNote(note: Note, pos: Int) {
        for (ntg in noteTags) {
            if (ntg.noteTag == note.tag) {
                if (pos > -1)
                    ntg.items.add(pos, note)
                else
                    ntg.items.add(note)
                return
            }
        }
        Log.d("UNEXPECTED NOTE", Note.toJson(note))
    }

    private fun removeNote(noteTg: String, pos: Int): Note {
        for (ntg in noteTags) {
            if (ntg.noteTag == noteTg) {
                return ntg.items.removeAt(pos)
            }
        }
        return Note()
    }

    override fun onPause() {
        super.onPause()
        NoteFactory.saveNotes(this, noteTags)
    }

    override fun onStop() {
        super.onStop()
        NoteFactory.saveNotes(this, noteTags)
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