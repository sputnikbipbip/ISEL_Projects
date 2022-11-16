package pt.isel

import org.junit.Test
import pt.isel.sample.LazyTime
import pt.isel.sample.Student
import java.lang.Thread.sleep
import java.time.LocalTime
import kotlin.test.assertEquals

class JsonGenericParserTest {

    @Test
    fun genericParserWithType() {
        val json = "{ nome: \"Ze Manel\", nr: \"7353\"}"
        val student = JsonParserDynamic.parse<Student>(json)
        assertEquals("Ze Manel", student.name)
        assertEquals(7353, student.nr)
    }

    @Test
    fun genericParserWithInferredType() {
        val json = "{ nome: \"Ze Manel\", nr: \"7353\"}"
        val s : Student = JsonParserDynamic.parse(json)
        assertEquals("Ze Manel", s.name)
        assertEquals(7353, s.nr)
    }

    @Test
    fun genericParserWithArray() {
        val json = "[{nr: \"94335\", nome: \"Ze Manel\"}, {nr: \"94265\", nome: \"Candida Raimunda\"}, {nr: \"94237\", nome: \"Kata Mandala\"}]";
        val ps = JsonParserDynamic.parseArray<Student>(json)
        assertEquals(3, ps.size)
        assertEquals("Ze Manel", ps[0].name)
        assertEquals("Candida Raimunda", ps[1].name)
        assertEquals("Kata Mandala", ps[2].name)
    }

    @Test
    fun lazyArrayParser() {
        val json = "[{nr: \"94335\", nome: \"Ze Manel\"}, {nr: \"94265\", nome: \"Candida Raimunda\"}, {nr: \"94237\", nome: \"Kata Mandala\"}]";
        val sequence = JsonParserDynamic.parseSequence<Student>(json)
        val iterator = sequence.iterator()
        assertEquals("Ze Manel", iterator.next()?.name)
        assertEquals("Candida Raimunda", iterator.next()?.name)
        assertEquals("Kata Mandala", iterator.next()?.name)
    }

    @Test
    fun lazyArrayParserWithTimer() {
        val json = "[{time: \"0\", nr: 1}, {time: \"0\", nr: 2}, {time: \"0\", nr: 3}]";
        val sequence = JsonParserReflect.parseSequence<LazyTime>(json)
        val iterator = sequence.iterator()
        var counter = 0
        while (iterator.hasNext()) {
            ++counter
            val timer = iterator.next()
            assertEquals(timer?.time, LocalTime.now().second)
            sleep(3000)
        }
        assertEquals(counter, 3)
    }

}