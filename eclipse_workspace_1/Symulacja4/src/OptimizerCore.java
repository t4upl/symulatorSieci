import ilog.concert.IloException;
import ilog.concert.IloNumVar;
import ilog.concert.IloNumVarType;
import ilog.cplex.IloCplex;

public class OptimizerCore extends CoreClass {

	IloCplex cplex;

	//koszt w chwili t
	IloNumVar[] koszt; 
	IloNumVar[] koszt_Zew;
	IloNumVar[] koszt_sklad;
	IloNumVar[] koszt_EV;
	IloNumVar[] koszt_handel;
	
	
	IloNumVar[] EB; 
	IloNumVar[] EB_c; 
	IloNumVar[] G_c; 
	IloNumVar[] EM_s; 
	IloNumVar[] G_sklad;
	IloNumVar[] G_s;
	IloNumVar[] EM_c;
	IloNumVar[] EB_s;
	
	//czy gospodrastwo domowe kupuje energiie z lokalnego rynku
	IloNumVar[] binHandelKupuj;
	
	
	
	double[] EB_solved; 
	double[] EB_c_solved; 
	double[] G_c_solved; 
	double[] EM_s_solved; 
	double[] G_sklad_solved;
	double[] G_s_solved;
	double[] EM_c_solved;
	double[] EB_s_solved;
	
	
	//zmienne dodatkowe
	IloNumVar[] EM_s_Z; 
	IloNumVar[] EB_s_Z; 
	IloNumVar[] G_s_Z; 
	IloNumVar[] EM_c_Z;
	
	//Solved 
	
	double[] binHandelKupuj_solved;
	
	double[] koszt_solved; 
	double[] koszt_Zew_solved;
	double[] koszt_sklad_solved;
	double[] koszt_handel_solved;

	
	
	IloNumVar[] stworzWektorZmiennychCiaglych(int liczbaElementow, double lowerBoundValue, double upperBoundValue, IloCplex cplex)
	{
		IloNumVar[] output=null;
		try {

			//IloNumVar[] output= new	IloNumVar[liczbaElementow];
			double[] lb =createDoubleArray(lowerBoundValue,liczbaElementow);
			double[] ub =createDoubleArray(upperBoundValue,liczbaElementow);
			IloNumVarType[] type =createIloNumVarTypeArray(IloNumVarType.Float,liczbaElementow);

			output  = cplex.numVarArray(liczbaElementow, lb, ub, type);		
		} catch (IloException e) {
			getInput("ERROR stworzWektorZmiennychBinarnych");
			e.printStackTrace();
		}		
		
		return output;
	}	
	
	IloNumVar[] stworzWektorZmiennychCiaglych(int liczbaElementow, double upperBoundValue, IloCplex cplex)
	{
		return stworzWektorZmiennychCiaglych(liczbaElementow, 0,upperBoundValue, cplex);
	}
	
	double[] createDoubleArray(double value,int liczbaElementow)
	{
		double[] d= new double[liczbaElementow];
		int a=0;
		while (a<liczbaElementow)
		{
			d[a]=value;
			a++;
		}
		
		return d;
	}
	
	IloNumVarType[] createIloNumVarTypeArray(IloNumVarType type,int liczbaElementow)
	{
		IloNumVarType[] d= new IloNumVarType[liczbaElementow];
		int a=0;
		while (a<liczbaElementow)
		{
			d[a]=type;
			a++;
		}
		
		return d;
	}
	
	IloNumVar[] stworzWektorZmiennychBinarnych(int liczbaElementow, IloCplex cplex)
	{
		IloNumVar[] output=null;

		try {

			double[] lb =createDoubleArray(0.0,liczbaElementow);
			double[] ub =createDoubleArray(1.0,liczbaElementow);
			IloNumVarType[] type =createIloNumVarTypeArray(IloNumVarType.Int,liczbaElementow);

			output  = cplex.numVarArray(liczbaElementow, lb, ub, type);
			
		} catch (IloException e) {
			
			print("stworzWektorZmiennychBinarnych");
			e.printStackTrace();
		}		
		
		return output;
	}	
	
	IloNumVar[] stworzWektorZmiennychDodatkowych(int liczbaElementow, double upperBoundValue,IloNumVar[] bin,IloNumVar[] ciagla, IloCplex cplex)
	{
		IloNumVar[] output = stworzWektorZmiennychCiaglych(liczbaElementow, upperBoundValue, cplex);
		
		//dodaj ograniczenia na zmienna dodatkowa Z
		int i=0;
		while (i<liczbaElementow)
		{
			try {
				//1
				cplex.addLe(output[i],cplex.prod(bin[i], upperBoundValue));
				
				//3 (L=0 wiec bez 2, ktore wymuszone przez granice przy tworzeniu output)
				cplex.addLe(output[i],ciagla[i]);
				cplex.addGe(output[i], cplex.diff(ciagla[i],cplex.prod(upperBoundValue, cplex.diff(1, bin[i]))  ));
				
			} catch (IloException e) {
				
				e.printStackTrace();
				getInput("Errror in stworzWektorZmiennychDodatkowych");
			}
			i++;
		}
		
		return output;
	}
	
}
