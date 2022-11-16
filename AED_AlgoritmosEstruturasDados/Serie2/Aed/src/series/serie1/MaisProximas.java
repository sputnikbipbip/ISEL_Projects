package series.serie1;

import java.io.*;

public class MaisProximas {


    public static void main (String []args) throws IOException{

        int count = Integer.parseInt(args[0]);											    //número de palavras a colocar no ficheiro de output
        final String prefix = args[1]; 													    //Prefixo passado
        AvaliadorDePalavra[] end = new AvaliadorDePalavra[count]; 								//Criacao do array com o tamanho solicitado
        for (int i = 3; i < args.length; i++) {
            BufferedReader reader = new BufferedReader(new FileReader(args[i])); 			//Buffered reader para o ficheiro com as palavras
            try {
                String line="";
                int lineDelimiter=0;
                while ((line=reader.readLine()) != null) {									//leitura de cada linha do ficheiro
                    System.out.println("Line: "+lineDelimiter);
                    lineDelimiter++;
                    if(line.charAt(0)==prefix.charAt(0)) {
                        AvaliadorDePalavra word = new AvaliadorDePalavra(line,prefix);          //retorna objeto que contem o numero de palavras no campo prefixcount
                        if (count>0) { 													    //adicao dos valores ate o array estar cheio
                            end[--count]=word;
                            if(count==0) StringHeap.buildMinHeap(end, end.length);  	    //Criacao do minHeap quando o array esta cheio
                        }
                        else {
                            if (word.getPrefixCounter() > StringHeap.minimum(end)) {		//Verificacao se o actual valor que estamos a ler o maior que o menor que temos no array
                                if (StringHeap.organizer(end, 0, word) == false) {	    //Verificacao se existe alguma palavra no array igual a que estamos a ler
                                    end[0] = word;										    //Coloca novo valor no array
                                    StringHeap.minHeapify(end, 0, end.length);			//coloca o array em minHeap novamente
                                }
                            }
                        }
                    }
                }
            } finally {
                reader.close();
            }
        }
        PrintWriter fileWriter = new PrintWriter(args[2]); 									        // cria novo ficheiro de escrita na respetiva diretoria
        for(int z=0; z<end.length-1; z++) {
            if(end[z]==null){
                ++z;
            }else{
                fileWriter.println(end[z].getWord());
            }
        }
        fileWriter.close();
    }
}