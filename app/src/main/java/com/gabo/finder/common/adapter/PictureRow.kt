package com.gabo.finder.common.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.gabo.architecture.utils.loadUrl
import com.gabo.finder.databinding.ImageRowBinding
import com.gabo.models.models.image.Picture
import com.squareup.picasso.Picasso

class PictureRow(
    val picture : Picture,
) : Row<ImageRowBinding> {
    override fun createBinding(
        layoutInflater: LayoutInflater,
        viewGroup: ViewGroup
    ): ImageRowBinding {
        return ImageRowBinding.inflate(
            layoutInflater,
            viewGroup,
            false
        )
    }

    override fun bind(binding: ImageRowBinding) {
        /**
         *  If t
         */
        if(picture.ratio >= 1) {
            Picasso.get()
                .load(picture.url)
                .fit()
                .centerCrop()
                .into(binding.image)
        }else {
            binding.image.loadUrl(picture.url)
        }
    }
}