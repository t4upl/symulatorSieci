import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		

		int[] array = {5,5, 5, 3, 4, 4, 2, 2, 1};

		Arrays.sort(array);
		Set<Integer> set = new HashSet<Integer>();

		for (int i = 0; i < array.length; i++) {

		    if (!set.add(array[i])) {
		        set.remove(array[i]);
		    }
		}
		
		System.out.println(set.toString());
		 
		
		
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
