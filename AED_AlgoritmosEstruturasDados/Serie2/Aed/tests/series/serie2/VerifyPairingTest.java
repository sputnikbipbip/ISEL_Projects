package series.serie2;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static series.serie2.Utils.verifyPairing;

public class VerifyPairingTest {

	@Test
	public void  verifyPairing_empty_String() {
		assertTrue( verifyPairing("") );
	}

	@Test
	public void  verifyPairing_simple_pair() {
		assertTrue( verifyPairing("{}") );
		assertTrue( verifyPairing("()") );
		assertTrue( verifyPairing("[]") );
		assertTrue( verifyPairing("..{...}") );
		assertTrue( verifyPairing(".(.).") );
		assertTrue( verifyPairing("[.]..") );
	}
	
	@Test
	public void  verifyPairing_open_only() {
		assertFalse( verifyPairing("{") );
		assertFalse( verifyPairing("[") );
		assertFalse( verifyPairing("(") );
		assertFalse( verifyPairing("..{..") );
		assertFalse( verifyPairing(".[") );
		assertFalse( verifyPairing("(.") );
	}

	@Test
	public void  verifyPairing_close_only() {
		assertFalse( verifyPairing("}") );
		assertFalse( verifyPairing(")") );
		assertFalse( verifyPairing("]") );
		assertFalse( verifyPairing("..}..") );
		assertFalse( verifyPairing(".)") );
		assertFalse( verifyPairing("].") );
	}
	
	@Test
	public void  verifyPairing_close_and_open_inverted() {
		assertFalse( verifyPairing("}{") );
		assertFalse( verifyPairing(")(") );
		assertFalse( verifyPairing("][") );
		assertFalse( verifyPairing("..}{..") );
		assertFalse( verifyPairing(".)(") );
		assertFalse( verifyPairing("][.") );
		assertFalse( verifyPairing("][.") );
	}

	@Test
	public void  verifyPairing_different_pair() {
		assertFalse( verifyPairing( "{]" ) );
		assertFalse( verifyPairing( "(}" ) );
		assertFalse( verifyPairing( "[)" ) );
		assertFalse( verifyPairing( "..{...)" ) );
		assertFalse( verifyPairing( ".(.]." ) );
		assertFalse( verifyPairing( "[.}.." ) );
		assertFalse( verifyPairing( "{)." ) );
	}

	@Test
	public void  verifyPairing_pairs() {
		assertTrue( verifyPairing( "{(-)[]([--{--} -]--)-{-}-}---" ) );
		assertTrue( verifyPairing( "{{-}(()[])[--{--} -]-(-)-{-}-}---" ) );
		assertFalse( verifyPairing( "{(-)[]([-- {--} -]--)-----" ) );
		assertFalse( verifyPairing( "{(-)[]([----} -]--)-{-}----" ) );
	}
}
