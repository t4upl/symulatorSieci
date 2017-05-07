
public class RynekHistory {

	//Singleton shit
	private static RynekHistory instance = null;
	private RynekHistory() 
	{
	}

	public static RynekHistory getInstance() {
	      if(instance == null) {
	         instance = new RynekHistory();
	      }
	      return instance;
	}
   	
	
}
