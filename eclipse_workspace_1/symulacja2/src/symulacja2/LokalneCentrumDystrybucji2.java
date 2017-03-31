package symulacja2;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Scanner;

public class LokalneCentrumDystrybucji2 {
		
		//Edytowalne
		int scenariusz=Stale.scenariusz;
		
		//Wewnetrzne parametry symulacji
		float cenaDystrybutoraZewnetrznego=Stale.cenaDystrybutoraZewnetrznego;
		
		//Folder z danymi zawierajacy foldery zuzycie i cars
		String mainFolder;
		//Folder z główny outputem, potem zmienia sie w outputFolder//Scenarion
		String outputFolder;

		//zawiera jako wartosci "godziny+dni" bez Stale.simulationEndDate 
		static ArrayList<String> hourList = new ArrayList<String>(); //zawiera dzien i godzine (ograniczone przez simulationEndDate)
		ListaProsumentow2 listaProsumentow2;
		
		//Stale
		String simulationEndDate = Stale.simulationEndDate;

		
		
		int liczbaProsumentow =Stale.liczbaProsumentow;
		Boolean handel=true;
		int horyzontCzasowy=Stale.horyzontCzasowy;
		
		//Zmienne
		private static int timeIndex=0;

		//System
		Scanner sc = new Scanner (System.in);
		//Optimizer optimizer = new Optimizer();
		Rynek rynek;
		Reporter reporter;
		AgregatorReporter agregatorReporter = AgregatorReporter.getInstance();
		
		long simulationStartTime;
		
		
		//Singleton shit
		private static LokalneCentrumDystrybucji2 instance = null;
		private LokalneCentrumDystrybucji2() 
		{
			
		}
		
		public static LokalneCentrumDystrybucji2 getInstance() {
		      if(instance == null) {
		         instance = new LokalneCentrumDystrybucji2();
		      }
		      return instance;
		}
		
		//-----------------
		//GETTERS
		
		static public int getTimeIndex()
		{
			return timeIndex;
		}
		
		
		static public String getCurrentDay()
		{
			String[] s2 = hourList.get(timeIndex).split(" ");
			return s2[0];
		}
		
		static public String getCurrentHour()
		{
			String[] s2 = hourList.get(timeIndex).split(" ");
			return s2[1];
		}
		
		
		
		//-------------
		//SETTERS
		
		//------------------------
		//OTHER FUNCTIONS
		
		
		void stworzProsumentow()
		{
			listaProsumentow2=new ListaProsumentow2(mainFolder+"\\zuzycie");
			
			rynek.setListaProsumentow(listaProsumentow2);
		}
		
		void modyfikatorScenariusza()
		{
			listaProsumentow2.modyfikatorScenariusza(cenaDystrybutoraZewnetrznego,scenariusz);
			
			switch (scenariusz)
			{
				case 1: scenarioOne(); break;
				case 2: break;
				case 3: break;
				case 4: break;
				case 5: break;
				case 6: break;
				default: print("!!!NO SUCH SCENARIO+"); sc.nextLine();
			}
		}
		
		
		
		void scenarioOne()
		{
			handel =false;
		}
		
		//returns subfolder (under otputFolder) based on setting data
		String subFolderName()
		{
			String s="";
			
			if (Stale.cenyZGeneratora)
			{
				s+="\\cenaZGeneratora";
			}
			else 
			{
				s+="\\cenaZZewnatrz";
			}
			
			return s;
		}
		
		//Fodler creation moved to reporter
		void createOutputFolder()
		{
			String newlyCreatedFolder=outputFolder+"\\Symulacja\\"+subFolderName()+"\\Scenario_"+scenariusz;
			outputFolder=newlyCreatedFolder;			
			
			reporter.createFolders(newlyCreatedFolder);
			reporter.setSimulationEndDate(simulationEndDate);
		}
		
		//TODO start
		public void start()
		{
			createOutputFolder();
			createHoursList(); //zrob liste godzin
			
			//Helper.printStringList(hourList);
			stworzProsumentow();
			modyfikatorScenariusza();
			
						
			startSimulation();
			
			endSimulation();
						
			System.out.println("\n-----Lokalne CentrumDystryucji start - end");
			System.out.println("Scenariusz: "+Stale.scenariusz+" end");

			
		}
		
		//uzywane do debugowania, sprawdza poprawnosc wczytania danych (przez wypisanie generacji i konsumpcji prosumenta)
		void testWczytanieDanych()
		{
			Prosument2 prosument =listaProsumentow2.getListaProsumentow().get(0);
			prosument.debugWypiszGeneracje();
			getInput("testWczytanieDanych - end");
			
		}
		
		//TODO end simualtion
		void endSimulation()
		{
			long timeElapsed =(long) ((System.nanoTime()- simulationStartTime)/(long)Math.pow(10, 9));
			print("Simulation time "+timeElapsed);

			//listaProsumentow2.consistencyCheck();
			
			//make all prosuments do the end of simualtion calculations and write report 
			listaProsumentow2.endSimulationReport();
			
			agregatorReporter.setListaProsumentow(listaProsumentow2.getListaProsumentow());
						
			agregatorReporter.run();
			
			rynek.endOfSimulation();
		}
		
		void print(String s)
		{
			System.out.println("LokalneCentrum2 "+s);
		}
		
		void startSimulation()
		{	
			simulationStartTime =System.nanoTime();
			
			print("startSimulation\n");
			int a=0;
			while(a<hourList.size())
			{
				//print(hourList.get(a));
				//getInput("Simulation step "+a);
				rynek.reset(a);
				
				simulationStep();				
				a++;
			}
		}
		
		void predykcjaCenNaRynkuLokalnym()
		{
			if (handel)
			{
				ArrayList<ArrayList<Float>> priceVector = rynek.predykcjaCenNaRynkuLokalnym(listaProsumentow2,timeIndex);				
								
				//rozeslij ene wsrod prosumentow, prosumenci odpowiadaja iloscia energii
				listaProsumentow2.broadcastFirstPriceVector(priceVector);
				
				//wyzancz cene
				rynek.wyznaczCeneAndBroadcast();
			}
		}
		

		//GDY NIE MA HANDLU!
		void zaktualizujHandelProsumentow()
		{
				listaProsumentow2.zaktualizujHandelProsumentow();
		}
		
		//TODO simulation step
		void simulationStep()
		{
			
			//Zeby byl jakis output keidy symualcja dziala
			if (timeIndex%4==0)
			{
				print ("simualtion step "+hourList.get(timeIndex));
			}
			
			if (handel)
			{
				//zainicjalizuj proces handlu, przeprowadz go i rozpisz kontrakty
				predykcjaCenNaRynkuLokalnym();
			}
			else
			{
				//Zaktualizuj prosumentow gdy nie ma handlu
				zaktualizujHandelProsumentow();	
			}
			
			Tick();
		}
		
		void Tick()
		{
			timeIndex++;
			listaProsumentow2.tickProsuments();
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
				e.printStackTrace();
			}
	        
			//System.out.println("\n-----\nLokalne CentrumDystryucji createHoursList - end");
		}
		
		void getInput(String s)
		{
			print("Input "+s);
			sc.nextLine();
		}
		
		void getInput()
		{
			getInput("");
		}

		public void setUp(String folderZDanymi, String outputFolder) {
			this.mainFolder=folderZDanymi;
			this.outputFolder=outputFolder;
			
			reporter = Reporter.getInstance();
			rynek = Rynek.getInstance();
		}
}
