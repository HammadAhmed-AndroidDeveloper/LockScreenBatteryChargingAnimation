package com.example.lockScreen.battery.charging.animation.model

import android.net.Uri

class Animation {
    var an: Int
        private set
    var animation = 0
        private set
    var animUri: Uri? = null
        private set

    constructor(i: Int, animUri: Uri?) {
        an = -1
        an = i
        this.animUri = animUri
    }

    constructor(animation: Int) {
        an = -1
        this.animation = animation
    }
}