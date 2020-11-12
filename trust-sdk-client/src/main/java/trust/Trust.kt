package trust

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri

object Trust {
    const val RESULT_ERROR = 1
    var packageName = "com.wallet.crypto.trustapp"

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
            openMarket(activity)
            null
        }
    }

    fun execute(activity: Activity, operation: Operation) {
        val intent = Intent(Intent.ACTION_VIEW, operation.buildUri())
        if (canStartActivity(activity, intent)) {
            activity.startActivity(intent)
        } else {
            openMarket(activity)
        }
    }

    fun handleOperationResult(intent: Intent?): OperationResult? = when (intent?.data?.getQueryParameter(ExtraKey.ACTION.key)) {
        Action.TRANSFER.key,
        Action.TRADE.key,
        Action.DELEGATE.key,
        Action.GET_ACCOUNTS.key -> buildOperationResult(intent)
        else -> null
    }

    private fun buildOperationResult(intent: Intent?): OperationResult? {
        val signature = intent?.data?.getQueryParameter(ExtraKey.TRANSACTION_SIGN.key)
        val hash = intent?.data?.getQueryParameter(ExtraKey.TRANSACTION_HASH.key)
        val data = intent?.data?.getQueryParameter(ExtraKey.DATA.key)
        val error = intent?.data?.getQueryParameter(ExtraKey.CANCEL.key)
        val id = intent?.data?.getQueryParameter(ExtraKey.ID.key)
        val operationError = if (error.isNullOrEmpty()) null else OperationError.safeValueOf(error)

        return OperationResult(
            data = data ?: hash ?: signature,
            error = operationError,
            id = id?.toIntOrNull()
        )
    }

    private fun openMarket(activity: Activity) {
        try {
            activity.startActivity(Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=${packageName}")))
        } catch (ignored: ActivityNotFoundException) {
            activity.startActivity(Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=${packageName}")))
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
           TRANSACTION_HASH("transaction_hash"),
           TRANSACTION_SIGN("transaction_sign"),
           DATA("data"),
           CANCEL("cancel"),
           ACTION("action"),
           ID("id"),
    }

    internal enum class Action(val key: String) {
        TRANSFER("transfer"),
        TRADE("trade"),
        DELEGATE("delegate"),
        GET_ACCOUNTS("get_accounts"),
    }

    internal enum class Host(val key: String) {
        SDK_GET_ACCOUNTS("sdk_get_accounts"),
        ACCOUNTS_CALLBACK("accounts_callback"),
        TX_CALLBACK("tx_callback"),
        SDK_TRANSACTION("sdk_transaction"),
    }
}