import java.util.ArrayList;
import java.util.Arrays;

import ilog.concert.IloException;
import ilog.concert.IloNumExpr;
import ilog.concert.IloNumVar;
import ilog.concert.IloNumVarType;
import ilog.cplex.IloCplex;

//FUNKCJONALNOSC OptimizerEV przeniesiona do OptimizerEV2
//jedyny powod dla ktorego ta kalsa zostala pozostawiona to klasy zagniezdzone
//Form i Sterowanie
public class OptimizerEV extends CoreClass {

	
	private static OptimizerEV instance = null;
	private OptimizerEV() 
	{
	}
	
	public static OptimizerEV getInstance() {
	      if(instance == null) {
	         instance = new OptimizerEV();
	      }
	      return instance;
	}	
	
	
	public static class Sterowanie
	{
		ArrayList<DayData> dayDataList = new ArrayList<DayData>();		
		ArrayList<ArrayList<EVData>> eVDataList = new ArrayList<>();  
		
		double[] koszt;
		double[] koszt_Zew;
		double[] koszt_sklad;
		double[] koszt_EV;
		
		
		
		public double[] getKoszt() {
			return koszt;
		}

		public void setKoszt(double[] koszt) {
			this.koszt = koszt;
		}

		public double[] getKoszt_Zew() {
			return koszt_Zew;
		}

		public void setKoszt_Zew(double[] koszt_Zew) {
			this.koszt_Zew = koszt_Zew;
		}

		public double[] getKoszt_sklad() {
			return koszt_sklad;
		}

		public void setKoszt_sklad(double[] koszt_sklad) {
			this.koszt_sklad = koszt_sklad;
		}

		public double[] getKoszt_EV() {
			return koszt_EV;
		}

		public void setKoszt_EV(double[] koszt_EV) {
			this.koszt_EV = koszt_EV;
		}

		public ArrayList<DayData> getDayDataList() {
			return dayDataList;
		}

		public ArrayList<ArrayList<EVData>> geteVDataList() {
			return eVDataList;
		}

		void addToDayDataList(DayData d)
		{
			dayDataList.add(d);
		}
		
		void addToEVDataList(ArrayList<EVData> eVDataList)
		{
			this.eVDataList.add(eVDataList);
		}
		
	} 
	
	//zawiera wszystkie wektory (dlugosci 24) potrzebne do przeprowadzenia optymalizacji
	static public class Form24
	{
		//TRZEBA DOISAC GENERACJE I KONSUMOCJE W HORYZONCIE 24
		
		ArrayList<DayData> dayDataList;
		ArrayList<ArrayList<EVData>> eVDataList;
		
		float[] generacjaArray;
		float[] konsumpcjaArray;
		
		float stanPoczatkowyBaterii;
		
		//lista bo dla akzdego smaochodu to trzeba wypelnic
		ArrayList<Float> stanPoczatkowyEVdomList = new ArrayList<>();
		
		//pod indeksem zaiwera lsite dla i-tego smaochodu zaiwerajaca statusy samochodowo
		ArrayList<ArrayList<Integer>> statusEV = new ArrayList<>();
		
		
		public ArrayList<ArrayList<Integer>> getStatusEV() {
			return statusEV;
		}

		public void addToStatusEV(ArrayList<Integer> L1)
		{
			statusEV.add(L1);
		}
		
		public void addToStanPoczatkowyEVdomList(Float value)
		{
			stanPoczatkowyEVdomList.add(value);
		}
		
		public ArrayList<Float> getStanPoczatkowyEVdomList() {
			return stanPoczatkowyEVdomList;
		}
		
		public float getStanPoczatkowyBaterii() {
			return stanPoczatkowyBaterii;
		}
		public void setStanPoczatkowyBaterii(float stanPoczatkowyBaterii) {
			this.stanPoczatkowyBaterii = stanPoczatkowyBaterii;
		}

		
		public float[] getGeneracjaArray() {
			return generacjaArray;
		}
		public void setGeneracjaArray(float[] generacjaArray) {
			this.generacjaArray = generacjaArray;
		}
		public float[] getKonsumpcjaArray() {
			return konsumpcjaArray;
		}
		public void setKonsumpcjaArray(float[] konsumpcjaArray) {
			this.konsumpcjaArray = konsumpcjaArray;
		}

		



		

		
		public ArrayList<DayData> getDayDataList() {
			return dayDataList;
		}
		public void setDayDataList(ArrayList<DayData> dayDataList) {
			this.dayDataList = dayDataList;
		}
		public ArrayList<ArrayList<EVData>> geteVDataList() {
			return eVDataList;
		}
		public void seteVDataList(ArrayList<ArrayList<EVData>> eVDataList) {
			this.eVDataList = eVDataList;
		}
	}
	
}
