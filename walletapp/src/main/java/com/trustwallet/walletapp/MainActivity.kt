package com.trustwallet.walletapp

import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import trust.SignRequestHelper
import trust.core.entity.Message
import trust.core.entity.Transaction

class MainActivity : AppCompatActivity(), SignRequestHelper.Callback {
    private lateinit var signHelper: SignRequestHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        signHelper = SignRequestHelper(intent, this)
    }

    override fun signMessage(message: Message?) {
        message?.let {
            Log.d("WALLET_APP", "Message: " + message.value + ":" + message.isPersonal)
            AlertDialog.Builder(this)
                    .setMessage(message.value)
                    .setNegativeButton("cancel") { dialog, wich ->
                        signHelper.onSignCancel(this@MainActivity)
                    }
                    .setPositiveButton("ok") {dialog, which ->
                        signHelper.onMessageSigned(this, "Hello!".toByteArray())
                    }
                    .show()

        }
    }

    override fun signPersonalMessage(message: Message?) {
        message?.let {
            Log.d("WALLET_APP", "Personal message: " + message.value + ":" + message.isPersonal)
            AlertDialog.Builder(this)
                    .setMessage(message.value)
                    .setNegativeButton("cancel") { dialog, wich ->
                        signHelper.onSignCancel(this@MainActivity)
                    }
                    .setPositiveButton("ok") {dialog, which ->
                        signHelper.onMessageSigned(this, "Hello personal!".toByteArray())
                    }
                    .show()

        }
    }

    override fun signTransaction(transaction: Transaction?) {
        transaction?.let {
            Log.d("WALLET_APP", "Message: " + it.value.toString() + ":" + it.recipient.toString())
        }
    }
}
