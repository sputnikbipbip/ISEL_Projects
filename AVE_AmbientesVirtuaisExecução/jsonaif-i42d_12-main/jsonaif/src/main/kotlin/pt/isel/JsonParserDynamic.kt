package pt.isel

import com.squareup.javapoet.JavaFile
import java.io.File
import java.net.URLClassLoader
import javax.tools.ToolProvider
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty
import kotlin.reflect.KParameter
import kotlin.reflect.full.*

object JsonParserDynamic : AbstractJsonParser(){
    private val root = File("./build")
    private val classLoader = URLClassLoader.newInstance(arrayOf(root.toURI().toURL()))
    private val compiler = ToolProvider.getSystemJavaCompiler()

    private val setters = mutableMapOf<KClass<*>, Map<String, Setter>>()

    override fun parsePrimitive(tokens: JsonTokens, klass: KClass<*>): Any? {
        val primitiveParser: (String) -> Any = basicParser[klass]
            ?: throw Exception("Primitive $klass doesn't have a parser")
        val primitive = tokens.popWordPrimitive()
        return primitiveParser(primitive)
    }

    override fun parseObject(tokens: JsonTokens, klass: KClass<*>): Any? {
        val receiver = klass.constructors.singleOrNull {
            it.parameters.all(KParameter::isOptional)
        }.let {
            try {
                if (it != null)
                    klass.createInstance() else throw Exception()
            } catch (e: Exception) {
                throw Exception("$klass doesn't have a constructor with no parameters or all parameters optional")
            }
        }
        tokens.pop(OBJECT_OPEN)
        tokens.trim()
        if (!setters.contains(klass)) {
            try {
                createSetterProperty(klass)
            } catch (e : Exception) {
                throw Exception("Properties of type $klass arent Mutable")
            }
        }
        while (tokens.current != OBJECT_END) {
            val propertyName = tokens.popWordFinishedWith(COLON)
            setters[klass]?.get(propertyName)?.apply(receiver, tokens)
            if (tokens.current == COMMA) tokens.pop(COMMA) else break
            tokens.trim()
        }
        tokens.pop(OBJECT_END)
        return receiver
    }

    private fun createSetterProperty(klass: KClass<*>){
        val map = mutableMapOf<String, Setter>()
        klass
            .memberProperties
            .forEach {
                val prop = it.findAnnotation<JsonProperty>()?.propertyName ?: it.name
                if (it is KMutableProperty<*>) {
                    val setterFile = buildSetterProperty(klass, it)
                    map.putIfAbsent(prop, loadAndCreateInstance(setterFile) as Setter)
                }
                else throw Exception()

            }
        setters[klass] = map.toMap()
    }

    private fun loadAndCreateInstance(source: JavaFile): Any {
        // Save source in .java file.
        source.writeToFile(root)

        // Compile source file.
        compiler.run(null, null, null, "${root.path}/${source.typeSpec.name}.java")

        // Load and instantiate compiled class.
        return classLoader
            .loadClass(source.typeSpec.name)
            .getDeclaredConstructor()
            .newInstance()
    }
}