package com.gabo.architecture.utils

import java.text.NumberFormat
import java.util.*

/**
 * Format prices according to user locale and the item currency.
 */
fun Double.formatCurrency(currencyId : String) :  String {
    val locale = Locale.getDefault()
    val symbol = Currency
        .getInstance(currencyId)
        .symbol

    val amount =  NumberFormat
        .getNumberInstance(locale)
        .apply {
            maximumFractionDigits = 2
        }.format(this)

    return "$symbol $amount"
}

/**
 *  Format max item quantities for displaying proposes.
 */
fun Int.formatQuantity(max : Int = 5000) : String {
    val locale = Locale.getDefault()
    val exceeds = this > max
    val qty = if(exceeds) max else this
    val num =  NumberFormat
        .getNumberInstance(locale)
        .apply {
            maximumFractionDigits = 2
        }.format(qty)
    return if(exceeds){
        "+$num"
    }else {
        num
    }
}
