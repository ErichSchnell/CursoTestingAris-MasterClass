package com.erichschnell.testingapp.data.util

import com.erichschnell.testingapp.domain.util.Clock
import java.time.Instant
import javax.inject.Inject

class SystemClock
    @Inject
    constructor() : Clock {
        override fun now(): Instant = Instant.now()
    }
