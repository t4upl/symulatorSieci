

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
		
		prosument.performEndOfSimulationCalculations(false);
		reporter.createProsumentReport(prosument);
	}
	

	  
}
