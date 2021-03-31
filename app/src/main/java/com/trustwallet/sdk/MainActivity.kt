package com.trustwallet.sdk

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import trust.*
import java.math.BigDecimal
import java.math.BigInteger

class MainActivity : AppCompatActivity() {

    private lateinit var resultText: TextView

    private var operation: Operation? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        resultText = findViewById(R.id.result_out)

        findViewById<Button>(R.id.sdk_get_accounts).setOnClickListener {
            operation = AccountsOperation.Builder()
                .callbackScheme("app_scheme")
                .callbackHost("accounts_callback")
                .coins(Coin.ETHEREUM, Coin.WAVES, Coin.ALGORAND, Coin.ATOM, Coin.BINANCE, Coin.BITCOINCASH)
                .requestId(0)
                .build()
            Trust.execute(this, operation!!)
        }
        findViewById<Button>(R.id.send_transaction).setOnClickListener {
            operation = TransferOperation.Builder()
                .action(ActionType.SEND)
                .callbackScheme("app_scheme")
                .coin(714)
                .to("bnb1sh6nuztt3dcevy4ngmztkpapnvxqy7je0t0udr")
                .amount(BigDecimal("0.0001"))
                .meta("memo")
                .build()
            Trust.execute(this, operation!!)
        }
        findViewById<Button>(R.id.sign_transaction).setOnClickListener {
            operation = TransferOperation.Builder()
                .action(ActionType.SIGN)
                .callbackScheme("app_scheme")
                .callbackHost("tx_callback")
                .coin(60)
                .tokenId("0x6B175474E89094C44Da98b954EedeAC495271d0F")
                .from("0xF36f148D6FdEaCD6c765F8f59D4074109E311f0c")
                .to("0x1b38BC1D3a7B2a370425f70CedaCa8119ac24576")
                .amount(BigDecimal("1"))
                .feeLimit(21000L)
                .feePrice(BigInteger("100000000000"))
                .nonce(2)
                .meta("0xa9059cbb0000000000000000000000000F36f148D6FdEaCD6c765F8f59D4074109E311f0c0000000000000000000000000000000000000000000000000000000000000001")
                .requestId(2)
                .build()
            Trust.execute(this, operation!!)
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        val txCallback = Trust.handleOperationResult(intent)
        if (txCallback?.error != null) {
            resultText.text =  txCallback.error?.name
        } else if (!txCallback?.data.isNullOrEmpty()) {
            onResultData(txCallback?.data)
        }
    }

    private fun onResultData(data: String?) {
        val parsedData = when (operation) {
            is AccountsOperation -> {
                val accounts = (operation as? AccountsOperation)?.parseResultData(data)
                accounts?.joinToString("\n") { account -> "${account.address.data} ${account.coin.name}" }
            }
            else -> data
        }
        resultText.text = parsedData
        Log.d("CALLBACK_RESULT", parsedData ?: return)
    }
}
