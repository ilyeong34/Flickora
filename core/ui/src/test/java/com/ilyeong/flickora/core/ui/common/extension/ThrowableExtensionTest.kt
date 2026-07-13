package com.ilyeong.flickora.core.ui.common.extension

import com.ilyeong.flickora.core.model.FlickoraError
import com.ilyeong.flickora.core.ui.R
import org.junit.Assert.assertEquals
import org.junit.Test

class ThrowableExtensionTest {

    @Test
    fun `Flickora errors map to their message resources`() {
        assertEquals(R.string.error_network, FlickoraError.Network().toMessageResId())
        assertEquals(R.string.error_authentication, FlickoraError.Authentication().toMessageResId())
        assertEquals(R.string.error_not_found, FlickoraError.NotFound().toMessageResId())
        assertEquals(R.string.error_server, FlickoraError.Server().toMessageResId())
        assertEquals(R.string.error_unknown, FlickoraError.Unknown().toMessageResId())
    }

    @Test
    fun `unexpected throwable maps to unknown message`() {
        assertEquals(R.string.error_unknown, IllegalStateException().toMessageResId())
    }

    @Test(expected = AssertionError::class)
    fun `Error is rethrown`() {
        AssertionError().toMessageResId()
    }
}
