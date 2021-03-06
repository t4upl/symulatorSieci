import java.util.ArrayList;

import ilog.concert.IloException;
import ilog.concert.IloNumExpr;
import ilog.concert.IloNumVar;
import ilog.concert.IloNumVarType;
import ilog.cplex.IloCplex;
import ilog.cplex.IloCplex.Status;

public class OptimizerEV2 extends CoreClass {

	//set it to false for test (test =no EVs)
	final Boolean EVwystpeujeWmodelu =true;


	IloCplex cplex;
	OptimizerEV.Form24 form24;
	ProsumentEV prosumentEV;
	
	
	//Stale
	private float predkoscBaterii;
	private float cenaDystrybutoraZewnetrznego =Stale.cenaDystrybutoraZewnetrznego;
	//private float cenaBaterii = Stale.kosztAmortyzacjiBaterii;
	private float pojemnoscBaterii;
	private int horyzontCzasu = Stale.horyzontCzasowy;
	
	private double reallyBigNumber =Stale.reallyBigNumber;
	private int liczbaSamochodow;
	
	//stany poczatkwoe magazynow energii
	private float stanPoczatkowyBaterii;
	private ArrayList<Float> stanPoczatkowyEVdom;
	
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
	private static OptimizerEV2 instance = null;
	private OptimizerEV2() 
	{
	}
	
	public static OptimizerEV2 getInstance() {
	      if(instance == null) {
	         instance = new OptimizerEV2();
	      }
	      return instance;
	}
	
	
	
	IloNumExpr createObjective()
	{
		if (stanPoczatkowyEVdom==null)
		{
			getInput("ERROR in createObjective,  null refference, check if setVariables was performed beor createObjective");
		}
		
		//print("WYLACZ TO ZANIM POJDZIESZ DALEJ koszty==0");

		
		IloNumExpr objective = null;
			
		try {
			int i =0;
			while (i<Stale.horyzontCzasowy)
			{
				//print("createObjective "+i);
				// 1 i 4
				createObjectiveKosztZew(i);
				createObjectiveKosztSklad(i);
				createObjectiveKosztEV(i);
				
				//getInput("createObjective - amnaged to create objective1");
				
				//cplex.addEq(koszt_sklad[i], 0);
				//cplex.addEq(koszt_EV[i], 0);
				
				//print("objective "+koszt_Zew[i].toString());
				
				cplex.addEq(koszt[i], cplex.sum(koszt_Zew[i],koszt_sklad[i],koszt_EV[i]  ));
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
	
	private void createObjectiveKosztZew( int t) throws IloException
	{
		IloNumExpr expr1 =null;
		

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
		
		cplex.addEq(koszt_Zew[t],cplex.prod(cenaDystrybutoraZewnetrznego, prawaStrona ));
	}
	
	
	private void createObjectiveKosztSklad(int t) throws IloException
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
		cplex.addEq(koszt_sklad[t],cplex.prod(Stale.kosztAmortyzacjiBaterii,prawaStrona) );
	}

	
	private void createObjectiveKosztEV(int t) throws IloException
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
		cplex.addEq(koszt_EV[t],cplex.prod(Stale.kosztAmortyzacjiAkumulatoraEV, prawaStrona));
	}		
	
	void dodajOgraniczeniaPoczatkowyStanBaterii()
	{
		try{
			cplex.addEq(EB[0], stanPoczatkowyBaterii);
			int i=0;
			while (i<stanPoczatkowyEVdom.size())
			{	
				IloNumVar[] EVdom24 = EVdom.get(i);

				
				//print("dodajOgraniczeniaPoczatkowyStanBaterii "+stanPoczatkowyEVdom.get(i));	
				cplex.addEq(stanPoczatkowyEVdom.get(i), EVdom24[0]);
			
				i++;
			}
		}catch (Exception e)
		{
			print("dodajOgraniczeniaPoczatkowyStanBaterii Exception");
		}
		
	}
	
	//TODO
	private void dodajOgraniczeniaModelu() 
	{
		//print("dodajOgraniczeniaModelu");
		try{
		//i=t
			int i=0;
			while (i<Stale.horyzontCzasowy)
			{
				
				if (i+1<Stale.horyzontCzasowy)
				{
					dodajOgraniczCiaglosciBaterii(i);
					dodajOgraniczPredkoscBateriiModulo(i);
					
				}
				
				dodajOgraniczSkladowychGeneracji(i);
				dodajOgraniczSkladowychBaterii(i);
				dodajOgraniczWyrownanieKonsumpcji(i);
				dodajOgraniczSkladowejPredkosciBaterii(i);

				
				
				i++;
			}
		} catch (IloException e) {
			print("dodajOgraniczCiaglosciBaterii -fail");
			e.printStackTrace();
		}		
	}
	
	private void dodajOgraniczSkladowejPredkosciBaterii(int t) throws IloException
	{
		//print("dodajOgraniczSkladowejPredkosciBaterii "+predkoscBaterii);
		IloNumExpr expr1 =null;
		int i=0;
		while (i<liczbaSamochodow)
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
		
		//print ("dodajOgraniczSkladowejPredkosciBaterii "+prawaStrona.toString());
		//IloNumExpr prawaStrona = cplex.sum(EB_c[t], G_sklad[t],expr1);
		cplex.addGe(predkoscBaterii,prawaStrona );
	}	
	
	private void dodajOgraniczWyrownanieKonsumpcji(int t) throws IloException
	{
		//print("dodajOgraniczWyrownanieKonsumpcji "+C[t]);
		IloNumExpr expr1 =null;
		int i=0;
		while (i<liczbaSamochodow)
		{
			IloNumVar[] EVdom_c24  = EVdom_c.get(i);	
			IloNumExpr exprToBeAdded = EVdom_c24[t];
			
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
	
	private void dodajOgraniczSkladowychBaterii(int t) throws IloException
	{
		IloNumExpr expr1 =null;
		int i=0;
		while (i<liczbaSamochodow)
		{
			IloNumVar[] EB_EVdom24  = EB_EVdom.get(i);			

			IloNumExpr exprToBeAdded = EB_EVdom24[t];
			
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
	
	private void dodajOgraniczSkladowychGeneracji(int t) throws IloException
	{
		IloNumExpr expr1 =null;
		int i=0;
		while (i<liczbaSamochodow)
		{
			IloNumVar[] G_EVdom24  = G_EVdom.get(i);			
			IloNumExpr exprToBeAdded = G_EVdom24[t];

			
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
	
	private void dodajOgraniczPredkoscBateriiModulo(int t) throws IloException
	{
		cplex.addGe(predkoscBaterii, cplex.diff(EB[t+1],EB[t]));
		cplex.addLe(-predkoscBaterii, cplex.diff(EB[t+1],EB[t]));
	}	
	
	private void dodajOgraniczCiaglosciBaterii(int t) throws IloException
	{
		IloNumExpr expr1 =null;
		int i=0;
		while (i<EVdom_EB.size())
		{
			IloNumVar[] EVdom_EB24  = EVdom_EB.get(i);			

			IloNumVar[] EB_EVdom24  = EB_EVdom.get(i);			
			
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
		
		IloNumExpr prawaStrona =cplex.diff(cplex.sum(EB[t],G_sklad[t],expr1),EB_c[t] );
		//print("dodajOgraniczCiaglosciBaterii "+prawaStrona.toString());
		//IloNumExpr prawaStrona =cplex.diff(cplex.sum(EB[t], expr1,G_sklad[t]),EB_c[t] );
		
		cplex.addEq(EB[t+1],prawaStrona );
	}
	
	void fillOutSolvedVariablesDayData(OptimizerEV.Sterowanie sterowanie)
	{
		try
		{
		//	print("fillOutSolvedVariablesDayData");
			double[] EB_solved = cplex.getValues(EB);
		//	print("fillOutSolvedVariablesDayData +EB_solved");
			
			double[] G_c_solved = cplex.getValues(G_c);
		//	print("fillOutSolvedVariablesDayData +G_c_solved");
			
			double[] G_sklad_solved = cplex.getValues(G_sklad);
		//	print("fillOutSolvedVariablesDayData +G_sklad");
			
			double[] EB_c_solved = cplex.getValues(EB_c);
		//	print("fillOutSolvedVariablesDayData +EB_c");
			

			ArrayList<String> dayList =  LokalneCentrum.getDayList();
			ArrayList<String> hourList =  LokalneCentrum.getHourList2();
			
		   //ArrayList<DayData> dList = new ArrayList<>();  
		   int i=0;
		   while (i<Stale.horyzontCzasowy)
		   {
			   DayData d = new DayData();
			   //hourList2 i dayList jest ograniczony prze zsimulatin enddat ewiec jest ryzyko wziecia z poza lsity
			   if (LokalneCentrum.getTimeIndex()+i<dayList.size())
			   {
				   d.setDay(dayList.get(LokalneCentrum.getTimeIndex()+i));
				   d.setHour(hourList.get(LokalneCentrum.getTimeIndex()+i));
			   }
			   
			   d.setConsumption(C[i]);
			   d.setGeneration(-1);
			   d.setTrueGeneration(G[i]);
			   
			   d.setStanBateriiNaPoczatkuSlotu((float)EB_solved[i]);
			   
			   if (i>0)
			   {
				   DayData dPrevious =sterowanie.getDayDataList().get(sterowanie.getDayDataList().size()-1);
				   dPrevious.setStanBateriiNaKoniecSlotu((float)EB_solved[i]);
			   }
			    

			   
			   d.setZGeneracjiNaKonsumpcje((float)G_c_solved[i]);
			   d.setZGeneracjiDoBaterii((float) G_sklad_solved[i]);
			   d.setZBateriiNaKonsumpcje((float)EB_c_solved[i]);
			   
			   sterowanie.addToDayDataList(d);

		
			   //dList.add(d);
			   i++;
		   }
		} catch (Exception e)
		{
			getInput ("ERROR in fillOutSolvedVariablesDayData");
		}
	}
	
	//TODO
	void fillOutSolvedVariables(OptimizerEV.Sterowanie sterowanie)
   	{
	//	print ("fillOutSolvedVariables -start");
		
		fillOutSolvedVariablesDayData(sterowanie);
		
		
		
		try {
			
		   int i=0;
		   while (i<liczbaSamochodow)
		   {
			   ArrayList<EVData> listaZeSterowaniemPojedynczegoEV = new ArrayList<>();
			   
			   IloNumVar[] EV24 = EVdom.get(i);
			   IloNumVar[] EV_c24 = EVdom_c.get(i); 
			   IloNumVar[] EV_EB24 = EVdom_EB.get(i);
			   IloNumVar[] EB_EV24 = EB_EVdom.get(i);
			   IloNumVar[] G_EV24 = G_EVdom.get(i);
			   IloNumVar[] Zew_EV24 = Zew_EVdom.get(i);
			   
				double[] EV24_solved = cplex.getValues(EV24);
				double[] EV_c24_solved = cplex.getValues(EV_c24);
				double[] EV_EB24_solved = cplex.getValues(EV_EB24);
				double[] EB_EV24_solved = cplex.getValues(EB_EV24);
	
				double[] G_EV24_solved = cplex.getValues(G_EV24);
				double[] Zew_EV24_solved = cplex.getValues(Zew_EV24);
				
				//statusy i-teog smaochodu
				ArrayList<Integer> statusy = statusEV.get(i);
			   
				ArrayList<EVData> eList = new ArrayList<>();
			   int j=0;
			   while (j<Stale.horyzontCzasowy)
			   {
				   	EVData eVData = new EVData();
										
					eVData.setEVdom((float)EV24_solved[j]);
					eVData.setEVdom_c((float)EV_c24_solved[j]);
					eVData.setEVdom_EB((float)EV_EB24_solved[j]);
					eVData.setEB_EVdom((float)EB_EV24_solved[j]);
					eVData.setG_EVdom((float)G_EV24_solved[j]);
					eVData.setZew_EVdom((float)Zew_EV24_solved[j]);
					
					eVData.setStatus(statusy.get(j));
					
					
					eList.add(eVData);
				   
				   j++;
			   }
			   
			   sterowanie.addToEVDataList(eList);
			   
			   i++;
		   }
		   
		   double[] koszt24_solved = cplex.getValues(koszt);
		   double[] koszt_Zew24_solved=cplex.getValues(koszt_Zew);
		   double[] koszt_sklad24_solved = cplex.getValues(koszt_sklad);
		   double[] koszt_EV24_solved = cplex.getValues(koszt_EV);
		   
		   sterowanie.setKoszt(koszt24_solved);
		   sterowanie.setKoszt_Zew(koszt_Zew24_solved);
		   sterowanie.setKoszt_sklad(koszt_sklad24_solved);
		   sterowanie.setKoszt_EV(koszt_EV24_solved);
		   
		}catch (Exception e)
		{
			print("ERROR in fillOutSolvedVariables \n"+e.getMessage()+"\n"+e.getCause()+e.getStackTrace());
		}
   	}	
	
	//TODO
	public OptimizerEV.Sterowanie wyznaczSterowanie(OptimizerEV.Form24 form24, ProsumentEV prosumentEV)
	{
		
		OptimizerEV.Sterowanie sterowanie = new OptimizerEV.Sterowanie();		

		try{
			cplex = new IloCplex();
			setVariables(prosumentEV, form24);
			
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
	
	void dodajOgraniczeniaEV()
	{
		int i=0;
		while (i<liczbaSamochodow)
		{
			
			if (EVwystpeujeWmodelu)
			{
				//getInput("dodajOgraniczeniaEV -dopisz");
				dodajOgraniczeniaEVDlaPojedynczegoSamochodu(i);
				//dodajOgraniczeniaEVDlaPojedynczegoSamochodu(cplex,i);
			}	
			else
			{
				wylaczSamochod(i);
			}
			i++;
		}
	}
	
	void dodajOgraniczeniaEVDlaPojedynczegoSamochodu(int indeksSamochodu)
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
	
	void wylaczSamochod(int indeksSamochodu)
	{
		
		try{
			IloNumVar[] EV24 = EVdom.get(indeksSamochodu);
			IloNumVar[] EV_c24 = EVdom_c.get(indeksSamochodu);			
			IloNumVar[] EV_EB24 = EVdom_EB.get(indeksSamochodu);
			
			IloNumVar[] EB_EVdom24 = EB_EVdom.get(indeksSamochodu);
			IloNumVar[] G_EVdom24 = G_EVdom.get(indeksSamochodu);			
			IloNumVar[] Zew_EVdom24 = Zew_EVdom.get(indeksSamochodu);
			
			int i=0;
			while(i<Stale.horyzontCzasowy)
			{
				cplex.addEq(EV24[i], 0);
				cplex.addEq(EV_c24[i], 0);
				cplex.addEq(EV_EB24[i], 0);
				
				cplex.addEq(EB_EVdom24[i], 0);
				cplex.addEq(G_EVdom24[i], 0);
				cplex.addEq(Zew_EVdom24[i], 0);
				
				i++;
			}
		} catch (Exception e)
		{
			print ("ERROR in wylaczSamochod");
		}
	}
	
	//TODO
	void setVariables(ProsumentEV prosumentEV, OptimizerEV.Form24 form24)
	{
		this.form24=form24;
		this.prosumentEV =prosumentEV;
		
		this.predkoscBaterii =prosumentEV.getPredkoscBaterii();
		this.pojemnoscBaterii = prosumentEV.getPojemnoscBaterii();
		this.liczbaSamochodow = form24.geteVDataList().size();
				
		koszt = stworzWektorZmiennychCiaglych(Stale.horyzontCzasowy,-reallyBigNumber,reallyBigNumber, cplex);
		koszt_Zew = stworzWektorZmiennychCiaglych(Stale.horyzontCzasowy,-reallyBigNumber,reallyBigNumber, cplex);
		koszt_sklad = stworzWektorZmiennychCiaglych(Stale.horyzontCzasowy,-reallyBigNumber,reallyBigNumber, cplex);
		koszt_EV = stworzWektorZmiennychCiaglych(Stale.horyzontCzasowy,-reallyBigNumber,reallyBigNumber, cplex);
		
		
		EB = stworzWektorZmiennychCiaglych(Stale.horyzontCzasowy,pojemnoscBaterii, cplex);
		//print("EB_solved");

		EB_c = stworzWektorZmiennychCiaglych(horyzontCzasu,predkoscBaterii, cplex);
		//print ("EB_c_solved");

		G_c = stworzWektorZmiennychCiaglych(horyzontCzasu,reallyBigNumber, cplex);
		//print ("G_c_solved");

		G_sklad = stworzWektorZmiennychCiaglych(horyzontCzasu,predkoscBaterii, cplex); 
		//print ("G_sklad_solved");

		stanPoczatkowyBaterii = form24.getStanPoczatkowyBaterii();
		stanPoczatkowyEVdom=form24.getStanPoczatkowyEVdomList();
		statusEV=form24.getStatusEV();
		
		C = form24.getKonsumpcjaArray();
		G = form24.getGeneracjaArray();
		
		EVdom=stworzListeWektorZmiennychCiaglych(cplex,Stale.pojemnoscAkumualtoraEV);
		EVdom_c=stworzListeWektorZmiennychCiaglych(cplex,Stale.predkoscAkumulatoraEV);
		
		//predkosc baterii, a nie Stale.predkoscBaterii bo dla wirtualnego rposumenta ona jest rozna niz domyslna
		//print ("SetVariables min:"+Math.min(Stale.predkoscAkumulatoraEV,predkoscBaterii));
		EVdom_EB=stworzListeWektorZmiennychCiaglych(cplex,Math.min(Stale.predkoscAkumulatoraEV,predkoscBaterii));
		EB_EVdom=stworzListeWektorZmiennychCiaglych(cplex,Math.min(Stale.predkoscAkumulatoraEV,predkoscBaterii));
		G_EVdom=stworzListeWektorZmiennychCiaglych(cplex,Stale.predkoscAkumulatoraEV);
		Zew_EVdom=stworzListeWektorZmiennychCiaglych(cplex,Stale.predkoscAkumulatoraEV);		
		
	}
	
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

	
}
