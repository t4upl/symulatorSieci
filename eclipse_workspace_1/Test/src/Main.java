import java.util.ArrayList;
import java.util.Arrays;

public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		//int[] s1 ={1, 2, 3, 4, 5, 6, 7, 3, 8, 9, 2, 10};
		//int[] s2={3, 2, 7, 12, 3, 9, 5, 2};
		
		int[] s1 ={4, 7, 1, 6, 9, 2, 3, 1};
		int[] s2 ={};
		
		arraysMatching(s1,s2);
		
		
	}
	
    public static void arraysMatching(int[] s1, int[] s2) {
    ArrayList<Integer> storage = new ArrayList<Integer>();
        for (int i = 0; i < s1.length; i++) {
            for (int j = 0; j < s2.length; j++) {
            	
            	System.out.println(s1[i]+" "+s2[j]);
            	
                if (s2[j] == s1[i]) {
                    storage.add(s2[j]);
                    break;
                }
            }

        }
        System.out.println(Arrays.toString(storage.toArray()));
    }

}
