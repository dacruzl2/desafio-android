package com.picpay.desafio.android.shared_utilities

import android.widget.ImageView
import com.bumptech.glide.Glide

fun ImageView.loadUrl(imageUrl: String, isRoundView: Boolean = false) {
    Glide.with(this)
        .load(imageUrl).also { if (isRoundView) it.circleCrop() }
        .placeholder(R.drawable.ic_round_account_circle)
        .into(this)
}