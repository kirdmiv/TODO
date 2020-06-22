package com.kirdmiv.todo

import android.view.View
import android.view.animation.Animation.RELATIVE_TO_SELF
import android.view.animation.RotateAnimation
import android.widget.TextView
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup
import com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder
import kotlinx.android.synthetic.main.list_item_tag.view.*


class TagHolder(itemView: View) : GroupViewHolder(itemView) {
    fun setTitle(group: ExpandableGroup<*>?){
        if (group is NoteTag) {
            itemView.tagTv.text = group.noteTag
        }
    }

    override fun expand() {
        animateExpand()
    }

    override fun collapse() {
        animateCollapse()
    }

    private fun animateExpand() {
        val rotate = RotateAnimation(
            360F,
            180F,
            RELATIVE_TO_SELF,
            0.5f,
            RELATIVE_TO_SELF,
            0.5f
        )
        rotate.duration = 300
        rotate.fillAfter = true
        itemView.expandV.animation = rotate
    }

    private fun animateCollapse() {
        val rotate = RotateAnimation(
            180F,
            360F,
            RELATIVE_TO_SELF,
            0.5f,
            RELATIVE_TO_SELF,
            0.5f
        )
        rotate.duration = 300
        rotate.fillAfter = true
        itemView.expandV.animation = rotate
    }
}