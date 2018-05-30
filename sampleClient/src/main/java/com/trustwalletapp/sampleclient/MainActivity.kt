package com.trustwalletapp.sampleclient

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import com.trustwalletapp.trustcore.Address
import com.trustwalletapp.trustcore.Transaction
import com.trustwalletapp.trustsdk.Trust
import com.trustwalletapp.trustsdk.commands.signMessage
import com.trustwalletapp.trustsdk.commands.signTransaction
import kotlinx.android.synthetic.main.activity_main.*
import java.math.BigInteger

private const val REQUEST_SIGN = 1

class MainActivity : AppCompatActivity() {

    @SuppressLint("SetTextI18n") // not translatable strings
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        text_tx_address.setText("0xe47494379c1d48ee73454c251a6395fdd4f9eb43")
        text_tx_data.setText("0x8f834227000000000000000000000000000000005224")
        text_tx_amount.setText("1")

        btn_sign_transaction.setOnClickListener { signTransaction() }
        btn_sign_message.setOnClickListener { signMessage() }
    }

    private fun signMessage() {
        val message = text_message.text.toString()
        val intent = Trust.signMessage(message, null, this) { signedMessage ->
            alert("Message", signedMessage)
        }

        startActivityForResult(intent, REQUEST_SIGN)
    }

    private fun signTransaction() {
        val addressText = text_tx_address.text.toString()
        val amountText = text_tx_amount.text.toString()

        val address = Address(addressText)
        val amount = BigInteger(amountText)

        val transaction = Transaction(address, amount, BigInteger.valueOf(21), BigInteger.valueOf(21000))
        val intent = Trust.signTransaction(transaction, this) {signedData ->
            alert("Transaction", signedData)
        }

        startActivityForResult(intent, REQUEST_SIGN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_SIGN) {
            if (resultCode == Activity.RESULT_OK) {
                data?.let { Trust.onActivityResult(it) }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun alert(title: String, message: String) {
        AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setNeutralButton("OK", null)
                .create()
                .show()

    }
}
