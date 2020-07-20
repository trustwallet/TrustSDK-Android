package trust

import android.content.Intent
import android.net.Uri
import kotlinx.android.parcel.Parcelize

@Parcelize
@Deprecated("Use new GetAccountsRequest with sdk_get_accounts link")
class AccountsRequest(vararg val coins: Coin) : Request<Array<Account>> {

    override fun data(): Uri {
        val uriBuilder = Uri.parse("trust://${Trust.Host.GET_ACCOUNTS.key}").buildUpon()
        for (coin in coins) {
            uriBuilder.appendQueryParameter(Trust.ExtraKey.COINS.key, coin.index.toString())
        }
        return uriBuilder.build()
    }

    override fun getRequestCode(): Int = 5001

    override fun buildResponse(intent: Intent?, error: Error): Response<Array<Account>> {
        val accounts = intent?.getStringArrayExtra(Trust.ExtraKey.ADDRESSES.key)?.mapIndexed { index, address ->
            Account(coins[index], Address(address))
        }?.toTypedArray()
        return Response(this, accounts, error)
    }
}

@Parcelize
class GetAccountsRequest(vararg val coins: Coin) : Request<Array<Account>> {

    override fun data(): Uri {
        val uriBuilder = Uri.parse("trust://${Trust.Host.SDK_GET_ACCOUNTS.key}").buildUpon()
        for ((i, coin) in coins.withIndex()) {
            uriBuilder.appendQueryParameter(Trust.ExtraKey.COINS.key + ".$i", coin.index.toString())
        }
        return uriBuilder.build()
    }

    override fun getRequestCode(): Int = 5002

    override fun buildResponse(intent: Intent?, error: Error): Response<Array<Account>> {
        val accounts = intent?.getStringArrayExtra(Trust.ExtraKey.ADDRESSES.key)?.mapIndexed { index, address ->
            Account(coins[index], Address(address))
        }?.toTypedArray()
        return Response(this, accounts, error)
    }
}