package com.gabo.architecture.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.viewbinding.ViewBinding
import com.gabo.architecture.navigation.NavigationActivity


abstract class BaseFragment <T : ViewBinding> : Fragment(){
    var binding : T? = null
        private set

    private val navController : NavController?
        get() = (activity as? NavigationActivity)?.navController


    abstract fun createBinding(layoutInflater: LayoutInflater, viewGroup: ViewGroup?) : T

    final override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)
        return createBinding(layoutInflater, container).apply {
            binding = this
        }.root
    }


    fun goUp() {
        navController?.navigateUp()
    }

    fun setToolbar(toolbar: Toolbar){
        val activity = activity as? AppCompatActivity?:return
        activity.setSupportActionBar(toolbar)
        toolbar.menu.clear()
        setTitle("")
        activity.invalidateOptionsMenu()
    }

    //Just a shortcut
    fun invalidateOptionsMenu(){
        activity?.invalidateOptionsMenu()
    }

    fun setTitle(title : String) {
        actionBar?.title = title
    }

    val actionBar : ActionBar?
        get() = (activity as AppCompatActivity).supportActionBar


}