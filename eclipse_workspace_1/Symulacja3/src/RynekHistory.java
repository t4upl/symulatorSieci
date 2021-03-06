import java.util.ArrayList;
import java.util.HashMap;


public class RynekHistory extends CoreClass {

	private float reportHandelSumaKupna;
	private float reportHandelSumaSprzedazy;
	private float reportHandelWolumenHandlu;

	
	//historia listy punktow przekazywana przez prosumentow
	private ArrayList<ArrayList<ArrayList<Point>>> historyListaFunkcjiUzytecznosci;
	
	//hisotria cen podawanych jako pierwsze
	//brak zmian przez trwania claosc symulacji
	private ArrayList<Float> historyDeclaredPrice = new ArrayList<Float>();
	
	//historia cen ostatecznie ustlaonych 
	//brak zmian przez trwania claosc symulacji
	private ArrayList<Float> historyFinalPrice = new ArrayList<Float>();
	
	//ostateczny woluemn ahndlach w kolejnyhc iteracjach
	private ArrayList<Float> historyWolumenHandlu = new ArrayList<Float>();
	
	//ile energii chce sprzedac/kupic prosument po cenie rozpisywania kontraktu
	//alfa - ile chce sprzedac/kupic
	//beta - ile kupi/sprzed w efekcie handlu
	//Lista budowany w trakcie nastawiania alf
	ArrayList<Point> reportHandelResults;	
	
	Reporter reporter = Reporter.getInstance();
	
	//mowi dlaczego zakonczyl sie proces handlu w kolejnych iteracjach
	//1 - funckja Rynku stlae powyzej osi - brak habndlu
	//0 - nromalne iteracje
	//-1  - funckja Rynku stale ponizej osi - brak habndlu
	//pojaiwa sie w raporcie cen predykcji vs cen finalnych
	private ArrayList<Integer> koniecHandluList = new ArrayList<Integer>();

	
	//wektor ceny przy wyznaczaniu ceny rynkowej
	//private ArrayList<Float> kontraktCeny;
	
	//wektor funkcji rynku
	//private ArrayList<Float> kontraktyFunkcjeRynku;
	
	//Arraylist uzywajaco jako klucz ceny
	private ArrayList<kontraktElement> kontraktList;
	
	//Singleton shit
	private static RynekHistory instance = null;
	private RynekHistory() 
	{
	}
	
   public static RynekHistory getInstance() {
	      if(instance == null) {
	         instance = new RynekHistory();
	      }
	      return instance;
	}
   
   //TODO
   //---------------------
   //SETTERS
   	
	public void setReportHandelSumaKupna(float value)
	{
		this.reportHandelSumaKupna=value;
	}
	
	public void setReportHandelSumaSprzedazy(float value)
	{
		this.reportHandelSumaSprzedazy=value;
	}
	
	public void setReportHandelWolumenHandlu(float value)
	{
		this.reportHandelWolumenHandlu=value;
	}
   
   //TODO
   //--------------------
   //OTHER FUNCTIONS
	
	public void reset(int liczbaHandlowcow)
	{
		reportHandelResults = new ArrayList<>();
		historyListaFunkcjiUzytecznosci = new ArrayList<ArrayList<ArrayList<Point>>>();
		
		int a=0;
		while (a<liczbaHandlowcow)
		{
			//nie mam pojecia do czego jest ta linia
			reportHandelResults.add(new Point());
			historyListaFunkcjiUzytecznosci.add(new ArrayList<ArrayList<Point>>());
			a++;
		}

		kontraktList = new ArrayList<>();
	}
	
	//dodaj taka cene jaka zadeklarowal rynek (przy incijalizacji procesu handlu)
	//ustawiana przez =>predykcjaCenNaRynkuLokalnym
	
	public void dodajZadeklarowanaCene(float value)
	{
		historyDeclaredPrice.add(value);
	}
	
	public void addPointToHistoryFunkcjaUzytecznosci(int IDprosumenta, ArrayList<ArrayList<Point>> listaFunkcjiUzytecznosci)
	{
		ArrayList<ArrayList<Point>> pointList2 = historyListaFunkcjiUzytecznosci.get(IDprosumenta);	
		
		//dodaj liste uzytecznosci prosumenta o ID = IDprosumenta i dodaj go do zbioru list uzytecznosci 
		//prosuemnta o wybrnaym ID
		pointList2.add(new ArrayList<Point> (listaFunkcjiUzytecznosci.get(IDprosumenta)));
	}
	
	//dodaj wolumen handlu jaki ustlail sie w ostatniej 9iteracji
	public void dodajWolumenHandlu(float value)
	{
		historyWolumenHandlu.add(value);
	}
	
	//dodaje punkt kontraktu do listy i ustawia mu alfe
	public void dodajOstatecznaCeneDoListOstatecznyCen(float value)
	{
		historyFinalPrice.add(value);
	}
	
	//indeks nie ID prosumenta
	public void ustawAlfaDlaWynikowHandlu(float value, int indeks)
	{
		Point p = reportHandelResults.get(indeks);
		p.setAlfa(value);
	}
	
	//indeks nie ID prosumenta
	public void ustawBetaDlaWynikowHandlu(float value, int indeks)
	{
		Point p = reportHandelResults.get(indeks);
		p.setBeta(value);
	}
	
	//tworzy plik pokazujacy jak zmienialy sie funkcje uzytecznsci w kolejnych iteracjach
	public void reportHistorieCen()
	{
		
		if (Stale.isScenariuszEV)
		{
			//print ("reportHistorieCen "+historyListaFunkcjiUzytecznosci.size());
			
			int i=0;
			while (i<historyListaFunkcjiUzytecznosci.size())
			{
				reporter.createPointHistoryReportVer2(historyListaFunkcjiUzytecznosci, i);
				i++;
			}

		}
		else
		{
			reporter.createPointHistoryReport(historyListaFunkcjiUzytecznosci );
		}
		
	}
	
	//tworzy plik z sotatecnzymi wynikami handlu
	public void createHandelReport()
	{
		Point point = new Point();
		point.setAlfa(reportHandelSumaSprzedazy);
		point.setBeta(reportHandelSumaKupna);
		point.setIloscEnergiiDoKupienia(reportHandelWolumenHandlu);
		
		reporter.createHandelReport(point,reportHandelResults);
	}
	
	public void createFirstPriceVSFinalPriceReport()
	{
		/*
		 * nie wiem po co to tu bylo? - prawdopodobnie wczensiej byla gorsza logika
		 * int i=0;
		while (i<koniecHandluList.size())
		{
			
			i++;
		}*/
		
		reporter.reportPierwszeCeny(historyDeclaredPrice, historyFinalPrice, historyWolumenHandlu, koniecHandluList);
	}
	
	
	public void ustawStatusWyjsciaZHandlu(int value)
	{
		if (value==0)
		{
			getInput("ustawStatusWyjsciaZHandlu -warning!");
		}
		
		koniecHandluList.add(value);
	}
	
	//dodja cene i wartosc funckji rynku
	public void kontraktDodajCene(float cena)
	{
		kontraktElement kt = new kontraktElement(cena);
		
		kontraktList.add(kt);
	}
	
	public void kontraktDodajWartoscFunkcjiRynku(float wartoscFunkcji, float cena)
	{
		int index = znajdzIndeksOCenieWKontraktList(cena);
		kontraktList.get(index).wartoscFunkcji=wartoscFunkcji;
	}
	
	public void kontraktDodajWartoscSumySprzedazy(float value, float cena)
	{
		int index = znajdzIndeksOCenieWKontraktList(cena);
		kontraktList.get(index).sumaSprzedazy=value;
	}
	
	public void kontraktDodajWartoscSumyKupna(float value, float cena)
	{
		int index = znajdzIndeksOCenieWKontraktList(cena);
		kontraktList.get(index).sumaKupna=value;
	}
	
	public void createFunkcjeKontraktyReport(Boolean debug)
	{
		if (debug)
		{
			print("createFunkcjeKontraktyReport");

			int i=0;
			while (i<kontraktList.size())
			{
				print(kontraktList.get(i).cena+" "+kontraktList.get(i).wartoscFunkcji);
				i++;
			}
		}
		
		reporter.createFunkcjeKontraktyReport(kontraktList);
	}
	
	//wywoluje na Reporterze wytworzenie raportu postaci
	//cena,warotsc funckji uzytecznosci
	public void createFunkcjeKontraktyReport()
	{
		reporter.createFunkcjeKontraktyReport(kontraktList);
	}
	
	int znajdzIndeksOCenieWKontraktList(float cena)
	{
		int indeks = -1;
		int i=0;
		while (i<kontraktList.size())
		{
			if (kontraktList.get(i).cena==cena)
			{
				return i;
			}
			i++;
		}
		
		getInput("znajdzIndeksOCenieWKontraktList - error");
		return indeks;
		
	}
	
	//zawsze pcozatkiem sturktutury kontrakt element jest cena
	public class kontraktElement{
		float cena;
		float wartoscFunkcji;
		float sumaSprzedazy;
		float sumaKupna;
		
		ArrayList<Float> checSprzedazyProsumentow = new ArrayList<>();
		
		kontraktElement(float cena)
		{
			this.cena=cena;
			
			int i=0;
			while (i<Stale.liczbaProsumentow)
			{
				checSprzedazyProsumentow.add(0f);
				i++;
			}
		}
	}
}
