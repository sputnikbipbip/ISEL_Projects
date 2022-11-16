package pt.isel.sample

import pt.isel.JsonConvert
import pt.isel.JsonProperty

data class Student (@property:JsonConvert(JsonToInt::class)var nr: Int = 0, @property:JsonProperty("nome") var name: String? = null)
