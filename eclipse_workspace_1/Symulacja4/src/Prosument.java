import java.util.ArrayList;

public class Prosument extends CoreClass {
	
	protected ArrayList<DayData> dayDataList = new ArrayList<DayData>();

	protected int ID;
	//active user has: EV, battery or generation
	private Boolean active=false;
	
	
	double mnoznikGeneracji=0;

	
	double pojemnoscBaterii =0;//faktyczna pojemnsoc baterii
	double predkoscBaterii = 0; //ile energii moze przekazac bateria w trakcie slotu czasowego
	
	String reportNote="";
	
	//Singletony
	Loader loader = Loader.getInstance();
	
	//KOSZTY
	
		//calkowity koszt jaki poniesie prosument (z uwzglednieniem ulg za niewykrozystana baterie)
		double totalCost=0;
		
		//jako test (w agregacie powinno byc rowne 0)
		double kosztHandlu=0;
		
		double costNoReserve=0;
		
		//zwrot za energie zromadozna w magazynie i niewykoryzstana
		double reserveBonus=0;
		
	
	//-------------------------------
	
	
		
	public int getID() {
		return ID;
	}
	
	public String getReportNote() {
		return reportNote;
	}

	public void setReportNote(String reportNote) {
		this.reportNote = reportNote;
	}

	public double getReserveBonus() {
		return reserveBonus;
	}

	public void setReserveBonus(double reserveBonus) {
		this.reserveBonus = reserveBonus;
	}

	public double getKosztHandlu() {
		return kosztHandlu;
	}

	public void setKosztHandlu(double kosztHandlu) {
		this.kosztHandlu = kosztHandlu;
	}

	public double getCostNoReserve() {
		return costNoReserve;
	}

	public void setCostNoReserve(double costNoReserve) {
		this.costNoReserve = costNoReserve;
	}

	public void setID(int iD) {
		ID = iD;
	}
	
	public double getTotalCost()
	{
		return this.totalCost;
	}
	
	//--------------
	
	public void loadData()
	{
		dayDataList =loader.loadProsument(ID);
	}
	
	float obliczCalkowitaGeneracja()
	{
		float sum=0f;
		
		int a=0;
		DayData d =dayDataList.get(a);
		String date=d.getDayHour();
		
		while (!date.equals(Stale.simulationEndDate))
		{
			d =dayDataList.get(a);
			sum+=d.getGeneration();
			
			a++;
			date = dayDataList.get(a).getDayHour();
		}
		
		return sum;
	}
	
	float obliczCalkowitaKonsumpcje()
	{
		float sum=0f;
		
		int a=0;
		DayData d =dayDataList.get(a);
		String date=d.getDayHour();
		
		while (!date.equals(Stale.simulationEndDate))
		{
			d =dayDataList.get(a);
			sum+=d.getConsumption();
			
			a++;
			date = dayDataList.get(a).getDayHour();
		}
		return sum;
	}
	
	public void aktywujBaterie()
	{
		pojemnoscBaterii=Stale.pojemnoscBateriiIfEnabled;
		predkoscBaterii = Stale.predkoscBaterii;
		active=true;
	}
	
	public void setMnoznikGeneracji(double mnoznikGeneracji2)
	{
		this.mnoznikGeneracji=mnoznikGeneracji2;
		
		if (mnoznikGeneracji2>0)
		{
			active=true;
		}
		
		int a=0;
		while (a<dayDataList.size())
		{
			dayDataList.get(a).countTrueGeneration(mnoznikGeneracji);
			a++;
		}
	}
	
	public void przesunGeneracje()
	{
		//lista z generacja przesunieta o 12 godzin
		ArrayList<Double> L1_trueGeneration = new ArrayList<>();
		ArrayList<Double> L1_Generation = new ArrayList<>();
		
		//o ile goidzn przesunac
		int shiftValue=12;
		int a=12;
		DayData d2 =dayDataList.get(a);
		
		Boolean endDateReached = false;
		
		while (!endDateReached)
		{
			d2 =dayDataList.get(a);
			String dayHourString = d2.getDay()+" "+d2.getHour();
			
			if (dayHourString.equals(Stale.simulationEndDate))
			{
				endDateReached=true;
			}
			
			if (!endDateReached)
			{
				L1_trueGeneration.add(d2.getTrueGeneration());
				L1_Generation.add(d2.getGeneration());

			}
			
			a++;
		}
		
		a=0;
		while (a<shiftValue)
		{
			d2 =dayDataList.get(a);
			L1_trueGeneration.add(d2.getTrueGeneration());
			L1_Generation.add(d2.getGeneration());
			a++;
		}
		
		przesunGeneracjeWstrzyknijlisty(L1_Generation,L1_trueGeneration);					
	}
		
	//przypisuje wartosci z list jako generation i trueGeneration do dayDataList
	void przesunGeneracjeWstrzyknijlisty(ArrayList<Double> generationList, ArrayList<Double> trueGenerationList)
	{
		int a=0;
		while (a<generationList.size())
		{
			DayData d = dayDataList.get(a);
			d.setGeneration(generationList.get(a));
			d.setTrueGeneration(trueGenerationList.get(a));
			
			a++;
		}
	}
	
	
	public void performEndOfSimulationCalculations()
	{
		print("performEndOfSimulationCalculations remians to be done");
	}

	public ArrayList getDayDataList() {
		return this.dayDataList;
	}
	
	//buduje pusta liste DayData skaldajaca o dlugosci size 
	public void createEmptyDataList(int size)
	{	
		int a=0;
		while (a<size)
		{
			DayData d = new DayData();
			dayDataList.add(d);
			a++;
		}
	}
	
	
	//dodaj DayDaty z DayData list prosumenta2 do this 
	//ID for debug only
	public void addProsument(Prosument prosument2,int ID)
	{		
		ArrayList<DayData> dayDataList = this.getDayDataList();
		ArrayList<DayData> dayDataList2 = prosument2.getDayDataList();

		int a=0;
		while (a<dayDataList.size())
		{
			DayData dayData = dayDataList.get(a);
			DayData dayData2 = dayDataList2.get(a);
			
			dayData.addDayData(dayData2,ID,a);
	
			a++;
		}
	}
	
	public void zaktualizujHandelBrakHandlu()
	{
		
		getInput("zaktualizujHandelBrakHandlu - fill this out");
		
		/*
		DayData dayData2 = dayDataList.get(LokalneCentrum.getTimeIndex());
		
		float generation = dayData2.getTrueGeneration();
		float consumption = dayData2.getConsumption();
		
		if (generation>consumption)
		{
			
			float generacjaDoBaterii = generation-dayData2.getConsumption();
			
			dayData2.setZGeneracjiNaKonsumpcje(dayData2.getConsumption());
			dayData2.chargeBatteryZGeneracji(generacjaDoBaterii,predkoscBaterii,pojemnoscBaterii);
			
		}
		else
		{
			dayData2.setZGeneracjiNaKonsumpcje(generation);
			float nieZaspokojonaKonsumpcja = dayData2.getConsumption()-generation;
			dayData2.dischargeBatteryNaKonsumpcje(nieZaspokojonaKonsumpcja,predkoscBaterii);
		}
		
		dayData2.obliczStanBateriiNaKoniecSlotu();*/
	}
	
	
}