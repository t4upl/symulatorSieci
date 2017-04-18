import java.util.ArrayList;
import java.util.Scanner;

import ilog.concert.IloException;
import ilog.concert.IloNumExpr;
import ilog.cplex.IloCplex;

public class OptimizerEV extends CoreClass {

	
	//set it to false for test (test =no EVs)
	final Boolean EVwystpeujeWmodelu =true;

	IloCplex cplex;
	Form form;
	
	//Singleton part
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
		
	}
	
	public static class Form
	{
		ArrayList<Float> prices = new ArrayList<>();
		ArrayList<Float> consumption = new ArrayList<>();
		ArrayList<Float> generation = new ArrayList<>();
		
		ArrayList<Integer> statusyEV = new ArrayList<>();
		
		
		public ArrayList<Float> getConsumption() {
			return consumption;
		}
		public void setConsumption(ArrayList<Float> consumption) {
			this.consumption = consumption;
		}
		public ArrayList<Float> getGeneration() {
			return generation;
		}
		public void setGeneration(ArrayList<Float> generation) {
			this.generation = generation;
		}
		public ArrayList<Integer> getStatusyEV() {
			return statusyEV;
		}
		public void setStatusyEV(ArrayList<Integer> statusyEV) {
			this.statusyEV = statusyEV;
		}
		
	}
	
	//---------------------------
	
	//TODO
	public Sterowanie wyznaczSterowanie(Form form24)
	{
		
		Sterowanie sterowanie = new OptimizerEV.Sterowanie();		

		try{
			cplex = new IloCplex();
			setVariables(form24);
			
			IloNumExpr objective = createObjective();
			//print("wyznaczSterowanie "+objective.toString());
			
			cplex.addMinimize(objective);
			
			dodajOgraniczeniaPoczatkowyStanBaterii();
			dodajOgraniczeniaModelu();
			dodajOgraniczeniaEV();

			cplex.setOut(null);			
			if (cplex.solve())
			{
				//Status status =cplex.getStatus();
				//print("STATUS "+ status.toString());
				
				fillOutSolvedVariables(sterowanie);
				cplex.end();
				//getInput("CPLEX success");
			}
			else
			{
				getInput("CPLEX FAILED!");
			}			
			
		} catch (Exception e)
		{
			print ("ERROR in wyznaczSterowanie");
		}
		
		return sterowanie;
	}
	
	//TODO
	private IloNumExpr createObjective() {
		IloNumExpr objective = null;
		
		try {
			int i =0;
			while (i<Stale.horyzontCzasowy)
			{

				createObjectiveKosztZew(i);
				createObjectiveKosztSklad(i);
				createObjectiveKosztEV(i);
				createObjectiveKosztHandel(i);

				
				cplex.addEq(koszt[i], cplex.sum(koszt_Zew[i],koszt_sklad[i],koszt_EV[i], koszt_handel[i]  ));
				if (i==0)
				{
					objective =koszt[i];
				}
				else
				{
					objective =cplex.sum(objective,koszt[i]);
				}
				
				i++;
			}
		} catch (IloException e) {
			e.printStackTrace();
		}
	
		return objective;				
	}

	//TODO
	void setVariables(Form form24)
	{
		
	}
	
	
	
	
}