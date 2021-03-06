package com.billkainkoom.ogya.extentions

import android.content.Context
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView


fun ImageView.greylize() {
    val matrix = ColorMatrix()
    matrix.setSaturation(0f)
    val filter = ColorMatrixColorFilter(matrix)
    this.colorFilter = filter
}

fun ImageView.fadalize() {
    val matrix = ColorMatrix()
    matrix.setSaturation(0.2f)
    val filter = ColorMatrixColorFilter(matrix)
    this.colorFilter = filter
}

fun ImageView.colorize() {
    val matrix = ColorMatrix()
    matrix.setSaturation(1f)
    val filter = ColorMatrixColorFilter(matrix)
    this.colorFilter = filter
}

fun ImageView.tealize() {
    val matrix = ColorMatrix()
    matrix.setSaturation(1f)
    val filter = ColorMatrixColorFilter(matrix)
    this.colorFilter = filter
}


fun EditText.watch(afterTextChanged: (text: String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            afterTextChanged(this@watch.text.toString())
        }

        override fun afterTextChanged(s: Editable) {

        }
    })
}

fun EditText.demandFocus(context: Context) {
    try {
        this.requestFocus()
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun EditText.enableEditing(enable: Boolean) {
    this.isFocusable = enable
    this.isFocusableInTouchMode = enable
    this.isClickable = enable
}