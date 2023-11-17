package com.example.lockScreen.battery.charging.animation.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.example.lockScreen.battery.charging.animation.R
import com.example.lockScreen.battery.charging.animation.activities.AnimationPreviewActivity
import com.example.lockScreen.battery.charging.animation.interfaces.StartListener
import com.example.lockScreen.battery.charging.animation.model.Animation
import com.example.lockScreen.battery.charging.animation.utils.Preferences.getAnimationId

class AnimationsAdapter(
    private val context: Context,
    private val animations: ArrayList<Animation>,
    var listener: StartListener
) : RecyclerView.Adapter<AnimationsAdapter.AnimationsViewHolder>() {
    private var checked = -1
    override fun onCreateViewHolder(viewGroup: ViewGroup, position: Int): AnimationsViewHolder {
        return AnimationsViewHolder(
            LayoutInflater.from(context).inflate(R.layout.anim_animation_row, viewGroup, false)
        )
    }

    override fun onBindViewHolder(
        holder: AnimationsViewHolder,
        @SuppressLint("RecyclerView") position: Int
    ) {
        val model = animations[position]
        val raw = getAnimationId(context, R.raw.anim3)
        if (animations[position].animation == raw) {
            checked = position
        }
        if (model.an != -1) {
            checked = 0
        }
        if (checked == position) {
            holder.imgChecked.visibility = View.VISIBLE
        } else {
            holder.imgChecked.visibility = View.GONE
        }
        if (model.an == -1 || model.animUri == null) {
            if (position == animations.size - 1) {
                holder.gifImageView.setPadding(0, 0, 0, 0)
                holder.gifImageView.scaleType = ImageView.ScaleType.CENTER_CROP
            }
            holder.gifImageView.visibility = View.GONE
            holder.imgAnimation.visibility = View.GONE
            holder.animationView.setAnimation(model.animation)
            holder.animationView.setOnClickListener { view: View? -> listener.onClick(position) }
            return
        }
        holder.imgAnimation.visibility = View.VISIBLE
        holder.animationView.visibility = View.GONE
        holder.gifImageView.visibility = View.GONE
        Glide.with(context).load(model.animUri).into(holder.imgAnimation)
        holder.imgAnimation.setOnClickListener { view: View? ->
            if (model.an == -1) {
                val intent = Intent(context, AnimationPreviewActivity::class.java)
                intent.putExtra("anim", model.animation)
                context.startActivity(intent)
            } else if (model.an == 0) {
                val intent2 = Intent(context, AnimationPreviewActivity::class.java)
                intent2.putExtra("uri", model.animUri.toString())
                context.startActivity(intent2)
            } else {
                val intent3 = Intent(context, AnimationPreviewActivity::class.java)
                intent3.putExtra("img_uri", model.animUri.toString())
                context.startActivity(intent3)
            }
        }
    }

    override fun getItemCount(): Int {
        return animations.size
    }

    class AnimationsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val animationView: LottieAnimationView
        val gifImageView: ImageView
        val imgAnimation: ImageView
        val imgChecked: ImageView

        init {
            imgAnimation = view.findViewById(R.id.imgAnimation)
            imgChecked = view.findViewById(R.id.imgDone)
            animationView = view.findViewById(R.id.animationView)
            gifImageView = view.findViewById(R.id.gifView)
        }
    }
}