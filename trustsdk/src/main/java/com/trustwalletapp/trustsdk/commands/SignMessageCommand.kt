// Copyright © 2018 Trust.
//
// This file is part of TrustSDK. The full TrustSDK copyright notice, including
// terms governing use, modification, and redistribution, is contained in the
// file LICENSE at the root of the source code distribution tree.

package com.trustwalletapp.trustsdk.commands

import android.app.Activity
import android.content.Intent
import android.net.Uri
import com.trustwalletapp.Constants.ACTION_SIGN_MESSAGE
import com.trustwalletapp.Constants.EXTRA_MESSAGE_ADDRESS
import com.trustwalletapp.Constants.EXTRA_MESSAGE_DATA
import com.trustwalletapp.Constants.EXTRA_MESSAGE_NAME
import com.trustwalletapp.trustcore.Address
import com.trustwalletapp.trustsdk.Command
import com.trustwalletapp.trustsdk.Trust

internal class SignMessageCommand(
        private val message: String,
        private val address: Address?

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

    override fun requestCode(): Int = Trust.REQUEST_SIGN_MESSAGE
}

/**
 * Begin the message signing flow. This method takes care of starting the Signing App (the Wallet).
 * Make sure to override [Activity.onActivityResult] in your activity and invoke the [Trust.onActivityResult]
 * method to handle the response.
 *
 */
fun Trust.signMessage(message: String, address: Address?, activity: Activity) {
    val command = SignMessageCommand(message, address)
    execute(command, activity)
}