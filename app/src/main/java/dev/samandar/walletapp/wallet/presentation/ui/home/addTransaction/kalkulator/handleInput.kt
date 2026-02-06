package dev.samandar.walletapp.wallet.presentation.ui.home.addTransaction.kalkulator

fun handleInput(current: String, input: String): String {
    val operators = listOf("+", "-", "×", "÷")
    val safeCurrent = current.ifEmpty { "0" }

    // Faqat bitta sonning (operand) limiti
    val MAX_OPERAND_LENGTH = 9

    return when {
        input == "C" -> "0"
        input == "⌫" -> if (safeCurrent.length <= 1 || safeCurrent == "0") "0" else safeCurrent.dropLast(1)

        else -> {
            val lastPart = safeCurrent.split(*operators.toTypedArray()).last()

            when {
                // 1. Operatorlar: Cheksiz qo'shish mumkin
                input in operators -> {
                    if (safeCurrent.last().toString() in operators) {
                        safeCurrent.dropLast(1) + input
                    } else {
                        safeCurrent + input
                    }
                }

                // 2. Nuqta
                input == "." -> {
                    if (lastPart.contains(".") || lastPart.length >= MAX_OPERAND_LENGTH) safeCurrent
                    else safeCurrent + input
                }

                // 3. Raqamlar (Faqat bitta sonni cheklaymiz)
                else -> {
                    val inputLen = if (input == "000") 3 else 1

                    // Agar joriy son (operand) 9 tadan oshsa, bloklaymiz
                    if (lastPart.length + inputLen > MAX_OPERAND_LENGTH && safeCurrent != "0") {
                        return safeCurrent
                    }

                    if (safeCurrent == "0") {
                        if (input == "000") "0" else input
                    } else {
                        safeCurrent + input
                    }
                }
            }
        }
    }
}