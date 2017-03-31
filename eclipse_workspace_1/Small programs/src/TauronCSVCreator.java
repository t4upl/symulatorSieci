import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Scanner;

public class TauronCSVCreator {
	
	//ZSUMUJ CSV OD TAURON KONSUMPCJA PO GODIZNACH (bez normalizacji)
	String pathToCSVFile;
	String pathToFolder;
	Scanner sc = new Scanner (System.in);
	
	TauronCSVCreator (String csvFileName, String pathToFolder)
	{
		this.pathToCSVFile=pathToFolder+"\\"+csvFileName+".csv";
		this.pathToFolder=pathToFolder;
		System.out.println(pathToCSVFile);
	}
	
	ArrayList<Float> stworzListe24()
	{
		ArrayList<Float> L1 = new ArrayList<Float>(); 
		int a=0;
		while (a<24)
		{
			L1.add(0f);
			a++;
		}
		return L1;
	}
	
	void wczytajDane(ArrayList<String> nameList,ArrayList<ArrayList<Float>> L1)
	{
		String startDate="2015-06-01";
		String endDate="2015-07-01";
		
		Boolean startDateReached = false;
		Boolean endDateReached = false;


		
		BufferedReader br = null;
        try {
        	String line="";
            br = new BufferedReader(new FileReader(pathToCSVFile));
            int a=0;
            Boolean header=true;

            while (((line = br.readLine()) != null) && !endDateReached )
            {
            	if (header)
            	{
            		if (line.contains("P+"))
            		{		
            			//System.out.println(a+"###  "+line);
            			String[] s2 =line.split("P+");
            			String stationName= s2[0].replace("?", "");
            			//System.out.println(stationName);
            			nameList.add(stationName);
            			if (nameList.size()==4)
            			{
                			header =false;
            			}
            		}
            	}
            	else
            	{
            		if (startDateReached)
            		{
                		if (line.contains(endDate))
                		{
                			endDateReached=true;
                		}
                		
                		if (!endDateReached)
                		{
                			String[] s2 = line.split(";");
                			String hourAsString = hourFromDate(s2[0]); 
                			Integer index =Integer.parseInt(hourAsString);
                			
                			System.out.println(line);
                			ArrayList<String> nonZeroValueList=nonEmptyStringFilter(s2);
                			
                			if (nonZeroValueList.size()!=4)
                			{
                				System.out.println("DUDE SOMETHING IS WRONG!!!!! "+nonZeroValueList.size());
                				sc.nextLine();
                			}
                			
                			int b=0;
                			while (b<nonZeroValueList.size()){
                				ArrayList<Float> L2 = L1.get(b);
                				L2.set(index, L2.get(index)+Float.parseFloat(nonZeroValueList.get(b)));
                				b++;
                			}
                		}
            			

            		}
            		else
            		{
                		if (line.contains(startDate))
                		{
                			startDateReached=true;
                		}
            		}
            		
            		



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
	
	ArrayList<String> nonEmptyStringFilter(String[] s2)
	{
		ArrayList<String> L1 = new ArrayList<String> ();
		int a=1;
		while (a<s2.length)
		{
			if (!s2[a].equals(""))
			{
				//System.out.println(s2[a]);
				L1.add(purify(s2[a]));
			}
			a++;
		}
		
		return L1;
	} 
	
	String purify(String s)
	{
		if (s.contains("#"))
		{
			s=s.replace("#","");
		}
		if (s.contains(","))
		{
			s=s.replace(",",".");
		}
		
		return s;
	}
	
	String hourFromDate(String s)
	{
		String[] s2 = s.split(" ");
		String[] s3 = s2[1].split(":");
		
		return s3[0];
	}
	
	void dropDataToFile(String name, ArrayList<Float> L1)
	{
		
		try {
			
			Writer writer = new FileWriter(pathToFolder+"\\"+name+".csv");
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
	
	ArrayList<ArrayList<Float>> stworzListe24Times4()
	{
		ArrayList<ArrayList<Float>> L1 = new ArrayList<ArrayList<Float>>() ;
		int a=0;
		while (a<4)
		{
			L1.add(stworzListe24());
			a++;
		}
		
		return L1;
	}
	
	void dropDataToFiles(ArrayList<String> nameList, ArrayList<ArrayList<Float>> L1)
	{
		int a=0;
		while (a<nameList.size())
		{
			dropDataToFile(nameList.get(a),L1.get(a));
			a++;
		}
	}
	
	public void run ()
	{
		
		ArrayList<ArrayList<Float>> L1 =stworzListe24Times4();
		ArrayList<String> nameList =new ArrayList<String>();

		wczytajDane(nameList,L1);
		dropDataToFiles(nameList,L1);
		System.out.println("\n---------\nPSEcsvCreator -end");
	        
	}
}
