package com.erichschnell.testingapp.fakes

import com.erichschnell.testingapp.domain.util.Clock
import io.mockk.impl.InternalPlatform.time
import java.time.Instant

class FakeSystemClock(): Clock {
    private var currentTime: Instant = Instant.now()

    fun setTime(time: Instant){ currentTime = time }

    fun advanceTime(seconds: Long){ currentTime = currentTime.plusSeconds(seconds) }

    fun retrocedTime(seconds: Long){ currentTime = currentTime.minusSeconds(seconds) }

    override fun now(): Instant = currentTime
}