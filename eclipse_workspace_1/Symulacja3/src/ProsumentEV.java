import java.util.ArrayList;

public class ProsumentEV extends Prosument {

	Loader loader = Loader.getInstance();
	
	//lista zawiera dane dotyczace uzycia akumualtorow EV
	private ArrayList<EVData> eVDataList = new ArrayList<>(); 
	
	OptimizerEV optimizerEV = OptimizerEV.getInstance();
	
	@Override
	public void loadData()
	{
		super.loadData();
		
		ArrayList<String> godzinyPodrozy = loader.loadCars(ID);
		createListaPodrozy(godzinyPodrozy);
		//getInput("loadData "+ID);
	}
	
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
	

	@Override
	//price vector ma postac (0) -mniejsza cena dla najblizszego slotu, (1) -normlana, (2) -wieksza	
	public void takeFirstPriceVector(ArrayList<ArrayList<Float>> ListOfPriceVectors)
	{	
		getInput("takeFirstPriceVector in ProsumentEV");
		Point p1 = null;
		Point p2 = null;
		Point p3 = null;
		
		ArrayList<Float> priceVector = ListOfPriceVectors.get(1);
		ArrayList<Float> priceVectorSmallerMod= ListOfPriceVectors.get(0);
		ArrayList<Float> priceVectorBiggerMod= ListOfPriceVectors.get(2);
		
		OptimizerEV.Sterowanie sterowanie = wyznaczSterowanie(priceVector);
		
		
	}
	
	OptimizerEV.Sterowanie wyznaczSterowanie (ArrayList<Float> priceVector)
	{
		OptimizerEV.Sterowanie sterowanie = new OptimizerEV.Sterowanie();
		OptimizerEV.Form form =createForm24(priceVector);
		
		getInput("wyznacz sterownaie -s top");
		
		return sterowanie;
	}
	
	OptimizerEV.Form createForm24(ArrayList<Float> priceVector)
	{
		OptimizerEV.Form form = new OptimizerEV.Form();
		
		ArrayList<Float> generation = new ArrayList<>();
		ArrayList<Float> consumption = new ArrayList<>();
		ArrayList<Integer> statusy = new ArrayList<>();
		
		int i=0;
		while (i<Stale.horyzontCzasowy)
		{
			int indeks = i+LokalneCentrum.getTimeIndex();
			DayData d = dayDataList.get(indeks);
			EVData ev = eVDataList.get(indeks);
			
			consumption.add(d.getConsumption());
			generation.add(d.getTrueGeneration());
			
			statusy.add(ev.getStatus());
			
			
			i++;
		}
		
		form.setConsumption(consumption);
		form.setGeneration(generation);
		form.setStatusyEV(statusy);
		
		
		return form;
	}
	
	
	
	
}