import java.util.ArrayList;
import java.util.Arrays;



public class Rynek extends CoreClass {

	private int liczbaProsumentow=Stale.liczbaProsumentow;
	
	//lista punktow przekazana przez prosumentow
	private ArrayList<ArrayList<Point>> listaFunkcjiUzytecznosci;
	
	//lista profili cen -> zawiera kolejne predykcje (wektor dlugosci 24) ze zmieniajaca sie predykcja na najblizszy slot
	//na ostatnim miejscu ostatnio zadeklarowana lista
	//vectory cen dodawanee przy predykcji i przy BroadcastCenaAmongProsuments
	//kazda lista zawiera predykcje na 24 godziny
	//nie zawiera predykcji dla cen z zakresu dostpenych cen (ceny minimalnej i maksymlanej) 
	private ArrayList<ArrayList<Float>> priceVectorsList;
	
	
	//uzywana do ograniczenia ilosci komunikatow miedzy rynkiem a prosumentami
	int iteracja;
	
	//jezeli funkcja rynku nie ma przeciecia z osia x to nie ma sensu prowadzic handlu
	//continuation status okresla jako 0 - jest przeciecie,  1- funkcja stale nad
	//ustaiwane przy pierwszym rozeslaniu cen
	//ustawiane w f:"ustawContinuationStatus"
	//int continuationStatus;

	//limit iteracji rynek -podjae cene, prosument odpowiada
	//w sklad limitu nie whchodzi pierwsza cena (podanie 3 predykcji)
	final int limitIteracji=Stale.limitIteracji;
	final int limitIteracjiBisekcji=Stale.limitBisekcji;
	
	//SYSTEM
	//keeps track of data for reporting
	RynekHistory rynekHistory =RynekHistory.getInstance();

	ListaProsumentowWrap listaProsumentowWrap = ListaProsumentowWrap.getInstance();
	
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
   
	//TODO
	//wykonaj akcje pod koniec symulacji
	public void endOfSimulation()
	{
		//print("dOPISZ!");
		rynekHistory.createFirstPriceVSFinalPriceReport();
		//reporter.reportPierwszeCeny(historyDeclaredPrice, historyFinalPrice, historyWolumenHandlu);
	}
	
	//-----------------------
	//TODO
	//GETTERS
	
	public int getIteracja()
	{
		return this.iteracja;
	}
	
	
	//Odpalanae na poczatku kazdego simulationStepu
	public void reset()
	{
		//this line is for debug purposes only
		
		iteracja =0;
		
		listaFunkcjiUzytecznosci = new ArrayList<ArrayList<Point>>();

		int a=0;
		while (a<Stale.liczbaProsumentow)
		{
			listaFunkcjiUzytecznosci.add(new ArrayList<Point>());
			a++;
		}
		
		priceVectorsList = new ArrayList<ArrayList<Float>>();
		
		rynekHistory.reset();
		
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
			//podaj predykcje taka jak wynika z podanego pliku
			getInput("Fill this part out - wczytaj rpedykcje z pliku!");
			return new ArrayList<Float>();
		}
		
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
		float alfa = p1.getIloscEnergiiDoKupienia()-p2.getIloscEnergiiDoKupienia();
		alfa = alfa / (p1.getPrice()-p2.getPrice());
		
		float beta = p1.getIloscEnergiiDoKupienia()-alfa * p1.getPrice();
		
		if (alfa > Stale.malaLiczba)
		{
			print("\nERROR\n-----------------");
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
		//w pierwszej iteracji sprawdz czy jest sens poszukiwac
		//d keidy wyleciala bisekcja to tez wylecialo
		/*if (iteracja==0)
		{
			ustawContinuationStatus();
		}*/
		
		if (iteracja<limitIteracji)
		{	
			float cena =wyznaczCene();
			//print (cena);
			
			//spreparuj vector cen
			ArrayList<Float> priceVector = new ArrayList<Float> (priceVectorsList.get(priceVectorsList.size()-1));
			priceVector.set(0, cena);
			
			//to musi byc tu a nie nizej - inaczej bedzie endless loop na if (iteracja <)
			iteracja++;

			BroadcastCenaAmongProsuments(priceVector);

		}
		else
		{
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
			return wyznaczCeneActual2(false);
		}
		else
		{
			return wyznaczCeneActual2(true);
		}
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
	
	/*
	float funkcjaRynku(float cena, Boolean debug, Boolean kontraktReport)
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
		
		if (debug)
		{
			print("funkcjaRynku "+cena);
			print("Suma sprzedazy "+sumaSprzedazy);
			print("Suma kupna "+sumaKupna);
			print ("f(x) "+(sumaKupna+sumaSprzedazy));
			
			getInput();
		}
		
		return sumaKupna+sumaSprzedazy;
	}*/
	
	
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
		while (a<Stale.liczbaProsumentow)
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
	
	/*
	//TODO
	float wyznaczCeneActual(Boolean debug)
	{
		
		if (LokalneCentrum.getCurrentHour().equals("11:00"))
		{
			//print("wyznaczCeneActual iteracja "+iteracja);
			//getInput("Welcome to 11");
			//debug=true;
		}
		
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
		
		float leftValue =funkcjaRynku(leftBound,funkcjaRynku);
		float rightValue =funkcjaRynku(rightBound,funkcjaRynku);
		
		float srodek=-1;
		float srodekValue=-1;
		
		if (welcome)
		{
			print ("Welcome to wyznaczCeneActual");
			print ("leftBound "+leftBound+" "+leftValue);
			print ("rightBound "+rightBound+" "+rightValue);
			getInput();
		}
		
		int a=0;
		while (a<Stale.limitBisekcji)
		{

			
			srodek = (leftBound+rightBound)/2;
			srodekValue = funkcjaRynku(srodek,funkcjaRynku);
			
			if (bisectionStep)
			{
				print("Biserkcja: "+a);
				print ("leftBound "+leftBound+" "+leftValue);
				print ("rightBound "+rightBound+" "+rightValue);
				print("srodek "+srodek+" "+srodekValue);
				getInput("");
				
			}
			
			if (srodekValue>0)
			{
				leftBound = srodek;
				leftValue = srodekValue;
			}
			else
			{
				rightBound = srodek;
				rightValue = srodekValue;
			}
			
			
			a++;
		}
		
		if (bisectionEnd)
		{
			print("Bisection end");
			print (srodek+" "+srodekValue);
			print ("leftBound "+leftBound+" "+leftValue);
			print ("rightBound "+rightBound+" "+rightValue);
			getInput();
		}
		
		float valueToBeReturned = wyznaczCeneRownomiernyPodzial(leftBound,rightBound,rownomienryPodzial);
		
		if (bisectionEnd)
		{
			print("Bisection end #2");
			print ("return " +valueToBeReturned);

			getInput();
		}
		
		return valueToBeReturned;
	}*/
	
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
		float cena = znajdzOstatecznaCene();
		
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
		
		
		//wyznacz wolumen handlu dla kazdego z prosumentow
		ArrayList<Prosument> listaProsumentowTrue =listaProsumentowWrap.getListaProsumentow();
		

		a=0;
		while (a<listaProsumentowTrue.size())
		{
						
			// ustala bianrke kupuj
			// ustala clakowita sprzedaz (jako generacje)
			//ustala calkowite kupno (jako consumption)
			DayData constrainMarker  = new DayData();
			
			L1	=listaFunkcjiUzytecznosci.get(a);
			
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
			
			priceVector = priceVectorsList.get(priceVectorsList.size()-1);
			
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