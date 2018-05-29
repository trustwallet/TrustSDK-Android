// Copyright © 2018 Trust.
//
// This file is part of TrustSDK. The full TrustSDK copyright notice, including
// terms governing use, modification, and redistribution, is contained in the
// file LICENSE at the root of the source code distribution tree.

package com.trustwalletapp.trustsdk.commands

import android.content.Intent
import android.net.Uri
import com.trustwalletapp.Constants
import com.trustwalletapp.Constants.ACTION_SIGN_MESSAGE
import com.trustwalletapp.Constants.EXTRA_MESSAGE_ADDRESS
import com.trustwalletapp.Constants.EXTRA_MESSAGE_DATA
import com.trustwalletapp.Constants.EXTRA_MESSAGE_NAME
import com.trustwalletapp.trustcore.Address
import com.trustwalletapp.trustcore.Data
import com.trustwalletapp.trustsdk.Command
import com.trustwalletapp.trustsdk.TrustSDK

internal class SignMessageCommand(
        private val message: Data,
        private val address: Address?,
        private val completion: (Data) -> Unit

) : Command {

    override val name: String
        get() = "sign-message"

    override fun requestIntent(scheme: String): Intent {
        val uri = Uri.fromParts(scheme, "", "")
        val intent = Intent(ACTION_SIGN_MESSAGE, uri)

        intent.putExtra(EXTRA_MESSAGE_NAME, name)
        intent.putExtra(EXTRA_MESSAGE_DATA, message)
        address?.let {
            intent.putExtra(EXTRA_MESSAGE_ADDRESS, it)
        }

        return intent
    }

    override fun handleCallback(resultData: Intent): Boolean {
        val data = resultData.getParcelableExtra(EXTRA_MESSAGE_DATA) as Data?

        return if (data != null) {
            completion(data)
            true
        } else {
            false
        }
    }
}

/**
 * Begin the message signing flow. This method returns an intent that should be used by the calling
 * context in order to launch the Signing App (the Wallet).
 *
 * The returned intent must be used in a startActivityForResult call so that the returned data in onActivityResult
 * can be passed back ot {@link TrustSDK} for completion of the flow.
 *
 * @return an intent to be used by the calling activity.
 */
fun TrustSDK.signMessage(message: Data, address: Address?, completion: (Data) -> Unit): Intent? {
    val command = SignMessageCommand(message, address, completion)
    return execute(command)
}