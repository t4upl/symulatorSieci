package symulacja;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

public class Prosument {

	//System
	String pathToDataZuzycie="C:\\Users\\Administrator\\Desktop\\magisterska_Dane_set1\\Zuzycie\\";
	float stosunekGeneracjiDoKonsumpcji=0f;
	
	//String simulationEndDate="";
	
	int liczbaMiesiecy =3; //Wymagane przy skalowaniu generacji
	LokalneCentrumDystrybucji lokalneCentrumDystrybucji;
	
	//Listy
	ArrayList<DayData> dayDataList = new ArrayList<DayData>();
	
	//Stale
	Boolean handel;
	int ID;
	float pojemnoscBateriiIfEnabled =13.5f ; //pojemnosc baterii, jezeli bylaby aktywowana
	float predkoscBaterii = 5f; //ile energii moze przekazac bateria w trakcie slotu czasowego
	Boolean obecnoscBaterii=false;
	Boolean debug=false;
	
	//Variables
	int timeIndex=0; //okresla moment czasu
	float pojemnoscBaterii =0f;//faktyczna pojemnsoc baterii
	
	

	
	
	float mnoznikGeneracji=0f;
	
	//Stale modelu
	float cenaZewnetrzna;
	
	
	//Setters
	public void setStosunekGeneracjiDoKonsumpcji(float stosunekGeneracjiDoKonsumpcji)
	{
		this.stosunekGeneracjiDoKonsumpcji=stosunekGeneracjiDoKonsumpcji;
	}
	
	public void setObecnoscBaterii(Boolean obecnoscBaterii)
	{
		if (obecnoscBaterii)
		{
			this.obecnoscBaterii=obecnoscBaterii;
			pojemnoscBaterii=pojemnoscBateriiIfEnabled;
		}
	}
	
	public void setPricePredictionOnLocalMarket()
	{
		
	}
	
	
	public void updateProsumentState()
	{
		
		if (handel)
		{
			//Fill this out!
		}
		else
		{
			noHandelBehavior();
		}
	}
	
	void noHandelBehavior()
	{
		
		DayData dayData = dayDataList.get(timeIndex); 
		
		if (ID==1 && debug)
		{
			System.out.println(ID+" noHandelBehavior");
			System.out.println("Time: "+timeIndex);
			System.out.println("Konsumpcja "+dayData.getConsumption());
			System.out.println("Generacja "+dayData.getGeneration());
			System.out.println("Bateria "+dayData.getStanBaterii());
		}
		
		
		
		if (dayData.getGeneration()<dayData.getConsumption())
		{

			dayData.setZGeneracjiNaKonsumpcje(dayData.getGeneration());
			float nieZbilansowanaKonsumpcja = dayData.getConsumption()-dayData.getZGeneracjiNaKonsumpcje();
			
			float rozladowanie =nieZbilansowanaKonsumpcja;
			float rozladowaniePoKontroli = kontrolaRoladowania(rozladowanie);

			if (ID==1 && debug)
			{
				System.out.println("Rozladowanie");
				System.out.println(rozladowaniePoKontroli);
			}
			
			dayData.rozladjuZBateriiNaKonsumpcje(rozladowaniePoKontroli);
			
		}
		else
		{
			if (ID==1 && debug)
			{
				System.out.println("Ladowanie");
			}
			
			dayData.setZGeneracjiNaKonsumpcje(dayData.getConsumption());
			
			float doladowanie = dayData.getGeneration() -dayData.getConsumption();
			float doladowaniePokontroli = kontrolaDoladowania(doladowanie);
			
			
			dayData.ladujZGeneracjiDoBaterii(doladowaniePokontroli);
		}
		
		if (ID==1 && debug)
		{
			System.out.println("ZGeneracjiNaKonsumpcje "+dayData.getZGeneracjiNaKonsumpcje());
			System.out.println("ZGeneracjiDoBaterii "+dayData.getZGeneracjiDoBaterii());

		}
	}
	
	//zwraca rozladowanie po uwzglendnieniu predkosci ladowania i nieujemnosci baterii
	float kontrolaRoladowania(float rozladowanie)
	{
		rozladowanie=Math.min(rozladowanie, predkoscBaterii);
		
		if (dayDataList.get(timeIndex).getStanBaterii()<rozladowanie)
		{
			rozladowanie=dayDataList.get(timeIndex).getStanBaterii();
		}
		
		rozladowanie = Math.max(rozladowanie, 0);

		return rozladowanie;
	}
	
	//zwraca doladowanie po uwzglendnieniu predkosci ladowania i maksymalnej pojemnosci baterii
	float kontrolaDoladowania(float doladowanie)
	{
		doladowanie=Math.min(doladowanie, predkoscBaterii);
		
		if (doladowanie+dayDataList.get(timeIndex).getStanBateriiNaKoniecSlotu()> pojemnoscBaterii)
		{
			doladowanie=pojemnoscBaterii-dayDataList.get(timeIndex).getStanBateriiNaKoniecSlotu();
		}
		
		doladowanie = Math.max(doladowanie, 0);
		
		return doladowanie;
	}
	
	Prosument (int ID,Boolean handel, LokalneCentrumDystrybucji lc, float cenaZewnetrzna, Boolean debug)
	{
		this.ID = ID;
		this.handel = handel;
		this.lokalneCentrumDystrybucji=lc;
		this.debug = debug;
		
	}
	
	
	
	public void loadData()
	{
		String functionName = "loadData";
		//System.out.println("\n-----\nLokalne CentrumDystryucji "+functionName+" prosument:"+ID+" - start");

		
		String pathToData=pathToDataZuzycie+ID+".txt";
        BufferedReader br = null;
        String line="";
        
        float roczneZuzycie=0f;

        
        try 
        {
			br = new BufferedReader(new FileReader(pathToData));
			int a=0;
	        while ((line = br.readLine()) != null)
	        {
	        	//Roczne zuzycie
	        	if (a==1)
	        	{
	        		String[] s2 = line.split(":");
	        		roczneZuzycie=Float.parseFloat(s2[1]);
	        	}
	        	
	        	if (a==2)
	        	{
	        		String[] s2 = line.split(":");
	        		//System.out.println(line);
	        		mnoznikGeneracji=stosunekGeneracjiDoKonsumpcji*roczneZuzycie*liczbaMiesiecy/12/Float.parseFloat(s2[1]);
	        		//System.out.println("mnoznikGeneracji: "+mnoznikGeneracji);
	        		
	        		if (ID==1 && debug)
	        		{
	        			System.out.println("mnoznikGeneracji "+mnoznikGeneracji);
	        		}
	        		
	        	}
	        	
	        	if (a>3)//a==3 to linia ###
	        	{
	        		//System.out.println(line);
	        		String[] s2 = line.split("#");
	        		String[] s3 = s2[0].split(" ");

	        		String day = s3[0];
	        		String hour =s3[1];
	        		float consumption =Float.parseFloat(s2[1]);
	        		float generation = Float.parseFloat(s2[2] )*mnoznikGeneracji;
	        		
	        		dayDataList.add(new DayData(day, hour,consumption,generation ));

	        	}
	        	
	        	a++;
	        }

        }
        catch (Exception e)
        {	
        	System.out.println(ID+" E! loadData");
        }
        
        //This is a test. generation should be about 4x(mnoznikGeneracji) bigger than consumption for chosen prosuments 
        //System.out.println(sumFloatList(getConsumptionList()));
        //System.out.println(sumFloatList(getGenerationList()));
		
		//System.out.println("\n-----\nLokalne CentrumDystryucji "+functionName+" prosument:"+ID+" - end");
	}
	
	ArrayList<Float> getConsumptionList()
	{
		ArrayList<Float> L1=new ArrayList<Float>();
		int a=0;
		while (a<dayDataList.size())
		{
			L1.add(dayDataList.get(a).getConsumption());
			a++;
		}
		return L1;
	}
	
	ArrayList<Float> getGenerationList()
	{
		ArrayList<Float> L1=new ArrayList<Float>();
		int a=0;
		while (a<dayDataList.size())
		{
			L1.add(dayDataList.get(a).getGeneration());
			a++;
		}
		return L1;
	}
	
	float sumFloatList(ArrayList<Float> list)
	{
		float sum=0f;
		int a=0;
		while (a<list.size())
		{
			sum+=list.get(a);
			a++;
		}
		return sum;
	}
	
	void functionTemplate()
	{
		String functionName = "TEMPLATE";
		System.out.println("\n-----\nLokalne CentrumDystryucji "+functionName+" - start");
		System.out.println("\n-----\nLokalne CentrumDystryucji "+functionName+" - end");
	}
	
	//Przesun godzine na +1
	public void tickTime()
	{
		if (timeIndex+1<dayDataList.size())
		{
			dayDataList.get(timeIndex+1).setStanBaterii(dayDataList.get(timeIndex).getStanBateriiNaKoniecSlotu());
			dayDataList.get(timeIndex+1).setStanBateriiNaKoniecSlotu(dayDataList.get(timeIndex).getStanBateriiNaKoniecSlotu());

		}
		//setStanBaterii
		timeIndex++;
	}
	
	public void doFinalCheck()
	{
		DayData d= null;
		DayData d2= null;
		
		int a=0;
		while (a<dayDataList.size())
		{	
			d = dayDataList.get(a);
			if ((a+1)<dayDataList.size())
			{
				d2 = dayDataList.get(a+1);				
			}
			
			doFinalCheckNoNNegative(d);
			doFinalCheckAdditionalChecks(d);	
			
			a++;
		}
	}
	
	void doFinalCheckAdditionalChecks(DayData d)
	{
		if (d.getGeneration()<d.getZGeneracjiDoBaterii()+d.getZGeneracjiNaKonsumpcje()+d.getZGeneracjiNaRynek())
		{
			System.out.println("ERROR! Generation sum problem "+ID);
		}
		
		float f =d.getStanBateriiNaKoniecSlotu()-(d.getStanBaterii()+d.getZBateriiNaKonsumpcje()+d.getZBateriiNaRynek()+d.getZGeneracjiDoBaterii()+d.getZRynekDoBaterii() );
		System.out.println(f);
		
		/*if(Math.abs(d.getStanBateriiNaKoniecSlotu()-(d.getStanBaterii()+d.getZBateriiNaKonsumpcje()+d.getZBateriiNaRynek()+d.getZGeneracjiDoBaterii()+d.getZRynekDoBaterii() ))<0.0001f)
		{
			System.out.println("ERROR! Battery sum problem "+ID);	
			System.out.println(d.getStanBateriiNaKoniecSlotu());
			System.out.println(d.getStanBaterii());
			System.out.println(d.getZBateriiNaKonsumpcje());
			System.out.println(d.getZBateriiNaRynek());
			System.out.println(d.getZGeneracjiDoBaterii());
			System.out.println(d.getZRynekDoBaterii());
			System.out.println(Math.abs(d.getStanBateriiNaKoniecSlotu()-(d.getStanBaterii()+d.getZBateriiNaKonsumpcje()+d.getZBateriiNaRynek()+d.getZGeneracjiDoBaterii()+d.getZRynekDoBaterii() )));

			
		}*/
	}
	
	void doFinalCheckNoNNegative(DayData d)
	{
		if (d.getConsumption()<0)
		{
			System.out.println("ERROR! detected negative Consumption "+ID);
		}
		
		if (d.getGeneration()<0)
		{
			System.out.println("ERROR! detected negative Generation "+ID);
		}
		
		if (d.getStanBaterii()<0)
		{
			System.out.println("ERROR! detected negative Bateria "+ID);
		}
		
		if (d.getStanBateriiNaKoniecSlotu()<0)
		{
			System.out.println("ERROR! detected negative Bateria na koniec slotu "+ID);
		}
		
		if (d.getZBateriiNaKonsumpcje()<0)
		{
			System.out.println("ERROR! detected negative getZBateriiNaKonsumpcje "+ID);
		}
		
		if (d.getZBateriiNaRynek()<0)
		{
			System.out.println("ERROR! detected negative getZBateriiNaRynek "+ID);
		}
		
		if (d.getZGeneracjiDoBaterii()<0)
		{
			System.out.println("ERROR! detected negative getZGeneracjiDoBaterii "+ID);
		}
		
		if (d.getZGeneracjiNaKonsumpcje()<0)
		{
			System.out.println("ERROR! detected negative getZGeneracjiNaKonsumpcje "+ID);
		}
				
		if (d.getZGeneracjiNaRynek()<0)
		{
			System.out.println("ERROR! detected negative getZGeneracjiNaRynek "+ID);
		}
		
		if (d.getZRynekDoBaterii()<0)
		{
			System.out.println("ERROR! detected negative getZRynekDoBaterii "+ID);
		}
		
		if (d.getZRynekNaKonsumpcje()<0)
		{
			System.out.println("ERROR! detected negative getZRynekNaKonsumpcje "+ID);
		}
	}
	
	public void dropToFile()
	{
		
	}
	
	
	
}
