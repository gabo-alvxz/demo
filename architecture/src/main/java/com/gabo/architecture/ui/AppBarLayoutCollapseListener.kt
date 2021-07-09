package com.gabo.architecture.ui

import com.google.android.material.appbar.AppBarLayout
import kotlin.math.abs

/***
 *  We'll use this class to hide / show the menus and everything wee need when the appbar
 *  gets collapsed.
 */
class AppBarLayoutCollapseListener(
    val onCollapseChanged : (Boolean) -> Unit
) : AppBarLayout.OnOffsetChangedListener{
    private var isCollapsed = false

    override fun onOffsetChanged(appBarLayout: AppBarLayout, verticalOffset: Int) {
        when {
          verticalOffset == 0 -> updateCollapseState(false)
          abs(verticalOffset) >= appBarLayout.totalScrollRange -> updateCollapseState(true)
          else -> return
        }
    }

    private fun updateCollapseState(collapsed : Boolean){
        if(isCollapsed != collapsed){
            onCollapseChanged(collapsed)
        }
        isCollapsed = collapsed
    }

}

fun AppBarLayout.setOnCollapseListener(listener : (Boolean) -> Unit) {
    this.addOnOffsetChangedListener(
        AppBarLayoutCollapseListener(listener)
    )
}