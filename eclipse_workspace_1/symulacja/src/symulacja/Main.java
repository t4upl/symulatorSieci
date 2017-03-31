package symulacja;

public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		System.out.println("Project symulacja - start");
		
		String folderZDanymi="C:\\Users\\Administrator\\Desktop\\dane_do_symulacji"; 
		
		LokalneCentrumDystrybucji2 lokalneCentrum = new LokalneCentrumDystrybucji2(folderZDanymi);
		lokalneCentrum.start();
		
		System.out.println("Project symulacja - end");

	}
	
	

}
