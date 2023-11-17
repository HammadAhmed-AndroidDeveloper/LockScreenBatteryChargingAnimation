package com.example.lockScreen.battery.charging.animation.service

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.lockScreen.battery.charging.animation.R
import com.example.lockScreen.battery.charging.animation.activities.MainActivity

class LockScreenNotification @SuppressLint("WrongConstant") constructor(context: Context)
    : NotificationCompat() {

    var builder: Builder
    private val manager: NotificationManager

    init {
        val data = Intent(context, MainActivity::class.java).putExtra("optimize", "yes")
        data.flags = 268468224
        val ID = "LockScreenChargingAnimator"
        val pi: PendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getActivity(
                context, 0, data, PendingIntent.FLAG_MUTABLE
            )
        } else {
            PendingIntent.getActivity(
                context, 0, data,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        }
        builder = Builder(context, ID).setSmallIcon(R.drawable.charging)
            .setContentTitle(context.resources.getString(R.string.app_name))
            .setContentText("App is running")
            .setStyle(BigTextStyle()).setAutoCancel(true).setPriority(1)
            .setContentIntent(pi)
        manager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            manager.createNotificationChannel(
                NotificationChannel(
                    ID,
                    "Lock Screen Charging Animator",
                    NotificationManager.IMPORTANCE_DEFAULT
                )
            )
        }
    }
}