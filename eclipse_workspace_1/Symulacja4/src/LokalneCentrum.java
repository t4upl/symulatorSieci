import java.util.ArrayList;

public class LokalneCentrum extends CoreClass {


	static int timeIndex=0;
	
	//taki indeks ze dayHourlist.get(index) = simualtionEndDate
	static int simulationEndDateIndex;
	
	//mowi czy w dnaym scenariuszu jest mozliwosc handlu flase tylko w senarisuzu =1
	Boolean isHandelWSieci=true;

	
	
	//zawiera dzien i godzine
	ArrayList<String> dayHourList = new ArrayList<String>(); 
	
	//zaiwera godizny
	ArrayList<String> hourList = new ArrayList<String>();
	
	//zawiera dni
	ArrayList<String> dayList = new ArrayList<String>();
	
	
	//start symualcji
	private long simulationStartTime;
	
	
	//Singletony	
	private Reporter reporter;
	private ListaProsumentowWrap listaProsumentowWrap;
	private Agregator agregator;
	private Rynek rynek;
	private Loader loader;
	
	//-----------------------
	
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
	
	//----------------------
	public static int getTimeIndex()
	{
		return timeIndex;
	} 
	
	public static int getSimulationEndDateIndex()
	{
		return simulationEndDateIndex;
	}
	
	//-----------------------
	
	String dayFromDayHour(String dayHour)
	{
		String[] s2 = dayHour.split(" ") ;
		return s2[0];
	}
	
	String hourFromDayHour(String dayHour)
	{
		String[] s2 = dayHour.split(" ") ;
		return s2[1];
	}
	
	
	//TODO
	public void start()
	{
		//ustaw singletony		
		setUp();
		
		//stworz lsite godzin
		createHoursList();
		
		//stworz prosumentow i zmodyfikuj ich pod scenariusz
		modyfikatorScenariusza();		
		
		startSimulation();
		
		
		endSimulation();
				
		System.out.println("\n-----\nLokalne CentrumDystryucji - end");
		
	}
	
	void modyfikatorScenariusza()
	{
		if (Stale.scenariusz==1)
		{
			isHandelWSieci=false;
		}
		else
		{
			isHandelWSieci=true;
		}
		
		listaProsumentowWrap.modyfikatorScenariusza();

	}
	
	void endSimulation()
	{
		print("endSimulation");
				
		//make all prosuments do the end of simualtion calculations and write report 
		listaProsumentowWrap.endSimulationReport();
		
		Agregator agregator =Agregator.getInstance();
		agregator.run();
			
		
		if (isHandelWSieci)
		{
			rynek.endOfSimulation();
		}
	}

	
	void startSimulation()
	{
		print("startSimulation");
		
		simulationStartTime =System.nanoTime();
		
		int i=0;
		while (i<simulationEndDateIndex)
		{
			simulationStep();				
			i++;
		}

		//po koncu symulacji timeIndex ma wskazywac na indeks w ktorym data = simulationEndDate
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
		
		if (Stale.isVirtualnyProsument)
		{
			getInput("Uzuplenij wirtualnego rposumenta");
		}
		else
		{
			if (isHandelWSieci)
			{
				getInput("uzuplenij");
			}
			else
			{
				listaProsumentowWrap.zaktualizujProsumentowBrakHandlu();
			}
		}
		
		
		timeIndex++;
	}
	
	
	void createHoursList()
	{
		print("createHoursList");
		dayHourList =loader.loadDayHourList();
		
		int i=0;
		while (i<dayHourList.size())
		{
			String dayHour =dayHourList.get(i);
			
			if (Stale.simulationEndDate.equals(dayHour))
			{
				simulationEndDateIndex=i;
			}
			
			dayList.add(dayFromDayHour(dayHour));
			hourList.add(hourFromDayHour(dayHour));
			
			i++;
		}
	}

	
	//ustaw wszystkie singletony
	void setUp()
	{
		print("setUP");
		
		reporter = Reporter.getInstance();
		listaProsumentowWrap = ListaProsumentowWrap.getInstance();
		rynek = Rynek.getInstance();		
		loader =Loader.getInstance();
	}
	
	
	
	
}
