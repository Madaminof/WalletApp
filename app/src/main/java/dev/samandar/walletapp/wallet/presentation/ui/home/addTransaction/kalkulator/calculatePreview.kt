package dev.samandar.walletapp.wallet.presentation.ui.home.addTransaction.kalkulator


fun calculatePreview(expression: String): String {
    if (expression.isEmpty()) return "0"

    // Oxirgi belgi operator bo'lsa, uni olib tashlaymiz (masalan "10+" -> "10")
    val cleanExpression = if (expression.lastOrNull() in listOf('+', '-', '×', '÷')) {
        expression.dropLast(1)
    } else {
        expression
    }.replace("×", "*").replace("÷", "/")

    val result = SimpleMathParser.evaluate(cleanExpression)

    return if (result % 1 == 0.0) {
        result.toLong().toString() // Butun son bo'lsa (15.0 -> 15)
    } else {
        String.format("%.2f", result) // O'nlik son bo'lsa 2 ta xona (15.555 -> 15.56)
    }
}


object SimpleMathParser {
    fun evaluate(expression: String): Double {
        // Bo'sh bo'lsa yoki faqat "0" bo'lsa
        if (expression.isBlank() || expression == "0") return 0.0

        return try {
            val tokens = tokenize(expression)
            if (tokens.isEmpty()) return 0.0

            // 1-bosqich: Ko'paytirish va Bo'lish (MD)
            val afterMD = mutableListOf<Any>()
            var i = 0
            while (i < tokens.size) {
                val token = tokens[i]
                if (token is String && (token == "*" || token == "/")) {
                    val prev = afterMD.removeAt(afterMD.size - 1) as Double
                    val next = tokens[i + 1] as Double
                    val res = if (token == "*") prev * next else {
                        if (next == 0.0) 0.0 else prev / next // 0 ga bo'lishdan himoya
                    }
                    afterMD.add(res)
                    i += 2
                } else {
                    afterMD.add(token)
                    i++
                }
            }

            // 2-bosqich: Qo'shish va Ayirish (AS)
            var result = afterMD[0] as Double
            var j = 1
            while (j < afterMD.size) {
                val op = afterMD[j] as String
                val next = afterMD[j + 1] as Double
                result = if (op == "+") result + next else result - next
                j += 2
            }
            result
        } catch (e: Exception) {
            0.0
        }
    }

    private fun tokenize(expression: String): List<Any> {
        val tokens = mutableListOf<Any>()
        var number = ""
        // Faqat kerakli belgilarni qabul qilamiz
        val cleanExp = expression.replace(" ", "")

        for (char in cleanExp) {
            if (char in "0123456789.") {
                number += char
            } else if (char in "+-*/") {
                if (number.isNotEmpty()) {
                    tokens.add(number.toDouble())
                    number = ""
                }
                tokens.add(char.toString())
            }
        }
        if (number.isNotEmpty()) tokens.add(number.toDouble())
        return tokens
    }
}