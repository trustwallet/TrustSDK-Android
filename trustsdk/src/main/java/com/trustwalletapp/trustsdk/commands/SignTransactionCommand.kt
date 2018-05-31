// Copyright © 2018 Trust.
//
// This file is part of TrustSDK. The full TrustSDK copyright notice, including
// terms governing use, modification, and redistribution, is contained in the
// file LICENSE at the root of the source code distribution tree.

package com.trustwalletapp.trustsdk.commands

import android.app.Activity
import android.content.Intent
import android.net.Uri
import com.trustwalletapp.Constants.ACTION_SIGN_TRANSACTION
import com.trustwalletapp.Constants.EXTRA_TRANSACTION
import com.trustwalletapp.trustcore.Transaction
import com.trustwalletapp.trustsdk.Command
import com.trustwalletapp.trustsdk.Trust

internal class SignTransactionCommand(
        private val transaction: Transaction
) : Command {
    override val name: String
        get() = "sign-transaction"

    override fun requestIntent(scheme: String): Intent {
        val uri = Uri.fromParts(scheme, "", "")
        val intent = Intent(ACTION_SIGN_TRANSACTION, uri)
        intent.putExtra(EXTRA_TRANSACTION, transaction)
        return intent
    }

    override fun requestCode(): Int = Trust.REQUEST_SIGN_TRANSACTION
}

/**
 * Begin the transaction signing flow. This method takes care of starting the Signing App (the Wallet).
 * Make sure to override [Activity.onActivityResult] in your activity and invoke the [Trust.onActivityResult]
 * method to handle the response.
 *
 */
fun Trust.signTransaction(transaction: Transaction, activity: Activity) {
    val command = SignTransactionCommand(transaction)
    execute(command, activity)
}