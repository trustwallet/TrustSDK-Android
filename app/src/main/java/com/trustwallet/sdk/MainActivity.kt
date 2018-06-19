package com.trustwallet.sdk

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Button
import trust.*
import trust.core.entity.Address
import trust.core.entity.TypedData
import trust.core.util.Hex
import java.math.BigDecimal
import java.math.BigInteger

class MainActivity : AppCompatActivity() {
    private var signMessageCall: Call<SignMessageRequest>? = null
    private var signPersonalMessageCall: Call<SignPersonalMessageRequest>? = null
    private var signTypedMessageCall: Call<SignTypedMessageRequest>? = null
    private var signTransactionCall: Call<SignTransactionRequest>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.sign_transaction).setOnClickListener {
            signTransactionCall = Trust.signTransaction()
                    .recipient(Address("0x3637a62430C67Fe822f7136D2d9D74bDDd7A26C1"))
                    .gasPrice(BigInteger.valueOf(16000000000))
                    .gasLimit(21000)
                    .value(BigDecimal.valueOf(0.3).multiply(BigDecimal.TEN.pow(18)).toBigInteger())
                    .nonce(0)
                    .payload("0x")
                    .call(this)
        }
      
        findViewById<Button>(R.id.sign_message).setOnClickListener {
            signMessageCall = Trust.signMessage()
                    .message("Hello world!!!")
                    .call(this)
        }
        findViewById<Button>(R.id.sign_msg_with_callback).setOnClickListener {
            signMessageCall = Trust.signMessage()
                    .message("Hello world!!!")
                    .callbackUri(Uri.parse("https://google.com/search?q=trust").toString())
                    .call(this)
        }
        findViewById<Button>(R.id.sign_personal_message).setOnClickListener {
            signPersonalMessageCall = Trust.signPersonalMessage()
                    .message("personal message to be signed")
                    .call(this)
        }
        findViewById<Button>(R.id.sign_typed_message).setOnClickListener {
            signTypedMessageCall = Trust.signTypedMessage()
                    .message(
                            TypedData("Var1", "string", "Val1"),
                            TypedData("Var2", "int", 10001))
                    .call(this)
        }

        if (savedInstanceState != null) {
            signMessageCall = savedInstanceState.getParcelable("message_sign_call")
            signTransactionCall = savedInstanceState.getParcelable("transaction_sign_call")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        signMessageCall?.let {
            it.onActivityResult(requestCode, resultCode, data) {response ->
                val result = response.result ?: ""
                Log.d("SIGN_TAG", "Data: " + (String(Hex.hexStringToByteArray(result)) + "; Error: " + response.error))
            }
        }
        signPersonalMessageCall?.let {
            it.onActivityResult(requestCode, resultCode, data) {response ->
                Log.d("SIGN_TAG", "Data: " + response.result + "; Error: " + response.error)
            }
        }
        signTransactionCall?.let {
            it.onActivityResult(requestCode, resultCode, data) {response ->
                Log.d("SIGN_TAG", "Data: " + response.result + "; Error: " + response.error)
            }
        }
        signTypedMessageCall?.let {
            it.onActivityResult(requestCode, resultCode, data) {response ->
                val result = response.result ?: ""
                Log.d("SIGN_TAG", "Data: " + (String(Hex.hexStringToByteArray(result)) + "; Error: " + response.error))
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        signTransactionCall.let {
            outState!!.putParcelable("transaction_sign_call", signTransactionCall)
        }
        signMessageCall.let {
            outState!!.putParcelable("message_sign_call", signMessageCall)
        }
    }
}
