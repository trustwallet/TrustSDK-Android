package com.trustwalletapp.samplewallet

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.DialogFragment
import com.trustwalletapp.trustcore.Address
import com.trustwalletapp.trustcore.Data
import com.trustwalletapp.trustcore.Transaction
import com.trustwalletapp.trustwalletsdk.TrustWalletSDK
import com.trustwalletapp.trustwalletsdk.WalletDelegate

private const val TAG_DIALOG_WORK = "workDialog"

class MainActivity : AppCompatActivity(), WalletDelegate {

    private val trustWalletSDK = TrustWalletSDK(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        trustWalletSDK.handleRequest(intent)
    }

    override fun signMessage(message: Data, address: Address?, completion: (Data?) -> Unit) {
        showWorkDialog("Signing message", message.data)

        // Real Wallet app should provide a proper signing here
        
        Handler(mainLooper).postDelayed(
                {
                    val result = message.data
                            .toByteArray()
                            .contentToString()

                    completion(Data(result))
                    closeWorkDialog()
                },
                3000
        )
    }

    override fun signTransaction(transaction: Transaction, completion: (Data?) -> Unit) {
        showWorkDialog("Signing transaction", transaction.toString())

        // Real Wallet app should provide a proper signing here

        Handler(mainLooper).postDelayed(
                {
                    val result = transaction.toString()
                            .toByteArray()
                            .contentToString()

                    completion(Data(result))
                    closeWorkDialog()
                },
                3000
        )
    }

    override fun onSignResult(resultData: Intent) {
        closeWorkDialog()

        setResult(RESULT_OK, resultData)
        finish()
    }

    override fun onSignFailure() {
        closeWorkDialog()

        setResult(Activity.RESULT_CANCELED)
        finish()
    }

    private fun showWorkDialog(title: String, message: String) {
        WorkDialog.newInstance(title, message)
                .show(supportFragmentManager, TAG_DIALOG_WORK)
    }

    private fun closeWorkDialog() {
        val fragment = supportFragmentManager.findFragmentByTag(TAG_DIALOG_WORK) as DialogFragment
        fragment.dismiss()
    }

}
