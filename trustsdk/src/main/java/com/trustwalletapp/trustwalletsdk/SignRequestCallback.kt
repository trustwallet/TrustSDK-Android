package com.trustwalletapp.trustwalletsdk

import android.app.Activity
import com.trustwalletapp.trustcore.Transaction

/**
 * A callback that will be provided to the wallet app by [SignRequestHelper]. This group of callback methods covers
 * the possible scenarios and in addition it would require an activity context so that it can finish the signing flow
 * and return control back to the calling app.
 */
interface SignRequestCallback {
    /**
     * Called by the [SignRequestHelper.Callback] when the signing is successful.
     *
     * @param [message] source message that was sent for signing
     * @param [transaction] source transaction that was sent for signing
     * @param [sign] is the result of the signing
     * @param [activity] the wallet activity used by this callback to finish and return control to the calling app
     *
     */
    fun onSuccess(message: String? = null, transaction: Transaction? = null, sign: ByteArray, activity: Activity)

    /**
     * Called by the [SignRequestHelper.Callback] when an error has occurred.
     *
     * @param [message] an optional error message
     * @param [error] an optional error object for the error that occurred
     * @param [activity] the wallet activity used by this callback to finish and return control to the calling app
     */
    fun onFail(message: String?, error: Throwable?, activity: Activity)

    /**
     * Called by the [SignRequestHelper.Callback] when the user has canceled the signing process.
     *
     * @param [message] source message that was sent for signing
     * @param [transaction] source transaction that was sent for signing
     * @param [activity] the wallet activity used by this callback to finish and return control to the calling app
     */
    fun onCancel(message: String? = null, transaction: Transaction? = null, activity: Activity)
}