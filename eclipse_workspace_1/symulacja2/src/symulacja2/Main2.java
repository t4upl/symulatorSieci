package symulacja2;


public class Main2 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		System.out.println("Project symulacja - start 2 ");
		
		String folderZDanymi="C:\\Users\\Administrator\\Desktop\\dane_do_symulacji";
		String outputFolder="C:\\Users\\Administrator\\Desktop\\symulacjaOutput";
		
		//LokalneCentrumDystrybucji2 lokalneCentrum = new LokalneCentrumDystrybucji2(folderZDanymi,outputFolder);
		//lokalneCentrum.start();
		
		LokalneCentrumDystrybucji2 lokalneCentrum = LokalneCentrumDystrybucji2.getInstance();
		lokalneCentrum.setUp(folderZDanymi,outputFolder);
		lokalneCentrum.start();
		
		System.out.println("\n\n-----------\nEND");
		
	}

}
