// Copyright © 2018 Trust.
//
// This file is part of TrustSDK. The full TrustSDK copyright notice, including
// terms governing use, modification, and redistribution, is contained in the
// file LICENSE at the root of the source code distribution tree.
package com.trustwalletapp.trustsdk

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.trustwalletapp.Constants

object Trust {
    private var pendingCommand: Command? = null
    var walletApp: WalletApp = WalletApp("Trust", "trust", Uri.parse("https://play.google.com/store/apps/details?id=com.wallet.crypto.trustapp"))

    /**
     * Handles a response callback
     * @return `true` if the URL was handled; `false` otherwise.
     */
    fun handleCallback(resultData: Intent): Boolean {
        val result = pendingCommand?.handleCallback(resultData) ?: false
        pendingCommand = null
        return result
    }

    internal fun execute(command: Command, context: Context): Intent? {

        return if (canStartWallet(context)) {
            pendingCommand = command
            command.requestIntent(walletApp.scheme)
        } else {
            fallbackToInstall()
        }
    }

    private fun canStartWallet(context: Context): Boolean {
        val packageManager = context.packageManager
        val uri = Uri.fromParts(walletApp.scheme, "", "")
        val intentMessage = Intent(Constants.ACTION_SIGN_MESSAGE, uri)
        val intentTransaction = Intent(Constants.ACTION_SIGN_TRANSACTION, uri)

        return packageManager.queryIntentActivities(intentMessage, 0).isNotEmpty() && packageManager.queryIntentActivities(intentTransaction, 0).isNotEmpty()
    }

    private fun fallbackToInstall(): Intent {
        return Intent(Intent.ACTION_VIEW, walletApp.installUri)
    }
}