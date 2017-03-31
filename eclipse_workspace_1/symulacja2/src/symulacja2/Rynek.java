package symulacja2;

import java.util.ArrayList;
import java.util.Scanner;

public class Rynek {
	
	//TODO zmienne
	
	private int liczbaProsumentow=Stale.liczbaProsumentow;
	
	//lista punktow przekazana przez prosumentow
	private ArrayList<ArrayList<Point>> listaFunkcjiUzytecznosci;
	
	//lista profili cen -> zawiera kolejne predykcje ze zmieniajaca sie predykcja na najblizszy slot
	//na ostatnim miejscu najdluzsza list
	private ArrayList<ArrayList<Float>> priceVectorsList;


	
	
	float cenaMinimalnaNaRynkuLokalnym =0f;
	float cenaMaksymalnaNaRynkuLokalnym =0.56f;
	
	//Stale
	
	//limit iteracji rynek -podjae cene, prosument odpowiada
	//w sklad limitu nie whchodzi pierwsza cena (podanie 3 predykcji)
	final int limitIteracji=4;
	final int limitIteracjiBisekcji=10;
	
	//słuzy do ograncizenia granic poczatkowej predykcji
	final float reallySmallNumber =0.001f;
	
	//obecna iteracja ograniczona prze zlimit iteracji
	int iteracja;
	
	//sluzy jedynie do debgua, rowny indeksowi z LokalnegoCentrum
	//ustawiania w resecie
	int debugIteracja =0;
	
	
	
	ListaProsumentow2 listaProsumentow;
		
	//SYSTEM
	Scanner sc = new Scanner (System.in);
	Reporter reporter = Reporter.getInstance();
	LokalneCentrumDystrybucji2 lokalneCentrum =LokalneCentrumDystrybucji2.getInstance();
	
	//REPORT
	float reportHandelSumaKupna;
	float reportHandelSumaSprzedazy;
	float reportHandelWolumenHandlu;
	
	//historia listy punktow przekazywana przez prosumentow
	private ArrayList<ArrayList<ArrayList<Point>>> historyListaFunkcjiUzytecznosci;
	
	//hisotria cen podawanych jako pierwsze
	//brak zmian przez trwania claosc symulacji
	private ArrayList<Float> historyDeclaredPrice = new ArrayList<Float>();
	
	//historia cen ostatecznie ustlaonych 
	//brak zmian przez trwania claosc symulacji
	private ArrayList<Float> historyFinalPrice = new ArrayList<Float>();
	
	//woluemn ahndlach we wszystkich iteracjach
	private ArrayList<Float> historyWolumenHandlu = new ArrayList<Float>();
	
	
	
	//ile energii chce sprzedac/kupic prosument poc enie rozpisywania kontraktu
	//alfa - ile chce sprzedac/kupic
	//beta - ile kupi/sprzed w efekcie handlu
	ArrayList<Point> reportHandelResults;
	
	//Singleton shit
	private static Rynek instance = null;
	private Rynek() 
	{
	}
	
   public static Rynek getInstance() {
	      if(instance == null) {
	         instance = new Rynek();
	      }
	      return instance;
	}
	
	//SETTERS
	public void setListaProsumentow(ListaProsumentow2 listaProsumentow2)
	{
		this.listaProsumentow = listaProsumentow2;
	}
	
	//-----------------------------
	//OTHER FUNCTIONS
	
	String pointListToString(ArrayList<Point> pointList)
	{
		String s="";
		int a=0;
		while (a<pointList.size())
		{	
			if (a>0)
			{
				s=s+"\n";
			}
			
			s=s+"\na:"+a+" "+pointList.get(a).toString();
			a++;
		}
		return s;
	}
	
	//this checks if price that prosuemnt wants to add is already on list (if it is adding shoudl be ignored)
	Boolean checkIfPriceExist(ArrayList<Point> pointList ,float  price)
	{
		int a=0;
		while(a<pointList.size())
		{
			if (price==pointList.get(a).getPrice())
			{
				return true;
			}
			a++;
		}
		return false;
	}
	
	//see addPricePoint to understand the use
	void addPricePointAndListNotEmpty(Prosument2 prosument, Point point, ArrayList<Point> pointList)
	{
		Boolean priceAlreadyInList = checkIfPriceExist(pointList,point.getPrice());
		
		//if price not in the list
		if (!priceAlreadyInList)
		{
			//says if found index that point.price < point(index).price
			//if not found add point as last in the list
			Boolean notFound = true; 
			
			int a=0;
			while (a<pointList.size() && notFound)
			{
				if (point.getPrice()<pointList.get(a).getPrice())
				{
					
					if (a>0)
					{
						updateAlfa(pointList.get(a-1),point,prosument,pointList);
					}
					
					updateAlfa(point,pointList.get(a),prosument,pointList);
					pointList.add(a, point);
					notFound=false;	
				}	
				a++;
			}
			
			if (notFound)
			{
				updateAlfa(pointList.get(pointList.size()-1),point,prosument, pointList);
				pointList.add(point);
			}
		}
		
	}
	
	//point jaki chce dodac prosument
	public void addPricePoint(Prosument2 prosument, Point point)
	{
		
		int idListy = prosument.getID()-1;
		ArrayList<Point> pointList = listaFunkcjiUzytecznosci.get(idListy);		
		
		if (pointList.size()==0)
		{
			pointList.add(point);
		}
		else
		{
			addPricePointAndListNotEmpty(prosument, point, pointList);
		}
		
		addPointToHistoryFunkcjaUzytecznosci(idListy);
		
	}
	
	void addPointToHistoryFunkcjaUzytecznosci(int IDprosumenta)
	{
		ArrayList<ArrayList<Point>> pointList2 = historyListaFunkcjiUzytecznosci.get(IDprosumenta);	
		pointList2.add(new ArrayList<Point> (listaFunkcjiUzytecznosci.get(IDprosumenta)));
	}
	
	void BroadcastCenaAmongProsuments(ArrayList<Float> priceVector)
	{
		//getInput("BroadcastCenaAmongProsuments");
		ArrayList<Prosument2> L1 =listaProsumentow.getListaProsumentow(); 
		
		int a=0;
		while (a<L1.size())
		{
			//wyznacz i dodaj punkt do funckji podazy
			L1.get(a).takePriceVector(priceVector);
			a++;
		}
		
		//for history reasons
		//last price in 
		priceVectorsList.add(priceVector);
		this.wyznaczCeneAndBroadcast();

	}
	
	int rozpiszKontraktyGetIndex()
	{
		//ostatnia zadeklarowana cena wsrod prosumentow
		float cena =(priceVectorsList.get(priceVectorsList.size()-1)).get(0);
		
		//reporting - dodaj finalnie ustalona cene do listy
		historyFinalPrice.add(cena);
		
		int index =-1;
		
		//wyznacz index taki ze point(index).cerna == ostatnia zadeklarowana cena
		//mozna dizlaac na dowolnym rposumencie wiec 0 tez dobre
		ArrayList<Point> L1	=listaFunkcjiUzytecznosci.get(0);
				
		int a=0;
		while (a<L1.size() && index==-1)
		{
			if (L1.get(a).getPrice()==cena)
			{
				index=a;
				//print("index "+index);
			}
			a++;
		}
		
		if (index==-1)
		{
			getInput("rozpiszKontrakty ERROR");
		}
		
		return index;
	}
	
	//TODO rozpisz kotnrakty
	void rozpiszKontrakty()
	{
		//reporting
		reportHandelResults = new ArrayList<Point>();
		Point point;
		
		float wolumenHandlu = 0f;
		float sumaKupna = 0f;
		float sumaSprzedazy = 0f;
		

		
		//index pod ktorym point.cena = cena ostatnio zadeklarowana
		int index =rozpiszKontraktyGetIndex();
		
		ArrayList<Point> L1;
		
		//znajdz sumaryczna kupno i sumaryczna sprzedaz
		int a=0;
		while(a<listaFunkcjiUzytecznosci.size())
		{
			L1	=listaFunkcjiUzytecznosci.get(a);
			float energia = L1.get(index).getIloscEnergiiDoKupienia();
			
			if (energia>0)
			{
				sumaKupna+=energia;
			}
			else
			{
				sumaSprzedazy-=energia;
			}
			
			//reporting 
			point = new Point();
			point.setAlfa(energia);
			reportHandelResults.add(point);
			
			a++;
		}
		
		wolumenHandlu =Math.min(sumaKupna,sumaSprzedazy);
		
		//reporting
		reportHandelSumaKupna =sumaKupna;
		reportHandelSumaSprzedazy =sumaSprzedazy;
		reportHandelWolumenHandlu = wolumenHandlu;
		
		//reporting
		historyWolumenHandlu.add(reportHandelWolumenHandlu);
		
		
		//wyznacz wolumen handlu dla kazdego z prosumentow
		ArrayList<Prosument2> listaProsumentowTrue =listaProsumentow.getListaProsumentow();

		a=0;
		while (a<listaProsumentowTrue.size())
		{
						
			// ustala bianrke kupuj
			// ustala clakowita sprzedaz (jako generacje)
			//ustala calkowite kupno (jako consumption)
			DayData2 constrainMarker  = new DayData2();
			
			L1	=listaFunkcjiUzytecznosci.get(a);
			
			//energia jaka zadeklarowal prosument ze sprzeda/kupi
			float energia = L1.get(index).getIloscEnergiiDoKupienia();
			
			if (energia>0)
			{
				float iloscEnergiiDoKupienia = energia/sumaKupna*wolumenHandlu;
				
				constrainMarker.setKupuj(1);
				constrainMarker.setGeneration(iloscEnergiiDoKupienia);
				
				point = reportHandelResults.get(a);
				point.setBeta(iloscEnergiiDoKupienia);
				
				
			}
			else
			{
				float iloscEnergiiDoSprzedania = energia/sumaSprzedazy*wolumenHandlu;

				
				constrainMarker.setKupuj(0);
				constrainMarker.setConsumption(iloscEnergiiDoSprzedania);
				
				point = reportHandelResults.get(a);
				point.setBeta(iloscEnergiiDoSprzedania);
			}
			
			ArrayList<Float> priceVector = priceVectorsList.get(priceVectorsList.size()-1);
			
			//poinformuj prosumenta o wyniakch handlu i dostosuj go do wynikow
			listaProsumentowTrue.get(a).getKontrakt(priceVector,constrainMarker);
			
			a++;
		}
				
	}
	
	//TODO wyznacz cene
	public void wyznaczCeneAndBroadcast()
	{
		//getInput("wyznaczCeneAndBroadcast -start");	
		
		if (iteracja<limitIteracji)
		{	
			float cena =wyznaczCene();
			
			
			//spreparuj vector cen
			ArrayList<Float> priceVector = new ArrayList<Float> (priceVectorsList.get(priceVectorsList.size()-1));
			priceVector.set(0, cena);
			
			iteracja++;
			
			BroadcastCenaAmongProsuments(priceVector);
		}
		else
		{

			rozpiszKontrakty();
			

			reportHistorieCen();
			
			//przekaz odpowiednie zmienne z klasy Rynek do funckji i stworz raport
			//potem sprawdz dlaczego handle nie dziala
			Point point = new Point();
			point.setAlfa(reportHandelSumaSprzedazy);
			point.setBeta(reportHandelSumaKupna);
			point.setIloscEnergiiDoKupienia(reportHandelWolumenHandlu);
			
			
			reporter.createHandelReport(point,reportHandelResults);
			
		}
	}
	
	//this used to be mcuh bigger
	void reportHistorieCen()
	{	
		reporter.createPointHistoryReport(historyListaFunkcjiUzytecznosci);
	}
	
	//TODO
	float wyznaczCene()
	{
		boolean localDebug=false;
		
		//wszystkie ceny sa tkaie same wiec mozna wziac pierwszego prosumenta i od neigo pobrac cene
		ArrayList<Point> L1 =listaFunkcjiUzytecznosci.get(0);
		
		float leftBound = L1.get(0).getPrice();
		float rightBound = L1.get(L1.size()-1).getPrice();
		
		float leftBoundValue=1; //inicjalizacja pozwala wejsc do loop'a
		float rightBoundValue=-1;
		float srodekValue = -1;
		
		//leftBound = 4; //test case
		//rightBound=10;
		
		float srodek = -1;
		
		if (LokalneCentrumDystrybucji2.getCurrentDay().equals("2015-06-01") && LokalneCentrumDystrybucji2.getCurrentHour().equals("19:00"))
		{
			localDebug=true;
			print(leftBound+"");
			print(rightBound+"");
			
			getInput("Hello to end of days");
		}

		
		int a=0;
		while (leftBoundValue!=rightBoundValue && a<limitIteracjiBisekcji)
		{
			if (localDebug)
			{
				print("bisection iteration");
				print(""+leftBound);
				print(""+rightBound);
				getInput("BISECTION iteration "+a);
			}
			
			
			leftBoundValue = funkcjaBilansujacaRynek(leftBound,localDebug);
			
			if (localDebug)
			{
				getInput("getLeft "+leftBound+" "+leftBoundValue);
			}
			
			rightBoundValue = funkcjaBilansujacaRynek(rightBound,localDebug);
			
			if (localDebug)
			{
				getInput("getRight "+rightBound+" "+rightBoundValue);
			}
			
			srodek = (leftBound+rightBound)/2;
			srodekValue = funkcjaBilansujacaRynek(srodek, localDebug);
			
			/*if ( !((leftBoundValue<=srodekValue) && (srodekValue<=rightBoundValue)) )
			{
				getInput("Problem in wyznaczCene");
			}*/
			
			if (localDebug)
			{
				print("bisection iteration");
				print(""+leftBound+" "+leftBoundValue);
				print(""+rightBound+" "+rightBoundValue);
				print(""+srodek+" "+srodekValue);
				getInput("");

			}
			
			
			if (srodekValue>0)
			{
				leftBound=srodek;
			}
			else
			{
				rightBound=srodek;
			}
			
			a++;
		}
		
		
		return srodek;
	}
	
	float funkcjaBilansujacaRynek(float price)
	{
		return funkcjaBilansujacaRynek(price,false);
	}

	
	//funckaj zwraca index a tkai ze L1(index) < price < L1(index+1)
	//funnkcja potrzeban,bo trzeba wiedizec skad brac alfe
	//L1 - lista <Point> o uporzadkowanej wzrastajaco cenie
	//
	int funkcjaBilansujacaRynekIndexAlfa(ArrayList<Point> L1, float price, boolean debugFlag)
	{
		int a=0;
		while (price>L1.get(a).getPrice())
		{
			if (debugFlag)
			{
				print("funkcjaBilansujacaRynekIndexAlfa "+L1.get(a).getPrice()+"");
			}
			
			a++;
		}
		
		if(debugFlag)
		{
			print("funkcjaBilansujacaRynekIndexAlfa " +a);
			print(L1.size());
		}
		
		if (a==L1.size()-1)
		{
			a--;
		}
		return a;
	}
	
	float funkcjaBilansujacaRynek(float price, Boolean debugFlag)
	{
		//float test =-8*price+5; //return this if you are testing
		
		//indeks ceny ktora zawiera alfe i bete funckji do ktorwej nalezy cena
		//price(a)<  price < price(a+1)
		
		ArrayList<Point> L1 = listaFunkcjiUzytecznosci.get(0);
	
		
		//cena wyszla poza dostepne granice
		if (price<L1.get(0).getPrice() || price>L1.get(L1.size()-1).getPrice())
		{
			getInput("funkcjaBilansujacaRynek -problem");
		}
		
		
		if (debugFlag)
		{
			print("funkcjaBilansujacaRynek cena na wejsciu "+price);
			print(""+L1.get(0).getPrice());
		}
		
		/*
		int indexAlfy = 0;
		int a=0;
		while (price>L1.get(a).getPrice())
		{
			if (debugFlag)
			{
				print(L1.get(a).getPrice()+"");
			}
			
			a++;
		}
		
		
		if (indexAlfy==L1.size())
		{
			a--;
		}*/
		
		int indexAlfy=funkcjaBilansujacaRynekIndexAlfa(L1,price, debugFlag);
		
		if (debugFlag)
		{
			
			print("idnexAlfy "+indexAlfy);
			getInput("");
		}
		
		//print(L1.get(indexAlfy).getPrice()+" "+price+" "+L1.get(indexAlfy+1).getPrice());
		//getInput("Firs Step end");
		
		
		//obliczanie sprzedazy i kupna prosumentow
		float sumaKupna = 0f;
		float sumaSprzedazy =0f;
		
		int a=0;
		while(a<listaFunkcjiUzytecznosci.size())
		{
			L1 = listaFunkcjiUzytecznosci.get(a);
			Point p = L1.get(indexAlfy);
			
			
			
			float energia = p.getAlfa()*price +p.getBeta();
			
			if (a==0 && debugFlag)
			{
				print("\n punkt z pryzblizenia");
				print(""+price);
				print(""+p.getAlfa());
				print(""+p.getBeta());
				print(""+energia);
				
				getInput("XYZ");
			}
			
			if (energia>0)
			{
				sumaKupna=sumaKupna+energia;
			}
			else
			{
				//ilosc energii dla sprzedazy jest ujemna
				sumaSprzedazy=sumaSprzedazy+energia;
			}
			
			a++;
		}
		
		//print (sumaKupna+" "+sumaSprzedazy);
		//getInput(" funkcjaBilansujacaRynek");
				
		return sumaKupna+sumaSprzedazy;
	}
	
	public void updateAlfa(Point p1, Point p2, Prosument2 prosument, ArrayList<Point> pointList)
	{
		float alfa = p1.getIloscEnergiiDoKupienia()-p2.getIloscEnergiiDoKupienia();
		alfa = alfa / (p1.getPrice()-p2.getPrice());
		
		float beta = p1.getIloscEnergiiDoKupienia()-alfa * p1.getPrice();
		
		if (alfa > reallySmallNumber)
		{
			print("\nprosument ID: "+prosument.getID());
			print("alfa "+alfa);
			print("iteration "+iteracja);
			
			print ("\np1 "+p1.toString());
			print ("\np2 "+p2.toString());
			print ("\n\n"+pointListToString(pointList));
			getInput("updateAlfa - error");
			
		}
		
		p1.setAlfa(alfa);
		p1.setBeta(beta);
	}

	//Odpalanae na poczatku kazdego simulationStepu
	//
	public void reset(int iterationFromLokalneCentrum)
	{
		//this line is for debug purposes only
		debugIteracja = iterationFromLokalneCentrum;
		
		iteracja =0;
		
		listaFunkcjiUzytecznosci = new ArrayList<ArrayList<Point>>();
		historyListaFunkcjiUzytecznosci = new ArrayList<ArrayList<ArrayList<Point>>>();

		
		
		int a=0;
		while (a<liczbaProsumentow)
		{
			listaFunkcjiUzytecznosci.add(new ArrayList<Point>());
			historyListaFunkcjiUzytecznosci.add(new ArrayList<ArrayList<Point>>());
			a++;
		}
		
		priceVectorsList = new ArrayList<ArrayList<Float>>();
		
	}
	
	public void setLiczbaProsumentow(int liczbaProsumentow)
	{
		this.liczbaProsumentow=liczbaProsumentow;
	}
	
	ArrayList<Float> pierwszaPredykcja(ListaProsumentow2 listaProsumentow2, int timeIndex)
	{
		if (Stale.cenyZGeneratora)
		{
			ArrayList<Float> listaSumarycznejGeneracji = listaProsumentow2.getListaSumarycznejGeneracji(timeIndex);
			ArrayList<Float> listaSumarycznejKonsumpcji = listaProsumentow2.getListaSumarycznejKonsumpcji(timeIndex);

			return predictPrice(listaSumarycznejGeneracji,listaSumarycznejKonsumpcji);
		}
		else
		{
			getInput("Fill this part out");
			return new ArrayList<Float>();
		}
	}
	
	//TODO
	public ArrayList<ArrayList<Float>> predykcjaCenNaRynkuLokalnym(ListaProsumentow2 listaProsumentow2, int timeIndex)
	{
		/*ArrayList<Float> listaSumarycznejGeneracji = listaProsumentow2.getListaSumarycznejGeneracji(timeIndex);
		ArrayList<Float> listaSumarycznejKonsumpcji = listaProsumentow2.getListaSumarycznejKonsumpcji(timeIndex);

		ArrayList<Float> normalnaPredykcja =predictPrice(listaSumarycznejGeneracji,listaSumarycznejKonsumpcji);*/
		
		ArrayList<Float> normalnaPredykcja = pierwszaPredykcja(listaProsumentow2, timeIndex);
		priceVectorsList.add(normalnaPredykcja);
		
		ArrayList<ArrayList<Float>> listaPredykcjiCen= new ArrayList<ArrayList<Float>>();
		
		
		//nizsza 
		ArrayList<Float> predykcjaZNizszaCena=new ArrayList<Float>(normalnaPredykcja);		
		ArrayList<Float> predykcjaZWyzszaCena=new ArrayList<Float>(normalnaPredykcja);
		
		float nizszaCena =(normalnaPredykcja.get(0)+cenaMinimalnaNaRynkuLokalnym)/2 ;
		float wyzszaCena=(normalnaPredykcja.get(0)+cenaMaksymalnaNaRynkuLokalnym)/2;
		
		predykcjaZNizszaCena.set(0, nizszaCena);
		predykcjaZWyzszaCena.set(0, wyzszaCena);
		
		listaPredykcjiCen.add(predykcjaZNizszaCena);
		listaPredykcjiCen.add(normalnaPredykcja);
		listaPredykcjiCen.add(predykcjaZWyzszaCena);
		
		//reporting - zapisz zadeklarowan cene
		historyDeclaredPrice.add(normalnaPredykcja.get(0));
		
		return listaPredykcjiCen;
	}
	
	
	ArrayList<Float> predictPrice(ArrayList<Float> listaSumarycznejGeneracji, ArrayList<Float> listaSumarycznejKonsumpcji) 
	{
		float expislon = 0.01f;
		
		ArrayList<Float> L1 =new ArrayList<Float> ();
		int a=0;
		while (a<listaSumarycznejGeneracji.size())
		{
			float wartoscDoDodania=expislon;
			if (listaSumarycznejKonsumpcji.get(a)>0)
			{
				wartoscDoDodania=(listaSumarycznejKonsumpcji.get(a)-listaSumarycznejGeneracji.get(a))/listaSumarycznejKonsumpcji.get(a);
				wartoscDoDodania=wartoscDoDodania*cenaMaksymalnaNaRynkuLokalnym;
			}
			
			//Ograniczenia na predykcje ceny 
			wartoscDoDodania=Math.max(wartoscDoDodania,expislon);
			wartoscDoDodania=Math.min(wartoscDoDodania,cenaMaksymalnaNaRynkuLokalnym-expislon);
			
			L1.add(wartoscDoDodania);
			
			
			a++;
		}
		
		//	print(Arrays.toString(L1.toArray()));			
		return L1;
	}
	
	
	void print(int a)
	{
		print(((Integer)a).toString());
	}
	
	void print(String s)
	{
		System.out.println("Rynek "+s);
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
	
	//TODO
	//wykonaj akcje pod koniec symulacji
	public void endOfSimulation()
	{
		reporter.reportPierwszeCeny(historyDeclaredPrice, historyFinalPrice, historyWolumenHandlu);
	}
	
}
