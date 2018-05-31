package com.trustwalletapp

object Constants {
    const val ACTION_SIGN_MESSAGE = "com.trustwalletapp.action.SIGN_MESSAGE"
    const val ACTION_SIGN_TRANSACTION = "com.trustwalletapp.action.SIGN_TRANSACTION"

    internal const val EXTRA_MESSAGE_NAME = "messageName"
    internal const val EXTRA_MESSAGE_DATA = "messageData"
    internal const val EXTRA_MESSAGE_ADDRESS = "messageAddress"

    internal const val EXTRA_TRANSACTION = "transaction"
    internal const val EXTRA_TRANSACTION_DATA = "transactionData"

    const val EXTRA_ERROR_MESSAGE = "errorMessage"
}