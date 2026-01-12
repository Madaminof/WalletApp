package dev.samandar.walletapp.utils

fun Double?.orZero(): Double = 0.0

val Double?.orZero: Double
    get() = this ?: 0.0

val Long?.orZero: Long
    get() = this ?: 0L

val Int?.orZero: Int
    get() = this ?: 0

val String.Companion.EMPTY: String
    get() = ""

val String.Companion.EMPTY_SPACE: String
    get() = " "
val String.Companion.COMMA: String
    get() = ","