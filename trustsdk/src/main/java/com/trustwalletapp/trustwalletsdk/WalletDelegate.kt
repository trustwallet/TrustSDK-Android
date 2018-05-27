package com.trustwalletapp.trustwalletsdk

import android.content.Intent
import com.trustwalletapp.trustcore.Address
import com.trustwalletapp.trustcore.Data
import com.trustwalletapp.trustcore.Transaction

/**
 * The wallet. Implementors of this interface are responsible for the actual signing process as well as
 * for the Android app-to-app communication.
 */
interface WalletDelegate {

    /**
     * Signs a message with the specified address.
     *
     * @param message data to sign
     * @param address address to use for signing
     * @param completion completing closure to call with the signed message (or `null` on failure)
     */
    fun signMessage(message: Data, address: Address?, completion: (Data?) -> Unit)

    /**
     * Signs a transaction.
     *
     * @param transaction transaction to sign
     * @param completion completing closure to call with the signed message (or `null` on failure)
     */
    fun signTransaction(transaction: Transaction, completion: (Data?) -> Unit)

    /**
     * Called when the sign process has finished and it's now safe to return to the caller app.
     * Use this method to finish your activity and return control to the caller app by setting the provided
     * intent as result
     *
     * @param resultData an intent holding the result of the sign process
     */
    fun onSignResult(resultData: Intent)

    /**
     * Called when the sign process has finished with an error
     * Use this method to finish your activity and return control to the caller app.
     *
     */
    fun onSignFailure()
}