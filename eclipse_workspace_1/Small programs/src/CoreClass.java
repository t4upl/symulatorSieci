import java.util.Scanner;

public class CoreClass {

	String ClassName =this.getClass().getName();
	Scanner sc = new Scanner(System.in);
	
	public void print(String s)
	{
		System.out.println(ClassName+" "+s);
	}
	
	public void getInput(String s)
	{
		print("getInput");
		sc.nextLine();
	}
	
}