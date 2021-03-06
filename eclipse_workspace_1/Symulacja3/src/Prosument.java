import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;





public class Prosument extends CoreClass {
	float stosunekGeneracjiDoKonsumpcji=0f;
	protected ArrayList<DayData> dayDataList = new ArrayList<DayData>();
	
	
	//W Virtual Prosument handel jest zawsze na false
	protected int ID;
	private float cenaDystrybutoraZewnetrznego=Stale.cenaDystrybutoraZewnetrznego;
	private float mnoznikGeneracji=0f;
	
	
	
	//active user has: EV, battery or generation
	private Boolean active=false;

	
	//bateria
	float pojemnoscBaterii =0f;//faktyczna pojemnsoc baterii
	float predkoscBaterii = 0; //ile energii moze przekazac bateria w trakcie slotu czasowego
	String simulationEndDate=Stale.simulationEndDate; //set during load full date day + hour
	//String simulationEndDateJustDay;//set 
	float kosztAmortyzacjiBaterii =Stale.kosztAmortyzacjiBaterii;

	//Variables
	//int transactionIteration =0;//rosnie po kolejnych iteracjach
	
	//After simulation variables
	float totalCost=0; //totalny koszt  do zaplacenia ze wszystkich slotow czasowych
	float totalGeneration=0;
	float totalConsumption=0;
	float totalUnusedGeneration=0;
	
	//zwrot kosztow za energie jaka pozostala w baterii pod koniec symulajci
	float reserveBonus=0;
	float costNoReserve=0; //koszt do zaplacenia bez rezerwy

	
	//średnia cena z ostatniego tygodnia
	//sluzy do wyceny energii kotra pozostla w baterii
	static float cenaRezerwy = 0f;
	
	//mozna ustawic to sie pojawi an prosument report, uzywane w prosumentach-agregatach
	String reportNote="";
	
	//SYSTEM
	//Optimizer optimizer;
	Reporter reporter= Reporter.getInstance();
	Rynek rynek =Rynek.getInstance();
	Optimizer optimizer = Optimizer.getInstance();
	
	//TODO	
	//---------------
	//GETTERS
	
	public float getCostNoReserve()
	{
		return this.costNoReserve;
	}
	
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
	
	public int getID()
	{
		return this.ID;
	}
	
	/*
	public int getTransactionIteration()
	{
		return this.transactionIteration;
	}*/
	
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
	
	
	public ArrayList<DayData> getDayDataList()
	{
		return dayDataList;
	}
	
	
	//TODO
	//---------------
	//SETTERS
	
	public void setPredkoscBaterii(float value)
	{
		this.predkoscBaterii =value;
	}
	
	public void setPojemnoscBaterii(float value)
	{
		this.pojemnoscBaterii=value;
	}
	
	public void setDayDataList(ArrayList<DayData> dList)
	{
		this.dayDataList =dList;
	}
	
	public void setID(int value)
	{
		this.ID=value;
	}
	
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
	
	//wymagane do testow
	public void setActive(Boolean value)
	{
		this.active = value;
	}
		
	//---------------
	//OTHER FUNCTIONS
		
	//wczytaj dane do DayData (z pewna tolerancja)
	public void loadData()
	{
		
		String plikZeZuzyciem = Stale.folderZDanymi+"\\zuzycie\\"+ID+".csv";
		
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

		    			if (s2[0].equals(Stale.simulationEndDate))
		    			{
		    				endReached=true;
		    			}
		    		}
		    		
	    			String[] s2 = line.split(",");
	    			String[] s3= s2[0].split(" ");
	    			
	    			DayData d = new DayData();
	    			d.setDay(s3[0]);
	    			d.setHour(s3[1]);
	    			d.setConsumption(Float.valueOf(s2[2]));
	    			d.setGeneration(Float.valueOf(s2[1]));
	    			d.setID(ID);
	    			
	    			//DayData2 d = new DayData2(s3[0],s3[1],s2[2],s2[1],ID );	    			
	    			dayDataList.add(d);
	    			
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
        	print("Exception while loadData()");
        	
        }
	}
	
	
	float obliczCalkowitaKonsumpcje()
	{
		float sum=0f;
		
		int a=0;
		DayData d =dayDataList.get(a);
		String date=d.getDayHour();
		
		while (!date.equals(Stale.simulationEndDate))
		{
			d =dayDataList.get(a);
			sum+=d.getConsumption();
			
			a++;
			date = dayDataList.get(a).getDayHour();
		}
		return sum;
	}
	
	float obliczCalkowitaGeneracja()
	{
		float sum=0f;
		
		int a=0;
		DayData d =dayDataList.get(a);
		String date=d.getDayHour();
		
		while (!date.equals(Stale.simulationEndDate))
		{
			d =dayDataList.get(a);
			sum+=d.getGeneration();
			
			a++;
			date = dayDataList.get(a).getDayHour();
		}
		
		return sum;
	}

	public void przesunGeneracje()
	{
		//lista z generacja przesunieta o 12 godzin
		ArrayList<Float> L1_trueGeneration = new ArrayList<Float>();
		ArrayList<Float> L1_Generation = new ArrayList<Float>();
		
		//o ile goidzn przesunac
		int shiftValue=12;
		int a=12;
		DayData d2 =dayDataList.get(a);
		
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
			DayData d = dayDataList.get(a);
			d.setGeneration(generationList.get(a));
			d.setTrueGeneration(trueGenerationList.get(a));
			
			a++;
		}
	}
	
	public void aktywujBaterie()
	{
		pojemnoscBaterii=Stale.pojemnoscBateriiIfEnabled;
		predkoscBaterii = Stale.predkoscBaterii;
		active=true;
	}
	
	public void tick()
	{
		
		int timeIndex= LokalneCentrum.getTimeIndex();
		if (timeIndex+1<dayDataList.size())
		{	
			dayDataList.get(timeIndex+1).setStanBateriiNaPoczatkuSlotu(dayDataList.get(timeIndex).getStanBateriiNaKoniecSlotu());
			dayDataList.get(timeIndex+1).setStanBateriiNaKoniecSlotu(dayDataList.get(timeIndex).getStanBateriiNaKoniecSlotu());
		}
		
		charge();
		//dayDataList.get(timeIndex).countUnusedGeneration();
	}
	
	void charge()
	{
		dayDataList.get(LokalneCentrum.getTimeIndex()).charge(cenaDystrybutoraZewnetrznego,kosztAmortyzacjiBaterii);
	}
	
	public void zaktualizujHandelBrakHandlu()
	{
		
		DayData dayData2 = dayDataList.get(LokalneCentrum.getTimeIndex());
		
		float generation = dayData2.getTrueGeneration();
		float consumption = dayData2.getConsumption();
		
		if (generation>consumption)
		{
			
			float generacjaDoBaterii = generation-dayData2.getConsumption();
			
			dayData2.setZGeneracjiNaKonsumpcje(dayData2.getConsumption());
			dayData2.chargeBatteryZGeneracji(generacjaDoBaterii,predkoscBaterii,pojemnoscBaterii);
			
		}
		else
		{
			dayData2.setZGeneracjiNaKonsumpcje(generation);
			float nieZaspokojonaKonsumpcja = dayData2.getConsumption()-generation;
			dayData2.dischargeBatteryNaKonsumpcje(nieZaspokojonaKonsumpcja,predkoscBaterii);
		}
		
		dayData2.obliczStanBateriiNaKoniecSlotu();
	}
	
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
			
			DayData d = dayDataList.get(a);
			
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
		reserveBonus = dayDataList.get(LokalneCentrum.getTimeIndex()).getStanBateriiNaKoniecSlotu()*Stale.kosztAmortyzacjiBaterii;
		totalCost-=reserveBonus;	
	}
	
	//buduje pusta liste DayData skaldajaca o dlugosci size 
	public void createEmptyDataList(int size)
	{	
		int a=0;
		while (a<size)
		{
			DayData d = new DayData();
			dayDataList.add(d);
			a++;
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
	
	//TODO
	ArrayList<DayData> wyznaczSterowanieDlaAktywnegoProsumenta(ArrayList<Float> priceVector)
	{
		//sterowanie dla normalnego scenariusza
		if (Stale.scenariusz<100)
		{
			return optimizer.wyznaczSterowanie(priceVector,this);
		}
		else
		{
			float cenaNaNajblizszySlot =priceVector.get(0);
			return wyznaczSterowanieDlaTestowegoProsumenta(cenaNaNajblizszySlot);
		}
	}
	
	//wyznaczSterowanieDlaAktywnegoProsumenta
	
	
	ArrayList<DayData> wyznaczSterowanieDlaTestowegoProsumenta(float cenaNaNajblizszySlot)
	{
		
		ArrayList<DayData> L1 = new ArrayList<DayData>();
		DayData d = new DayData();
		L1.add(d);
		
		float iloscEnergiiNaHandel=iloscEnergiiNaHandelScenariuszTesowy(cenaNaNajblizszySlot);
		
		
		if (iloscEnergiiNaHandel>0)
		{
			d.setKupuj(1);
			d.setZRynekNaKonsumpcje(iloscEnergiiNaHandel);
		}
		else
		{
			d.setKupuj(0);
			//ilsoc na handel jest do sprzedazy jest ustawiana jako -, ale 
			//wszystkie zmienne msuza byc >0 wiec dlatego minus
			d.setZGeneracjiNaRynek(-iloscEnergiiNaHandel);
		}
		
		return L1;
	}
	
	float iloscEnergiiNaHandelScenariuszTesowy(float cenaNaNajblizszySlot)
	{
		switch (Stale.scenariusz)
		{
			//jeden sprzedaj drugi kupuje - funkcja Rynku ma miejsce zerowe
			case 101: return iloscEnergiiNaHandelScenariuszTesowy101(cenaNaNajblizszySlot);
			
			//funkcja rynku stale >0 
			case 102: return iloscEnergiiNaHandelScenariuszTesowy102(cenaNaNajblizszySlot);
			case 103: return iloscEnergiiNaHandelScenariuszTesowy103(cenaNaNajblizszySlot);

			default: getInput("iloscEnergiiNaHandelScenariuszTesowy - Zly scenariusz testowy"); return -1;
		} 
	}
	
	float iloscEnergiiNaHandelScenariuszTesowy101(float cenaNaNajblizszySlot)
	{
		float iloscEnergiiNaHandel=-1;
		if (ID==1)
		{
			iloscEnergiiNaHandel=5;
			if (cenaNaNajblizszySlot>0.3)
			{
				iloscEnergiiNaHandel=3;
			}
			
			if (cenaNaNajblizszySlot>0.35)
			{
				iloscEnergiiNaHandel=1;
			}
		}
		
		if (ID==2)
		{
			iloscEnergiiNaHandel=1;
			if (cenaNaNajblizszySlot>0.32)
			{
				iloscEnergiiNaHandel=-3;
			}
			
			if (cenaNaNajblizszySlot>0.4)
			{
				iloscEnergiiNaHandel=-6;
			}
		}
		
		return iloscEnergiiNaHandel;
	}
	
	float iloscEnergiiNaHandelScenariuszTesowy102(float cenaNaNajblizszySlot)
	{
		float iloscEnergiiNaHandel=-1;
		if (ID==1)
		{
			iloscEnergiiNaHandel=5;
			if (cenaNaNajblizszySlot>0.3)
			{
				iloscEnergiiNaHandel=3;
			}
		}
		
		if (ID==2)
		{
			iloscEnergiiNaHandel=1;
			if (cenaNaNajblizszySlot>0.4)
			{
				iloscEnergiiNaHandel=-1;
			}
		}
		
		return iloscEnergiiNaHandel;
	}
	
	float iloscEnergiiNaHandelScenariuszTesowy103(float cenaNaNajblizszySlot)
	{
		float iloscEnergiiNaHandel=-1;
		if (ID==1)
		{
			iloscEnergiiNaHandel=2;
			if (cenaNaNajblizszySlot>0.3)
			{
				iloscEnergiiNaHandel=1;
			}
		}
		
		if (ID==2)
		{
			iloscEnergiiNaHandel=-5;
			if (cenaNaNajblizszySlot>0.4)
			{
				iloscEnergiiNaHandel=-10;
			}
		}
		
		return iloscEnergiiNaHandel;
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
		
		
		//wyznacz steorwanie gdy prosument jest aktywny lub scneariusz testowy
		if (active)
		{						
			ArrayList<DayData> sterowanieForPriceVector =wyznaczSterowanieDlaAktywnegoProsumenta(priceVector);
			ArrayList<DayData> sterowanieForpriceVectorSmallerMod =wyznaczSterowanieDlaAktywnegoProsumenta(priceVectorSmallerMod);
			ArrayList<DayData> sterowanieForPriceVectorBiggerMod =wyznaczSterowanieDlaAktywnegoProsumenta(priceVectorBiggerMod);
			
			
			int transactionIteration = rynek.getIteracja();
			reporter.createSterowanieReport(sterowanieForPriceVector,this,"",priceVector, transactionIteration);
			reporter.createSterowanieReport(sterowanieForpriceVectorSmallerMod,this,"smaller",priceVectorSmallerMod, transactionIteration);			
			reporter.createSterowanieReport(sterowanieForPriceVectorBiggerMod,this,"bigger",priceVectorBiggerMod, transactionIteration);
			
			p1 = getPunktFunkcjiUzytecznosci(sterowanieForPriceVector,priceVector);
			p2 = getPunktFunkcjiUzytecznosci(sterowanieForpriceVectorSmallerMod,priceVectorSmallerMod);
			p3 = getPunktFunkcjiUzytecznosci(sterowanieForPriceVectorBiggerMod,priceVectorBiggerMod);
			
		}
		else
		{
			ArrayList<DayData> sterowaniePasywnegoProsumenta =wyznaczSterowanieDlaPasywnegoProsumenta();
			p1 = getPunktFunkcjiUzytecznosci(sterowaniePasywnegoProsumenta,priceVector);
			p2 = getPunktFunkcjiUzytecznosci(sterowaniePasywnegoProsumenta,priceVectorSmallerMod);
			p3 = getPunktFunkcjiUzytecznosci(sterowaniePasywnegoProsumenta,priceVectorBiggerMod);
		}
		
		
		rynek.addPricePoint(this,p1);
		rynek.addPricePoint(this,p2);
		rynek.addPricePoint(this,p3);
		
		//transactionIteration++;
		//getInput("takeFirstPriceVector -end");
		
	}
	
	public ArrayList<DayData> getDayDataListInT ()
	{
		int horyzont = Stale.horyzontCzasowy;
		ArrayList<DayData> L1= new ArrayList<DayData>();
		
		int a=0;
		while (a<horyzont)
		{
			L1.add(dayDataList.get(a+LokalneCentrum.getTimeIndex()));
			a++;
		}
		
		return L1;
	}
	
	Point getPunktFunkcjiUzytecznosci(ArrayList<DayData> L1, ArrayList<Float> priceVector)
	{
		Point point = new Point();
		
		point.setPrice(priceVector.get(0));	
		//uncommented for debug
		DayData d =L1.get(0);
		
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
	
	ArrayList<DayData> wyznaczSterowanieDlaPasywnegoProsumenta()
	{
		ArrayList<DayData> L1 = new ArrayList<DayData>();
		DayData d =new DayData();
		
		float konsumpcja = dayDataList.get(LokalneCentrum.getTimeIndex()).getConsumption();
		
		d.setKupuj(1);
		
		d.setZBateriiNaRynek(0f);
		d.setZGeneracjiNaRynek(0f);
		
		d.setZRynekDoBaterii(0f);
		d.setZRynekNaKonsumpcje(konsumpcja);

		L1.add(d);
		
		return L1;
	}	
	
	public void takePriceVector(ArrayList<Float> priceVector)
	{
		//getInputID("takePriceVector");
		
		//Keidys bylo potrzebne stan przekazywnianie stnau poczatkowego baterii ale chyba juz sam sobie oprtimizer wydlubuje
		//DayData d =dayDataList.get(LokalneCentruml);
		//float stanPoczatkowyBaterii=d.getStanBateriiNaPoczatkuSlotu();
		
		
		ArrayList<DayData> sterowanieForPriceVector;

		if (active)
		{	
			sterowanieForPriceVector =wyznaczSterowanieDlaAktywnegoProsumenta(priceVector);
			
			int transactionIteration = rynek.getIteracja();
			reporter.createSterowanieReport(sterowanieForPriceVector,this,"",priceVector, transactionIteration);

		}
		else
		{
			sterowanieForPriceVector =wyznaczSterowanieDlaPasywnegoProsumenta();
		}
		
		Point p1 = getPunktFunkcjiUzytecznosci(sterowanieForPriceVector,priceVector);
		rynek.addPricePoint(this,p1);
		
		//transactionIteration++;
	}	
	
	//wykonaj optymalizacje przy uwzglednieniu wynikow handlu
	public void getKontrakt(ArrayList<Float> priceVector, DayData constainMarker)
	{	
		int timeIndex =LokalneCentrum.getTimeIndex();
		
		float cenaNaRynkuLokalnym = priceVector.get(0);
		
		if (active)
		{

			ArrayList<DayData> dList =optimizer.wyznaczSterowanie(priceVector, this, constainMarker);
			DayData mergedDayData =getKontraktMerge(dList.get(0),dayDataList.get(timeIndex));
			
			//ustaw cene na rynku lokalnym
			mergedDayData.setCenaNaLokalnymRynku(cenaNaRynkuLokalnym);
			
			dayDataList.set(timeIndex, mergedDayData);

		}
		else
		{

			
			float nabytaEnergia = constainMarker.getGeneration();
			DayData d =dayDataList.get(timeIndex);
			nabytaEnergia = Math.min(nabytaEnergia,d.getConsumption());
			
			d.setZRynekNaKonsumpcje(nabytaEnergia);
			d.setCenaNaLokalnymRynku(cenaNaRynkuLokalnym);
			dayDataList.set(timeIndex, d);
		}
	}
	
	//zmwerguj wyniki optymalnego handlu przy ustalonej sprzedazy z 
	DayData getKontraktMerge(DayData sterowanie, DayData d2)
	{
			
		sterowanie.setDay(d2.getDay());
		sterowanie.setHour(d2.getHour());
		
		sterowanie.setConsumption(d2.getConsumption());
		sterowanie.setGeneration(d2.getGeneration());
		sterowanie.setTrueGeneration(d2.getTrueGeneration());
		return sterowanie;
	}
	
	//sprawdza czy w zadnym dniu nie ma ujemnego kosztu
	//uzywane przy agregacji
	public void costCheck()
	{
		int i=0;
		while (i<dayDataList.size())
		{
			if (dayDataList.get(i).getCost()+Stale.malaLiczba<0)
			{
				
				print("\nERROR in costCheck");
				print("Fail at index "+i+"\nprosument ID "+ID);
				print("cost for agregate prosument must be =>0");
				print("cost "+dayDataList.get(i).getCost());
				getInput("");
				
			}
			
			i++;
		}
	}
	
	
}
