package com.example.lockScreen.battery.charging.animation.service

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.TextView

class BatteryChecker(private val batteryTxtView: TextView) : BroadcastReceiver() {
    @SuppressLint("SetTextI18n")
    override fun onReceive(context: Context, intent: Intent) {
        val intExtra =
            (intent.getIntExtra("level", -1) * 100).toFloat() / intent.getIntExtra(
                "scale", -1
            ).toFloat()
        batteryTxtView.text = "$intExtra%"
    }
}