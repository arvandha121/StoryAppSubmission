package com.dicoding.storyappsubmission.ui.custome

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.Gravity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import com.dicoding.storyappsubmission.R

class ButtonRegister  : AppCompatButton {
    private lateinit var enable: Drawable
    private lateinit var disable: Drawable
    private var textColors: Int = 0

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        background = if (isEnabled) {
            enable
        } else {
            disable
        }

        setTextColor(textColors)
        gravity = Gravity.CENTER
        text = if (isEnabled) {
            resources.getString(R.string.register)
        } else {
            resources.getString(R.string.fill_in)
        }
    }

    private fun init() {
        textColors = ContextCompat.getColor(context, android.R.color.white)
        enable = ContextCompat.getDrawable(context, R.drawable.button) as Drawable
        disable = ContextCompat.getDrawable(context, R.drawable.button_disable) as Drawable
    }
}