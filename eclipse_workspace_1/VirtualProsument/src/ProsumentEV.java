import java.util.ArrayList;
import java.util.Arrays;



//extends w sensie bierze wszystkie zmienne i zadnych funkcji(poza set-getami)
public class ProsumentEV extends Prosument {

	Loader loader = Loader.getInstance();
	
	//lista zawiera dane dotyczace uzycia akumualtorow EV
	private ArrayList<EVData> eVDataList = new ArrayList<>(); 
	
	//zawiera listy kolejnych smaochodow
	ArrayList<ArrayList<EVData>> listaPodrozyFlota = new ArrayList<>(); 
	
	
	//lista zawiera sume EVData
	private ArrayList<EVData> eVDataListSuma = new ArrayList<>();

	OptimizerEV optimizerEV = OptimizerEV.getInstance();
	OptimizerEV2 optimizerEV2 = OptimizerEV2.getInstance();
	
	
	//uzywane przy reportowaniu
	//zahcowaj u prosument, bo trudno bedzie to zwiazac z EV()
	ArrayList<Float> koszty = new ArrayList<>();
	ArrayList<Float> koszty_Zew = new ArrayList<>();
	ArrayList<Float> koszty_sklad = new ArrayList<>();
	ArrayList<Float> koszty_EV = new ArrayList<>();

	
	//GETTTERS
	ArrayList<EVData> getEVDataListSuma()
	{
		return this.eVDataListSuma;
	}
	
	ArrayList<EVData> getEVDataList()
	{
		return this.eVDataList;
	}
	
	public ArrayList<Float> getKoszty() {
		return koszty;
	}

	public ArrayList<Float> getKoszty_Zew() {
		return koszty_Zew;
	}

	public ArrayList<Float> getKoszty_sklad() {
		return koszty_sklad;
	}

	public ArrayList<Float> getKoszty_EV() {
		return koszty_EV;
	}

	public ArrayList<ArrayList<EVData>> getListaPodrozyFlota() {
		return listaPodrozyFlota;
	}

	
	//SETTERS
	
	//TODO
	//------------------------------
	//OTHER
	

	//uzupelnia(wyplenia wartosciami) liste podrozy
	void createListaPodrozy(ArrayList<String> godzinyPodrozy)
	{
		int status =0;
		int i=0;
		
		String wyjazd = godzinyPodrozy.get(0);
		String przyjazd = godzinyPodrozy.get(1);
		
		//jezeli spotkano Stale.endOfSimulation to wstawiaj tylko 0 do listy (prosuemtn w domu) 
		Boolean endDataReached = false;
		
		while (eVDataList.size()!=dayDataList.size())
		{

			String hour = dayDataList.get(i).getHour();
			
			EVData eVData = new EVData();
			int statusEVData;
			//print (hour+" "+wyjazd);
			
			if (status==0)
			{
				if (hour.equals(wyjazd))
				{
					statusEVData=1;
					status=2;
				}
				else
				{
					statusEVData=0;
				}
			}
			else
			{
				if (hour.equals(przyjazd))
				{
					statusEVData=3;
					status=0;
				}
				else
				{
					statusEVData=2;
				}
			}
			i++;
			
			//sprawdz czy znaleziono Stale.Simualtion end date jezeli tak to wstaiwaj zera
			
			if (endDataReached)
			{
				statusEVData =0;
			}
			else
			{
				String day = dayDataList.get(i).getDay();
				
				if ((day+" "+hour).equals(Stale.simulationEndDate))
				{
					//getInput("Heelo from createListaPodrozy");
					endDataReached=true;					
				}
			}
			

			
			eVData.setStatus(statusEVData);
			eVDataList.add(eVData);
		}
		
	}
	
	public void dodajListeDolistaPodrozyFlota(ArrayList<EVData> listaPodrozy)
	{
		listaPodrozyFlota.add(listaPodrozy);		
	}
	
	
	@Override
	public void loadData()
	{
		super.loadData();
		
		ArrayList<String> godzinyPodrozy = loader.loadCars(ID);
		
		//inicjalizuje listy EVData pustymi obiektami
		createListaPodrozy(godzinyPodrozy);		
	}
	
	
	//tworzy i uzupelnia liste eVDataListSuma EVData 
	public void initializeEVDataListSuma()
	{
		
		
		//getInput("initializeEVDataListSuma - start");
		while (eVDataListSuma.size()!=dayDataList.size())
		{
			EVData ev = new EVData();
			
			int i=0;
			while (i<listaPodrozyFlota.size())
			{
				EVData ev2 = listaPodrozyFlota.get(i).get(eVDataListSuma.size());
				
				ev.setEB_EVdom(ev.getEB_EVdom()+ev2.getEB_EVdom());
				ev.setEVdom(ev.getEVdom()+ev2.getEVdom());
				ev.setEVdom_c(ev.getEVdom_c()+ev2.getEVdom_c());
				ev.setEVdom_EB(ev.getEVdom_EB()+ev2.getEVdom_EB());
				ev.setG_EVdom(ev.getG_EVdom()+ev2.getG_EVdom());
				
				ev.setZew_EVdom(ev.getZew_EVdom() +ev2.getZew_EVdom());
				
				
				i++;
			}
			
			
			
			eVDataListSuma.add(ev);
		}
				
	}
	
	
	<T>ArrayList<ArrayList<T>> get24ListMassive(ArrayList<ArrayList<T>> inputList)
	{
		ArrayList<ArrayList<T>> L1 = new ArrayList<>();
		
		int i=0;
		while (i<inputList.size())
		{
			ArrayList<T> L2 = get24List(inputList.get(i));
			L1.add(L2);
			
			i++;
		}
		
		return L1;
	}
	
	//zwraca liste eleementow dostepan w zakresie horyzontu czaoswego
	<T>ArrayList<T> get24List(ArrayList<T> inputList)
	{
		ArrayList<T> L1 = new ArrayList<>();
		
		int i=0;
		while (i<Stale.horyzontCzasowy)
		{
			L1.add(inputList.get(LokalneCentrum.getTimeIndex()+i));
			i++;
		}
		
		return L1;
	}
	
	DayData currentDayData()
	{
		return dayDataList.get(LokalneCentrum.getTimeIndex());
	}
	
	
	//TODO
	OptimizerEV.Form24 createForm24()
	{
		OptimizerEV.Form24 form24 = new OptimizerEV.Form24();
		
		ArrayList<DayData> dList24 = get24List(dayDataList);
		form24.setDayDataList(dList24);
		
		ArrayList<ArrayList<EVData>> EvListOfLists = get24ListMassive(listaPodrozyFlota);
		form24.seteVDataList(EvListOfLists);
		
		
		float[]generacja = getGenerationArray(dList24);
		float[] konsumpcja = getConsumptionArray(dList24);
		
		form24.setGeneracjaArray(generacja);
		form24.setKonsumpcjaArray(konsumpcja);
		
		
		form24.setStanPoczatkowyBaterii(currentDayData().getStanBateriiNaPoczatkuSlotu());
		
		
		
		int i=0;
		while (i<listaPodrozyFlota.size())
		{
			ArrayList<EVData> L1 = listaPodrozyFlota.get(i);
			EVData eVData = L1.get(LokalneCentrum.getTimeIndex());
			
			form24.addToStanPoczatkowyEVdomList(eVData.getEVdom());
			
			//wypelnij statusy w form
			ArrayList<Integer> statusy = new ArrayList<>();
			int j=0;
			while (j<Stale.horyzontCzasowy)
			{
				statusy.add(L1.get(LokalneCentrum.getTimeIndex()+j).getStatus());
				j++;
			}
			form24.addToStatusEV(statusy);
			//form24.addTostanPoczatkowyEVpracaList(eVData.getEVpraca());
			
			i++;
		}
		
		
		
		//dopsiac generacje i konsumpcje, dayData to generacja list - > 24 generacja ()
		
		return form24;
	}
	
	//TODO
	@Override
	//nzjadz i wykonaj sterowanie wirtualnego prosuemtna posiadajaceog EV
	public void zaktualizujHandelBrakHandlu()
	{
		
		OptimizerEV.Form24 form24 = createForm24();
		
		//getInput("Managed to create form 24");
		
		OptimizerEV.Sterowanie sterowanie =optimizerEV2.wyznaczSterowanie(form24,this);
		//OptimizerEV.Sterowanie sterowanie =optimizerEV.wyznaczSterowanie(form24,this);
		
		reporter.createSterowanieReport(sterowanie,this,form24);
		
		
		rozpakujSterownaie(sterowanie);
		
		/*if (LokalneCentrum.getCurrentDay().equals("2015-06-02"))
		{
			print("IF FOR DEBUG ONLy"); //actually this whoel seciton is for debug - prosuemnts report only at the end
			reporter.createProsumentReport(this);
			getInput("STOP");
		}*/
		
	}
	
	
	//bierze wyniki z lsit w steornwniu i wrzuca do DayData i EVDataList
	//nie zpaewnia,przepisania  stanu baterii ze slotu #0 jako poczatkowy stna baterii w slocie #1
	//jest do teog oddzielna funkcja
	void rozpakujSterownaie (OptimizerEV.Sterowanie sterowanie)
	{
		//print("rozpakujSterownaie -end");
		
		DayData fisrDayData = sterowanie.getDayDataList().get(0);
		dayDataList.set(LokalneCentrum.getTimeIndex(), fisrDayData);
		
		
		ArrayList<ArrayList<EVData>> eVDataListOfLists = sterowanie.eVDataList;
		int i=0;
		while (i<eVDataListOfLists.size())
		{

			
			//print("rozpakujSterownaie "+i);
			ArrayList<EVData> eVDataList2 = eVDataListOfLists.get(i);
			ArrayList<EVData> eVDataListWirtualnegoProsumenta =listaPodrozyFlota.get(i);
			
			eVDataListWirtualnegoProsumenta.set(LokalneCentrum.getTimeIndex(), eVDataList2.get(0));
			
			//bo trzeba zapakowac stan baterii "na koneic slotu" 
			//w "", bo EV nie ma czgeos takiego jak na koniec slotu - ma stan baterii na poczatku kolejnego slotu
			eVDataListWirtualnegoProsumenta.set(LokalneCentrum.getTimeIndex()+1, eVDataList2.get(1));
			i++;
		}
		
		
		//rozpakuj sterowanie
		
		double[] koszt24_solved = sterowanie.getKoszt();
		double[] koszt_Zew24_solved= sterowanie.getKoszt_Zew();
		double[] koszt_sklad24_solved = sterowanie.getKoszt_sklad();
		double[] koszt_EV24_solved = sterowanie.getKoszt_EV();
		
		koszty.add((float)koszt24_solved[0]);
		koszty_Zew.add((float)koszt_Zew24_solved[0]);
		koszty_sklad.add((float)koszt_sklad24_solved[0]);
		koszty_EV.add((float)koszt_EV24_solved[0]);
		   
		
		
		//getInput("rozpakujSterownaie -end");
	}
	
	float[] getGenerationArray(ArrayList<DayData> dayDataList24)
  	{
  		return getXXXArray(dayDataList24,"generation");
  	}
  	
  	float[] getConsumptionArray(ArrayList<DayData> dayDataList24)
  	{
  		return getXXXArray(dayDataList24,"consumption");
  	}
  	
  	//type ="generation" or "consumption"
  	float[] getXXXArray(ArrayList<DayData> dayDataList24,String type)
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
  	
  	@Override
  	void charge()
  	{
  		//print("UZUPELNIJ CHARGE");
  		int currentTime = LokalneCentrum.getTimeIndex();
  		DayData d =dayDataList.get(currentTime);
  		
  		//w kosztach jest trzymany ksozt uzyskany w wyniku optymalizacji
  		//funkcja celu ma usunieta konsumpcje 
  		d.setCost(koszty.get(currentTime)+d.getConsumption()*Stale.cenaDystrybutoraZewnetrznego);
  	}
  	
  	public void createReport()
  	{
  		reporter.createProsumentReport(this);
  	}
  	
  	@Override
	public void performEndOfSimulationCalculations(Boolean countGenerationFlag)
	{
		//getInput("performEndOfSimulationCalculations - for EVprosument");
		totalCost=0;
		
		int a=0;
		String date="";
		while (!date.equals(Stale.simulationEndDate))
		{
			DayData d = dayDataList.get(a);
			totalCost+=d.getCost();			
			a++;
			d = dayDataList.get(a);
			date=d.getDay()+" "+d.getHour();
		}
		
		costNoReserve = totalCost;
		countReserveBonus();		
	}
  	
  	@Override
	void countReserveBonus()
	{
  		print ("countReserveBonus ");
		//index LokalneCentrum.getTimeIndex() bo to jest stan konca symualcji (Lokalne Centrum doszlo do Simulatin End date)
		reserveBonus = dayDataList.get(LokalneCentrum.getTimeIndex()).getStanBateriiNaKoniecSlotu()*Stale.kosztAmortyzacjiBaterii;
		int i=0;
		while (i<listaPodrozyFlota.size())
		{
			ArrayList<EVData> eVlist = listaPodrozyFlota.get(i);
			//print (reserveBonus);
			//print ("stan baterii dla samochodu "+ i+" "+eVlist.get(LokalneCentrum.getTimeIndex()).EVdom );
			reserveBonus+=eVlist.get(LokalneCentrum.getTimeIndex()).EVdom*Stale.kosztAmortyzacjiAkumulatoraEV;
			i++;
		}
		
		
		totalCost-=reserveBonus;
		
	}
  	
  	//wykoanj skalownaie dla wsyzsktich smaochodow
  	void EVDataDivide(int divider)
  	{
  		int i=0;
  		while (i<listaPodrozyFlota.size())
  		{
  			ArrayList<EVData> eVlist = listaPodrozyFlota.get(i);
  			
  			int j=0;
  			while (j<eVlist.size())
  			{
  				EVData eVData = eVlist.get(j);
  				
  				//skalowanie EVDaTa jest potrzebne w wirtualnym prosumencie po to zbey policzyc dobrze rezerwe
  				//nie liczyc z nieprzeskalowanej rezerwy
  				//zrzutu usrednionego prosuemnta tez brka wiec nie ma sensu liczyc reszty fieldow
  				eVData.setEVdom(eVData.getEVdom()/divider);
  				
  				j++;
  			}
  			
  			
  			i++;
  		}
  	}
  	
}
