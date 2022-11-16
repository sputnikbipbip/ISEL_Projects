package pt.isel.sample

import pt.isel.JsonConvert

data class Account (@JsonConvert(JsonToInt::class)val balance: Int, var transactions: ArrayList<Transaction>)