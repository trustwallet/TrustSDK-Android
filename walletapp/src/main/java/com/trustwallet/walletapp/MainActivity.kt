package com.trustwallet.walletapp

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import trust.SignRequestHelper
import trust.core.entity.Message
import trust.core.entity.Transaction

class MainActivity : AppCompatActivity(), SignRequestHelper.Callback {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        SignRequestHelper(intent, this)
    }

    override fun signMessage(message: Message?) {
        message?.let {
            Log.d("WALLET_APP", "Message: " + message.value + ":" + message.isPersonal)
        }
    }

    override fun signPersonalMessage(message: Message?) {
        message?.let {
            Log.d("WALLET_APP", "Message: " + it.value + ":" + it.isPersonal)
        }
    }

    override fun signTransaction(transaction: Transaction?) {
        transaction?.let {
            Log.d("WALLET_APP", "Message: " + it.value.toString() + ":" + it.recipient.toString())
        }
    }
}
