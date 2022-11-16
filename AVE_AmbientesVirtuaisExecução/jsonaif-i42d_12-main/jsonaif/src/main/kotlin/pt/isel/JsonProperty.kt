package pt.isel

@Target(AnnotationTarget.PROPERTY, AnnotationTarget.VALUE_PARAMETER)
annotation class JsonProperty(val propertyName : String)
