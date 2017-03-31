package symulacja2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class AgregatorReporter {

	private int scenariusz = Stale.scenariusz;
	
	//String outputFolder;
	private ArrayList<Prosument2> listaProsumentow;
	String simulationEndDate=Stale.simulationEndDate;
	
	//ArrayList<String> hourList;
	Scanner sc = new Scanner (System.in);
	Reporter reporter = Reporter.getInstance();
	
	//Singleton shit
	private static AgregatorReporter instance = null;
	private AgregatorReporter() 
	{
	}
	
	public static AgregatorReporter getInstance() {
	      if(instance == null) {
	         instance = new AgregatorReporter();
	      }
	      return instance;
	}
	//--------------------------
	//SETTERS
	
	public void setListaProsumentow(ArrayList<Prosument2> listaProsumentow)
	{
		this.listaProsumentow =  listaProsumentow;
	}
	
	//--------------------------
	//OTHER FUNCTIONS
	
	//TODO
	void run()
	{
		everyoneReport();
		scenarioOne();
		
		/*switch (scenariusz)
		{
			case 1: scenarioOne(); break;
			case 2: scenarioOne(); break;
			default: print("!!!NO SUCH SCENARIO");
		}*/
	}
	
	void scenarioOne()
	{
		//Uwaga! create array zwraca tablice indeksow a nie ID prosumentow!
		Integer[] indexArray = createArray(0,4);
		createAgregateReport(101,indexArray,"pierwszych 4");
		
		indexArray = createArray(4,16-4);
		createAgregateReport(102,indexArray,"ostatnich 12");
		
		indexArray = createArray(5,2);
		createAgregateReport(102,indexArray,"prosumenci 5_6");
		
		/*
		//This is for testinfg only
		indexArray = createArray(2,2);
		createAgregateReport(200,indexArray,"");*/
		
	}
	
	
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
	
	//usredniony wynik prosumentow jako grupy
	void everyoneReport()
	{	
		
		//indexy prosumentow biorace udzial w agregacji
		Integer[] indexArray = createArray(0,listaProsumentow.size() );

		
		createAgregateReport(100,indexArray,"all prosuments");
	}
	
	
	Prosument2 createNewProsument(int ID)
	{
		Prosument2 prosument2 = new Prosument2(ID);
		//prosument2.setSimulationEndDateJustDay(simulationEndDate);
		prosument2.deepCopyProsument2(listaProsumentow.get(0));
		//prosument2.setOutputFolder(outputFolder);
		//prosument2.resetDayDataListToZero();
		
		return prosument2;
	}
	
	void createAgregateReport(int ID, Integer[] indexArray, String reportNote)
	{
		Prosument2 prosument2 = createNewProsument(ID);
		prosument2.setReportNote(reportNote);
		
		int a=0;
		while (a<indexArray.length)
		{	
			//print(indexArray[a].toString());
			


			int IDdodawanegoProsumenta =indexArray[a];
			Prosument2 dodawanyProsument =listaProsumentow.get(IDdodawanegoProsumenta);
			
			addProsument(prosument2,dodawanyProsument,IDdodawanegoProsumenta  );
			
		
			a++;
		}
		
		prosument2.DayDataDivide(indexArray.length);
		
		//false bo unused geenration licozna jako srednia a nie na podstawie danych
		prosument2.performEndOfSimulationCalculations(false);
		
		
		reporter.createProsumentReport(prosument2);

	}
	
	//ID for debug only
	void addProsument(Prosument2 prosument1, Prosument2 prosument2,int ID)
	{
		ArrayList<DayData2> dayDataList = prosument1.getDayDataList();
		ArrayList<DayData2> dayDataList2 = prosument2.getDayDataList();

		
		
		int a=0;
		while (a<dayDataList.size())
		{
			DayData2 dayData = dayDataList.get(a);
			DayData2 dayData2 = dayDataList2.get(a);
			
			/*if (a==8)
			{
				print("addProsument ID:"+ID+" "+dayData2.getTrueGeneration());
				print("addProsument d1 "+dayData.getTrueGeneration());
				print("addProsument d2 "+dayData2.getTrueGeneration());
				getInput("hello");
			}*/
			
			addDayData(dayData,dayData2,ID,a);
			
			/*
			if (a==8)
			{
				print("post add addProsument d2 "+dayData2.getTrueGeneration());
				getInput("stop");
			}*/
			
			a++;
		}
	}
	
	//ID and a for debug
	void addDayData(DayData2 d1, DayData2 d2,int ID, int a)
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
	
	
	void print(String s)
	{
		System.out.println("AgregatorReporter "+s);
	}
	
	void getInput(String s)
	{
		print("getInput "+s);
		sc.nextLine();
	}
	
	void getInput()
	{
		getInput("");
	}

	
}
