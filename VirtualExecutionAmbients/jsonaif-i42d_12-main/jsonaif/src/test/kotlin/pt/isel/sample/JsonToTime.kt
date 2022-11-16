package pt.isel.sample

import java.time.LocalTime

class JsonToTime {

    fun converter(value: String) : Int {
        return LocalTime.now().second
    }
}