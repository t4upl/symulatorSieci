import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;




public class Prosument extends CoreClass {
	
	float stosunekGeneracjiDoKonsumpcji=0f;
	protected ArrayList<DayData> dayDataList = new ArrayList<DayData>();
	
	
	//W Virtual Prosument handel jest zawsze na false
	private Boolean handel=false;
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
	int transactionIteration =0;//rosnie po kolejnych iteracjach
	
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
	
	public void debugWypiszGeneracje()
	{
		print("debugWypiszGeneracje");
		int a=0;
		while (a<dayDataList.size())
		{
			DayData d = dayDataList.get(a);
			print(a+" "+d.getDay()+" "+d.getHour()+" "+d.getConsumption()+" "+d.getGeneration()+" "+d.getTrueGeneration());
			a++;
		}
	}
	
	public void aktywujBaterie()
	{
		pojemnoscBaterii=Stale.pojemnoscBateriiIfEnabled;
		predkoscBaterii = Stale.predkoscBaterii;
		active=true;
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
			date = dayDataList.get(a).getDayHour();;
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
			date = dayDataList.get(a).getDayHour();;
		}
		
		return sum;
	}
	
	//dodaje generacje, konsumpcje i true generacje do DayDataLsit
	public void addDayDataList(ArrayList<DayData> dList)
	{
		int i=0;
		while (i<dList.size())
		{
			DayData d =dayDataList.get(i); 
			DayData d2 =dList.get(i);
			
			d.setGeneration(d.getGeneration()+d2.getGeneration());
			d.setTrueGeneration(d.getTrueGeneration()+d2.getTrueGeneration());
			d.setConsumption(d.getConsumption()+d2.getConsumption());
			
			i++;
		}
		
	}
	
	void zaktualizujHandelBrakHandluZwyklyScenariusz()
	{
		DayData dayData2 = dayDataList.get(LokalneCentrum.getTimeIndex());
		
		float generation = dayData2.getTrueGeneration();
		float consumption = dayData2.getConsumption();
		
		if (generation>consumption)
		{
			
			float generacjaDoBaterii = generation-dayData2.getConsumption();
			
			dayData2.setZGeneracjiNaKonsumpcje(dayData2.getConsumption());
			
			/*
			if (LokalneCentrum.getCurrentDay().equals("2015-06-06") && LokalneCentrum.getCurrentHour().equals("15:00"))
			{
				print(1.0f);
				print(generation);
				print(consumption);
				print(generacjaDoBaterii);
				print(predkoscBaterii);
				print(pojemnoscBaterii);
				getInput("hello");
			}*/
			
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
	
	void zaktualizujHandelBrakHandluEVScenariusz()
	{
		
	}
	
	public void zaktualizujHandelBrakHandlu()
	{
		//print("Hello zwykly prosument");
		zaktualizujHandelBrakHandluZwyklyScenariusz();	
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
		dayDataList.get(timeIndex).countUnusedGeneration();
	}
	
	void charge()
	{
		/*if (LokalneCentrum.getTimeIndex()>=dayDataList.size())
		{
			print (LokalneCentrum.getTimeIndex());
			print (dayDataList.size());
			getInput("charge problem");
		}*/
		
		dayDataList.get(LokalneCentrum.getTimeIndex()).charge(cenaDystrybutoraZewnetrznego,kosztAmortyzacjiBaterii);
	}
	
	public void performEndOfSimulationCalculations()
	{
		performEndOfSimulationCalculations(true);
	}
	
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
		
		costNoReserve = totalCost;
		countReserveBonus();
		
	}
	
	//odpala sie po wylcizeniu totalnego kosztu 
	void countReserveBonus()
	{
		//index LokalneCentrum.getTimeIndex() bo to jest stan konca symualcji (Lokalne Centrum doszlo do Simulatin End date)
		reserveBonus = dayDataList.get(LokalneCentrum.getTimeIndex()).getStanBateriiNaKoniecSlotu()*Stale.kosztAmortyzacjiBaterii;
		totalCost-=reserveBonus;
		
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
	
}
