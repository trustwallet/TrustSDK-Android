package com.trustwallet.walletapp

import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.google.gson.Gson
import trust.SignRequestHelper
import trust.core.entity.Message
import trust.core.entity.Transaction
import trust.core.entity.TypedData

class MainActivity : AppCompatActivity(), SignRequestHelper.Callback {
    private lateinit var signHelper: SignRequestHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        signHelper = SignRequestHelper(intent, this)
    }

    override fun signMessage(message: Message<String>?) {
        message?.let {
            Log.d("WALLET_APP", "Message: " + message.value)
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

    override fun signTypedMessage(message: Message<Array<TypedData>>?) {
        message?.let {
            Log.d("WALLET_APP", "Message: " + message.value)
            AlertDialog.Builder(this)
                    .setMessage(Gson().toJson(message.value))
                    .setNegativeButton("cancel") { dialog, wich ->
                        signHelper.onSignCancel(this@MainActivity)
                    }
                    .setPositiveButton("ok") {dialog, which ->
                        signHelper.onMessageSigned(this, "Hello!".toByteArray())
                    }
                    .show()

        }
    }

    override fun signPersonalMessage(message: Message<String>?) {
        message?.let {
            Log.d("WALLET_APP", "Personal message: " + message.value)
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
