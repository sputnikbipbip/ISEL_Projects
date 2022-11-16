package series.serie1;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static series.serie1.Arrays.greaterCommonPrefix;



import org.junit.jupiter.api.Test;

public class GreaterCommonPrefixTest {


	@Test
	public  void greaterCommonPrefix_onEmptyArray(){
		String[] array={};
		assertEquals(null,greaterCommonPrefix(array,0,-1,"AED"));
	}

	@Test
	public void greaterCommonPrefix_onArrayWithOneElement1(){
		String[] array={"arvore"};
		assertEquals("arvore",greaterCommonPrefix(array,0,0,"aed"));
	}

	@Test
	public void greaterCommonPrefix_onArrayWithOneElement2(){
		String[] array={"programar"};
		assertEquals("programar",greaterCommonPrefix(array,0,0,"aed"));
	}

	@Test
	public void greaterCommonPrefix_onArrayWithOneElement3(){
		String[] array={"ar"};
		assertEquals("ar",greaterCommonPrefix(array,0,0,"arvore"));
	}

	@Test
	public void greaterCommonPrefix_onArrayWithElementsWithCommonPrefix(){
		String[] array={"agendar","dia","teste"};
		assertEquals("agendar",greaterCommonPrefix(array,0,2,"agendas"));
	}


	@Test
	public void greaterCommonPrefix_onArrayWithElementsWithCommonPrefix2(){
		String[] array={"agem","agendaa","agendap","agendar","agendara", "agendarao","teste"};
		assertEquals("agendarao",greaterCommonPrefix(array,0,6,"agendas"));
	}


	@Test
	public void greaterCommonPrefix_onArrayWithEqualsElementOnTheRight(){
		String[] array={"agem","agendas","agendas","agendas","agendas", "agendas","agendas"};
		assertEquals("agendas",greaterCommonPrefix(array,0,6,"agendas"));
	}

	@Test
	public void greaterCommonPrefix_onArrayWithEqualsElementOnTheLeft(){
		String[] array={"agendas","agendas","agendas","canetas","testes","testes"};
		assertEquals("agendas",greaterCommonPrefix(array,0,5,"agendas"));
	}



	@Test
	public void greaterCommonPrefix_onArrayWithElementsWithoutCommonPrefix(){
		String[] array={"agendar","dia","teste"};
		assertEquals("teste",greaterCommonPrefix(array,0,2,"caneta"));
	}
	
}
