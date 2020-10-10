package com.trustwallet.sdk

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import trust.*
import java.math.BigDecimal
import java.math.BigInteger

class MainActivity : AppCompatActivity() {
    private var sdkGetAccountsCall: Call<Array<Account>, GetAccountsRequest>? = null
    private lateinit var resultText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        resultText = findViewById(R.id.result_out)

        findViewById<Button>(R.id.sdk_get_accounts).setOnClickListener {
            sdkGetAccountsCall = Trust.execute(this, GetAccountsRequest(Coin.ETHEREUM, Coin.WAVES, Coin.ALGORAND, Coin.ATOM, Coin.BINANCE, Coin.BITCOINCASH))
        }
        findViewById<Button>(R.id.send_transaction).setOnClickListener {
            val operation = TransferOperation.Builder()
                .action(ActionType.SEND)
                .callback(Uri.parse("app_scheme://tx_callback"))
                .coin(714)
                .to("bnb1sh6nuztt3dcevy4ngmztkpapnvxqy7je0t0udr")
                .amount(BigDecimal("0.0001"))
                .meta("memo")
                .build()
            Trust.execute(this, operation)
        }
        findViewById<Button>(R.id.sign_transaction).setOnClickListener {
            val operation = TransferOperation.Builder()
                .action(ActionType.SIGN)
                .callback(Uri.parse("app_scheme://tx_callback"))
                .coin(60)
                .tokenId("0x6B175474E89094C44Da98b954EedeAC495271d0F")
                .to("0xF36f148D6FdEaCD6c765F8f59D4074109E311f0c")
                .amount(BigDecimal("1"))
                .feeLimit(21000L)
                .feePrice(BigInteger("100000000000"))
                .nonce(2)
                .meta("0xa9059cbb0000000000000000000000000F36f148D6FdEaCD6c765F8f59D4074109E311f0c0000000000000000000000000000000000000000000000000000000000000001")
                .build()
            Trust.execute(this, operation)
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        val txCallback = Trust.handleTransferResult(intent)
        val result = when {
            !txCallback?.hash.isNullOrEmpty() -> txCallback?.hash
            !txCallback?.signature.isNullOrEmpty() -> txCallback?.signature
            txCallback?.isCancelled == true -> "Cancelled"
            else -> throw IllegalStateException()
        }
        resultText.text = result
        Log.d("CALLBACK_RESULT", result!!)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        sdkGetAccountsCall?.let {
            it.onActivityResult(requestCode, resultCode, data, OnCompleteListener<Array<Account>> { response ->
                val result = response.result?.joinToString("\n") { account -> "${account.address.data} ${account.coin.name}" }
                resultText.text = result
                Log.d("SDK_GET_ACCOUNTS", result ?: "")
            })
        }
    }
}
