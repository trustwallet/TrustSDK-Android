package com.trustwallet.sdk;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import trust.Response;
import trust.SignTransactionRequest;
import trust.Trust;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ResponseTest {
    @Test
    public void testIsAvailable() {
        Response response = new Response(null, null, Trust.ErrorCode.NONE);
        assertFalse(response.isAvailable());
        response = new Response(null, null, Trust.ErrorCode.CANCELED);
        assertFalse(response.isAvailable());
        response = new Response(null, "0x", Trust.ErrorCode.NONE);
        assertFalse(response.isAvailable());
        response = new Response(SignTransactionRequest.builder().get(), "0x", Trust.ErrorCode.NONE);
        assertTrue(response.isAvailable());
        response = new Response(SignTransactionRequest.builder().get(), null, Trust.ErrorCode.NONE);
        assertFalse(response.isAvailable());
        response = new Response(SignTransactionRequest.builder().get(), "0x", Trust.ErrorCode.CANCELED);
        assertTrue(response.isAvailable());
    }
}
