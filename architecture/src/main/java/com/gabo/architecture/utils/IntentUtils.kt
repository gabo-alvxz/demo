package com.gabo.architecture.utils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.annotation.StringRes
import javax.inject.Inject

/**
 *  Here we should add all the helpers to deal with sending Intents. It should be mockable so
 *  we can seamlessly test our app without worrying about intents getting sent in the tests.
 */
class IntentUtils @Inject constructor(){
    fun openInBrowser(url: String, activity: Activity) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        activity.startActivity(intent)
    }

    fun share(
        activity: Activity,
        @StringRes
        title : Int,
        message: String,
        subject: String? = null
    ) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, message)
            subject?.let {
                putExtra(Intent.EXTRA_SUBJECT, it)
            }
        }
        activity.startActivity(
            Intent.createChooser(intent, activity.getString(title))
        )
    }
}