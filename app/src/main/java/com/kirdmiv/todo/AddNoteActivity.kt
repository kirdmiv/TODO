package com.kirdmiv.todo

import android.annotation.SuppressLint
import android.app.*
import android.app.DatePickerDialog.OnDateSetListener
import android.app.TimePickerDialog.OnTimeSetListener
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.SystemClock
import android.text.SpannableStringBuilder
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.FragmentActivity
import dev.sasikanth.colorsheet.ColorSheet
import kotlinx.android.synthetic.main.activity_add_note.*
import java.util.*


class AddNoteActivity : AppCompatActivity() {
    private var note = Note()
    private var colors: IntArray = intArrayOf()
    private var curColor: Int = 0
    private val tags: Array<String> = arrayOf("Important", "Uncompleted", "Completed")
    private val date: Calendar = Calendar.getInstance()
    private var checked: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_note)

        colors = intArrayOf(
            ResourcesCompat.getColor(resources, R.color.white, null),
            ContextCompat.getColor(applicationContext, R.color.red),
            ContextCompat.getColor(applicationContext, R.color.green),
            ContextCompat.getColor(applicationContext, R.color.blue),
            ContextCompat.getColor(applicationContext, R.color.cyan),
            ContextCompat.getColor(applicationContext, R.color.yellow),
            ContextCompat.getColor(applicationContext, R.color.purple))

        colorB.setBackgroundColor(colors[0])
        curColor = colors[0]
        colorB.setTextColor(ContextCompat.getColor(applicationContext, R.color.black))

        if (intent.extras != null && intent.extras!!.containsKey(NoteHolder.NOTE_KEY))
            note = Note.fromJson(intent.getStringExtra(NoteHolder.NOTE_KEY))

        noteEt.text = SpannableStringBuilder(note.msg)

        colorB.setOnClickListener {
            ColorSheet().colorPicker(
                colors = colors,
                selectedColor = ResourcesCompat.getColor(resources, R.color.white, null),
                listener = { color ->
                    colorB.setBackgroundColor(color)
                    curColor = color
                })
                .show(supportFragmentManager)
        }

        tagField.setAdapter(ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, tags))

        dateTv.text = "${date[Calendar.MONTH] + 1}/${date[Calendar.DAY_OF_MONTH]}/${date[Calendar.YEAR]}"
        timeTv.text = "${date[Calendar.HOUR]}:${date[Calendar.MINUTE]} ${getAMPM()}"

        checked = false
        notifyCb.setOnClickListener {
            if (notifyCb.isChecked) {
                dateTv.visibility = View.VISIBLE
                timeTv.visibility = View.VISIBLE
                if (!checked) {
                    setTime(this)
                    setDate(this)
                }
                checked = true
            } else {
                dateTv.visibility = View.INVISIBLE
                timeTv.visibility = View.INVISIBLE
            }
        }

        dateTv.setOnClickListener {
            if (notifyCb.isChecked) {
                setDate(this)
            }
        }

        timeTv.setOnClickListener {
            if (notifyCb.isChecked) {
                setTime(this)
            }
        }

        submitBtn.setOnClickListener {
            collectNote()
            if (note.msg != null && note.msg!!.isNotBlank()){
                if (notifyCb.isChecked)
                    createNotification(this)

                val pos = intent.getIntExtra(NoteHolder.POS_KEY, 0)
                val noteTg = intent.getStringExtra(NoteHolder.TAG_KEY)
                val intent = Intent()
                intent.putExtra(NOTE_KEY, Note.toJson(note))
                intent.putExtra(POS_KEY, pos)
                intent.putExtra(TAG_KEY, noteTg)
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
        }
    }

    private fun getAMPM() : String{
        if (date[Calendar.AM_PM] == Calendar.AM) return "AM"
        return "PM"
    }

    private fun getNotificationId() : Int {
        if (note.notificationId != 0) return note.notificationId
        val sharedPref = getSharedPreferences(App.NOTIFICATION_PREFERENCES, Context.MODE_PRIVATE)
        val curId: Int = sharedPref.getInt(NOTIFICATION_ID, 0) + 1
        sharedPref.edit().putInt(NOTIFICATION_ID, curId).apply()
        return curId
    }

    private fun createNotification(context: Context) {
        val notificationId = getNotificationId()
        note.notificationId = notificationId
        val time = SystemClock.elapsedRealtime() + (date.timeInMillis - Calendar.getInstance().timeInMillis)
        Log.d("NOTIFICATION_ID", notificationId.toString())

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        }
        val pendingActivity = PendingIntent.getActivity(this, 0, intent, 0)

        val notificationBuilder = NotificationCompat.Builder(this, App.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_baseline_notification_important_24)
            .setContentTitle("TODO ${note.tag}")
            .setContentText(note.msg)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingActivity)
            .setAutoCancel(true)

        val notification: Notification = notificationBuilder.build()

        val notificationIntent = Intent(context, NotificationPublisher::class.java)
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_ID, notificationId)
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION, notification)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            notificationId,
            notificationIntent,
            0
        )

//        Log.d("Current time", SystemClock.elapsedRealtime().toString())
//        Log.d("Current time", Calendar.getInstance().timeInMillis.toString())
//        Log.d("Picked time", time.toString())
        val alarmManager: AlarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, time, pendingIntent)
    }

    private fun collectNote(){
        note.msg = noteEt.text.toString().trim()
        note.color = curColor
        note.tag = tagField.text.toString().trim()
        if (note.tag!!.isBlank()) {
            note.tag = getString(R.string.default_text)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setDate(context: Context){
        DatePickerDialog(
            context,
            OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                date.set(year, monthOfYear, dayOfMonth)
                dateTv.text = "${date[Calendar.MONTH] + 1}/${date[Calendar.DAY_OF_MONTH]}/${date[Calendar.YEAR]}"
            },
            date[Calendar.YEAR],
            date[Calendar.MONTH],
            date[Calendar.DATE]
        ).show()
    }

    @SuppressLint("SetTextI18n")
    private fun setTime(context: Context){
        TimePickerDialog(
            context,
            OnTimeSetListener { _, hourOfDay, minute ->
                date[Calendar.HOUR_OF_DAY] = hourOfDay
                date[Calendar.MINUTE] = minute
                timeTv.text = "${date[Calendar.HOUR]}:${date[Calendar.MINUTE]} ${getAMPM()}"
            },
            date[Calendar.HOUR_OF_DAY],
            date[Calendar.MINUTE],
            true
        ).show()
    }

    companion object {
        const val NOTE_KEY = "NOTE_KEY"
        const val POS_KEY = "POS_KEY"
        const val TAG_KEY = "TAG_KEY"
        const val NOTIFICATION_ID = "notification_id_for_main_channel"
    }
}