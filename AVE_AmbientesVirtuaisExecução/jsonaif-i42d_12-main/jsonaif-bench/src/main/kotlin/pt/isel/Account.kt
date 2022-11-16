package pt.isel

data class Account (@JsonConvert(JsonToInt::class)val balance: Int, var transactions: ArrayList<Transaction>)