// Copyright © 2018 Trust.
//
// This file is part of TrustSDK. The full TrustSDK copyright notice, including
// terms governing use, modification, and redistribution, is contained in the
// file LICENSE at the root of the source code distribution tree.
package com.trustwalletapp.trustsdk

import android.content.Context
import android.content.Intent

class TrustSDK(context: Context) {
    private val walletApp: WalletApp?
    private var pendingCommand: Command? = null

    init {
        walletApp = WalletAppManager(context)
                .availableApps.first()
    }

    /**
     * Handles an open URL callback
     * @return `true` if the URL was handled; `false` otherwise.
     */
    fun handleCallback(resultData: Intent): Boolean {
//        guard let components = URLComponents(url: url, resolvingAgainstBaseURL: false), components.scheme == callbackScheme else {
//        return false
//    }
        val result = pendingCommand?.handleCallback(resultData) ?: false
        pendingCommand = null
        return result
    }

    internal fun execute(command: Command): Intent? {
        return walletApp?.let {
            pendingCommand = command
            command.requestIntent(it.scheme)
        } ?: fallbackToInstall()
    }

    private fun fallbackToInstall(): Intent? {
        return walletApp?.let {
            return Intent(Intent.ACTION_VIEW, it.installUri)
        }
    }
}