package series.serie2;

public class District {

    private String district;
    private int infected = 0;
    private int resistant = 0;
    private int susceptible = 0;

    public District(String district) {

        this.district = district;
    }

    public String toString() {
        return "\nDistrict = "+district+"\nSusceptible = "+susceptible+"\nResistant =  "+resistant+"\nInfected = "+infected;
    }

    public String getDistrict(){
        return district;
    }

    public int getInfected() {
        return this.infected;
    }

    public int getResistant() {
        return resistant;
    }

    public int getSusceptible() {
        return susceptible;
    }

    public void incrementInfected(int previous) {
        this.infected = previous + 1;
    }

    public void incrementResistant(int previous) {
        this.resistant = previous + 1;
    }

    public void incrementSusceptible(int previous) {
        this.susceptible = previous + 1;
    }
}
