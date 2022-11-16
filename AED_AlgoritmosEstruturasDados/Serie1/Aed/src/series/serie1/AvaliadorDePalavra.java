package series.serie1;

public class AvaliadorDePalavra {
    private String word;
    private int prefixCounter;

    public AvaliadorDePalavra(String  word, String prefix) {
        this.setWord(word);
        this.setPrefixCounter(prefixCounter(word, prefix));
    }

    public int prefixCounter(String word, String prefix) {
        int value=1;
        for(int i=1; i<prefix.length();i++) {
            if(word.charAt(i)==prefix.charAt(i)) value++;
            else break;
        }
        return value;
    }

    public int getPrefixCounter() {
        return prefixCounter;
    }

    private void setPrefixCounter(int prefixCounter) {
        this.prefixCounter = prefixCounter;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }
}
