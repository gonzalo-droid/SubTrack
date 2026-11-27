package com.gondroid.subtrack.core.util

import kotlin.test.Test
import kotlin.test.assertEquals

class MoneyTest {
    @Test fun solesUseTheSymbolAndTwoDecimals() {
        assertEquals("S/ 54.90", 54.9.asMoney())
        assertEquals("S/ 0.00", 0.0.asMoney())
        assertEquals("S/ 13.73", (54.9 / 4).asMoney())
    }

    @Test fun otherCurrenciesUseTheirCode() {
        assertEquals("USD 9.99", 9.99.asMoney("USD"))
    }
}
