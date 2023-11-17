package com.example.lockScreen.battery.charging.animation.activities

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.lockScreen.battery.charging.animation.R
import com.example.lockScreen.battery.charging.animation.utils.Preferences
import com.example.lockScreen.battery.charging.animation.utils.Preferences.getFirstTimeLaunch
import com.google.android.material.dialog.MaterialAlertDialogBuilder

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private lateinit var startImg: ImageView

    public override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        supportRequestWindowFeature(1)
        window.setFlags(1024, 1024)
        val decorView = window.decorView
        var uiVisibility = decorView.systemUiVisibility
        uiVisibility = uiVisibility or View.SYSTEM_UI_FLAG_LOW_PROFILE
        uiVisibility = uiVisibility or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        uiVisibility = uiVisibility or View.SYSTEM_UI_FLAG_IMMERSIVE
        uiVisibility = uiVisibility or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        decorView.systemUiVisibility = uiVisibility

        setContentView(R.layout.activity_splash);

        startImg = findViewById(R.id.startImg)

        startImg.setOnClickListener {

            if (intent.getStringExtra("isLock") != null) {
                startActivity(Intent(applicationContext, LockScreenActivity::class.java))
                finish()
            }
            if (getFirstTimeLaunch(this, true)) {
                sdk23()
            } else {
                startActivity(Intent(applicationContext, MainActivity::class.java))
                finish()
            }
        }
    }

    private fun sdk23() {
        if (!Settings.canDrawOverlays(this@SplashActivity)) {
            overlayPermissionPopup()
        }
    }

    private fun overlayPermissionPopup() {
        MaterialAlertDialogBuilder(this).setTitle("Permission Required").setCancelable(false)
            .setMessage(
                resources.getString(R.string.overlay)
            ).setPositiveButton("OK") { _: DialogInterface?, _: Int ->
                startActivityForResult(
                    Intent(
                        "android.settings.action.MANAGE_OVERLAY_PERMISSION",
                        Uri.parse("package:$packageName")
                    ),
                    1
                )
            }
            .setNegativeButton("Cancel") { dialogInterface: DialogInterface, _: Int ->
                dialogInterface.dismiss()
            }.show()
    }

    public override fun onActivityResult(i: Int, i2: Int, intent: Intent?) {
        super.onActivityResult(i, i2, intent)
        startActivity(Intent(this@SplashActivity, MainActivity::class.java))
        Preferences.setFirstLaunch(this@SplashActivity, false)
        finish()
    }
}