package generatorSamochodowElektrycznych;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Random;

public class EVgenerator {

	
	//From file
	ArrayList<String> dates = new ArrayList<String>();
	
	ArrayList<String> departures_time = new ArrayList<String>();
	ArrayList<Integer> departures_value = new ArrayList<Integer>();
	
	ArrayList<String> return_time = new ArrayList<String>();
	ArrayList<Integer> return_value = new ArrayList<Integer>();
	
	//To be dropped
	ArrayList<String> departures_time_final = new ArrayList<String>();
	ArrayList<String> return_time_final = new ArrayList<String>();
	
	public void run(String inputHoursFolder, String inputHoursFileName,String inputDistirbutionFolder, String inputDistirbutionFile, String outputFolder, String outputFilename)
	{
		String prefix = "_cars";
		
		System.out.println("\n---------\n run start\n");
		
		String inputHoursePath = inputHoursFolder+"\\"+inputHoursFileName+".txt";
		String inputDistributionPath = inputDistirbutionFolder+"\\"+inputDistirbutionFile+".csv";
		String outputFilePath=outputFolder+"\\"+outputFilename+prefix+".txt";
		     
		readDataHours(inputHoursePath);
		readDataDistribution(inputDistributionPath);
		generateSample();		
		dropToFile(outputFilePath);
		
		System.out.println("\n---------\n run end\n");
		
	}
	
	void dropToFile(String outputFileName)
	{
		System.out.println("\n----------\ndropToFile-start");
		
		try {
				
			Writer writer = new FileWriter(outputFileName);
			
			int a=0;
			while (a<dates.size())
			{
				writer.write(dates.get(a)+"#"+departures_time_final.get(a)+"#"+return_time_final.get(a)+System.lineSeparator());
				a++;
			}
			
			writer.close();
		}
		catch(Exception e)
		{
			
		}
		
		System.out.println("\n----------\ndropToFile -end");

	}
	
	void generateSample()
	{
		int a=0;
		while (a<dates.size())
		{
			
			int b=randInt(0, departures_value.get(departures_value.size()-1));
			
		
			
			String departureTime = readTime(departures_value,departures_time, b);
			
			b=randInt(0, return_value.get(return_value.size()-1));
			
			String returnTime = readTime(return_value,return_time, b);
			
			departures_time_final.add(departureTime);
			return_time_final.add(returnTime);
						
			a++;
		}
	}
	
	String readTime(ArrayList<Integer> departures_value, ArrayList<String> departures_time, int pickedNumber)
	{
		int a=0;
		
		String toBeReturned = departures_time.get(a);
		
		while (pickedNumber>departures_value.get(a))
		{
			a++;
			
			if (a<departures_value.size())
			{
				toBeReturned = departures_time.get(a);
			}
		}
		
		return toBeReturned;
		
		
	}
	
	public static int randInt(int min, int max) {

	    Random rand = new Random();
	    int randomNum = rand.nextInt((max - min) + 1) + min;
	    
	    return randomNum;
	}
	
	void readDataDistribution(String inputFile)
	{
		System.out.println("\n---------\readDataDistribution -start");
		BufferedReader br=null;
        try {
        	String line="";
            br = new BufferedReader(new FileReader(inputFile));
            
            Boolean b1 = true;
            int a=0;
            int sum=0;
            
            while ((line = br.readLine()) != null)
            {	
            	if (line.equals(""))
            	{
            		b1=false;
            		sum=0;
            	}
            	else
            	{
            		String[] s2 = line.split(",");
            		sum+=Integer.parseInt(s2[1]);
            		
            		if (b1)
                	{
                		departures_time.add(s2[0]);
                		departures_value.add(sum);                		
                	}
                	else
                	{
                		
                		return_time.add(s2[0]);
                		return_value.add(sum);
                	}
            	}	
            	
            }
        }
        catch (Exception e)
        {
        	System.out.println("Exception e");
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
		
		
		System.out.println("\n---------\readDataDistribution -end");
	}
	
	void readDataHours(String inputFile)
	{
		System.out.println("\n---------\nreadDataHours -start");
		
        BufferedReader br = null;
        try {
        	String line="";
            br = new BufferedReader(new FileReader(inputFile));
            
            Boolean b1 = false;
            int a=0;
            while ((line = br.readLine()) != null)
            {
            	if (b1)
            	{
            		if (a==0)
            		{
            			//System.out.println("0 "+line);
            			String[] s2 =line.split(" ");
            			//System.out.println(s2[0]);
            			dates.add(s2[0]);
            			
            		}
            		a++;
            		a=a%24;
            	}
            	
            	if (line.equals("###"))
            	{
            		b1=true;
            	}
            	
            }
        }
        catch (Exception e)
        {
        	System.out.println("Exception e");
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
        System.out.println("\n---------\nreadDataHours -end");
        

	}

}
