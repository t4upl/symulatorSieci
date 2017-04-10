import java.util.ArrayList;
import java.util.Arrays;



//extends w sensie bierze wszystkie zmienne i zadnych funkcji(poza set-getami)
public class ProsumentEV extends Prosument {

	Loader loader = Loader.getInstance();
	
	//lista zawiera dane dotyczace uzycia akumualtorow EV
	private ArrayList<EVData> eVDataList = new ArrayList<>(); 
	
	//zawiera listy kolejnych smaochodow
	ArrayList<ArrayList<EVData>> listaPodrozyFlota = new ArrayList<>(); 

	OptimizerEV optimizerEV = OptimizerEV.getInstance();
	
	//GETTTERS
	ArrayList<EVData> getEVDataList()
	{
		return this.eVDataList;
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
		
		while (eVDataList.size()!=dayDataList.size())
		{
			//print("panda");
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
			
			eVData.setStatus(statusEVData);
			eVDataList.add(eVData);
		}
		
		//test
		/*i=0;
		for (Integer j: listaPodrozy)
		{
			String hour = dayDataList.get(i).getHour();
			print(hour+" "+j);
			i++;
		}*/
	}
	
	public void dodajListeDolistaPodrozyFlota(ArrayList<EVData> listaPodrozy)
	{
		listaPodrozyFlota.add(listaPodrozy);
		
		//print (listaPodrozyFlota.size());
	}
	
	
	@Override
	public void loadData()
	{
		super.loadData();
		
		ArrayList<String> godzinyPodrozy = loader.loadCars(ID);
		createListaPodrozy(godzinyPodrozy);
		//getInput("loadData "+ID);
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
				statusy.add(L1.get(j).getStatus());
				j++;
			}
			form24.addToStatusEV(statusy);
			//form24.addTostanPoczatkowyEVpracaList(eVData.getEVpraca());
			
			i++;
		}
		
		
		
		//dopsiac generacje i konsumpcje, dayData to generacja list - > 24 generacja ()
		
		return form24;
	}
	
	@Override
	//nzjadz i wykonaj sterowanie wirtualnego prosuemtna posiadajaceog EV
	public void zaktualizujHandelBrakHandlu()
	{
		
		OptimizerEV.Form24 form24 = createForm24();
		
		//getInput("Managed to create form 24");
		
		OptimizerEV.Sterowanie sterowanie =optimizerEV.wyznaczSterowanie(form24,this);
		print("zaktualizujHandelBrakHandlu -end");
		getInput("");
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
}
