package pt.isel

import kotlin.reflect.KClass


abstract class AbstractJsonParser : JsonParser {

    override fun parse(source: String, klass: KClass<*>): Any? {
        return parse(JsonTokens(source), klass)
    }

    fun parse(tokens: JsonTokens, klass: KClass<*>) = when (tokens.current) {
        OBJECT_OPEN -> parseObject(tokens, klass)
        ARRAY_OPEN -> parseArray(tokens, klass)
        DOUBLE_QUOTES -> parseString(tokens)
        else -> parsePrimitive(tokens, klass)
    }

    abstract fun parsePrimitive(tokens: JsonTokens, klass: KClass<*>): Any?

    abstract fun parseObject(tokens: JsonTokens, klass: KClass<*>): Any?

    private fun parseString(tokens: JsonTokens): String {
        tokens.pop(DOUBLE_QUOTES) // Discard double quotes "
        return tokens.popWordFinishedWith(DOUBLE_QUOTES)
    }

    private fun parseArray(tokens: JsonTokens, klass: KClass<*>): List<Any?> {
        val list = mutableListOf<Any?>()
        tokens.pop(ARRAY_OPEN) // Discard square brackets [ ARRAY_OPEN
        while (tokens.current != ARRAY_END) {
            val v = parse(tokens, klass)
            list.add(v)
            if (tokens.current == COMMA) // The last element finishes with ] rather than a comma
                tokens.pop(COMMA) // Discard COMMA
            else break
            tokens.trim()
        }
        tokens.pop(ARRAY_END) // Discard square bracket ] ARRAY_END
        return list;
    }

    inline fun <reified T : Any> parseSequence(json : String) : Sequence<T?> {
        val jsonToken = JsonTokens(json)
        return sequence {
            jsonToken.pop(ARRAY_OPEN)
            while (jsonToken.current != ARRAY_END) {
                val v = parse(jsonToken, T::class)
                yield(v as T)
                if (jsonToken.current == COMMA)
                    jsonToken.pop(COMMA)
                else break
                jsonToken.trim()
            }
            jsonToken.pop(ARRAY_END)
        }
    }
}
