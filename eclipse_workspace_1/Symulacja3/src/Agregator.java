import java.util.ArrayList;



public class Agregator extends CoreClass {
	
	private int scenariusz = Stale.scenariusz;
	
	//String outputFolder;
	private ArrayList<Prosument> listaProsumentow =ListaProsumentowWrap.getInstance().getListaProsumentow();
	String simulationEndDate=Stale.simulationEndDate;
	
	//SYSTEM
	Reporter reporter = Reporter.getInstance();

	
	//Singleton shit
	private static Agregator instance = null;
	private Agregator() 
	{
	}
	
	public static Agregator getInstance() {
	      if(instance == null) {
	         instance = new Agregator();
	      }
	      return instance;
	}
	//--------------------------
	//SETTERS
	
	public void setListaProsumentow(ArrayList<Prosument> listaProsumentow)
	{
		this.listaProsumentow =  listaProsumentow;
	}
	
	//--------------------------
	//OTHER FUNCTIONS
	
	//TODO
	//--------------------------
	//OTHER FUNCTIONS
	
	//TODO
	void run()
	{
		
		if (listaProsumentow==null)
		{
			getInput("run() error listaProsumentow==null");
		}
		
		
		//indexy prosumentow biorace udzial w agregacji
		Integer[] indexArray = createArray(0,listaProsumentow.size() );			
		createAgregateReport(100,indexArray,"all prosuments");
		
		
		indexArray = createArray(0,4);
		createAgregateReport(101,indexArray,"pierwszych 4");

		
		indexArray = createArray(4,16-4);
		createAgregateReport(102,indexArray,"ostatnich 12");
		
		//test do sprawdzenia poprawnosci agregacji
		indexArray = createArray(2,2);
		createAgregateReport(102,indexArray,"prosumenci 2_3");
	}
	
	//Uwaga! create array zwraca tablice indeksow a nie ID prosumentow!
	Integer[] createArray(int indexPoczatkowy, int liczbaElementow)
	{
		Integer[] indexArray = new Integer[liczbaElementow];	
		int a=0;
		while(a<liczbaElementow)
		{
			indexArray[a]=indexPoczatkowy+a;
			a++;
		}
		
		return indexArray;
	}
	
	void createAgregateReport(int ID, Integer[] indexArray, String reportNote)
	{
		if (Stale.isScenariuszEV)
		{
			createAgregateReportEV(ID,indexArray,reportNote);
		}
		else
		{
			createAgregateReportNoEV(ID, indexArray, reportNote);
		}
	}
	
	void createAgregateReportEV(int ID, Integer[] indexArray, String reportNote)
	{
		ProsumentEV prosument2 = createNewProsumentEV(ID);
		prosument2.setReportNote(reportNote);
		
		int i=0;
		while (i<indexArray.length)
		{	
			int IDdodawanegoProsumenta =indexArray[i];
			Prosument dodawanyProsument =listaProsumentow.get(IDdodawanegoProsumenta);
			
			addProsument(prosument2,dodawanyProsument,IDdodawanegoProsumenta  );
			
			if (dodawanyProsument instanceof ProsumentEV)
			{
				prosument2.addProsumentEV((ProsumentEV)dodawanyProsument,IDdodawanegoProsumenta );	
			}	
			i++;
		}
		
		prosument2.DayDataDivide(indexArray.length);
		prosument2.EVDataDivide(indexArray.length);
		
		prosument2.performEndOfSimulationCalculations(false);
		reporter.createProsumentReport(prosument2);
	}
	
	void createAgregateReportNoEV(int ID, Integer[] indexArray, String reportNote)
	{
		Prosument prosument2 = createNewProsument(ID);
		prosument2.setReportNote(reportNote);
		
		
		int a=0;
		while (a<indexArray.length)
		{	
			int IDdodawanegoProsumenta =indexArray[a];
			Prosument dodawanyProsument =listaProsumentow.get(IDdodawanegoProsumenta);
			
			addProsument(prosument2,dodawanyProsument,IDdodawanegoProsumenta  );
			
			a++;
		}
		
		//debug only
		if (ID==100)
		{
			prosument2.costCheck();
		}
		
		prosument2.DayDataDivide(indexArray.length);
				
		//false bo unused geenration licozna jako srednia a nie na podstawie danych
		prosument2.performEndOfSimulationCalculations(false);
		
		
		reporter.createProsumentReport(prosument2);
		
	}
	
	ProsumentEV createNewProsumentEV(int ID)
	{
		int rozmiarDayDataList = listaProsumentow.get(0).getDayDataList().size();
		
		ProsumentEV prosument = new ProsumentEV();
		prosument.setID(ID);
		prosument.createEmptyDataList(rozmiarDayDataList);
		
		return prosument;
	}
	
	//stowrz prostego rposumenta  z putsymi (zeorwymi) DayData w DayDataList
	Prosument createNewProsument(int ID)
	{
		int rozmiarDayDataList = listaProsumentow.get(0).getDayDataList().size();
		
		Prosument prosument = new Prosument();
		prosument.setID(ID);
		prosument.createEmptyDataList(rozmiarDayDataList);
		
		return prosument;
	}
	
	//dodaj DayDaty z DayData list prosumenta 2 do prosumenta 1 
	//ID for debug only
	void addProsument(Prosument prosument1, Prosument prosument2,int ID)
	{
		Boolean isDebug=false;
		
		ArrayList<DayData> dayDataList = prosument1.getDayDataList();
		ArrayList<DayData> dayDataList2 = prosument2.getDayDataList();

		int a=0;
		while (a<dayDataList.size())
		{
			DayData dayData = dayDataList.get(a);
			DayData dayData2 = dayDataList2.get(a);
			
			addDayData(dayData,dayData2,ID,a);
	
			a++;
		}
	}
	
	//ID and a for debug
	void addDayData(DayData d1, DayData d2,int ID, int a)
	{
		
		//getInput("WORK HERE!");
		
		//adding hours works becaus d2 always has an hour
		d1.setHour(d2.getHour());
		d1.setDay(d2.getDay());
		
		d1.setConsumption(d1.getConsumption()+d2.getConsumption());
		d1.setGeneration(d1.getGeneration()+d2.getGeneration());
		d1.setTrueGeneration(d1.getTrueGeneration()+d2.getTrueGeneration());
		
		d1.setStanBateriiNaPoczatkuSlotu(d1.getStanBateriiNaPoczatkuSlotu()+d2.getStanBateriiNaPoczatkuSlotu());
		d1.setStanBateriiNaKoniecSlotu(d1.getStanBateriiNaKoniecSlotu()+d2.getStanBateriiNaKoniecSlotu());
		
		d1.setZBateriiNaKonsumpcje(d1.getZBateriiNaKonsumpcje()+d2.getZBateriiNaKonsumpcje());
		d1.setZBateriiNaRynek(d1.getZBateriiNaRynek()+d2.getZBateriiNaRynek());
		
		
		d1.setZGeneracjiNaKonsumpcje(d1.getZGeneracjiNaKonsumpcje()+d2.getZGeneracjiNaKonsumpcje());
		d1.setZGeneracjiNaRynek(d1.getZGeneracjiNaRynek()+d2.getZGeneracjiNaRynek());
		d1.setZGeneracjiDoBaterii(d1.getZGeneracjiDoBaterii()+d2.getZGeneracjiDoBaterii());
		
		d1.setZRynekNaKonsumpcje(d1.getZRynekNaKonsumpcje()+d2.getZRynekNaKonsumpcje());
		d1.setZRynekDoBaterii(d1.getZRynekDoBaterii()+d2.getZRynekDoBaterii());
		
		d1.setCost(d1.getCost()+d2.getCost());
		d1.setUnusedGeneration(d1.getUnusedGeneration()+d2.getUnusedGeneration());
		
		d1.setCenaNaLokalnymRynku(d2.getCenaNaLokalnymRynku());

	}
	
}
