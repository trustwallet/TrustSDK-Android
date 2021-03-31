package trust

import java.util.*

enum class OperationError {
    WRONG_ACCOUNT,
    CANCEL,
    UNKNOWN;

    companion object {
        fun safeValueOf(key: String) = try {
            valueOf(key.toUpperCase(Locale.ROOT))
        } catch (t: Throwable) {
            UNKNOWN
        }
    }
}