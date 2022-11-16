package pt.isel

import org.junit.Test
import pt.isel.sample.*
import kotlin.test.assertEquals

class JsonParserDynamicTest {
    @Test
    fun parseSimpleObjectViaProperties() {
        val json = "{ nome: \"Ze Manel\", nr: \"7353\"}"
        val student = JsonParserDynamic.parse(json, Student::class) as Student
        assertEquals("Ze Manel", student.name)
        assertEquals(7353, student.nr)
    }

    @Test fun parseStudentGroup() {
        val json = "{ student1: { nome: \"Maria Leal\", nr: \"735344\"}, student2: { nome: \"Salvador Sobral\", nr: \"1\"}}"
        val s = JsonParserDynamic.parse(json, StudentGroup::class) as StudentGroup
        assertEquals("Maria Leal", s.student1?.name)
        assertEquals("Salvador Sobral", s.student2?.name)
    }

    @Test fun parseDate() {
        val json = "{ day: 25, month: 3, year: 1974}"
        val d = JsonParserDynamic.parse(json, Date::class) as Date
        assertEquals(25, d.day)
        assertEquals(3, d.month)
        assertEquals(1974, d.year)
    }

    @Test fun parseStudentContact() {
        val json = "{ student: { nome: \"Ze Manel\", nr: \"7353\"}, contact: 92525752}"
        val s = JsonParserDynamic.parse(json, StudentContact::class) as StudentContact
        assertEquals("Ze Manel", s.student?.name)
        assertEquals(7353, s.student?.nr)
        assertEquals(92525752, s.contact)
    }

}