package pt.isel

data class Student (@property:JsonConvert(JsonToInt::class)var nr: Int = 0, @property:JsonProperty("nome") var name: String? = null)
