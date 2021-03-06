

public class Agregator {

	
	Reporter reporter = Reporter.getInstance();
	
	//Singleton shit
	private static Agregator instance = null;
	private Agregator() 
	{
	}
	
	public static Agregator getInstance() {
	      if(instance == null) {
	         instance = new Agregator();
	      }
	      return instance;
	}
	
	void run()
	{
		usrednijVirtualnegoProsumenta();
	}
	
	void usrednijVirtualnegoProsumenta()
	{
		ListaProsumentowWrap listaProsumentowWrap = ListaProsumentowWrap.getInstance();
		
		//jest tylko jedne prosument - virtualny prosument
		Prosument prosument = listaProsumentowWrap.getListaProsumentow().get(0);
		prosument.setID(100);
		
		prosument.DayDataDivide(Stale.liczbaProsumentow);
		
		if (prosument instanceof ProsumentEV)
		{
			//trzeba przeskalowac wyniki samochodow elektyrcznych, bo inaczej przy liczeniu rezerwy
			//w "performEndOfSimulationCalculations" bedize zle (odejmowanien nieprzeskalowanej rekompensatyz a baterie od przeskalowanego kosztu)
			((ProsumentEV)prosument).EVDataDivide(Stale.liczbaProsumentow);
			prosument.performEndOfSimulationCalculations();
		}
		else
		{
			prosument.performEndOfSimulationCalculations(false);
		}
		
		reporter.createProsumentReport(prosument);
	}
	

	  
}
