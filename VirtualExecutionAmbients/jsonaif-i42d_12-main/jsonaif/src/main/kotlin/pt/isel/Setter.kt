package pt.isel

interface Setter {
    fun apply(target: Any, tokens: JsonTokens)
}
