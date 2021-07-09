package com.gabo.architecture.navigation

import android.content.Context
import androidx.navigation.NavDirections
import com.gabo.architecture.BuildConfig
import com.gabo.architecture.utils.fromContextWrapper
import java.lang.AssertionError

/***
 *  A very simple extension function to make our lives easier with NavigationComponent, without
 *  having to manually reference the NavController on every place we need it.
 *  @throws AssertionError if we are in a debuggable variant and the context is not a NavigationActivity.
 */
fun NavDirections.open(context : Context?){
    context ?: return
    val navActivity : NavigationActivity = context.fromContextWrapper() ?: if(BuildConfig.DEBUG){
        throw AssertionError("Not a NavigationActivity. Context : $context")
    }else{
        return
    }
    navActivity.navController.navigate(this)
}