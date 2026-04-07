package com.erichschnell.testingapp.domain.util

import java.time.Instant

interface Clock {
    fun now(): Instant
}