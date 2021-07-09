package com.gabo.finder.common.components

import android.animation.Animator
import android.animation.ObjectAnimator
import android.content.Context
import android.os.Parcelable
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.gabo.finder.R
import com.gabo.finder.databinding.ComponentLongTextBinding
import kotlinx.parcelize.Parcelize

private const val ANIM_MAX_LINES = 50
private const val ANIM_DURATION = 1000L


class LongTextView(
    context : Context,
    attributeSet: AttributeSet
) : ConstraintLayout(context, attributeSet) {

    @Parcelize
    private data class State(
        val isCollapsed : Boolean,
        val maxLines : Int,
        val superState : Parcelable?
    ) : Parcelable

    private val binding = ComponentLongTextBinding.inflate(
        LayoutInflater.from(context),
        this
    )


    private var maxLines  = 4

    var isCollapsed =  true
    set(value) {
        field = value
        update()
    }

    override fun onSaveInstanceState(): Parcelable {
       val superState = super.onSaveInstanceState()
        return State(
            isCollapsed = isCollapsed,
            superState = superState,
            maxLines = maxLines
        )
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
       val savedState = state as? State ?:return
        super.onRestoreInstanceState(savedState.superState)
        isCollapsed = savedState.isCollapsed
        maxLines = savedState.maxLines
        displaySilently()
    }



    var text : String? = null
        set(value) {
            field = value
            binding.text.text = value
        }

    init {
        parseAttributes(attributeSet)
        binding.seeMoreButton.setOnClickListener {
            isCollapsed = !isCollapsed
        }
        binding.text.maxLines = maxLines
        isSaveEnabled = true
    }

    private fun expand(){
        ObjectAnimator.ofInt(
            binding.text,
            "maxLines",
            ANIM_MAX_LINES
        ).apply {
            duration = ANIM_DURATION
            addListener(object: Animator.AnimatorListener{
                override fun onAnimationStart(animation: Animator?) {}
                override fun onAnimationEnd(animation: Animator?) {
                    binding.text.maxLines = Integer.MAX_VALUE
                }
                override fun onAnimationCancel(animation: Animator?) {
                    binding.text.maxLines = Integer.MAX_VALUE
                }
                override fun onAnimationRepeat(animation: Animator?) {}

            })
        }.start()

    }

    private fun collapse(){
       binding.text.maxLines =  ANIM_MAX_LINES
        ObjectAnimator.ofInt(
            binding.text,
            "maxLines",
            maxLines
        ).apply {
            duration = ANIM_DURATION
        }.start()
    }

    fun update(){
        binding.text.text = text
        if(isCollapsed){
            collapse()
            binding.seeMoreButton.text = context
                .resources
                .getString(R.string.see_more)
        } else {
            expand()
            binding.seeMoreButton.text = context
                .resources
                .getText(R.string.see_less)
        }
    }

    private fun displaySilently(){
        binding.text.text = text
        if(isCollapsed) {
            binding.text.maxLines = maxLines
            binding.seeMoreButton.text = context
                .resources
                .getString(R.string.see_more)
        }else {
            binding.text.maxLines = Integer.MAX_VALUE
            binding.seeMoreButton.text = context
                .resources
                .getText(R.string.see_less)
        }
    }

    private fun parseAttributes(attributeSet: AttributeSet) {
        val attrs = context.obtainStyledAttributes(attributeSet, R.styleable.LongTextView)
        maxLines = attrs.getInteger(R.styleable.LongTextView_android_maxLines,4)
        text = attrs.getString(R.styleable.LongTextView_android_text)
        attrs.recycle()
    }

}