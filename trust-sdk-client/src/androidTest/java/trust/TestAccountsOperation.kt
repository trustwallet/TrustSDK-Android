package trust

import android.content.Intent
import android.net.Uri
import org.junit.Assert.*
import org.junit.Test

class TestAccountsOperation {

    @Test
    fun testAccountsOperation() {
        val operation = AccountsOperation.Builder()
                .callbackScheme("app_scheme")
                .callbackHost("callback")
                .coins(Coin.ETHEREUM, Coin.WAVES, Coin.ALGORAND, Coin.ATOM, Coin.BINANCE, Coin.BITCOINCASH)
                .requestId(0)
                .build()

        assertEquals("trust://sdk_get_accounts?action=get_accounts&app=app_scheme&callback=callback&id=0&coins.0=60&coins.1=5741564&coins.2=283&coins.3=118&coins.4=714&coins.5=145", operation.buildUri().toString())
    }

    @Test
    fun testAccountsDefaultHost() {
        val operation = AccountsOperation.Builder()
                .callbackScheme("app_scheme")
                .coins(Coin.ETHEREUM, Coin.WAVES, Coin.ALGORAND, Coin.ATOM, Coin.BINANCE, Coin.BITCOINCASH)
                .requestId(0)
                .build()

        assertEquals("trust://sdk_get_accounts?action=get_accounts&app=app_scheme&callback=accounts_callback&id=0&coins.0=60&coins.1=5741564&coins.2=283&coins.3=118&coins.4=714&coins.5=145", operation.buildUri().toString())
    }

    @Test
    fun testAccountsOperationSchemeRequired() {
        try {
            AccountsOperation.Builder()
                    .coins(Coin.AETERNITY)
                    .build()
            fail("Assert scheme param required")
        } catch (ex: IllegalArgumentException) {
            assertEquals("java.lang.IllegalArgumentException: 'scheme' param required", ex.message)
        }
    }

    @Test
    fun testAccountsOperationCoinsRequired() {
        try {
            AccountsOperation.Builder()
                    .callbackScheme("app_scheme")
                    .build()
            fail("Assert coins param required")
        } catch (ex: IllegalArgumentException) {
            assertEquals("java.lang.IllegalArgumentException: 'coins' params required", ex.message)
        }
    }

    @Test
    fun testHandleAccountsCancelResult() {
        val intent = Intent()
        intent.data = Uri.parse("app_scheme://tx_callback?action=get_accounts&cancel=cancel")

        val result = Trust.handleOperationResult(intent)

        assertEquals(OperationError.CANCEL, result!!.error)
        assertNull(result.data)
    }
}
