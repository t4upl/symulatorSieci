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
		Prosument prosument2 =null;
		
		int a=0;
		while (a<Stale.liczbaProsumentow)
		{
			if (Stale.scenariusz>=Stale.numerPierwszegoScenariuszaZawierajacegoEV && a<4)
			{
				prosument2 = new ProsumentEV();

			}
			else
			{
				prosument2 = new Prosument();
			}
			
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
		
		if (Stale.scenariusz>=Stale.numerPierwszegoScenariuszaZawierajacegoEV)
		{
			Stale.isScenariuszEV=true;
		}
		else
		{
			Stale.isScenariuszEV=false;
		}
		
		mnoznikGeneracji =getMnoznik();
		
		switch (Stale.scenariusz)
		{
			case 1: scenarioOne(); break;
			case 2: scenarioTwo(); break;
			case 3: scenarioThree(); break;
			case 4: scenarioFour(); break;
			case 5: scenarioFive(); break;
			case 6: scenarioSix(); break;
			
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
	
	
	void scenario17()
	{
		Stale.handelWPracy =true;
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
		Stale.handelWPracy =true;
		int i=0;
		while (i<4)
		{
			listaProsumentow.get(i).setMnoznikGeneracji(mnoznikGeneracji);
			//listaProsumentow.get(i).aktywujBaterie();
			i++;
		}
	}
	
	void scenario19()
	{
		Stale.handelWPracy =false;
		int i=0;
		while (i<4)
		{
			listaProsumentow.get(i).setMnoznikGeneracji(mnoznikGeneracji);
			//listaProsumentow.get(i).aktywujBaterie();
			i++;
		}
	}
	
	void scenario20()
	{
		Stale.handelWPracy =false;
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
		Stale.handelWPracy =true;
		int i=0;
		while (i<4)
		{
			listaProsumentow.get(i).setMnoznikGeneracji(mnoznikGeneracji);
			listaProsumentow.get(i).przesunGeneracje();
			//listaProsumentow.get(i).aktywujBaterie();
			i++;
		}
	}
	
	void scenario22()
	{
		Stale.handelWPracy =false;
		int i=0;
		while (i<4)
		{
			listaProsumentow.get(i).setMnoznikGeneracji(mnoznikGeneracji);
			
			if (i<2)
			{
				listaProsumentow.get(i).przesunGeneracje();
			}
			//listaProsumentow.get(i).aktywujBaterie();
			i++;
		}
	}
	
	void scenario23()
	{
		Stale.handelWPracy =false;
		int i=0;
		while (i<4)
		{
			listaProsumentow.get(i).setMnoznikGeneracji(mnoznikGeneracji);
			listaProsumentow.get(i).aktywujBaterie();
			i++;
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
		
		//Jezlei scenariusz uwzglednia EV to energia wydana na podroz przez EV jest liczona w skald zuzycia
		if (Stale.isScenariuszEV)
		{
			sumaZuzycia+=obliczZuzycieEnergiinaEV();
		}
		
		float mnoznik = sumaZuzycia/sumaGeneracji;
		
		

		return mnoznik;
	}
	
	//oblicza ilosc energii jaka wydaja wszsycy prosumenci wchdozacy w sklad symualcji na rzecz podrozy EV
	float obliczZuzycieEnergiinaEV()
	{
		float sum=0;
		
		int liczbaDni=LokalneCentrum.getHourList().size()/24;
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
		
		print("liczba Dni "+liczbaDni);
		
		//2 - dwie podroze dizennie
		sum =liczbaDni*2*liczbaProsumenowEV*Stale.podrozMinimumEnergii;
		print("sum "+sum);
		
		return sum;
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
			
			if (prosument instanceof ProsumentEV)
			{
				reporter.createProsumentReport((ProsumentEV)prosument);
			}
			else
			{
				reporter.createProsumentReport(prosument);
			}
			
						
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
	
	public void endSimulationCheck()
	{		
		if (Stale.scenariusz<Stale.numerPierwszegoScenariuszaZawierajacegoEV)
		{
			endSimulationCheckStaloscHandluNoEV();
		}
	}
	
	void endSimulationCheckStaloscHandluNoEV()
	{
		
		//ODWROTNIE DayData w zewnetrznej petli, prosumenci w wewnetrznej
		
		ArrayList<DayData> dList2 =listaProsumentow.get(0).getDayDataList();
		
		int i=0;
		for (DayData d: dList2)
		{
			float sumaKupna=0;
			float sumaSprzedazy=0;
			
			for (Prosument prosument: listaProsumentow)
			{
				ArrayList<DayData> dList3 = prosument.getDayDataList();
				DayData d2 =dList3.get(i);
				
				float kupno = d2.getZRynekDoBaterii()+d2.getZRynekNaKonsumpcje();
				float sprzedaz =d2.getZGeneracjiNaRynek()+d2.getZBateriiNaRynek();
				
				sumaKupna+=kupno;
				sumaSprzedazy+=sprzedaz;
				
				if (kupno>0 && sprzedaz>0)
				{
					print ("\nERROR endSimulationCheckStaloscHandlu - prosument has selling/buying mix");
					print("prosument "+prosument.getID());
					print ("kupno "+kupno );
					print ("sprzedaz "+sprzedaz);
					print("kupuj "+d2.getKupuj());
					print("DayData indeks "+i);
					getInput();
					
				}
				
			}
			
			/*
			if (i==21)
			{
				print("i==21 endSimulationCheckStaloscHandlu");
				print ("kupno suma "+sumaKupna );
				print ("sprzedaz suma "+sumaSprzedazy);
				getInput();
			}*/
			
			if (Math.abs(sumaKupna-sumaSprzedazy)>Stale.malaLiczba)
			{
				print ("\nERROR endSimulationCheckStaloscHandlu - rynek ma nierowny wolumen sprzedazy i kupna");
				print ("kupno suma "+sumaKupna );
				print ("sprzedaz suma "+sumaSprzedazy);
				print("DayData indeks "+i);
				getInput();
				
			}
			
			i++;
		}
		
		/*
		int i=0;
		while (i<listaProsumentow.size())
		{
			ArrayList<DayData> dList =listaProsumentow.get(i).getDayDataList();
			
			float sumaKupna=0;
			float sumaSprzedazy=0;
			
			for ( DayData d: dList)
			{
				sumaKupna+=(d.getZRynekDoBaterii()+d.getZRynekNaKonsumpcje());
				sumaSprzedazy+=d.getZGeneracjiNaRynek()+d.getZBateriiNaRynek();
			}
			
			float shouldBeZero = Math.abs(sumaKupna-sumaSprzedazy);
			
			if (shouldBeZero>Stale.malaLiczba)
			{
				print("\nERROR in endSimulationCheckStaloscHandlu");
				print("at indeks ");
				getInput();
			}
			
			i++;
		}
		*/
		
	}
	
	
}
