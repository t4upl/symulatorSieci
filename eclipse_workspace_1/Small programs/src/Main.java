
public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		/*String path = "C:\\Users\\Administrator\\Desktop\\PSE_2";
		String fileName="OZE1";
		
		TauronCSVCreator ps = new TauronCSVCreator(fileName,path);
		ps.run();*/
		
		/*int Scenario=2;
		String folder = "cenaZZewnatrz";
		System.out.println("\n"+Scenario);
		String path ="C:\\Users\\Administrator\\Desktop\\symulacjaOutput\\"+folder
			+ "\\Scenario_"+Scenario+"\\handel\\pierwszeCeny_"+Scenario+".csv";
		
		String generationPath="C:\\Users\\Administrator\\Desktop\\symulacjaOutput\\cenaZZewnatrz\\Scenario_2\\agregate\\100.csv";
		
		Corelation c = new Corelation();
		c.readCeny(path);
		c.readGeneracja(generationPath);
		c.run();*/
		
		
		Massive m = new Massive();
		m.massiveCorelationKonsumpcja();
		
		System.out.println("\nEND");


	}

}
