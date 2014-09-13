package org.jtalks.pochta

import kotlin.test.assertEquals
import org.jetbrains.spek.api.Spek

class SampleSpecs : Spek() {{

    given("a string with default locale settings") {
        val string = "OloLo"
        on("lowercasing") {
            val value = string.toLowerCase()
            it("should result in a string with lowercased chars only") {
                assertEquals("ololo", value)
            }
        }
    }
}}
