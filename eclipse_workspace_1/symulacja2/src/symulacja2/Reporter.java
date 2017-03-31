package symulacja2;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

public class Reporter {
	
	Scanner sc = new Scanner (System.in);
	float reallySmallNumber = Stale.malaLiczba;
	
	//Folder paths
	String scenarioFolder;
	String predictionFolder;
	String handelFolder;
	
	//OTHER VARIABLES
	int liczbaProsumentow=Stale.liczbaProsumentow;
	
	//set by Lokalne Centrum
	String simulationEndDate;
	
	int scenariusz = Stale.scenariusz;
	
	String sectionSeparator="###";
	
	//ENABLE/DISABLE
	boolean enableSterowanieReport=true;
	boolean enableProsumentReport=true;
	boolean enablePriceVectorReport=true;
	boolean enableHandelReport=true;


	
	//STEROWANIE
	//ostatni utowrozny folder(sama nazwa) na potrzbey przechowywania sterownaia
	String sterowanieLastDay="";
	String sterowanieLastHour="";
	
	//POINT
	String pointLastDay="";
	String pointLastHour="";
	
	//HANDEL
	String handelLastDay="";
	String handelLastHour="";
	
	//Singleton shit
	private static Reporter instance = null;
	private Reporter() 
	{
	}
	
	public static Reporter getInstance() {
	      if(instance == null) {
	         instance = new Reporter();
	      }
	      return instance;
	}
	//------------------------
	
	
	//---------------------------
	//HANDEL
	
	//Point p
	//alfa - suma sprzedazy, beta -suma kupna, energia - suma 
	public void createHandelReport(Point point,ArrayList<Point> transactionList)
	{
		if (enableHandelReport)
		{	
			String day = LokalneCentrumDystrybucji2.getCurrentDay();
			String hour = LokalneCentrumDystrybucji2.getCurrentHour();
			String hourForFileName = sterowanieLastHour.replace(":", "_");

			
			createHandleFolders(day, hour);
			createHandelReportBody(point, transactionList, day,hourForFileName );
		}
	}
	
	void createHandleFolders(String day, String hour)
	{
		if (!handelLastDay.equals(day))
		{	
			handelLastDay=day;
			String pathToDayFolder =scenarioFolder+"\\handel\\handel_results\\"+day;
			File dir = new File(pathToDayFolder);
			dir.mkdir();	
		}
		
		
		if (!handelLastHour.equals(hour))
		{
			handelLastHour=hour;
			String hourForFileName = sterowanieLastHour.replace(":", "_");
			String pathToHourFolder =scenarioFolder+"\\handel\\handel_results\\"+day+"\\"+hourForFileName;
			File dir = new File(pathToHourFolder);
			dir.mkdir();

		}	
	}
	
	//Point p
	//alfa - suma sprzedazy, beta -suma kupna, energia - suma 
	void createHandelReportBody(Point point, ArrayList<Point> transactionList, String day, String hour)
	{
		//TODO fill this out
		String pathToFile =scenarioFolder+"\\handel\\handel_results\\"+day+"\\"+hour+"\\handel_results.csv";
		try {
			Writer writer = new FileWriter(pathToFile);
			writerWriteLine(writer, "day,"+day);
			writerWriteLine(writer, "hour,"+hour.replace("_", ":"));
			writerWriteLine(writer, "Chec sprzedazy,"+point.getAlfa());
			writerWriteLine(writer, "Chec kupna,"+point.getBeta());
			writerWriteLine(writer, "Wolumen handlu,"+point.getIloscEnergiiDoKupienia());
			writerWriteLine(writer, "");

			
			int a=0;
			while (a<transactionList.size())
			{
				Point prousmentPoint = transactionList.get(a);
				
				writerWriteLine(writer, "ID,"+(a+1));
				writerWriteLine(writer, "Wish,"+prousmentPoint.getAlfa());
				writerWriteLine(writer, "Result,"+prousmentPoint.getBeta());
				
				writerWriteLine(writer, "");
				a++;
			}
			
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		

		
	}
	
	public void setSimulationEndDate(String simulationEndDate)
	{
		this.simulationEndDate = simulationEndDate;
	}
	
	String getProsumentFolderPath(int ID)
	{
		return scenarioFolder+"\\"+(ID);
	}
	
	//TODO
	public void createFolders(String newlyCreatedFolder)
	{		
		//File dir = new File(newlyCreatedFolder);
		//dir.mkdir();
		
		scenarioFolder= newlyCreatedFolder;
		
		createFolderCascade(scenarioFolder);
		
		String handelFolder =newlyCreatedFolder+"\\handel";
		File dir = new File(handelFolder);
		dir.mkdir();
		
		String priceVectorFolder =scenarioFolder+"\\handel\\point";
		dir = new File(priceVectorFolder);
		dir.mkdir();
		
		String handelResultFolder =scenarioFolder+"\\handel\\handel_results";
		dir = new File(handelResultFolder);
		dir.mkdir();
		
		
		String agregateFolder =newlyCreatedFolder+"\\agregate";
		dir = new File(agregateFolder);
		dir.mkdir();
		
		
		
		int a=0;
		while (a<liczbaProsumentow)
		{
			//String folderProsumenta = newlyCreatedFolder+"\\"+(a+1);
			String folderProsumenta = getProsumentFolderPath(a+1);
			
			dir = new File(folderProsumenta);
			dir.mkdir();
			
			String folderProsumentaPredykcje = folderProsumenta+"\\predykcje";
			dir = new File(folderProsumentaPredykcje);
			dir.mkdir();
			
			
			a++;
		}
	}
	
	//do so that entire path path is created 
		void createFolderCascade(String path)
		{	
			String[] s2 = path.split("\\\\");
			
			//pelna sciezka do kolejnych podfolderow
			//jak nie isteniej stworz
			String folderPath="";
			
			int a=0;
			while (a<s2.length)
			{
				if (a>0)
				{
					folderPath+="\\";
				}
				folderPath+=s2[a];
				
				Path pathAsPath = Paths.get(folderPath);
				
				//jezeli folder nie istnieje to go stowrz
				if (!Files.exists(pathAsPath))
				{
					createFolder(folderPath);
				}
				
				a++;
			}
			
		}
		
		//create a single folder given by path
		void createFolder(String path)
		{
			File dir = new File(path);
			dir.mkdir();
			
			Path pathAsPath = Paths.get(path);
			if (!Files.exists(pathAsPath))
			{
				getInput("createFolder - couldnt cretae Folder");
			}
			
		}		
	
	public void createProsumentReport(Prosument2 prosument)
	{
		if (enableProsumentReport)
		{	
			createProsumentReportBody(prosument);
		}
	}
	
	
	void prosumentHeader(Writer writer, Prosument2 prosument)
	{
		try {
			writerWriteLine(writer,"scenariusz,"+scenariusz);
			writer.write("ID,"+prosument.getID()+System.lineSeparator());
			writer.write("Cena Zewnetrzna,"+Stale.cenaDystrybutoraZewnetrznego+System.lineSeparator());
			
			writerWriteLine(writer,"Cena Baterii,"+floatToString(Stale.kosztAmortyzacjiBaterii));
			writerWriteLine(writer,"Mnoznik,"+floatToString(prosument.getMnoznikGeneracji()));
			writerWriteLine(writer, "Koszt calkowity,"+floatToString(prosument.getTotalCost()));
			writerWriteLine(writer, "Caklowite zuzycie,"+floatToString(prosument.getTotalConsumption()));
			writerWriteLine(writer, "Calkowita generacja,"+floatToString(prosument.getTotalGeneration()));
			writerWriteLine(writer, "Unused generacja,"+floatToString(prosument.getTotalUnusedGeneration()));
			writerWriteLine(writer, "Reserve bonus,"+floatToString(prosument.getReserveBonus()));
			writerWriteLine(writer, "Report note,"+prosument.getReportNote());		



		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	void writerWriteLine(Writer writer, String s)
	{
		try {
			writer.write(s+System.lineSeparator());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	void addSeperator(Writer writer)
	{
		try {
			int a=0;
			while (a<6)
			{
				writerWriteLine(writer,"");
				a++;
			}
			
			writer.write("###"+System.lineSeparator());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//TODO
	void createProsumentReportBody(Prosument2 prosument)
	{
		
		String pathToFile =scenarioFolder+"\\"+"agregate"+"\\"+prosument.getID()+".csv";
		if (prosument.getID()<=liczbaProsumentow)
		{
			pathToFile =scenarioFolder+"\\"+prosument.getID()+"\\prosument_"+prosument.getID()+".csv";	
		}
		
		try {
			Writer writer = new FileWriter(pathToFile);
			
			prosumentHeader(writer, prosument);
			
			addSeperator(writer);
			addDataHeader(writer);
			
			ArrayList<DayData2> dList = prosument.getDayDataList();
			String dateAhead=dList.get(0).getDay()+" "+dList.get(0).getHour();
		
			int a=0;
			while (!dateAhead.equals(simulationEndDate))
			{
				
				wrtieDayData(writer,dList.get(a));
				a++;
				dateAhead=dList.get(a).getDay()+" "+dList.get(a).getHour();
			}
			
			writer.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	void addDataHeader(Writer writer)
	{
		String header1 ="Day,Hour,Konsumpcja,Generacja,Generacja(True),,stanBateriiNaPoczatkuSlotu,stanBateriiNaKoniecSlotu,,zBateriiNaKonsumpcje,zBateriiNaRynek,";
		String header2 ="zGeneracjiNaKonsumpcje,zGeneracjiNaRynek,zGeneracjiDoBaterii,,zRynekNaKonsumpcje,zRynekDoBaterii,,cenaLokalna,koszt,unusedGeneration,kupuj";
		
		
		try {
			writer.write(header1+","+header2+System.lineSeparator());
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
	
	String floatToString(float value)
	{
		return eToZero(""+value);
	}
	
	void wrtieDayData(Writer writer,DayData2 d)
	{
		String line = stringSeries(d.getDay(),d.getHour(),floatToString(d.getConsumption()),floatToString(d.getGeneration()),floatToString(d.getTrueGeneration()),"");
		line+=","+stringSeries(floatToString(d.getStanBateriiNaPoczatkuSlotu()),floatToString(d.getStanBateriiNaKoniecSlotu()),"",floatToString(d.getZBateriiNaKonsumpcje()),floatToString(d.getZBateriiNaRynek()),"");
		line+=","+stringSeries(floatToString(d.getZGeneracjiNaKonsumpcje()),floatToString(d.getZGeneracjiNaRynek()),floatToString(d.getZGeneracjiDoBaterii()),"");
		line+=","+stringSeries(floatToString(d.getZRynekNaKonsumpcje()),floatToString(d.getZRynekDoBaterii()),"");
		line+=","+stringSeries(floatToString(d.getCenaNaLokalnymRynku()),floatToString(d.getCost()),floatToString(d.getUnusedGeneration()),floatToString(d.getKupuj()) );
		
		
		try {
			writer.write(line+System.lineSeparator());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//used in createSterowanieReportBody
	void createSterowanieMergeDayData(ArrayList<DayData2> sterowanieForPriceVector, Prosument2 prosument, ArrayList<Float> priceVector )
	{
		int prosumentTime = prosument.getTimeIndex();
		ArrayList<DayData2> dList = prosument.getDayDataList();
		
		int a=0;
		while (a<sterowanieForPriceVector.size())
		{
			DayData2 d=sterowanieForPriceVector.get(a);
			DayData2 prosumentD = dList.get(prosumentTime+a);
			
			d.setDay(prosumentD.getDay());
			d.setHour(prosumentD.getHour());
			
			d.setCenaNaLokalnymRynku(priceVector.get(a));
			d.setConsumption(prosumentD.getConsumption());
			d.setGeneration(prosumentD.getGeneration());
			d.setTrueGeneration(prosumentD.getTrueGeneration());
			a++;
		}
		
	}
	
	void createPointFolders(String day, String hour)
	{
		if (!pointLastDay.equals(day))
		{	
			pointLastDay=day;
			String pathToDayFolder =scenarioFolder+"\\handel\\point\\"+day;
			File dir = new File(pathToDayFolder);
			dir.mkdir();	
		}
		
		
		if (!pointLastHour.equals(hour))
		{
			pointLastHour=hour;
			String hourForFileName = sterowanieLastHour.replace(":", "_");
			String pathToHourFolder =scenarioFolder+"\\handel\\point\\"+day+"\\"+hourForFileName;
			File dir = new File(pathToHourFolder);
			dir.mkdir();

		}		
	}
	
	void createPointHistoryReport(ArrayList<ArrayList<ArrayList<Point>>> historyListaFunkcjiUzytecznosci)
	{
		if (enablePriceVectorReport)
		{
			String day = LokalneCentrumDystrybucji2.getCurrentDay();
			String hour = LokalneCentrumDystrybucji2.getCurrentHour();
			createPointFolders(day,hour);
			createPointHistoryReportBody(day,hour, historyListaFunkcjiUzytecznosci);
		}
		//getInput("createPriceHistoryReport");
	}
	
	void createPointHistoryWritePointVector(Writer writer, ArrayList<Point> L1)
	{	
		String price="";
		String energia="";
		String alfa="";
		String beta="";
		
		
		int a=0;
		while (a<L1.size())
		{
			
			if (a>0)
			{
				price+=",";
				energia+=",";
				alfa+=",";
				beta+=",";
			}
			
			Point point = L1.get(a);
			
			price+=point.getPrice();
			energia+=point.getIloscEnergiiDoKupienia();
			alfa+=point.getAlfa();
			beta+=point.getBeta();
			
			a++;
		}
		
		writerWriteLine(writer, "price,"+price);
		writerWriteLine(writer, "energia,"+energia);
		writerWriteLine(writer, "alfa,"+alfa);
		writerWriteLine(writer, "beta,"+beta);
	}

	void createPointHistoryReportBody(String day, String hour, ArrayList<ArrayList<ArrayList<Point>>> historyListaFunkcjiUzytecznosci)
	{
		String hourForFileName = sterowanieLastHour.replace(":", "_");
		
		int a=0;
		while (a<historyListaFunkcjiUzytecznosci.size())
		{
			String pathToFile = scenarioFolder+"\\handel\\point\\"+day+"\\"+hourForFileName+"\\"+(a+1)+".csv";
			
			ArrayList<ArrayList<Point>> zbiorFunkcjiUzytecznosci = historyListaFunkcjiUzytecznosci.get(a);
			
			try {
				Writer writer = new FileWriter(pathToFile);
				
				int b=0;
				while (b<zbiorFunkcjiUzytecznosci.size())
				{
					if (b>0)
					{
						writerWriteLine(writer,"");
					}
					
					writerWriteLine(writer,""+b);
					createPointHistoryWritePointVector(writer,zbiorFunkcjiUzytecznosci.get(b));

					b++;
				}
				
				
				
				writer.close();
				
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			
			a++;
		}
		
	}
	
	void createSterowanieReportBody(ArrayList<DayData2> sterowanieForPriceVector, Prosument2 prosument,String additionalText, ArrayList<Float> priceVector, int iteration)
	{
		String hourForFileName = prosument.getCurrentHour();
		hourForFileName = hourForFileName.replace(":", "_");
		String pathToHourFolder =getProsumentFolderPath(prosument.getID())+"\\predykcje\\"+sterowanieLastDay+"\\"+hourForFileName+"\\"+iteration+additionalText+".csv";
		
				
		createSterowanieMergeDayData(sterowanieForPriceVector, prosument, priceVector);
		
		try {
			Writer writer = new FileWriter(pathToHourFolder);
			
			addDataHeader(writer);
			
			int a=0;
			while (a<sterowanieForPriceVector.size())
			{
				wrtieDayData(writer,sterowanieForPriceVector.get(a));
				a++;
			}
		

			writer.close();
		}
		catch (Exception e)
		{
			print("createSterowanieReportBody EXCEPTION");
		}
		
	};
	
	//utowrz foldery na potrzbey przechowywania sterownaia zarowno 
	//foldery dni jak i foldery godizn
	void createSterowanieFolders(String day, String hour)
	{
		if (!sterowanieLastDay.equals(day))
		{	
			sterowanieLastDay=day;
			int a=0;
			while (a<liczbaProsumentow)
			{
				String pathToDayFolder =getProsumentFolderPath(a+1)+"\\predykcje\\"+sterowanieLastDay;
				File dir = new File(pathToDayFolder);
				dir.mkdir();
				a++;
			}
		}
		
		if (!sterowanieLastHour.equals(hour))
		{
			sterowanieLastHour=hour;
			String hourForFileName = sterowanieLastHour.replace(":", "_");
			
			int a=0;
			while (a<liczbaProsumentow)
			{
				String pathToHourFolder =getProsumentFolderPath(a+1)+"\\predykcje\\"+sterowanieLastDay+"\\"+hourForFileName;
				File dir = new File(pathToHourFolder);
				dir.mkdir();
				a++;
			}

		}		
	}
	
	public void createSterowanieReport(ArrayList<DayData2> sterowanieForPriceVector, Prosument2 prosument,String additionalText, ArrayList<Float> priceVector, int iteration)
	{
		if (enableSterowanieReport)
		{
			
			createSterowanieFolders(prosument.getCurrentDay(),prosument.getCurrentHour());
			createSterowanieReportBody(sterowanieForPriceVector, prosument, additionalText,priceVector, iteration);
		}		
	}
	
	String eToZero(String s)
	{
		String output="";
		String[] s2 = s.split(",");
		int a=0;
		while (a<s2.length)
		{
			if (a>0)
			{
				output=output+",";
			}
			String s32 =s2[a];
			
			if (s32.contains("E"))
			{
				s32="0.0";
			}
			
			output=output+s32;
			a++;
		}
		
		return output;
	}
	
	String floatListToString(ArrayList<Float> L1)
	{
		String s="";
		int a=0;
		while (a<L1.size())
		{
			if (a>0)
			{
				s=s+",";
			}
			
			s=s+L1.get(a);
			a++;
		}
		
		return s;
	}
	
	//connect Strings into one comma seperated
	String stringSeries(String... args)
	{
		String s="";
		
		int a=0;
		while (a<args.length)
		{
			if (a>0)
			{
				s=s+",";
			}
			s=s+args[a];
			a++;
		}
		
		return s;
	}
	
	void print(int a)
	{
		print(((Integer)a).toString());
	}
	
	void print(String s)
	{
		System.out.println("Reporter "+s);
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
	
	//--------------------
	
	//tworyz plik z zapisem peirwszych cen, finalnych cen, i wolumenu handu
	public void reportPierwszeCeny(ArrayList<Float> pierwszeCeny, ArrayList<Float> finalneCeny, ArrayList<Float> wolumenHandlu )
	{
		
		String pathToFile=scenarioFolder+"\\handel\\pierwszeCeny.csv";
		try {
			Writer writer = new FileWriter(pathToFile);
			
			
			writerWriteLine(writer,"Scenraiusz,"+Stale.scenariusz+"\n");
			writerWriteLine(writer,reportPierwszeCenyHeader());

			
			int a=0;
			while (a<pierwszeCeny.size())
			{
				String s= stringSeries(eToZero(pierwszeCeny.get(a)+""),eToZero(finalneCeny.get(a)+""),eToZero(wolumenHandlu.get(a)+""));
				
				writerWriteLine(writer,s);
				a++;
			}
			
			
			writerWriteLine(writer,"\n"+sectionSeparator);
			writerWriteLine(writer,reportPierwszeCenyHeader());

			a=0;
			while (a<pierwszeCeny.size())
			{
				if (wolumenHandlu.get(a)!=0)
				{
					String s= stringSeries(eToZero(pierwszeCeny.get(a)+""),eToZero(finalneCeny.get(a)+""),eToZero(wolumenHandlu.get(a)+""));
					writerWriteLine(writer,s);
				}
				
				a++;
			}

			
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	String reportPierwszeCenyHeader()
	{
		return "predykcja,finalna,wolumen";
	}
	
	
}
