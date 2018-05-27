// Copyright © 2017-2018 Trust.
//
// This file is part of Trust. The full Trust copyright notice, including
// terms governing use, modification, and redistribution, is contained in the
// file LICENSE at the root of the source code distribution tree.
package com.trustwalletapp.trustcore

import java.io.Serializable
import java.math.BigInteger

/**
 * Ethereum transaction.
 */
data class Transaction(
        var to: Address,
        var amount: BigInteger,
        var gasPrice: BigInteger,
        var gasLimit: BigInteger
) : Serializable {
    var nonce: Int = 0
    var payload: Data? = null

}