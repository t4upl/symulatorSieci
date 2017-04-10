import java.awt.List;
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
			//w scenariuszach z wykorzystaniem EV uzywnay ejst prosumentEV
			if (Stale.scenariusz>=17 && a<4)
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
		
		print("loadProsumenci "+listaProsumentow.size());
	}
	
	public void modyfikatorScenariusza()
	{
		mnoznikGeneracji =getMnoznik();
		
		switch (Stale.scenariusz)
		{
			//scenariusze zywkle
			case 1: scenarioOne(); break;
			case 2: scenarioTwo(); break;
			case 3: scenarioThree(); break;
			case 4: scenarioFour(); break;
			case 5: scenarioFive(); break;
			case 6: scenarioSix(); break;
			
			//scenariusz EV
			case 17: scenario17(); break;
			
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
	
	void scenario17()
	{
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
		
		float mnoznik = sumaZuzycia/sumaGeneracji;

		return mnoznik;
	}
	
	void stworzVirutalProsumentZwyklyScenariusz()
	{
		Prosument virtualProsument = listaProsumentow.get(0);
		//ustawiamy DayDate Virtualnego prosumenta na DayDate 0 prosumenta
		//virtualProsument.setDayDataList(listaProsumentow.get(0).getDayDataList());
		
		//zacyznamy dodawac od drugiego
		int i=1;
		while (i<listaProsumentow.size())
		{
			Prosument prosument =listaProsumentow.get(i);
			
			virtualProsument.setPojemnoscBaterii(virtualProsument.getPojemnoscBaterii()+prosument.getPojemnoscBaterii());
			virtualProsument.setPredkoscBaterii(virtualProsument.getPredkoscBaterii()+prosument.getPredkoscBaterii());
			
			virtualProsument.addDayDataList(prosument.getDayDataList());
			i++;
		}
		
		this.listaProsumentow = new ArrayList<>();
		listaProsumentow.add(virtualProsument);		
	}
	
	void stworzVirutalProsumentEVScenariusz()
	{
		ProsumentEV virtualProsument = (ProsumentEV) listaProsumentow.get(0);
		virtualProsument.dodajListeDolistaPodrozyFlota(virtualProsument.getEVDataList());
		
		//zacyznamy dodawac od drugiego
		int i=1;
		while (i<listaProsumentow.size())
		{
			Prosument prosument =listaProsumentow.get(i);
			
			virtualProsument.setPojemnoscBaterii(virtualProsument.getPojemnoscBaterii()+prosument.getPojemnoscBaterii());
			virtualProsument.setPredkoscBaterii(virtualProsument.getPredkoscBaterii()+prosument.getPredkoscBaterii());
			
			virtualProsument.addDayDataList(prosument.getDayDataList());
			
			if (prosument instanceof ProsumentEV)
			{
				virtualProsument.dodajListeDolistaPodrozyFlota(((ProsumentEV)prosument).getEVDataList());
			}
			
			i++;
		}
		
		this.listaProsumentow = new ArrayList<>();
		listaProsumentow.add(virtualProsument);
		
	}
	
	public void stworzVirutalProsument()
	{
		print("stworzVirutalProsument");
		
		if (Stale.scenariusz<17)
		{
			stworzVirutalProsumentZwyklyScenariusz();
		}
		else
		{
			stworzVirutalProsumentEVScenariusz();
		}
		
	}
	
	public void zaktualizujProsumentowBrakHandlu()
	{
		int a=0;
		while (a<listaProsumentow.size())
		{
			//dla scenatriuszy EV w listaProsumentow jest ProsumentEV z overridden funkcjom zaktualizujHandelBrakHandlu
			listaProsumentow.get(a).zaktualizujHandelBrakHandlu();
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
	
	
	
	
		
		
	
}
