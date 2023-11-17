package com.example.lockScreen.battery.charging.animation.activities

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieAnimationView
import com.example.lockScreen.battery.charging.animation.R
import com.example.lockScreen.battery.charging.animation.interfaces.ClicksListener
import com.example.lockScreen.battery.charging.animation.interfaces.DoubleTapListener
import com.example.lockScreen.battery.charging.animation.utils.Preferences.getAnimation
import com.example.lockScreen.battery.charging.animation.utils.Preferences.getAnimationId
import com.example.lockScreen.battery.charging.animation.utils.Preferences.getClosing
import com.example.lockScreen.battery.charging.animation.utils.Preferences.getPerVal
import com.example.lockScreen.battery.charging.animation.utils.Preferences.getUri
import com.example.lockScreen.battery.charging.animation.utils.Utils

class LockScreenActivity : AppCompatActivity(), ClicksListener {

    private var anim = 0
    private var closing = 0
    private var custom = 0
    private var duration = -1
    private var imgClick: ImageView? = null
    private var imgPreview: ImageView? = null
    private var isPer = false
    private var layoutBattery: LinearLayout? = null
    private var layoutVideo: RelativeLayout? = null
    private var lottie: LottieAnimationView? = null
    private var uri: String? = null
    private var videoView: VideoView? = null

    @SuppressLint("SetTextI18n")
    private fun loadAnim() {
        if (anim == R.raw.anim23) {
            lottie!!.setPadding(0, 0, 0, 0)
            lottie!!.scaleType = ImageView.ScaleType.CENTER_CROP
        }
        if (isPer) {
            layoutBattery!!.visibility = View.VISIBLE
        } else {
            layoutBattery!!.visibility = View.GONE
        }
        batteryPercent!!.text = Utils.getBatteryPercentage(this).toString() + " %"
        when (custom) {
            -1 -> {
                lottie!!.setAnimation(anim)
                layoutVideo!!.visibility = View.GONE
                imgPreview!!.visibility = View.GONE
            }

            0 -> {
                layoutVideo!!.visibility = View.VISIBLE
                videoView!!.setVideoURI(Uri.parse(uri))
                videoView!!.setOnPreparedListener { mp: MediaPlayer ->
                    videoView!!.start()
                    if (duration == -1) {
                        mp.isLooping = true
                    }
                }
            }

            else -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    val bitmap: Bitmap = AnimationPreviewActivity.imageRotationUtil(
                        Uri.parse(uri),
                        Utils.convertUriToBitmap(this, Uri.parse(uri)), this
                    )!!
                    imgPreview!!.setImageBitmap(bitmap)
                }
            }
        }
        if (closing == 1) {
            lottie!!.setOnClickListener { finish() }
        } else {
            lottie!!.setOnClickListener(DoubleTapListener(this))
        }
    }

    public override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)

        window.statusBarColor = resources.getColor(R.color.purple_700)
        window.decorView.systemUiVisibility = 5890
        window.addFlags(2621440)
        setContentView(R.layout.activity_lock_screen)

        anim = getAnimationId(this, R.raw.anim3)
        isPer = getPerVal(this)
        closing = getClosing(this)
        custom = getAnimation(this, -1)
        uri = getUri(this, "uri")

        videoView = findViewById<View>(R.id.videoView) as VideoView
        imgPreview = findViewById<View>(R.id.imgAnim) as ImageView
        lottie = findViewById<View>(R.id.animationView) as LottieAnimationView
        batteryPercent = findViewById<View>(R.id.txtPercentage) as TextView
        layoutBattery = findViewById<View>(R.id.layoutBattery) as LinearLayout
        imgClick = findViewById<View>(R.id.imgClick) as ImageView
        layoutVideo = findViewById<View>(R.id.layoutVideo) as RelativeLayout

        loadAnim()

        registerReceiver(object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                finish()
            }
        }, IntentFilter().apply { addAction("android.intent.action.ACTION_POWER_DISCONNECTED") })
    }

    override fun onDoubleClick() {
        finish()
    }

    override fun onSingleClick() {
        imgClick!!.visibility = View.VISIBLE
        Handler().postDelayed({ imgClick!!.visibility = View.GONE }, 1000)
    }

    companion object {
        var batteryPercent: TextView? = null
    }
}