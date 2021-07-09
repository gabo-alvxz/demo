package com.gabo.architecture.ui

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.annotation.IntegerRes
import androidx.annotation.StyleRes
import timber.log.Timber

class DynamicRadioGroup(
    context: Context,
    attributeSet: AttributeSet
) : RadioGroup(context, attributeSet) {
    private var groupPadding: Int = 0
    @StyleRes
    private var groupStyle: Int? = null

    private val options  = mutableSetOf<Any>()

    fun <T :  Any> setOptions(
        options : List<T>,
        textProducer :(T) -> String,
        onOptionSelected : (T) -> Unit,
        initialSelection : ((T) -> Boolean)? = null
    ){
        this.options.clear()
        this.removeAllViews()
        options.forEach { option ->
            this.options.add(option)
            val text = textProducer(option)
            val isChecked = initialSelection?.invoke(option) ?: false
            addView(
                createView(text, isChecked)
            )
        }
        setOnSelectionListener(onOptionSelected)
    }

    fun <T : Any> getSelected() : T? {
        val idx = checkedRadioButtonId
        return options.elementAtOrNull(idx) as? T?
    }


    private fun <T : Any> setOnSelectionListener(callback : (T) -> Unit){
        setOnCheckedChangeListener(OnCheckedChangeListener {_ , checkedId ->
            val selected = options.elementAt(checkedId) as T
            callback(selected)
        })
    }


    private fun createView(text : String, isSelected : Boolean) : RadioButton{
        val idx = options.size - 1
        val radioButton = RadioButton(context)
        radioButton.text = text
        radioButton.id = idx
        radioButton.setPadding(
            groupPadding,
            radioButton.paddingTop,
            radioButton.paddingRight,
            radioButton.paddingBottom
        )
        groupStyle?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                radioButton.setTextAppearance(it)
            } else {
                radioButton.setTextAppearance(context,it)
            }
        }
        radioButton.isChecked = isSelected
        return radioButton
    }


}