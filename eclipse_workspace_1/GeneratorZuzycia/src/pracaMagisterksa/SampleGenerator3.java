package pracaMagisterksa;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;
import java.util.stream.IntStream;

public class SampleGenerator3 {

	//Listy
	ArrayList<String> godziny = new ArrayList<String>();
	ArrayList<LokalnaStacja> lokalnaStacjaList = new ArrayList<LokalnaStacja>();
	
	ArrayList<LokalnaStacja> lsPosortowanaPoNieZerowejGeneracji = new ArrayList<LokalnaStacja>();
	ArrayList<LokalnaStacja> lsPosortowanaPoNieZerowejKonsumpcji = new ArrayList<LokalnaStacja>();
	ArrayList<LokalnaStacja> lsWygenerowane = new ArrayList<LokalnaStacja>();

	//Stale klasy
	int liczbaMiesiecy =3;//potrzebne do skalowania energii z GUS do probka
	String startDate = "2015-06-01 00:00";
	String endDate = "2015-09-01 00:00";
	
	Float[] wspolczynniki= new Float[4];
	
	Scanner sc = new Scanner(System.in);
	
	void printArrayListLokalnaStacja(ArrayList<LokalnaStacja> L1 )
	{
		System.out.println("----------");
		int a=0;
		while (a<L1.size())
		{
			System.out.println(a+": "+L1.get(a).print());
			a++;
		}
	}
	
	void countNonZerosAndSort()
	{
		int a=0;
		while(a<lokalnaStacjaList.size())
		{
			lokalnaStacjaList.get(a).updateNonZeros();
			a++;
		}
		
		LokalnaStacjaSorter lsSorter = new LokalnaStacjaSorter(lokalnaStacjaList);
	
		lsSorter.getNumberOfNonZeroConsumption();
		lokalnaStacjaList=lsSorter.getLocalList();
		stacjaListCopyFirstElements(4,lokalnaStacjaList,lsPosortowanaPoNieZerowejKonsumpcji,"konsumpcja");

		
		lsSorter.getNumberOfNonZeroGeneration();
		lokalnaStacjaList=lsSorter.getLocalList();
		stacjaListCopyFirstElements(4,lokalnaStacjaList,lsPosortowanaPoNieZerowejGeneracji,"generacja");		
		
	}
	
	void stacjaListCopyFirstElements(int n, ArrayList<LokalnaStacja> scr, ArrayList<LokalnaStacja> dst, String fileName)
	{
		int a=0;
		while(a<n)
		{
			LokalnaStacja ls =scr.get(a);
			ls.setFilename(fileName+((Integer)(a+1)).toString());
			
			dst.add(ls);
			a++;
		}
	}
	
	void printLokalnaStacjaListNiezerowa(ArrayList<LokalnaStacja> L1,Boolean generacjaSort)
	{
		System.out.println(" printLokalnaStacjaListNiezerowa "+String.valueOf(generacjaSort));
		
		int a=0;
		while (a<L1.size())
		{	
			if (generacjaSort)
			{
				System.out.println(a+" "+L1.get(a).getNumberOfNonZeroGenerationRecords());
			}
			else
			{
				System.out.println(a+" "+L1.get(a).getNumberOfNonZeroConsumptionRecords());				
			}
			a++;
		}
	}
	
	void sortLokalnaStacjaListByNiezerowa(ArrayList<LokalnaStacja> L1,Boolean generacjaSort)
	{
		LokalnaStacja ls1=null;
		LokalnaStacja ls2 = null;
		LokalnaStacja lsTemp = null;
		int ls1Count=0;
		int ls2Count=0;
		
		int a=0;
		while(a<L1.size())
		{
			
			ls1=L1.get(a);
			if (generacjaSort)
			{
				ls1Count = L1.get(a).getNumberOfNonZeroGenerationRecords();
			}
			else
			{
				ls1Count = L1.get(a).getNumberOfNonZeroConsumptionRecords();
			}
			
			int b=a+1;
			while (b<L1.size())
			{
				if (generacjaSort)
				{
					ls2Count = L1.get(b).getNumberOfNonZeroGenerationRecords();	
				}
				else
				{
					ls2Count = L1.get(b).getNumberOfNonZeroConsumptionRecords();
				}
				
				if (ls2Count>ls1Count)
				{
					ls2Count=ls1Count;
					lsTemp=ls1;
					L1.set(a, ls2);
					L1.set(b, lsTemp);
				}
				
				b++;
			}
			a++;
		}
	}
	
	public void run(String pathToDataFolder, String outputFolderPath, Boolean debug, int liczbaProbek)
	{
		String extraOutpurFolderPath=outputFolderPath+"\\extras";
		String finalOutputFolderPath=outputFolderPath+"\\final";
		String extraOutpurFolderCorePath=outputFolderPath+"\\extras\\core";
		String extraOutpurFolderCorePathNormalzied=outputFolderPath+"\\extras\\coreZnormalizowane";
		String extraOutpurFolderUtworzoneNieskalowane=outputFolderPath+"\\extras\\utworzoneNieSkalowane";

		
		createClock(pathToDataFolder,"OZE1");
		consumeDataFile(pathToDataFolder,"OZE1",debug);
		consumeDataFile(pathToDataFolder,"OZE2",debug);
		consumeDataFile(pathToDataFolder,"OZE3",debug);
		
		createOutputFolders(extraOutpurFolderPath,finalOutputFolderPath);

		checkLoadedDataConsistency();
		
		//drop data the way they ar estored in memory
		//dropFilesAsTheyWhereLoaded(extraOutpurFolderPath); 
		
		countNonZerosAndSort();
		
		//Zrzuc przbeieg na podstawie ktorcyh generowane beda pozostale przbeigi
		dropCoreFiles(extraOutpurFolderCorePath,lsPosortowanaPoNieZerowejGeneracji);
		dropCoreFiles(extraOutpurFolderCorePath,lsPosortowanaPoNieZerowejKonsumpcji);
		
		utworzPrzbiegi(lsWygenerowane,liczbaProbek,extraOutpurFolderCorePathNormalzied,extraOutpurFolderUtworzoneNieskalowane);
		
		Helper.dropFinal(finalOutputFolderPath,lsWygenerowane);
		
		//printStringList(godziny);
		
		
		//wczytajDaneTotal(pathToDataFolder,debug);
		
		//zrzucPrzbiegDoPliku(outputFolderPath,fileName);
		
		System.out.print("Folder wyjsciowy: "+finalOutputFolderPath);
		System.out.println("\n SampleGenerator3 -end");
	}
	
	
	LokalnaStacja utworzPrzebiegBezSkalowania(String numerProbki, Float[] wspolczynniki)
	{
		LokalnaStacja ls = new 	LokalnaStacja("nieprzeskalowana",numerProbki,-1,-1);		
		int b=0;
		int liczbaRekordow =lsPosortowanaPoNieZerowejGeneracji.get(0).getGeneracjaList().size();
		while (b<liczbaRekordow)
		{
			Float konsumpcja = arrayTimeList(lsPosortowanaPoNieZerowejKonsumpcji ,wspolczynniki, true, b);
			Float generacja = arrayTimeList(lsPosortowanaPoNieZerowejGeneracji ,wspolczynniki, false, b);
			
			String hour =lsPosortowanaPoNieZerowejGeneracji.get(0).getHourList().get(b);
			
			ls.addConsumptionElement(hour, konsumpcja);
			ls.addGenerationElement(hour, generacja);

			b++;
		}
		
		return ls;
	
	}
	
	
	void utworzPrzbiegi(ArrayList<LokalnaStacja> listaGdzieBedaDodawanePrzebiegi,int liczbaProbek, String pathToZnormalizowanePrzebiegiFolder, String folderUtworzone)
	{

		System.out.println("-----\nSampleGenerator3 + utworzPrzbiegi ");
		
		//check if sum(wspolczynniki)=1
  		/*float sum=0;
		int b=0;
		while (b<randomPoints.length)
		{
			sum+=wspolczynniki[b];
			b++;
		}*/
		
		//dropCoreFiles(pathToZnormalizowanePrzebiegiFolder,lsPosortowanaPoNieZerowejGeneracji);
		//dropCoreFiles(pathToZnormalizowanePrzebiegiFolder,lsPosortowanaPoNieZerowejKonsumpcji);

		
		//Utworz probki 
		int a=0; //kolejne probki
		while (a<liczbaProbek)
		{
			
			//Wyznacz wspolczynniki
			Float[] randomPoints = generateRandomPoints(4);
			wspolczynniki=randomPoints;
			
			znormalizuj(lsPosortowanaPoNieZerowejGeneracji,true);
			znormalizuj(lsPosortowanaPoNieZerowejKonsumpcji,false);
			
			LokalnaStacja ls= utworzPrzebiegBezSkalowania(((Integer)liczbaProbek).toString(), wspolczynniki);
			ls.setFilename(((Integer)(a+1)).toString());
			ls.setWspolczynniki(randomPoints);
			listaGdzieBedaDodawanePrzebiegi.add(ls);	
			a++;
		}
		
		dropCoreFiles(folderUtworzone,listaGdzieBedaDodawanePrzebiegi);
		
		//System.out.println("-----\nSampleGenerator3 + utworzPrzbiegi + Wygeneruj moc ");
		
		//Wygeneruj moc dla kazdego i znormalizuj generacje i energie
		a=0; //kolejne probki
		while (a<liczbaProbek)
		{
			LokalnaStacja ls= listaGdzieBedaDodawanePrzebiegi.get(a);
			float trzyMiesieczneZuzycie=obliczEnergie()*liczbaMiesiecy/12;//wygeneruj jakie powinno byc roczne zuzycie energii
			//System.out.print(roczneZuzycieEnergii);
			ls.normalizeToTheEnergy(trzyMiesieczneZuzycie);
			ls.setEnergiaWtrakcie3Miesiecy(trzyMiesieczneZuzycie);
			ls.createDailyLists();
			a++;
		}
		
	}
	
	//Wyxzznacz zuzycie energii zgodnie z  rozkaldem z GUS
	Float obliczEnergie()
	{
		Random random = new Random();		 
		int pick=Math.abs(random.nextInt())%100;		 
		 
		int min = 1001;
		int max = 1500;		 
		 
		 if (22<pick)
		 {
			 min = 1501;
			 max = 2000;
		 }
		 
		 if (46<pick)
		 {
			 min = 2001;
			 max = 3000;
		 }
		 
		 if (80<pick)
		 {
			 min = 3001;
			 max = 5000;
		 }
		 
		 return (float)randInt(min,max);
	}
	
	public static int randInt(int min, int max) {
	    Random rand = new Random();

	    int randomNum = rand.nextInt((max - min) + 1) + min;

	    return randomNum;
	}
	
	void znormalizuj (ArrayList<LokalnaStacja> L1, Boolean doGeneracji)
	{
		float max = znajdzMax(L1,doGeneracji);
		
		int a=0;
		while (a<L1.size())
		{
			LokalnaStacja ls = L1.get(a);

			if (doGeneracji)
			{
				ls.multiply(max/ls.getSumaGeneracji(),doGeneracji);				
			}
			else
			{
				ls.multiply(max/ls.getSumaKonsumpcji(),doGeneracji);				
			}

			a++;
		}
	}
	
	float znajdzMax(ArrayList<LokalnaStacja> L1,Boolean doGeneracji)
	{
		int a=0;
		float max=0;
		float max2=0;
		
		while (a<L1.size())
		{
			if (doGeneracji)
			{
				max2 =L1.get(a).sumGeneracjaList();
			}
			else
			{
				max2 =L1.get(a).sumKonsumpcjaList();
			}
			
			if (max2>max)
			{
				max=max2;
			}
			
			a++;
		}
		
		return max;
	}
	
	Float arrayTimeList(ArrayList<LokalnaStacja> L1 ,Float[] fArray, Boolean konsumpcja, int index)
	{
		float f=0;
		
		int a=0;
		while (a<L1.size())
		{
			float d=fArray[a];
			float e=1f;
			
			if (konsumpcja)
			{
				e=L1.get(a).getKonsumpcjaList().get(index);
			}
			else
			{
				e=L1.get(a).getGeneracjaList().get(index);				
			}
			
			f+=(d*e);
			a++;
		}
		
		return f;
	}
	
	Float[] generateRandomPoints(int liczbaPunktow)
	{

		
	    Random random = new Random();
		Float f;
		
		Float[] randomPoints = new Float[liczbaPunktow-1];
		Float[] wspolczynniki = new Float[liczbaPunktow];
	    
		int a=0;
		while (a<liczbaPunktow-1)
		{
			f = random.nextFloat();
			randomPoints[a]=f;
			a++;
		}
		
		Arrays.sort(randomPoints);
		
		a=0;
		while (a<liczbaPunktow-1)
		{
			if (a==0)
			{
				wspolczynniki[a] = randomPoints[0];
			}
			else
			{
				wspolczynniki[a] = randomPoints[a]-randomPoints[a-1];
			}
			
			a++;
		}
		
		wspolczynniki[a]=1-randomPoints[a-1];
		
		return wspolczynniki;
	}
	
	
	void checkLoadedDataConsistency()
	{
		int hoursSize = lokalnaStacjaList.get(0).getGeneracjaList().size();
		
		int a=0;
		while (a<lokalnaStacjaList.size())
		{
			lokalnaStacjaList.get(a).checkDataConsistency();
			
			if (hoursSize!=lokalnaStacjaList.get(a).getGeneracjaList().size())
			{
				System.out.println("ERROR! checkLoadedDataConsistency  ");
			}
			
			a++;
		}
		
		
	}
	
	void dropFilesAsTheyWhereLoaded(String outputFolder, ArrayList<LokalnaStacja> L1)
	{
		int a=0;
		while (a<L1.size())
		{
			L1.get(a).dropDataPure(outputFolder);
			a++;
		}
		
	}
	
	void dropCoreFiles(String outputFolder, ArrayList<LokalnaStacja> L1)
	{
		System.out.println("SampleGenerator 3 +dropCoreFiles");
		
		int a=0;
		while (a<L1.size())
		{
			L1.get(a).dropDataCore(outputFolder);
			a++;
		}
		
	}
	
	void createOutputFolders(String extraFolder, String primalFolder)
	{
		createFolder(extraFolder);
		createFolder(primalFolder);
		createFolder(extraFolder+"\\OZE1");
		createFolder(extraFolder+"\\OZE2");
		createFolder(extraFolder+"\\OZE3");

		createFolder(extraFolder+"\\core");
		createFolder(extraFolder+"\\coreZnormalizowane");

		createFolder(extraFolder+"\\nieprzeskalowane");
		createFolder(extraFolder+"\\utworzoneNieSkalowane");
		
		
	}
	
	void createFolder(String folderPath)
	{
        Path path = Paths.get(folderPath);
        //if directory exists?
        if (!Files.exists(path)) {
            try {
                Files.createDirectories(path);
            } catch (IOException e) {
                //fail to create directory
            	
            	System.out.print("EXCEPTION! dropResults");
                e.printStackTrace();
            }
        }
	}
	
	void consumeMainDataLine(String[] s2, String fileName, ArrayList<LokalnaStacja> L1)
	{
		String hour = s2[0];
		
		int a=1;
		while (a<s2.length)
		{
			//System.out.println("consume "+a+" "+s2[a]);
			findStationAndEnterData(L1, fileName, a, s2[a],hour);
			
			a++;
		}
	}
	
	void findStationAndEnterData(ArrayList<LokalnaStacja> L1, String fileName, int indexConsumed, String stringToBeAdded,String hour)
	{
		Boolean notFound =true;
		
		int a=0;
		while(a<L1.size() && notFound)
		{
			LokalnaStacja lokalnaStacja = L1.get(a);
			if (lokalnaStacja.getFileName().equals(fileName))
			{
				if (lokalnaStacja.getConsumptionIndex()==indexConsumed)
				{
					lokalnaStacja.addConsumptionElement(hour,stringToBeAdded);
					notFound=false;
				}
				
				if (lokalnaStacja.getGenerationIndex()==indexConsumed)
				{
					lokalnaStacja.addGenerationElement(hour,stringToBeAdded);
					notFound=false;
				}
				
			}
			a++;
		}
		
		if (notFound)
		{
			System.out.println("Something went terribly wrong in findStationAndEnterData "+fileName+" "+indexConsumed+" "+
					stringToBeAdded+" "+hour);
		}
	}
	
	void consumeDataFile(String pathToworkSheet, String fileName, Boolean debug)
	{
		String pathToWorkSheet=pathToworkSheet+"\\"+fileName+".csv";
		int dateActive=0;
		String csvFile = pathToWorkSheet;
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ",";
        
        Boolean mainData=false; //says if line with horus has beeen reached
        Boolean headerEncountered = false;//says tru if you LokalnaStacja objects from file has been created
        
        int index=0;
        
        Boolean justTest = true; //debug only!
        
        try {
            br = new BufferedReader(new FileReader(csvFile));
            int a=0;            
            while ((line = br.readLine()) != null  && dateActive<2  ) {  	
            	
            	if (mainData)
            	{
            		
            		line=inputControl(line,debug);
            		String[] s2 = line.split(",");

            		
            		if (s2[0].equals(startDate))
            		{
            			dateActive=1;
            		}
            		
            		if (s2[0].equals(endDate))
            		{
            			dateActive=2;
            		}
            		
            		if (dateActive==1)
            		{
        				consumeMainDataLine(s2,fileName,lokalnaStacjaList);

            		}	
            	}
            	else
            	{
            		//System.out.println(a+" "+line);
            		
            		if (line.contains(",") && !line.contains("Moc") && headerEncountered)
            		{
            			String[] s2 = line.split(",");
            			if (s2.length>4)
            			{
            				mainData=true;
            				//System.out.println("MAIN DATA TRUE "+line);
            				//sc.nextLine();
            				//printArrayListLokalnaStacja(lokalnaStacjaList);
            			}
            		}
            		
            		if (!mainData)
            		{
            			
            			if (line.equals("Ca?a doba"))
            			{
            				headerEncountered=true;
            				Boolean generationFirst = true;
            				line = br.readLine();
            				
            				index++;
            				int firstIndex=index;
            				
            				//System.out.println("\n*"+line);
            				String stationName = line.substring(0, line.indexOf(",")-4);
            				
            				if (line.contains("+"))
            				{
            					generationFirst=false;
            					
            				}
            				
            				line = br.readLine();
            				line = br.readLine();
            				//System.out.println(line);
            				//System.out.println(generationFirst);
            				
            				index++;
            				int secondIndex=index;
            				
            				
            				LokalnaStacja lokalnaStacja =null;
            				if (generationFirst)
            				{
                				lokalnaStacja = new LokalnaStacja(stationName,fileName,firstIndex,secondIndex);            					
            				}
            				else
            				{
                				lokalnaStacja = new LokalnaStacja(stationName,fileName,secondIndex,firstIndex);
            				}
            				
            				lokalnaStacjaList.add(lokalnaStacja);
                  				
            			}
            		}
            	}
            	
            	a++;   	
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }	


	}
	
	void createClock(String pathToworkSheet, String fileName)
	{
		String pathToWorkSheet=pathToworkSheet+"\\"+fileName+".csv";
		int dateActive=0;
		
		String csvFile = pathToWorkSheet;
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ",";
		
        try {
            br = new BufferedReader(new FileReader(csvFile));
            int a=0;            
            while ((line = br.readLine()) != null  && dateActive<2  ) {  	
            
            	
            	if (a>40)
            	{
            		line=inputControl(line,false);
            		String[] s2 = line.split(",");
            		if (s2[0].equals(startDate))
            		{
            			dateActive=1;
            		}
            		
            		if (s2[0].equals(endDate))
            		{
            			dateActive=2;
            		}
            		
            		if (dateActive==1)
            		{
            			godziny.add(s2[0]);	
            		}
            		
            	}
            	
            	a++;   	
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }	
	}
	
	String replaceCommaBeforeHashWithDot(String line)
	{
		if (line.contains("#") || line.contains("*"))
		{
			//	System.out.println(line);
			Boolean hashDetected=false;
			int a=0;
			while (a<line.length())
			{
				//System.out.println(line.charAt(line.length()-1-a));
				
				int indexPodzialu = line.length()-1-a;
				char c=line.charAt(line.length()-1-a);
				if (hashDetected)
				{
					if (c==',')
					{
						line = line.substring(0, indexPodzialu)+"."+line.substring(indexPodzialu+1, line.length());
						hashDetected=false;
					}
				}
				else
				{
					if (c=='#' || c=='*')
					{
						hashDetected=true;
					}
				}
				
				a++;
			}
		}
		
		return line;
	}
	
	String inputControl(String input, Boolean debug)
	{
		
		if (input.contains("#") && debug)
		{
			//System.out.println(input);
			//sc.nextLine();
		}
		
		input =	replaceCommaBeforeHashWithDot(input);
		
		if (input.contains("#")&& debug)
		{
			//System.out.println(input);
			//sc.nextLine();
			
		}
		
		if (input.contains("\""))
		{
			input=input.replace("\"", "");
		}
		
		if (input.contains("*"))
		{
			input=input.replace("*", "");
		}

		if (input.contains("#"))
		{
			input=input.replace("#", "");
		}
		
		return input;
	}
	
	void printStringList(ArrayList<String> list)
	{
		System.out.println("\nprintFloatlist");
		int a=0;
		while (a<list.size())
		{
			System.out.println(a+" "+list.get(a));
			a++;
		}
	}
}
