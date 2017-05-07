import java.util.ArrayList;

public class ListaProsumentowWrap extends CoreClass {

	//mnoznik generacji dobrnay tak, zeby generacja z prosumentow generujacych energie (pierwszych 4)
	//byla rowna zuzyciu wszystkich prosumentow
	double mnoznikGeneracji;
	
	private ArrayList<Prosument> listaProsumentow = new ArrayList<>();

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
			case 7: scenario17(); break;
			case 8: scenario18(); break;
			case 9: scenario19(); break;
			case 10: scenario20(); break;
			case 11: scenario21(); break;
			case 12: scenario22(); break;
			case 13: scenario23(); break;
			
			default: System.out.println("!!!NO SUCH SCENARIO"); getInput();
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
		
		print("mnoznik "+mnoznik);
		
		return mnoznik;
	}
	
	//oblicza ilosc energii jaka wydaja wszsycy prosumenci wchdozacy w sklad symualcji na rzecz podrozy EV
	float obliczZuzycieEnergiinaEV()
	{
		print ("obliczZuzycieEnergiinaEV");
		
		float sum=0;
		
		int liczbaDni=(LokalneCentrum.getSimulationEndDateIndex()-1)/24;
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
	
	
}