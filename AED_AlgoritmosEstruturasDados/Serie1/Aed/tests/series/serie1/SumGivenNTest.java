package series.serie1;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static series.serie1.Arrays.sumGivenN;


public class SumGivenNTest {


    @Test
    public void sumGivenN_onZero(){
        assertEquals( 1, sumGivenN( 0 ) );
    }


    @Test
    public void sumGivenN_onGiven24(){
        assertEquals( 2, sumGivenN( 24 ) );
    }

    @Test
    public void sumGivenN_onGiven25(){
        assertEquals( 3, sumGivenN( 25 ) );
    }

    @Test
    public void sumGivenN_onGiven26(){
        assertEquals( 2, sumGivenN( 26 ) );
    }

    @Test
    public void sumGivenN_onGiven27(){
        assertEquals( 4, sumGivenN( 27 ) );
    }

    @Test
    public void sumGivenN_onGiven28(){ assertEquals( 2, sumGivenN( 28 ) ); }


}
