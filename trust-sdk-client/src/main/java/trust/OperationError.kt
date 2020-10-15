package trust

import java.util.*

enum class OperationError {
    WRONG_ACCOUNT,
    CANCEL,
    UNKNOWN;

    companion object {
        fun safeValueOf(key: String) = try {
            OperationError.valueOf(key.toUpperCase(Locale.ROOT))
        } catch (t: Throwable) {
            OperationError.UNKNOWN
        }
    }
}