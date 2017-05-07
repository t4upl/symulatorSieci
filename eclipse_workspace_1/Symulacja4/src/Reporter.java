import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Reporter extends CoreClass {
	
	
	String scenarioFolder;
	
	//Singleton shit
	private static Reporter instance = null;
	private Reporter() 
	{
	}
	
	public static Reporter getInstance() {
	      if(instance == null) {
	         instance = new Reporter();
	         
	         instance.setUpScenarioFolder();
	      }
	      return instance;
	}
	
	//---------------------------
	
	//-----------------------------------
	//klasa reprezentuje rpaort w postaci: header (klucz wartosc), searator, header tabeli dnaych, tabela dnaych	
	class GridReport
	{
		int headerLiczbaLinii =30;
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
		
		public void addToHeader(String key, double value)
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
	
	
	//TODO
	//----------------------------
	
	public void createProsumentReport(Prosument prosument)
	{
		String pathToFile =scenarioFolder+"\\"+"agregate"+"\\"+prosument.getID()+".csv";
		if (prosument.getID()<Stale.liczbaProsumentow)
		{
			pathToFile =scenarioFolder+"\\"+prosument.getID()+"\\prosument_"+prosument.getID()+".csv";	
		}
		
		GridReport gridReport = new GridReport();
		gridReport.setPathToFile(pathToFile);
		
		createProsumentReportHeader(gridReport, prosument,pathToFile);
		
		//Data Header tkai sam jak w raportach sterowania
		createSterowanieDataHeader(gridReport);
		createProsumentFillOutReport(prosument, gridReport);
		
		gridReport.dropToFile();	
	}
	
	void createProsumentFillOutReport(Prosument prosument, GridReport gridReport)
	{
		int liczbaElemetnow =LokalneCentrum.getSimulationEndDateIndex()+1;
		
		ArrayList<DayData> dList=  getNFirstFromList(prosument.getDayDataList(),liczbaElemetnow);
		
		//createSterowanieFillOutReportDayDataLoop dodaje wssystkie elementy z lsity wic trzeba najpeirw list eprzyciac 
		createSterowanieFillOutReportDayDataLoop(gridReport,dList );
	}
	
	void createSterowanieFillOutReportDayDataLoop(GridReport gridReport, ArrayList<DayData> dList )
	{
		
		int i=0;
		while (i<dList.size())
		{
			DayData d = dList.get(i);
			
			gridReport.addDataToTable("Day",d.getDay());
			gridReport.addDataToTable("Hour",d.getHour());
			
			gridReport.addDataToTable("Konsumpcja",d.getConsumption());
			gridReport.addDataToTable("Generacja",d.getGeneration());
			gridReport.addDataToTable("Generacja(True)",d.getTrueGeneration());
			
			
			gridReport.addDataToTable("stanBateriiNaPoczatkuSlotu",d.getStanBateriiNaPoczatkuSlotu());

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
	
	void createProsumentFillOutReport(ProsumentEV prosumentEV, GridReport gridReport)
	{
		//+1 bo chcemy wziac eleementy z data == Stale.endDate, +1 za to ze liczba eleemetnow,a nei indeks ostatniego eleementu
		int liczbaElemetnow =LokalneCentrum.getSimulationEndDateIndex()+1;
		
		ArrayList<DayData> dList=  getNFirstFromList(prosumentEV.getDayDataList(),liczbaElemetnow);
		ArrayList<EVData> eVList=  getNFirstFromList(prosumentEV.getEVDataList(),liczbaElemetnow);
		
		//createSterowanieFillOutReportDayDataLoop dodaje wssystkie elementy z lsity wic trzeba najpeirw list eprzyciac 
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
			
			
			i++;
		}
		
	}
	
	
	void createSterowanieDataHeader(GridReport gridReport)
	{
		gridReport.addDataHeader("Day");
		gridReport.addDataHeader("Hour");
		gridReport.addDataHeader("Konsumpcja");
		gridReport.addDataHeader("Generacja");
		gridReport.addDataHeader("Generacja(True)",1);
		
		gridReport.addDataHeader("stanBateriiNaPoczatkuSlotu",2);
		
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
		
		gridReport.addDataHeader("koszt(opt)");
		gridReport.addDataHeader("koszt_handel");
		gridReport.addDataHeader("koszt_Zew");
		gridReport.addDataHeader("koszt_sklad",1);
		
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
		
		gridReport.addDataHeader("koszt_EV");

		
	}
	
	void createProsumentReportHeader(GridReport gridReport,Prosument prosument,String path)
	{
		gridReport.addToHeader("Path",path.replace("\\", ",") );
		gridReport.addToHeader("Scenariusz",Stale.scenariusz );
		gridReport.addToHeader("virtual",Stale.isVirtualnyProsument );
		gridReport.addToHeader("ceny z generatora",Stale.isCenyZGeneratora );
		gridReport.addToHeader("ID", prosument.getID());
		
		gridReport.addToHeader("Koszt calkowity", prosument.getTotalCost());
		gridReport.addToHeader("Cost no reserve", prosument.getCostNoReserve());
		gridReport.addToHeader("Reserve", prosument.getReserveBonus());
		gridReport.addToHeader("Report note", prosument.getReportNote());
	}
	
	
	public void createProsumentReport(ProsumentEV prosumentEV)
	{
		print ("createProsumentReport - hello ");
		
		String pathToFile =scenarioFolder+"\\"+"agregate"+"\\"+prosumentEV.getID()+".csv";
		if (prosumentEV.getID()<Stale.liczbaProsumentow)
		{
			pathToFile =scenarioFolder+"\\"+prosumentEV.getID()+"\\prosument_"+prosumentEV.getID()+".csv";	
		}
		
		GridReport gridReport = new GridReport();
		gridReport.setPathToFile(pathToFile);
		
		createProsumentReportHeader(gridReport, prosumentEV,pathToFile);
		
		//Data Header tkai sam jak w raportach sterowania
		createSterowanieDataHeader(gridReport);
		createProsumentFillOutReport(prosumentEV, gridReport);
		
		gridReport.dropToFile();
		
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

	void writerWriteLine(Writer writer, String s)
	{
		try {
			writer.write(s+System.lineSeparator());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	void setUpScenarioFolder()
	{
		//print("setUpScenarioFolder");
		
		scenarioFolder = Stale.outputFolder;
		
		
		if (Stale.isVirtualnyProsument)
		{
			scenarioFolder+="\\Virtual";
		}
		else
		{
			if (Stale.isCenyZGeneratora)
			{
				scenarioFolder+="\\cenaZGeneratora";
			}
			else
			{
				scenarioFolder+="\\cenaZZewnatrz";
			}
		}
		
		scenarioFolder+="\\Scenario_"+Stale.scenariusz;
		
		//print(scenarioFolder);

		
		
		//getInput("setUpScenarioFolder -end");
	}
}