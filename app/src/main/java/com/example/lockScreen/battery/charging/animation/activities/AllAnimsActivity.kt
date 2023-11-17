package com.example.lockScreen.battery.charging.animation.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.lockScreen.battery.charging.animation.R

import com.example.lockScreen.battery.charging.animation.adapters.AnimationsAdapter
import com.example.lockScreen.battery.charging.animation.interfaces.StartListener
import com.example.lockScreen.battery.charging.animation.model.Animation
import com.example.lockScreen.battery.charging.animation.utils.Preferences.getAnimation
import com.example.lockScreen.battery.charging.animation.utils.Preferences.getUri

class AllAnimsActivity : AppCompatActivity() {

    private val animations = ArrayList<Animation>()
    private var custom = 0
    private var rv: RecyclerView? = null
    private var uri: String? = null
    private var anim = 0

    public override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)

        setContentView(R.layout.activity_all_anims)

        custom = getAnimation(this, -1)
        uri = getUri(this, "uri")

        rv = findViewById<View>(R.id.rcAnimations) as RecyclerView

        getDataAndSetAdapter()
    }

    @SuppressLint("WrongConstant")
    public override fun onActivityResult(i: Int, i2: Int, intent: Intent?) {
        var data: Uri? = null
        super.onActivityResult(i, i2, intent)
        if (i == 176 && i2 == -1 && intent!!.data.also { data = it!! } != null) {
            try {
                contentResolver.takePersistableUriPermission(
                    intent.data!!,
                    intent.flags and Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (e: SecurityException) {
                e.printStackTrace()
            }
            val intent1 = Intent(this, AnimationPreviewActivity::class.java)
            intent1.putExtra("uri", data!!.toString())
//            
            startActivity(intent1)

        }
    }


    private fun getDataAndSetAdapter() {
        animations.clear()
        if (custom != -1) {
            animations.add(Animation(custom, Uri.parse(uri)))
        }
        val data = intent.getIntExtra("position", 0)

        animations.clear()
        when (data) {
            0 -> {
                animations.add(Animation(R.raw.animal1))
                animations.add(Animation(R.raw.animal2))
                animations.add(Animation(R.raw.animal3))
                animations.add(Animation(R.raw.animal4))
                animations.add(Animation(R.raw.animal5))
                animations.add(Animation(R.raw.animal6))
            }

            1 -> {
                animations.add(Animation(R.raw.cartoon1))
                animations.add(Animation(R.raw.cartoon2))
                animations.add(Animation(R.raw.cartoon3))
                animations.add(Animation(R.raw.cartoon4))
                animations.add(Animation(R.raw.cartoon5))
                animations.add(Animation(R.raw.cartoon6))

            }

            2 -> {
                animations.add(Animation(R.raw.nature1))
                animations.add(Animation(R.raw.nature2))
                animations.add(Animation(R.raw.nature3))
                animations.add(Animation(R.raw.nature4))
                animations.add(Animation(R.raw.nature5))
                animations.add(Animation(R.raw.nature6))
            }

            3 -> {
                animations.add(Animation(R.raw.circle1))
                animations.add(Animation(R.raw.circle2))
                animations.add(Animation(R.raw.circle3))
                animations.add(Animation(R.raw.circle4))
                animations.add(Animation(R.raw.circle5))
                animations.add(Animation(R.raw.circle6))
            }
        }

        val adapter = AnimationsAdapter(this, animations, object : StartListener {
            override fun onClick(position: Int) {
                val model = animations[position]
                when (model.an) {
                    -1 -> {
                        anim = model.animation
                        val intent1 =
                            Intent(
                                this@AllAnimsActivity,
                                AnimationPreviewActivity::class.java
                            )
                        intent1.putExtra("anim", anim)
                        startActivity(intent1)
                    }

                    0 -> {
                        val intent2 =
                            Intent(this@AllAnimsActivity, AnimationPreviewActivity::class.java)
                        intent2.putExtra("uri", model.animUri.toString())
                        startActivity(intent2)

                    }

                    else -> {
                        val intent3 =
                            Intent(this@AllAnimsActivity, AnimationPreviewActivity::class.java)
                        intent3.putExtra("animUri", model.animUri.toString())
                        startActivity(intent3)

                    }
                }
            }
        })
        rv!!.layoutManager = GridLayoutManager(this, 2)
        rv!!.adapter = adapter
    }
}