package com.kirdmiv.todo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.thoughtbot.expandablerecyclerview.ExpandableRecyclerViewAdapter
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup

class NoteAdapter(
    groups: MutableList<out ExpandableGroup<*>>
) : ExpandableRecyclerViewAdapter<TagHolder, NoteHolder>(groups) {
    override fun onCreateGroupViewHolder(parent: ViewGroup?, viewType: Int): TagHolder {
        val view: View = LayoutInflater.from(parent?.context).inflate(
            R.layout.list_item_tag,
            parent,
            false
        )
        return TagHolder(view)
    }

    override fun onCreateChildViewHolder(parent: ViewGroup?, viewType: Int): NoteHolder {
        val view: View = LayoutInflater.from(parent?.context).inflate(
            R.layout.note,
            parent,
            false
        )
        return NoteHolder(view)
    }

    override fun onBindChildViewHolder(
        holder: NoteHolder?,
        flatPosition: Int,
        group: ExpandableGroup<*>?,
        childIndex: Int
    ) {
        holder?.bindNote((group as NoteTag).items[childIndex], childIndex)
    }

    override fun onBindGroupViewHolder(
        holder: TagHolder?,
        flatPosition: Int,
        group: ExpandableGroup<*>?
    ) {
        holder?.setTitle(group)
    }
}