package co.framework.app.utils

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

fun AppCompatActivity.startActivityWithFinish(context : Context, activity : Class<*>) {
    startActivity(Intent(context, activity).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
    this.finish()
}