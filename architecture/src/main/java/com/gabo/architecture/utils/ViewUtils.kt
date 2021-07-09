package com.gabo.architecture.utils

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import androidx.viewbinding.ViewBinding
import com.squareup.picasso.Picasso

fun View.hideKeyboard(){
    val imm = this
        .context
        .getSystemService(Activity.INPUT_METHOD_SERVICE) as? InputMethodManager ?: return
    imm.hideSoftInputFromWindow(this.windowToken, 0)
}

fun ImageView.loadUrl(url : String) {
    Picasso.get()
        .load(url)
        .into(this)
}

val ViewBinding.context : Context
    get() = this.root.context