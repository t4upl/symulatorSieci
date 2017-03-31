
public class CoreClass {

	ScannerWrapper scW = ScannerWrapper.getInstance();
	String ClassName =this.getClass().getName();
	
	public void getInput(String s)
	{
		print(s);
		scW.getInput();
	}
	
	public void getInput()
	{
		print("get Input");
		scW.getInput();
	}
	
	public void print(String s)
	{
		System.out.println(ClassName+" " +s);
	}
	
	public void print(float s)
	{
		print(s+"");
	}
}