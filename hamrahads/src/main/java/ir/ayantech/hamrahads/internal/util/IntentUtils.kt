package ir.ayantech.hamrahads.internal.util

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri


fun handleIntent(context: Context, type: Int?, data: String?) {

    val intent: Intent? = when (type) {
        1, 2, 3, 6, 7 -> Intent(Intent.ACTION_VIEW, Uri.parse(data))

        4 -> {
            openSmsApp(data)
        }

        5 -> {
            openPhoneApp(data)
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


fun openPhoneApp(data: String?): Intent {
    val intent = Intent(Intent.ACTION_DIAL)
    intent.data = Uri.parse(data)
    return intent
}

fun openSmsApp(text: String?): Intent? {
    val regex = Regex("sms:(\\d{11})\\?body=(.+)")
    if (text != null) {
        val matchResult = regex.find(text)
        if (matchResult != null) {
            val phoneNumber = matchResult.groups[1]?.value
            val message = matchResult.groups[2]?.value

            if (!phoneNumber.isNullOrEmpty() && !message.isNullOrEmpty()) {
                val intent = Intent(Intent.ACTION_SENDTO).apply {
                    data = Uri.parse("smsto:")
                    putExtra("address", phoneNumber)
                    putExtra("sms_body", message)
                }
                return intent
            } else {
                return null
            }
        } else {
            return null
        }
    } else {
        return null
    }
}

fun openMarket(url: String, context: Context) {
    val intent = Intent()
    intent.action = Intent.ACTION_VIEW
    intent.data = Uri.parse(url)
    context.startActivity(intent)
}
