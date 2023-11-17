package com.example.lockScreen.battery.charging.animation.service

import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.airbnb.lottie.LottieAnimationView
import com.example.lockScreen.battery.charging.animation.R
import com.example.lockScreen.battery.charging.animation.activities.MainActivity
import com.example.lockScreen.battery.charging.animation.interfaces.ClicksListener
import com.example.lockScreen.battery.charging.animation.interfaces.DoubleTapListener
import com.example.lockScreen.battery.charging.animation.utils.Preferences.getAnimation
import com.example.lockScreen.battery.charging.animation.utils.Preferences.getAnimationId
import com.example.lockScreen.battery.charging.animation.utils.Preferences.getClosing
import com.example.lockScreen.battery.charging.animation.utils.Preferences.getLock
import com.example.lockScreen.battery.charging.animation.utils.Preferences.getPerVal
import com.example.lockScreen.battery.charging.animation.utils.Preferences.getUri
import com.example.lockScreen.battery.charging.animation.view.LockScreenChargingAnimationView

class LockScreenBatteryAnimationService : Service(), ClicksListener {
    private var duration = -1
    private var customChargingScreen: LockScreenChargingAnimationView? = null
    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onSingleClick() {}
    override fun onCreate() {
        super.onCreate()

        val mAnimation = getAnimationId(this, R.raw.animal1)

        val isLock = getLock(this)
        val isClosing = getClosing(this)
        val custom = getAnimation(this, -1)
        val str = getUri(this, "uri")
        val isPer = getPerVal(this)

        val windowManager2 = applicationContext.getSystemService(WINDOW_SERVICE) as WindowManager
        val layoutParams = WindowManager.LayoutParams()

        wManager = application.getSystemService(WINDOW_SERVICE) as WindowManager
        if (Build.VERSION.SDK_INT >= 26) {
            layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            layoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR
        }

        layoutParams.format = -3
        if (isLock) {
            layoutParams.flags = 6817152
        } else if (duration == -1) {
            layoutParams.flags = 1408
        } else {
            layoutParams.flags = 1280
        }

        getNavigationBarHeight(applicationContext)
        windowManager2.defaultDisplay.getRealMetrics(DisplayMetrics())
        layoutParams.width = -1
        layoutParams.height = -1
        layoutParams.format = -3
        val relativeLayout2 = LayoutInflater.from(application)
            .inflate(R.layout.anim_screen_filter, null as ViewGroup?) as RelativeLayout
        layout = relativeLayout2
        val linearLayout = relativeLayout2.findViewById<View>(R.id.layoutBattery) as LinearLayout
        customChargingScreen =
            layout!!.findViewById<View>(R.id.videoView) as LockScreenChargingAnimationView
        val imgPreview = layout!!.findViewById<View>(R.id.imgAnim) as ImageView
        val lottieAnimationView =
            layout!!.findViewById<View>(R.id.animationView) as LottieAnimationView
        val textView = layout!!.findViewById<View>(R.id.txtPercentage) as TextView
        textView.text = getBatteryPercentage(this)
        if (isPer) {
            linearLayout.visibility = View.VISIBLE
        } else {
            linearLayout.visibility = View.GONE
        }
        when (custom) {
            -1 -> {
                lottieAnimationView.setAnimation(mAnimation)
                customChargingScreen!!.visibility = View.GONE
                imgPreview.visibility = View.GONE
            }
            0 -> {
                customChargingScreen!!.visibility = View.VISIBLE
                customChargingScreen!!.setVideoURI(Uri.parse(str))
                customChargingScreen!!.setOnPreparedListener { m: MediaPlayer ->
                    m.setVolume(0f, 0f)
                    customChargingScreen!!.start()
                    if (duration == -1) {
                        m.isLooping = true
                    }
                }
            }
            else -> {
                imgPreview.setImageURI(Uri.parse(str))
            }
        }

        if (isClosing == 1) {
            lottieAnimationView.setOnClickListener {  removeScreenFilter() }
        } else {
            lottieAnimationView.setOnClickListener(DoubleTapListener(this))
        }
        addBroadcastReceiver()
    }

    private fun addBroadcastReceiver() {
        val intentFilter = IntentFilter()
        intentFilter.addAction("android.intent.action.BATTERY_CHANGED")
        intentFilter.addAction("android.intent.action.BATTERY_LOW")
        intentFilter.addAction("android.intent.action.ACTION_POWER_CONNECTED")
        intentFilter.addAction("android.intent.action.ACTION_POWER_DISCONNECTED")
        intentFilter.addAction("android.intent.action.PHONE_STATE")
        intentFilter.addAction("android.intent.action.BOOT_COMPLETED")
        registerReceiver(MainActivity.receiver, intentFilter)
    }

    override fun onStartCommand(intent: Intent, i: Int, i2: Int): Int {
        addBroadcastReceiver()

        if (MainActivity.myNotification != null) {
            startForeground(1, MainActivity.myNotification!!.builder.build())
        }
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(MainActivity.receiver)
    }

    fun getBatteryPercentage(c: Context): String {
        val registerReceiver =
            c.registerReceiver(null, IntentFilter("android.intent.action.BATTERY_CHANGED"))
        return (registerReceiver!!.getIntExtra("level", -1)
            .toFloat() / registerReceiver.getIntExtra("scale", -1).toFloat() * 100.0f).toInt()
            .toString() + " %"
    }

    @SuppressLint("InternalInsetResource")
    fun getNavigationBarHeight(context: Context) {
        if (context.resources.getIdentifier("config_showNavigationBar", "bool", "android") == 0) {
            return
        }
        context.resources.getDimensionPixelSize(
            context.resources.getIdentifier("navigation_bar_height", "dimen", "android")
        )
    }

    override fun onDoubleClick() {
        removeScreenFilter()
    }

    companion object {
        var layout: RelativeLayout? = null
        var wManager: WindowManager? = null
        fun removeScreenFilter() {
            val relativeLayout2 = layout
            if (relativeLayout2 != null && relativeLayout2.parent != null) {
                wManager!!.removeViewImmediate(layout)
            }
        }
    }
}