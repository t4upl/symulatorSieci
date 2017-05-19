import java.util.ArrayList;

public class ListaProsumentowWrap extends CoreClass {

	//mnoznik generacji dobrnay tak, zeby generacja z prosumentow generujacych energie (pierwszych 4)
	//byla rowna zuzyciu wszystkich prosumentow
	double mnoznikGeneracji;
	
	//zawiera tylko Prosument i ProsumentEV
	private ArrayList<Prosument> listaProsumentow = new ArrayList<>();
	
	//uzywane tylko w scenariuszach z Virutalnym prosumentem 
	ProsumentVirtual prosumentVirtual;

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
	
	//---------------------
	void stworzProsumentow()
	{
		Prosument prosument =null;
		
		int i=0;
		while (i<Stale.liczbaProsumentow)
		{
			if (Stale.isScenariuszEV && i<4)
			{
				prosument = new ProsumentEV();
			}
			else
			{
				prosument = new Prosument();
			}
			
			prosument.setID(i);
			prosument.loadData();
			
			listaProsumentow.add(prosument);
			i++;
		}
		
	}
	
	public void modyfikatorScenariusza()
	{
		//getInput("modyfikatorScenariusza");
		
		if (Stale.scenariusz>=Stale.numerPierwszegoScenariuszaZawierajacegoEV)
		{
			Stale.isScenariuszEV=true;
		}
		else
		{
			Stale.isScenariuszEV=false;
		}
		
		stworzProsumentow();
		
		mnoznikGeneracji =getMnoznik();
		
		switch (Stale.scenariusz)
		{
			case 1: scenario1(); break;
			case 2: scenario2(); break;
			case 3: scenario3(); break;
			case 4: scenario4(); break;
			case 5: scenario5(); break;
			case 6: scenario6(); break;
			
			//scenariusz EV
			case 17: scenario17(); break;
			case 18: scenario18(); break;
			case 19: scenario19(); break;
			case 20: scenario20(); break;
			case 21: scenario21(); break;
			case 22: scenario22(); break;
			case 23: scenario23(); break;
			
			default: System.out.println("!!!NO SUCH SCENARIO"); getInput();
		}
		
		if (Stale.isVirtualnyProsument)
		{
			stworzVirualnegoProsumenta();
		}
	}
	
	//po stworzeniu 
	void stworzVirualnegoProsumenta()
	{		
		prosumentVirtual = new ProsumentVirtual(listaProsumentow);
		prosumentVirtual.setID(100);
	}
	
	public void zaktualizujHandelVirtualnegoProsumenta()
	{
		prosumentVirtual.zaktualizujHandel();
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
		
		//Jezlei scenariusz uwzglednia EV to energia wydana na podroz przez EV jest liczona w skald zuzycia
		if (Stale.isScenariuszEV)
		{
			sumaZuzycia+=obliczZuzycieEnergiinaEV();
		}
		
		float mnoznik = sumaZuzycia/sumaGeneracji;
		
		
		return mnoznik;
	}
	
	//oblicza ilosc energii jaka wydaja wszsycy prosumenci wchdozacy w sklad symualcji na rzecz podrozy EV
	double obliczZuzycieEnergiinaEV()
	{		
		double sum=0;
		
		int liczbaDni=(LokalneCentrum.getSimulationEndDateIndex())/24;
		int liczbaProsumenowEV=0;
		
		int i=0;
		while (i<listaProsumentow.size())
		{
			if (listaProsumentow.get(i) instanceof ProsumentEV)
			{
				liczbaProsumenowEV++;
			}
			i++;
		}
				
		//2 - dwie podroze dizennie
		sum =liczbaDni*2*liczbaProsumenowEV*Stale.podrozMinimumEnergii;
		
		return sum;
	}
	
	void scenario1()
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
	
	void scenario2()
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
	
	void scenario3()
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
	
	void scenario4()
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
	
	void scenario5()
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
	
	void scenario6()
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
	
	
	void scenario17()
	{
		Stale.isHandelWPracy =true;
		int i=0;
		while (i<4)
		{
			listaProsumentow.get(i).setMnoznikGeneracji(mnoznikGeneracji);
			listaProsumentow.get(i).aktywujBaterie();
			i++;
		}
	}
	
	void scenario18()
	{
		Stale.isHandelWPracy =true;
		int i=0;
		while (i<4)
		{
			listaProsumentow.get(i).setMnoznikGeneracji(mnoznikGeneracji);
			i++;
		}
	}
	
	void scenario19()
	{
		Stale.isHandelWPracy =false;
		int i=0;
		while (i<4)
		{
			listaProsumentow.get(i).setMnoznikGeneracji(mnoznikGeneracji);
			i++;
		}
	}
	
	void scenario20()
	{
		Stale.isHandelWPracy =false;
		int i=0;
		while (i<4)
		{
			listaProsumentow.get(i).setMnoznikGeneracji(mnoznikGeneracji);
			listaProsumentow.get(i).przesunGeneracje();
			//listaProsumentow.get(i).aktywujBaterie();
			i++;
		}
	}
	
	void scenario21()
	{
		Stale.isHandelWPracy =true;
		int i=0;
		while (i<4)
		{
			listaProsumentow.get(i).setMnoznikGeneracji(mnoznikGeneracji);
			listaProsumentow.get(i).przesunGeneracje();
			i++;
		}
	}
	
	void scenario22()
	{
		Stale.isHandelWPracy =false;
		int i=0;
		while (i<4)
		{
			listaProsumentow.get(i).setMnoznikGeneracji(mnoznikGeneracji);
			
			if (i<2)
			{
				listaProsumentow.get(i).przesunGeneracje();
			}
			i++;
		}
	}
	
	void scenario23()
	{
		Stale.isHandelWPracy =false;
		int i=0;
		while (i<4)
		{
			listaProsumentow.get(i).setMnoznikGeneracji(mnoznikGeneracji);
			listaProsumentow.get(i).aktywujBaterie();
			i++;
		}
	}
	
	public void endSimulationReport()
	{
		if (Stale.isVirtualnyProsument)
		{
			endSimulationReportVirtualProsument();
		}
		else
		{
			endSimulationReportProsumenci();
		}
	}
	
	void endSimulationReportVirtualProsument()
	{
		prosumentVirtual.endSimulation();
	}
	
	//called from endSimulationReport
	void endSimulationReportProsumenci()
	{
	
		int i=0;
		while(i<listaProsumentow.size())
		{
			Prosument prosument =listaProsumentow.get(i);
			prosument.performEndOfSimulationCalculations();
			
			if (prosument instanceof ProsumentEV)
			{
				reporter.createProsumentReport((ProsumentEV)prosument);
			}
			else
			{
				reporter.createProsumentReport(prosument);
			}
			
						
			i++;
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
	
	//used by getListaSumarycznejGeneracji and getListaSumarycznejKonsumpcji
	ArrayList<Double> getListaSumarycznej(int startTimeIndex, Boolean generationFlag)
	{
		ArrayList<Double> L1 = new ArrayList<>();
		
		double wartoscDoDodania=0f;
		
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
	
	public ArrayList<Double> getListaSumarycznejGeneracji(int startTimeIndex)
	{
		return getListaSumarycznej(startTimeIndex,true);
	}
	
	public ArrayList<Double> getListaSumarycznejKonsumpcji(int startTimeIndex)
	{
		return getListaSumarycznej(startTimeIndex,false);
	}
	
	public void broadcastFirstPriceVector(ArrayList<ArrayList<Double>> listOfPriceVectors)
	{	
		int a=0;
		while (a<listaProsumentow.size())
		{
			listaProsumentow.get(a).takeFirstPriceVector(listOfPriceVectors);
			a++;
		}
	}
	
	
}
