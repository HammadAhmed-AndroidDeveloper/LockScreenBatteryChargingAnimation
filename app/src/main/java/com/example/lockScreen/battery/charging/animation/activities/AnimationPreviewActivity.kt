package com.example.lockScreen.battery.charging.animation.activities

import android.animation.Animator
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.exifinterface.media.ExifInterface
import com.example.lockScreen.battery.charging.animation.R
import com.airbnb.lottie.LottieAnimationView
import com.example.lockScreen.battery.charging.animation.utils.Preferences.getAnimationId
import com.example.lockScreen.battery.charging.animation.utils.Preferences.getClosing
import com.example.lockScreen.battery.charging.animation.utils.Preferences.getIsShowing
import com.example.lockScreen.battery.charging.animation.utils.Preferences.getLock
import com.example.lockScreen.battery.charging.animation.utils.Preferences.getPerVal
import com.example.lockScreen.battery.charging.animation.utils.Preferences.getUri
import com.example.lockScreen.battery.charging.animation.utils.Preferences.setAnimation
import com.example.lockScreen.battery.charging.animation.utils.Preferences.setAnimationId
import com.example.lockScreen.battery.charging.animation.utils.Preferences.setClosing
import com.example.lockScreen.battery.charging.animation.utils.Preferences.setIsShowing
import com.example.lockScreen.battery.charging.animation.utils.Preferences.setLock
import com.example.lockScreen.battery.charging.animation.utils.Preferences.setPerVal
import com.example.lockScreen.battery.charging.animation.utils.Preferences.setUri
import com.example.lockScreen.battery.charging.animation.utils.Utils
import java.io.IOException

class AnimationPreviewActivity : AppCompatActivity() {

    private var anim = 0
    private var closingTime = 0
    private var previewImage: AppCompatButton? = null
    private var imageUri: Uri? = null
    private var isLocked = false
    private var percentageValue = false
    private var layoutBatteryLayout: LinearLayout? = null
    private var mainDialog: Dialog? = null
    private var raw = 0
    private var isShowing = false
    private var applyText: AppCompatButton? = null
    private var uri: Uri? = null
    private var str: String? = null
    private var mPlayer: MediaPlayer? = null
    private var videoView: VideoView? = null
    private var percentText: TextView? = null
    private var animationView: LottieAnimationView? = null
    private var imgAnim: ImageView? = null
    private var layoutVideo: RelativeLayout? = null
    private var shouldShowAd = false

    @SuppressLint("SetTextI18n")
    public override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)

        shouldShowAd = intent.getBooleanExtra("shouldShowAd", false)
        setContentView(R.layout.activity_animation_preview)


        loadPreferences()

        animationView = findViewById(R.id.animationView)
        videoView = findViewById(R.id.videoView)
        applyText = findViewById(R.id.txtConfirm)



        imgAnim = findViewById(R.id.imgAnim)
        percentText = findViewById(R.id.txtPercentage)
        previewImage = findViewById(R.id.imgPreview)
        layoutBatteryLayout = findViewById(R.id.layoutBattery)
        layoutVideo = findViewById(R.id.layoutVideo)

        percentText!!.text = Utils.getBatteryPercentage(this).toString() + "%"
        raw = intent.getIntExtra("anim", 0)

        animationView!!.setOnClickListener {
            previewImage!!.visibility = View.VISIBLE
            layoutBatteryLayout!!.visibility = View.GONE
            applyText!!.visibility = View.VISIBLE
        }


        previewImage!!.setOnClickListener {
            previewImage!!.visibility = View.GONE
            applyText!!.visibility = View.GONE
            layoutBatteryLayout!!.visibility = View.VISIBLE
            loadPreferences()
            if (percentageValue) {
                layoutBatteryLayout!!.visibility = View.VISIBLE
            } else {
                layoutBatteryLayout!!.visibility = View.GONE
            }
        }

        checkIntentsAndNulls()



        applyText!!.setOnClickListener { animateIt() }
    }

    @SuppressLint("SetTextI18n")
    private fun checkIntentsAndNulls() {
        if (intent.getStringExtra("uri") != null) {
            uri = Uri.parse(intent.getStringExtra("uri"))
        }
        if (intent.getStringExtra("animUri") != null) {
            imageUri = Uri.parse(intent.getStringExtra("animUri"))
        }
        if (raw == anim) {
            applyText!!.text = "Applied"
        }
        if (uri != null && uri.toString() == str) {
            applyText!!.text = "Applied"
        }
        val uri2 = imageUri
        if (uri2 != null && uri2.toString() == str) {
            applyText!!.text = "Applied"
        }

        if (uri != null) {
            layoutVideo!!.visibility = View.VISIBLE
            setVideoPlayer()
        } else if (raw != 0) {

            layoutVideo!!.visibility = View.GONE
            imgAnim!!.visibility = View.GONE
            animationView!!.setAnimation(raw)
            animationView!!.addAnimatorListener(object : Animator.AnimatorListener {
                override fun onAnimationCancel(animator: Animator) {}
                override fun onAnimationEnd(animator: Animator) {}
                override fun onAnimationRepeat(animator: Animator) {}
                override fun onAnimationStart(animator: Animator) {}
            })
            setMediaPlayer()
        } else {
            layoutVideo!!.visibility = View.GONE
            imgAnim!!.visibility = View.VISIBLE
            var bitmap: Bitmap? = null
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    bitmap =
                        imageRotationUtil(imageUri, Utils.convertUriToBitmap(this, imageUri), this)
                    imgAnim!!.setImageBitmap(bitmap)
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
            imgAnim!!.setImageBitmap(bitmap)
        }
    }


    private fun animateIt() {
        setAnimation(this, -1)
        setAnimationId(this, raw)
        if (uri != null) {
            setAnimation(this, 0)
            setUri(this, uri.toString())
            setAnimationId(this, -1)
            loadPreferences()
        }
        if (imageUri != null) {
            setAnimation(this, 1)
            setUri(this, imageUri.toString())
            loadPreferences()
        }
        Toast.makeText(this@AnimationPreviewActivity, "Applied Successfully", Toast.LENGTH_SHORT)
            .show()
        finish()
    }

    private fun setVideoPlayer() {
        if (uri != null) {
            videoView!!.setVideoURI(uri)
            videoView!!.setOnPreparedListener { mp: MediaPlayer ->
                videoView!!.start()
                loadPreferences()
                mp.isLooping = true
                mPlayer = mp
            }
        }
    }

    private fun setMediaPlayer() {
        loadPreferences()
        mPlayer?.setVolume(0.0f, 0.0f)
    }

    override fun onBackPressed() {
        if (previewImage!!.visibility == View.GONE) {
            previewImage!!.visibility = View.VISIBLE
            layoutBatteryLayout!!.visibility = View.GONE
            applyText!!.visibility = View.VISIBLE
            return
        }
        super.onBackPressed()
    }

    private fun loadPreferences() {
        anim = getAnimationId(this, R.raw.anim3)
        isLocked = getLock(this)
        percentageValue = getPerVal(this)
        isShowing = getIsShowing(this)
        closingTime = getClosing(this)
        str = getUri(this, "uri")
    }

    @SuppressLint("ResourceType")
    private fun showSettingPopup(isIt: Boolean) {
        loadPreferences()
        val inflate = layoutInflater.inflate(R.layout.anim_settings_layout, null)
        val imgDismiss = inflate.findViewById<ImageView>(R.id.imgBack)
        val toggleButton = inflate.findViewById<ToggleButton>(R.id.switchLock)
        val toggleButton3 = inflate.findViewById<ToggleButton>(R.id.switchPer)
        val spinner2 = inflate.findViewById<Spinner>(R.id.spinnerClosingMethod)
        val toggleButton4 = inflate.findViewById<ToggleButton>(R.id.switchShow)
        mainDialog = Dialog(this)
        val clicks: ArrayList<String> = ArrayList()
        clicks.add("Double click")
        clicks.add("Single click")

        spinner2.adapter = ArrayAdapter(this, R.layout.anim_spinner_item, clicks)

        toggleButton.isChecked = isLocked
        toggleButton3.isChecked = this.percentageValue
        toggleButton4.isChecked = isShowing
        spinner2.setSelection(closingTime)

        toggleButton.setOnCheckedChangeListener { _: CompoundButton?, b: Boolean ->
            setLock(
                this@AnimationPreviewActivity, b
            )
        }
        toggleButton3.setOnCheckedChangeListener { _: CompoundButton?, b: Boolean ->
            setPerVal(
                this@AnimationPreviewActivity, b
            )
        }
        toggleButton4.setOnCheckedChangeListener { _: CompoundButton?, b: Boolean ->
            setIsShowing(
                this@AnimationPreviewActivity, b
            )
        }
        spinner2.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View, i: Int, j: Long) {
                setClosing(this@AnimationPreviewActivity, i)
            }
        }
        mainDialog!!.requestWindowFeature(1)
        imgDismiss.setOnClickListener { mainDialog!!.dismiss() }
        mainDialog!!.window!!.setBackgroundDrawableResource(17170445)
        mainDialog!!.setContentView(
            inflate, LinearLayout.LayoutParams(
                resources.displayMetrics.widthPixels, resources.displayMetrics.heightPixels
            )
        )
        mainDialog!!.setOnDismissListener { setMediaPlayer() }
        mainDialog!!.show()
    }


    companion object {
        fun imageRotationUtil(uri: Uri?, bitmap: Bitmap?, context: Context): Bitmap? {
            val attr = ExifInterface(
                context.contentResolver.openInputStream(
                    uri!!
                )!!
            ).getAttributeInt(ExifInterface.TAG_ORIENTATION, 1)
            if (attr == 3) {
                return rotateCamera(180, bitmap)
            }
            if (attr == 6) {
                return rotateCamera(90, bitmap)
            }
            return if (attr != 8) {
                bitmap
            } else rotateCamera(270, bitmap)
        }

        private fun rotateCamera(i: Int, b: Bitmap?): Bitmap {
            val matrix = Matrix()
            matrix.postRotate(i.toFloat())
            val bmp = Bitmap.createBitmap(b!!, 0, 0, b.width, b.height, matrix, true)
            b.recycle()
            return bmp
        }
    }
}