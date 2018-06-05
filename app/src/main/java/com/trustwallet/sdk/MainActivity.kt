package com.trustwallet.sdk

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.Toast
import trust.Call
import trust.Trust
import trust.core.entity.Address
import java.math.BigDecimal
import java.math.BigInteger

class MainActivity : AppCompatActivity() {

    private var messageCall: Call? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.sign_message).setOnClickListener {
            Trust.signMessage()
                    .message("")
                    .isPersonal(false)
                    .call(this)
            //        messageCall = Trust.signMessage()
            //                .message("")
            //                .isPersonal(false)
            //                .call(this)
        }

        findViewById<Button>(R.id.sign_transaction).setOnClickListener {
            Trust.signTransaction()
                    .recipient(Address("0x3637a62430C67Fe822f7136D2d9D74bDDd7A26C1"))
                    .gasPrice(BigInteger.valueOf(16))
                    .gasLimit(21000)
                    .value(BigDecimal.valueOf(0.3).multiply(BigDecimal.TEN.pow(18)).toBigInteger())
                    .nonce(0)
                    .payload("0x")
                    .call(this)

        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        messageCall?.let {
            outState!!.putParcelable("message_sign_call", it)
        }
    }

    @SuppressLint("ShowToast")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Trust.onActivityResult(requestCode, resultCode, data).subscribe { request, signHex ->
            Toast.makeText(this, "Sign: $signHex", Toast.LENGTH_LONG)
        }
        messageCall?.let {
            it.onActivityResult(requestCode, resultCode, data).subscribe { request, signHex ->

            }
        }
    }
}
