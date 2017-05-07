import java.util.ArrayList;
import java.util.Arrays;



public class Rynek extends CoreClass {

	//sprawddza czy 
	Boolean isAlfaCheck =false;
	
	private int liczbaProsumentow=Stale.liczbaProsumentow;
	
	//lista punktow przekazana przez prosumentow
	private ArrayList<ArrayList<Point>> listaFunkcjiUzytecznosci;
	
	//lista profili cen -> zawiera kolejne predykcje (wektor dlugosci 24) ze zmieniajaca sie predykcja na najblizszy slot
	//na ostatnim miejscu ostatnio zadeklarowana lista
	//vectory cen dodawanee przy predykcji i przy BroadcastCenaAmongProsuments
	//kazda lista zawiera predykcje na 24 godziny
	//nie zawiera predykcji dla cen z zakresu dostpenych cen (ceny minimalnej i maksymlanej) 
	private ArrayList<ArrayList<Float>> priceVectorsList;
	
	//wczytana z pliku lista pierwszych cen podawanych w kolejnych iteracjach 
	//uzywane tylko gdy Stale.cenyZGeneratora =false
	private ArrayList<Float> listaCenWczytanaZPliku=new ArrayList<Float>(); 
	
	
	//uzywana do ograniczenia ilosci komunikatow miedzy rynkiem a prosumentami
	int iteracja;

	//limit iteracji rynek -podjae cene, prosument odpowiada
	//w sklad limitu nie whchodzi pierwsza cena (podanie 3 predykcji)
	final int limitIteracji=Stale.limitIteracji;
	//final int limitIteracjiBisekcji=Stale.limitBisekcji;
	
	//SYSTEM
	//keeps track of data for reporting
	RynekHistory rynekHistory =RynekHistory.getInstance();
	ListaProsumentowWrap listaProsumentowWrap = ListaProsumentowWrap.getInstance();
	Loader loader = Loader.getInstance();
	
	
	//Singleton shit
	private static Rynek instance = null;
	private Rynek() 
	{
	}
	
   public static Rynek getInstance() {
	      if(instance == null) {
	         instance = new Rynek();
	         instance.inicjalizujRynek();
	         
	      }
	      return instance;
	}
   
	//TODO
	//wykonaj akcje pod koniec symulacji
	public void endOfSimulation()
	{
		rynekHistory.createFirstPriceVSFinalPriceReport();
	}
	
	//-----------------------
	//TODO
	//GETTERS
	
	public int getIteracja()
	{
		return this.iteracja;
	}
	
	//TODO
	//--------------------------
	
	//wywolywane przy getInstance() pobrnaiu instancji gdy instnacja==null
	//musi byc public bo wolane przez funkcje statyczna na rzecz nowostowrzonej isntancji
	public void inicjalizujRynek()
	{
		if (!Stale.cenyZGeneratora)
		{
			listaCenWczytanaZPliku=loader.loadPrices();
		}
	}
	
	//zwraca ile kont nalezy utworzyc
	//dla scenariusz bez EV = 16 (jedno na prosuemnta)
	//dla scenraiusz z EV =20 (16+ 4 -dla kazdego EV)
	int obliczLiczbaKont()
	{
		ListaProsumentowWrap listaProsumentowWrap= ListaProsumentowWrap.getInstance();
		ArrayList<Prosument> listaProsumentow = listaProsumentowWrap.getListaProsumentow();
		
		int sum=0;
		
		int i=0;
		while (i<listaProsumentow.size())
		{
			Prosument prosument = listaProsumentow.get(i);
			
			if (prosument instanceof ProsumentEV)
			{
				sum+=2;
			}
			else
			{
				sum++;
			}
			
			i++;
		}
		
		return sum;
	}
	
	//Odpalanae na poczatku kazdego simulationStepu
	public void reset()
	{
		//this line is for debug purposes only
		
		iteracja =0;
		
		listaFunkcjiUzytecznosci = new ArrayList<ArrayList<Point>>();

		//dla scenariuszy nie psoiadajacych EV 
		int liczbaHandlowcow=obliczLiczbaKont();
	
		
		int a=0;
		while (a<liczbaHandlowcow)
		{
			listaFunkcjiUzytecznosci.add(new ArrayList<Point>());
			a++;
		}
		
		priceVectorsList = new ArrayList<ArrayList<Float>>();
		
		rynekHistory.reset(liczbaHandlowcow);
		
	}
	
	ArrayList<Float> pierwszaPredykcjaNormal()
	{
		//podja predykcje cen z generatora
		if (Stale.cenyZGeneratora)
		{
			ArrayList<Float> listaSumarycznejGeneracji = listaProsumentowWrap.getListaSumarycznejGeneracji(LokalneCentrum.getTimeIndex());
			ArrayList<Float> listaSumarycznejKonsumpcji = listaProsumentowWrap.getListaSumarycznejKonsumpcji(LokalneCentrum.getTimeIndex());

			return predictPrice(listaSumarycznejGeneracji,listaSumarycznejKonsumpcji);
		}
		else
		{
			ArrayList<Float> proposedPriceVector = pierwszaPredykcjaWezPredykcjeZListy();
			
			//jezeli brakuje elementow w plikyu do pelnej predykcji an horyzont czasowy to uuzplenij dnaymi z modelu
			if (proposedPriceVector.size()<Stale.horyzontCzasowy)
			{
				ArrayList<Float> listaSumarycznejGeneracji = listaProsumentowWrap.getListaSumarycznejGeneracji(LokalneCentrum.getTimeIndex());
				ArrayList<Float> listaSumarycznejKonsumpcji = listaProsumentowWrap.getListaSumarycznejKonsumpcji(LokalneCentrum.getTimeIndex());
				ArrayList<Float> cenyZmodelu =predictPrice(listaSumarycznejGeneracji,listaSumarycznejKonsumpcji);
				
				proposedPriceVector = polaczListy(proposedPriceVector, cenyZmodelu);
				
				if (proposedPriceVector.size()!=Stale.horyzontCzasowy)
				{
					getInput("ERROR in pierwszaPredykcjaNormal");
				}
			}
			
			return proposedPriceVector;
			//podaj predykcje taka jak wynika z podanego pliku
			//getInput("Fill this part out - wczytaj rpedykcje z pliku!");
			//return new ArrayList<Float>();
		}
	}
	
	//dopisyuwanie zawsze do L1
	ArrayList<Float> polaczListy (ArrayList<Float> L1, ArrayList<Float> L2)
	{
		int i=L1.size();
		while(i<Stale.horyzontCzasowy)
		{
			L1.add(L2.get(i));
			i++;
		}
		
		return L1;
	}
	
	//tworzy list edlugosci Stale.horyzont czasowy z wczytanych danych przyjmujac za pierwszy element
	ArrayList<Float> pierwszaPredykcjaWezPredykcjeZListy()
	{
		ArrayList<Float> L1 = new ArrayList<Float>();
		
		int i=0;
		while (i<Stale.horyzontCzasowy && (LokalneCentrum.getTimeIndex()+i)<listaCenWczytanaZPliku.size())
		{
			L1.add(listaCenWczytanaZPliku.get(LokalneCentrum.getTimeIndex()+i));
			i++;
		}
		
		return L1;
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
				wartoscDoDodania=wartoscDoDodania*Stale.cenaDystrybutoraZewnetrznego;
			}
			 
			wartoscDoDodania=Math.max(wartoscDoDodania,expislon);
			wartoscDoDodania=Math.min(wartoscDoDodania,Stale.cenaDystrybutoraZewnetrznego-expislon);
			
			L1.add(wartoscDoDodania);
				
			a++;
		}
		
		return L1;
	}
	
	//zwraca wektor predykcji peirwszych cen
	ArrayList<Float> pierwszaPredykcja()
	{
		if (Stale.scenariusz<100)
		{
			return pierwszaPredykcjaNormal();
		}
		else
		{
			//dla scenariusza testowego cnea predykcji jest ustawiana na 0.30
			ArrayList<Float> L1 = new ArrayList<>();
			L1.add(0.30f);
			return L1;
		}
	}

	//TODO
	//pierwsza predykcja - poczatek procesu handlu
	public ArrayList<ArrayList<Float>> predykcjaCenNaRynkuLokalnym()
	{
		float cenaMinimalnaNaRynkuLokalnym= Stale.cenaMinimalnaNaRynkuLokalnym;
		float cenaMaksymalnaNaRynkuLokalnym = Stale.cenaDystrybutoraZewnetrznego;
		
		//tu siedzi to rozroznienie czy z generatora czy predykcja z poprzendiej symualcji
		ArrayList<Float> normalnaPredykcja = pierwszaPredykcja();
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
		rynekHistory.dodajZadeklarowanaCene(normalnaPredykcja.get(0));
		
		
		return listaPredykcjiCen;	
	}
	
	//see addPricePoint to understand the use
	void addPricePointAndListNotEmpty(Prosument prosument, Point point, ArrayList<Point> pointList)
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
					//jezeli a>0 to przestaw alfe i bete poprzednikowi dodawanego punktu
					if (a>0)
					{
						updateAlfa(pointList.get(a-1),point,prosument,pointList,"poprzednik dodawanego punktu");
					}
					
					//ustaw alfe i bete dodawanego punktu
					updateAlfa(point,pointList.get(a),prosument,pointList,"dodawany punkt");
					pointList.add(a, point);
					notFound=false;	
				}	
				a++;
			}
			
			if (notFound)
			{
				updateAlfa(pointList.get(pointList.size()-1),point,prosument, pointList,"not found");
				pointList.add(point);
			}
		}
		
	}
	
	//point jaki chce dodac prosument
	public void addPricePoint(Prosument prosument, Point point)
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
		
		rynekHistory.addPointToHistoryFunkcjaUzytecznosci(idListy, listaFunkcjiUzytecznosci);
		
	}
	
	//dodaje punkt do konta kaumulatora EV
	public void addPricePointEV(ProsumentEV prosumentEV, Point point)
	{
		int idListy = prosumentEV.getID()-1;
		
		//konta EV znajduja sie po kontach prosumentow
		idListy+=ListaProsumentowWrap.getInstance().getListaProsumentow().size();
		
		ArrayList<Point> pointList = listaFunkcjiUzytecznosci.get(idListy);		
		
		if (pointList.size()==0)
		{
			pointList.add(point);
		}
		else
		{
			addPricePointAndListNotEmpty(prosumentEV, point, pointList);
		}
		
		rynekHistory.addPointToHistoryFunkcjaUzytecznosci(idListy, listaFunkcjiUzytecznosci);
		
		
		
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

	public void updateAlfa(Point p1, Point p2, Prosument prosument, ArrayList<Point> pointList)
	{
		updateAlfa(p1, p2, prosument, pointList,"");
	}

	
	//p2 - nastepnik w sensie ceny , p1
	public void updateAlfa(Point p1, Point p2, Prosument prosument, ArrayList<Point> pointList,String note)
	{
		float roznicaEnergii = p1.getIloscEnergiiDoKupienia()-p2.getIloscEnergiiDoKupienia();
		float roznicaCen = (p1.getPrice()-p2.getPrice());
		float alfa = roznicaEnergii / roznicaCen;
		
		float beta = p1.getIloscEnergiiDoKupienia()-alfa * p1.getPrice();
		
		//sprawdza sensownosc
		if (alfa > Stale.malaLiczba && isAlfaCheck)
		{
			
			
			print ("-----------\nalfa error start\n");
			print("\nERROR\n-----------------");
			print ("Wraz ze wzrastjaaca cena chec nabywcza powinna spadac => alfa mniejsza rowna zero");
			print("Roznica energii "+roznicaEnergii+" POWINNA BYC WIEKSZA OD ZERA");
			print("roznicaCen "+roznicaCen+" POWINNA BYC MNIEJSZA OD ZERA");
			print("\nprosument ID: "+prosument.getID());
			print("NOTE "+note);
			print("alfa "+alfa);
			print("iteration "+iteracja);
			print("time "+LokalneCentrum.getCurrentDay()+" "+LokalneCentrum.getCurrentHour());
			print("time index "+LokalneCentrum.getTimeIndex());
			print("Scenariusz" +Stale.scenariusz);
			print("Ceny z generatora" +Stale.cenyZGeneratora);
			
			
			print("\n");

			
			print ("\np1 "+p1.toString()+"\n");
			print ("\np2 "+p2.toString()+"\n");
			print ("\n\n"+pointListToString(pointList));
			getInput("updateAlfa - error");
			
		}
		
		p1.setAlfa(alfa);
		p1.setBeta(beta);
	}
	
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
	
	/*
	void ustawContinuationStatus()
	{
		Boolean isDebug=false;
		
		continuationStatus =0;
		ArrayList<Float> listaCen = znajdzOstatecznaCeneCenaNaNajblizszeSloty();
		
		Boolean isWystepujeDodatnie = false;
		Boolean isWystepujeUjemne = false;
		
		int i=0;
		while (i<listaCen.size())
		{
			//print();
			float cena =listaCen.get(i);
			float value = funkcjaRynku2(cena);
			
			print (cena+" "+value,isDebug);
			
			if (value<0)
			{
				isWystepujeUjemne=true;
			}
			
			if (value>0)
			{
				isWystepujeDodatnie=true;
			}
			
			i++;
		}
		
		if(isWystepujeDodatnie && !isWystepujeUjemne)
		{
			continuationStatus=1;
			print("ustawContinuationStatus ",isDebug);
		}
		
		if(!isWystepujeDodatnie && isWystepujeUjemne)
		{
			continuationStatus=-1;
			print("ustawContinuationStatus ",isDebug);
		}
		
		getInput("ustawContinuationStatus - end",isDebug);
		 
	}*/
	
	//TODO wyznacz cene
	public void wyznaczCeneAndBroadcast()
	{

		
		if (iteracja<limitIteracji)
		{	
			float cena =wyznaczCene();
			
			//spreparuj vector cen
			ArrayList<Float> priceVector = new ArrayList<Float> (priceVectorsList.get(priceVectorsList.size()-1));
			priceVector.set(0, cena);
			
			//to musi byc tu a nie nizej - inaczej bedzie endless loop na if (iteracja <)
			iteracja++;

			BroadcastCenaAmongProsuments(priceVector);

		}
		else
		{
			//getInput("doszedlem do kontraktow");
			//getInput("end");
			if (Stale.scenariusz>100)
			{
				getInput("Scenariusz testowy wiec bez rozpisywania kontraktow");
			}
			
			rozpiszKontrakty();
			
			rynekHistory.reportHistorieCen();	
			rynekHistory.ustawStatusWyjsciaZHandlu(listaFunkcjiUzytecznosci.get(0).size());
			rynekHistory.createHandelReport();
			
			Boolean b1=false;
			if (LokalneCentrum.getCurrentHour().equals("03:00"))
			{
				b1 = false;
			}
			
			rynekHistory.createFunkcjeKontraktyReport(b1);
			
			
			//getInput("wyznaczCeneAndBroadcast -rozpisz kontrakty -end");
			
		}
	}
	
	void BroadcastCenaAmongProsuments(ArrayList<Float> priceVector)
	{
		//getInput("BroadcastCenaAmongProsuments");
		ArrayList<Prosument> L1 =listaProsumentowWrap.getListaProsumentow(); 
		
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
	
	float wyznaczCene()
	{
		if (Stale.scenariusz<100)
		{
			return wyznaczCeneActual3(false);
		}
		else
		{
			return wyznaczCeneActual3(true);
		}
	}
	
	//TODO
	
	float wyznaczCeneActual3(Boolean debug)
	{
		
		//wszystkie ceny sa tkaie same wiec mozna wziac pierwszego prosumenta i od neigo pobrac cene
		ArrayList<Point> L1 =listaFunkcjiUzytecznosci.get(0);
		
		float leftBound = L1.get(0).getPrice();
		float rightBound = L1.get(L1.size()-1).getPrice();
		
		
		//wyznacz zbior punktow dla ktorych cena minimalna jest najmniejsza 
		
		//zbiot probek - cena i funckja Rynkowa
		ArrayList<Point> zbiorProbek = new ArrayList<>();
		
		//zbior probke przyjmujacych najwyzsza warotsc
		ArrayList<Point> zbiorMaxProbek = new ArrayList<>();
		
		float MaxValue=-1;
		int i=0;
		
		float step = (rightBound-leftBound)/Stale.iloscRownomiernychPodzialow;
		float cenaContender =leftBound;
		while (cenaContender<=rightBound)
		{
			Point p = funkcjaRynku2(cenaContender);
			
			//ilosc energii przehcowuje indeks
			p.setIloscEnergiiDoKupienia(i);
			zbiorProbek.add(p);
			
			//w pierwszej tieracji zawsze uznaj ze pierwsza wartosc jest najwieksza
			if (cenaContender ==leftBound)
			{
				MaxValue = p.getBeta();
				zbiorMaxProbek.add(p);
			}
			
			//jezeli wartosc funkcji rynku jest wieksza niz max to zrob max z obecnej i zainicjuj nowa liste
			if (MaxValue < p.getBeta())
			{
				MaxValue = p.getBeta();
				zbiorMaxProbek.clear();
				zbiorMaxProbek.add(p);
			}
			else
			{
				if (MaxValue == p.getBeta())
				{
					zbiorMaxProbek.add(p);
				}
				//jezlei funckja 
			}
			
			cenaContender +=step;
		}
	
		
		//Wyznacz cene
		int kierunekZmian =(int)zbiorMaxProbek.get(0).getAlfa();
		
		float finalnaCena;
		
		//jezeli kierunek zmian >0 to przeaga kupujacych czyli podwyz cene
		//wez ostatnia cyfre z listy
		if (kierunekZmian>0)
		{
			finalnaCena = zbiorMaxProbek.get(zbiorMaxProbek.size()-1).getPrice();
		}
		else
		{
			finalnaCena = zbiorMaxProbek.get(0).getPrice();
		}
		
		
		//getInput("wyznaczCeneActual3 -wait");
		
		return finalnaCena;
	}
	
	float wyznaczCeneActual2(Boolean debug)
	{
		
		//dane do testowania
		//mnozone razy debug
		Boolean welcome = false && debug;
		Boolean funkcjaRynku = false && debug;
		Boolean bisectionStep = false && debug;
		Boolean bisectionEnd = true && debug;
		Boolean rownomienryPodzial = true && debug;


		
		//printListaFunkcjiUzytecznosci(1);
		//printListaFunkcjiUzytecznosci(2);
		
		//wszystkie ceny sa tkaie same wiec mozna wziac pierwszego prosumenta i od neigo pobrac cene
		ArrayList<Point> L1 =listaFunkcjiUzytecznosci.get(0);
		
		float leftBound = L1.get(0).getPrice();
		float rightBound = L1.get(L1.size()-1).getPrice();
		

		
		if (welcome)
		{
			print ("Welcome to wyznaczCeneActual");
			print ("leftBound "+leftBound+" ");
			print ("rightBound "+rightBound+" ");
			getInput();
		}
		
				
		float valueToBeReturned = wyznaczCeneRownomiernyPodzial(leftBound,rightBound,rownomienryPodzial,false);
		
		if (bisectionEnd)
		{
			print("Bisection end #2");
			print ("return " +valueToBeReturned);

			getInput();
		}
		
		return valueToBeReturned;
	}
	
	/*
	float funkcjaRynku(float cena)
	{
		return funkcjaRynku(cena, false);
	}
	
	
	//zwroc funkcje rynku - suam wszysktich checi zakupu ponniejszone o chec wszystkich sprzedazy	
	float funkcjaRynku(float cena, Boolean debug)
	{
		return funkcjaRynku(cena, debug,false);
	}*/
	
	float funkcjaRynku2(float cena, Boolean debug)
	{
		return funkcjaRynku2(cena, debug, false);
		
	}
	
	float funkcjaRynku2(float cena, Boolean debug, Boolean kontraktReport)
	{
		//ustala index dla ktorego point.cena <cena i jest to anjwiekszy tkai indeks
		int priceIndex = funkcjaRynkuGetIndex(cena);
		
		//suma powinna byc ujemna, bo chec sprzedazy w punktach ejst rpzetryzmywnaa jako lcizby ujemne
		float sumaSprzedazy = funkcjaRynkuSumaSprzedazy(priceIndex,cena);
		float sumaKupna = funkcjaRynkuSumaKupna(priceIndex,cena);
		
		if (kontraktReport)
		{
			rynekHistory.kontraktDodajWartoscSumySprzedazy(sumaSprzedazy,cena);
			rynekHistory.kontraktDodajWartoscSumyKupna(sumaKupna, cena);
		}
		
		return Math.min(sumaKupna,-sumaSprzedazy);
	}
	
	
	//zwraca punkt postaci
	//cena -cena
	//alfa  - przewaga 1 - kupujacych, -1 sprzedajacych
	//beta wartosc funkcji rynku
	Point funkcjaRynku2(float cena)
	{
		Point p =new Point();
		p.setPrice(cena);
		
		//ustala index dla ktorego point.cena <cena i jest to anjwiekszy tkai indeks
		int priceIndex = funkcjaRynkuGetIndex(cena);
		
		//suma powinna byc ujemna, bo chec sprzedazy w punktach ejst rpzetryzmywnaa jako lcizby ujemne
		float sumaSprzedazy = funkcjaRynkuSumaSprzedazy(priceIndex,cena);
		float sumaKupna = funkcjaRynkuSumaKupna(priceIndex,cena);
		
		p.setAlfa(1);
		
		if (-sumaSprzedazy>sumaKupna)
		{
			p.setAlfa(-1);
		}
		
		p.setBeta(Math.min(sumaKupna,-sumaSprzedazy));
		
		return p;
	}
	
	
	
	float funkcjaRynkuSumaSprzedazy(int index,float cena)
	{
		return funkcjaRynkuSumaXXX(index, cena, true);
	}
	
	float funkcjaRynkuSumaKupna(int index,float cena)
	{
		return funkcjaRynkuSumaXXX(index,  cena, false);
	}
	
	float funkcjaRynkuSumaXXX(int index, float cena, Boolean sprzedaz)
	{
		float sum=0f;
		int a=0;
		
		//print ("funkcjaRynkuSumaXXX "+listaFunkcjiUzytecznosci.size() );
		
		//listaFunkcjiUzytecznosci.size() zamiast Stale.liczbaProsumentow, bo dla scenariuszy Ev jest wiecejkont
		while (a<listaFunkcjiUzytecznosci.size())
		{
			//lista punktow prosumenta a
			ArrayList<Point> L1 =listaFunkcjiUzytecznosci.get(a);
			
			//punkt  zktorego nalezy brac alfe i bete
			Point p = L1.get(index);
			
			float energiaPoInterpolacji = p.getAlfa()*cena+p.getBeta(); 
			
			//print(a+" "+ energiaPoInterpolacji);
			//getInput();
			
			if (sprzedaz)
			{
				if (energiaPoInterpolacji<0)
				{
					sum+=energiaPoInterpolacji;
				}
			}
			else
			{
				if (energiaPoInterpolacji>0)
				{
					sum+=energiaPoInterpolacji;
				}
			}
			
			a++;
		}
		
		return sum;
	}
	
	int funkcjaRynkuGetIndex(float cena)
	{
		//wektor cen (przed przetworzeniem)
		ArrayList<Point> L1 =listaFunkcjiUzytecznosci.get(0);

		int a=0;
		while (cena>L1.get(a).getPrice())
		{
			a++;
		}
		
		if(a>0)
		{
			a--;
		}
		
		if (a==L1.size())
		{
			a--;	
		}
		
		
		//getInput("funkcjaRynkuGetIndex "+a+" cena "+ cena);
			
		return a;
	}
	

	
	//isminimum= true zwraca mnimum inaczej zwracanae jest maximum
	float wyznaczCeneRownomiernyPodzial(float leftBound, float rightBound, Boolean debug, Boolean isMinimum)
	{
		//jezeli isMinimum =-1 i wszsytkie wartosci suzkanej funkcji sa mnozone przez -1
		//szukajac minimum suzkasz maksimum
		int inverter=1;
		if (!isMinimum)
		{
			inverter=-1;
		}
		
		float step = (rightBound-leftBound)/Stale.iloscRownomiernychPodzialow;
		
		float minimum = leftBound;
		float minimumValue = inverter*funkcjaRynku2(minimum,false);
		
		float cenaContender = minimum+step;
		
		if (debug)
		{
			print("wyznaczCeneRownomiernyPodzial");
			print(leftBound+" "+funkcjaRynku2(minimum,false) );
			print(rightBound+" "+funkcjaRynku2(rightBound,false) );
			
			getInput();
		}
		
		
		while (cenaContender<=rightBound)
		{
			float cenaContenderValue = inverter*funkcjaRynku2(cenaContender,false);
			
			if (cenaContenderValue<minimumValue)
			{
				minimum=cenaContender;
				minimumValue=cenaContenderValue;
			}
			
			cenaContender +=step;
		}
		
		if (debug)
		{
			print("wyznaczCeneRownomiernyPodzial - END");
			print(minimum+" "+funkcjaRynku2(minimum,false) );			
			getInput();
		}
				
		return minimum;
	}
	
	//wydrukuj liste punktow funckji podazy porsumenta ID
	void printListaFunkcjiUzytecznosci(int ID)
	{
		
		print("----------\n printListaFunkcjiUzytecznosci "+ID);
		ID--;
		ArrayList<Point>L1=listaFunkcjiUzytecznosci.get(ID);
		
		int a=0;
		while (a<L1.size())
		{
			Point p =L1.get(a);
			print("\n\n"+p.toString());
			a++;
		}
	}
	
	//TODO rozpisz kotnrakty
	void rozpiszKontrakty()
	{

		//Point point;
		
		float wolumenHandlu = 0f;
		float sumaKupna = 0f;
		float sumaSprzedazy = 0f;
		
		//wybierz taka cene zeby funkcja rynku dazyla do zera
		float cena = znajdzOstatecznaCene2();
		
		ArrayList<Float> priceVector = priceVectorsList.get(priceVectorsList.size()-1);
		priceVector.set(0, cena);
		priceVectorsList.add(priceVector);

		
		//znajdz finalna cene i znajdz jej index w liscie pointow
		int index =rozpiszKontraktyGetIndex(cena);
		
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
			
			
			rynekHistory.ustawAlfaDlaWynikowHandlu(energia, a);
			
			a++;
		}
		
		wolumenHandlu =Math.min(sumaKupna,sumaSprzedazy);
		
		//reporting
		rynekHistory.setReportHandelSumaKupna(sumaKupna);
		rynekHistory.setReportHandelSumaSprzedazy(sumaSprzedazy);
		rynekHistory.setReportHandelWolumenHandlu(wolumenHandlu);
		rynekHistory.dodajWolumenHandlu(wolumenHandlu);
		
		if (Stale.isScenariuszEV)
		{
			rozpiszKontraktyPart2EV( index, wolumenHandlu, sumaKupna, sumaSprzedazy );
		}
		else
		{
			rozpiszKontraktyPart2NoEV( index, wolumenHandlu, sumaKupna, sumaSprzedazy );
		}
		

				
	}
	
	//index - idnex dla ktorego punkt ma cene = cena rykowa
	void rozpiszKontraktyPart2EV(int index,float wolumenHandlu, float sumaKupna,float sumaSprzedazy )
	{
		//wektor z ostatecznie ustalona cena
		//rozpiszKontrakty() - wrzuca jako ostatnia cene cene obowiazujaa na lokalnym rynku 
		ArrayList<Float> priceVector = priceVectorsList.get(priceVectorsList.size()-1);

		
		//ograniczenia handlu prosumenta
		ArrayList<DayData> constrainMarkerList = new ArrayList<DayData>();
		
		//ograniczenia handlu EV
		ArrayList<DayData> constrainMarkerListEV = new ArrayList<DayData>();
		
		//print(listaFunkcjiUzytecznosci.size());
		//getInput("rozpiszKontraktyPart2EV first stop");
		
		int i=0;
		while(i<listaFunkcjiUzytecznosci.size())
		{
			
			//lista funkcji uzytecznosci o indeksie i
			ArrayList<Point> L1	=listaFunkcjiUzytecznosci.get(i);
			
			//point z cena = cena rynkowa
			Point point = L1.get(index);
			
			
			DayData d =rozpiszKontraktyPointToConstrainMarker(point, wolumenHandlu, sumaKupna, sumaSprzedazy, i);
			
			if (i<Stale.liczbaProsumentow)
			{
				constrainMarkerList.add(d);
			}
			else
			{
				constrainMarkerListEV.add(d);
				
				/*print(d.getKupuj());
				print(d.getConsumption());
				print(d.getGeneration());
				
				getInput("rozpiszKontraktyPart2EV - Ostatni kontrakt");*/
			}

			
			//print("rozpiszKontraktyPart2EV "+i);
			i++;
		}
		
		ArrayList<Prosument> listaProsumentow =listaProsumentowWrap.getListaProsumentow();

		//wyywolaj pobranie ontraktu
		i=0;
		while (i<Stale.liczbaProsumentow)
		{
			if (i<constrainMarkerListEV.size())
			{
				((ProsumentEV)listaProsumentow.get(i)).getKontrakt(priceVector,constrainMarkerList.get(i),constrainMarkerListEV.get(i));
				//print("constrainMarkerListEV "+i);
			}
			else
			{
				listaProsumentow.get(i).getKontrakt(priceVector,constrainMarkerList.get(i));
			}
			i++;
		}
		
		//getInput("rozpiszKontraktyPart2EV -end");
	}
	
	//
	DayData rozpiszKontraktyPointToConstrainMarker(Point point,float wolumenHandlu, float sumaKupna,float sumaSprzedazy, int indeksKonta)
	{
		//print("rozpiszKontraktyPointToConstrainMarker idnex "+indeksKonta);
		
		DayData constrainMarker  = new DayData();

		float energia = point.getIloscEnergiiDoKupienia();
		
		
		if (energia>0)
		{
			
			float iloscEnergiiDoKupienia = energia/sumaKupna*wolumenHandlu;
			
			constrainMarker.setKupuj(1f);
			constrainMarker.setGeneration(iloscEnergiiDoKupienia);
			
			rynekHistory.ustawBetaDlaWynikowHandlu(iloscEnergiiDoKupienia,indeksKonta);				
		}
		else
		{
			float iloscEnergiiDoSprzedania = energia/sumaSprzedazy*wolumenHandlu;
			if (wolumenHandlu==0)
			{
				iloscEnergiiDoSprzedania=0;
			}

			
			constrainMarker.setKupuj(0f);
			constrainMarker.setConsumption(-iloscEnergiiDoSprzedania);
			
			/*if (indeksKonta>15)
			{	
				print("iloscEnergiiDoSprzedania "+iloscEnergiiDoSprzedania );
				print("sumaSprzedazy "+sumaSprzedazy);
				print("wolumenHandlu "+wolumenHandlu);
				getInput("rozpiszKontraktyPointToConstrainMarker energia<=0");
			}*/
			
			
			rynekHistory.ustawBetaDlaWynikowHandlu(iloscEnergiiDoSprzedania,indeksKonta);				
		}
		
		
		return 	constrainMarker;

	}

	
	void rozpiszKontraktyPart2NoEV(int index,float wolumenHandlu, float sumaKupna,float sumaSprzedazy )
	{
		//wyznacz wolumen handlu dla kazdego z prosumentow
		ArrayList<Prosument> listaProsumentowTrue =listaProsumentowWrap.getListaProsumentow();
		

		int a=0;
		while (a<listaProsumentowTrue.size())
		{
						
			// ustala bianrke kupuj
			// ustala clakowita sprzedaz (jako consumption)
			//ustala calkowite kupno (jako generacje)
			DayData constrainMarker  = new DayData();
			
			ArrayList<Point> L1	=listaFunkcjiUzytecznosci.get(a);
			
			//energia jaka zadeklarowal prosument ze sprzeda/kupi
			float energia = L1.get(index).getIloscEnergiiDoKupienia();
			
			if (energia>0)
			{
				float iloscEnergiiDoKupienia = energia/sumaKupna*wolumenHandlu;
				
				constrainMarker.setKupuj(1);
				constrainMarker.setGeneration(iloscEnergiiDoKupienia);
				
				rynekHistory.ustawBetaDlaWynikowHandlu(iloscEnergiiDoKupienia,a);				
			}
			else
			{
				float iloscEnergiiDoSprzedania = energia/sumaSprzedazy*wolumenHandlu;

				
				constrainMarker.setKupuj(0);
				constrainMarker.setConsumption(iloscEnergiiDoSprzedania);
				
				rynekHistory.ustawBetaDlaWynikowHandlu(iloscEnergiiDoSprzedania,a);				
			}
			
			ArrayList<Float> priceVector = priceVectorsList.get(priceVectorsList.size()-1);
			
			//poinformuj prosumenta o wyniakch handlu i dostosuj go do wynikow
			listaProsumentowTrue.get(a).getKontrakt(priceVector,constrainMarker);
			
			a++;
		}
	}
	
	//zwraca liste wszsytkich cen jakie byly deklarowane na najblizsyz slot
	//takze na granicach przedzialu
	ArrayList<Float> znajdzOstatecznaCeneCenaNaNajblizszeSloty()
	{
		//getInput("znajdzOstatecznaCeneCenaNaNajblizszeSloty -start");
		
		
		//wez wszystkie ceny 
		//moze byc brane od dowolnego prosumenta wiec 0 tez jest ok
		ArrayList<Point> L1 = listaFunkcjiUzytecznosci.get(0);
		
		ArrayList<Float> L2 = new ArrayList<Float>();
		
		int i=0;
		while (i<L1.size())
		{
			float cena = L1.get(i).getPrice();
			L2.add(cena);
			//print(cena);
			i++;
		}
		
		//print (Arrays.toString(L2.toArray()));
		
		//getInput("znajdzOstatecznaCeneCenaNaNajblizszeSloty -end");
		
		return L2;
	}
	
	//wywolywane przy rozpisywaniu kontrakotw
	//znajduje cene dla ktorej funckja rynku jest najblizej zera
	float znajdzOstatecznaCene()
	{
		//getInput("znajdzOstatecznaCene");
		//print ("znajdzOstatecznaCene "+iteracja);
		
		//debug
		Boolean debug = false;
		if (LokalneCentrum.getCurrentHour().equals("03:00"))
		{
			print("03:00 on the clock",debug);
			debug=false;
		}
		
		//wszystkie ceny jakie byly oglaszan ne na najblizszy slot w 
		ArrayList<Float> cenyNaNajblizszySlot =znajdzOstatecznaCeneCenaNaNajblizszeSloty();
		
		
		//Stworzenie cen w raportowaniu
		int i=0;
		while (i<cenyNaNajblizszySlot.size())
		{
			rynekHistory.kontraktDodajCene(cenyNaNajblizszySlot.get(i));
			i++;
		}
		
		print("ceny na najblizszy slot "+cenyNaNajblizszySlot.size());

		
		//do rpzerobienia problemu minimalizacji na maksymalizacje
		int inverter =-1;
		
		i=0;
		float cena  =cenyNaNajblizszySlot.get(i);
		float minimuCena =cena;		
		float minimumValue =inverter*funkcjaRynku2(cena, false,true);
		i++;
		
		rynekHistory.kontraktDodajWartoscFunkcjiRynku(funkcjaRynku2(cena, false), minimuCena);
		
		while (i<cenyNaNajblizszySlot.size())
		{
			cena  =cenyNaNajblizszySlot.get(i);
			float value =inverter*funkcjaRynku2(cena, false,true);			

			rynekHistory.kontraktDodajWartoscFunkcjiRynku(funkcjaRynku2(cena, false), cena);

			if (value<minimumValue)
			{
				minimuCena =cena;
				minimumValue = value;

			}
			
			i++;
		}
		
		if(debug)
		{
			getInput("03:00 end");
		}
		
		//getInput("znajdzOstatecznaCene - nto finished");
		return minimuCena;
		
	}
	
	float znajdzOstatecznaCene2()
	{
		//wszystkie ceny jakie byly oglaszan ne na najblizszy slot w 
		ArrayList<Float> cenyNaNajblizszySlot =znajdzOstatecznaCeneCenaNaNajblizszeSloty();
		
		float minimuCena =-1;
		
		//Stworzenie cen w raportowaniu
		int i=0;
		while (i<cenyNaNajblizszySlot.size())
		{
			rynekHistory.kontraktDodajCene(cenyNaNajblizszySlot.get(i));
			i++;
		}
		
		//wyznacz liste ktora ma najwieksza wartosc funkcji rynkowej
		
		ArrayList<Float> cenyZNajwiekszaFunkcjaRynkowa = new ArrayList<>();
		float maximumFunkcjiRynkowej=-1;
		
		i=0;
		while (i<cenyNaNajblizszySlot.size())
		{
			float cena  =cenyNaNajblizszySlot.get(i);
			float value =funkcjaRynku2(cena, false,true);			

			rynekHistory.kontraktDodajWartoscFunkcjiRynku(value, cena);

			
			if (value>maximumFunkcjiRynkowej)
			{
				cenyZNajwiekszaFunkcjaRynkowa.clear();
				cenyZNajwiekszaFunkcjaRynkowa.add(cena);
				maximumFunkcjiRynkowej = value;
			}
			else if (value==maximumFunkcjiRynkowej)
			{
				cenyZNajwiekszaFunkcjaRynkowa.add(cena);
			} 
			
			
			i++;
		}
		
		minimuCena = cenyZNajwiekszaFunkcjaRynkowa.get(cenyZNajwiekszaFunkcjaRynkowa.size()/2);
		
		return minimuCena;

	}
	
	int rozpiszKontraktyGetIndex(float cena)
	{


		
		rynekHistory.dodajOstatecznaCeneDoListOstatecznyCen(cena);
		
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

	//debug use only
	void printPriceVectorsList(ArrayList<ArrayList<Float>> L1)
	{
		print("\n=============printPriceVectorsList");
		
		int a=0;
		while (a<L1.size())
		{
			print("a "+a);
			ArrayList<Float> L2 =L1.get(a); 
			
			int b=0;
			while (b<L2.size())
			{
				print(Arrays.toString(L2.toArray()));
				b++;
			}
			
			a++;
		}
	}

}
