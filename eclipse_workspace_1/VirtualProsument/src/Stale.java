
public class Stale {
	
	static final int scenariusz=6;
	static final float cenaDystrybutoraZewnetrznego=0.56f;
	static final float pojemnoscBateriiIfEnabled=13.5f;
	static final float predkoscBaterii=5;

	
	static final float kosztAmortyzacjiBaterii=0.31f;
	
	static final int liczbaProsumentow=16;
	static final float malaLiczba=00000.1f;
	
	
	static final String folderZDanymi="C:\\Users\\Administrator\\Desktop\\dane_do_symulacji";
	static final String outputFolder="C:\\Users\\Administrator\\Desktop\\symulacjaOutput";
	
	
	
	//full
	static final String simulationEndDate="2015-06-08 00:00";
	
	//debug
	//static final String simulationEndDate="2015-06-03 00:00";
	
	
	
	
	
	
	//Singleton shit
	private static Stale instance = null;
	private Stale() 
	{
		
	}
	
	public static Stale getInstance() {
	      if(instance == null) {
	         instance = new Stale();
	      }
	      return instance;
	}
	
	//-------------------------
	//OTHER FUNCTIONS
	
	static String simulationEndDateGetDay()
	{
		String[] s2 =simulationEndDate.split(" ");
		return s2[0];
	}
}