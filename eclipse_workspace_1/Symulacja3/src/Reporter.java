import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;


public class Reporter extends CoreClass {
	
	String simulationEndDate =Stale.simulationEndDate;
	
	//ustawiane przez LokalenCentrum
	private String scenarioFolder;

	private int liczbaProsumentow=Stale.liczbaProsumentow;
	String sectionSeparator="###";



	
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


	
	//ENABLE/DISABLE
	boolean enableSterowanieReport=true;
	boolean enableProsumentReport=true;
	boolean enablePriceVectorReport=true;
	boolean enableHandelReport=true;
	
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
	
	//TODO
	//---------------------
	//OTHER FUNCTIONS
	
	public void createFolders(String newlyCreatedFolder)
	{	
		scenarioFolder= newlyCreatedFolder;

		createFolderCascade(scenarioFolder);
		
		
		String handelFolder =newlyCreatedFolder+"\\handel";
		createFolder(handelFolder);
		
		String priceVectorFolder =scenarioFolder+"\\handel\\point";
		createFolder(priceVectorFolder);
		
		
		String handelResultFolder =scenarioFolder+"\\handel\\handel_results";
		createFolder(handelResultFolder);
		
		
		String agregateFolder =newlyCreatedFolder+"\\agregate";
		createFolder(agregateFolder);
		
		
		int a=0;
		while (a<liczbaProsumentow)
		{
			
			String folderProsumenta = getProsumentFolderPath(a+1);
			createFolder(folderProsumenta);
			
			String folderProsumentaPredykcje = folderProsumenta+"\\predykcje";
			createFolder(folderProsumentaPredykcje);
			
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
	
	String getProsumentFolderPath(int ID)
	{
		return scenarioFolder+"\\"+(ID);
	}
	
	public void createProsumentReport(Prosument prosument)
	{
		if (enableProsumentReport)
		{	
			createProsumentReportBody(prosument);
		}
	}
	
	void prosumentHeader(Writer writer, Prosument prosument)
	{
		try {
			writerWriteLine(writer,"scenariusz,"+Stale.scenariusz);
			writerWriteLine(writer,"ceny z generatora,"+Stale.cenyZGeneratora);

			writer.write("ID,"+prosument.getID()+System.lineSeparator());
			writer.write("Cena Zewnetrzna,"+Stale.cenaDystrybutoraZewnetrznego+System.lineSeparator());
			
			writerWriteLine(writer,"Cena Baterii,"+floatToString(Stale.kosztAmortyzacjiBaterii));
			writerWriteLine(writer,"Mnoznik,"+floatToString(prosument.getMnoznikGeneracji()));
			writerWriteLine(writer, "Koszt calkowity,"+floatToString(prosument.getTotalCost()));
			writerWriteLine(writer, "Caklowite zuzycie,"+floatToString(prosument.getTotalConsumption()));
			writerWriteLine(writer, "Calkowita generacja,"+floatToString(prosument.getTotalGeneration()));
			writerWriteLine(writer, "Unused generacja,"+floatToString(prosument.getTotalUnusedGeneration()));
			writerWriteLine(writer, "Reserve bonus,"+floatToString(prosument.getReserveBonus()));
			writerWriteLine(writer, "Cost no reserve,"+prosument.getCostNoReserve());		
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
	void createProsumentReportBody(Prosument prosument)
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
			
			ArrayList<DayData> dList = prosument.getDayDataList();
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
	
	void wrtieDayData(Writer writer,DayData d)
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
	
	//TODO
	void createSterowanieReportBody(ArrayList<DayData> sterowanieForPriceVector, Prosument prosument,String additionalText, ArrayList<Float> priceVector, int iteration)
	{
		String hourForFileName = LokalneCentrum.getCurrentHour();
		hourForFileName = hourForFileName.replace(":", "_");
		String pathToHourFolder =getProsumentFolderPath(prosument.getID())+"\\predykcje\\"+sterowanieLastDay+"\\"+hourForFileName+"\\"+iteration+"_"+additionalText+".csv";
		
				
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
	
	public void createSterowanieReport(ArrayList<DayData> sterowanieForPriceVector, Prosument prosument,String additionalText, ArrayList<Float> priceVector, int iteration)
	{
		if (enableSterowanieReport)
		{
			
			createSterowanieFolders(LokalneCentrum.getCurrentDay(),LokalneCentrum.getCurrentHour());
			createSterowanieReportBody(sterowanieForPriceVector, prosument, additionalText,priceVector, iteration);
		}		
	}
	
	//used in createSterowanieReportBody
	void createSterowanieMergeDayData(ArrayList<DayData> sterowanieForPriceVector, Prosument prosument, ArrayList<Float> priceVector )
	{
		int prosumentTime = LokalneCentrum.getTimeIndex();
		ArrayList<DayData> dList = prosument.getDayDataList();
		
		int a=0;
		while (a<sterowanieForPriceVector.size())
		{
			DayData d=sterowanieForPriceVector.get(a);
			DayData prosumentD = dList.get(prosumentTime+a);
			
			d.setDay(prosumentD.getDay());
			d.setHour(prosumentD.getHour());
			
			d.setCenaNaLokalnymRynku(priceVector.get(a));
			d.setConsumption(prosumentD.getConsumption());
			d.setGeneration(prosumentD.getGeneration());
			d.setTrueGeneration(prosumentD.getTrueGeneration());
			a++;
		}
		
	}
	
	//indeks w lsicie nie prosumenta
	public void createPointHistoryReportVer2(ArrayList<ArrayList<ArrayList<Point>>> historyListaFunkcjiUzytecznosci, int indeks)
	{
		String day = LokalneCentrum.getCurrentDay();
		String hour = getHourForPath();
		
		String pathToFile = scenarioFolder+"\\handel\\point\\"+day+"\\"+hour;
		
		createFolderCascade(pathToFile);
		
		pathToFile+="\\"+(indeks+1)+".csv";
		
		try
		{
			Writer writer = new FileWriter(pathToFile);
			
			ArrayList<ArrayList<Point>> zbiorFunkcjiUzytecznosci = historyListaFunkcjiUzytecznosci.get(indeks);
			//print (zbiorFunkcjiUzytecznosci.size());
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
		} catch (Exception e)
		{
			e.printStackTrace();
			getInput("Error in createPointHistoryReportVer2");
		}
	
	}
	
	//wez godzine z lokalnego centurm i przetworz do postaci sciezki
	String getHourForPath()
	{
		String hour = LokalneCentrum.getCurrentHour();
		
		if (hour.contains(":"))
		{
			hour=hour.replace(":", "_");
		}
		
		return hour;
	}
	
	void createPointHistoryReport(ArrayList<ArrayList<ArrayList<Point>>> historyListaFunkcjiUzytecznosci)
	{
		if (enablePriceVectorReport)
		{
			String day = LokalneCentrum.getCurrentDay();
			String hour = LokalneCentrum.getCurrentHour();
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
	
	//---------------------------
		//HANDEL
		
	//Point p
	//alfa - suma sprzedazy, beta -suma kupna, energia - suma 
	public void createHandelReport(Point point,ArrayList<Point> transactionList)
	{
		if (enableHandelReport)
		{	
			String day = LokalneCentrum.getCurrentDay();
			String hour = LokalneCentrum.getCurrentHour();
			
			//print("createHandelReport "+hour);
			//getInput("createHandelReport stop");
			
			String hourForFileName = hour.replace(":", "_");

			
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
		
		String pathToFile =scenarioFolder+"\\handel\\handel_results\\"+day+"\\"+hour+"\\handel_results.csv";
		
		String folder =getfolderName(pathToFile);
		createFolderCascade(folder);
		
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
	
	//tworyz plik z zapisem peirwszych cen, finalnych cen, i wolumenu handu
	//status wyjscia 0- normalny handel, 1/-1 handelprzerwany bo funkcjaRynku pwoyzje/ponizej osi ox
	public void reportPierwszeCeny(ArrayList<Float> pierwszeCeny, ArrayList<Float> finalneCeny, ArrayList<Float> wolumenHandlu, ArrayList<Integer> statusWyjscia)
	{
		ArrayList<String> dayHourList =LokalneCentrum.getHourList();

		
		String pathToFile=scenarioFolder+"\\handel\\pierwszeCeny_"+Stale.scenariusz+".csv";
		try {
			Writer writer = new FileWriter(pathToFile);
			
			
			writerWriteLine(writer,"Scenraiusz,"+Stale.scenariusz+"\n");
			writerWriteLine(writer,reportPierwszeCenyHeader());

			
			int a=0;
			while (a<pierwszeCeny.size())
			{
				String dayHour =dayHourList.get(a);
				String day = dayHourGetDay(dayHour);
				String hour = dayHourGetHour(dayHour);
								
				String s= stringSeries(day,hour,
						eToZero(pierwszeCeny.get(a)+""),eToZero(finalneCeny.get(a)+""),
						eToZero(wolumenHandlu.get(a)+""),statusWyjscia.get(a)+"");
				
				writerWriteLine(writer,s);
				a++;
			}
			
			
			//wydrukuj ceny z wolumenem roznym od zera
			writerWriteLine(writer,"\n"+sectionSeparator);
			writerWriteLine(writer,reportPierwszeCenyHeader());

			a=0;
			while (a<pierwszeCeny.size())
			{
				//print(a);
				//print(wolumenHandlu.get(a));
				
				if (wolumenHandlu.get(a)!=0)
				{
					String dayHour =dayHourList.get(a);
					String day = dayHourGetDay(dayHour);
					String hour = dayHourGetHour(dayHour);
					String s= stringSeries(day,hour,
							eToZero(pierwszeCeny.get(a)+""),eToZero(finalneCeny.get(a)+""),
							eToZero(wolumenHandlu.get(a)+""),statusWyjscia.get(a)+"");
					
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
		return "dzien,godzina,predykcja,finalna,wolumen,statusWyjscia";
	}	
	
	String dayHourGetDay(String s)
	{
		String[] s2 = s.split(" ");
		return s2[0];
	}
	
	String dayHourGetHour(String s)
	{
		String[] s2 = s.split(" ");
		return s2[1];
	}

	//tworzy raport postaci cena, wartosic funcki rynku
	public void createFunkcjeKontraktyReport(ArrayList<RynekHistory.kontraktElement> kontraktList)
	{
		String day = LokalneCentrum.getCurrentDay();
		String hour = LokalneCentrum.getCurrentHour();
		
		createHandleFolders(day, hour);
		createFunkcjeKontraktyReportBody(day, hour,kontraktList);
		
	}
	
	
	String createFunkcjeKontraktyHeader()
	{
		return "cena,funckjaRynku,sprzedaz,kupno";
	}
	
	void createFunkcjeKontraktyReportBody( String day, String hour,ArrayList<RynekHistory.kontraktElement > ktList)
	{
		
		hour = hour.replace(":","_");
		
		String pathToFile =scenarioFolder+"\\handel\\handel_results\\"+day+"\\"+hour+"\\funkcjeRynku.csv";
		try {
			Writer writer = new FileWriter(pathToFile);
			
			writerWriteLine(writer,"Day,"+day );
			writerWriteLine(writer,"Hour,"+hour.replace("_", ":") );
			writerWriteLine(writer,"" );

			
			writerWriteLine(writer,sectionSeparator );
			writerWriteLine(writer,createFunkcjeKontraktyHeader() );

			
			int i=0;
			while (i<ktList.size())
			{
				RynekHistory.kontraktElement kt = ktList.get(i);
				writerWriteLine(writer,stringSeries(kt.cena+"",kt.wartoscFunkcji+"",kt.sumaSprzedazy+"",kt.sumaKupna+"") );

				i++;
			}
			
			writer.close();
		}
		catch (IOException e) {
			print("createFunkcjeKontraktyReportBody excpetion");
			e.printStackTrace();
		}
		
	}
	
	
	public void createSterowanieReport(ProsumentEV prosumentEV, OptimizerEV.Sterowanie sterowanie,String additionalText, int iteration)
	{
		
		String day = LokalneCentrum.getCurrentDay();
		String hourForFileName = LokalneCentrum.getCurrentHour();
		hourForFileName = hourForFileName.replace(":", "_");
		String pathToHourFolder =getProsumentFolderPath(prosumentEV.getID())+"\\predykcje\\"+day+"\\"+hourForFileName+"\\"+iteration+"_"+additionalText+".csv";
		
		//print (pathToHourFolder);
		
		GridReport gridReport = new GridReport();
		gridReport.setPathToFile(pathToHourFolder);
		
		createSterowanieHeader(gridReport,additionalText, iteration,prosumentEV);
		
		
		
		createSterowanieDataHeader(gridReport);
		
		createSterowanieFillOutReport(sterowanie, gridReport);
		
		
		gridReport.dropToFile();
		
		
		//getInput("createSterowanieReport -end");
	}
	
	//TODO
	void createSterowanieFillOutReport(OptimizerEV.Sterowanie sterowanie, GridReport gridReport)
	{
		ArrayList<DayData> dList= sterowanie.getDList();
		ArrayList<EVData> eVList= sterowanie.getEvList();
		
		//czesc 
		createSterowanieFillOutReportDayDataLoop(gridReport,dList );
		createSterowanieFillOutReportEVDataLoop(gridReport, eVList );

	}
	
	void createSterowanieFillOutReportEVDataLoop(GridReport gridReport, ArrayList<EVData> eVList )
	{		
		int i=0;
		while (i<eVList.size())
		{
			EVData ev = eVList.get(i);
			
			gridReport.addDataToTable("EV",ev.getEV());
			gridReport.addDataToTable("EB_EV",ev.getEB_EV());
			gridReport.addDataToTable("EV_EB",ev.getEV_EB());
			gridReport.addDataToTable("Zew_EV",ev.getZew_EV());
			gridReport.addDataToTable("G_EV",ev.getG_EV());
			gridReport.addDataToTable("EV_c",ev.getEV_c());
			
			gridReport.addDataToTable("EV_EM",ev.getEV_EM());			
			gridReport.addDataToTable("EM_EV",ev.getEM_EV());
			
			gridReport.addDataToTable("EVbinKupuj",ev.getEVbinKupuj());
			gridReport.addDataToTable("Status",ev.getStatus());
			
			gridReport.addDataToTable("koszt(opt)",ev.getKoszt());
			gridReport.addDataToTable("koszt_Zew",ev.getKoszt_Zew());
			gridReport.addDataToTable("koszt_sklad",ev.getKoszt_sklad());
			gridReport.addDataToTable("koszt_EV",ev.getKoszt_EV());
			gridReport.addDataToTable("koszt_handel",ev.getKoszt_handel());
			
			i++;
		}
		
	}
	
	void createSterowanieFillOutReportDayDataLoop(GridReport gridReport, ArrayList<DayData> dList )
	{
		int currentTime = LokalneCentrum.getTimeIndex();
		
		int i=0;
		while (i<dList.size())
		{
			DayData d = dList.get(i);
			
			gridReport.addDataToTable("Day",d.getDay());
			gridReport.addDataToTable("Hour",d.getHour());
			
			gridReport.addDataToTable("Konsumpcja",d.getConsumption());
			gridReport.addDataToTable("Generacja(True)",d.getTrueGeneration());
			
			
			gridReport.addDataToTable("stanBateriiNaPoczatkuSlotu",d.getStanBateriiNaPoczatkuSlotu());
			gridReport.addDataToTable("stanBateriiNaKoniecSlotu",d.getStanBateriiNaKoniecSlotu());

			gridReport.addDataToTable("zBateriiNaKonsumpcje",d.getZBateriiNaKonsumpcje());
			gridReport.addDataToTable("zBateriiNaRynek",d.getZBateriiNaRynek());
			
			gridReport.addDataToTable("zGeneracjiNaKonsumpcje",d.getZGeneracjiNaKonsumpcje());
			gridReport.addDataToTable("zGeneracjiNaRynek",d.getZGeneracjiNaRynek());			
			gridReport.addDataToTable("zGeneracjiDoBaterii",d.getZGeneracjiDoBaterii());
			
			gridReport.addDataToTable("zRynekNaKonsumpcje",d.getZRynekNaKonsumpcje());
			gridReport.addDataToTable("zRynekDoBaterii",d.getZRynekDoBaterii());
			
			gridReport.addDataToTable("cenaLokalna",d.getCenaNaLokalnymRynku());
			gridReport.addDataToTable("koszt",d.getCost());
			gridReport.addDataToTable("kupuj",d.getKupuj());
			


			i++;
		}
	}
	
	
	
	void createSterowanieHeader(GridReport gridReport,String additionalText, int iteration,ProsumentEV prosumentEV)
	{
		gridReport.addToHeader("Day", LokalneCentrum.getCurrentDay());
		gridReport.addToHeader("hour", LokalneCentrum.getCurrentHour());
		
		gridReport.addToHeader("iteration", iteration);
		gridReport.addToHeader("note", additionalText);
		gridReport.addToHeader("ID", prosumentEV.getID());
		gridReport.addToHeader("Scenariusz", Stale.scenariusz);


	}
	
	void createSterowanieDataHeader(GridReport gridReport)
	{
		gridReport.addDataHeader("Day");
		gridReport.addDataHeader("Hour");
		gridReport.addDataHeader("Konsumpcja");
		gridReport.addDataHeader("Generacja");
		gridReport.addDataHeader("Generacja(True)",1);
		
		gridReport.addDataHeader("stanBateriiNaPoczatkuSlotu");
		gridReport.addDataHeader("stanBateriiNaKoniecSlotu",1);
		
		gridReport.addDataHeader("zBateriiNaKonsumpcje");
		gridReport.addDataHeader("zBateriiNaRynek",1);
		
		gridReport.addDataHeader("zGeneracjiNaKonsumpcje");
		gridReport.addDataHeader("zGeneracjiNaRynek");
		gridReport.addDataHeader("zGeneracjiDoBaterii",1);
		
		gridReport.addDataHeader("zRynekNaKonsumpcje");
		gridReport.addDataHeader("zRynekDoBaterii",1);
		
		gridReport.addDataHeader("cenaLokalna");
		gridReport.addDataHeader("koszt");
		gridReport.addDataHeader("kupuj",1);
		
		gridReport.addDataHeader("EV");
		gridReport.addDataHeader("EB_EV");
		gridReport.addDataHeader("EV_EB");
		gridReport.addDataHeader("Zew_EV");
		
		gridReport.addDataHeader("G_EV");
		gridReport.addDataHeader("EV_c");
		gridReport.addDataHeader("EV_EM");
		gridReport.addDataHeader("EM_EV");
		gridReport.addDataHeader("EVbinKupuj");
		gridReport.addDataHeader("Status",1);
		

		gridReport.addDataHeader("koszt(opt)");
		gridReport.addDataHeader("koszt_Zew");
		gridReport.addDataHeader("koszt_sklad");
		gridReport.addDataHeader("koszt_EV");
		gridReport.addDataHeader("koszt_handel");
		
	}
	
	//wyskrobuje ze sciezki nazwe pliku
	String getfolderName(String pathToFile)
	{
		//print("getfolderName "+pathToFile);
		
		String[] s2 =pathToFile.split("\\\\");
		String output="";
		
		int i=0;
		while (i<s2.length-1)
		{
			if (i>0)
			{
				output+="\\";
			}
			output+=s2[i];
			//print("getfolderName " +s2[i]);
			i++;
		}
		
		//output = output.substring(0,output.length()-1);
		
		return output;
	}
	
	//TODO
	public void createProsumentReport(ProsumentEV prosumentEV)
	{
		
		String pathToFile =scenarioFolder+"\\"+"agregate"+"\\"+prosumentEV.getID()+".csv";
		if (prosumentEV.getID()<=liczbaProsumentow)
		{
			pathToFile =scenarioFolder+"\\"+prosumentEV.getID()+"\\prosument_"+prosumentEV.getID()+".csv";	
		}
		
		GridReport gridReport = new GridReport();
		gridReport.setPathToFile(pathToFile);
		
		createProsumentReportHeader(gridReport, prosumentEV);
		
		//Data Header tkai sam jak w raportach sterowania
		createSterowanieDataHeader(gridReport);
		createProsumentFillOutReport(prosumentEV, gridReport);
		
		gridReport.dropToFile();
			
		
	}
	
	void createProsumentFillOutReport(ProsumentEV prosumentEV, GridReport gridReport)
	{
		//+1 bo chcemy wziac eleementy z data == Stale.endDate, +1 za to ze liczba eleemetnow,a nei indeks ostatniego eleementu
		int liczbaElemetnow =LokalneCentrum.getTimeIndex()+2;
		
		ArrayList<DayData> dList=  getNFirstFromList(prosumentEV.getDayDataList(),liczbaElemetnow);
		ArrayList<EVData> eVList=  getNFirstFromList(prosumentEV.getEVDataList(),liczbaElemetnow);
		
		//createSterowanieFillOutReportDayDataLoop dodaje wssystkie elementy z lsity wic trzeba najpeirw list eprzyciac 
		createSterowanieFillOutReportDayDataLoop(gridReport,dList );
		createSterowanieFillOutReportEVDataLoop(gridReport, eVList );
	}
	

	
	void createProsumentReportHeader(GridReport gridReport,ProsumentEV prosumentEV)
	{
		gridReport.addToHeader("Scenariusz",Stale.scenariusz );
		gridReport.addToHeader("ceny z generatora",Stale.cenyZGeneratora );
		gridReport.addToHeader("ID", prosumentEV.getID());
		gridReport.addToHeader("Koszt calkowity", prosumentEV.getTotalCost());
		gridReport.addToHeader("Cost no reserve", prosumentEV.getCostNoReserve());
		gridReport.addToHeader("Reserve", prosumentEV.getReserveBonus());
		gridReport.addToHeader("Report note", prosumentEV.getReportNote());
		
		

	}
	
	//-----------------------------------
	//klasa reprezentuje rpaort w postaci: header (klucz wartosc), searator, header tabeli dnaych, tabela dnaych	
	class GridReport
	{
		int headerLiczbaLinii =20;
		int dataLines=0;
		String pathToFile;
		
		String separator="###";
		
		//postaci key-value
		ArrayList<String> headerList= new ArrayList<>();
		
		//nazwy kolumn w tabeli
		ArrayList<String> dataHeaderList= new ArrayList<>();
		
		//wartosci w tabeli ulozone kolumnami
		ArrayList<ArrayList<String>> dataList= new ArrayList<>();

		//okresla ile spacji wstawic po kazdej kolumnie
		ArrayList<Integer> dataHeaderSpacesList= new ArrayList<>();
		
		String getColumns()
		{
			String s="";
			
			int i=0;
			while (i<dataHeaderList.size())
			{
				s+=dataHeaderList.get(i)+", ";
				i++;
			}
			
			return s;
		}
		
		
		
		public void addDataToTable(String columnName, Integer value)
		{
			addDataToTable(columnName, value+"");
		}
		
		public void addDataToTable(String columnName, Float float1)
		{
			String s = String.format("%.8f", float1);
			addDataToTable(columnName, s);
		}
		
		public void addDataToTable(String columnName, double double1)
		{
			String s = String.format("%.8f", double1);
			addDataToTable(columnName, s);
		}
		
		public void addDataToTable(String columnName, String value)
		{
			int index = dataHeaderList.indexOf(columnName);
			
			if (index==-1)
			{
				print(columnName +" is does not exist in table");
				print("available columns "+getColumns());
				getInput("ERROR in addDataToTable- no such column");
			}
			else
			{
				dataList.get(index).add(value);
			}
			
		}
		
		public void addDataHeader(String value)
		{
			addDataHeader(value,0);
		}
		
		public void addDataHeader(String value,Integer spacesFollowing)
		{
			//index sprawdza czy kolumna znajduje sie w liscie kolumn
			int index = dataHeaderList.indexOf(value);
			
			if (index==-1)
			{
				dataHeaderList.add(value);
				dataHeaderSpacesList.add(spacesFollowing);
				dataList.add(new ArrayList<String>());
			}
			else
			{
				getInput("ERROR in addDataHeader - koluman "+value+" already exists");
			}
		}
		
		public void addToHeader(String key, Float value)
		{
			headerList.add(key+","+value);
		}
		
		public void addToHeader(String key, Boolean value)
		{
			headerList.add(key+","+value);
		}
		
		public void addToHeader(String key, String value)
		{
			headerList.add(key+","+value);
		}
		
		public void addToHeader(String key, int value)
		{
			headerList.add(key+","+value);
		}
		
		public void setPathToFile(String pathToFile)
		{
			this.pathToFile =pathToFile;
		}
		
		
		String getfolderName()
		{
			String[] s2 =pathToFile.split("\\\\");
			String output="";
			
			int i=0;
			while (i<s2.length-1)
			{
				if (i>0)
				{
					output+="\\";
				}
				output+=s2[i];
				//print("getfolderName " +s2[i]);
				i++;
			}
			
			return output;
		}
		
		public void dropToFile()
		{
			String folder =getfolderName();
			createFolderCascade(folder);
			
			Writer writer;
			try {
				writer = new FileWriter(pathToFile);
				dropHeader(writer);
				dropDataHeader(writer);
				dropData(writer);
				
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
				getInput("ERROR in dropToFile");
			}

			
			//print ("dropToFile -end");
			
			
		}
		
		void dropData(Writer writer)
		{
			//oblicz ile jest linii w tabeli
			countDataLines();
			int i=0;
			while (i<dataLines)
			{
				dropLine(writer, i);
				i++;
			}
		}
		
		void dropLine(Writer writer, int numerLinii)
		{
			String line="";
			int i=0;
			while (i<dataList.size())
			{
				ArrayList<String> kolumna =dataList.get(i);
				
				String toBeAdded="null";
				if (numerLinii < kolumna.size())
				{
					toBeAdded=kolumna.get(numerLinii);
				}
				
				line+=toBeAdded+","+csvSpace(dataHeaderSpacesList.get(i));
				i++;
			}
			
			
			writerWriteLine(writer,line);
		}
		
		void countDataLines()
		{
			int i=0;
			while (i<headerList.size())
			{
				int numberOflines=dataList.get(i).size();
				
				if (numberOflines>dataLines)
				{
					dataLines=numberOflines;
				}
				i++;
			}
		}
		
		void dropHeader(Writer writer)
		{
			int i=0;
			while (i<headerLiczbaLinii)
			{
				if (i<headerList.size())
				{
					writerWriteLine(writer,headerList.get(i));
				}
				else
				{
					writerWriteLine(writer,"");
				}
				i++;
			}
			
			writerWriteLine(writer,separator);
		}
		
		String csvSpace(int number)
		{
			String s="";
			int i=0;
			while (i<number)
			{
				s+=",";
				i++;
			}
			return s;
			
		}
		
		void dropDataHeader(Writer writer)
		{
			//ArrayList<String> dataHeaderList= new ArrayList<>();		
			//ArrayList<ArrayList<String>> dataList= new ArrayList<>();

			//okresla ile spacji wstawic po kazdej kolumnie
			//ArrayList<Integer> dataHeaderSpacesList= new ArrayList<>();
			
			String s="";
			int i=0;
			while (i<dataHeaderList.size())
			{
				s+=dataHeaderList.get(i)+","+csvSpace(dataHeaderSpacesList.get(i));
				i++;
			}
			
			writerWriteLine(writer,s);

		}
	}
	
}
