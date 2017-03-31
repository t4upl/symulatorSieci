package pracaMagisterksa;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

public class sampleGenerator2 {
	//OUT OF USE! use Sample Generator3 instead!
	
	//Dane z pliku
	ArrayList<String> godziny = new ArrayList<String>();
	
	ArrayList<Float> LudzmierzGeneracja = new ArrayList<Float>();
	ArrayList<Float> LudzmierzKonsumpcja = new ArrayList<Float>();
	
	ArrayList<Float> WieliczkaGeneracja = new ArrayList<Float>();
	ArrayList<Float> WieliczkaKonsumpcja = new ArrayList<Float>();

	ArrayList<Float> ReguliceGeneracja = new ArrayList<Float>();
	ArrayList<Float> ReguliceKonsumpcja = new ArrayList<Float>();
	
	ArrayList<Float> KrakowGeneracja = new ArrayList<Float>();
	ArrayList<Float> KrakowKonsumpcja = new ArrayList<Float>();
	
	//Finalne przebiegi
	ArrayList<Float> przbiegKonsumpcji = new ArrayList<Float>();
	ArrayList<Float> przbiegGeneracji = new ArrayList<Float>();

	//ArrayList<Float> totalEnergyConsumed = new ArrayList<Float>();
//	ArrayList<Float> totalEnergyGenerated = new ArrayList<Float>();
	
	
	//Stale klasy
	int liczbaMiesiecy =3;
	String startDate = "2015-06-01 00:00";
	String endDate = "2015-09-01 00:00";
	
	//Pozostale zmienne
	Float roczneZuzycieEnergii; //w kWh, gdyby przebieg obejmowal caly rok
	Float energiaWygenerowana; //bez jednostek, suma generacji

	Float[] wspolczynniki= new Float[4];

	//System
	Scanner sc = new Scanner(System.in);
	
	public void run(String pathToDataFolder, String outputFolderPath, String fileName, Boolean debug)
	{
		prerun();
		createClock(pathToDataFolder,"OZE1");
		
		//printStringList(godziny);
		
		
		wczytajDaneTotal(pathToDataFolder,debug);
		//utworzPrzbieg();
		//zrzucPrzbiegDoPliku(outputFolderPath,fileName);
		
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
            		line=inputControl(line);
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

	
	//wyzeruj tabele
	void prerun()
	{	przbiegKonsumpcji = new ArrayList<Float>();
		przbiegGeneracji = new ArrayList<Float>();
		godziny = new ArrayList<String>();
		
		LudzmierzGeneracja = new ArrayList<Float>();
		LudzmierzKonsumpcja = new ArrayList<Float>();
		
		WieliczkaGeneracja = new ArrayList<Float>();
		WieliczkaKonsumpcja = new ArrayList<Float>();

		ReguliceGeneracja = new ArrayList<Float>();
		ReguliceKonsumpcja = new ArrayList<Float>();
		
		KrakowGeneracja = new ArrayList<Float>();
		KrakowKonsumpcja = new ArrayList<Float>();
		
	}
	
	void zrzucPrzbiegDoPliku(String folderPath, String fileName)
	{
		System.out.println("\n----------\nZrzucPrzebiegDopliku start\n");
		System.out.println(przbiegKonsumpcji.size()+" "+godziny.size()+" "+przbiegGeneracji.size()  );
		
		try {
			
			Writer writer = new FileWriter(folderPath+"\\"+fileName+".txt");
			
			
			writer.write("WSPOLCZYNNIK:"+wspolczynniki[0]+"#"+wspolczynniki[1]+"#"+wspolczynniki[2]+"#"+wspolczynniki[3]+System.lineSeparator());
			writer.write("ROCZNE ZUZYCIE[kWh]:"+roczneZuzycieEnergii+System.lineSeparator());
			writer.write("CALKOWITA ENERGIA GENEROWANA[bez jednostki]:"+ energiaWygenerowana+System.lineSeparator());
			
			writer.write("###"+System.lineSeparator());
			int a=0;
			while (a<przbiegKonsumpcji.size())
			{
				writer.write(godziny.get(a)+"#"+przbiegKonsumpcji.get(a)+"#"+przbiegGeneracji.get(a)+System.lineSeparator());
				a++;
			}
			
			writer.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
		
		System.out.println("\n----------\nZrzucPrzebiegDopliku end\n");

		
	}
	
	public static int randInt(int min, int max) {
	    Random rand = new Random();

	    int randomNum = rand.nextInt((max - min) + 1) + min;

	    return randomNum;
	}
	
	//Wyxzznacz zuzycie energii zgodnie z  rozkaldem z GUS
	Float obliczEnergie()
	{
		Random random = new Random();
		 
		 
		int pick=Math.abs(random.nextInt())%100;
		int liczba_miesiecy=3;
		 
		 System.out.println("Pick "+pick);
		 
		 
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
	
	Float[] generateRandomPoints()
	{
	    Random random = new Random();
		Float f;
		
		Float[] randomPoints = new Float[4];
		Float[] wspolczynniki = new Float[4];
	    
		int a=0;
		while (a<4)
		{
			f = random.nextFloat();
			randomPoints[a]=f;
			a++;
		}
		
		Arrays.sort(randomPoints);
		
		a=0;
		while (a<4)
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
		
		return wspolczynniki;
	}
	
	//Tworzy 1 przebieg z 4; przeskalowywuje energie poboru do wylsoowanego
	void utworzPrzbieg()
	{
		System.out.println("\n--------\nUtworz przbeigi -start\n");

		Float[] randomPoints = generateRandomPoints();
		wspolczynniki=randomPoints;
		
		System.out.println("wspolczynniki "+Arrays.toString(wspolczynniki));
		
		Float sumaKonsumpcji=0f;
		Float sumaGeneracji=0f;
		
		int a=0;
		while (a<LudzmierzGeneracja.size())
		{
			Float konsumpcja = wspolczynniki[0]*LudzmierzKonsumpcja.get(a)+wspolczynniki[1]*WieliczkaKonsumpcja.get(a)+wspolczynniki[2]*ReguliceKonsumpcja.get(a)+wspolczynniki[3]*KrakowKonsumpcja.get(a);
			Float generacja = wspolczynniki[0]*LudzmierzGeneracja.get(a)+wspolczynniki[1]*WieliczkaGeneracja.get(a)+wspolczynniki[2]*ReguliceGeneracja.get(a)+wspolczynniki[3]*KrakowGeneracja.get(a);
						
			sumaKonsumpcji+=konsumpcja;
			sumaGeneracji+=generacja;
			
			przbiegKonsumpcji.add(konsumpcja);
			przbiegGeneracji.add(generacja);
			
			a++;
		}
				

		 
		 roczneZuzycieEnergii=obliczEnergie();//wygeneruj jakie powinno byc roczne zuzycie energii
		 energiaWygenerowana=sumaGeneracji;
		 
		 System.out.println("Energia koncowa "+roczneZuzycieEnergii);
		 
		 float mnoznik = (float)liczbaMiesiecy/12*(float)roczneZuzycieEnergii/sumaKonsumpcji;
		 
		 a=0;
		 while (a<przbiegKonsumpcji.size())
		 {
			 przbiegKonsumpcji.set(a, przbiegKonsumpcji.get(a)*mnoznik);
			 a++;
		 }
		 
		 System.out.println("Test: energia dla konsumpcji "+4*sumArray(przbiegKonsumpcji));
		 
		 //roczneZuzycieEnergii= (float)zuzyciEnergii;*/
		 

		 
		 System.out.println("\n--------\nUtworz przbeigi -end\n");
	}
	
	
	void wczytajDaneAddToList(ArrayList<Float> konsumpcjaList, ArrayList<Float> generacjaList, String konsumpcjaString, String generacjaString, String time, String city )
	{
		float konsumpcjaFloat =Float.parseFloat(konsumpcjaString);
		float generacjaFloat=Float.parseFloat(generacjaString);
		
		konsumpcjaList.add(konsumpcjaFloat);
		generacjaList.add(generacjaFloat);
		
		if (generacjaFloat>konsumpcjaFloat)
		{
			System.out.println("Probable problem at wczytajDaneAddToList at time "+time+" "+city);
			System.out.println(generacjaFloat+" "+konsumpcjaFloat);
		}
		
		
	}
	
	void wczytajDane(String pathToWorkSheet)
	{
		int dateActive = 0; //0 - start day not reached, 1 - start date reached, 2- end date reached
		int a=0;
		
		String csvFile = pathToWorkSheet;
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ",";
		
        try {
            br = new BufferedReader(new FileReader(csvFile));
            a=0;            
            while ((line = br.readLine()) != null  && dateActive<2  ) {  	
            	
            	if (a>40)
            	{
            		line=inputControl(line);
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
            			
            			/*LudzmierzKonsumpcja.add(Float.parseFloat(s2[1]));
            			LudzmierzGeneracja.add(Float.parseFloat(s2[2]));
            			
            			WieliczkaKonsumpcja.add(Float.parseFloat(s2[4]));
            			WieliczkaGeneracja.add(Float.parseFloat(s2[3]));
            			
            			ReguliceKonsumpcja.add(Float.parseFloat(s2[5]));
            			ReguliceGeneracja.add(Float.parseFloat(s2[6]));
            			
            			KrakowKonsumpcja.add(Float.parseFloat(s2[8]));
            			KrakowGeneracja.add(Float.parseFloat(s2[7]));*/
            			
            			wczytajDaneAddToList(LudzmierzKonsumpcja,LudzmierzGeneracja,s2[1],s2[2],s2[0],"Ludzmierz");
            			wczytajDaneAddToList(WieliczkaKonsumpcja,WieliczkaGeneracja,s2[4],s2[3],s2[0],"Wieliczka");
            			wczytajDaneAddToList(ReguliceKonsumpcja,ReguliceGeneracja,s2[5],s2[6],s2[0],"Regulice");
            			wczytajDaneAddToList(KrakowKonsumpcja,KrakowGeneracja,s2[8],s2[7],s2[0],"Krakow");
            	
            			sunControl();
            			
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
	
	int nieZeroweProbki(ArrayList<Float> L1)
	{
		int sum=0;
		int a=0;
		
		while(a<L1.size())
		{
			if (L1.get(a)>0)
			{
				sum++;
			}
			
			a++;
		}
		
		return sum;	
	}
	
	void nieZeroweProbkiRaport()
	{
		ArrayList<Float> konsumpcja = LudzmierzKonsumpcja;
		ArrayList<Float> generacja = LudzmierzGeneracja;
		String city= "Ludzmierz";
		
		System.out.println(city+ " konsumpcja "+nieZeroweProbki(konsumpcja)+" generacja "+nieZeroweProbki(generacja));
		
		konsumpcja = WieliczkaKonsumpcja;
		generacja = WieliczkaGeneracja;
		city= "Wieliczka";
		
		System.out.println(city+ " konsumpcja "+nieZeroweProbki(konsumpcja)+" generacja "+nieZeroweProbki(generacja));

		konsumpcja = ReguliceKonsumpcja;
		generacja = ReguliceGeneracja;
		city= "Regulice";

		System.out.println(city+ " konsumpcja "+nieZeroweProbki(konsumpcja)+" generacja "+nieZeroweProbki(generacja));
			
		konsumpcja = KrakowKonsumpcja;
		generacja = KrakowGeneracja;
		city= "Krakow";

		System.out.println(city+ " konsumpcja "+nieZeroweProbki(konsumpcja)+" generacja "+nieZeroweProbki(generacja));
		
	}
	
	void wczytajDaneTotal(String pathToWorkSheet, Boolean debug)
	{
		System.out.println("\n-----\nwczytaj dane -start\n");
		wczytajDane(pathToWorkSheet);


		
		/*int a=0;
		while (a<4)
		{
			totalEnergyConsumed.add(0f);
			totalEnergyGenerated.add(0f);
			a++;
		}*/
		
		
        /*
        
        //Normalize
        totalEnergyConsumed.set(0, sumArray(LudzmierzKonsumpcja));
        totalEnergyConsumed.set(1, sumArray(WieliczkaKonsumpcja));
        totalEnergyConsumed.set(2, sumArray(ReguliceKonsumpcja));
        totalEnergyConsumed.set(3, sumArray(KrakowKonsumpcja));
        
        totalEnergyGenerated.set(0, sumArray(LudzmierzGeneracja));
        totalEnergyGenerated.set(1, sumArray(WieliczkaGeneracja));
        totalEnergyGenerated.set(2, sumArray(ReguliceGeneracja));
        totalEnergyGenerated.set(3, sumArray(KrakowGeneracja));

        Float consumptionMax = findMax(totalEnergyConsumed);
        Float generationMax = findMax(totalEnergyGenerated);
        
        
        System.out.println("\nPart I");
        printFloatlist(totalEnergyConsumed);
        printFloatlist(totalEnergyGenerated);
        
        System.out.println("consumptionMax "+consumptionMax);
        System.out.println("generationMax "+generationMax);

        //Find scalling factor
        scale(consumptionMax,totalEnergyConsumed);
        scale(generationMax,totalEnergyGenerated);
        
        //Przeksaluj przbeiegi
        przeskalujPrzebieg(totalEnergyConsumed.get(0),LudzmierzKonsumpcja);
        przeskalujPrzebieg(totalEnergyConsumed.get(1),WieliczkaKonsumpcja);
        przeskalujPrzebieg(totalEnergyConsumed.get(2),ReguliceKonsumpcja);
        przeskalujPrzebieg(totalEnergyConsumed.get(3),KrakowKonsumpcja);
        
        przeskalujPrzebieg(totalEnergyGenerated.get(0),LudzmierzGeneracja);
        przeskalujPrzebieg(totalEnergyGenerated.get(1),WieliczkaGeneracja);
        przeskalujPrzebieg(totalEnergyGenerated.get(2),ReguliceGeneracja);
        przeskalujPrzebieg(totalEnergyGenerated.get(3),KrakowGeneracja);
        
        
        //Sprawd zczy wsyzstko (wyniki powinnien byc ten sam dla wszystkich generacji)
        System.out.println("\nWczytaj dane - final test");
        
        System.out.println("Konsumpcja");
        System.out.println(sumArray(LudzmierzKonsumpcja));
        System.out.println(sumArray(WieliczkaKonsumpcja));
        System.out.println(sumArray(ReguliceKonsumpcja));
        System.out.println(sumArray(KrakowKonsumpcja));

        System.out.println("Generacja");
        System.out.println(sumArray(LudzmierzGeneracja));
        System.out.println(sumArray(WieliczkaGeneracja));
        System.out.println(sumArray(ReguliceGeneracja));
        System.out.println(sumArray(KrakowGeneracja));
        

        
        
        System.out.println("Konsumpcja po przeskalowaniu");
                
        
		System.out.println("\n-----\nwczytaj dane -end\n");
		*/
		
	}
	
	void przeskalujPrzebieg(Float scallingFactor, ArrayList<Float> list)
	{
		int a=0;
		while (a<list.size())
		{
			list.set(a, list.get(a)*scallingFactor);
			a++;
		}
		
	}
	
	void printFloatList(ArrayList<Float> list)
	{
		System.out.println("\nprintFloatlist");
		int a=0;
		while (a<list.size())
		{
			System.out.println(a+" "+list.get(a));
			a++;
		}
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
	
	
	void scale (Float max, ArrayList<Float> list)
	{
		int a=0;
		while(a<list.size())
		{
			list.set(a, max/list.get(a));
			a++;
		}
		
	}
	
	
	Float findMax(ArrayList<Float> list)
	{		
		Float max =0f;
		int a=0;
		while (a<list.size())
		{
			if (max<list.get(a))
			{
				max=list.get(a);
			}
			a++;
		}
		
		return max;
	}
	
	Float sumArray(ArrayList<Float> list)
	{
		Float sum =0f;
		int a=0;
		while (a<list.size())
		{
			sum+=list.get(a);
			a++;
		}
		
		return sum;
	}
	
	//Sprawdza czy w nocy nie ma produkcji energii
	void sunControl()
	{
		int a=godziny.size()-1;
		
		String[] s22 = godziny.get(a).split(":");
		Integer samaGodzina = Integer.parseInt(s22[0].substring(s22[0].length()-2, s22[0].length()));
		
		if (samaGodzina>21 || samaGodzina<5)
		{
			if (LudzmierzGeneracja.get(a) > 0 || ReguliceGeneracja.get(a)>0 || KrakowGeneracja.get(a)>0 || WieliczkaGeneracja.get(a)>0 )
			{
						
				System.out.println("SUNCONTROL PROBLEM! sun doesnt shine in the night!");
				System.out.println(godziny.get(a)+" "+LudzmierzGeneracja.get(a)+" "+ReguliceGeneracja.get(a)+" "+KrakowGeneracja.get(a)+" "+ WieliczkaGeneracja.get(a));
			}
		}
		
		
	}
	
	String inputControl(String input)
	{
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
	
}
