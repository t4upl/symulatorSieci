package symulacja2;

import java.util.ArrayList;
import java.util.Scanner;

public class ListaProsumentow2 {
	
	Prosument2 prosument;
		
	float mnoznikGeneracji;

	private ArrayList<Prosument2> listaProsumentow = new ArrayList<Prosument2>();
	
	int horyzontCzasowy=Stale.horyzontCzasowy; //ustawiane przy ustawianiu prosumentow
	
	Scanner sc = new Scanner(System.in);
	Reporter reporter = Reporter.getInstance();
	
	

	//SETTERS
	public void setProsument(Prosument2 prosument)
	{
		this.prosument=prosument;
	}
	
	public void setHoryzontCzasowy(int horyzontCzasowy)
	{
		this.horyzontCzasowy=horyzontCzasowy;
	}
	
	//GETTERS
	public ArrayList<Prosument2> getListaProsumentow()
	{
		return listaProsumentow;
	}
	
	//----------------------
	//OTHER FUNCTIONS
	
	ListaProsumentow2(String folderZZuzyciem)
	{
		int a=0;
		while (a<Stale.liczbaProsumentow)
		{
			Prosument2 prosument2 = new Prosument2(a+1);
			prosument2.loadData(folderZZuzyciem);
			
			listaProsumentow.add(prosument2);
			a++;
		}
		
	}
	

	//force prosuments to do calculations and create prosuments reports
	public void endSimulationReport()
	{		
		int a=0;
		while(a<listaProsumentow.size())
		{
			Prosument2 prosument =listaProsumentow.get(a);
			prosument.performEndOfSimulationCalculations();
	
			reporter.createProsumentReport(prosument);			
			a++;
		}
	}
	
	public void modyfikatorScenariusza(float cenaZewnetrzna,int numerScenariusza)
	{
		mnoznikGeneracji =getMnoznik();

		
		switch (numerScenariusza)
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
	
	public void zaktualizujHandelProsumentow()
	{
		//print("zaktualizujHandelProsumentow");
		int a=0;
		while (a<listaProsumentow.size())
		{
			listaProsumentow.get(a).zaktualizujHandel();
			a++;
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
		
		//wylacz handel
		a=0;
		while (a<listaProsumentow.size())
		{
			listaProsumentow.get(a).disableHandel();
			a++;
		}
	}
	
	void scenarioTwo()
	{
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
	
	
	public void tickProsuments()
	{
		int a=0;
		while (a<listaProsumentow.size())
		{
			listaProsumentow.get(a).tick();
			a++;
		}
	}
	
	/*
	public void consistencyCheck()
	{
		int a=0;
		while (a<listaProsumentow.size())
		{
			listaProsumentow.get(a).consistencyCheck();
			a++;
		}		
	}*/
	
	
	//used by getListaSumarycznejGeneracji and getListaSumarycznejKonsumpcji
	ArrayList<Float> getListaSumarycznej(int startTimeIndex, Boolean generationFlag)
	{
		ArrayList<Float> L1 = new ArrayList<Float>();
		
		float wartoscDoDodania=0f;
		
		int a=0;
		while (a<listaProsumentow.size())
		{
			ArrayList<DayData2> dList = listaProsumentow.get(a).getDayDataList();
			int b=0;
			while (b<horyzontCzasowy)
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
		
		//print("panda");
		//print(((Integer)L1.size()).toString() );
		//print(((Integer)horyzontCzasowy).toString() );

		//getInput();
		
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
		//print("broadcastFirstPriceVector");
		//print(((Integer)priceVector.size()).toString());
		//getInput();
		
		int a=0;
		while (a<listaProsumentow.size())
		{
			listaProsumentow.get(a).takeFirstPriceVector(listOfPriceVectors);
			a++;
		}
		
		
		//getInput("broadcastFirstPriceVector -end");
	}
	
	void print(int a)
	{
		print(((Integer)a).toString());
	}
	
	void print(String s)
	{
		System.out.println("Lista Prosumentow "+s);
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
	
	//znajduje mnoznik taki ze suma generacji grupy prosumentow == suma konsumpcji grupy 
	public float getMnoznik()
	{
		float sumaGeneracji=0f;
		float sumaZuzycia=0f;
		
		int b=0;
		while (b<Stale.liczbaProsumentow)
		{
			Prosument2 prosument =listaProsumentow.get(b);
			
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

	
}
