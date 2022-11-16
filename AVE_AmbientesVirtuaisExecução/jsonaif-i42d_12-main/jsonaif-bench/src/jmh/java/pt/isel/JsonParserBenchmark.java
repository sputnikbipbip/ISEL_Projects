package pt.isel;

import kotlin.jvm.JvmClassMappingKt;
import kotlin.reflect.KClass;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;

import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class JsonParserBenchmark {
    static final JsonParserDynamic jsonParserDynamic = JsonParserDynamic.INSTANCE;
    static final JsonParserReflect jsonParserReflect = JsonParserReflect.INSTANCE;

    static final String student = "{ nome: \"Ze Manel\", nr: \"7353\"}";
    static final KClass<Student> studentKlass = JvmClassMappingKt.getKotlinClass(Student.class);

    static final String date = "{ day: 25, month: 3, year: 1974}";

    static final KClass<Date> dateKlass = JvmClassMappingKt.getKotlinClass(Date.class);


    static final String studentGroup = "{ student1: { nome: \"Maria Leal\", nr: \"735344\"}, student2: { nome: \"Salvador Sobral\", nr: \"1\"}}";

    static final KClass<StudentGroup> studentGroupKlass = JvmClassMappingKt.getKotlinClass(StudentGroup.class);

    static final String studentContact = "{ student: { nome: \"Ze Manel\", nr: \"7353\"}, contact: 92525752}";

    static final KClass<StudentContact> studentContactKlass = JvmClassMappingKt.getKotlinClass(StudentContact.class);


    @Benchmark
    public void benchParseStudentViaDynamic() {
        jsonParserDynamic.parse(student, studentKlass);
    }

    @Benchmark
    public void benchParseStudentViaReflection() {
        jsonParserReflect.parse(student, studentKlass);
    }

    @Benchmark
    public void primitiveTypesParseViaDynamic() {
        jsonParserDynamic.parse(date, dateKlass);
    }

    @Benchmark
    public void primitiveTypesParseViaReflection() {
        jsonParserReflect.parse(date, dateKlass);
    }

    @Benchmark
    public void referenceTypesParseViaDynamic() {
        jsonParserDynamic.parse(studentGroup, studentGroupKlass);
    }

    @Benchmark
    public void referenceTypesParseViaReflection() {
        jsonParserReflect.parse(studentGroup, studentGroupKlass);
    }

    @Benchmark
    public void referenceAndPrimitiveTypesParseViaDynamic() {
        jsonParserDynamic.parse(studentContact, studentContactKlass);
    }

    @Benchmark
    public void referenceAndPrimitiveTypesParseViaReflection() {
        jsonParserReflect.parse(studentContact, studentContactKlass);
    }

}
