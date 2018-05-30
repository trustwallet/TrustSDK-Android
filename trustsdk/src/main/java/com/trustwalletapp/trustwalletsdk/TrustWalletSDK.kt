package com.trustwalletapp.trustwalletsdk

import android.content.Intent
import com.trustwalletapp.Constants.ACTION_SIGN_MESSAGE
import com.trustwalletapp.Constants.ACTION_SIGN_TRANSACTION
import com.trustwalletapp.Constants.EXTRA_MESSAGE_ADDRESS
import com.trustwalletapp.Constants.EXTRA_MESSAGE_DATA
import com.trustwalletapp.Constants.EXTRA_TRANSACTION
import com.trustwalletapp.Constants.EXTRA_TRANSACTION_DATA
import com.trustwalletapp.trustcore.Address
import com.trustwalletapp.trustcore.Transaction

/**
 * A Wallet SDK to be used by a Wallet application. This class handles incoming signing requests by delegating
 * them to a {@link WalletDelegate} and then returns the result to the calling app (again via {@link WalletDelegate}.
 */
class TrustWalletSDK(private val delegate: WalletDelegate) {

    /**
     * Handle a request to sign
     *
     * @param intent the incoming intent
     * @return whether the request was handled
     */
    fun handleRequest(intent: Intent): Boolean {

        return when (intent.action) {
            ACTION_SIGN_MESSAGE -> handleSignMessage(intent)
            ACTION_SIGN_TRANSACTION -> handleSignTransaction(intent)
            else -> false
        }
    }

    // region Message
    private fun handleSignMessage(intent: Intent): Boolean {
        val data = intent.getStringExtra(EXTRA_MESSAGE_DATA)
        val address = intent.getParcelableExtra(EXTRA_MESSAGE_ADDRESS) as Address?

        delegate.signMessage(data, address) {
            onMessageSigned(it)
        }

        return true
    }

    private fun onMessageSigned(signedData: String?) {
        if (signedData == null) {
            delegate.onSignFailure()
            return
        }

        val resultIntent = Intent()
        resultIntent.putExtra(EXTRA_MESSAGE_DATA, signedData)

        delegate.onSignResult(resultIntent)
    }
    // endregion

    // region Transaction
    private fun handleSignTransaction(intent: Intent): Boolean {
        if (!intent.hasExtra(EXTRA_TRANSACTION)) {
            return false
        }

        val transaction: Transaction = intent.getParcelableExtra(EXTRA_TRANSACTION) as Transaction

        delegate.signTransaction(transaction) {
            onTransactionSigned(it)
        }

        return true
    }

    private fun onTransactionSigned(signedData: String?) {
        if (signedData == null) {
            delegate.onSignFailure()
            return
        }

        val resultIntent = Intent()
        resultIntent.putExtra(EXTRA_TRANSACTION_DATA, signedData)

        delegate.onSignResult(resultIntent)
    }
    // endregion
}