package ir.ayantech.hamrahads.utils

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri


fun handleIntent(context: Context, type: Int?, data: String?) {
    val intent: Intent? = when (type) {
        1, 2, 3, 6, 7 -> Intent(Intent.ACTION_VIEW, Uri.parse(data))

        4 -> Intent(Intent.ACTION_SENDTO).apply {
            Uri.parse("sms:$data")
            putExtra("sms_body", "Sample message")
        }

        5 -> Intent(Intent.ACTION_DIAL).apply {
            Uri.parse(data)
        }

        else -> null
    }

    intent?.let {
        try {
            context.startActivity(it)
        } catch (e: ActivityNotFoundException) {
            e.printStackTrace()
        }
    }
}


fun openPhoneApp(phoneNumber: String, context: Context) {
    val intent = Intent(Intent.ACTION_DIAL)
    intent.data = Uri.parse("tel:$phoneNumber")
    context.startActivity(intent)
}

fun openSmsApp(phoneNumber: String?, message: String?, context: Context) {
    val intent = Intent(Intent.ACTION_SENDTO).apply {
        data = Uri.parse("smsto:")
        if (phoneNumber != null) {
            putExtra("address", phoneNumber)
        }
        if (message != null) {
            putExtra("sms_body", message)
        }
    }
    if (intent.resolveActivity(context.packageManager) != null) {
        context.startActivity(intent)
    }
}

fun openMarket(url: String, context: Context) {
    val intent = Intent()
    intent.action = Intent.ACTION_VIEW
    intent.data = Uri.parse(url)
    context.startActivity(intent)
}