package com.kirdmiv.todo

import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup

class NoteTag(
    val noteTag: String, title: String?, items: MutableList<Note>?
) : ExpandableGroup<Note>(title, items)