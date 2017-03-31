package symulacja;

public class DayData {
	private String day;
	private String hour;
	private float consumption=-1f; //ustawiane Prosument.loadData()
	private float generation=-1f; //juz po przeksaowaniu //ustawiane Prosument.loadData()
	private float stanBaterii=0f; // na poczatku slotu
	private float stanBateriiNaKoniecSlotu=0f; // na poczatku slotu
	
	//sterowanie
	private float zBateriiNaKonsumpcje=0f;
	private float zBateriiNaRynek=0f;
	
	private float zGeneracjiNaKonsumpcje=0f;
	private float zGeneracjiNaRynek=0f;
	private float zGeneracjiDoBaterii=0f;

	private float zRynekNaKonsumpcje=0f;
	private float zRynekDoBaterii=0f;
	
	//Other data
	private float cenaNaLokalnymRynku=0f;
	//private float rachunek; //sumaryczny rachunek poniesiony od pcozatku sumalcji
	private float rachunekWSlocie; //rachunek poniesiony w trakcie trwania slotu
	//private float generacjaNiewykorzystana=0f;
	
	
	DayData(String day, String hour, float consumption, float generation)
	{
		this.day=day;
		this.hour=hour;
		this.consumption=consumption;
		this.generation=generation;
	}
	
	//getters 
	public float getConsumption()
	{
		return consumption;	
	}
	
	public float getGeneration()
	{
		return generation;
	}
	
	public float getStanBaterii()
	{
		return stanBaterii;
	}
	
	public float getStanBateriiNaKoniecSlotu()
	{
		return stanBateriiNaKoniecSlotu;
	}
	
	public float getZBateriiNaKonsumpcje()
	{
		return zBateriiNaKonsumpcje;
	}
	
	public float getZBateriiNaRynek()
	{
		return zBateriiNaRynek;
	}
	
	public float getZGeneracjiNaKonsumpcje()
	{
		return zGeneracjiNaKonsumpcje;
	}
	
	public float getZGeneracjiNaRynek()
	{
		return zGeneracjiNaRynek;
	}
	
	public float getZGeneracjiDoBaterii()
	{
		return zGeneracjiDoBaterii;
	}
	
	public float getZRynekNaKonsumpcje()
	{
		return zRynekNaKonsumpcje;
	}
	
	public float getZRynekDoBaterii()
	{
		return zRynekDoBaterii;
	}
	
	
	
	
	//setters
	public void setConsumption(float c)
	{
		consumption=c;
	}
	
	public void setZGeneracjiDoBaterii(float c)
	{
		if (zGeneracjiDoBaterii==0)
		{
			zGeneracjiDoBaterii=c;
		}
		else
		{
			System.out.println("ERROR! setZGeneracjiDoBaterii");
		}
	}
	
	
	public void setStanBaterii(float c)
	{
		stanBaterii=c;
	}
	
	public void setStanBateriiNaKoniecSlotu(float c)
	{
		stanBateriiNaKoniecSlotu=c;
	}
	
	public void setZGeneracjiNaKonsumpcje(float c)
	{
		if (zGeneracjiNaKonsumpcje==0)
		{
			zGeneracjiNaKonsumpcje=c;
		}
		else
		{
			System.out.println("ERROR! setZGeneracjiNaKonsumpcje");
		}

	}
	
	//ladowanie z generacji na baterie
	public void ladujZGeneracjiDoBaterii(float c)
	{
		zGeneracjiDoBaterii=c;
		stanBateriiNaKoniecSlotu+=c;
	}
	
	//rozladowanie z baterii na konsumpcje
	public void rozladjuZBateriiNaKonsumpcje(float c)
	{
		zBateriiNaKonsumpcje=c;
		stanBateriiNaKoniecSlotu-=c;
	}
	
	
	
}
