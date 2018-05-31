// Copyright © 2018 Trust.
//
// This file is part of TrustSDK. The full TrustSDK copyright notice, including
// terms governing use, modification, and redistribution, is contained in the
// file LICENSE at the root of the source code distribution tree.
package com.trustwalletapp.trustsdk

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import com.trustwalletapp.Constants

object Trust {
    const val REQUEST_SIGN_MESSAGE = 1
    const val REQUEST_SIGN_TRANSACTION = 2
    private const val EXTRA_COMMAND = "com.trustwalletapp.trust.command"

    interface SigningResponseHandler {
        fun onMessageSigned(message: String?)
        fun onTransactionSigned(transactionData: String?)
    }

    var walletApp: WalletApp = WalletApp("Trust", "trust", Uri.parse("https://play.google.com/store/apps/details?id=com.wallet.crypto.trustapp"))

    /**
     * Handles the response callback.
     *
     * @param requestCode the request code as received in [Activity.onActivityResult]
     * @param resultCode the result code as received in [Activity.onActivityResult]
     * @param resultData the result data as received in [Activity.onActivityResult]
     * @param responseHandler a function that receives the parsed response data
     */
    fun onActivityResult(requestCode: Int, resultCode: Int, resultData: Intent?, responseHandler: SigningResponseHandler?): Boolean {
        if (resultCode != Activity.RESULT_OK) {
            return false
        }

        if (requestCode == REQUEST_SIGN_MESSAGE) {
            val signedMessage = resultData?.getStringExtra(Constants.EXTRA_MESSAGE_DATA)
            responseHandler?.onMessageSigned(signedMessage)
        } else if (requestCode == REQUEST_SIGN_TRANSACTION) {
            val signedTransactionData = resultData?.getStringExtra(Constants.EXTRA_TRANSACTION_DATA)
            responseHandler?.onTransactionSigned(signedTransactionData)
        }

        return true
    }

    internal fun execute(command: Command, activity: Activity) {
        if (!canStartWallet(activity)) {
            fallbackToInstall(activity)
            return
        }

        val intent = command.requestIntent(walletApp.scheme)
        val requestCode = command.requestCode()

        activity.startActivityForResult(intent, requestCode)
    }

    private fun canStartWallet(context: Context): Boolean {
        val packageManager = context.packageManager
        val uri = Uri.fromParts(walletApp.scheme, "", "")
        val intentMessage = Intent(Constants.ACTION_SIGN_MESSAGE, uri)
        val intentTransaction = Intent(Constants.ACTION_SIGN_TRANSACTION, uri)

        return packageManager.queryIntentActivities(intentMessage, 0).isNotEmpty()
                && packageManager.queryIntentActivities(intentTransaction, 0).isNotEmpty()
    }

    private fun fallbackToInstall(context: Context) {
        var uri = walletApp.installUri
        if (uri.scheme.isNullOrEmpty()) {
            uri = uri.buildUpon().scheme("http").build()
        }
        context.startActivity(Intent(Intent.ACTION_VIEW, uri))
    }
}