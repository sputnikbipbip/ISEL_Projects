package pt.isel

data class Person (@property:JsonConvert(JsonToInt::class)var id: Int = 0,
                   @property:JsonProperty("nome")var name: String? = null,
                   @property:JsonConvert(JsonToDate::class)
                   @property:JsonProperty("birth_date")
                   var birth: Date? = null,
                   var sibling: Person? = null)
