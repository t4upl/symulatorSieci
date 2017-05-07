import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		int first=0;
		int second =1;
		int nInput=10;
		
		int sum=first;
		
		int n = nInput;
		
		int i=1;
		while(i<n)
		{
			sum=first+second;
			
			first = second;
			second =sum;
			
			
			i++;
		}
		
		System.out.println(n+" "+sum);
		
		/*
		HashMap<String, Integer> map= new HashMap<>();
		
		map.put("Ania", 3);
		map.put("Kate", 5);
		
		System.out.println(map);
		
		/*String path="C:\\Users\\Administrator\\Desktop\\test.txt";
		float float1=0.1f;
		float float2=0.0000000001f;
		
		try {
			Writer writer = new FileWriter(path);
			writer.write(String.format("%f", float1)+System.lineSeparator());
			writer.write(String.format("%.10f", float2)+System.lineSeparator());
			writer.close();
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/

		


		 
		
		
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
