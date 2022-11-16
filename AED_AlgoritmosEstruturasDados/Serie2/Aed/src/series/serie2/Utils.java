package series.serie2;
import java.util.EmptyStackException;


public class Utils {

	public static boolean verifyPairing(String s) {
		if (s.length() == 0) {
			return true;
		} else if (s.length() == 1) {
			return false;
		}
		StackArray<Character> stack = new StackArray<>(s.length());
		for(int i = 0; i < s.length(); i++) {
			if(s.charAt(i) == '{' || s.charAt(i) == '[' || s.charAt(i) == '(') {
				stack.push(s.charAt(i));
			} else if (!stack.empty() && ((
					(s.charAt(i) == ']') && stack.peek() == '[') ||
					((s.charAt(i) == '}') && (stack.peek() == '{')) ||
					((s.charAt(i) == ')') && (stack.peek() == '('))
			)) {
				stack.pop();
			}else if(s.charAt(i) == '}' || s.charAt(i) == ']' || s.charAt(i) == ')')
				return false;
		}
		if(stack.empty()) {
			return true;
		} else {
			return false;
		}
	}

	public static class StackArray<Character>{
		private char[] aux;
		private int top;
		public StackArray(int capacity) {
			aux = new char[capacity];
		}
		public boolean empty(){
			return top == 0;
		}

		public char peek() {
			if(empty())
				throw new EmptyStackException();
			return aux[top-1];
		}
		public char push(char item) {
			if(top == aux.length)
				return '\0';
			aux[top++] = item;
			return item;
		}
		public char pop() {
			if(empty()) throw new EmptyStackException();
			char e = aux[--top];
			aux[top] = '\0';
			return e;
		}
	}


	public static int median(int[] v, int l, int r) {
		if(v.length == 2)
			return (v[0]+v[1]/2);
		if(v.length == 1)
			return v[0];
		if(v.length < 1)
			return 0;
		int k = (l+r)/2;
		int result = splitter(v ,l ,r, k);
		if((r-l+1) % 2 == 0){
			int extra = splitter(v,l,k+1, k+1);
			return (result+extra)/2;
		}
		return result;
	}

	//TODO: 1.2
	public static int splitter(int[] v, int l, int r, int k) {
		int partition = partition(v,l,r);
		if(partition == k)
			if(v.length % 2 == 0) {
				return v[partition];
			}
			else
				return v[partition];
		else if(partition < k )
			return splitter(v, partition + 1, r, k );
		else
			return splitter(v, l, partition-1, k );
	}

	public static int partition (int[] v, int l, int r) {
		int pivot = v[r];
		int mobilePivot = l;
		for (int i = l; i <= r; i++) {
			if(v[i] < pivot) {
				int temp = v[i];
				v[i] = v[mobilePivot];
				v[mobilePivot] = temp;
				mobilePivot++;
			}
		}
		int temp = v[r];
		v[r] = v[mobilePivot];
		v[mobilePivot] = temp;
		return mobilePivot;
	}
}
