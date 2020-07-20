package com.trustwallet.sdk

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import trust.*

class MainActivity : AppCompatActivity() {
    private var getAccountsCall: Call<Array<Account>, AccountsRequest>? = null
    private var sdkGetAccountsCall: Call<Array<Account>, GetAccountsRequest>? = null
    private lateinit var resultText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        resultText = findViewById(R.id.result_out)

        findViewById<Button>(R.id.get_accounts).setOnClickListener {
            getAccountsCall = Trust.execute(this, AccountsRequest(Coin.ETHEREUM, Coin.WAVES, Coin.ALGORAND, Coin.ATOM, Coin.BINANCE, Coin.BITCOINCASH))
        }
        findViewById<Button>(R.id.sdk_get_accounts).setOnClickListener {
            sdkGetAccountsCall = Trust.execute(this, GetAccountsRequest(Coin.ETHEREUM, Coin.WAVES, Coin.ALGORAND, Coin.ATOM, Coin.BINANCE, Coin.BITCOINCASH))
        }

        if (savedInstanceState != null) {
            getAccountsCall = savedInstanceState.getParcelable("get_accounts_call")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        getAccountsCall?.let {
            it.onActivityResult(requestCode, resultCode, data, OnCompleteListener<Array<Account>> { response ->
                val result = response.result?.joinToString("\n") { account -> "${account.address.data} ${account.coin.name}" }
                resultText.text = result
                Log.d("GET_ACCOUNTS", result ?: "")
            })
        }
        sdkGetAccountsCall?.let {
            it.onActivityResult(requestCode, resultCode, data, OnCompleteListener<Array<Account>> { response ->
                val result = response.result?.joinToString("\n") { account -> "${account.address.data} ${account.coin.name}" }
                resultText.text = result
                Log.d("SDK_GET_ACCOUNTS", result ?: "")
            })
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        getAccountsCall.let {
            outState.putParcelable("get_accounts_call", getAccountsCall)
        }
    }
}
