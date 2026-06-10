package com.erichschnell.testingapp.fakes

import com.erichschnell.testingapp.domain.util.Clock
import java.time.Instant

class FakeSystemClock : Clock {
    private var currentTime: Instant = Instant.now()

    fun setTime(time: Instant) {
        currentTime = time
    }

    fun advanceTime(seconds: Long) {
        currentTime = currentTime.plusSeconds(seconds)
    }

    override fun now(): Instant = currentTime
}
