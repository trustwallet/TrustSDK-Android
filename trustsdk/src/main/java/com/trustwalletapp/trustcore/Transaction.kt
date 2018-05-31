// Copyright © 2017-2018 Trust.
//
// This file is part of Trust. The full Trust copyright notice, including
// terms governing use, modification, and redistribution, is contained in the
// file LICENSE at the root of the source code distribution tree.
package com.trustwalletapp.trustcore

import android.os.Parcel
import android.os.Parcelable
import java.math.BigInteger

/**
 * Ethereum transaction.
 */
data class Transaction(
        var to: Address,
        var amount: BigInteger,
        var gasPrice: BigInteger,
        var gasLimit: BigInteger
) : Parcelable {
    var nonce: Int = 0
    var payload: String? = null


    private constructor(parcel: Parcel) : this(
            parcel.readParcelable(Address::class.java.classLoader) as Address,
            BigInteger(parcel.readString()),
            BigInteger(parcel.readString()),
            BigInteger(parcel.readString())
    ) {
        nonce = parcel.readInt()
        payload = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(to, flags)
        parcel.writeString(amount.toString())
        parcel.writeString(gasPrice.toString())
        parcel.writeString(gasLimit.toString())

        parcel.writeInt(nonce)
        parcel.writeString(payload)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Transaction> {
        override fun createFromParcel(parcel: Parcel): Transaction {
            return Transaction(parcel)
        }

        override fun newArray(size: Int): Array<Transaction?> {
            return arrayOfNulls(size)
        }
    }

}