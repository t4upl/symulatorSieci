import java.util.ArrayList;


public class ListaProsumentowWrap extends CoreClass {

	private ArrayList<Prosument> listaProsumentow = new ArrayList<>();
	
	//mnoznik generacji dobrnay tak, zeby generacja z prosumentow generujacych energie (pierwszych 4)
	//byla rowna zuzyciu wszystkich prosumentow
	float mnoznikGeneracji;
	
	Reporter reporter = Reporter.getInstance();
	
	//Singleton shit
	private static ListaProsumentowWrap instance = null;
	private ListaProsumentowWrap() 
	{
	}
	
	public static ListaProsumentowWrap getInstance() {
	      if(instance == null) {
	         instance = new ListaProsumentowWrap();
	      }
	      return instance;
	}
	
	//----------------------
	//GETTERS
	
	ArrayList<Prosument> getListaProsumentow()
	{
		return this.listaProsumentow;
	}
	
	
	//TODO
	//-------------------------
	//OTHER FUNCTIONS
	public void loadProsumenci()
	{
		int a=0;
		while (a<Stale.liczbaProsumentow)
		{
			Prosument prosument2 = new Prosument();
			prosument2.setID(a+1);
			
			prosument2.loadData();
			
			listaProsumentow.add(prosument2);
			a++;
		}
	}
	
	//wszyscy prosumenci bioracy udzial w testach musza byc aktywni
	public void modyfikatorScenariuszaTestowego()
	{
		int a=0;
		while (a<Stale.liczbaProsumentow)
		{
			listaProsumentow.get(a).setActive(true);;		
			a++;
		}
	}
	
	public void modyfikatorScenariusza()
	{
		print("modyfikatorScenariusza "+Stale.scenariusz );
		
		mnoznikGeneracji =getMnoznik();
		
		switch (Stale.scenariusz)
		{
			case 1: scenarioOne(); break;
			case 2: scenarioTwo(); break;
			case 3: scenarioThree(); break;
			case 4: scenarioFour(); break;
			case 5: scenarioFive(); break;
			case 6: scenarioSix(); break;
			default: System.out.println("!!!NO SUCH SCENARIO"); getInput();
		}
	}
	
	void scenarioOne()
	{
		print("Scenario 1");
		//ustaw 4 generacje i baterie
		int a=0;
		while (a<4)
		{
			listaProsumentow.get(a).aktywujBaterie();
			listaProsumentow.get(a).setMnoznikGeneracji(mnoznikGeneracji);		
			a++;
		}
		
	}
	
	void scenarioTwo()
	{
		print("Scenario 2");
		//ustaw 4 generacje i baterie
		int a=0;
		while (a<4)
		{
			listaProsumentow.get(a).aktywujBaterie();
			listaProsumentow.get(a).setMnoznikGeneracji(mnoznikGeneracji);		
			a++;
		}
	}
	
	void scenarioThree()
	{
		print("Scenario 3");
		//ustaw 4 generacje i baterie
		int a=0;
		while (a<4)
		{
			listaProsumentow.get(a).setMnoznikGeneracji(mnoznikGeneracji);		
			a++;
		}
	}
	
	void scenarioFour()
	{
		print("Scenario 4");
		//ustaw 4 generacje i baterie
		int a=0;
		while (a<4)
		{
			listaProsumentow.get(a).setMnoznikGeneracji(mnoznikGeneracji);		
			a++;
		}
		
		a=0;
		while (a<4)
		{
			listaProsumentow.get(a+4).aktywujBaterie();		
			a++;
		}
		
	}
	
	void scenarioFive()
	{
		print("Scenario 5");
		//ustaw 4 generacje i baterie
		int a=0;
		while (a<4)
		{
			listaProsumentow.get(a).setMnoznikGeneracji(mnoznikGeneracji);
			listaProsumentow.get(a).aktywujBaterie();		

			//bo tylko dwoch ma miec przesunieta generacje
			if (a>1)
			{
				listaProsumentow.get(a).przesunGeneracje();
			}
			a++;
		}
	}
	
	void scenarioSix()
	{
		print("Scenario 6");
		//ustaw 4 generacje i baterie
		int a=0;
		while (a<4)
		{
			listaProsumentow.get(a).setMnoznikGeneracji(mnoznikGeneracji);
			
			//bo tylko dwoch ma miec przesunieta generacje
			if (a>1)
			{
				listaProsumentow.get(a).przesunGeneracje();
			}
			a++;
		}
	}
	
	//znajduje mnoznik taki ze suma generacji grupy prosumentow == suma konsumpcji grupy 
	public float getMnoznik()
	{
		float sumaGeneracji=0f;
		float sumaZuzycia=0f;
		
		int b=0;
		while (b<Stale.liczbaProsumentow)
		{
			Prosument prosument =listaProsumentow.get(b);
			
			if (b<4)
			{
				sumaGeneracji+=prosument.obliczCalkowitaGeneracja();
			}
			
			sumaZuzycia+=prosument.obliczCalkowitaKonsumpcje();
			
			
			b++;
		}
		
		float mnoznik = sumaZuzycia/sumaGeneracji;

		return mnoznik;
	}
	
	public void tickProsuments()
	{
		int a=0;
		while (a<listaProsumentow.size())
		{
			listaProsumentow.get(a).tick();
			a++;
		}
	}
	
	public void zaktualizujProsumentowBrakHandlu()
	{
		int a=0;
		while (a<listaProsumentow.size())
		{
			listaProsumentow.get(a).zaktualizujHandelBrakHandlu();
			a++;
		}
	}
	
	//force prosuments to do calculations and create prosuments reports
	public void endSimulationReport()
	{		
		int a=0;
		while(a<listaProsumentow.size())
		{
			Prosument prosument =listaProsumentow.get(a);
			prosument.performEndOfSimulationCalculations();
	
			reporter.createProsumentReport(prosument);			
			a++;
		}
	}
	
	//used by getListaSumarycznejGeneracji and getListaSumarycznejKonsumpcji
	ArrayList<Float> getListaSumarycznej(int startTimeIndex, Boolean generationFlag)
	{
		ArrayList<Float> L1 = new ArrayList<Float>();
		
		float wartoscDoDodania=0f;
		
		int a=0;
		while (a<listaProsumentow.size())
		{
			ArrayList<DayData> dList = listaProsumentow.get(a).getDayDataList();
			int b=0;
			while (b<Stale.horyzontCzasowy)
			{
				if (generationFlag)
				{
					wartoscDoDodania =dList.get(startTimeIndex+b).getTrueGeneration();
				}
				else
				{
					wartoscDoDodania =dList.get(startTimeIndex+b).getConsumption();
				}
				
				if (a==0)
				{
					L1.add(wartoscDoDodania);
				}
				else
				{
					L1.set(b, L1.get(b)+wartoscDoDodania);
				}
				b++;
			}
			a++;
		}		
		
		return L1;
	}
	
	public ArrayList<Float> getListaSumarycznejGeneracji(int startTimeIndex)
	{
		return getListaSumarycznej(startTimeIndex,true);
	}
	
	public ArrayList<Float> getListaSumarycznejKonsumpcji(int startTimeIndex)
	{
		return getListaSumarycznej(startTimeIndex,false);
	}
	
	public void broadcastFirstPriceVector(ArrayList<ArrayList<Float>> listOfPriceVectors)
	{	
		int a=0;
		while (a<listaProsumentow.size())
		{
			listaProsumentow.get(a).takeFirstPriceVector(listOfPriceVectors);
			a++;
		}
	}
	
	
}