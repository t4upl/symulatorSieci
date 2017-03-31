package symulacja2;

import java.util.Arrays;

import ilog.concert.IloException;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloNumExpr;
import ilog.concert.IloNumVar;
import ilog.concert.IloNumVarType;
import ilog.cplex.*;

//JUST TEST NOT PART OF PROGRAM!
public class CplexTest {
	public void test1()
	{
		try {
			IloCplex cplex = new IloCplex();
			IloNumVar x = cplex.numVar(0, 3);
			IloNumVar y = cplex.numVar(0, 3);
			
			
			IloLinearNumExpr object= cplex.linearNumExpr();
			object.addTerm(0.12, x);
			object.addTerm(0.15, y);
			
			cplex.addMinimize(object);

			
			
			cplex.addMaximize(cplex.prod(x, 2));
			cplex.solve();


			

			
			
		} catch (IloException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	//Works!!!
	public void test2()
	{
		try {
			IloCplex cplex = new IloCplex();
			
			double[] xlb={0.0,0.0,0.0};
			double[] xub={10.0,40.0,50.0};
			IloNumVarType[] xt  = {IloNumVarType.Float,IloNumVarType.Float,IloNumVarType.Float};
			IloNumVar[] x  = cplex.numVarArray(3, xlb, xub, xt);
			
			double[] ylb={0.0,0.0,0.0};
			double[] yub={1.0,1.0,1.0};
			IloNumVarType[] yt  = {IloNumVarType.Int,IloNumVarType.Int,IloNumVarType.Int};
			IloNumVar[] y  = cplex.numVarArray(3, ylb, yub, yt);
			
			IloNumExpr objective= cplex.scalProd(y, x);
			cplex.addMaximize(objective);
			cplex.addLe(cplex.sum(y),1);
			
			System.out.print(objective);
			
			cplex.solve();
			
			double[] x22=cplex.getValues(x);
			double[] y22=cplex.getValues(y);

			
			System.out.println(Arrays.toString(x22));
			System.out.println(Arrays.toString(y22));

			
			
		} catch (IloException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	    //IloNumVar[] x  = model.numVarArray(12, xlb, xub, xt);

	}
}
