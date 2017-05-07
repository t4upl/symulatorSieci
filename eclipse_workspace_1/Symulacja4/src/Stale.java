
public class Stale {

	static final int scenariusz=1;
	
	//okresla czy wykorzystac cenyz  generatora cyz wykorzystac wektor cena z zewnatrz
	//np z poprzedniej iteracji
	static final boolean isCenyZGeneratora = true;
	
	static  final boolean isVirtualnyProsument = false;
	
	static final String folderZDanymi="C:\\Users\\Administrator\\Desktop\\dane_do_symulacji";
	static final String outputFolder="C:\\Users\\Administrator\\Desktop\\symulacjaOutput2";
	
	
	static int numerPierwszegoScenariuszaZawierajacegoEV=17;

	
	//nie final, bo testy sa wykonywnae na 2 prosumentach
	static int liczbaProsumentow=16;
	
	
	
	//BATERIA
	
	static final float pojemnoscBateriiIfEnabled=13.5f;
	static final float predkoscBaterii=5;
	
	//EV
	
	
	static boolean isScenariuszEV;
	
	//jak bardoz probkowac podzial uzyskany w wyniku bisekcji
	static int iloscRownomiernychPodzialow = 200;

	static final float pojemnoscAkumualtoraEV=85;
	static final float predkoscAkumulatoraEV=18.89f;
	static final float kosztAmortyzacjiAkumulatoraEV=0.078f;
	
	//energia potrzebna na przeproawdzenie podrozy
	static final float podrozMinimumEnergii = 3.6f;
	static final float podrozPredkoscAkumulatoraEV = predkoscAkumulatoraEV/2;
	
	//nie final bo ustaiwane w scneariuszu
	static Boolean isHandelWPracy=true;
	
	
	
	//full - do uzycia przy juz dzialajacej symulacji
	//static final String simulationEndDate="2015-06-08 00:00";
	
	//debug - for testing purposes only
	static final String simulationEndDate="2015-06-03 00:00";
	
	//debug extreme - for testing purposes only, when nothing goes ok
	//static final String simulationEndDate="2015-06-02 00:00";	
	
	
	
}
