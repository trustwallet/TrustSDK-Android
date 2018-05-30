// Copyright © 2018 Trust.
//
// This file is part of TrustSDK. The full TrustSDK copyright notice, including
// terms governing use, modification, and redistribution, is contained in the
// file LICENSE at the root of the source code distribution tree.

package com.trustwalletapp.trustsdk.commands

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.trustwalletapp.Constants.ACTION_SIGN_TRANSACTION
import com.trustwalletapp.Constants.EXTRA_TRANSACTION
import com.trustwalletapp.Constants.EXTRA_TRANSACTION_DATA
import com.trustwalletapp.trustcore.Transaction
import com.trustwalletapp.trustsdk.Command
import com.trustwalletapp.trustsdk.Trust

internal class SignTransactionCommand(
        private val transaction: Transaction,
        private val completion: (String) -> Unit
) : Command {
    override val name: String
        get() = "sign-transaction"

    override fun requestIntent(scheme: String): Intent {
        val uri = Uri.fromParts(scheme, "", "")
        val intent = Intent(ACTION_SIGN_TRANSACTION, uri)
        intent.putExtra(EXTRA_TRANSACTION, transaction)
        return intent
    }

    override fun handleCallback(resultData: Intent): Boolean {
        val data = resultData.getStringExtra(EXTRA_TRANSACTION_DATA)

        return if (data != null) {
            completion(data)
            true
        } else {
            false
        }
    }
}

/**
 * Begin the transaction signing flow. This method returns an intent that should be used by the calling
 * context in order to launch the Signing App (the Wallet).
 *
 * The returned intent must be used in a startActivityForResult call so that the returned data in onActivityResult
 * can be passed back ot {@link TrustSDK} for completion of the flow.
 *
 * @return an intent to be used by the calling activity.
 */
fun Trust.signTransaction(transaction: Transaction, context: Context, completion: (String) -> Unit): Intent? {
    val command = SignTransactionCommand(transaction, completion)
    return execute(command, context)
}