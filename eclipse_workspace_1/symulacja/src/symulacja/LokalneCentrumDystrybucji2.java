package symulacja;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

public class LokalneCentrumDystrybucji2 {
	
	//Edytowalne
	String country = "US";
	int scenariusz=1;
	
	//Wewnetrzne parametry symulacji
	float cenaDystrybutoraZewnetrznegoUS=0.52f;
	float cenaDystrybutoraZewnetrznegoPL=0.34f;
	float cenaDystrybutoraZewnetrznego=0.52f;
	
	//Folder z danymi zawierajacy foldery zuzycie i cars
	String mainFolder;

	
	ArrayList<String> hourList = new ArrayList<String>(); //zawiera dzien i godzine
	
	//Stale
	String simulationEndDate = "2015-07-01 00:00";
	int liczbaProsumentow =16;

	
	LokalneCentrumDystrybucji2(String folderZDanymi)
	{
		this.mainFolder=folderZDanymi;
	}
	
	void stworzProsumentow()
	{
		/*String functionName = "stworzProsumentow";
		//System.out.println("\n-----\nLokalne CentrumDystryucji "+functionName+" - start");
		int a=0;
		while (a<liczbaProsumentow)
		{
			prosumentList.add(new Prosument(a+1,handel,this,cenaDystrybutoraZewnetrznego,debug));
			a++;
		}
		
		scenariuszPick(scenariusz);
		
		//wywolaj na prosumentahc wczytanie danych
		a=0;
		while (a<liczbaProsumentow)
		{
			prosumentList.get(a).loadData();
			a++;
		}*/
		
		
		//System.out.println("\n-----\nLokalne CentrumDystryucji "+functionName+" - end");
	}
	
	public void start()
	{
		//System.out.println("\n-----\nLokalne CentrumDystryucji start - start");
		createHoursList(); //zrob liste godzin
		Helper.printStringList(hourList);
		stworzProsumentow();
		
		//setSimulationParamesters();	
		//stworzProsumentow();
		
		//startSimulation();
		
		//dropResults();
		
		System.out.println("\n-----Lokalne CentrumDystryucji start - end");
		
	}
	
	void createHoursList()
	{
		String pathToData=mainFolder+"\\zuzycie\\1.csv";
		
		//System.out.println("\n-----\nLokalne CentrumDystryucji createHoursList - start");
        BufferedReader br = null;
        String line="";

        Boolean body=false;
        Boolean stop=false;

        
        try {
			br = new BufferedReader(new FileReader(pathToData));
	        while ((line = br.readLine()) != null && !stop)
	        {
	        	if (body)
	        	{	
	        		
	        		String[] s2 = line.split(",");
	        		
	        		if (s2[0].equals(simulationEndDate))
	        		{
	        			stop=true;
	        		}
	        		
	        		if (!stop)
	        		{
		        		//System.out.println(line);
	        			hourList.add(s2[0]);
	        		}
	        		
	        	}
	        	else
	        	{
		        	if (line.equals("###"))
		        	{
		        		body=true;
		        	}
	        	}
	        }
				
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
		//System.out.println("\n-----\nLokalne CentrumDystryucji createHoursList - end");
	}
}
