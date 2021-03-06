
public class Stale {
	
	//dla scenariuszy z EV zawsze nazwa scenariusza+10
	static final int scenariusz=17;
	static final float cenaDystrybutoraZewnetrznego=0.56f;
	
	
	static final float pojemnoscBateriiIfEnabled=13.5f;
	static final float predkoscBaterii=5;
	static final float kosztAmortyzacjiBaterii=0.31f;

	static final float pojemnoscAkumualtoraEV=85;
	static final float predkoscAkumulatoraEV=18.89f;
	static final float kosztAmortyzacjiAkumulatoraEV=0.078f;
	
	
	//energia potrzebna na przeproawdzenie podrozy
	static final float podrozMinimumEnergii = 3.6f;
	static final float podrozPredkoscAkumulatoraEV = predkoscAkumulatoraEV/2;
	
	static final int liczbaProsumentow=16;
	static final float malaLiczba=00000.1f;
	
	static final int horyzontCzasowy=24;	
	
	static final String folderZDanymi="C:\\Users\\Administrator\\Desktop\\dane_do_symulacji";
	static final String outputFolder="C:\\Users\\Administrator\\Desktop\\symulacjaOutput";
	
	//used in Optimizer
	static double reallyBigNumber =10000.0; //used while creating cariables

	//nie final bo ustaiwane w scneariuszu
	static Boolean handelWPracy=true;
	
	
	//full
	static final String simulationEndDate="2015-06-08 00:00";
	
	//debug
	//static final String simulationEndDate="2015-06-03 00:00";
	
	static int numerPierwszegoScenariuszaZawierajacegoEV=17;

	//okresla czy w scenariuszu wystepuja EV
	//nie final bo ustaiwane rpzy modyfikatorze scenariusza
	static boolean isScenariuszEV;
	
	
	
	
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
