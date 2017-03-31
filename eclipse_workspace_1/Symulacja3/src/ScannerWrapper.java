import java.util.Scanner;

public class ScannerWrapper {
	
	Scanner sc = new Scanner(System.in);
	
	//Singleton shit
	private static ScannerWrapper instance = null;
	private ScannerWrapper() 
	{
		
	}
	
	public static ScannerWrapper getInstance() {
	      if(instance == null) {
	         instance = new ScannerWrapper();
	      }
	      return instance;
	}
	
	public void getInput()
	{
		sc.nextLine();
	}
}
