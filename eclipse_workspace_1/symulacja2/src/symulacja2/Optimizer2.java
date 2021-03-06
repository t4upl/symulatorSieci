package symulacja2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import ilog.concert.IloException;
import ilog.concert.IloNumExpr;
import ilog.concert.IloNumVar;
import ilog.concert.IloNumVarType;
import ilog.cplex.IloCplex;
import ilog.cplex.IloCplex.UnknownObjectException;

public class Optimizer2 {

	
	Scanner sc = new Scanner(System.in);
	
	Prosument2 prosument;
	
	//Stale
	private float predkoscBaterii;
	private float cenaDystrybutoraZewnetrznego;
	private float cenaBaterii;
	private float pojemnoscBaterii;
	private float stanPoczatkowyBaterii;
	private int horyzontCzasu;
	private ArrayList<Float> priceVector;
	
	//variables	
	IloNumVar[] binHandelKupuj;

	IloNumVar[] EB; 
	IloNumVar[] EM_c; 
	IloNumVar[] EB_c; 
	IloNumVar[] G_c; 
	IloNumVar[] EM_s; 
	IloNumVar[] G_sklad; 
	IloNumVar[] EB_s; 
	IloNumVar[] G_s; 
	
	IloNumVar[] EM_s_Z; 
	IloNumVar[] EB_s_Z; 
	IloNumVar[] G_s_Z; 
	IloNumVar[] EM_c_Z;
	
	float[] C;
	float[] G;
	
	
	//Solved variables
	double[] EB_solved; 
	double[] EM_c_solved; 
	double[] EB_c_solved; 
	double[] G_c_solved; 
	double[] EM_s_solved; 
	double[] G_sklad_solved; 
	double[] EB_s_solved; 
	double[] G_s_solved; 
	double[] binHandelKupuj_solved;
	
	double[] EM_s_Z_solved; 
	double[] EB_s_Z_solved; 
	double[] G_s_Z_solved; 
	double[] EM_c_Z_solved;
	
	
	
	double reallySmallNumber = 0.0001;
	double reallyBigNumber =10000.0; //used while creating cariables
	
	//do wyrowniania bledow numerycznych 
	double reallySmallNumberConstrain =0.0001; //u

	
	//Singleton shit
	private static Optimizer2 instance = null;
	private Optimizer2() 
	{
	}
	
   public static Optimizer2 getInstance() {
	      if(instance == null) {
	         instance = new Optimizer2();
	      }
	      return instance;
	}
   
   void setVariables(ArrayList<Float> priceVector, IloCplex cplex)
   {
	   
	   
		this.predkoscBaterii =prosument.getPredkoscBaterii();
		this.cenaDystrybutoraZewnetrznego =prosument.getCenaDystrybutoraZewnetrznego();
		this.cenaBaterii = prosument.getKosztAmortyzacjiBaterii();
		this.pojemnoscBaterii = prosument.getPojemnoscBaterii();
		this.priceVector =priceVector;
		
		ArrayList <DayData2> L2 = prosument.getDayDataList();
		this.stanPoczatkowyBaterii =L2.get(prosument.getTimeIndex()).getStanBateriiNaPoczatkuSlotu();
		this.horyzontCzasu =priceVector.size();
		
		binHandelKupuj = stworzWektorZmiennychBinarnych(horyzontCzasu, cplex);


		EB = stworzWektorZmiennychCiaglych(horyzontCzasu,pojemnoscBaterii, cplex); 
		EM_c = stworzWektorZmiennychCiaglych(horyzontCzasu,reallyBigNumber, cplex); 
		EB_c = stworzWektorZmiennychCiaglych(horyzontCzasu,predkoscBaterii, cplex); 
		G_c = stworzWektorZmiennychCiaglych(horyzontCzasu,reallyBigNumber, cplex); 
		EM_s = stworzWektorZmiennychCiaglych(horyzontCzasu,predkoscBaterii, cplex); 
		G_sklad = stworzWektorZmiennychCiaglych(horyzontCzasu,predkoscBaterii, cplex); 
		EB_s = stworzWektorZmiennychCiaglych(horyzontCzasu,predkoscBaterii, cplex); 
		G_s = stworzWektorZmiennychCiaglych(horyzontCzasu,reallyBigNumber, cplex); 
		
		//zmienne dodatkowe do poradzenia sobie z kwadratowa natura constrainow
		EM_s_Z = stworzWektorZmiennychCiaglych(horyzontCzasu,predkoscBaterii, cplex); 
		EB_s_Z = stworzWektorZmiennychCiaglych(horyzontCzasu,predkoscBaterii, cplex); 
		G_s_Z = stworzWektorZmiennychCiaglych(horyzontCzasu,reallyBigNumber, cplex); 
		EM_c_Z = stworzWektorZmiennychCiaglych(horyzontCzasu,reallyBigNumber, cplex);
		
		
		
		ArrayList<DayData2> dayDataList24 = prosument.getDayDataListInT(horyzontCzasu);
		
		C = getConsumptionArray(dayDataList24);
		G = getGenerationArray(dayDataList24); 
		

   }
   
   void zapakujDoData2List(ArrayList<DayData2> L1)
	{
		int a=0;
		while (a<EB_solved.length)
		{
			DayData2 d= new DayData2();
			
			d.setStanBateriiNaPoczatkuSlotu((float)EB_solved[a]);
			
			if (a+1<EB_solved.length)
			{
				d.setStanBateriiNaKoniecSlotu((float)EB_solved[a+1]);
			}

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
   
   void cleanUp()
   {
		IloNumVar[] binHandelKupuj=null;

		IloNumVar[] EB=null; 
		IloNumVar[] EM_c=null; 
		IloNumVar[] EB_c=null; 
		IloNumVar[] G_c=null; 
		IloNumVar[] EM_s=null; 
		IloNumVar[] G_sklad=null; 
		IloNumVar[] EB_s=null; 
		IloNumVar[] G_s=null; 
		
		IloNumVar[] EM_s_Z=null; 
		IloNumVar[] EB_s_Z=null; 
		IloNumVar[] G_s_Z=null; 
		IloNumVar[] EM_c_Z=null;
   }
   
   void dodajConstrainWynikowHandlu(IloCplex cplex, DayData2 constrianMarker)
   {
	   	try
	   	{
	   		float kupuj =  constrianMarker.getKupuj();
			
			IloNumExpr handelConstrain=null;
			
			//0.5 bo bledy w reprezentacji floata
			if (kupuj>0.5f) 
			{
				//generacja zawiera wyniki handlu (energia nabyta)
				handelConstrain=cplex.addGe(constrianMarker.getGeneration(), cplex.sum(EM_c[0],EM_s[0]));
				cplex.addEq(binHandelKupuj[0],1 );
			}
			else
			{
				//konsumpcja zawiera wyniki handlu (energia sprzedana)
				handelConstrain=cplex.addGe(Math.abs(constrianMarker.getConsumption()), cplex.sum(EB_s[0],G_s[0]));
				cplex.addEq(binHandelKupuj[0],0 );
			}			
	   	}
	   	catch (Exception e)
	   	{
	   		getInput("Exception in dodajConstrainWynikowHandlu");
	   	}
		
   }
   
   //TODO
   public ArrayList<DayData2> wyznaczSterowanie(ArrayList<Float> priceVector, Prosument2 prosument)
   {
	   return wyznaczSterowanie(priceVector, prosument, null);
   }
   
   public ArrayList<DayData2> wyznaczSterowanie(ArrayList<Float> priceVector, Prosument2 prosument, DayData2 constrianMarker)
   {
		ArrayList<DayData2> outputList = new ArrayList<DayData2>();
		
		try {
			IloCplex cplex = new IloCplex();
			
			//this must be set outside of setVariables!
			this.prosument = prosument;
			
			setVariables(priceVector, cplex);


			IloNumExpr objective = null;
			int a=0;
			while (a<horyzontCzasu)
			{
				
				IloNumExpr objectiveAdd =createObjective(cplex,a);
				
				//createObjective();
				if (objective==null)
				{
					objective=objectiveAdd;
				}
				else
				{
					objective=cplex.sum(objective,objectiveAdd);
				}
				
				createConstrain(cplex,a);
				a++;
			}
			
			
			cplex.addEq(stanPoczatkowyBaterii, EB[0]);
			
			//adds constrians for final decision
			if (constrianMarker!=null)
			{
				dodajConstrainWynikowHandlu(cplex,constrianMarker );
			}
			
			cplex.addMinimize(objective);
			
			cplex.setOut(null);
			if (cplex.solve())
			{
				fillOutSolvedVariables(cplex);	
				constrainCheck();
				zapakujDoData2List(outputList);
				//cleanUp();
				
				
				cplex.end();


			}
			else
			{
				getInput("CPLEX FAILED!");
			}
			
		} catch (IloException e) {
			e.printStackTrace();
		}

		
		return outputList;
	}
   
   void constrainCheck()
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
				if (EB_solved[a]+reallySmallNumberConstrain<EB_c_solved[a]+(1-binHandelKupuj_solved[a])*EB_s_solved[a])
				{
					print(prosument.getID());
					
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
			
			//sumaryczna predkosc baterii
			if (predkoscBaterii+reallySmallNumberConstrain<EB_c_solved[a]+G_sklad_solved[a]+(1-binHandelKupuj_solved[a])*EB_s_solved[a]+binHandelKupuj_solved[a]*EM_s_solved[a])
			{
				print("a: "+a);
				print("EB_c "+EB_c_solved[a]+"");
				print("kupuj: "+binHandelKupuj_solved[a]);
				
				print("G_sklad: "+G_sklad_solved[a]);
				print("EB_s: "+EB_s_solved[a]);
				print("EM_s: "+EM_s_solved[a]);
	
				
				float sum =(float)-1f;
				print("sum "+sum);
				getInput("cons6 - sumaryczna predkosc baterii violated");
			}
			
			
			
			
			a++;
		}
	}
   
   void fillOutSolvedVariables(IloCplex cplex)
   {
		try {
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
		} catch (UnknownObjectException e) {
			e.printStackTrace();
		} catch (IloException e) {
			e.printStackTrace();
		} 

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

			//IloNumVar[] output= new	IloNumVar[liczbaElementow];
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
	
	IloNumVar[] stworzWektorZmiennychCiaglych(int liczbaElementow, double upperBoundValue, IloCplex cplex)
	{
		IloNumVar[] output=null;
		try {

			//IloNumVar[] output= new	IloNumVar[liczbaElementow];
			double[] lb =createDoubleArray(0.0,liczbaElementow);
			double[] ub =createDoubleArray(upperBoundValue,liczbaElementow);
			IloNumVarType[] type =createIloNumVarTypeArray(IloNumVarType.Float,liczbaElementow);

			output  = cplex.numVarArray(liczbaElementow, lb, ub, type);
			
		} catch (IloException e) {
			getInput("ERROR stworzWektorZmiennychBinarnych");
			e.printStackTrace();
		}		
		
		return output;
	}
	
	IloNumExpr createObjective(IloCplex cplex, int index)
	{	
		
		IloNumExpr exp = null;
		int a=index;

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
			print("createObjective Exception");
			getInput();
			
			e.printStackTrace();
		}

		return exp;
	}
	
	IloNumExpr createConstrain(IloCplex cplex, int a)
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
			
			//ograniczenie na sumaryczne ladowanie baterii
			IloNumExpr cons6 = cplex.sum(EB_c[a],G_sklad[a],EB_s[a],EM_s_Z[a]);
			cplex.addGe(predkoscBaterii,cplex.diff(cons6,EB_s_Z[a] ));
			
			

		} catch (IloException e) {
			e.printStackTrace();
		}
		
		return exp;

		
	}
	
	//---------------------------------
	//DEBUG
	void print(int a)
	{
		print(((Integer)a).toString());
	}
	
	void print(String s)
	{
		System.out.println("Optimizer2 "+s);
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
