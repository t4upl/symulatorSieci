import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;


public class Reporter extends CoreClass {

	String simulationEndDate =Stale.simulationEndDate;
	
	//ustawiane przez LokalenCentrum
	private String scenarioFolder;

	private int liczbaProsumentow=Stale.liczbaProsumentow;



	
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
	//-------------------------
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
	
	
	
}