package pt.isel

import kotlin.reflect.*
import kotlin.reflect.full.*

object JsonParserReflect  : AbstractJsonParser() {

    /**
     * For each domain class we keep a Map<String, Setter> relating properties names with their setters.
     * This is for Part 2 of Jsonaif workout.
     */
    private val setters = mutableMapOf<KClass<*>, Map<String, Setter>>()
    
    override fun parsePrimitive(tokens: JsonTokens, klass: KClass<*>): Any? {
        val primitiveParser: (String) -> Any = basicParser[klass]
            ?: throw Exception("Primitive $klass doesn't have a parser")
        val primitive = tokens.popWordPrimitive()
        return primitiveParser(primitive)
    }

    override fun parseObject(tokens: JsonTokens, klass: KClass<*>): Any? {
        val args = mutableMapOf<KParameter, Any?>()
        val receiver = klass.constructors.singleOrNull {
            it.parameters.all(KParameter::isOptional)
        }.let {
            try {
                if (it != null)
                    klass.createInstance() else args
            } catch (e: Exception) {
                throw Exception("Error creating instance of type $klass")
            }
        }
        tokens.pop(OBJECT_OPEN)
        tokens.trim()
        if (!setters.contains(klass)) {
            if (receiver is MutableMap<*,*>) {
                createSetterParameter(klass)
            } else {
                createSetterProperty(klass)
            }
        }
        while (tokens.current != OBJECT_END) {
            val propertyName = tokens.popWordFinishedWith(COLON)
            setters[klass]?.get(propertyName)?.apply(receiver, tokens)
            if (tokens.current == COMMA) tokens.pop(COMMA) else break
            tokens.trim()
        }
        tokens.pop(OBJECT_END)

        if (receiver is MutableMap<*,*>)
            return klass.primaryConstructor?.callBy(args)
        return receiver
    }

    private fun createSetterProperty(klass: KClass<*>){
        val map = mutableMapOf<String, Setter>()
        klass
            .memberProperties
            .forEach {
                val prop = it.findAnnotation<JsonProperty>()?.propertyName ?: it.name
                map.putIfAbsent(prop, object : Setter {
                    override fun apply(target: Any, tokens: JsonTokens) {
                        var value = parse(tokens, it.returnType.classifier as KClass<*>)
                        if (it.hasAnnotation<JsonConvert>()) {
                            val converter = it.findAnnotation<JsonConvert>()?.converter?.createInstance()
                            val method = converter!!::class.java.declaredMethods.first()
                            value = method.invoke(converter, value)
                        }
                        if (it is KMutableProperty<*>)
                            it.setter.call(target, value)
                    }
                })
            }
        setters[klass] = map.toMap()
    }

    private fun createSetterParameter(klass: KClass<*>){
        val map = mutableMapOf<String, Setter>()
        klass
            .primaryConstructor
            ?.parameters
            ?.forEach{
                val prop = it.findAnnotation<JsonProperty>()?.propertyName ?: it.name
                if (prop != null) {
                    map.putIfAbsent(prop, object : Setter {
                        override fun apply(target: Any, tokens: JsonTokens){
                            val type = it.type.arguments.getOrNull(0)?.type?.classifier ?:
                                it.type.classifier
                            var value = parse(tokens, type as KClass<*>)
                            if (it.hasAnnotation<JsonConvert>()) {
                                val converter = it.findAnnotation<JsonConvert>()?.converter?.createInstance()
                                val method = converter!!::class.java.declaredMethods.first()
                                value = method.invoke(converter, value)
                            }
                            target as MutableMap<KParameter, Any?>
                            target.put(it, value)
                        }
                    })
                }
            }
        setters[klass] = map.toMap()
    }
}