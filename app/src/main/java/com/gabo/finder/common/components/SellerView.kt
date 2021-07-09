package com.gabo.finder.common.components

import android.app.Activity
import android.content.Context
import android.os.Parcelable
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.gabo.architecture.utils.IntentUtils
import com.gabo.architecture.utils.fromContextWrapper
import com.gabo.finder.databinding.ComponentSellerViewBinding
import com.gabo.models.models.seller.User
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.parcelize.Parcelize
import javax.inject.Inject

@AndroidEntryPoint
class SellerView  constructor(
    context: Context,
    attributeSet: AttributeSet
) : ConstraintLayout(context, attributeSet) {

    private val binding = ComponentSellerViewBinding.inflate(
        LayoutInflater.from(context),
        this
    )

    @Parcelize
    private data class State(
        val seller: User?,
        val parentState: Parcelable?
    ) : Parcelable

    @Inject
   lateinit var intentUtils: IntentUtils

    var seller: User? = null
        set(value) {
            field = value
            display()
        }



    init {
        isSaveEnabled = true
        display()
    }

    override fun onSaveInstanceState(): Parcelable? {
        return State(
            seller = seller,
            parentState = super.onSaveInstanceState()
        )
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state !is State) return
        super.onRestoreInstanceState(state.parentState)
        seller = state.seller
    }

    private fun display() {
        binding.root.isVisible = seller != null
        val seller = seller ?: return
        binding.sellerName.text = seller.displayName
        binding.sellerReputationText.isVisible = seller.reputation.level != null &&
                seller.reputation.status != null

        seller.reputation.level?.let {
            binding.sellerReputationText.setTextColor(
                ContextCompat.getColor(context, it.color)
            )
        }

        seller.reputation.status?.let {
            binding.sellerReputationText.text = context.getString(it.stringRes)
        }
        binding.pageLink.setOnClickListener {
            val activity : Activity = context.fromContextWrapper() ?: return@setOnClickListener
            intentUtils.openInBrowser(seller.url, activity)
        }
    }

}