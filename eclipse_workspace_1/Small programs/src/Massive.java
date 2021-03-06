
public class Massive {

	//wykonaj to co inen klasy tylko masowo (np dla wsyzsktich scenariuszy)

	//mowi co wziac jako x przy obliczainiu korelacji z finalna cena
	//generacja - generacja usrednionego prosumenta
	//
	String x = "generacja";
	
	public void massiveCorelationCeny()
	{
		massiveCorelation("");
	}
	
	public void massiveCorelationGeneracja()
	{
		massiveCorelation("generacja");
	}
	
	public void massiveCorelationKonsumpcja()
	{
		massiveCorelation("konsumpcja");
	}
	
	//mode = "generacja" ustawia jako x generacja
	//mode = "konsumpcja" ustawia jako x konsumpcje
	public void massiveCorelation(String mode)
	{
		int Scenario=2;
		
		while (Scenario<7)
		{
			
			//wybierz jeden, wedle ptorzeby
			//String folder ="cenaZGeneratora";
			String folder = "cenaZZewnatrz";

			
			System.out.println("\n"+Scenario);
			
			//plik z cenami (CenyPredykcjiVSCenyFinalne)
			String path ="C:\\Users\\Administrator\\Desktop\\symulacjaOutput\\"+folder
				+ "\\Scenario_"+Scenario+"\\handel\\pierwszeCeny_"+Scenario+".csv";
			
			String prosumentPath="C:\\Users\\Administrator\\Desktop\\symulacjaOutput\\"+folder
					+"\\Scenario_"+Scenario+"\\agregate\\100.csv";

		
			Corelation c = new Corelation();
			c.readCeny(path);
			
			if (mode.equals("generacja"))
			{
				c.readGeneracja(prosumentPath);
			}
			
			if (mode.equals("konsumpcja"))
			{
				c.readKonsumpcja(prosumentPath);
			}
			
			//if (x.equals(arg0))
			c.run();
			Scenario++;
		}
	}
	
}
