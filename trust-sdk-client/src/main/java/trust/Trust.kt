package trust

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri

object Trust {
    const val RESULT_ERROR = 1
    private var packageName = "com.wallet.crypto.trustapp"

    fun setWalletAppPackageName(packageName: String) {
        Trust.packageName = packageName
    }

    fun <R, T : Request<R>> execute(activity: Activity, request: T): Call<R, T>? {
        val intent = Intent()
        intent.data = request.data()
        return if (canStartActivity(activity, intent)) {
            activity.startActivityForResult(intent, request.getRequestCode())
            Call(request)
        } else {
            try {
                activity.startActivity(Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("market://details?id=$packageName")))
            } catch (anfe: ActivityNotFoundException) {
                activity.startActivity(Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id=$packageName")))
            }
            null
        }
    }

    private fun canStartActivity(context: Context, intent: Intent): Boolean {
        val pm = context.packageManager
        return pm.queryIntentActivities(intent, 0).size > 0
    }

    internal enum class ExtraKey(val key: String) {
           ERROR("error"),
           COINS("coins"),
           ADDRESSES("addresses"),
    }

    internal enum class Host(val key: String) {
        GET_ACCOUNTS("get_accounts")
    }
}