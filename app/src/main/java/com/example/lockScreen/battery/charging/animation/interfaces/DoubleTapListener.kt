package com.example.lockScreen.battery.charging.animation.interfaces

import android.content.Context
import android.view.View

class DoubleTapListener(context: Context) : View.OnClickListener {
    private val listener: ClicksListener
    private var counter = 0
    private var isRunning = false
    private val resetInTime = 500

    init {
        listener = context as ClicksListener
    }

    override fun onClick(view: View) {
        listener.onSingleClick()
        if (isRunning && counter == 1) {
            listener.onDoubleClick()
        }
        counter++
        if (!isRunning) {
            isRunning = true
            Thread {
                try {
                    Thread.sleep(resetInTime.toLong())
                    isRunning = false
                    counter = 0
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }.start()
        }
    }
}