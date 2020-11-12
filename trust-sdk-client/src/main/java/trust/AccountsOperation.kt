package trust

import android.net.Uri

class AccountsOperation private constructor(
        val scheme: String,
        val host: String,
        val id: Int,
        vararg val coins: Coin
): Operation {

    class Builder(
            var scheme: String? = null,
            var host: String = Trust.Host.ACCOUNTS_CALLBACK.key,
            var id: Int = 1,
            vararg var coins: Coin
    ) {

        /**
         * callback deep link Uri scheme to app initialized request.
         *
         * Implement deep link handling to get transaction result
         */
        fun callbackScheme(scheme: String) = apply { this.scheme = scheme }

        /**
         * callback deep link Uri host to app initialized request.
         *
         * 'accounts_callback' by default
         */
        fun callbackHost(host: String) = apply { this.host = host }

        /**
         * Requested coin accounts
         */
        fun coins(vararg coins: Coin) = apply { this.coins = coins }

        /**
         * (Optional) Any incrementing integer
         */
        fun requestId(id: Int) = apply { this.id = id }

        @Throws(IllegalArgumentException::class)
        fun build(): AccountsOperation {
            val coins = this.coins
            val scheme = this.scheme
            val host = this.host
            require(coins.isNotEmpty()) { IllegalArgumentException("'coins' params required") }
            require(scheme != null) { IllegalArgumentException("'scheme' param required") }

            return AccountsOperation(
                    coins = coins,
                    scheme = scheme,
                    id = id,
                    host = host,
            )
        }
    }

    override fun buildUri(): Uri {
        val uriBuilder = Uri.parse("trust://${Trust.Host.SDK_GET_ACCOUNTS.key}").buildUpon().apply {
            appendQueryParameter("action", Trust.Action.GET_ACCOUNTS.key)
            appendQueryParameter("app", scheme)
            appendQueryParameter("callback", host)
            appendQueryParameter("id", id.toString())
            for ((i, coin) in coins.withIndex()) {
                appendQueryParameter(Trust.ExtraKey.COINS.key + ".$i", coin.index.toString())
            }
        }
        return uriBuilder.build()
    }

    fun parseResultData(data: String?) = data?.split(",")?.mapIndexed { index, address ->
        Account(coins[index], Address(address))
    }?.toTypedArray()
}