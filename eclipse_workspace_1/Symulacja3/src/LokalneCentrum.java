import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;



public class LokalneCentrum extends CoreClass {

	//ustawiane  w setUp()
	private int scenariusz;
	
	//okresla czy w sieci jest mozliwosc handlu
	//na ogol ustawione na true
	//ustawiane na flase tylko dla scenariusza ==1 przy modyfikatorze scenariusza
	Boolean handel=true;

	
	//zawiera dzien i godzine (ograniczone przez simulationEndDate)
	static ArrayList<String> hourList = new ArrayList<String>(); 
	
	//zaiwera godizny
	static ArrayList<String> hourList2 = new ArrayList<String>();
	
	//zawiera dni
	static ArrayList<String> dayList = new ArrayList<String>(); 

	
	//System
	//deklaracje w funkcji setUp
	private Stale stale;
	private Reporter reporter;
	private ListaProsumentowWrap listaProsumentowWrap;
	private Agregator agregator = Agregator.getInstance();
	private Rynek rynek;

	
	//start symualcji
	private long simulationStartTime;
	
	//w ktorym slocie teraz sie znajduje
	static private int timeIndex;

	//Singleton shit
	private static LokalneCentrum instance = null;
	private LokalneCentrum() 
	{
		
	}
	
	public static LokalneCentrum getInstance() {
	      if(instance == null) {
	         instance = new LokalneCentrum();
	      }
	      return instance;
	}
	
	//TODO
	//--------------------
	//GETTERS
	
	public static  ArrayList<String> getHourList()
	{
		return hourList;
	}
	
	public static int getTimeIndex()
	{
		return timeIndex;
	} 
	
	static public String getCurrentHour()
	{
		String[] s2 =hourList.get(timeIndex).split(" ");		
		return s2[1];
	}
	
	static public String getCurrentDay()
	{
		String[] s2 =hourList.get(timeIndex).split(" ");
		return s2[0];
	}
	
	static public String getHour(int index)
	{
		if (index<hourList2.size())
		{
			return hourList2.get(index);
		}
		else
		{
			return "HH:HH";
		}
	}
	
	static public String getDay(int index)
	{
		if (index<hourList2.size())
		{
			return dayList.get(index);
		}
		else
		{
			return "DD-DD-DDDD";
		}
	}
	
	//--------------------
	//SETTERS
	
	//--------------------
	//OTHER
	

	
	//TODO
	public void start()
	{
				
		setUp();
		
		
		createOutputFolder();
		
		createHoursList(); //zrob liste godzin
		

		stworzProsumentow();
		
		modyfikatorScenariusza();
		
		
		startSimulation();
		
		
		endSimulation();
				
		System.out.println("\n-----\nLokalne CentrumDystryucji - end");
		
	}
	
	//ustaw wszystkie singletony
	void setUp()
	{
		stale=Stale.getInstance();
		reporter = Reporter.getInstance();
		listaProsumentowWrap = ListaProsumentowWrap.getInstance();
		rynek = Rynek.getInstance();

		
		scenariusz=Stale.scenariusz;
		
		setUpScenarioModifier();
	}
	
	//tylko do testow
	void setUpScenarioModifier()
	{
		if (Stale.scenariusz>100)
		{
			Stale.liczbaProsumentow=2;
		}
	}
	
	//returns subfolder (under otputFolder) based on setting data
	String subFolderName()
	{
		String s="";
		
		if (Stale.cenyZGeneratora)
		{
			s+="cenaZGeneratora";
		}
		else 
		{
			s+="cenaZZewnatrz";
		}
		
		return s;
	}
	
	//Fodler creation moved to reporter
	void createOutputFolder()
	{
		String newlyCreatedFolder=Stale.outputFolder+"\\"+subFolderName()+"\\Scenario_"+scenariusz;
		
		reporter.createFolders(newlyCreatedFolder);
	}
	
	//wczytaj z pliku prosuemnta liste dni i godzin
	void createHoursList()
	{
		String pathToData=Stale.folderZDanymi+"\\zuzycie\\1.csv";
		
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
	        		
	        		if (s2[0].equals(Stale.simulationEndDate))
	        		{
	        			stop=true;
	        		}
	        		
	        		if (!stop)
	        		{
		        		//System.out.println(line);
	        			hourList.add(s2[0]);
	        			
	        			String dayHour = s2[0];
	        			String[] s22 =dayHour.split(" "); 
	        			String day = s22[0];
	        			String hour = s22[1];
	        			
	        			hourList2.add(hour);
	        			dayList.add(day);
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
			print("createHoursList EXCEPTION");
			e.printStackTrace();
		}
        
	}
	
	void stworzProsumentow()
	{
		listaProsumentowWrap.loadProsumenci();
	}
	
	void modyfikatorScenariusza()
	{
		//zwykly scenariusz nie testowy
		if (scenariusz<100)
		{
			listaProsumentowWrap.modyfikatorScenariusza();
			
			if (scenariusz==1)
			{
				handel =false;
			}
		}
		else
		{
			listaProsumentowWrap.modyfikatorScenariuszaTestowego();
		}

	}
	
	void startSimulation()
	{	
		simulationStartTime =System.nanoTime();
		
		print("startSimulation\n");
		int a=0;
		while(a<hourList.size())
		{
			
			simulationStep();				
			a++;
		}
		
		//po koncu symulacji timeIndex wskazuje poza liste 
		//wiec cofamy go na koniec zeby wskazywal ostatni element
		timeIndex--;
	}
	
	//TODO simulation step
	void simulationStep()
	{
		
		//print ("simualtion step "+hourList.get(timeIndex));
		
		//Zeby byl jakis output keidy symualcja dziala
		if (timeIndex%4==0)
		{
			print ("simualtion step "+hourList.get(timeIndex)+" #");
		}
		
		if (handel)
		{
			//zainicjuj proces handlu
			predykcjaCenNaRynkuLokalnym();
		}
		else
		{
			//Zaktualizuj prosumentow gdy nie ma handlu
			zaktualizujProsumentowBrakHandlu();	
		}
		
		Tick();
	}
	
	//inicjalizacja procesu handlu
	void predykcjaCenNaRynkuLokalnym()
	{
		rynek.reset();
		ArrayList<ArrayList<Float>> priceVector = rynek.predykcjaCenNaRynkuLokalnym();				
							
		//rozeslij ene wsrod prosumentow, prosumenci odpowiadaja iloscia energii
		listaProsumentowWrap.broadcastFirstPriceVector(priceVector);
			
		//wyzancz cene
		rynek.wyznaczCeneAndBroadcast();
		
	}
	
	void Tick()
	{
		listaProsumentowWrap.tickProsuments();
		timeIndex++;
	}
	
	void zaktualizujProsumentowBrakHandlu()
	{
		listaProsumentowWrap.zaktualizujProsumentowBrakHandlu();
	}
	
	//TODO end simualtion
	void endSimulation()
	{
		long timeElapsed =(long) ((System.nanoTime()- simulationStartTime)/(long)Math.pow(10, 9));
		print("Simulation time "+timeElapsed+"\n");


		listaProsumentowWrap.endSimulationCheck();
				
		//make all prosuments do the end of simualtion calculations and write report 
		listaProsumentowWrap.endSimulationReport();
		
		

		Agregator agregator =Agregator.getInstance();
		agregator.run();
			
		
		if (handel)
		{
			rynek.endOfSimulation();
		}
	}
	


	
	
}
