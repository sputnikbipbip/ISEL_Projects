package pt.isel

const val OBJECT_OPEN = '{'
const val OBJECT_END = '}'
const val ARRAY_OPEN = '['
const val ARRAY_END = ']'
const val DOUBLE_QUOTES = '"'
const val COMMA = ','
const val COLON = ':'

class JsonTokens(json: String) {
    private val src = json.toCharArray()
    private var index = 0
    val current: Char
        get() = src[index]

    fun tryAdvance(): Boolean {
        index++
        return index < src.size
    }

    fun trim() {
        while (src[index] == ' ')
            if (!tryAdvance())
                break
    }

    fun pop(): Char {
        val token = src[index]
        index++
        return token
    }

    fun pop(expected: Char) {
        if (current != expected)
            throw Exception("Expected $expected but found $current")
        index++
    }

    fun popWordFinishedWith(delimiter: Char): String {
        trim()
        var acc = ""
        while (current != delimiter) {
            acc += current
            tryAdvance()
        }
        tryAdvance() // Discard delimiter
        trim()
        return acc
    }

    fun popWordPrimitive(): String {
        trim()
        var acc = ""
        while (!isEnd(current)) {
            acc += current
            tryAdvance()
        }
        trim()
        return acc
    }

    private fun isEnd(curr: Char): Boolean {
        return curr == OBJECT_END || curr == ARRAY_END || curr == COMMA
    }
}
