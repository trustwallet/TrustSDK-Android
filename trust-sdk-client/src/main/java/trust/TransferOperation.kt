package trust

import android.net.Uri
import java.math.BigDecimal
import java.math.BigInteger

class TransferOperation private constructor(
        val action: ActionType,
        val scheme: String,
        val host: String,
        val coin: Int,
        val tokenId: String?,
        val from: String?,
        val to: String,
        val amount: BigDecimal,
        val meta: String?,
        val feePrice: BigInteger?,
        val feeLimit: Long?,
        val nonce: Long,
        val id: Int
): Operation {

    data class Builder(
            var action: ActionType? = null,
            var scheme: String? = null,
            var host: String = Trust.Host.TX_CALLBACK.key,
            var coin: Int? = null,
            var tokenId: String? = null,
            var from: String? = null,
            var to: String? = null,
            var amount: BigDecimal? = null,
            var meta: String? = null,
            var feePrice: BigInteger? = null,
            var feeLimit: Long? = null,
            var nonce: Long = -1,
            var id: Int = 1
    ) {

        /**
         * ActionType - Send or Sign transaction request
         */
        fun action(action: ActionType) = apply { this.action = action }

        /**
         * callback deep link Uri scheme to app initialized request.
         *
         * Implement deep link handling to get transaction result
         */
        fun callbackScheme(scheme: String) = apply { this.scheme = scheme }

        /**
         * callback deep link Uri host to app initialized request.
         *
         * 'tx_callback' by default
         */
        fun callbackHost(host: String) = apply { this.host = host }

        /**
         * Slip44 index
         *
         * Exception: Use 20000714 for Binance Smart Chain
         *
         * More info: https://github.com/satoshilabs/slips/blob/master/slip-0044.md
         */
        fun coin(coin: Int) = apply { this.coin = coin }

        /**
         * (Optional) token, following standard of unique identifier on the blockhain as smart contract address or asset ID
         */
        fun tokenId(tokenId: String) = apply { this.tokenId = tokenId }

        /**
         * (Optional) "From" address parameter specifies a wallet which contains given account
         *
         * If you leave it null - current wallet will be used for signing
         */
        fun from(from: String) = apply { this.from = from }

        /**
         * Recipient address
         */
        fun to(to: String) = apply { this.to = to }

        /**
         * Transaction amount in human-readable (unit) format
         *
         * E.g. 0.01 ETH - BigDecimal("0.01")
         */
        fun amount(amount: BigDecimal) = apply { this.amount = amount }

        /**
         * (Optional) Metadata stands for:
         * - Transaction data in hex format
         * - Memo
         * - Destination tag
         */
        fun meta(meta: String) = apply { this.meta = meta }

        /**
         * (Optional) You can set your custom fee price
         *
         * Subunit format
         * Eth gas price 100 gwei - BigInteger("100000000000")
         */
        fun feePrice(feePrice: BigInteger) = apply { this.feePrice = feePrice }

        /**
         * (Optional) You can set your custom fee limit
         *
         * Subunit format
         * Eth gas limit 21000 wei - 21000L
         */
        fun feeLimit(feeLimit: Long) = apply { this.feeLimit = feeLimit }

        /**
         * (Optional) You can set your custom nonce or sequence
         */
        fun nonce(nonce: Long) = apply { this.nonce = nonce }

        /**
         * (Optional) Any incrementing integer
         */
        fun requestId(id: Int) = apply { this.id = id }

        @Throws(IllegalArgumentException::class)
        fun build(): TransferOperation {
            val coin = this.coin
            val to = this.to
            val amount = this.amount
            val action = this.action
            val scheme = this.scheme
            val host = this.host
            require(coin != null) { IllegalArgumentException("'coin' param required") }
            require(!to.isNullOrEmpty()) { IllegalArgumentException("'to' param required") }
            require(amount != null) { IllegalArgumentException("'amount' param required") }
            require(action != null) { IllegalArgumentException("'action' param required") }
            require(scheme != null) { IllegalArgumentException("'scheme' param required") }

            return TransferOperation(
                    action = action,
                    scheme = scheme,
                    host = host,
                    coin = coin,
                    tokenId = tokenId,
                    from = from,
                    to = to,
                    amount = amount,
                    meta = meta,
                    feePrice = feePrice,
                    feeLimit = feeLimit,
                    nonce = nonce,
                    id = id
            )
        }
    }

    override fun buildUri(): Uri {
        val uriBuilder = Uri.parse("trust://${Trust.Host.SDK_TRANSACTION.key}").buildUpon().apply {
            appendQueryParameter("action", Trust.Action.TRANSFER.key)
            appendQueryParameter("asset", buildAssetId(coin, tokenId))
            appendQueryParameter("to", to)
            appendQueryParameter("amount", amount.toPlainString())
            appendQueryParameter("nonce", nonce.toString())
            appendQueryParameter("app", scheme)
            appendQueryParameter("callback", host)
            appendQueryParameter("confirm_type", action.key)
            appendQueryParameter("id", id.toString())
            from?.let { appendQueryParameter("from", it) }
            meta?.let { appendQueryParameter("meta", it) }
            feePrice?.let { appendQueryParameter("fee_price", it.toString()) }
            feeLimit?.let { appendQueryParameter("fee_limit", it.toString()) }
        }
        return uriBuilder.build()
    }

}