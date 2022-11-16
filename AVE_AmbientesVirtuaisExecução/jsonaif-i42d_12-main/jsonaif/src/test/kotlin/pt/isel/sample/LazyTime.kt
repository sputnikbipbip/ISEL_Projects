package pt.isel.sample

import pt.isel.JsonConvert

data class LazyTime(@property:JsonConvert(JsonToTime::class)var time : Int = 0, var nr : Int = 0)