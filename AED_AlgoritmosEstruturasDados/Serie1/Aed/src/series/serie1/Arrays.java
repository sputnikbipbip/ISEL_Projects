package series.serie1;

public class Arrays {


    public static int countEquals(int[] v1, int l1, int r1, int[] v2, int l2, int r2){
        int count=0;
        while(l1<=r1 && l2<=r2){
            if(v1[l1]>v2[l2]) l2++;
            else
                if(v1[l1]<v2[l2]) l1++;
                else { count++; l1++; l2++;
                }
        }
        return count;
    }

    public static int printEachThreeElementsThatSumTo(int[] v, int l, int r, int s){
        /*
        //1.1 - Custo O(N^3)
        if(l>=r) return 0;
        int count=0;
        for (int i=l; i<=r; i++) {
            if(v[i]<s) {
                for(int j=i+1; j<=r;j++) {
                    if(v[i]+v[j]<=s) {
                        for (int k=j+1;k<=r;k++) {
                            if(v[i]+v[j]+v[k]==s) {
                                count++;
                                System.out.println("FIRST "+v[i]+" SECOND "+v[j]+" THIRD "+v[k]);
                            }
                        }
                    }
                }
            }
        }
        return count;


        //1.2 - Custo O(N^2*log n)

        if(l>=r) return 0;
        int count = 0;
        v = sort(v,l,r); // sorting algorithm

        for(int i=l; i<r;i++) {
            if(v[i]<s) {
                for (int j=i+1;j<r;j++) {
                    if(v[i]+v[j]<= s && v[i] != v[j]) {
                        count+=searchLast(v,j+1,r,v[i],v[j],s);  //Chama algoritmo binarySearch que tem um custo O(log n)
                    }
                }
            }
        }
        return count;
        */
        //1.3 - Custo O(N^2) foi criado os métodos sort e searchlast de forma a poder usufruir de binarySearch

        if(l>=r) return 0;
        int count = 0;
        int aux = l;
        for(int i = aux+1; i <= r; i++){
            if(i >= r-1 && aux <= r){
                aux++;
                i = aux+1;
            }
            if(v[aux]+v[i] <= s) {
                System.out.println("Two elements "+v[aux]+", "+v[i]);
                for (int j = i+1; j <= r; j++) {
                    if (v[j] + v[i] + v[aux] == s) {
                        count++;
                        System.out.println("First "+v[aux]+", Second "+v[i]+", Third "+v[j]);
                    }
                }
            }
        }
        return count;


    }

    public static int[] sort (int[]v, int l, int r){
        for(int j=2; j<v.length-1; j++) {
            int key=v[j], i=j-1;
            while (i>0 && v[i]>key) {
                v[i+1]=v[i];
                i=i-1;
            }
            v[i+1]=key;
        }
        return v;
    }

    public static int searchLast(int[] v, int l, int r, int first, int second, int s) {
        int m = (l+r)/2;
        if(l>=r) {
            if(first + second + v[m] == s && v[m] != first && v[m] != second) return 1;
            else return 0;
        }
        if (first + second + v[m] == s) return 1;
        else if (first + second + v[m] < s) return searchLast(v, m+1, r, first, second, s);
        else return searchLast(v, l, m-1, first, second, s);
    }


    public static int removeIndexes(int v[], int l, int r, int[] vi, int li, int ri) {
        System.out.println("li: "+li);
        System.out.println("ri: "+ri);

        if (v.length <= 0) {	// return para arrays vazios
            System.out.println("return 0");
            return 0;
        }

        if (li > ri) {		// return para Vi vazio
            System.out.println("return length");
            return v.length;
        }

        int bus = 0; // indice que e substituido por novo valor
        int index = li;   // vai percorrer o array vi

        for (int j = l; j <= r; j++) { // percorre o array v de l a r

            if (vi[index] != j) {
                v[bus] = v[j];
                bus++; 		 // o bus vai incrementando conforme são guardados os valores
                System.out.println("RemoveAux: "+bus);
            }
            else if(index<ri){
                index++;
            }
        }
        System.out.print ("Array{");
        for (int i=0; i<=r; i++) {
            System.out.print(v[i]+",");
        }
        System.out.print("}");
        System.out.println(""); System.out.println("Final:"+bus);System.out.println("");
        return bus;


    }


    public static String greaterCommonPrefix(String[] v, int l, int r, String word) {
        if(l>r) return null;

        int letra=0;
        String test="", resultWord="";
        System.out.println(word);
        for (int i = 0; i < r; i++) {
            System.out.println("v["+i+"]= "+v[i]);
            if (v[i].charAt(0) == word.charAt(0)) {
                if (v[i].length() > test.length()) { //podemos ter uma igual no mínimo

                    String aux = test;
                    test = "";

                    System.out.println("Palavra" + word);
                    while (letra < v[i].length() && letra < word.length()) { // Corre a palavra no array v na posição i
                        System.out.println("Inside while: " + letra);

                        if (v[i].charAt(letra) == word.charAt(letra)) { // Concatenação da palavra
                            System.out.println("Before:" + test);
                            test += v[i].charAt(letra);
                            System.out.println("After:" + test);
                        }

                        else if (test.length() < aux.length()) { //coloca o resultado anterior (maior prefixo) caso seja menor
                            test = aux;
                            letra = 0;
                            break;
                        }
                        letra++;
                    }

                    if (test.length() > 0) { // atribuição da palavra caso tenhamos um prefixo válido
                        resultWord = v[i];
                        letra = 0;
                    }
                }
            }
        }
        if (resultWord == "")
            return v[v.length - 1];
        return resultWord;
    }


     public static int sumGivenN(int n){
         int res=1;
         for(int i=1; i<n; i++) {   //começa por 0 à procura de sequência que satisfaçam igualdade com n
             int sum=0, number=i;
             String sequence = "";
             while((sum+=number++)<n){
                 sequence += number+" ";
             };
             if(sum==n) {
                 System.out.println(sequence);
                 res++;
             }
         }
         System.out.println(n);
         return res;
     }

    public static int deleteMin(int[] maxHeap, int sizeHeap) {
        if(sizeHeap<=1)return 0;
        heapSort(maxHeap, sizeHeap);		    //ordena o Heap de forma crescente
        int aux=maxHeap[sizeHeap-1];			//Troca do menor valor para a ultima posição
        maxHeap[sizeHeap-1]=maxHeap[0];
        maxHeap[0]=aux;
        --sizeHeap;
        maxHeapify(maxHeap, aux, sizeHeap);
        return sizeHeap;
    }

    public static void maxHeapify(int[] h, int i, int n) {
        int l = 2 * i + 1;
        int r = 2 * i + 2;
        int largest;
        if (l < n && h[l] > h[i])
            largest = l;
        else largest = i;
        if (r < n && h[r] > h[largest])
            largest = r;
        if (largest != i) {
            exchange(h, i, largest);
            maxHeapify(h, largest, n);
        }
    }

    private static void exchange(int[] h, int i, int j) {
        int aux = h[i];
        h[i] = h[j];
        h[j] = aux;
    }

    public static void buildMaxHeap(int[] h, int n) {
        for (int i = n / 2 - 1; i >= 0; i--)
            maxHeapify(h, i, n);
    }

    public static void heapSort(int[] h, int n) {
        buildMaxHeap(h, n);
        for (int i = n - 1; i >= 1; i--) {
            exchange(h, i, 0);
            maxHeapify(h, 0, i);
        }
    }

}
