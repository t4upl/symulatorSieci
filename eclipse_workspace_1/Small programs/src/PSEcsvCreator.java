import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;

public class PSEcsvCreator {

	//ZSUMUJ CSV OD PSE PO GODIZNACH (bez normalizacji)
	String pathToCSVFile;
	
	PSEcsvCreator (String pathToCSVFile)
	{
		this.pathToCSVFile=pathToCSVFile;
	}
	
	ArrayList<Integer> stworzListe24()
	{
		ArrayList<Integer> L1 = new ArrayList<Integer>(); 
		int a=0;
		while (a<24)
		{
			L1.add(0);
			a++;
		}
		return L1;
	}
	
	void wczytajDaneZPSE(ArrayList<Integer> L1)
	{
		BufferedReader br = null;
        try {
        	String line="";
            br = new BufferedReader(new FileReader(pathToCSVFile));
            
            Boolean header=true;

            while ((line = br.readLine()) != null)
            {
            	if (header)
            	{
            		header =false;
            	}
            	else
            	{
	            	//System.out.println(line);
	            	String[] s2 =line.split(";");
	            	
	            	Integer hour =Integer.valueOf(s2[1]);
	            	hour=hour%24;
	            	//System.out.println(s2[3]);
	            	
	            	if (!line.contains("."))
	            	{
	            		System.out.println(line);
	            	}
	            	
	            	String[] s3 = s2[3].split("\\.");
	            	//System.out.println(s3[0]);
	            	String s22 = s3[0];
	            	
	     
	            	Integer b=Integer.valueOf(s22);
	            	
	            	L1.set(hour, L1.get(hour)+b);
	            	
            	//System.out.print(b);
            	}
            }
        }
        catch (Exception e)
        {
        	System.out.println("Exception e");
            e.printStackTrace(System.out);
        }
    	finally {
	        if (br != null) {
	            try {
	                br.close();
	            } catch (IOException e) {
	                e.printStackTrace();
	            }
	        }
    	}

	} 
	
	public void run ()
	{
		String outputFile="C:\\Users\\Administrator\\Desktop\\PSE_2\\czerwiecPSE_average.csv";;
		
		ArrayList<Integer> L1 =stworzListe24();
		wczytajDaneZPSE(L1);
		dropDataToFile(L1,outputFile);
			        System.out.println("\n---------\nPSEcsvCreator -end");
	        
	}
	
	void dropDataToFile(ArrayList<Integer> L1,String outputFile )
	{

		
		try {
			
			Writer writer = new FileWriter(outputFile);
			writer.write("godzina,konsumpcja"+System.lineSeparator());
			

			int a=0;
			while(a<L1.size())
			{
				writer.write(a+","+L1.get(a)+System.lineSeparator());
				a++;
			}
			
		
			writer.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
