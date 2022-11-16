package pt.isel

import pt.isel.sample.*
import kotlin.test.Test
import kotlin.test.assertEquals

class JsonParserTest {

    @Test fun parseSimpleObjectViaProperties() {
        val json = "{ nome: \"Ze Manel\", nr: \"7353\"}"
        val student = JsonParserReflect.parse(json, Student::class) as Student
        assertEquals("Ze Manel", student.name)
        assertEquals(7353, student.nr)
    }

    @Test fun parseSimpleObjectViaConstructor() {
        val json = "{ id: 94646, nome: \"Ze Manel\"}"
        val p = JsonParserReflect.parse(json, Person::class) as Person
        assertEquals(94646, p.id)
        assertEquals("Ze Manel", p.name)
    }

    @Test fun parseComposeObject() {
        val json = "{ id: 94646, nome: \"Ze Manel\", birth_date: \"1999-09-19\", sibling: { id: 94235, nome: \"Kata Badala\"}}"
        val p = JsonParserReflect.parse(json, Person::class) as Person
        assertEquals(94646, p.id)
        assertEquals("Ze Manel", p.name)
        assertEquals(19, p.birth?.day)
        assertEquals(9, p.birth?.month)
        assertEquals(1999, p.birth?.year)
    }

    @Test fun parseArray() {
        val json = "[{id: 94335, nome: \"Ze Manel\"}, {id: 94265, nome: \"Candida Raimunda\"}, {id: 94237, nome: \"Kata Mandala\"}]";
        val ps = JsonParserReflect.parse(json, Person::class) as List<Person>
        assertEquals(3, ps.size)
        assertEquals("Ze Manel", ps[0].name)
        assertEquals("Candida Raimunda", ps[1].name)
        assertEquals("Kata Mandala", ps[2].name)
    }

    @Test fun parseClassroom() {
        val json = "{ students: [{nome: \"Ze Manel\", nr: \"7353\"}, {nome: \"Candida Raimunda\", nr: \"94265\"}, {nome: \"Kata Mandala\", nr: \"94237\"}]}"
        val c = JsonParserReflect.parse(json, Classroom::class) as Classroom
        assertEquals(7353, c.students[0].nr)
        assertEquals(94265, c.students[1].nr)
        assertEquals(94237, c.students[2].nr)
    }

    @Test fun parseAccount() {
        val json = "{ balance: \"1000\", transactions: [{amount: \"100\", Person: {id: 94335, nome: \"Ze Manel\"}}, {amount: \"120\", Person: {id: 94265, nome: \"Candida Raimunda\"}}]}"
        val a = JsonParserReflect.parse(json, Account::class) as Account
        assertEquals(1000, a.balance)
        assertEquals(2, a.transactions.size)
        assertEquals(120, a.transactions[1].amount)
        assertEquals(94335, a.transactions[0].dest?.id)
    }

    @Test fun parseSubscription() {
        val json = "{ date: { year: 2022, month: 4, day: 10}, account: { balance: \"1000\", transactions: [{amount: \"100\"}]}}"
        val s = JsonParserReflect.parse(json, Subscription::class) as Subscription
        assertEquals(10, s.date.day)
        assertEquals(4, s.date.month)
        assertEquals(2022, s.date.year)
        assertEquals(1000, s.account.balance)
        assertEquals(100, s.account.transactions[0].amount)
    }


}
