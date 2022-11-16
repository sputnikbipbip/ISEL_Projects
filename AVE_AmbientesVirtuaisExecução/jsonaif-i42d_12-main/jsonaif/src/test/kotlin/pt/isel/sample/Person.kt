package pt.isel.sample

import pt.isel.JsonConvert
import pt.isel.JsonProperty

data class Person (val id: Int,
                   @JsonProperty("nome")val name: String,
                   @JsonConvert(JsonToDate::class)
                   @JsonProperty("birth_date")
                   val birth: Date? = null,
                   var sibling: Person? = null)
