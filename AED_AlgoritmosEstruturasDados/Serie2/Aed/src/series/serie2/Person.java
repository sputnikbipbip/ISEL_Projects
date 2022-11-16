package series.serie2;

public class Person {

    private int id;
    private String district;
    private char clinicalState;
    private String date;
    private String contactedPersons;


    public Person(int id, String district, char clinicalState, String date) {
        this.id = id;
        this.district = district;
        this.clinicalState = clinicalState;
        this.date = date;
    }

    public String toString() {
        return "PersonId = "+id+" District = "+district;
    }

}
