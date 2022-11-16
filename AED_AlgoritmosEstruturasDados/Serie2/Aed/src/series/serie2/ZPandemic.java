package series.serie2;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class ZPandemic {

    private static Map<String, Person> people = new HashMap<>();
    private static Map<String, District> district = new HashMap<>();

    public static void main(String[] args) throws IOException {
        BufferedReader fileInput = new BufferedReader(new FileReader(args[0]));
        Scanner scanner = new Scanner(fileInput);
        insertInformation(scanner);
        userInterface();
    }

    public static void insertInformation(Scanner in) {
        int filePersons = Integer.parseInt(in.nextLine().trim());
        while(in.hasNext()) {
            String subject = in.nextLine();
            String[] values = subject.split(" ");
            Person tested = new Person(Integer.parseInt(values[0]),values[1],values[2].charAt(0), values[3]);
            District districtAux = district.get(values[1]);
            if(districtAux == null) {
                districtAux = new District(values[1]);
                district.put(values[1], districtAux);
            }
            switch (values[2].charAt(0)) {
                case 'I':
                    districtAux.incrementInfected(districtAux.getInfected());
                    break;
                case 'R':
                    districtAux.incrementResistant(districtAux.getResistant());
                    break;
                case 'S':
                    districtAux.incrementSusceptible(districtAux.getSusceptible());
                    break;
                default:
                    System.out.println("Invalid clinical state on the file");
                    break;
            }
            people.put(values[0], tested);
        }
    }

    public static void userInterface() throws IOException {
        boolean on = true;
        while(on) {
            String screen = "\n##########Insert your choice########## \n" +
                    "Add new file ?\n--->Type: add yourFileName\n\n" +
                    "Show the information of all Districts ?\n--->Type: town_hall\n\n" +
                    "Get k districts with more infected ?\n--->Type: top k\n\n" +
                    "Shut down masterPiece ?\n--->Type: exterminate\n";
            Scanner in = new Scanner(System.in);
            String answer = ask(screen, in);
            answer.trim();
            String[] aux = answer.split(" ");
            switch (aux[0]) {
                case "add":
                    BufferedReader getThem = new BufferedReader(new FileReader(aux[1]));
                    Scanner populate = new Scanner(getThem);
                    insertInformation(populate);
                    break;
                case "town_hall":
                    district.entrySet().forEach(entry->{
                        System.out.println("" + entry.getValue().toString());
                    });
                    break;
                case "top":
                    getKBiggest(Integer.parseInt(aux[1]));
                    break;
                case "exterminate":
                    System.out.println("Switching off");
                    try {
                        TimeUnit.SECONDS.sleep(2);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    on = false;
                    break;
                default:
                    System.out.println("OH NO, it's not valid! Please try again");
            }
        }
    }

    private static String ask(String message, Scanner in) {
        System.out.println(message);
        String userInput = in.nextLine();
        return userInput;
    }

    //TODO: Penso que era a isto que a Professora se referia
    private static void getKBiggest(int k) {
        District[] values = new District[district.size()];
        int counter = 0;
        for(District d: district.values()) {
            values[counter++] = d;
        }
        Comparator<District> cmp = new Comparator<>() {
            @Override
            public int compare(District district, District t1) {
                return district.getInfected() - t1.getInfected();
            }

            @Override
            public boolean equals(Object o) {
                return false;
            }
        };
        buildMaxHeap(values, district.size(), cmp);
        for(int i = 0; i < k; ++i){
            System.out.println(values[i]);
        }
    }

    public static <E> void buildMaxHeap(District[] h, int n, Comparator<District> cmp) {
        for (int i = n / 2 - 1; i >= 0; i--) {
            if(h[i]!=null) maxHeapify(h, i, n, cmp);
        }
    }

    public static <E> void maxHeapify(District[] h, int i, int n, Comparator<District> cmp) {
        int l = 2 * i + 1;
        int r = 2 * i + 2;
        int largest;
        if (l < n && cmp.compare(h[l], h[i]) > 0)
            largest = l;
        else largest = i;
        if (r < n && cmp.compare(h[r], h[largest]) > 0)
            largest = r;
        if (largest != i) {
            exchange(h, i, largest);
            maxHeapify(h, largest, n, cmp);
        }
    }

    private static <E> void exchange(District[] h, int i, int j) {
        District aux = h[i];
        h[i] = h[j];
        h[j] = aux;
    }
}
