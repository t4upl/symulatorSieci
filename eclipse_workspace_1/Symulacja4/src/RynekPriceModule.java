import java.util.ArrayList;
import java.util.Arrays;

public class RynekPriceModule extends CoreClass {

	//lewy i prawy zakres cen
	Double leftBound;
	Double rightBound;
	
	ArrayList<ArrayList<Point>> listaFunkcjiUzytecznosci;
	
	ArrayList<Double> posortowanaListaCen;
	
	//Singleton shit
	private static RynekPriceModule instance = null;
	private RynekPriceModule() 
	{
		
	}

	public static RynekPriceModule getInstance() {
	      if(instance == null) {
	         instance = new RynekPriceModule();
	      }
	      return instance;
	}

	//-------------------------------------
	
	public Double getLeftBound() {
		return leftBound;
	}

	public void setLeftBound(Double leftBound) {
		this.leftBound = leftBound;
	}

	public Double getRightBound() {
		return rightBound;
	}

	public void setRightBound(Double rightBound) {
		this.rightBound = rightBound;
	}
	
	//------------------------------------
	
	//TODO
	//------------------------------
	
	//uzywane w wyznacznaiu ceny na rynku lokalnym
	class Probka extends CoreClass
	{
		double cena;
		
		//index w funkcji uzytecznosci
		int index;
		
		//suma po wszysktich kontach
		double sumaSprzedazy=0;
		double sumaKupna=0;
		
		double wartoscFunckjiRynku=0;
		
		//1 jezlei rpzewaga kupujacych, -1 jezeli przewaga sprzedajacych
		int przewagaKupujacych;
		
		public void obliczWartoscFunkcjiRynkuKontrakty()
		{
			int i=0;
			while (i<listaFunkcjiUzytecznosci.size())
			{
				//lista punktow i-tego prosumenta
				ArrayList<Point> list1 =  listaFunkcjiUzytecznosci.get(i);
				
				double energia = list1.get(index).getIloscEnergiiDoKupienia(); 
				
				if (energia<0)
				{
					sumaSprzedazy+=energia;
				}else
				{
					sumaKupna+=energia;
				}
				
				i++;
			}
			
			//suam sprzedazy w petli ejst ujemna, wiec trzeba to odwrocic na wartosc przeciwna
			sumaSprzedazy=-sumaSprzedazy;
			
			//trap
			if (sumaSprzedazy<0)
			{
				getInput("ERROR obliczWartoscFunkcjiRynku sumaSprzedazy<0");
			}
			
			wartoscFunckjiRynku = Math.min(sumaSprzedazy, sumaKupna);
		}
		
		public void obliczWartoscFunkcjiRynku()
		{
			//przejdz po wszysktich kontach
			int i=0;
			while (i<listaFunkcjiUzytecznosci.size())
			{
				//przeanalizuj lsite z punktami dla i-tego prosumenta
				obliczWartoscFunkcjiRynkuPart2(listaFunkcjiUzytecznosci.get(i), i);
				i++;
			}
			
			//suam sprzedazy w petli ejst ujemna, wiec trzeba to odwrocic na wartosc przeciwna
			sumaSprzedazy=-sumaSprzedazy;
			
			//trap
			if (sumaSprzedazy<0)
			{
				getInput("ERROR obliczWartoscFunkcjiRynku sumaSprzedazy<0");
			}
			
			if (sumaKupna>sumaSprzedazy)
			{
				przewagaKupujacych=1;	
			}
			else 
			{
				przewagaKupujacych=-1;
			}
			
			wartoscFunckjiRynku = Math.min(sumaSprzedazy, sumaKupna);
			
			
		}
		
		//indeks for debug only, indeks konta
		void obliczWartoscFunkcjiRynkuPart2(ArrayList<Point> list1, int indeks)
		{
			Point p = list1.get(index);
			double energiaPoInterpolacji = p.getAlfa()*cena+p.getBeta(); 
			
			if (energiaPoInterpolacji<0)
			{
				sumaSprzedazy+=energiaPoInterpolacji;
			}else
			{
				sumaKupna+=energiaPoInterpolacji;
			}
			
			//trap 
			trapObliczWartoscFunkcjiRynkuPart2(p,list1, energiaPoInterpolacji,indeks);


					
		}
		
		void trapObliczWartoscFunkcjiRynkuPart2(Point p, ArrayList<Point> list1, double energiaPoInterpolacji, int indeks)
		{
			if (index+1<list1.size())
			{
				Point p2 = list1.get(index+1);
				
				if (!( p.getPrice()<= cena && cena <= p2.getPrice() ))
				{
					print ("cena "+cena+" should be in range\n");
					print (p.getPrice()+" ; "+ p2.getPrice());
					
					getInput("ERROR in obliczWartoscFunkcjiRynkuPart2");
				}
				
				if (!isValueInRange(p.getIloscEnergiiDoKupienia(),p2.getIloscEnergiiDoKupienia(),energiaPoInterpolacji))
				{
					print("----------\nProblem z interpolacja\n");
					print (p.toString()+"\n");
					print (p2.toString()+"\n");
					print (energiaPoInterpolacji+"\n");
					print(indeks);
					
					
					getInput("ERROR 2 in obliczWartoscFunkcjiRynkuPart2");
				}
				
			}
			
			
		}
		
	
		
	}
	
	//---------------------------------------------------
	
	class Kontrakt 
	{
		//lista zawierajaca przydzialy energii uzyskane w wyniku handlu, liczby ujemne oznaczaja sprzedaz
		ArrayList<Double> listaPrzydzialowEnergii;
		
		double SumaKupna;
		double SumaSprzedazy;
		double wolumenHandlu;

		//idnex pod ktorym w liscie prosuemtnow jest cena finalna
		int index;
		double cenaFinalna;
		
	}
	
	//---------------------------------------------------
	
	//trap
	void cenaNaRynkuLokalnymCheck(double cenaNaRynkuLokalnym)
	{
		if (cenaNaRynkuLokalnym<leftBound || cenaNaRynkuLokalnym>rightBound )
		{
			print("----------");
			print("leftBound "+leftBound);
			print("rightBound "+rightBound);
			print("cena na rynku lokalnym "+cenaNaRynkuLokalnym);

			
			getInput("cenaNaRynkuLokalnymCheck - something went wrong");
		}
	}
	
	ArrayList<Double> wyznaczPosortowanaLiseCen()
	{
		ArrayList<Point> list1 = listaFunkcjiUzytecznosci.get(0);
		ArrayList<Double> output = new ArrayList<>();
		
		int i=0;
		while (i<list1.size())
		{
			double cena  = list1.get(i).getPrice();
			
			//trap
			if (output.size()>0)
			{
				if (!(cena>output.get(output.size()-1)  ))
				{
					getInput("wyznaczPosortowanaLiseCen error");
				}
			}
				
			output.add(cena);
			
			i++;
		}
		
		return output;
	}
	
	//TODO
	public Kontrakt rozpiszKontrakty(ArrayList<ArrayList<Point>> listaFunkcjiUzytecznosci)
	{
		
		Kontrakt kontrakt = new Kontrakt();
		
		this.listaFunkcjiUzytecznosci =listaFunkcjiUzytecznosci;
		posortowanaListaCen = wyznaczPosortowanaLiseCen();
		
		ArrayList<Probka> zbiorProbek = rozpiszKontraktyWyznaczZbiorProbek();
		double najwiekszaWartoscFunkcjiRynku = znajdzNajwiekszaWartoscZeZbiorProbek(zbiorProbek);
		
		//zbior probek o najwiekszej wartosci funkcji rynku
		ArrayList<Probka> zbiorProbekMax = filtrujZbiorProbek(najwiekszaWartoscFunkcjiRynku, zbiorProbek);
		
		
		double finalnaCena = wyznaczFinalnaCeneKontrakty (zbiorProbekMax);
		
		stworzKontraktObliczSumy(kontrakt, finalnaCena);
		stworzKontraktListaTransakcji(kontrakt, finalnaCena);

		return kontrakt;
	}
	
	void stworzKontraktListaTransakcji(Kontrakt kontrakt, double finalnaCena)
	{
		ArrayList<Double> output = new ArrayList<>();
		
		int index = posortowanaListaCen.indexOf(finalnaCena);
		
		int i=0;
		while (i<listaFunkcjiUzytecznosci.size())
		{
			//lista punktow i-teog konta
			ArrayList<Point> list1 = listaFunkcjiUzytecznosci.get(i);
			
			double IloscEnergii  =list1.get(index).getIloscEnergiiDoKupienia();
			
			if (IloscEnergii>0)
			{
				output.add(IloscEnergii/kontrakt.SumaKupna*kontrakt.wolumenHandlu);
			}
			else
			{
				if (kontrakt.wolumenHandlu==0)
				{
					output.add(0.0);
				}else
				{
					output.add(IloscEnergii/kontrakt.SumaSprzedazy*kontrakt.wolumenHandlu);
				}
			}
			
			i++;
		}
		
		kontrakt.listaPrzydzialowEnergii = output;

	}
	
	//oblicza sume sprzedazy, sume kupna, wolument handlu
	void stworzKontraktObliczSumy(Kontrakt kontrakt, double finalnaCena)
	{
		int index = posortowanaListaCen.indexOf(finalnaCena);
		
		//trap
		if (index==-1)
		{
			getInput("ERROR stworzKontrakt");
		}
		
		double SumaKupna = 0;
		
		//powinna byc ujemna
		double SumaSprzedazy = 0;
		double wolumenHandlu = 0;
		
		int i=0;
		while (i<listaFunkcjiUzytecznosci.size())
		{
			//lista punktow i-teog konta
			ArrayList<Point> list1 = listaFunkcjiUzytecznosci.get(i);
			
			double IloscEnergii  =list1.get(index).getIloscEnergiiDoKupienia();
			
			if (IloscEnergii>0)
			{
				SumaKupna+=IloscEnergii;
			}
			else
			{
				SumaSprzedazy+=IloscEnergii;
			}
			
			i++;
		}
		
		SumaSprzedazy=-SumaSprzedazy;
		
		//trpa
		if (SumaSprzedazy<0)
		{
			getInput("Error #2 stworzKontraktObliczSumy ");
		}
		
		wolumenHandlu = Math.min(SumaKupna, SumaSprzedazy);
		
		kontrakt.cenaFinalna =finalnaCena;
		kontrakt.index =index;
		
		kontrakt.SumaKupna = SumaKupna;
		kontrakt.SumaSprzedazy =SumaSprzedazy;
		kontrakt.wolumenHandlu = wolumenHandlu;
		
		
		//getInput("stworzKontraktObliczSumy -end");
		
	}

	
	//ze zbioru probke o maksymalnej warotsic wyznacza cene finalna
	double wyznaczFinalnaCeneKontrakty (ArrayList<Probka> list1)
	{
		return list1.get(list1.size()/2).cena;
	}
	
	ArrayList<Probka> rozpiszKontraktyWyznaczZbiorProbek()
	{
		ArrayList<Probka> zbiorProbek =new ArrayList<>();
		
		int i=0;
		while (i<posortowanaListaCen.size())
		{
			Probka p = rozpiszKontraktyWyznaczZbiorProbekStworzProbke(i);
			zbiorProbek.add(p);
			
			i++;
		}
		
		return zbiorProbek;
	}
	
	Probka rozpiszKontraktyWyznaczZbiorProbekStworzProbke(int indeksZlistyCen)
	{
		Probka probka = new Probka();
		
		probka.cena =posortowanaListaCen.get(indeksZlistyCen);
		probka.index =indeksZlistyCen;
		probka.obliczWartoscFunkcjiRynkuKontrakty();
		return probka;
	}
	
	
	//TODO
	public double wyznaczCeneNaRynkuLokalnym(ArrayList<ArrayList<Point>> listaFunkcjiUzytecznosci)
	{
		//tersting purposes only
		//testStworzProbkeDlaCenyZnajdzIndex();
		
		
		this.listaFunkcjiUzytecznosci =listaFunkcjiUzytecznosci;
		posortowanaListaCen = wyznaczPosortowanaLiseCen();
		
		
		ArrayList<Probka> zbiorProbek = wyznaczZbiorProbek();
		
		double najwiekszaWartoscFunkcjiRynku = znajdzNajwiekszaWartoscZeZbiorProbek(zbiorProbek);
		
		//zbior probek o najwiekszej wartosci funkcji rynku
		ArrayList<Probka> zbiorProbekMax = filtrujZbiorProbek(najwiekszaWartoscFunkcjiRynku, zbiorProbek);
		
		
		double finalnaCena = wyznaczFinalnaCene (zbiorProbekMax);
		
		return finalnaCena;
	}
	
	double wyznaczFinalnaCene(ArrayList<Probka> zbiorProbekMax)
	{
		//sprawdz czy w zbiorze max warotsci jest wiecej probek ktore
		int sumaPrzewag =0;
		
		int i=0;
		while (i<zbiorProbekMax.size())
		{
			sumaPrzewag+=zbiorProbekMax.get(i).przewagaKupujacych;
			i++;
		}
		
		double output =-1;
		if (sumaPrzewag>0)
		{
			output = zbiorProbekMax.get(zbiorProbekMax.size()-1).cena;
		}else
		{
			output = zbiorProbekMax.get(0).cena;
		}
		
		if (!isValueInRange(leftBound, rightBound, output))
		{
			getInput("ERROR in wyznaczFinalnaCene");
		}
		
		return output;
	}
	
	ArrayList<Probka> filtrujZbiorProbek(double najwiekszaWartoscFunkcjiRynku, ArrayList<Probka> zbiorProbek ) 
	{
		ArrayList<Probka> output = new ArrayList<>();
		
		int i=0;
		while (i<zbiorProbek.size())
		{
			if (zbiorProbek.get(i).wartoscFunckjiRynku == najwiekszaWartoscFunkcjiRynku)
			{
				output.add(zbiorProbek.get(i));
			}
			
			i++;
		}
		
		return output;
	}
	
	
	double znajdzNajwiekszaWartoscZeZbiorProbek(ArrayList<Probka> list1)
	{
		double maxValue=0;
		
		int i=0;
		while (i<list1.size())
		{
			Probka probka =list1.get(i);
			
			if (maxValue<probka.wartoscFunckjiRynku)
			{
				maxValue =probka.wartoscFunckjiRynku;
			}
			
			i++;
		}
		
		return maxValue;
	}
	
	
	
	ArrayList<Probka> wyznaczZbiorProbek()
	{

		//print("start");
		
		ArrayList<Probka> zbiorProbek = new ArrayList<>(); 
		
		double step = (rightBound-leftBound)/Stale.iloscRownomiernychPodzialow;
		double cenaContender =leftBound;
		while (cenaContender<=rightBound)
		{
			
			Probka p = stworzProbkeDlaCeny(cenaContender);
			zbiorProbek.add(p);
			
			cenaContender +=step;
		}		
		
		return zbiorProbek;
	}
	
	Probka stworzProbkeDlaCeny(double cena)
	{
		Probka probka =new Probka();
		
		probka.cena =cena;
		
		probka.index =stworzProbkeDlaCenyZnajdzIndex(cena);
		
		probka.obliczWartoscFunkcjiRynku();
		
		return probka;
	}
	
	int stworzProbkeDlaCenyZnajdzIndex(double cena)
	{
		int i=0;
		while (cena>posortowanaListaCen.get(i+1))
		{
			i++;
			
			if (i==posortowanaListaCen.size())
			{
				getInput("ERROR stworzProbkeDlaCenyZnajdzIndex");
			}
		}
		
		return i;
		
	}
	
	//test sprawdzajacy dzialanie FunkcjaRynkuGetIndex
	public void testStworzProbkeDlaCenyZnajdzIndex()
	{
		print ("testFunkcjaRynkuGetIndex -start");
		
		posortowanaListaCen = new ArrayList<>(Arrays.asList(2.0, 4.0, 7.0));
		
		//expected output = "0,0,0,1,1,1 "
		print(stworzProbkeDlaCenyZnajdzIndex(2) +" 2" );
		print(stworzProbkeDlaCenyZnajdzIndex(3) + " 3");
		print(stworzProbkeDlaCenyZnajdzIndex(4) +" 4");
		print(stworzProbkeDlaCenyZnajdzIndex(5) +" 5");
		print(stworzProbkeDlaCenyZnajdzIndex(6) +" 6");
		print(stworzProbkeDlaCenyZnajdzIndex(7) + " 7");
		
		
		getInput("testFunkcjaRynkuGetIndex -end");
	}
	
	
	

	
	
}
