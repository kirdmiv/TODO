package com.kirdmiv.todo

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        val name = "TODO channel"
        val descriptionText = "TODO notifications com.kirdmiv.todo https://github.com/kirdmiv/TODO"
        val importance = NotificationManager.IMPORTANCE_HIGH
        val mChannel = NotificationChannel(CHANNEL_ID, name, importance)
        mChannel.description = descriptionText
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(mChannel)
    }

    companion object {
        const val CHANNEL_ID: String = "main_todo_channel"
        const val NOTIFICATION_PREFERENCES = "notification_preferenses_com_kirdmiv_todo"
    }
}