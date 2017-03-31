
public class Stale {
	
	
	static final int scenariusz=2;
	
	static final float pojemnoscBateriiIfEnabled=13.5f;
	static final float predkoscBaterii=5;

	static final int horyzontCzasowy=24;

	
	static final float kosztAmortyzacjiBaterii=0.31f;
	
	//nie final, bo testy sa wykonywnae na 2 prosumentach
	static int liczbaProsumentow=16;
	static final float malaLiczba=00000.1f;
	
	
	static final String folderZDanymi="C:\\Users\\Administrator\\Desktop\\dane_do_symulacji";
	static final String outputFolder="C:\\Users\\Administrator\\Desktop\\symulacjaOutput";
	
	//okresla czy wykorzystac cenyz  generatora cyz wykorzystac wektor cen z zewnatrz
	//np z poprzedniej iteracji
	static final boolean cenyZGeneratora = true;
	
	
	//cena minimalna i maksymalna na rynku lokalnym
	static final float cenaMinimalnaNaRynkuLokalnym =0f;
	static final float cenaDystrybutoraZewnetrznego=0.56f;
	
	//used in Optimizer
	static double reallyBigNumber =10000.0; //used while creating cariables

	//stlae zwiazane z handlem
	static int limitIteracji = 4;
	static int limitBisekcji =10;
	
	//jak bardoz probkowac podzial uzyskany w wyniku bisekcji
	static int iloscRownomiernychPodzialow = 200;

	
	//full - do uzycia przy juz dzialajacej symulacji
	//static final String simulationEndDate="2015-06-08 00:00";
	
	//debug - for testing purposes only
	static final String simulationEndDate="2015-06-03 00:00";
	
	//debug extreme - for testing purposes only, when nothing goes ok
	//static final String simulationEndDate="2015-06-02 00:00";	
	
	
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
}