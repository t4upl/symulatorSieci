package symulacja2;

import java.util.Scanner;

public class DayData2 {

	private String day;
	private String hour;
	private float consumption=0f; //ustawiane Prosument.loadData()
	private float generation=0f; //ustawiane Prosument.loadData() Bez mnzonika
	private float trueGeneration=0f; //generation * mnoznik, ustawione przy ustawianu mnoznika
	
	
	private float stanBateriiNaPoczatkuSlotu=0f; // na poczatku slotu
	private float stanBateriiNaKoniecSlotu=0f; // na poczatku slotu
	private int ID;
	private float cost=0f;
	
	//sterowanie
		private float zBateriiNaKonsumpcje=0f;
		private float zBateriiNaRynek=0f;
		
		private float zGeneracjiNaKonsumpcje=0f;
		private float zGeneracjiNaRynek=0f;
		private float zGeneracjiDoBaterii=0f;

		private float zRynekNaKonsumpcje=0f;
		private float zRynekDoBaterii=0f;
		
		private float kupuj=-1;
		
	//Other data
		private float cenaNaLokalnymRynku=0f;
		private float unusedGeneration=0f;
		
	//Paramety
		private float predkoscLadowaniaBaterii=5f;
		private float pojemnoscBaterii=13.5f;

		
	static Scanner sc = new Scanner(System.in);
	
	DayData2()
	{
		
	}
	
	DayData2(String day, String hour, String consumption, String generation, int ID)
	{
		this.day=day;
		this.hour=hour;
		this.consumption=Float.valueOf(consumption);
		this.generation=Float.valueOf(generation);
		this.ID=ID;
	}
	
	DayData2 (DayData2 d)
	{
		this.day = d.getDay();
		this.hour =d.getHour();
		this.consumption = d.getConsumption();
		this.generation =d.getGeneration();
		
		this.zBateriiNaKonsumpcje = d.getZBateriiNaKonsumpcje();
		this.zBateriiNaRynek =d.getZBateriiNaRynek();
		
		this.zGeneracjiDoBaterii =d.getZGeneracjiDoBaterii();
		this.zGeneracjiNaKonsumpcje =d.getZGeneracjiNaKonsumpcje();
		this.zGeneracjiNaRynek =d.getZGeneracjiNaRynek();
		
		this.zRynekDoBaterii =d.getZRynekDoBaterii();
		this.zRynekNaKonsumpcje =d.getZRynekNaKonsumpcje();
		
		this.kupuj =d.getKupuj();
		
	}
	

	
	//GETTERS
	float getUnusedGeneration()
	{
		return this.unusedGeneration;
	}
	
	float getCenaNaLokalnymRynku()
	{
		return this.cenaNaLokalnymRynku;
	}
	
	float getKupuj()
	{
		return this.kupuj;
	}
	
	float getTrueGeneration()
	{
		return trueGeneration;
	}
	
	//---
	
	float getZRynekNaKonsumpcje()
	{
		return this.zRynekNaKonsumpcje;
	}
	
	float getZRynekDoBaterii()
	{
		return this.zRynekDoBaterii;
	}
	
	//---	
	float getZGeneracjiDoBaterii()
	{
		return zGeneracjiDoBaterii;
	}
	
	float getZGeneracjiNaKonsumpcje()
	{
		return zGeneracjiNaKonsumpcje;
	}
	
	float getZGeneracjiNaRynek()
	{
		return this.zGeneracjiNaRynek;
	}
	
	//---
	float getZBateriiNaKonsumpcje()
	{
		return zBateriiNaKonsumpcje;
	}
	
	float getZBateriiNaRynek()
	{
		return this.zBateriiNaRynek;
	}
	
	
	float getStanBateriiNaPoczatkuSlotu()
	{
		return stanBateriiNaPoczatkuSlotu;
	}
	
	float getStanBateriiNaKoniecSlotu()
	{
		return stanBateriiNaKoniecSlotu;
	}
	
	String getDay()
	{
		return this.day;
	}
	
	String getHour()
	{
		return this.hour;
	}
	
	float getConsumption()
	{
		return this.consumption;
	}
	
	float getGeneration()
	{
		return this.generation;
	}
	
	float getCost()
	{
		return this.cost;
	}
	
	
	//SETTERS
	public void setTrueGeneration(float value)
	{
		this.trueGeneration=value;
	}
	
	public void setUnusedGeneration(float value)
	{
		this.unusedGeneration=value;
	}
	
	public void setDay(String s)
	{
		this.day=s;
	}
	
	public void setHour(String s)
	{
		this.hour=s;
	}
	

	
	public void setZBateriiNaRynek(float value)
	{
		this.zBateriiNaRynek =value;
	}
	
	public void setKupuj(float value)
	{
		this.kupuj =value;
	}
	
	public void setZGeneracjiNaRynek(float value)
	{
		this.zGeneracjiNaRynek =value;
	}

	public void setZRynekDoBaterii(float value)
	{
		this.zRynekDoBaterii=value;
	}
	
	public void setZRynekNaKonsumpcje(float value)
	{
		this.zRynekNaKonsumpcje=value;
	}
	
	public void setCost(float value)
	{
		this.cost=value;
	}
	
	public void setZGeneracjiDoBaterii(float value)
	{
		zGeneracjiDoBaterii=value;
	}
	
	public void setZBateriiNaKonsumpcje(float value)
	{
		zBateriiNaKonsumpcje=value;
	}
	
	public void setConsumption(float value)
	{
		consumption = value;
	}
	
	public void setGeneration(float value)
	{
		generation = value;
	}
	
	public void setStanBateriiNaPoczatkuSlotu(float value)
	{
		stanBateriiNaPoczatkuSlotu = value;
	}
	
	public void setStanBateriiNaKoniecSlotu(float value)
	{
		stanBateriiNaKoniecSlotu = value;
	}
	
	public void setZGeneracjiNaKonsumpcje(float value)
	{
		zGeneracjiNaKonsumpcje=value;
	}
	
	public void setCenaNaLokalnymRynku(float value)
	{
		this.cenaNaLokalnymRynku = value;
	}
	
	//OTHER
	
	public void countTrueGeneration(float mnoznik)
	{
		this.trueGeneration = generation*mnoznik;
	}
	
	public void countUnusedGeneration()
	{
		unusedGeneration = trueGeneration - (zGeneracjiDoBaterii+zGeneracjiNaKonsumpcje+zGeneracjiNaRynek);
		unusedGeneration= Math.max(unusedGeneration,0);
	}
	
	public void charge(float cenaZewnetrzna, float kosztAmortyzajcjiBaterii)
	{
		float pierwszySkładnik =cenaZewnetrzna *(consumption-zGeneracjiNaKonsumpcje-zBateriiNaKonsumpcje-zRynekNaKonsumpcje);
		float drugiSkładnik=(cenaNaLokalnymRynku+kosztAmortyzajcjiBaterii)*zRynekDoBaterii;
		float trzeciSkladnik =cenaNaLokalnymRynku*zRynekNaKonsumpcje;
		float czwartySkladnik = kosztAmortyzajcjiBaterii*zGeneracjiDoBaterii;
		float piatySkladnik=cenaNaLokalnymRynku*(zBateriiNaRynek+zGeneracjiNaRynek);
		
		cost =pierwszySkładnik+drugiSkładnik+trzeciSkladnik+czwartySkladnik-piatySkladnik;
	}
	
	float batteryDischargeValue(float value)
	{
		value =Math.min(predkoscLadowaniaBaterii,value);
		
		if (stanBateriiNaKoniecSlotu-value<0)
		{
			value=stanBateriiNaKoniecSlotu;
		}
		return value;
	}
	
	public void dischargeBatteryNaKonsumpcje(float value)
	{
		value =batteryDischargeValue(value);
		
		
		if (zBateriiNaKonsumpcje==0)
		{
			zBateriiNaKonsumpcje = value;
			stanBateriiNaKoniecSlotu-=zBateriiNaKonsumpcje;
		}
		else
		{
			print("Error in dischargeBatteryNaKonsumpcje");
		}



	}
	
	float batteryChargeValue(float value)
	{
		value =Math.min(predkoscLadowaniaBaterii,value);
		
		if (stanBateriiNaKoniecSlotu+value>pojemnoscBaterii)
		{
			value=pojemnoscBaterii-stanBateriiNaKoniecSlotu;
		}
		return value;
	}
	
	public void chargeBatteryZGeneracji(float value)
	{
		
		//print("charge Battery1 "+value);
		
		value =batteryChargeValue(value);
		
		//print("charge Battery2 "+value);

		
		if (zGeneracjiDoBaterii==0)
		{
			zGeneracjiDoBaterii=value;
			stanBateriiNaKoniecSlotu+=zGeneracjiDoBaterii;
			
			//print("Bateria: "+stanBateriiNaKoniecSlotu);
			
			if (stanBateriiNaKoniecSlotu>pojemnoscBaterii)
			{
				print("chargeBatteryZGeneracji Error #1");
			}
		}
		else
		{
			print("Error in chargeBatteryZGeneracji Double access");
		}
	}
	
	void printID(String s)
	{
		if (ID==1)
		{
			System.out.println("DayData2 ID: "+ID+" "+s);
		}
	} 
	
	void print(String s)
	{

		System.out.println("DayData2 ID: "+ID+" "+s);
		
	} 
	
	void print(float s)
	{
		print(((Float)s).toString());
	} 
	
	
	public String reportString()
	{
		String header1 = "day,hour,consumption,generation,trueGeneration,stanBateriiNaPoczatkuSlotu,stanBateriiNaKoniecSlotu";
		String header2 = "zBateriiNaKonsumpcje,zBateriiNaRynek,zGeneracjiNaKonsumpcje,zGeneracjiNaRynek,zGeneracjiDoBaterii";
		String header3 = "zRynekNaKonsumpcje,zRynekDoBaterii,cost";

		
		String s1=day+","+hour+","+consumption+","+generation+","+trueGeneration+","+stanBateriiNaPoczatkuSlotu+","+stanBateriiNaKoniecSlotu;
		String s2=zBateriiNaKonsumpcje+","+zBateriiNaRynek+","+zGeneracjiNaKonsumpcje+","+zGeneracjiNaRynek+","+zGeneracjiDoBaterii;
		String s3=zRynekNaKonsumpcje+","+zRynekDoBaterii+","+cost;
		
		String headerFull = header1+" , "+header2+" , "+header3;
		String sFull=s1+" , "+s2+" , "+s3;
		
		return "\n"+headerFull+"\n"+sFull;
	}
	
	void debugInput()
	{
		
		/*if (ID==1)
		{
			print("debugInput");
			sc.nextLine();
		}*/
	}
	
	//ustaw wszystko an zeor poza dniem, godizna
	public void resetDayDataToZero()
	{
		consumption=0f; //ustawiane Prosument.loadData()
		generation=0f; //ustawiane Prosument.loadData() Bez mnzonika
		stanBateriiNaPoczatkuSlotu=0f; // na poczatku slotu
		stanBateriiNaKoniecSlotu=0f; // na poczatku slotu
		cost=0;
		
		zBateriiNaKonsumpcje=0f;
		zBateriiNaRynek=0f;
			
		zGeneracjiNaKonsumpcje=0f;
		zGeneracjiNaRynek=0f;
		zGeneracjiDoBaterii=0f;

		zRynekNaKonsumpcje=0f;
		zRynekDoBaterii=0f;
		
		//rachunekWSlocie=0f; //out off use, use "cost" instead
		cenaNaLokalnymRynku=0f;
	}
	
	public void divide (int divider)
	{
		float dividerAsFloat = (float)divider;
		
		consumption/=dividerAsFloat; //ustawiane Prosument.loadData()
		generation/=dividerAsFloat; //ustawiane Prosument.loadData() Bez mnzonika
		trueGeneration/=dividerAsFloat;
		
		stanBateriiNaPoczatkuSlotu/=dividerAsFloat; // na poczatku slotu
		stanBateriiNaKoniecSlotu/=dividerAsFloat; // na poczatku slotu
		
		zBateriiNaKonsumpcje/=dividerAsFloat;
		zBateriiNaRynek/=dividerAsFloat;
			
		zGeneracjiNaKonsumpcje/=dividerAsFloat;
		zGeneracjiNaRynek/=dividerAsFloat;
		zGeneracjiDoBaterii/=dividerAsFloat;

		zRynekNaKonsumpcje/=dividerAsFloat;
		zRynekDoBaterii/=dividerAsFloat;
		
		cost/=dividerAsFloat;
		unusedGeneration/=dividerAsFloat;
	}
	
	void getInput(String s)
	{
		print("DayData2 "+s);
		sc.nextLine();
	}
	
	void getInput()
	{
		getInput("");
	}
	
	public String getDayHour()
	{
		return this.day+" "+this.getHour();
	}
	
	
	
}
