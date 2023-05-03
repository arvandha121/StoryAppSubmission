package com.dicoding.storyappsubmission.ui.custome

import android.content.Context
import android.graphics.Canvas
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.AppCompatEditText
import okhttp3.internal.notify

class EditTextPassword : AppCompatEditText, View.OnTouchListener {

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

    private val passwordRegex: Regex = Regex("^(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9]).{8,}\$")

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        textAlignment = View.TEXT_ALIGNMENT_VIEW_START
        maxLines = 1
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean = false

    private fun init() {
        setOnTouchListener(this)

        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                s?.isEmpty()
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!s.isNullOrEmpty() && !passwordRegex.matches(s.toString())) {
                    error = "Password format minimum 8 characters, 1 capital, 1 lowercase, and 1 number"
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })
    }
}