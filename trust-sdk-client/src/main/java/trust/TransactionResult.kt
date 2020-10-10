package trust

data class TransactionResult(val hash: String? = null, val signature: String? = null, val isCancelled: Boolean = false)