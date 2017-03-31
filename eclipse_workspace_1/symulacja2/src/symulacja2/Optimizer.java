package symulacja2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

import ilog.concert.IloException;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloNumExpr;
import ilog.concert.IloNumVar;
import ilog.concert.IloNumVarType;
import ilog.concert.IloObjective;
import ilog.cplex.IloCplex;
import ilog.cplex.IloCplex.BooleanParam;
import ilog.cplex.IloCplex.UnknownObjectException;

public class Optimizer {

	//OUT OF USE!!! USE OPTTIMIZER2 instead
	
	Scanner sc = new Scanner(System.in);
	
	Prosument2 prosument;
	
	//Solved variables
	private double[] EB_solved; 
	private double[] EM_c_solved; 
	private double[] EB_c_solved; 
	private double[] G_c_solved; 
	private double[] EM_s_solved; 
	private double[] G_sklad_solved; 
	private double[] EB_s_solved; 
	private double[] G_s_solved; 
	private double[] binHandelKupuj_solved;
	
	private double[] EM_s_Z_solved; 
	private double[] EB_s_Z_solved; 
	private double[] G_s_Z_solved; 
	private double[] EM_c_Z_solved;
	
	
	private double reallySmallNumber = 0.0001;
	private double reallyBigNumber =10000.0;
	private double reallySmallNumberConstrain =0.0001;
	
	
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
	
	IloNumVar[] stworzWektorZmiennychBinarnych(int liczbaElementow)
	{
		IloNumVar[] output=null;
		IloCplex cplex;
		try {
			cplex = new IloCplex();

			//IloNumVar[] output= new	IloNumVar[liczbaElementow];
			double[] lb =createDoubleArray(0.0,liczbaElementow);
			double[] ub =createDoubleArray(1.0,liczbaElementow);
			IloNumVarType[] type =createIloNumVarTypeArray(IloNumVarType.Int,liczbaElementow);

			output  = cplex.numVarArray(liczbaElementow, lb, ub, type);
			
		} catch (IloException e) {
			// TODO Auto-generated catch block
			print("stworzWektorZmiennychBinarnych");
			e.printStackTrace();
		}		
		
		return output;
	}
	
	IloNumVar[] stworzWektorZmiennychCiaglych(int liczbaElementow, double upperBoundValue)
	{
		IloNumVar[] output=null;
		IloCplex cplex;
		try {
			cplex = new IloCplex();

			//IloNumVar[] output= new	IloNumVar[liczbaElementow];
			double[] lb =createDoubleArray(0.0,liczbaElementow);
			double[] ub =createDoubleArray(upperBoundValue,liczbaElementow);
			IloNumVarType[] type =createIloNumVarTypeArray(IloNumVarType.Float,liczbaElementow);

			output  = cplex.numVarArray(liczbaElementow, lb, ub, type);
			
		} catch (IloException e) {
			// TODO Auto-generated catch block
			print("stworzWektorZmiennychBinarnych");
			e.printStackTrace();
		}		
		
		return output;
	}
	
	float[] getGenerationArray(ArrayList<DayData2> dayDataList24)
	{
		return getXXXArray(dayDataList24,"generation");
	}
	
	float[] getConsumptionArray(ArrayList<DayData2> dayDataList24)
	{
		return getXXXArray(dayDataList24,"consumption");
	}
	
	float[] getXXXArray(ArrayList<DayData2> dayDataList24,String type)
	{
		float[] f = new float[dayDataList24.size()];
		
		int a=0;
		while(a<f.length)
		{
			if (type.equals("generation"))
			{
				f[a]=dayDataList24.get(a).getTrueGeneration();
			}
			
			if (type.equals("consumption"))
			{
				f[a]=dayDataList24.get(a).getConsumption();
				
			}
			
			a++;
		}
		
		return f;
	}
	
	public ArrayList<DayData2> wyznaczSterowanie2(ArrayList<Float> priceVector, int horyzontCzasowy, Prosument2 prosument, float stanPoczatkowyBaterii )
	{
		ArrayList<DayData2> L1 = new ArrayList<DayData2>();
		

		//print("wyznaczSterowanie2 ID:"+prosument.getID());
		
		try {
			IloCplex cplex = new IloCplex();
			
			float predkoscBaterii =prosument.getPredkoscBaterii();
			float cenaDystrybutoraZewnetrznego =prosument.getCenaDystrybutoraZewnetrznego();
			float cenaBaterii = prosument.getKosztAmortyzacjiBaterii();
			float pojemnoscBaterii = prosument.getPojemnoscBaterii();
			
			this.prosument = prosument;
			
			int horyzontCzasu =priceVector.size();
			
			IloNumVar[] binHandelKupuj = stworzWektorZmiennychBinarnych(horyzontCzasu);


			IloNumVar[] EB = stworzWektorZmiennychCiaglych(horyzontCzasu,pojemnoscBaterii); 
			IloNumVar[] EM_c = stworzWektorZmiennychCiaglych(horyzontCzasu,reallyBigNumber); 
			IloNumVar[] EB_c = stworzWektorZmiennychCiaglych(horyzontCzasu,predkoscBaterii); 
			IloNumVar[] G_c = stworzWektorZmiennychCiaglych(horyzontCzasu,reallyBigNumber); 
			IloNumVar[] EM_s = stworzWektorZmiennychCiaglych(horyzontCzasu,predkoscBaterii); 
			IloNumVar[] G_sklad = stworzWektorZmiennychCiaglych(horyzontCzasu,predkoscBaterii); 
			IloNumVar[] EB_s = stworzWektorZmiennychCiaglych(horyzontCzasu,predkoscBaterii); 
			IloNumVar[] G_s = stworzWektorZmiennychCiaglych(horyzontCzasu,reallyBigNumber); 
			
			//zmienne dodatkowe do poradzenia sobie z kwadratowa natura constrainow
			IloNumVar[] EM_s_Z = stworzWektorZmiennychCiaglych(horyzontCzasu,predkoscBaterii); 
			IloNumVar[] EB_s_Z = stworzWektorZmiennychCiaglych(horyzontCzasu,predkoscBaterii); 
			IloNumVar[] G_s_Z = stworzWektorZmiennychCiaglych(horyzontCzasu,reallyBigNumber); 
			IloNumVar[] EM_c_Z = stworzWektorZmiennychCiaglych(horyzontCzasu,reallyBigNumber);
			
			ArrayList<DayData2> dayDataList24 = prosument.getDayDataListInT(horyzontCzasu);
			
			float[] C = getConsumptionArray(dayDataList24);
			float[] G = getGenerationArray(dayDataList24);

			


			
			IloNumExpr objective = null;
			int a=0;
			while (a<horyzontCzasu)
			{
				
				IloNumExpr objectiveAdd =createObjective(cplex, binHandelKupuj, EB, EM_c, EB_c, G_c, EM_s, G_sklad, EB_s, G_s, 
						predkoscBaterii, cenaDystrybutoraZewnetrznego, cenaBaterii, pojemnoscBaterii,  a, EM_s_Z, EB_s_Z, G_s_Z, EM_c_Z,priceVector);
				
				//createObjective();
				if (objective==null)
				{
					objective=objectiveAdd;
				}
				else
				{
					objective=cplex.sum(objective,objectiveAdd);
				}
				
				createConstrain(cplex, binHandelKupuj, EB, EM_c, EB_c, G_c, EM_s, G_sklad, EB_s, G_s, 
						predkoscBaterii, cenaDystrybutoraZewnetrznego, cenaBaterii, pojemnoscBaterii,  a,reallyBigNumber,
						EM_s_Z, EB_s_Z,G_s_Z,EM_c_Z,C,G);
				a++;
			}
			
			cplex.addEq(stanPoczatkowyBaterii, EB[0]);

			
			//wrap it up
			cplex.addMinimize(objective);
			
			cplex.setOut(null);
			if (cplex.solve())
			{	
				EB_solved =cplex.getValues(EB); 
				EM_c_solved=cplex.getValues(EM_c);  
				EB_c_solved=cplex.getValues(EB_c);   
				G_c_solved=cplex.getValues(G_c);  ; 
				EM_s_solved=cplex.getValues(EM_s);  
				G_sklad_solved=cplex.getValues(G_sklad); 
				EB_s_solved=cplex.getValues(EB_s); 
				G_s_solved=cplex.getValues(G_s); 
				binHandelKupuj_solved=cplex.getValues(binHandelKupuj);
				
				EM_s_Z_solved=cplex.getValues(EM_s_Z); 
				EB_s_Z_solved=cplex.getValues(EB_s_Z); 
				G_s_Z_solved=cplex.getValues(G_s_Z); 
				EM_c_Z_solved=cplex.getValues(EM_c_Z);
				
				constrainCheck(G,C,predkoscBaterii);
				zapakujDoData2List(L1);
				
			}
			else
			{
				getInput("CPLEX FAILED!");
			}
			

			
			
		} catch (IloException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return L1;
	}
	
	void zapakujDoData2List(ArrayList<DayData2> L1)
	{
		
		int a=0;
		while (a<EB_solved.length)
		{
			
			
			DayData2 d= new DayData2();
			
			d.setStanBateriiNaPoczatkuSlotu((float)EB_solved[a]);

			d.setZBateriiNaKonsumpcje((float)EB_c_solved[a]);
			d.setZBateriiNaRynek((float)(EB_s_solved[a]*(1-binHandelKupuj_solved[a])));
			
			d.setZGeneracjiDoBaterii((float)G_sklad_solved[a]);
			d.setZGeneracjiNaKonsumpcje((float)G_c_solved[a]);
			d.setZGeneracjiNaRynek((float)(G_s_solved[a]*(1-binHandelKupuj_solved[a])));
			
			d.setZRynekDoBaterii((float)EM_s_Z_solved[a]);
			d.setZRynekNaKonsumpcje((float)EM_c_Z_solved[a]);
			
			d.setKupuj((float)binHandelKupuj_solved[a]);
			
			L1.add(d);
			a++;
		}
	}
	
	void constrainCheck(float[] G, float[] C, float predkoscBaterii)
	{	
		int a=0;
		while (a<EB_solved.length)
		{
			
			if (a>1)
			{
				//cons1 
				double cons1_value = EB_solved[a-1]-EB_c_solved[a-1]-(1-binHandelKupuj_solved[a-1])*EB_s_solved[a-1]+EM_s_Z_solved[a-1]+G_sklad_solved[a-1];
				cons1_value = EB_solved[a]-cons1_value;
				
				if (Math.abs(cons1_value)>reallySmallNumber)
				{
					print("cons1 violated");
					print("EB_solved[a]: "+EB_solved[a]);
					print("EB_solved[a-1] "+EB_solved[a-1]);
					print("EB_s_Z_solved[a-1] "+EB_s_Z_solved[a-1]);
					
					print("EM_s_Z_solved[a-1] "+EM_s_Z_solved[a-1]);
					print("G_sklad_solved[a-1] "+G_sklad_solved[a-1]);
					getInput();
				}
				
				
				//cons3
				if (EB_solved[a]<EB_c_solved[a]+(1-binHandelKupuj_solved[a])*EB_s_solved[a])
				{
					getInput("cons3 violated");
				}
				
				//cons 6
				double cons6_value = Math.abs(EB_solved[a]-EB_solved[a-1]);
				if (cons6_value-reallySmallNumberConstrain>predkoscBaterii)
				{
					print(""+cons6_value);
					print(""+predkoscBaterii);
					print(""+EB_solved[a]);
					print(""+EB_solved[a-1]);
					getInput("cons6 violated");
					
				}
					
			}
			
			
			//cons2
			if (G[a]+reallySmallNumberConstrain<G_c_solved[a]+(1-binHandelKupuj_solved[a])*G_s_solved[a]+G_sklad_solved[a])
			{
				getInput("cons2 violated - suma skladowych generacji = generacji");
				print("prosument: "+prosument.getID());
				print("index "+a);
				
				print(G[a]+"");
				print((1-binHandelKupuj_solved[a])+"");
				print(G_s_solved[a]+"");
				print(G_sklad_solved[a]+"");
				double suma  = G_c_solved[a]+(1-binHandelKupuj_solved[a])*G_s_solved[a]+G_sklad_solved[a];
				print("suma: "+suma);
				print ("miara bledu: "+(suma-G[a]));
				
				getInput();
			}
			
			
			//cons5 - wyrownanie 
			
			if (C[a]+reallySmallNumberConstrain<(binHandelKupuj_solved[a])*EM_c_solved[a]+EB_c_solved[a]+G_c_solved[a])
			{
				print("a: "+a);
				print(C[a]+"");
				print("kupuj: "+binHandelKupuj_solved[a]);
				print(""+EM_c_solved[a]);
				print(""+EB_c_solved[a]);
				print(""+G_c_solved[a]);
				float sum =(float)((binHandelKupuj_solved[a])*EM_c_solved[a]+EB_c_solved[a]+G_c_solved[a]);
				print("sum "+sum);
				getInput("cons5 violated");
			}
			
			
			a++;
		}
	}
	
	IloNumExpr createConstrain(IloCplex cplex, IloNumVar[] binHandelKupuj, IloNumVar[] EB, IloNumVar[] EM_c, IloNumVar[] EB_c, IloNumVar[] G_c, IloNumVar[] EM_s, IloNumVar[] G_sklad, IloNumVar[] EB_s, IloNumVar[] G_s, 
			float predkoscBaterii, float cenaDystrybutoraZewnetrznego, float cenaBaterii, float pojemnoscBaterii, int a,double reallyBigNumber, 
			IloNumVar[] EM_s_Z, IloNumVar[] EB_s_Z, IloNumVar[] G_s_Z,IloNumVar[] EM_c_Z,float[] C, float[] G)
	{

		
		IloNumExpr exp = null;

		try {
			//IloNumExpr cbin1 = cplex.prod(EM_c[a-1],binHandelKupuj[a-1]);
			
			//Ograniczenia dodatkopwych zmiennych
			cplex.addLe(EM_s_Z[a],cplex.prod(predkoscBaterii, binHandelKupuj[a]));			
			cplex.addLe(EM_s_Z[a],EM_s[a]);
			cplex.addGe(EM_s_Z[a], cplex.diff(EM_s[a], cplex.prod(predkoscBaterii, cplex.diff(1.0, binHandelKupuj[a]))));
			
			cplex.addLe(EB_s_Z[a],cplex.prod(predkoscBaterii, binHandelKupuj[a]));			
			cplex.addLe(EB_s_Z[a],EB_s[a]);
			cplex.addGe(EB_s_Z[a], cplex.diff(EB_s[a], cplex.prod(predkoscBaterii, cplex.diff(1.0, binHandelKupuj[a]))));
			
			cplex.addLe(G_s_Z[a],cplex.prod(reallyBigNumber, binHandelKupuj[a]));			
			cplex.addLe(G_s_Z[a],G_s[a]);
			cplex.addGe(G_s_Z[a],cplex.diff(G_s[a], cplex.prod(reallyBigNumber, cplex.diff(1.0, binHandelKupuj[a]))));
			
			cplex.addLe(EM_c_Z[a],cplex.prod(reallyBigNumber, binHandelKupuj[a]));			
			cplex.addLe(EM_c_Z[a],EM_c[a]);
			cplex.addGe(EM_c_Z[a],cplex.diff(EM_c[a], cplex.prod(reallyBigNumber, cplex.diff(1.0, binHandelKupuj[a]))));
			
			
			
			if (a>0)
			{
				//constrain 1
				
				IloNumExpr cons1 = cplex.sum(EB[a-1],G_sklad[a-1],EM_s_Z[a-1], EB_s_Z[a-1]);
				
				cons1 = cplex.diff(cons1, EB_c[a-1]);
				cons1 = cplex.diff(cons1, EB_s[a-1]);
				
				cplex.addEq(EB[a],cons1);

				//constrain 3
				
				IloNumExpr cons3 = cplex.sum(EB_c[a],EB_s[a]);
				cons3 = cplex.diff(cons3, EB_s_Z[a]);
				
				cplex.addGe(EB[a],cons3 );
				
				//constrain 7
				cplex.addLe(cplex.diff(EB[a-1], EB[a]),predkoscBaterii); //7a
				cplex.addGe(cplex.diff(EB[a-1], EB[a]),-predkoscBaterii); //7b
				
				
			}
			
			//cosntrain 2
			IloNumExpr cons2 = cplex.sum(G_c[a],G_sklad[a],G_s[a]);
			cons2 = cplex.diff(cons2,G_s_Z[a] );
			cons2=cplex.addGe(G[a],cons2);
			
			//constrain 5
			IloNumExpr cons5 = cplex.sum(EM_c_Z[a],EB_c[a],G_c[a]);
			cplex.addGe(C[a],cons5);
			

		} catch (IloException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return exp;

		
	}
	
	IloNumExpr createObjective(IloCplex cplex, IloNumVar[] binHandelKupuj, IloNumVar[] EB, IloNumVar[] EM_c, IloNumVar[] EB_c, IloNumVar[] G_c, IloNumVar[] EM_s, IloNumVar[] G_sklad, IloNumVar[] EB_s, IloNumVar[] G_s, 
			float predkoscBaterii, float cenaDystrybutoraZewnetrznego, float cenaBaterii, float pojemnoscBaterii, int a, 
			IloNumVar[] EM_s_Z, IloNumVar[] EB_s_Z, IloNumVar[] G_s_Z,IloNumVar[] EM_c_Z, ArrayList<Float> priceVector)
	{	
		
		IloNumExpr exp = null;
		

		try {
			//pierwszy wyraz			
 			//IloNumExpr bin1 = cplex.prod(EM_c[a],binHandelKupuj[a]);
			IloNumExpr pierwszyWyraz=cplex.sum(EM_c_Z[a],EB_c[a],G_c[a]);
			pierwszyWyraz=cplex.prod(-cenaDystrybutoraZewnetrznego, pierwszyWyraz);
			
			IloNumExpr drugiWyraz =cplex.prod((priceVector.get(a)+cenaBaterii),EM_s_Z[a]);
			IloNumExpr trzeciWyraz = cplex.prod(priceVector.get(a),EM_c_Z[a]);
			IloNumExpr czwartyWyraz =cplex.prod(cenaBaterii,G_sklad[a]);
			
			IloNumExpr piatyWyraz =cplex.prod(cplex.diff(1.0, binHandelKupuj[a]), cplex.prod(-priceVector.get(a),cplex.sum(EB_s[a],G_s[a])));
			
			exp=cplex.sum(pierwszyWyraz,drugiWyraz,trzeciWyraz,czwartyWyraz,piatyWyraz);
			
		} catch (IloException e) {
			// TODO Auto-generated catch block
			print("createObjective Exception");
			getInput();
			
			e.printStackTrace();
		}

		return exp;
	}

	/*
	public ArrayList<DayData2> wyznaczSterowanie(ArrayList<Float> priceVector, int horyzontCzasowy, Prosument2 prosument )
	{
		ArrayList<DayData2> L1 = new ArrayList<DayData2>();
		
		try {
			IloCplex cplex = new IloCplex();
			
			float predkoscBaterii =prosument.getPredkoscBaterii();
			float cenaDystrybutoraZewnetrznego =prosument.getCenaDystrybutoraZewnetrznego();
			float cenaBaterii = prosument.getKosztAmortyzacjiBaterii();
			float pojemnoscBaterii = prosument.getPojemnoscBaterii();
			
			int horyzontCzasu =priceVector.size();
			
			
			IloNumVar[] binHandelKupuj = stworzWektorZmiennychBinarnych(horyzontCzasu);
			IloNumVar[] binHandelSprzedaj = stworzWektorZmiennychBinarnych(horyzontCzasu);
			
			double reallyBigNumber =10000.0; //something goes wrong if Double.MAX is inserted
			
			IloNumVar[] EB = stworzWektorZmiennychCiaglych(horyzontCzasu,pojemnoscBaterii); 

			
			IloNumVar[] EM_c = stworzWektorZmiennychCiaglych(horyzontCzasu,reallyBigNumber); 
			IloNumVar[] EB_c = stworzWektorZmiennychCiaglych(horyzontCzasu,predkoscBaterii); 
			IloNumVar[] G_c = stworzWektorZmiennychCiaglych(horyzontCzasu,reallyBigNumber); 
			IloNumVar[] EM_s = stworzWektorZmiennychCiaglych(horyzontCzasu,predkoscBaterii); 
			IloNumVar[] G_sklad = stworzWektorZmiennychCiaglych(horyzontCzasu,predkoscBaterii); 
			IloNumVar[] EB_s = stworzWektorZmiennychCiaglych(horyzontCzasu,predkoscBaterii); 
			IloNumVar[] G_s = stworzWektorZmiennychCiaglych(horyzontCzasu,reallyBigNumber); 

			
			IloNumExpr objective = null;

			int a=0;
			while (a<horyzontCzasu)
			{
				
				//pierwszy wyraz
				IloNumExpr bin1 =cplex.prod(EM_c[a],binHandelKupuj[a]);
				IloNumExpr pierwszyWyraz=cplex.sum(bin1,EB_c[0],G_c[a]);
				pierwszyWyraz=cplex.prod(-cenaDystrybutoraZewnetrznego, pierwszyWyraz);
				
				//drugi wyraz
				IloNumExpr bin2 =cplex.prod(EM_s[a],binHandelKupuj[a]);
				IloNumExpr drugiWyraz =cplex.prod((priceVector.get(a)+cenaBaterii),bin2);
				
				//trzeci wyraz
				IloNumExpr bin3 =cplex.prod(EM_c[a],binHandelKupuj[a]);
				IloNumExpr trzeciWyraz =cplex.prod(priceVector.get(a),bin3);
				
				//czwarty wyraz
				IloNumExpr czwartyWyraz =cplex.prod(cenaBaterii,G_sklad[a]);
				
				//piaty wyraz
				IloNumExpr bin4 =cplex.prod(cplex.sum(EB_s[a],G_s[a]),binHandelSprzedaj[a]);
				IloNumExpr piatyWyraz =cplex.prod(-priceVector.get(a),bin4);
				if (objective==null)
				{
					objective=cplex.sum(pierwszyWyraz,drugiWyraz,trzeciWyraz, czwartyWyraz,piatyWyraz);
				}
				else
				{
					objective=cplex.sum(objective,pierwszyWyraz,drugiWyraz,trzeciWyraz, czwartyWyraz,piatyWyraz);
				}
				
				//Cosntrains
				if(a>0)
				{
					IloNumExpr cbin1 = cplex.prod(EB_s[a-1],binHandelSprzedaj[a-1]);
					IloNumExpr cbin2 = cplex.prod(EM_s[a-1],binHandelKupuj[a-1]);
					//IloNumExpr cons1=cplex.diff(cplex.diff(EB[a-1],EB_c[a-1]),binHandelSprzedaj[a-1]);
					//IloNumExpr cons1=cplex.diff(cplex.diff(EB[a-1],EB_c[a-1]),cbin1);
					IloNumExpr cons1 =binHandelSprzedaj[a-1];

					//getInput();
					
					//cplex.addEq(EB[a],cplex.sum(cplex.diff(cplex.diff(EB[a-1],EB_c[a]),cbin1),cbin2,G_sklad[a]));
					cplex.addEq(EB[a],cbin1);

				}
				
				a++;
			}
			
			//Cplex wrap it up
			
			cplex.addMinimize(objective);
			
			
			print(objective.toString());
			
			

			print("just before solving");
			getInput();
			
			//cplex.setParam(IloCplex.BooleanParam.PreInd, false);			
			//cplex.setParam("PreInd", false);
			
			//Solve
			
			cplex.setOut(null);
			if (cplex.solve())
			{
				System.out.println("Solved ok");
				double[] EM_cSolved =  cplex.getValues(EM_c);
				

				print("cplex solved");
				getInput();
			}
			else
			{
				print("CPLEX FAILED!");
				getInput();
			}
			
			
			

			//double[] lbKupuj = createDoubleArray(0.0,1);
			//double[] ubKupuj = createDoubleArray(1.0,1);
			//IloNumVarType[] xtKupuj =createIloNumVarTypeArray(IloNumVarType.Int,1);
			
			
			//IloNumExpr pierwszyNawias=cplex.scalProd((double)priceVector.get(0),1.0);

			
			print("wyznaczSterowanie");
			print(((Integer)horyzontCzasowy).toString());
			print(Arrays.toString(priceVector.toArray()));
			getInput();
			
			
		} catch (IloException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		

		return L1;
	}*/
	
	void print(int a)
	{
		print(((Integer)a).toString());
	}
	
	void print(String s)
	{
		System.out.println("Optimizer "+s);
	}
	
	void getInput(String s)
	{
		print("getInput "+s);
		sc.nextLine();
	}
	
	void getInput()
	{
		getInput("");
	}

}
