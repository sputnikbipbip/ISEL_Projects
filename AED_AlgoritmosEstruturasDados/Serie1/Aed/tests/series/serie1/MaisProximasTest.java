package series.serie1;


import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MaisProximasTest {

    public static void main (String [] args) throws IOException{
        long initTime = System.currentTimeMillis();
        String f1path = "C:\\Users\\azeve\\Desktop\\LEIC\\Universidade\\4º Semestre\\AED\\FilesEx3\\f1.txt";
        String f2path = "C:\\Users\\azeve\\Desktop\\LEIC\\Universidade\\4º Semestre\\AED\\FilesEx3\\f2.txt";
        String f3path = "C:\\Users\\azeve\\Desktop\\LEIC\\Universidade\\4º Semestre\\AED\\FilesEx3\\f3.txt";
        String outpath = "C:\\Users\\azeve\\Desktop\\LEIC\\Universidade\\4º Semestre\\AED\\FilesEx3\\output.txt";
        String[] s = {"1000000", "aa", outpath, f1path, f2path, f3path};
        MaisProximas.main(s);

        long elapsedTime = System.currentTimeMillis()-initTime;

        System.out.println(new SimpleDateFormat("mm:ss.SSS").format(new Date(elapsedTime)));
    }
}