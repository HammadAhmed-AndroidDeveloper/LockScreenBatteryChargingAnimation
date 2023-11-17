package com.example.lockScreen.battery.charging.animation.utils

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import com.example.lockScreen.battery.charging.animation.R
import com.example.lockScreen.battery.charging.animation.service.LockScreenBatteryAnimationService
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.io.IOException

object Utils {

    fun getBatteryPercentage(context: Context): Int {
        val receiver = context.registerReceiver(
            null, IntentFilter("android.intent.action.BATTERY_CHANGED")
        )
        return (receiver!!.getIntExtra("level", -1)
            .toFloat() / receiver.getIntExtra("scale", -1).toFloat() * 100.0f).toInt()
    }

    fun convertUriToBitmap(context: Context, uri: Uri?): Bitmap? {
        return try {
            val openFileDescriptor = context.contentResolver.openFileDescriptor(
                uri!!, "r"
            )
            val decodeFileDescriptor = BitmapFactory.decodeFileDescriptor(
                openFileDescriptor!!.fileDescriptor
            )
            openFileDescriptor.close()
            decodeFileDescriptor
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    public fun batteryOptimizationDialog(context: AppCompatActivity) {
        MaterialAlertDialogBuilder(context).setTitle("Background Permission" as CharSequence)
            .setMessage("Allow app to run in background" as CharSequence)
            .setCancelable(false).setPositiveButton(
                "Allow" as CharSequence
            ) { dialogInterface, _ ->
                dialogInterface.dismiss()
                Intent().apply {
                action = "android.settings.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS"
                data = Uri.parse("package:" + context.packageName)
                context.startActivityForResult(this, 123)
                }
            }.setNegativeButton(
                "Leave" as CharSequence
            ) { di, _ ->
                di.dismiss()
                context.finish()
            }.show()
    }

    fun hasTakenOverlayPermission(activity: AppCompatActivity) {
        MaterialAlertDialogBuilder(activity).setTitle("Permission Required" as CharSequence)
            .setCancelable(false).setMessage(
                activity.resources.getString(R.string.overlay) as CharSequence
            ).setPositiveButton(
                "Allow" as CharSequence
            ) { _, _ ->
                activity.startActivityForResult(
                    Intent(
                        "android.settings.action.MANAGE_OVERLAY_PERMISSION",
                        Uri.parse("package:${activity.packageName}")
                    ), 1
                )
            }.setNegativeButton(
                "Leave" as CharSequence
            ) { _, _ ->
                activity.finish()
            }.show()
    }

    fun isMyServiceRunning(activity: AppCompatActivity): Boolean {
        for (info in (activity.getSystemService(AppCompatActivity.ACTIVITY_SERVICE) as ActivityManager).getRunningServices(
            Int.MAX_VALUE
        )) {
            if (LockScreenBatteryAnimationService::class.java.name == info.service.className) {
                return true
            }
        }
        return false
    }
}