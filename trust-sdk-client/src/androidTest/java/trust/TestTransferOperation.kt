package trust

import android.content.Intent
import android.net.Uri
import org.junit.Assert.*
import org.junit.Test
import java.math.BigDecimal
import java.math.BigInteger

class TestTransferOperation {

    @Test
    fun testTransferSignOperation() {
        val operation = TransferOperation.Builder()
                .action(ActionType.SIGN)
                .callbackScheme("app_scheme")
                .callbackHost("callback")
                .coin(60)
                .tokenId("0x6B175474E89094C44Da98b954EedeAC495271d0F")
                .to("0xF36f148D6FdEaCD6c765F8f59D4074109E311f0c")
                .amount(BigDecimal("1"))
                .feeLimit(21000L)
                .feePrice(BigInteger("100000000000"))
                .nonce(2)
                .meta("0xa9059cbb0000000000000000000000000F36f148D6FdEaCD6c765F8f59D4074109E311f0c0000000000000000000000000000000000000000000000000000000000000001")
                .requestId(0)
                .build()

        assertEquals("trust://sdk_transaction?action=transfer&asset=c60_t0x6B175474E89094C44Da98b954EedeAC495271d0F&to=0xF36f148D6FdEaCD6c765F8f59D4074109E311f0c&amount=1&nonce=2&app=app_scheme&callback=callback&confirm_type=sign&id=0&meta=0xa9059cbb0000000000000000000000000F36f148D6FdEaCD6c765F8f59D4074109E311f0c0000000000000000000000000000000000000000000000000000000000000001&fee_price=100000000000&fee_limit=21000", operation.buildUri().toString())

        assertEquals("c60", operation.buildAssetId(60))
        assertEquals("c60_t0x6B175474E89094C44Da98b954EedeAC495271d0F", operation.buildAssetId(60, "0x6B175474E89094C44Da98b954EedeAC495271d0F"))
    }

    @Test
    fun testTransferSendOperation() {
        val operation = TransferOperation.Builder()
                .action(ActionType.SEND)
                .callbackScheme("app_scheme")
                .callbackHost("callback")
                .coin(1001)
                .to("0xF36f148D6FdEaCD6c765F8f59D4074109E311f0c")
                .amount(BigDecimal("1"))
                .build()

        assertEquals("trust://sdk_transaction?action=transfer&asset=c1001&to=0xF36f148D6FdEaCD6c765F8f59D4074109E311f0c&amount=1&nonce=-1&app=app_scheme&callback=callback&confirm_type=send&id=1", operation.buildUri().toString())
    }

    @Test
    fun testTransferFromOperation() {
        val operation = TransferOperation.Builder()
                .action(ActionType.SEND)
                .callbackScheme("app_scheme")
                .coin(195)
                .to("TWLUfM2y4wW1HZqrRKfz2cksF7sEbYfSj4")
                .from("TKAiGtx6Da2ZJskZQV5RDEkgs9Sd2jJDDC")
                .amount(BigDecimal("1"))
                .build()

        assertEquals("trust://sdk_transaction?action=transfer&asset=c195&to=TWLUfM2y4wW1HZqrRKfz2cksF7sEbYfSj4&amount=1&nonce=-1&app=app_scheme&callback=tx_callback&confirm_type=send&id=1&from=TKAiGtx6Da2ZJskZQV5RDEkgs9Sd2jJDDC", operation.buildUri().toString())
    }

    @Test
    fun testTransferOperationActionRequired() {
        try {
            TransferOperation.Builder()
                    .callbackScheme("app_scheme")
                    .coin(60)
                    .to("0xF36f148D6FdEaCD6c765F8f59D4074109E311f0c")
                    .amount(BigDecimal("1"))
                    .build()
            fail("Assert action param required")
        } catch (ex: IllegalArgumentException) {
            assertEquals("java.lang.IllegalArgumentException: 'action' param required", ex.message)
        }
    }

    @Test
    fun testTransferOperationCallbackRequired() {
        try {
            TransferOperation.Builder()
                    .action(ActionType.SIGN)
                    .coin(60)
                    .to("0xF36f148D6FdEaCD6c765F8f59D4074109E311f0c")
                    .amount(BigDecimal("1"))
                    .build()
            fail("Assert callback param required")
        } catch (ex: IllegalArgumentException) {
            assertEquals("java.lang.IllegalArgumentException: 'scheme' param required", ex.message)
        }
    }

    @Test
    fun testTransferOperationCoinRequired() {
        try {
            TransferOperation.Builder()
                    .action(ActionType.SIGN)
                    .callbackScheme("app_scheme")
                    .to("0xF36f148D6FdEaCD6c765F8f59D4074109E311f0c")
                    .amount(BigDecimal("1"))
                    .build()
            fail("Assert coin param required")
        } catch (ex: IllegalArgumentException) {
            assertEquals("java.lang.IllegalArgumentException: 'coin' param required", ex.message)
        }
    }

    @Test
    fun testTransferOperationToRequired() {
        try {
            TransferOperation.Builder()
                    .action(ActionType.SIGN)
                    .callbackScheme("app_scheme")
                    .coin(60)
                    .amount(BigDecimal("1"))
                    .build()
            fail("Assert to param required")
        } catch (ex: IllegalArgumentException) {
            assertEquals("java.lang.IllegalArgumentException: 'to' param required", ex.message)
        }
    }

    @Test
    fun testTransferOperationAmountRequired() {
        try {
            TransferOperation.Builder()
                    .action(ActionType.SIGN)
                    .callbackScheme("app_scheme")
                    .coin(60)
                    .to("0xF36f148D6FdEaCD6c765F8f59D4074109E311f0c")
                    .build()
            fail("Assert amount param required")
        } catch (ex: IllegalArgumentException) {
            assertEquals("java.lang.IllegalArgumentException: 'amount' param required", ex.message)
        }
    }

    @Test
    fun testHandleTransferSendResult() {
        val intent = Intent()
        intent.data = Uri.parse("app_scheme://tx_callback?action=transfer&transaction_hash=0xF36f148D6FdEaCD6c765F8f59D4074109E311f0c")

        val result = Trust.handleOperationResult(intent)

        assertEquals("0xF36f148D6FdEaCD6c765F8f59D4074109E311f0c", result!!.data)
        assertNull(result.error)
    }

    @Test
    fun testHandleTransferSignResult() {
        val intent = Intent()
        intent.data = Uri.parse("app_scheme://tx_callback?action=transfer&transaction_sign=0xF36f148D6FdEaCD6c765F8f59D4074109E311f0c")

        val result = Trust.handleOperationResult(intent)

        assertEquals("0xF36f148D6FdEaCD6c765F8f59D4074109E311f0c", result!!.data)
        assertNull(result.error)
    }

    @Test
    fun testHandleTransferCancelResult() {
        val intent = Intent()
        intent.data = Uri.parse("app_scheme://tx_callback?action=transfer&cancel=cancel")

        val result = Trust.handleOperationResult(intent)

        assertEquals(OperationError.CANCEL, result!!.error)
        assertNull(result.data)
    }

    @Test
    fun testHandleTransferUnknownErrorResult() {
        val intent = Intent()
        intent.data = Uri.parse("app_scheme://tx_callback?action=transfer&cancel=noclue")

        val result = Trust.handleOperationResult(intent)

        assertEquals(OperationError.UNKNOWN, result!!.error)
        assertNull(result.data)
    }

    @Test
    fun testHandleTransferAccountErrorResult() {
        val intent = Intent()
        intent.data = Uri.parse("app_scheme://tx_callback?action=transfer&cancel=wrong_account")

        val result = Trust.handleOperationResult(intent)

        assertEquals(OperationError.WRONG_ACCOUNT, result!!.error)
        assertNull(result.data)
    }
}
