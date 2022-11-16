package pt.isel

import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.TypeSpec
import javax.lang.model.element.Modifier
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation

/**
 * Generate a class that implements Setter similar to:
 *
 * if (prop.hasAnnotation()) v = () it.findAnnotation<JsonConvert>()?.converter?.createInstance() converter!!::class.java.declaredMethods.first() value = method.invoke(converter, value)
 *
 * class SetterStudent_nr implements Setter {
 *    apply(target: Any, tokens: JsonTokens) {
 *      int v = Integer.parseInt(tokens.popWordPrimitives());
 *      ((Student) target).setNr(v);
 *    }
 * }
 */

fun buildSetterProperty(klass: KClass<*>, prop: KMutableProperty<*>) : JavaFile {

    val propKlass = prop.returnType.classifier as KClass<*>
    val primitiveType = propKlass.javaObjectType.name
    val type = propKlass.java.name

    val hasAnnotation = prop.hasAnnotation<JsonConvert>()
    val annotation = prop.findAnnotation<JsonConvert>()?.converter

    val statement = if (!propKlass.java.isPrimitive && !hasAnnotation) "\$T v = ($type) pt.isel.JsonParserDynamic.INSTANCE.parse(tokens, kotlin.jvm.JvmClassMappingKt.getKotlinClass($type.class))"
                else if (!hasAnnotation)"\$T v = $primitiveType.parse${propKlass.simpleName}(tokens.popWordPrimitive())"
                else "${(annotation as KClass<*>).java.name} converter = new ${annotation.createInstance()::class.java.name}();\n" +
                        "tokens.pop('\"');\n" +
                        "String str = tokens.popWordFinishedWith('\"');\n" +
                        "\$T v = converter.converter(str)"

    val apply = MethodSpec
        .methodBuilder("apply")
        .addModifiers(Modifier.PUBLIC)
        .addParameter(Any::class.java, "target")
        .addParameter(JsonTokens::class.java, "tokens")
        .addStatement(statement, propKlass.java)
        .addStatement("((${klass.qualifiedName}) target).set${prop.name.replaceFirstChar { it.uppercase() }}(v)")
        .build()

    val setter = TypeSpec
        .classBuilder("Setter${klass.simpleName}_${prop.name}")
        .addModifiers(Modifier.PUBLIC)
        .addSuperinterface(Setter::class.java)
        .addMethod(apply)
        .build()

    return JavaFile.builder("", setter).build()
}
