package com.example.lockScreen.battery.charging.animation.activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.KeyguardManager
import android.content.*
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.os.SystemClock
import android.provider.Settings
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.lockScreen.battery.charging.animation.R
import com.example.lockScreen.battery.charging.animation.service.LockScreenBatteryAnimationService
import com.example.lockScreen.battery.charging.animation.service.LockScreenNotification
import com.example.lockScreen.battery.charging.animation.utils.Preferences
import com.example.lockScreen.battery.charging.animation.utils.Utils

class MainActivity : AppCompatActivity() {

    private var cartoons: ImageView? = null
    private var animals: ImageView? = null
    private var nature: ImageView? = null
    private var circles: ImageView? = null
    private lateinit var pm: PowerManager
    private var myIntent: Intent? = null
    private var lastActiveTime: Long = 0
    private var isDeviceLocked = false

    public override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)

        setContentView(R.layout.anim_activity_main)

        pm = getSystemService(POWER_SERVICE) as PowerManager
        android13NotificationChecker()
        overlayPermissionChecker()


        cartoons = findViewById<View>(R.id.cartoons) as ImageView
        nature = findViewById<View>(R.id.nature) as ImageView
        circles = findViewById<View>(R.id.circles) as ImageView
        animals = findViewById<View>(R.id.animals) as ImageView

        animals!!.setOnClickListener {
           sendData(0)
        }

        cartoons!!.setOnClickListener {
            sendData(1)
        }


        nature!!.setOnClickListener {
            sendData(2)
        }


        circles!!.setOnClickListener {
            sendData(3)
        }

        powerConnectionReceiver()
        notificationServiceHandler()
    }

    private fun sendData(i: Int) {
        val intent = Intent(this@MainActivity, AllAnimsActivity::class.java)
        intent.putExtra("position", i)
        startActivity(intent)
    }

    private fun notificationServiceHandler() {
        if (Build.VERSION.SDK_INT < 23) {
            val lsn1 = LockScreenNotification(this)
            myNotification = lsn1
            handleService()
        } else if (Settings.canDrawOverlays(this) && pm.isIgnoringBatteryOptimizations(
                packageName
            )
        ) {
            val lsn = LockScreenNotification(this)
            myNotification = lsn
            handleService()
        }
    }

    private fun overlayPermissionChecker() {
        if (!Settings.canDrawOverlays(this)) {
            runOnUiThread { Utils.hasTakenOverlayPermission(this@MainActivity) }
        } else if (!pm.isIgnoringBatteryOptimizations(packageName)) {
            Utils.batteryOptimizationDialog(this@MainActivity)
        }
    }

    private fun android13NotificationChecker() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 22
                )
            }
        }
    }

    public override fun onActivityResult(x: Int, y: Int, intent: Intent?) {
        super.onActivityResult(x, y, intent)
        var data: Uri? = null
        if (x != 1 || !Settings.canDrawOverlays(this)) {
            if (x != 123 || !Settings.canDrawOverlays(this)) {
                finish()
            } else if (pm.isIgnoringBatteryOptimizations(getPackageName())) {
                val lsn = LockScreenNotification(this)
                myNotification = lsn
                handleService()
            }
        } else if (!pm.isIgnoringBatteryOptimizations(getPackageName())) {
            Utils.batteryOptimizationDialog(this@MainActivity)
        }
        if (x == 682 && y == -1 && intent!!.data.also { data = it!! } != null) {
            try {
                contentResolver.takePersistableUriPermission(
                    intent.data!!, Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (e: SecurityException) {
                e.printStackTrace()
            }
            Intent(this, AnimationPreviewActivity::class.java).apply {
                putExtra("uri", data.toString())
                startActivity(this)
            }

        }
    }

    private fun handleService() {
        myIntent = Intent(this, LockScreenBatteryAnimationService::class.java)
        if (!Utils.isMyServiceRunning(this@MainActivity)) {
            if (Build.VERSION.SDK_INT >= 26) {
                ContextCompat.startForegroundService(this, myIntent!!)
            } else {
                startService(myIntent)
            }
        }
    }

    companion object {
        var myNotification: LockScreenNotification? = null
        var receiver: BroadcastReceiver? = null
    }



    private fun powerConnectionReceiver() {
        receiver = object : BroadcastReceiver() {
            @SuppressLint("SetTextI18n")
            override fun onReceive(context: Context, intent: Intent) {
                if (intent.action == "android.intent.action.ACTION_POWER_CONNECTED") {
                    if (SystemClock.elapsedRealtime() - lastActiveTime >= 2000) {
                        lastActiveTime = SystemClock.elapsedRealtime()
                        isDeviceLocked = Preferences.getLock(context)
                        LockScreenBatteryAnimationService.removeScreenFilter()
                        if (!isDeviceLocked) {
                            Intent(
                                this@MainActivity, LockScreenActivity::class.java
                            ).apply {
                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                startActivity(this)
                            }

                        } else if ((context.getSystemService(KEYGUARD_SERVICE) as KeyguardManager).inKeyguardRestrictedInputMode()) {
                            Intent(
                                this@MainActivity, LockScreenActivity::class.java
                            ).apply {
                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                startActivity(this)
                            }
                        }
                    }
                } else if (intent.action == "android.intent.action.ACTION_POWER_DISCONNECTED") {
                    LockScreenBatteryAnimationService.removeScreenFilter()
                } else if (intent.action == "android.intent.action.BATTERY_CHANGED") {
                    val intExtra = (intent.getIntExtra(
                        "level", -1
                    ) * 100).toFloat() / intent.getIntExtra("scale", -1).toFloat()
                    if (LockScreenActivity.batteryPercent != null) {
                        val textView = LockScreenActivity.batteryPercent
                        textView!!.text = intExtra.toInt().toString() + "%"
                    }
                }
            }
        }

        addFilter(receiver!!)
    }

    private fun addFilter(receiver: BroadcastReceiver) {
        val filter = IntentFilter()
        filter.addAction("android.intent.action.BATTERY_CHANGED")
        filter.addAction("android.intent.action.BATTERY_LOW")
        filter.addAction("android.intent.action.PHONE_STATE")
        filter.addAction("android.intent.action.BOOT_COMPLETED")
        registerReceiver(receiver, filter)
    }

}