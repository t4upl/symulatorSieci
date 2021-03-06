import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;


public class LokalneCentrum extends CoreClass {

	//ustawiane  w setUp()
	private int scenariusz;
	
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

	private long simulationStartTime;

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
	
	public static ArrayList<String> getHourList2()
	{
		return hourList2;
	}
	
	public static ArrayList<String> getDayList()
	{
		return dayList;
	}
	
	
	public static ArrayList<String> getHourList()
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
		
		
		stworzVirutalProsument();
		
		startSimulation();
		
		
		endSimulation();
				
		System.out.println("\n-----\nLokalne CentrumDystryucji - end");
		
	}
	
	void endSimulation()
	{
		long timeElapsed =(long) ((System.nanoTime()- simulationStartTime)/(long)Math.pow(10, 9));
		
		//make all prosuments do the end of simualtion calculations and write report 
		listaProsumentowWrap.endSimulationReport();
		
		Agregator agregator =Agregator.getInstance();
		agregator.run();
		
	}
	
	void stworzVirutalProsument()
	{
		listaProsumentowWrap.stworzVirutalProsument();
	}
	
	void modyfikatorScenariusza()
	{
		listaProsumentowWrap.modyfikatorScenariusza();
		
	}
	
	//ustaw wszystkie singletony
	void setUp()
	{
		stale=Stale.getInstance();
		reporter = Reporter.getInstance();
		listaProsumentowWrap = ListaProsumentowWrap.getInstance();
		
		scenariusz=Stale.scenariusz;
	}
	
	//returns Fodler name that will be just under OutputFolder
	String returnOutPutSubFolder()
	{
		return "Virtual";
	}
	
	//Fodler creation moved to reporter
	void createOutputFolder()
	{
		String newlyCreatedFolder=Stale.outputFolder+"\\"+returnOutPutSubFolder()+"\\Scenario_"+scenariusz;
		
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
		//ListaProsumentowWrap=new ListaProsumentow2(mainFolder+"\\zuzycie");	
		//rynek.setListaProsumentow(listaProsumentow2);
	}
	
	void startSimulation()
	{	
		simulationStartTime =System.nanoTime();
		
		print("startSimulation\n");
		int a=0;
		while(a<hourList.size())
		{
			//print(hourList.get(a));
			
			simulationStep();				
			a++;
		}
	}
	
	//TODO simulation step
	void simulationStep()
	{
		
		//Zeby byl jakis output keidy symualcja dziala
		if (timeIndex%6==0)
		{
			print ("simualtion step "+hourList.get(timeIndex));
		}
		
		zaktualizujProsumentowBrakHandlu();
		
		Tick();
	}
	
	void zaktualizujProsumentowBrakHandlu()
	{
		listaProsumentowWrap.zaktualizujProsumentowBrakHandlu();
	}
	
	void Tick()
	{
		listaProsumentowWrap.tickProsuments();
		timeIndex++;
	}
	

}
