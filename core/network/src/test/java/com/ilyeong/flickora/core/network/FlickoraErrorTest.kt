package com.ilyeong.flickora.core.network

import com.ilyeong.flickora.core.model.FlickoraError
import com.ilyeong.flickora.core.network.di.toFlickoraError
import org.junit.Assert.assertTrue
import org.junit.Test

class FlickoraErrorTest {

    @Test
    fun `authentication status codes map to Authentication`() {
        assertTrue(401.toFlickoraError() is FlickoraError.Authentication)
        assertTrue(403.toFlickoraError() is FlickoraError.Authentication)
    }

    @Test
    fun `not found status code maps to NotFound`() {
        assertTrue(404.toFlickoraError() is FlickoraError.NotFound)
    }

    @Test
    fun `server status codes map to Server`() {
        assertTrue(500.toFlickoraError() is FlickoraError.Server)
        assertTrue(599.toFlickoraError() is FlickoraError.Server)
    }

    @Test
    fun `other status codes map to Unknown`() {
        assertTrue(400.toFlickoraError() is FlickoraError.Unknown)
        assertTrue(429.toFlickoraError() is FlickoraError.Unknown)
    }
}
