package pt.isel.sample

import pt.isel.JsonConvert
import pt.isel.JsonProperty

data class Transaction (@JsonConvert(JsonToInt::class)val amount: Int, @JsonProperty("Person")var dest: Person? = null)
