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

/**
 * A helper class that finds supported wallet apps that are installed on the device
 */
internal class WalletAppManager(context: Context) {

    companion object {
        private val apps = listOf(
                WalletApp("Trust", "trust", Uri.parse("https://play.google.com/store/apps/details?id=com.wallet.crypto.trustapp"))
        )
    }

    // List of available wallet apps
    val availableApps: List<WalletApp>

    init {
        val packageManager = context.packageManager
        availableApps = apps.filter {
            //            UIApplication.shared.canOpenURL(URL(string: "\($0.scheme)://")!)
            val uri = Uri.fromParts(it.scheme, "", "")
            val intentMessage = Intent(Constants.ACTION_SIGN_MESSAGE, uri)
            val intentTransaction = Intent(Constants.ACTION_SIGN_TRANSACTION, uri)

            packageManager.queryIntentActivities(intentMessage, 0).isNotEmpty()
                    && packageManager.queryIntentActivities(intentTransaction, 0).isNotEmpty()
        }
    }

    val hasWalletApp = availableApps.isNotEmpty()
}