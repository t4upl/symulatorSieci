
public class CoreClass {
	
	ScannerWrapper scW = ScannerWrapper.getInstance();
	String ClassName =this.getClass().getName();
	
	public void getInput(String s)
	{
		print("getInput "+s);
		scW.getInput();
	}
	
	public void getInput()
	{
		print("get Input");
		scW.getInput();
	}
	
	public void getInput(String s, Boolean b1)
	{
		if (b1)
		{
			getInput(s);
		}
	}
	
	public void print(String s)
	{
		System.out.println(ClassName+" " +s);
	}
	
	public void print(float s)
	{
		print(s+"");
	}
	
	public void print(float s,Boolean b1)
	{
		if(b1)
		{
			print(s+"");			
		}

	}
	
	public void print(String s,Boolean b1)
	{
		if (b1)
		{
			print(s);
		}
	}
	
}
