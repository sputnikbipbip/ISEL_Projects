package pt.isel

import kotlin.reflect.KClass

@Target(AnnotationTarget.PROPERTY, AnnotationTarget.VALUE_PARAMETER)
annotation class JsonConvert(val converter: KClass<*>)
