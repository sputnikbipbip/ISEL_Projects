package pt.isel

import kotlin.reflect.KClass

interface JsonParser {

    fun parse(source: String, klass: KClass<*>): Any?

}


inline fun <reified T : Any> JsonParser.parse(json: String) : T {
    return parse(json, T::class) as T
}

inline fun <reified T : Any> JsonParser.parseArray(json : String) : List<T> {
    return parse(json, T::class) as List<T>
}


