package com.example.meeshoassignment.service

import android.app.*
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Handler
import android.os.IBinder
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.example.meeshoassignment.utils.Constants
import com.example.meeshoassignment.Home
import com.example.meeshoassignment.R
import com.example.meeshoassignment.model.SessionDetailsModel
import java.util.*

class CountUpService:Service() {
    val NOTIFICATION_CHANNEL_ID = "com.example.assignment"
    val channelName = "Background Timer"
    private var seconds = 0
    lateinit var sessionModel:SessionDetailsModel
    private var running = false
    lateinit var  handler : Handler
    override fun onBind(p0: Intent?): IBinder? {

        return null
    }

    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) startMyOwnForeground() else startForeground(
            1,
            Notification()
        )

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun startMyOwnForeground() {

        val chan = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            channelName,
            NotificationManager.IMPORTANCE_NONE
        )
        chan.lightColor = Color.BLUE
        chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        val manager = (getSystemService(NOTIFICATION_SERVICE) as NotificationManager)
        manager.createNotificationChannel(chan)
        startForeground(Constants.NOTIFICATION_ID, CreateUpdateNotification("", null))
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {


        sessionModel= intent!!.getSerializableExtra(Constants.EXTRAS_SESSION_MODEL) as SessionDetailsModel
        running=true
         handler = Handler()
        handler.post(object : Runnable {
            override fun run() {
                val hours: Int = seconds / 3600
                val minutes: Int = seconds % 3600 / 60
                val secs: Int = seconds % 60

                // Format the seconds into hours, minutes,
                // and seconds.
                val time: String = java.lang.String.format(Locale.getDefault(), "%d:%02d:%02d", hours, minutes, secs)

                // Set the text view text.
              //  timeView.setText(time)

                // If running is true, increment the
                // seconds variable.
                if (running) {
                    seconds++
                }

                val intentTimer = Intent(Constants.TIME_INFO)
                intentTimer.putExtra(Constants.VALUE, time)
                intentTimer.putExtra(Constants.EXTRAS_SESSION_MODEL,sessionModel)
                sendBroadcast(intentTimer)
                CreateUpdateNotification(time,sessionModel)
                // Post the code again
                // with a delay of 1 second.
                handler.postDelayed(this, 1000)
            }
        })
        return START_NOT_STICKY

    }

    private fun CreateUpdateNotification(updateTime: String, sessionModel: SessionDetailsModel?): Notification {
        val notificationBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)


        val notificationIntent = Intent(this, Home::class.java)
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent = PendingIntent.getActivity(
            this, 0, notificationIntent, PendingIntent.FLAG_ONE_SHOT
        )

        val notification: Notification = notificationBuilder.setOngoing(true)
            .setSmallIcon(R.drawable.ic_baseline_timer_24)
            .setContentTitle(getString(R.string.Timer)+getString(R.string.duration)+updateTime)
            .setContentText( getString(R.string.location_id)+sessionModel?.location_id)
            .setPriority(NotificationManager.IMPORTANCE_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
        notification.flags = notification.flags or Notification.FLAG_AUTO_CANCEL

        val mNotificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        mNotificationManager.notify(Constants.NOTIFICATION_ID, notification)
        return notification


    }

    override fun onDestroy() {
        super.onDestroy()
        running=false;
        handler.removeCallbacksAndMessages(null)
    }
}