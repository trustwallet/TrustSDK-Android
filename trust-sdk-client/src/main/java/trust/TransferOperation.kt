package trust

import android.net.Uri
import java.math.BigDecimal
import java.math.BigInteger

class TransferOperation private constructor(
        val action: ActionType,
        val callback: Uri,
        val assetId: String,
        val to: String,
        val amount: BigDecimal,
        val meta: String?,
        val feePrice: BigInteger?,
        val feeLimit: Long?,
        val nonce: Long
): Operation {

    data class Builder(
            var action: ActionType? = null,
            var callback: Uri? = null,
            var assetId: String? = null,
            var to: String? = null,
            var amount: BigDecimal? = null,
            var meta: String? = null,
            var feePrice: BigInteger? = null,
            var feeLimit: Long? = null,
            var nonce: Long = -1
    ) {

        /**
         * ActionType - Send or Sign transaction request
         */
        fun action(action: ActionType) = apply { this.action = action }

        /**
         * callback deep link Uri to app initialized request.
         *
         * Implement deep link handling to get transaction result
         */
        fun callback(callback: Uri) = apply { this.callback = callback }

        /**
         * Universal asset identifier
         *
         * More info: https://developer.trustwallet.com/add_new_asset/universal_asset_id
         */
        fun assetId(assetId: String) = apply { this.assetId = assetId }

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
         * Metadata stands for:
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

        @Throws(IllegalArgumentException::class)
        fun build(): TransferOperation {
            val assetId = this.assetId
            val to = this.to
            val amount = this.amount
            val action = this.action
            val callback = this.callback
            require(!assetId.isNullOrEmpty()) { IllegalArgumentException("'assetId' param required") }
            require(!to.isNullOrEmpty()) { IllegalArgumentException("'to' param required") }
            require(amount != null) { IllegalArgumentException("'amount' param required") }
            require(action != null) { IllegalArgumentException("'action' param required") }
            require(callback != null) { IllegalArgumentException("'callback' param required") }

            return TransferOperation(action, callback, assetId, to, amount, meta, feePrice, feeLimit, nonce)
        }
    }

    override fun buildUri(): Uri {
        val uriBuilder = Uri.parse("trust://${Trust.Host.SDK_TRANSACTION.key}").buildUpon().apply {
            appendQueryParameter("asset", assetId)
            appendQueryParameter("to", to)
            appendQueryParameter("amount", amount.toPlainString())
            appendQueryParameter("nonce", nonce.toString())
            appendQueryParameter("callback", callback.toString())
            appendQueryParameter("confirm_type", action.key)
            meta?.let { appendQueryParameter("meta", it) }
            feePrice?.let { appendQueryParameter("fee_price", it.toString()) }
            feeLimit?.let { appendQueryParameter("fee_limit", it.toString()) }
        }
        return uriBuilder.build()
    }
}