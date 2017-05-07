
public class Rynek {

	RynekHistory rynekHistory =RynekHistory.getInstance();

	//Singleton shit
	private static Rynek instance = null;
	private Rynek() 
	{
	}
	
   public static Rynek getInstance() {
	      if(instance == null) {
	         instance = new Rynek();
	         instance.inicjalizujRynek();
	         
	      }
	      return instance;
	}
   
   public void inicjalizujRynek()
   {
	   
   }
   
   public void endOfSimulation()
   {
	   
   }
}
