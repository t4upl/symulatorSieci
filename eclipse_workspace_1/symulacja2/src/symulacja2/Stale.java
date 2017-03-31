package symulacja2;

public class Stale {
	
	static final int scenariusz = 5;
	static final float cenaDystrybutoraZewnetrznego=0.56f;
	static final float pojemnoscBateriiIfEnabled=13.5f;
	static final float kosztAmortyzacjiBaterii=0.31f;
	static final int horyzontCzasowy=24;
	static final int liczbaProsumentow=16;
	static final float malaLiczba=00000.1f;
	
	//okresla czy wykorzystac cenyz  generatora cyz wykorzystac wektor cen z zewnatrz
	//np z poprzedniej iteracji
	static final boolean cenyZGeneratora = true;
	
	
	//full
	static final String simulationEndDate="2015-06-08 00:00";
	
	//debug
	//static final String simulationEndDate="2015-06-03 00:00";
	
	static String simulationEndDateGetDay()
	{
		String[] s2 =simulationEndDate.split(" ");
		return s2[0];
	}
	
}
