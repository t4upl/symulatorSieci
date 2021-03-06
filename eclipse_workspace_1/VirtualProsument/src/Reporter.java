import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;



public class Reporter extends CoreClass {

	final Boolean samochodyWlaczone = false;
	
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
			print("Failure to create "+path);
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
				//print(dateAhead);
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
	
	String stringSeries(Float... args)
	{
		String s="";
		
		int a=0;
		while (a<args.length)
		{
			if (a>0)
			{
				s=s+",";
			}
			
			String floatAsString = String.format("%.8f", args[a]);

			
			s=s+floatAsString;
			a++;
		}
		
		return s;
	}
	
	
	
	void createSterowanieReport(OptimizerEV.Sterowanie sterowanie, ProsumentEV prosumentEV,OptimizerEV.Form24 form24)
	{
		String pathToFolder =scenarioFolder+"\\"+prosumentEV.getID()+"\\predykcje\\"+LokalneCentrum.getCurrentDay();
		createFolderCascade(pathToFolder);
		
		
		String pathToFile = pathToFolder+"\\"+LokalneCentrum.getCurrentHour().replaceAll(":", "_")+".csv";
		
		try {
			Writer writer = new FileWriter(pathToFile);
			
			//String ReportHeader = createSterowanieReportHeader(prosumentEV,sterowanie);
			
			writerWriteLine(writer,createSterowanieReportHeaderProsumentHeader(prosumentEV));
			//writerWriteLine(writer, ReportHeader);
			
			
			addSeperator(writer);
			writerWriteLine(writer,createSterowanieReportDataHeader(sterowanie));
			
			ArrayList<DayData> dList = sterowanie.getDayDataList();
			ArrayList<ArrayList<EVData>> eVList =sterowanie.geteVDataList();
			
			int i=0;
			while (i<Stale.horyzontCzasowy)
			{
				DayData d =dList.get(i);
				String dayDataline = createSterowanieReportDayDataLine(d);
				
				String lineToBePut=dayDataline+",,"+evLines(eVList,i,form24)+",,"+kosztyLines(sterowanie,i);
				//String lineToBePut=dayDataline+",";
				
				writerWriteLine(writer, lineToBePut);
				i++;
				
			}
			
			writer.close();
		}catch (Exception e){
			print("Error createSterowanieReport "+e.getMessage());
			
		}
		
		//getInput("createSterowanieReport -end");
 
	}
	
	private String kosztyLines(OptimizerEV.Sterowanie sterowanie, int t) {
		
		double[] koszty = sterowanie.getKoszt();
		double[] koszty_Zew = sterowanie.getKoszt_Zew();
		double[] koszty_sklad = sterowanie.getKoszt_sklad();
		double[] koszty_EV = sterowanie.getKoszt_EV();
		
		
		
		// TODO Auto-generated method stub
		return stringSeries(""+koszty[t],""+koszty_Zew[t],""+koszty_sklad[t],""+koszty_EV[t] );
	}

	private String evLines(ArrayList<ArrayList<EVData>> eVList, int t, OptimizerEV.Form24 form24) 
	{	
		int i=0;;
		String s="";
		
		//przejscie po samochodach
		while(i<eVList.size())
		{
			//print(i);
			
			if (i>0)
			{
				s+=",,";
			}
			
			ArrayList<Integer> statusList = form24.getStatusEV().get(i);
			ArrayList<EVData> eVListSingle =eVList.get(i);
			s+=evLine(eVListSingle,t,statusList);
			
			
			i++;
		}
		
		return s;
	}

	private String evLine(ArrayList<EVData> eVListSingle, int t,ArrayList<Integer> statusList) 
	{
		
		if (eVListSingle==null)
		{
			getInput("Error in evLine");
		}
		
		EVData e =eVListSingle.get(t);
		String s=stringSeries(e.getEVdom(),e.getEB_EVdom(),e.getEVdom_EB(),e.getZew_EVdom(),e.getG_EVdom(),e.getEVdom_c());
		
		s+=","+statusList.get(t);
		return s;
	}

	String stringAndLineSeparator(String s)
	{
		
		return s+System.lineSeparator();
	}
	
	String createSterowanieReportHeaderProsumentHeader(ProsumentEV prosument)
	{
		String s="";
		
		s+=stringAndLineSeparator("scenariusz,"+Stale.scenariusz);
		
		//format formatek ma linie na day i hour wiec tu tez je zostaw
		s+=stringAndLineSeparator("Day,Leave this empty");
		s+=stringAndLineSeparator("Hour,Leave this empty");
		s+=stringAndLineSeparator("ID,"+prosument.getID());
		s+=stringAndLineSeparator("Cena Zewnetrzna,"+Stale.cenaDystrybutoraZewnetrznego);
		s+=stringAndLineSeparator("Cena Baterii,"+floatToString(Stale.kosztAmortyzacjiBaterii));
		
		s+=stringAndLineSeparator("Cena Akumulatora,"+floatToString(Stale.kosztAmortyzacjiAkumulatoraEV));
		s+=stringAndLineSeparator("Energia na podroz,"+floatToString(Stale.podrozMinimumEnergii));
		s+=stringAndLineSeparator("Mnoznik,"+floatToString(prosument.getMnoznikGeneracji()));
		s+=stringAndLineSeparator("Koszt calkowity,"+floatToString(prosument.getTotalCost()));
		s+=stringAndLineSeparator("Koszt calkowity,"+floatToString(prosument.getTotalCost()));

		s+=stringAndLineSeparator("Caklowite zuzycie,"+floatToString(prosument.getTotalConsumption()));
		s+=stringAndLineSeparator("Calkowita generacja,"+floatToString(prosument.getTotalGeneration()));
		s+=stringAndLineSeparator("Unused generacja,"+floatToString(prosument.getTotalUnusedGeneration()));
		s+=stringAndLineSeparator("Reserve bonus,"+floatToString(prosument.getReserveBonus()));
		s+=stringAndLineSeparator("Cost no reserve,"+prosument.getCostNoReserve());
		s+=stringAndLineSeparator("Report note,"+prosument.getReportNote());

		return s;

	}
	
	String createSterowanieReportHeaderEVDataHeader(int i)
	{
		String s=stringSeries("EB_"+i,"EB_EV_"+i,"EV_EB"+i,"Zew_EV"+i,"G_EV"+i,"EV_c"+i,"Status_"+i);
		return s;
	}
	
	String createSterowanieReportHeaderEVDataHeader(String suffix)
	{
		String i =suffix;
		String s=stringSeries("EB_"+i,"EB_EV_"+i,"EV_EB"+i,"Zew_EV"+i,"G_EV"+i,"EV_c"+i,"Status_"+i);
		return s;
	}
	
	String createSterowanieReportDataHeader(OptimizerEV.Sterowanie sterowanie)
	{
		String s=dataHeader();
				
		ArrayList<ArrayList<EVData>>L1 = sterowanie.geteVDataList();
		
		int i=0;
		while (i < L1.size())
		{
			s+=",,"+createSterowanieReportHeaderEVDataHeader(i);
			//print("createSterowanieReportHeader "+i);
			i++;
		}
		
		s+=",,Koszt(opt),KosztZew,KosztSklad,KosztEV";
		
		return s;
	}
	
	String createSterowanieReportDayDataLine(DayData d)
	{
		String line = stringSeries(d.getDay(),d.getHour(),floatToString(d.getConsumption()),floatToString(d.getGeneration()),floatToString(d.getTrueGeneration()),"");
		line+=","+stringSeries(floatToString(d.getStanBateriiNaPoczatkuSlotu()),floatToString(d.getStanBateriiNaKoniecSlotu()),"",floatToString(d.getZBateriiNaKonsumpcje()),floatToString(d.getZBateriiNaRynek()),"");
		line+=","+stringSeries(floatToString(d.getZGeneracjiNaKonsumpcje()),floatToString(d.getZGeneracjiNaRynek()),floatToString(d.getZGeneracjiDoBaterii()),"");
		line+=","+stringSeries(floatToString(d.getZRynekNaKonsumpcje()),floatToString(d.getZRynekDoBaterii()),"");
		line+=","+stringSeries(floatToString(d.getCenaNaLokalnymRynku()),floatToString(d.getCost()),floatToString(d.getUnusedGeneration()),floatToString(d.getKupuj()) );
		
		return line;
		
	}
	
	String dataHeader()
	{
		String header1 ="Day,Hour,Konsumpcja,Generacja,Generacja(True),,stanBateriiNaPoczatkuSlotu,stanBateriiNaKoniecSlotu,,zBateriiNaKonsumpcje,zBateriiNaRynek,";
		String header2 ="zGeneracjiNaKonsumpcje,zGeneracjiNaRynek,zGeneracjiDoBaterii,,zRynekNaKonsumpcje,zRynekDoBaterii,,cenaLokalna,koszt,unusedGeneration,kupuj";
		return header1+","+header2;
	}

	
	public void createProsumentReport(ProsumentEV prosumentEV)
	{
		String pathToFile =scenarioFolder+"\\"+"agregate"+"\\"+prosumentEV.getID()+".csv";
		if (prosumentEV.getID()<=liczbaProsumentow)
		{
			pathToFile =scenarioFolder+"\\"+prosumentEV.getID()+"\\prosument_"+prosumentEV.getID()+".csv";	
		}
		
		try {
			Writer writer = new FileWriter(pathToFile);
						
			ArrayList<DayData> dList = prosumentEV.getDayDataList();
			
			ArrayList<ArrayList<EVData>> EVList = prosumentEV.getListaPodrozyFlota();
			
			
			ArrayList<EVData> test =EVList.get(0);
			
			
			writerWriteLine(writer,createSterowanieReportHeaderProsumentHeader(prosumentEV));
			
			
			addSeperator(writer);
			writerWriteLine(writer,createSterowanieReportDataHeader(EVList.size()));
			
			
			
			String dateAhead=dList.get(0).getDay()+" "+dList.get(0).getHour();
			
		
			int i=0;
			while (!dateAhead.equals(simulationEndDate))
			{
				//print("dateAhead "+dateAhead);
				
				
				DayData d =dList.get(i);
				String dayDataline = createSterowanieReportDayDataLine(d);
				
				String lineToBePut=dayDataline+",,"+evLines(EVList,i)+",,"+kosztyLines(prosumentEV,i);
				
				//dodaj sumy do lini
				lineToBePut+=",,"+evLine(prosumentEV.getEVDataListSuma(),i);
				//String lineToBePut=dayDataline+",";
				
				writerWriteLine(writer, lineToBePut);
				
				i++;
				
				dateAhead=dList.get(i).getDay()+" "+dList.get(i).getHour();

			}
			
			writer.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}		
		
	}
	
	String createSterowanieReportDataHeader(int liczbaSamochodowElektrycznych)
	{
		String s=dataHeader();
		
		int i=0;
		while (i < liczbaSamochodowElektrycznych)
		{
			s+=",,"+createSterowanieReportHeaderEVDataHeader(i);
			//print("createSterowanieReportHeader "+i);
			i++;
		}
		
		s+=",,Koszt(opt),KosztZew,KosztSklad,KosztEV";
		s+=",,"+createSterowanieReportHeaderEVDataHeader("S");
		
		return s;
	}
	
	private String evLines(ArrayList<ArrayList<EVData>> eVList, int t) 
	{	
		int i=0;;
		String s="";
		
		//przejscie po samochodach
		while(i<eVList.size())
		{
			//print(i);
			
			if (i>0)
			{
				s+=",,";
			}
			
			ArrayList<EVData> eVListSingle =eVList.get(i);
			s+=evLine(eVListSingle,t);
			
			
			i++;
		}
		
		return s;
	}
	
	private String evLine(ArrayList<EVData> eVListSingle, int t) 
	{
		
		if (eVListSingle==null)
		{
			getInput("Error in evLine");
		}
		
		EVData e =eVListSingle.get(t);
		String s=stringSeries(e.getEVdom(),e.getEB_EVdom(),e.getEVdom_EB(),e.getZew_EVdom(),e.getG_EVdom(),e.getEVdom_c());
		
		s+=","+e.getStatus();
		return s;
	}
	
	private String kosztyLines(ProsumentEV prosumentEV,int t) {
		
		ArrayList<Float > koszty = prosumentEV.getKoszty();
		
		
		String stringToBeReturned = stringSeries("-1","-1","-1","-1");
		if (t<koszty.size())
		{
			ArrayList<Float> koszty_Zew = prosumentEV.getKoszty_Zew();
			ArrayList<Float> koszty_Sklad = prosumentEV.getKoszty_sklad();
			ArrayList<Float> koszty_Ev = prosumentEV.getKoszty_EV();
		
			stringToBeReturned= stringSeries(koszty.get(t),koszty_Zew.get(t),koszty_Sklad.get(t),koszty_Ev.get(t));
			//getInput(" kosztyLines - uzupelnij");
		}
		
		
		
		/*
		double[] koszty = sterowanie.getKoszt();
		double[] koszty_Zew = sterowanie.getKoszt_Zew();
		double[] koszty_sklad = sterowanie.getKoszt_sklad();
		double[] koszty_EV = sterowanie.getKoszt_EV();*/
		
		
		
		// TODO Auto-generated method stub
		return stringToBeReturned;
		//return stringSeries(""+koszty[t],""+koszty_Zew[t],""+koszty_sklad[t],""+koszty_EV[t] );
	}	
	
	
}
