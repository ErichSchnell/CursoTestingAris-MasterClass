package com.erichschnell.testingapp.domain.ex

import com.erichschnell.testingapp.domain.models.Promotion
import java.time.Instant

fun List<Promotion>.activeAt(now: Instant): List<Promotion> =
    this.filter {
        it.startTime <= now && it.endTime >= now
    }
