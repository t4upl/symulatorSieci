
public class DayData extends CoreClass {

	private String day;
	private String hour;
	private float consumption=0f; //ustawiane Prosument.loadData()
	private float generation=0f; //ustawiane Prosument.loadData() Bez mnzonika
	private float trueGeneration=0f; //generation * mnoznik, ustawione przy ustawianu mnoznika
	
	
	private float stanBateriiNaPoczatkuSlotu=0f;
	private float stanBateriiNaKoniecSlotu=0f; 
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
	private float predkoscLadowaniaBaterii=Stale.predkoscBaterii;
	private float pojemnoscBaterii=0f;
	
	//TODO
	//----------------------
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

	//TODO
	//----------------------
	//SETTERS	
	public void setID(int value)
	{
		this.ID=value;
	}
	
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
	
	//TODO
	//----------------------
	//OTHER
	
	public void countTrueGeneration(float mnoznik)
	{
		this.trueGeneration = generation*mnoznik;
	}
	
	public String getDayHour()
	{
		return this.day+" "+this.getHour();
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
		
		/*
		//to sprawdzalo czy wszysyc prosumenci maja  te same cene lokalna => maja
		if (LokalneCentrum.getTimeIndex()==21)
		{
			print ("charge "+cenaNaLokalnymRynku);
			getInput();
			
		}*/
		
		cost =pierwszySkładnik+drugiSkładnik+trzeciSkladnik+czwartySkladnik-piatySkladnik;
	}
	
	//value - wartosc jaka chcialby doladowac prosument
	public void chargeBatteryZGeneracji(float value, float predkoscBaterii, float pojemnoscBaterii)
	{
		
		value =Math.min(value, predkoscBaterii);
		
		//ilosc energii jaka jest w baterii
		float capacityLeft = pojemnoscBaterii -stanBateriiNaPoczatkuSlotu;
		
		value = Math.min(value, capacityLeft);
		zGeneracjiDoBaterii = value;
	}
	
	public void dischargeBatteryNaKonsumpcje(float value, float predkoscBaterii)
	{
		value =Math.max(value, 0);
		value =Math.min(value, predkoscBaterii);
		value = Math.min(value,stanBateriiNaPoczatkuSlotu);
		zBateriiNaKonsumpcje = value;
	}
	
	//uzywane tylko w sytuacji bez handlu
	public void obliczStanBateriiNaKoniecSlotu()
	{
		stanBateriiNaKoniecSlotu = stanBateriiNaPoczatkuSlotu -zBateriiNaKonsumpcje + zGeneracjiDoBaterii; 
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
	
}
