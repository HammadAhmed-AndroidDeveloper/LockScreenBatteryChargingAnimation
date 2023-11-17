package com.example.lockScreen.battery.charging.animation.view

import android.content.Context
import android.util.AttributeSet
import android.widget.VideoView

class LockScreenChargingAnimationView : VideoView {
    constructor(context: Context?) : super(context) {}
    constructor(context: Context?, attributeSet: AttributeSet?) : super(context, attributeSet) {}
    constructor(context: Context?, attributeSet: AttributeSet?, i: Int) : super(
        context,
        attributeSet,
        i
    ) {
    }

    public override fun onMeasure(i: Int, i2: Int) {
        super.onMeasure(i, i2)
        setMeasuredDimension(i, i2)
    }
}