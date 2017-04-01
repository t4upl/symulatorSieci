import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		

		BufferedReader br = null;
		FileReader fr = null;
		
		String FILENAME ="C:\\Users\\Administrator\\Desktop\\hi.txt";

		try {

			fr = new FileReader(FILENAME);
			br = new BufferedReader(fr);

			String sCurrentLine;

			br = new BufferedReader(new FileReader(FILENAME));

			while ((sCurrentLine = br.readLine()) != null) {
				System.out.println(sCurrentLine);
			}
			
			br.close();
			
			//open once more
			br = new BufferedReader(new FileReader(FILENAME));

			while ((sCurrentLine = br.readLine()) != null) {
				System.out.println(sCurrentLine);
			}
			

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)
					br.close();
				if (fr != null)
					fr.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}

		}
		
		
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
