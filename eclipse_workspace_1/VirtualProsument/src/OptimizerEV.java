import java.util.ArrayList;

import ilog.concert.IloException;
import ilog.concert.IloNumExpr;
import ilog.concert.IloNumVar;
import ilog.concert.IloNumVarType;
import ilog.cplex.IloCplex;

public class OptimizerEV extends CoreClass {

	ProsumentEV prosumentEV;

	//Stale
	private float predkoscBaterii;
	private float cenaDystrybutoraZewnetrznego =Stale.cenaDystrybutoraZewnetrznego;
	private float cenaBaterii = Stale.kosztAmortyzacjiBaterii;
	private float pojemnoscBaterii;
	private int horyzontCzasu = Stale.horyzontCzasowy;
	
	private double reallyBigNumber =Stale.reallyBigNumber;
	private int liczbaSamochodow;
	
	//stany poczatkwoe magazynow energii
	private float stanPoczatkowyBaterii;
	private ArrayList<Float> stanPoczatkowyEVdom;
	private ArrayList<Float> stanPoczatkowyEVpraca;
	ArrayList<ArrayList<Integer>> statusEV;
	
	float[] C;
	float[] G;
	
	//koszt w chwili t
	IloNumVar[] koszt; 
	
	//pierwsyz skaldnik kosztu - niezrownowazona konsumpcja
	IloNumVar[] koszt_Zew;
	
	//drugi skaldnik kosztu - amortyzacja baterii
	IloNumVar[] koszt_sklad;
	
	//trzeci skaldnik kosztu - amortyzacja akumulatorow EV
	IloNumVar[] koszt_EV;
	



	
	IloNumVar[] EB; 
	
	IloNumVar[] EB_c; 
	IloNumVar[] G_c; 
	IloNumVar[] EM_s; 
	IloNumVar[] G_sklad; 
	 
	//lista zaiwera pod i-tym indeksem dla i-tego samochodu 
	//tablice rozmiaru hoeyzotn czasowy okreslajaca zachowanie 
	//kiedys byl pdozial na dom i prace potem zostalo to zastapione ejdna bateria stad "dom"
	//na koncu nazw zmiennych
	ArrayList<IloNumVar[]> EVdom;
	ArrayList<IloNumVar[]> EVdom_c; 
	ArrayList<IloNumVar[]> EVdom_EB;
	ArrayList<IloNumVar[]> EB_EVdom;
	ArrayList<IloNumVar[]> G_EVdom;
	ArrayList<IloNumVar[]> Zew_EVdom;
	
	
	//Singleton shit
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
	
	
	ArrayList<IloNumVar[]> stworzListeWektorZmiennychCiaglych(IloCplex cplex,double upperBoundValue)
	{
		ArrayList<IloNumVar[]> output = new ArrayList<IloNumVar[]>();
		int i=0;
		while (i<liczbaSamochodow)
		{
			output.add(stworzWektorZmiennychCiaglych(Stale.horyzontCzasowy, upperBoundValue, cplex));
			
			i++;
		}
		
		return output;
	}
	
	//TODO
	void setVariables(IloCplex cplex, ProsumentEV prosumentEV, Form24 form24)
	{
		this.predkoscBaterii =prosumentEV.getPredkoscBaterii();
		this.pojemnoscBaterii = prosumentEV.getPojemnoscBaterii();
		this.liczbaSamochodow = form24.geteVDataList().size();
		//print ("liczbaSamochodow "+liczbaSamochodow);
		
		koszt = stworzWektorZmiennychCiaglych(Stale.horyzontCzasowy,reallyBigNumber, cplex);
		koszt_Zew = stworzWektorZmiennychCiaglych(Stale.horyzontCzasowy,reallyBigNumber, cplex);
		koszt_sklad = stworzWektorZmiennychCiaglych(Stale.horyzontCzasowy,reallyBigNumber, cplex);
		koszt_EV = stworzWektorZmiennychCiaglych(Stale.horyzontCzasowy,reallyBigNumber, cplex);

		
		EB = stworzWektorZmiennychCiaglych(Stale.horyzontCzasowy,pojemnoscBaterii, cplex);

		EB_c = stworzWektorZmiennychCiaglych(horyzontCzasu,predkoscBaterii, cplex); 
		G_c = stworzWektorZmiennychCiaglych(horyzontCzasu,reallyBigNumber, cplex); 
		EM_s = stworzWektorZmiennychCiaglych(horyzontCzasu,predkoscBaterii, cplex); 
		G_sklad = stworzWektorZmiennychCiaglych(horyzontCzasu,predkoscBaterii, cplex); 
 
		//
		stanPoczatkowyBaterii = form24.getStanPoczatkowyBaterii();
		stanPoczatkowyEVdom=form24.getStanPoczatkowyEVdomList();
		statusEV=form24.getStatusEV();
		//stanPoczatkowyEVpraca=form24.getStanPoczatkowyEVpracaList();
		
		//ArrayList<DayData> dayDataList24 = prosumentEV.getDayDataListInT();
		
		C = form24.getKonsumpcjaArray();
		G = form24.getGeneracjaArray();	
		
		//

		EVdom=stworzListeWektorZmiennychCiaglych(cplex,Stale.pojemnoscAkumualtoraEV);
		EVdom_c=stworzListeWektorZmiennychCiaglych(cplex,Stale.predkoscAkumulatoraEV);
		
		//predkosc baterii, a nie Stale.predkoscBaterii bo dla wirtualnego rposumenta ona jest rozna niz domyslna
		EVdom_EB=stworzListeWektorZmiennychCiaglych(cplex,Math.min(Stale.predkoscAkumulatoraEV,predkoscBaterii));
		EB_EVdom=stworzListeWektorZmiennychCiaglych(cplex,Math.min(Stale.predkoscAkumulatoraEV,predkoscBaterii));
		G_EVdom=stworzListeWektorZmiennychCiaglych(cplex,Stale.predkoscAkumulatoraEV);
		Zew_EVdom=stworzListeWektorZmiennychCiaglych(cplex,Stale.predkoscAkumulatoraEV);
		
		//print("Set Variables -almost end");
		//getInput("Zew_EVdom "+Zew_EVdom.size());
		
	
		/*
		EVpraca=stworzListeWektorZmiennychCiaglych(cplex,Stale.pojemnoscAkumualtoraEV);
		EVpraca_c=stworzListeWektorZmiennychCiaglych(cplex,Stale.predkoscAkumulatoraEV);
		EVpraca_EB=stworzListeWektorZmiennychCiaglych(cplex,Math.min(Stale.predkoscAkumulatoraEV,Stale.predkoscBaterii));
		EB_EVpraca=stworzListeWektorZmiennychCiaglych(cplex,Math.min(Stale.predkoscAkumulatoraEV,Stale.predkoscBaterii));
		G_EVpraca=stworzListeWektorZmiennychCiaglych(cplex,Stale.predkoscAkumulatoraEV);
		Zew_EVpraca=stworzListeWektorZmiennychCiaglych(cplex,Stale.predkoscAkumulatoraEV);*/


	}
	

	

	private void createObjectiveKosztZew(IloCplex cplex, int t) throws IloException
	{
		IloNumExpr expr1 =null;
		
		//print ("createObjectiveKosztZew -start");
		//print("Zew_EVdom.size()"+Zew_EVdom.size());
		
		//przejscie po samochodach
		int i=0;
		while (i<Zew_EVdom.size())
		{
			//print("createObjectiveKosztZew "+i);
			IloNumVar[] Zew_EVdom24 = Zew_EVdom.get(i);
			IloNumVar[] EV_c24 = EVdom_c.get(i);
			
			IloNumExpr exprToBeAdded =cplex.diff(Zew_EVdom24[t],EV_c24[t]) ;
			
			if (i==0)
			{
				expr1=exprToBeAdded;
			}
			else
			{
				expr1=cplex.sum(expr1,exprToBeAdded);
			}
			
			i++;
		}
		
		IloNumExpr prawaStrona = cplex.diff(expr1,cplex.sum(EB_c[t],G_c[t]));
		
		cplex.addEq(koszt_Zew[t],prawaStrona );
	}
	
	private void createObjectiveKosztSklad(IloCplex cplex, int t) throws IloException
	{
		IloNumExpr expr1 =null;
		
		//przejscie po samochodach
		int i=0;
		while (i<EVdom_EB.size())
		{
			
			IloNumVar[] EVdom_EB24 = EVdom_EB.get(i);
			
			IloNumExpr exprToBeAdded =EVdom_EB24[t] ;
			
			if (i==0)
			{
				expr1=exprToBeAdded;
			}
			else
			{
				expr1=cplex.sum(expr1,exprToBeAdded);
			}
			
			i++;
		}
		
		IloNumExpr prawaStrona = cplex.sum(G_sklad[t],expr1);
		cplex.addEq(koszt_sklad[t],prawaStrona );
	}

	
	private void createObjectiveKosztEV(IloCplex cplex, int t) throws IloException
	{
		IloNumExpr expr1 =null;
		
		//przejscie po samochodach
		int i=0;
		while (i<Zew_EVdom.size())
		{
			
			IloNumVar[] Zew_EVdom24 = Zew_EVdom.get(i);
			IloNumVar[] G_EVdom24 = G_EVdom.get(i);
			IloNumVar[] EB_EVdom24 = EB_EVdom.get(i);
			
			IloNumExpr exprToBeAdded =cplex.sum(Zew_EVdom24[t],G_EVdom24[t],EB_EVdom24[t]) ;
			
			if (i==0)
			{
				expr1=exprToBeAdded;
			}
			else
			{
				expr1=cplex.sum(expr1,exprToBeAdded);
			}
			
			i++;
		}
		
		IloNumExpr prawaStrona = expr1;
		cplex.addEq(koszt_EV[t],prawaStrona );
	}	
	
	
	IloNumExpr createObjective(IloCplex cplex)
	{
		if (stanPoczatkowyEVdom==null)
		{
			getInput("ERROR in createObjective,  null refference, check if setVariables was performed beor createObjective");
		}
		
		IloNumExpr objective = null;
			
		try {
			int i =0;
			while (i<Stale.horyzontCzasowy)
			{
				//print("createObjective "+i);
				// 1 i 4
				createObjectiveKosztZew(cplex, i);
				//getInput("createObjective - amnaged to create objective1");
				
				//2
				createObjectiveKosztSklad(cplex, i);
				
				//3
				createObjectiveKosztEV(cplex, i);
				
				//cplex.addEq(koszt[i], koszt_1[i]);
				//print ("part II");
				cplex.addEq(koszt[i], cplex.sum(koszt_Zew[i],koszt_sklad[i],koszt_EV[i]  ));
				if (i==0)
				{
					objective =koszt[i];
				}
				else
				{
					cplex.sum(objective,koszt[i]);
				}
				
				i++;
			}
		} catch (IloException e) {
			e.printStackTrace();
		}
	
		return objective;
	}
	
	void dodajOgraniczeniaPoczatkowyStanBaterii(IloCplex cplex)
	{
		try{
			cplex.eq(EB[0], stanPoczatkowyBaterii);
			int i=0;
			while (i<stanPoczatkowyEVdom.size())
			{
				//print("dodajOgraniczeniaPoczatkowyStanBaterii "+i);
				
				IloNumVar[] EVdom24 = EVdom.get(i);
				//IloNumVar[] EVpraca24 = EVpraca.get(i);
				
				cplex.addEq(stanPoczatkowyEVdom.get(i), EVdom24[0]);
				//cplex.eq(stanPoczatkowyEVpraca.get(i),EVpraca24[0]);
				i++;
			}
		}catch (Exception e)
		{
			print("dodajOgraniczeniaPoczatkowyStanBaterii Exception");
		}
		
	}
	
	void dodajOgraniczeniaEV(IloCplex cplex)
	{
		int i=0;
		while (i<liczbaSamochodow)
		{
			dodajOgraniczeniaEVDlaPojedynczegoSamochodu(cplex,i);			
			i++;
		}
	}
	
	void dodajOgraniczeniaEVDlaPojedynczegoSamochodu(IloCplex cplex, int indeksSamochodu)
	{
		//print ("\n"+indeksSamochodu+"\n");
		
		try{
			int i=0;
			while(i<Stale.horyzontCzasowy)
			{
				IloNumVar[] EV24 = EVdom.get(indeksSamochodu);
				IloNumVar[] EV_c24 = EVdom_c.get(indeksSamochodu);			
				IloNumVar[] EV_EB24 = EVdom_EB.get(indeksSamochodu);
				
				cplex.addGe(EV24[i], cplex.sum(EV_c24[i],EV_EB24[i]));
				
				if (i<Stale.horyzontCzasowy-1)
				{
					cplex.addGe(Stale.predkoscAkumulatoraEV,cplex.diff(EV24[i+1],EV24[i]));
					cplex.addLe(-Stale.predkoscAkumulatoraEV,cplex.diff(EV24[i+1],EV24[i]));
				}
		
				i++;
			}
			
			dodajOgraniczeniaEVDlaPojedynczegoSamochoduOgraniczeniaStatusu(cplex,indeksSamochodu);

			
		} catch (Exception e)
		{
			print("dodajOgraniczeniaEVDlaPojedynczegoSamochodu");
		}
	}
	
	void dodajOgraniczeniaEVDlaPojedynczegoSamochoduOgraniczeniaStatusu(IloCplex cplex, int indeksSamochodu)
	{
		//print("dodajOgraniczeniaEVDlaPojedynczegoSamochoduOgraniczeniaStatusu "+);
		
		ArrayList<Integer> L1 =statusEV.get(indeksSamochodu);
		int i=0;
		while(i<L1.size())
		{
			
			int status = L1.get(i);
			//print(status);
			
			switch (status){
			case 0: ograniczeniaStatusuDom(cplex, indeksSamochodu,i); break;
			case 1: ograniczeniaStatusuDomPraca(cplex, indeksSamochodu,i); break;
			case 2:	ograniczeniaStatusuPraca(cplex, indeksSamochodu,i); break;
			case 3:	ograniczeniaStatusuPracaDom(cplex, indeksSamochodu,i); break;
			default: getInput("ERROR dodajOgraniczeniaEVDlaPojedynczegoSamochoduOgraniczeniaStatusu no status "+status);
			}
			i++;
		}
	}
	
	void ograniczeniaStatusuDom(IloCplex cplex, int indeksSamochodu,int t)
	{
		try{
			IloNumVar[] EB_EV24 = EB_EVdom.get(indeksSamochodu);
			IloNumVar[] G_EV24 = G_EVdom.get(indeksSamochodu);
			IloNumVar[] EV_EB24 = EVdom_EB.get(indeksSamochodu);
			IloNumVar[] EV_c24 = EVdom_c.get(indeksSamochodu);

			IloNumVar[] EV24 = EVdom.get(indeksSamochodu);
			IloNumVar[] Zew_EV24 = Zew_EVdom.get(indeksSamochodu);

			
			
			cplex.addGe(Stale.predkoscAkumulatoraEV, cplex.sum(EB_EV24[t],G_EV24[t],EV_EB24[t],EV_c24[t],Zew_EV24[t]));
		
			if ((t+1)<Stale.horyzontCzasowy)
			{
				IloNumExpr sum1 = cplex.sum(EV24[t],EB_EV24[t],G_EV24[t], Zew_EV24[t] );
				IloNumExpr dif1 = cplex.sum(EV_EB24[t],EV_c24[t]);
				IloNumExpr prawaStrona = cplex.diff(sum1, dif1);
				cplex.addEq(EV24[t+1], prawaStrona);
			}
		} catch (Exception e)
		{
			
		}
	}
	
	void ograniczeniaStatusuDomPraca(IloCplex cplex, int indeksSamochodu,int t)
	{
		try{
			IloNumVar[] EB_EV24 = EB_EVdom.get(indeksSamochodu);
			IloNumVar[] G_EV24 = G_EVdom.get(indeksSamochodu);
			IloNumVar[] EV_EB24 = EVdom_EB.get(indeksSamochodu);
			IloNumVar[] EV_c24 = EVdom_c.get(indeksSamochodu);

			IloNumVar[] EV24 = EVdom.get(indeksSamochodu);
			IloNumVar[] Zew_EV24 = Zew_EVdom.get(indeksSamochodu);
			
			cplex.addGe(EV24[t], Stale.podrozMinimumEnergii);
			cplex.addGe(Stale.podrozPredkoscAkumulatoraEV, cplex.sum(EB_EV24[t],G_EV24[t],EV_EB24[t],EV_c24[t],Zew_EV24[t]));

			
			if ((t+1)<Stale.horyzontCzasowy)
			{	
				IloNumExpr sum1 = cplex.sum(EV24[t],EB_EV24[t],G_EV24[t], Zew_EV24[t] );
				IloNumExpr dif1 = cplex.sum(cplex.sum(EV_EB24[t],EV_c24[t]),Stale.podrozMinimumEnergii);
				IloNumExpr prawaStrona = cplex.diff(sum1, dif1);
				cplex.addEq(EV24[t+1], prawaStrona);
			}
			
		} catch (Exception e)
		{
			print("");
		}
		
	}
	
	void ograniczeniaStatusuPraca(IloCplex cplex, int indeksSamochodu,int t)
	{
		try{
			IloNumVar[] EB_EV24 = EB_EVdom.get(indeksSamochodu);
			IloNumVar[] G_EV24 = G_EVdom.get(indeksSamochodu);
			IloNumVar[] EV_EB24 = EVdom_EB.get(indeksSamochodu);
			IloNumVar[] EV_c24 = EVdom_c.get(indeksSamochodu);

			IloNumVar[] EV24 = EVdom.get(indeksSamochodu);
			IloNumVar[] Zew_EV24 = Zew_EVdom.get(indeksSamochodu);

			cplex.addGe(Stale.predkoscAkumulatoraEV, cplex.sum(EB_EV24[t],G_EV24[t],EV_EB24[t],EV_c24[t],Zew_EV24[t]));
			
			if ((t+1)<Stale.horyzontCzasowy)
			{
				IloNumExpr sum1 = cplex.sum(EV24[t],EB_EV24[t],G_EV24[t], Zew_EV24[t] );
				IloNumExpr dif1 = cplex.sum(EV_EB24[t],EV_c24[t]);
				IloNumExpr prawaStrona = cplex.diff(sum1, dif1);
				cplex.addEq(EV24[t+1], prawaStrona);
			}			
			
			if (!Stale.handelWPracy)
			{
				cplex.addEq(EB_EV24[t],0);
				cplex.addEq(G_EV24[t],0);
				cplex.addEq(EV_EB24[t],0);
				cplex.addEq(EV_c24[t],0);
			}
			
		} catch (Exception e)
		{
			print("");
		}
		
	}
	
	void ograniczeniaStatusuPracaDom(IloCplex cplex, int indeksSamochodu,int t)
	{
		try{
			
			IloNumVar[] EB_EV24 = EB_EVdom.get(indeksSamochodu);
			IloNumVar[] G_EV24 = G_EVdom.get(indeksSamochodu);
			IloNumVar[] EV_EB24 = EVdom_EB.get(indeksSamochodu);
			IloNumVar[] EV_c24 = EVdom_c.get(indeksSamochodu);

			IloNumVar[] EV24 = EVdom.get(indeksSamochodu);
			IloNumVar[] Zew_EV24 = Zew_EVdom.get(indeksSamochodu);
			
			cplex.addGe(EV24[t], Stale.podrozMinimumEnergii);
			cplex.addGe(Stale.podrozPredkoscAkumulatoraEV, cplex.sum(EB_EV24[t],G_EV24[t],EV_EB24[t],EV_c24[t],Zew_EV24[t]));			
			
			if ((t+1)<Stale.horyzontCzasowy)
			{
				IloNumExpr sum1 = cplex.sum(EV24[t],EB_EV24[t],G_EV24[t], Zew_EV24[t] );
				IloNumExpr dif1 = cplex.sum(cplex.sum(EV_EB24[t],EV_c24[t]),Stale.podrozMinimumEnergii);
				IloNumExpr prawaStrona = cplex.diff(sum1, dif1);
				cplex.addEq(EV24[t+1], prawaStrona);
			}
			
			if (!Stale.handelWPracy)
			{
				cplex.addEq(EB_EV24[t],0);
				cplex.addEq(G_EV24[t],0);
				cplex.addEq(EV_EB24[t],0);
				cplex.addEq(EV_c24[t],0);
			}
			
		} catch (Exception e)
		{
			print("");
		}
		
	}

	//TODO
	void fillOutSolvedVariables(IloCplex cplex, Sterowanie sterowanie)
   	{
		
		print ("fillOutSolvedVariables -start");
		try {
			
		
			double[] EB_solved = cplex.getValues(EB);
			double[] G_c_solved = cplex.getValues(G_c);
			double[] G_sklad_solved = cplex.getValues(G_sklad);
			double[] EB_c_solved = cplex.getValues(EB_c);


			/*
			 
			//lista zaiwera pod i-tym indeksem dla i-tego samochodu 
			//tablice rozmiaru hoeyzotn czasowy okreslajaca zachowanie 
			ArrayList<IloNumVar[]> EVdom;
			ArrayList<IloNumVar[]> EVdom_c; 
			ArrayList<IloNumVar[]> EVdom_EB;
			ArrayList<IloNumVar[]> EB_EVdom;
			ArrayList<IloNumVar[]> G_EVdom;
			ArrayList<IloNumVar[]> Zew_EVdom;		
			*/
			
		   ArrayList<DayData> dList = new ArrayList<>();  
		   int i=0;
		   while (i<Stale.horyzontCzasowy)
		   {
			   DayData d = new DayData();
			   d.setStanBateriiNaPoczatkuSlotu((float)EB_solved[i]);
			   d.setZGeneracjiNaKonsumpcje((float)G_c_solved[i]);
			   d.setZGeneracjiDoBaterii((float) G_sklad_solved[i]);
			   d.setZBateriiNaKonsumpcje((float)EB_c_solved[i]);
		
			   dList.add(d);
			   i++;
		   }
		   
		   i=0;
		   while (i<liczbaSamochodow)
		   {
			   
			   int j=0;
			   while (j<Stale.horyzontCzasowy)
			   {
				   j++;
			   }
			   
			   i++;
		   }
		}catch (Exception e)
		{
			print("ERROR in fillOutSolvedVariables");
		}
   	}
	
	//TODO
	//obie listy ograniczone do 24 elementow
	public Sterowanie wyznaczSterowanie(Form24 form24, ProsumentEV prosumentEV)
	{
		Sterowanie sterowanie = new Sterowanie();
		try {
			IloCplex cplex = new IloCplex();
			setVariables(cplex, prosumentEV, form24);

						
			IloNumExpr objective = createObjective(cplex);
			
			//getInput("Managed to create objective");
			cplex.addMinimize(objective);
			
			dodajOgraniczeniaPoczatkowyStanBaterii(cplex);
			dodajOgraniczeniaModelu(cplex);
			dodajOgraniczeniaEV(cplex);
			
			
			cplex.setOut(null);
			//cplex.solve();
			
			if (cplex.solve())
			{
				fillOutSolvedVariables(cplex,sterowanie);
				cplex.end();
				getInput("CPLEX success");
			}
			else
			{
				getInput("CPLEX FAILED!");
			}
			
		} catch (IloException e) {
			print("wyznaczSterowanie -fail");
			e.printStackTrace();
		}

		return sterowanie;
	}
	
	private void dodajOgraniczSkladowychGeneracji(IloCplex cplex, int t) throws IloException
	{
		IloNumExpr expr1 =null;
		int i=0;
		while (i<G_EVdom.size())
		{
			IloNumVar[] G_EVdom24  = G_EVdom.get(i);
			//IloNumVar[] G_EVpraca24  = G_EVpraca.get(i);
			
			IloNumExpr exprToBeAdded = G_EVdom24[t];
			//IloNumExpr exprToBeAdded = cplex.sum(G_EVdom24[t],G_EVpraca24[t] );

			
			if (i==0)
			{
				expr1=exprToBeAdded;
			}
			else
			{
				expr1=cplex.sum(expr1,exprToBeAdded);
			}
			
			i++;
		}
		
		IloNumExpr prawaStrona = cplex.sum(G_c[t],G_sklad[t] ,expr1);
		
		cplex.addGe(G[t],prawaStrona );
	}
	
	private void dodajOgraniczSkladowychBaterii(IloCplex cplex, int t) throws IloException
	{
		IloNumExpr expr1 =null;
		int i=0;
		while (i<EB_EVdom.size())
		{
			//print("dodajOgraniczSkladowychBaterii "+i);
			IloNumVar[] EB_EVdom24  = EB_EVdom.get(i);
			//IloNumVar[] EB_EVpraca24  = EB_EVpraca.get(i);
			
			IloNumExpr exprToBeAdded = EB_EVdom24[t];
			//IloNumExpr exprToBeAdded = cplex.sum(EB_EVdom24[t],EB_EVpraca24[t] );

			
			if (i==0)
			{
				expr1=exprToBeAdded;
			}
			else
			{
				expr1=cplex.sum(expr1,exprToBeAdded);
			}
			
			i++;
		}
		
		IloNumExpr prawaStrona = cplex.sum(EB_c[t] ,expr1);
		
		cplex.addGe(EB[t],prawaStrona );
	}
	
	
	
	private void dodajOgraniczWyrownanieKonsumpcji(IloCplex cplex, int t) throws IloException
	{
		IloNumExpr expr1 =null;
		int i=0;
		while (i<EVdom_c.size())
		{
			//print("dodajOgraniczSkladowychBaterii "+i);
			IloNumVar[] EVdom_c24  = EVdom_c.get(i);
			//IloNumVar[] EVpraca_c24  = EVpraca_c.get(i);
			
			IloNumExpr exprToBeAdded = EVdom_c24[t];
			//IloNumExpr exprToBeAdded = cplex.sum(EVdom_c24[t],EVpraca_c24[t] );

			
			if (i==0)
			{
				expr1=exprToBeAdded;
			}
			else
			{
				expr1=cplex.sum(expr1,exprToBeAdded);
			}
			
			i++;
		}
		
		
		IloNumExpr prawaStrona = cplex.sum(EB_c[t], G_c[t],expr1);
		cplex.addGe(C[t],prawaStrona );
	}
	
	private void dodajOgraniczPredkoscBateriiModulo(IloCplex cplex, int t) throws IloException
	{
		//print ("dodajOgraniczPredkoscBateriiModulo "+predkoscBaterii);
		cplex.addGe(predkoscBaterii, cplex.diff(EB[t+1],EB[t]));
		cplex.addLe(-predkoscBaterii, cplex.diff(EB[t+1],EB[t]));

	}
	
	private void dodajOgraniczSkladowejPredkosciBaterii(IloCplex cplex, int t) throws IloException
	{
		//print("dodajOgraniczSkladowejPredkosciBaterii "+predkoscBaterii);
		IloNumExpr expr1 =null;
		int i=0;
		while (i<EVdom_EB.size())
		{
			//print("dodajOgraniczSkladowychBaterii "+i);
			IloNumVar[] EVdom_EB24  = EVdom_EB.get(i);
			IloNumVar[] EB_EVdom24  = EB_EVdom.get(i);
			
			IloNumExpr exprToBeAdded = cplex.sum(EVdom_EB24[t],EB_EVdom24[t]);

			if (i==0)
			{
				expr1=exprToBeAdded;
			}
			else
			{
				expr1=cplex.sum(expr1,exprToBeAdded);
			}
			
			i++;
		}
		
		
		IloNumExpr prawaStrona = cplex.sum(EB_c[t], G_sklad[t],expr1);
		cplex.addGe(predkoscBaterii,prawaStrona );
	}
	
	//TODO
	private void dodajOgraniczeniaModelu(IloCplex cplex) {
		print("dodajOgraniczeniaModelu");
		try{
		//i=t
			int i=0;
			while (i<Stale.horyzontCzasowy)
			{
				
				if (i<Stale.horyzontCzasowy-1)
				{
					dodajOgraniczCiaglosciBaterii(cplex,i);
					dodajOgraniczPredkoscBateriiModulo(cplex,i);
					
				}
				
				dodajOgraniczSkladowychGeneracji(cplex,i);
				dodajOgraniczSkladowychBaterii(cplex,i);
				dodajOgraniczWyrownanieKonsumpcji(cplex,i);
				dodajOgraniczSkladowejPredkosciBaterii(cplex,i);

				
				
				i++;
			}
		} catch (IloException e) {
			print("dodajOgraniczCiaglosciBaterii -fail");
			e.printStackTrace();
		}		
	}
	

	
	private void dodajOgraniczCiaglosciBaterii(IloCplex cplex, int t) throws IloException
	{
		IloNumExpr expr1 =null;
		int i=0;
		while (i<EVdom_EB.size())
		{
			IloNumVar[] EVdom_EB24  = EVdom_EB.get(i);			
			//IloNumVar[] EVpraca_EB24  = EVpraca_EB.get(i);

			IloNumVar[] EB_EVdom24  = EB_EVdom.get(i);			
			//IloNumVar[] EB_EVpraca24  = EB_EVpraca.get(i);
			
			//IloNumExpr exprToBeAdded = .sum(EVdom_EB24[t],EVpraca_EB24[t] );
			IloNumExpr exprToBeAdded = cplex.diff(EVdom_EB24[t],EB_EVdom24[t] );

			
			if (i==0)
			{
				expr1=exprToBeAdded;
			}
			else
			{
				expr1=cplex.sum(expr1,exprToBeAdded);
			}
			
			i++;
		}
		
		IloNumExpr prawaStrona =cplex.diff(cplex.sum(EB[t+1], expr1,G_sklad[t]),EB_c[t] );

		
		cplex.addEq(EB[t],prawaStrona );
	}

	public class Sterowanie
	{
		DayData dayData;		
		ArrayList<ArrayList<EVData>> eVDataList = new ArrayList<>();  
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
