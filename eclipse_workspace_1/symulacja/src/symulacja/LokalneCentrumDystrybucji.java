package symulacja;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

public class LokalneCentrumDystrybucji {

	//Parametry symulacji
	String country = "US";
	int scenariusz=1;
	Boolean handel = false;
	
	int numerTestu=1; //do powtarzania teog samego testu kilka razy
	Boolean debug = true;

	
	//Stale
	
	String simulationEndDate = "2015-07-01 00:00";
	int liczbaProsumentow =16;

	//Wewnetrzne parametry symulacji
	float cenaDystrybutoraZewnetrznegoUS=0.52f;
	float cenaDystrybutoraZewnetrznegoPL=0.34f;
	float cenaDystrybutoraZewnetrznego=0.52f;
	
	
	
	//Listy
	
	ArrayList<String> hourList = new ArrayList<String>(); //zawiera dzien i godzine
	ArrayList<Prosument> prosumentList = new ArrayList<Prosument>();
	
	
	//System
	String mainFolder ="C:\\Users\\Administrator\\Desktop\\magisterska_Dane_set1";
	String pathToData="C:\\Users\\Administrator\\Desktop\\magisterska_Dane_set1\\Zuzycie\\1.txt";
	Scanner sc = new Scanner(System.in);
	
	public void start()
	{
		//System.out.println("\n-----\nLokalne CentrumDystryucji start - start");
		createHoursList(); //zrob liste godzin
		setSimulationParamesters();
		
		
		stworzProsumentow();
		
		startSimulation();
		
		dropResults();
		
		System.out.println("\n-----Lokalne CentrumDystryucji start - end");
		
	}
	
	void createFolderForResults()
	{
		String resultsFolder =mainFolder+"\\"+scenariusz+"_"+country+"_"+numerTestu;
        Path path = Paths.get(resultsFolder);
        //if directory exists?
        if (!Files.exists(path)) {
            try {
                Files.createDirectories(path);
            } catch (IOException e) {
                //fail to create directory
            	
            	System.out.print("EXCEPTION! dropResults");
                e.printStackTrace();
            }
        }
	}
	
	void checkAndDropProsuments()
	{
		int a=0;
        while (a<liczbaProsumentow)
        {
        	prosumentList.get(a).doFinalCheck();
        	prosumentList.get(a).dropToFile();
        	a++;
        }
	}
	
	void dropResults()
	{
		createFolderForResults();
        checkAndDropProsuments();
		
        
	}
	
	void setSimulationParamesters()
	{
		if (country.equals("US"))
		{
			cenaDystrybutoraZewnetrznego=cenaDystrybutoraZewnetrznegoUS;
		}
		else
		{
			cenaDystrybutoraZewnetrznego=cenaDystrybutoraZewnetrznegoPL;
		}
	}
	
	void stworzProsumentow()
	{
		String functionName = "stworzProsumentow";
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
		}
		
		
		//System.out.println("\n-----\nLokalne CentrumDystryucji "+functionName+" - end");
	}
	
	void scenariuszPick(int scenarioNumber)
	{
		String functionName = "scenariusz";
		//System.out.println("\n-----\nLokalne CentrumDystryucji "+functionName+" - start");
		
		
		switch (scenarioNumber)
		{
			case 1: scenarioOne(); break;
			default: System.out.println("!!!NO SUCH SCENARIO");
		}
		
		
		//System.out.println("\n-----\nLokalne CentrumDystryucji "+functionName+" - end");
	}
	
	void scenarioOne()
	{
		String functionName = "scenarioOne";
		System.out.println("\n-----\nLokalne CentrumDystryucji "+functionName+" - start");
		
		int numerProsumenta=0;
		while (numerProsumenta<4)
		{
			prosumentList.get(numerProsumenta).setStosunekGeneracjiDoKonsumpcji(4);
			prosumentList.get(numerProsumenta).setObecnoscBaterii(true);
			numerProsumenta++;
		}

		
		
		
		System.out.println("\n-----\nLokalne CentrumDystryucji "+functionName+" - end");
	}	
	
	void functionTemplate()
	{
		String functionName = "TEMPLATE";
		System.out.println("\n-----\nLokalne CentrumDystryucji "+functionName+" - start");
		System.out.println("\n-----\nLokalne CentrumDystryucji "+functionName+" - end");
	}
	
	
	void startSimulation()
	{
		String functionName = "startSimulation";
		System.out.println("\n-----\nLokalne CentrumDystryucji "+functionName+" - start");
		
		int a=0;
		while (a<hourList.size())
		{
			
			if (debug)
			{
				System.out.println("Simulation time "+hourList.get(a)+" wait for input");
				sc.nextLine();
			}
			predictPrice();

			int b=0;
			while (b<prosumentList.size())
			{
				
				//ustaw predykcje ceny na 24 do przodu u prosumentow
				prosumentList.get(b).setPricePredictionOnLocalMarket();
				
				//ustal sterownaie agenta
				prosumentList.get(b).updateProsumentState();
				
				//przejdz do nastepnej chwili czasu
				prosumentList.get(b).tickTime();
				b++;
			}
			//System.out.println(hourList.get(a));
			
			
			a++;
		}
		
		System.out.println("\n-----\nLokalne CentrumDystryucji "+functionName+" - end");
	}
	
	void predictPrice()
	{
		//FILL THIS OUT!!!!
	}
	
	void createHoursList()
	{
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
	        		
	        		String[] s2 = line.split("#");
	        		
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
