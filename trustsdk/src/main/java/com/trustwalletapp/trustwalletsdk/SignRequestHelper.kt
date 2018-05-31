package com.trustwalletapp.trustwalletsdk

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import com.trustwalletapp.Constants.ACTION_SIGN_MESSAGE
import com.trustwalletapp.Constants.ACTION_SIGN_TRANSACTION
import com.trustwalletapp.Constants.EXTRA_ERROR_MESSAGE
import com.trustwalletapp.Constants.EXTRA_MESSAGE_ADDRESS
import com.trustwalletapp.Constants.EXTRA_MESSAGE_DATA
import com.trustwalletapp.Constants.EXTRA_TRANSACTION
import com.trustwalletapp.Constants.EXTRA_TRANSACTION_DATA
import com.trustwalletapp.trustcore.Address
import com.trustwalletapp.trustcore.Transaction

/**
 * A Wallet SDK to be used by a Wallet application. This class handles incoming signing requests by delegating
 * them to a [Callback] and then returns the result to the calling app - the [Callback] has to call its provided [SignRequestCallback].
 */
object SignRequestHelper : SignRequestCallback {

    /**
     * The wallet. Implementors of this interface are responsible for the actual signing process as well as
     * for the Android app-to-app communication.
     */
    interface Callback {

        /**
         * Signs a message with the specified address.
         *
         * @param message data to sign
         * @param address address to use for signing
         * @param callback the callback to call with the result of the signing
         */
        fun signMessage(message: String, address: Address?, callback: SignRequestCallback)

        /**
         * Signs a transaction.
         *
         * @param transaction transaction to sign
         * @param callback the callback to call with the result of the signing
         */
        fun signTransaction(transaction: Transaction, callback: SignRequestCallback)
    }


    /**
     * Handle a request to sign. This method parses the request intent and determines further actions.
     *
     * The caller needs to provide a [Callback] that implements the actual signing functionality. The [Callback]
     * would then receive the data to sign along with a [SignRequestCallback] that handles the result of the signing.
     *
     * @param intent the incoming intent
     * @param callback the callback that can sign a message or transaction
     * @return whether the request was handled
     */
    fun handleRequest(intent: Intent, callback: Callback): Boolean {

        return when (intent.action) {
            ACTION_SIGN_MESSAGE -> handleSignMessage(intent, callback)
            ACTION_SIGN_TRANSACTION -> handleSignTransaction(intent, callback)
            else -> false
        }
    }

    private fun handleSignMessage(intent: Intent, callback: Callback): Boolean {
        if (!intent.hasExtra(EXTRA_MESSAGE_DATA)) {
            return false
        }

        val data = intent.getStringExtra(EXTRA_MESSAGE_DATA)
        val address = intent.getParcelableExtra(EXTRA_MESSAGE_ADDRESS) as Address?

        callback.signMessage(data, address, this)

        return true
    }

    private fun handleSignTransaction(intent: Intent, callback: Callback): Boolean {
        if (!intent.hasExtra(EXTRA_TRANSACTION)) {
            return false
        }

        val transaction: Transaction = intent.getParcelableExtra(EXTRA_TRANSACTION) as Transaction

        callback.signTransaction(transaction, this)

        return true
    }

    private fun onMessageSigned(signedData: String?): Intent {
        val resultIntent = Intent()
        resultIntent.putExtra(EXTRA_MESSAGE_DATA, signedData)
        return resultIntent
    }

    private fun onTransactionSigned(signedData: String?): Intent {
        val resultIntent = Intent()
        resultIntent.putExtra(EXTRA_TRANSACTION_DATA, signedData)
        return resultIntent
    }

    private fun onErrorInSigning(message: String?, error: Throwable?): Intent {
        val resultIntent = Intent()
        val errorMessage = message ?: error?.message
        resultIntent.putExtra(EXTRA_ERROR_MESSAGE, errorMessage)
        return resultIntent
    }

    // region SignRequestCallback
    override fun onSuccess(message: String?, transaction: Transaction?, sign: ByteArray, activity: Activity) {
        val dataStr = String(sign)
        val intent = message?.let {
            onMessageSigned(dataStr)
        } ?: transaction?.let {
            onTransactionSigned(dataStr)
        }

        activity.setResult(AppCompatActivity.RESULT_OK, intent)
        activity.finish()
    }

    override fun onFail(message: String?, error: Throwable?, activity: Activity) {
        val intent = onErrorInSigning(message, error)
        activity.setResult(Activity.RESULT_CANCELED, intent)
        activity.finish()
    }

    override fun onCancel(message: String?, transaction: Transaction?, activity: Activity) {
        activity.setResult(Activity.RESULT_CANCELED)
        activity.finish()
    }
    //endregion
}