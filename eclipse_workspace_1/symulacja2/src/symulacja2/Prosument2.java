package symulacja2;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Scanner;

public class Prosument2 {
	
	float stosunekGeneracjiDoKonsumpcji=0f;
	ArrayList<DayData2> dayDataList = new ArrayList<DayData2>();
	
	//Stale
	private Boolean handel=true;
	private int ID;
	private float cenaDystrybutoraZewnetrznego=Stale.cenaDystrybutoraZewnetrznego;
	private float mnoznikGeneracji=0f;
	
	
	private int horyzontCzasowy=Stale.horyzontCzasowy;
	
	//active user has: EV, battery or generation
	private Boolean active=false;

	
	//bateria
	//float pojemnoscBateriiIfEnabled =13.5f ; //pojemnosc baterii, jezeli bylaby aktywowana
	float predkoscBaterii = 5f; //ile energii moze przekazac bateria w trakcie slotu czasowego
	String simulationEndDate=Stale.simulationEndDate; //set during load full date day + hour
	String simulationEndDateJustDay;//set 
	float kosztAmortyzacjiBaterii =Stale.kosztAmortyzacjiBaterii;

	//Variables
	int timeIndex=0; //okresla moment czasu
	float pojemnoscBaterii =0f;//faktyczna pojemnsoc baterii
	int transactionIteration =0;//rosnie po kolejnych iteracjach
	
	//After simulation variables
	float totalCost=0; //totalny koszt  do zaplacenia ze wszystkich slotow czasowych
	float totalGeneration=0;
	float totalConsumption=0;
	float totalUnusedGeneration=0;
	
	//zwrot kosztow za energie jaka pozostala w baterii pod koniec symulajci
	float reserveBonus=0;
	
	//średnia cena z ostatniego tygodnia
	//sluzy do wyceny energii kotra pozostla w baterii
	static float cenaRezerwy = 0f;
	
	//mozna ustawic to sie pojawi an prosument report, uzywane w prosumentach-agregatach
	String reportNote="";
	
	//SYSTEM
	Scanner sc = new Scanner(System.in);
	//Optimizer optimizer;
	Reporter reporter= Reporter.getInstance();
	Rynek rynek = Rynek.getInstance();
	Optimizer2 optimizer2=Optimizer2.getInstance();

	
	Prosument2()
	{
		
	}
	
	Prosument2(int ID)
	{
		this.ID =ID;
	}
	
	//TODO
	//------------
	//SETTERS
	public void setReportNote(String s)
	{
		this.reportNote=s;
	}
	
	public void setMnoznikGeneracji(float value)
	{
		this.mnoznikGeneracji=value;
		
		if (value>0)
		{
			active=true;
		}
		
		int a=0;
		while (a<dayDataList.size())
		{
			dayDataList.get(a).countTrueGeneration(mnoznikGeneracji);
			a++;
		}
	}
	
	public void setCenaDystrybutoraZewnetrznego(float value)
	{
		this.cenaDystrybutoraZewnetrznego=value;
	}
	
	public void setSimulationEndDateJustDay(String simulationEndDateJustDay)
	{
		this.simulationEndDateJustDay=simulationEndDateJustDay;
	}
	
	//TODO
	//-------------
	//GETTERS
	public String getReportNote()
	{
		return this.reportNote;
	}
	
	public float getReserveBonus()
	{
		return this.reserveBonus;
	}
	
	public float getTotalUnusedGeneration()
	{
		return this.totalUnusedGeneration;
	}
	
	public float getTotalConsumption()
	{
		return this.totalConsumption;
	}
	
	public float getTotalGeneration()
	{
		return this.totalGeneration;
	}
	
	public float getTotalCost()
	{
		return this.totalCost;
	}
	
	public float getMnoznikGeneracji()
	{
		return this.mnoznikGeneracji;
	}
	
	public int getHoryzontCzasowy()
	{
		return this.horyzontCzasowy;
	}
	
	public int getTimeIndex()
	{
		return timeIndex;
	}
	

	
	public int getID()
	{
		return this.ID;
	}
	
	public int getTransactionIteration()
	{
		return this.transactionIteration;
	}
	
	public float getPojemnoscBaterii()
	{
		return this.pojemnoscBaterii;
	}
	
	public float getKosztAmortyzacjiBaterii()
	{
		return this.kosztAmortyzacjiBaterii;
	}
	
	public float getCenaDystrybutoraZewnetrznego()
	{
		return this.cenaDystrybutoraZewnetrznego;
	} 
	
	public float getPredkoscBaterii()
	{
		return this.predkoscBaterii;
	} 
	
	
	public ArrayList<DayData2> getDayDataList()
	{
		return dayDataList;
	}
	
	//ACTIVATE/DISABLE
	public void aktywujBaterie()
	{
		pojemnoscBaterii=Stale.pojemnoscBateriiIfEnabled;
		active=true;
	}
	
	public void disableHandel()
	{
		this.handel=false;
	}
	
	float obliczCalkowityRachunek()
	{
		float sum=0f;
		
		String date ="";
		int a=0;
		while(!simulationEndDateJustDay.equals(date))
		{
			sum+=dayDataList.get(a).getCost();
			date=dayDataList.get(a+1).getDay();
			a++;
		}
		return sum;
	}
	
	//to jest takie sobie,b o sprawdza tylko dzien, bez godizny
	float obliczCalkowitaKonsumpcje()
	{
		float sum=0f;
		
		String date ="";
		int a=0;
		while(!simulationEndDateJustDay.equals(date))
		{
			sum+=dayDataList.get(a).getConsumption();
			date=dayDataList.get(a+1).getDay();
			a++;
		}
		return sum;
	}
	
	
	//---------------------------------------------------------------------------
	//OTHER FUNCTIONS

	public void performEndOfSimulationCalculations()
	{
		performEndOfSimulationCalculations(true);
	}
	
	//TODO
	//count everything that needs to eb coutned at the end of simulation
	//countGenerationFlag with FALSE is used in agregator
	public void performEndOfSimulationCalculations(Boolean countGenerationFlag)
	{
		
		totalCost=0;
		totalConsumption=0;
		totalGeneration=0;
		totalUnusedGeneration=0;
		
		int a=0;
		
		String date="";
		while (!date.equals(Stale.simulationEndDate))
		{
			
			DayData2 d = dayDataList.get(a);
			
			totalCost+=d.getCost();
			totalConsumption+=d.getConsumption();
			totalGeneration+=d.getTrueGeneration();
			
			if (countGenerationFlag)
			{
				d.countUnusedGeneration();
			}
			totalUnusedGeneration+=d.getUnusedGeneration();

			
			a++;
			d = dayDataList.get(a);
			date=d.getDay()+" "+d.getHour();
		}
		
		countReserveBonus();
		
	}
	
	
	//odpala sie po wylcizeniu totalnego kosztu 
	void countReserveBonus()
	{
		reserveBonus = dayDataList.get(timeIndex).getStanBateriiNaKoniecSlotu()*Stale.kosztAmortyzacjiBaterii;
		totalCost-=reserveBonus;
		
	}
	
	public void resetDayDataListToZero()
	{
		int a=0;
		while (a<dayDataList.size())
		{
			dayDataList.get(a).resetDayDataToZero();
			
			a++;
		}
	}
	
	
	public void tick()
	{

		if (timeIndex+1<dayDataList.size())
		{
			
			dayDataList.get(timeIndex+1).setStanBateriiNaPoczatkuSlotu(dayDataList.get(timeIndex).getStanBateriiNaKoniecSlotu());
			dayDataList.get(timeIndex+1).setStanBateriiNaKoniecSlotu(dayDataList.get(timeIndex).getStanBateriiNaKoniecSlotu());

		}
		transactionIteration=0;
		
		//policz oplate
		charge();
		
		dayDataList.get(timeIndex).countUnusedGeneration();
		
		timeIndex++;
	}
	
	
	void charge()
	{
		dayDataList.get(timeIndex).charge(cenaDystrybutoraZewnetrznego,kosztAmortyzacjiBaterii);
	}
	
	
	void zaktualizujHandelNoHandel()
	{
		
		DayData2 dayData2 = dayDataList.get(timeIndex);
		
		float generation = dayData2.getTrueGeneration();
		
		if (generation>dayData2.getConsumption())
		{
			
			float generacjaDoBaterii = generation-dayData2.getConsumption();
			
			dayData2.setZGeneracjiNaKonsumpcje(dayData2.getConsumption());
			dayData2.chargeBatteryZGeneracji(generacjaDoBaterii);
			
		}
		else
		{
			dayData2.setZGeneracjiNaKonsumpcje(generation);
			float nieZaspokojonaKonsumpcja = dayData2.getConsumption()-generation;
			dayData2.dischargeBatteryNaKonsumpcje(nieZaspokojonaKonsumpcja);
		}

	}
	
	
	public void zaktualizujHandel()
	{
		if (handel)
		{
			getInput("zaktualizujHandel - you shouldn't be here");
		}
		else
		{
			zaktualizujHandelNoHandel();
		}
	}
	
	public void DayDataDivide(int divider)
	{
		int a=0;
		while (a<dayDataList.size())
		{
			dayDataList.get(a).divide(divider);
			a++;
		}
	}
	
	//used by Aggregator only
	public void deepCopyProsument2(Prosument2 p) 
	{
		dayDataList=new ArrayList<DayData2>();
		ArrayList<DayData2> L1 = p.getDayDataList();
		
		int a=0;
		while (a<L1.size())
		{
			DayData2 d = new DayData2();

			dayDataList.add(d);
			a++;
		}
		
		
		//dayDataList=new ArrayList<DayData2>(p.getDayDataList());
	}
	
	public void loadData(String folderZeZuzyciem ) //used by normal prosuments
	{
		
		String[] s22 =simulationEndDate.split(" ");
		this.simulationEndDateJustDay=s22[0];
		
		String plikZeZuzyciem = folderZeZuzyciem+"\\"+ID+".csv";
		
        BufferedReader br = null;
        String line="";
        
        Boolean body=false;
        Boolean endReached=false;
        
        //turns falls if end Reached and endCounter==endLimit
        Boolean continuationFlag=true;

        
        int endCounter=1;//yes it should be one!
        
        //Ile rekordow po osiagnieciu endDate zostanie zassanych. ustaw jako wielokrotnosc 24
        int endLimit=24*3; 

        
        
        try 
		{
			  
			br = new BufferedReader(new FileReader(plikZeZuzyciem));
			int a=0;
		    while (((line = br.readLine()) != null) && continuationFlag )
		    {
		    	if (body)
		    	{
		    		if (endReached)
		    		{
		    			endCounter++;
		    			if (endCounter==endLimit)
		    			{
		    				continuationFlag=false;
		    			}
		    		}
		    		else
		    		{
		    			String[] s2 = line.split(",");

		    			if (s2[0].equals(simulationEndDate))
		    			{
		    				
		    				endReached=true;
		    			}
		    		}
		    		
	    			String[] s2 = line.split(",");
	    			String[] s3= s2[0].split(" ");
	    			
	    			DayData2 d = new DayData2(s3[0],s3[1],s2[2],s2[1],ID );
	    			
	    			//d.setTrueGeneration(mnoznikGeneracji);
	    			dayDataList.add(d);
	    			
	    			//print(line);

		    	}
		    	else
		    	{
		    		if (line.equals("###"))
					{
						body=true;
					}
		    	}
		    	
		    	a++;
		    }
		    
		    br.close();
        }
        catch (Exception e)
        {	
        	System.out.println(ID+" E! loadData");
        }
		
	}
	
	
	
	void print(String s)
	{
		System.out.println("Prosument2 ID: "+ID+" "+s);
	}
	

	/*
	public void consistencyCheck()
	{
		batteryCapacityConsistencyCheck();
		batteryChargingSpeedConsistencyCheck();
	}
	
	void batteryCapacityConsistencyCheck()
	{
		/*int a=0;
		while (a<)
		{
			a++;
		}*/
	/*	
		String date ="";
		int a=0;
		while (!simulationEndDateJustDay.equals(date))
		{
			if (dayDataList.get(a).getStanBateriiNaPoczatkuSlotu()>pojemnoscBaterii)
			{
				System.out.println("batteryCapacityConsistencyCheck");
				System.out.println(ID+" "+dayDataList.get(a).getDay()+" "+dayDataList.get(a).getHour());
				sc.hasNextLine();
			}
			//writer.write(dayDataList.get(a).reportString()+","+mnoznikGeneracji*dayDataList.get(a).getGeneration()+System.lineSeparator());
			date=dayDataList.get(a+1).getDay();
			a++;
		}
	}
	/*
	void batteryChargingSpeedConsistencyCheck()
	{
		String date ="";
		int a=1;
		while (!simulationEndDateJustDay.equals(date))
		{
			if (Math.abs(dayDataList.get(a).getStanBateriiNaPoczatkuSlotu()-dayDataList.get(a-1).getStanBateriiNaPoczatkuSlotu())>predkoscBaterii)
			{
				System.out.println("batteryChargingSpeedConsistencyCheck");
				System.out.println(ID+" "+dayDataList.get(a).getDay()+" "+dayDataList.get(a).getHour());
				sc.hasNextLine();
			}
			//writer.write(dayDataList.get(a).reportString()+","+mnoznikGeneracji*dayDataList.get(a).getGeneration()+System.lineSeparator());
			date=dayDataList.get(a+1).getDay();
			a++;
		}
	}
	*/
	
	public void takePriceVector(ArrayList<Float> priceVector)
	{
		//getInputID("takePriceVector");
		
		
		DayData2 d =dayDataList.get(timeIndex);
		float stanPoczatkowyBaterii=d.getStanBateriiNaPoczatkuSlotu();
		
		
		ArrayList<DayData2> sterowanieForPriceVector;

		if (active)
		{	
			sterowanieForPriceVector =optimizer2.wyznaczSterowanie(priceVector,this);
			reporter.createSterowanieReport(sterowanieForPriceVector,this,"",priceVector, transactionIteration);

		}
		else
		{
			sterowanieForPriceVector =wyznaczSterowanieDlaPasywnegoProsumenta();
		}
		
		Point p1 = getPunktFunkcjiUzytecznosci(sterowanieForPriceVector,priceVector);
		rynek.addPricePoint(this,p1);
		
		transactionIteration++;

	}
	
	//price vector ma postac (0) -mniejsza cena dla najblizszego slotu, (1) -normlana, (2) -wieksza
	public void takeFirstPriceVector(ArrayList<ArrayList<Float>> ListOfPriceVectors)
	{
		Point p1 = null;
		Point p2 = null;
		Point p3 = null;
		
		ArrayList<Float> priceVector = ListOfPriceVectors.get(1);
		
		ArrayList<Float> priceVectorSmallerMod= ListOfPriceVectors.get(0);
		ArrayList<Float> priceVectorBiggerMod= ListOfPriceVectors.get(2);
		
		
		if (active)
		{		
						
			ArrayList<DayData2> sterowanieForPriceVector =optimizer2.wyznaczSterowanie(priceVector,this);
			ArrayList<DayData2> sterowanieForpriceVectorSmallerMod =optimizer2.wyznaczSterowanie(priceVectorSmallerMod,this);
			ArrayList<DayData2> sterowanieForPriceVectorBiggerMod =optimizer2.wyznaczSterowanie(priceVectorBiggerMod,this);
			
				
			reporter.createSterowanieReport(sterowanieForPriceVector,this,"",priceVector, transactionIteration);
			reporter.createSterowanieReport(sterowanieForpriceVectorSmallerMod,this,"smaller",priceVectorSmallerMod, transactionIteration);			
			reporter.createSterowanieReport(sterowanieForPriceVectorBiggerMod,this,"bigger",priceVectorBiggerMod, transactionIteration);
			
			p1 = getPunktFunkcjiUzytecznosci(sterowanieForPriceVector,priceVector);
			p2 = getPunktFunkcjiUzytecznosci(sterowanieForpriceVectorSmallerMod,priceVectorSmallerMod);
			p3 = getPunktFunkcjiUzytecznosci(sterowanieForPriceVectorBiggerMod,priceVectorBiggerMod);
			

		}
		else
		{
			ArrayList<DayData2> sterowaniePasywnegoProsumenta =wyznaczSterowanieDlaPasywnegoProsumenta();
			p1 = getPunktFunkcjiUzytecznosci(sterowaniePasywnegoProsumenta,priceVector);
			p2 = getPunktFunkcjiUzytecznosci(sterowaniePasywnegoProsumenta,priceVectorSmallerMod);
			p3 = getPunktFunkcjiUzytecznosci(sterowaniePasywnegoProsumenta,priceVectorBiggerMod);
		}
		
		//Uncomment in future!
		rynek.addPricePoint(this,p1);
		rynek.addPricePoint(this,p2);
		rynek.addPricePoint(this,p3);
		
		transactionIteration++;
		//getInput("takeFirstPriceVector -end");
		
	}
	
	ArrayList<DayData2> wyznaczSterowanieDlaPasywnegoProsumenta()
	{
		ArrayList<DayData2> L1 = new ArrayList<DayData2>();
		DayData2 d =new DayData2();
		
		float konsumpcja = dayDataList.get(timeIndex).getConsumption();
		
		d.setKupuj(1);
		
		d.setZBateriiNaRynek(0f);
		d.setZGeneracjiNaRynek(0f);
		
		d.setZRynekDoBaterii(0f);
		d.setZRynekNaKonsumpcje(konsumpcja);

		L1.add(d);
		
		return L1;
	}
	
	//zmwerguj wyniki optymalnego handlu przy ustalonej sprzedazy z 
	DayData2 getKontraktMerge(DayData2 sterowanie, DayData2 d2)
	{
			
		sterowanie.setDay(d2.getDay());
		sterowanie.setHour(d2.getHour());
		
		sterowanie.setConsumption(d2.getConsumption());
		sterowanie.setGeneration(d2.getGeneration());
		sterowanie.setTrueGeneration(d2.getTrueGeneration());
		return sterowanie;
	}
	
	//wykonaj optymalizacje przy uwzglednieniu wynikow handlu
	public void getKontrakt(ArrayList<Float> priceVector, DayData2 constainMarker)
	{	
		
		float cenaNaRynkuLokalnym = priceVector.get(0);
		
		if (active)
		{

			ArrayList<DayData2> dList =optimizer2.wyznaczSterowanie(priceVector, this, constainMarker);
			DayData2 mergedDayData =getKontraktMerge(dList.get(0),dayDataList.get(timeIndex));
			
			//ustaw cene na rynku lokalnym
			mergedDayData.setCenaNaLokalnymRynku(cenaNaRynkuLokalnym);
			
			dayDataList.set(timeIndex, mergedDayData);

		}
		else
		{

			
			float nabytaEnergia = constainMarker.getGeneration();
			DayData2 d =dayDataList.get(timeIndex);
			nabytaEnergia = Math.min(nabytaEnergia,d.getConsumption());
			
			d.setZRynekNaKonsumpcje(nabytaEnergia);
			d.setCenaNaLokalnymRynku(cenaNaRynkuLokalnym);
			dayDataList.set(timeIndex, d);
		}
	}
	
	public String getCurrentDay()
	{
		return dayDataList.get(timeIndex).getDay();
	}
	
	public String getCurrentHour()
	{
		return dayDataList.get(timeIndex).getHour();
	}
	
	Point getPunktFunkcjiUzytecznosci(ArrayList<DayData2> L1, ArrayList<Float> priceVector)
	{
		Point point = new Point();
		
		point.setPrice(priceVector.get(0));	
		//uncommented for debug
		DayData2 d =L1.get(0);
		
		float sprzedaz = d.getZBateriiNaRynek()+d.getZGeneracjiNaRynek();
		float kupno = d.getZRynekDoBaterii()+d.getZRynekNaKonsumpcje();
		
		float wolumenEnergii;
		
		//0.5, bo blad w reprezentacji floata
		if (d.getKupuj()>0.5)
		{
			wolumenEnergii=kupno;
		}
		else
		{
			wolumenEnergii=-sprzedaz;

		}
		point.setIloscEnergiiDoKupienia(wolumenEnergii);
		
		return point;
	}
	
	
	
	public ArrayList<DayData2> getDayDataListInT (int horyzont)
	{
		ArrayList<DayData2> L1= new ArrayList<DayData2>();
		
		int a=0;
		while (a<horyzont)
		{
			L1.add(dayDataList.get(a+timeIndex));
			a++;
		}
		
		return L1;
	}
	
	
	public void przesunGeneracje()
	{
		//lista z generacja przesunieta o 12 godzin
		ArrayList<Float> L1_trueGeneration = new ArrayList<Float>();
		ArrayList<Float> L1_Generation = new ArrayList<Float>();
		
		//o ile goidzn przesunac
		int shiftValue=12;
		int a=12;
		DayData2 d2 =dayDataList.get(a);
		
		Boolean endDateReached = false;
		
		while (!endDateReached)
		{
			d2 =dayDataList.get(a);
			String dayHourString = d2.getDay()+" "+d2.getHour();
			
			if (dayHourString.equals(simulationEndDate))
			{
				endDateReached=true;
			}
			
			if (!endDateReached)
			{
				L1_trueGeneration.add(d2.getTrueGeneration());
				L1_Generation.add(d2.getGeneration());

			}
			
			a++;
		}
		
		a=0;
		while (a<shiftValue)
		{
			d2 =dayDataList.get(a);
			L1_trueGeneration.add(d2.getTrueGeneration());
			L1_Generation.add(d2.getGeneration());
			a++;
		}
		
		przesunGeneracjeWstrzyknijlisty(L1_Generation,L1_trueGeneration);
				
	}
	
	//przypisuje wartosci z list jako generation i trueGeneration do dayDataList
	void przesunGeneracjeWstrzyknijlisty(ArrayList<Float> generationList, ArrayList<Float> trueGenerationList)
	{
		int a=0;
		while (a<generationList.size())
		{
			DayData2 d = dayDataList.get(a);
			d.setGeneration(generationList.get(a));
			d.setTrueGeneration(trueGenerationList.get(a));
			
			a++;
		}
		
	}
	
	public void debugWypiszGeneracje()
	{
		print("debugWypiszGeneracje");
		int a=0;
		while (a<dayDataList.size())
		{
			DayData2 d = dayDataList.get(a);
			print(a+" "+d.getDay()+" "+d.getHour()+" "+d.getConsumption()+" "+d.getGeneration()+" "+d.getTrueGeneration());
			a++;
		}
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
	
	void getInputID(String s)
	{
		if (ID ==1)
		{
			getInput(s);
		}
	}
	
	void printID(String s)
	{
		if (ID ==1)
		{
			print(s);
		}
	}
	
	float obliczCalkowitaGeneracja()
	{
		float sum=0f;
		
		int a=0;
		DayData2 d =dayDataList.get(a);
		String date=d.getDayHour();
		
		while (!date.equals(Stale.simulationEndDate))
		{
			d =dayDataList.get(a);
			sum+=d.getGeneration();
			
			a++;
			date = dayDataList.get(a).getDayHour();;
		}
		
		return sum;
	}
}
