package com.trustwalletapp.samplewallet

import android.content.DialogInterface
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import com.trustwalletapp.trustcore.Address
import com.trustwalletapp.trustcore.Transaction
import com.trustwalletapp.trustwalletsdk.SignRequestCallback
import com.trustwalletapp.trustwalletsdk.SignRequestHelper

private const val TAG_DIALOG_WORK = "workDialog"

class MainActivity : AppCompatActivity(), SignRequestHelper.Callback {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        SignRequestHelper.handleRequest(intent, this)
    }

    // Real Wallet app should provide a proper signing here
    override fun signMessage(message: String, address: Address?, callback: SignRequestCallback) {
        val result = message.toByteArray().contentToString()

        val alert = alert(title = "Sign message?", message = message)
        alert.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel") { _, _ ->
            callback.onCancel(message, null, this)
        }
        alert.setButton(DialogInterface.BUTTON_POSITIVE, "OK") { _, _ ->
            callback.onSuccess(message, null, result.toByteArray(), this)
        }
        alert.setButton(DialogInterface.BUTTON_NEUTRAL, "Fail!") { _, _ ->
            callback.onFail("A test failure", null, this)
        }
        alert.show()
    }

    // Real Wallet app should provide a proper signing here
    override fun signTransaction(transaction: Transaction, callback: SignRequestCallback) {
        val result = transaction.toString().toByteArray().contentToString()
        val alert = alert(title = "Sign transaction?", message = transaction.toString())
        alert.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel") {
            _, _ -> callback.onCancel(null, transaction, this)
        }
        alert.setButton(DialogInterface.BUTTON_POSITIVE, "OK") {
            _, _ -> callback.onSuccess(null, transaction, result.toByteArray(), this)
        }
        alert.setButton(DialogInterface.BUTTON_NEUTRAL, "Fail!") {
            _, _ -> callback.onFail(null, Throwable("A test error"), this)
        }
        alert.show()
    }

    private fun alert(title: String, message: String) = AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("OK", null)
            .setNegativeButton("Cancel", null)
            .create()
}
