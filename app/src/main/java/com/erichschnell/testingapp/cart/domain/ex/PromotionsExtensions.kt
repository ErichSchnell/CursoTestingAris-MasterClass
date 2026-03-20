package com.erichschnell.testingapp.cart.domain.ex

import com.erichschnell.testingapp.productList.domain.models.Promotion
import java.time.Instant

fun List<Promotion>.activeAt(now: Instant):List<Promotion> = this.filter {
    it.startTime <= now && it.endTime >= now
}