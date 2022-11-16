package pt.isel

class JsonToDate {
    fun converter(value: String) : Date {
        val values = value.split("-")
        return Date(values[2].toInt(), values[1].toInt(), values[0].toInt())
    }
}