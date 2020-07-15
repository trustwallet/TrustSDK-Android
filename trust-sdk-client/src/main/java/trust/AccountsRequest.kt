package trust

import android.content.Intent
import android.net.Uri
import kotlinx.android.parcel.Parcelize

@Parcelize
class AccountsRequest(vararg val coins: Coin) : Request<Array<Account>> {

    override fun data(): Uri {
        val uriBuilder = Uri.parse("trust://${Trust.Host.SDK_GET_ACCOUNTS.key}").buildUpon()
        for ((i, coin) in coins.withIndex()) {
            uriBuilder.appendQueryParameter(Trust.ExtraKey.COINS.key + ".$i", coin.index.toString())
        }
        return uriBuilder.build()
    }

    override fun getRequestCode(): Int = 5001

    override fun buildResponse(intent: Intent?, error: Error): Response<Array<Account>> {
        val raw = intent?.getStringArrayExtra(Trust.ExtraKey.ADDRESSES.key)
        val accounts = intent?.getStringArrayExtra(Trust.ExtraKey.ADDRESSES.key)?.mapIndexed { index, address ->
            Account(coins[index], Address(address))
        }?.toTypedArray()
        return Response(this, accounts, error)
    }
}