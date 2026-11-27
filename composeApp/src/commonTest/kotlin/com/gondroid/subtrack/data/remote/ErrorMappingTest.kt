package com.gondroid.subtrack.data.remote

import com.gondroid.subtrack.core.result.AppResult
import com.gondroid.subtrack.core.result.DataError
import com.gondroid.subtrack.core.result.isConnectivity
import com.gondroid.subtrack.platform.RemoteFailure
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ErrorMappingTest {

    @Test fun successWrapsTheValue() = runTest {
        assertEquals(AppResult.Success(42), dataCall { 42 })
    }

    @Test fun classifiedBridgeFailuresKeepTheirError() = runTest {
        val result = dataCall { throw RemoteFailure(DataError.Network) }
        assertEquals(AppResult.Failure(DataError.Network), result)
        assertTrue(DataError.Network.isConnectivity)
        assertTrue(DataError.Timeout.isConnectivity)
    }

    @Test fun anyOtherExceptionIsUnknown() = runTest {
        val result = dataCall { error("boom") }
        assertEquals(AppResult.Failure(DataError.Unknown("boom")), result)
        assertFalse(DataError.Unknown("boom").isConnectivity)
    }
}
