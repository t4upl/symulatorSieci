import java.util.ArrayList;

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
	
	public <T> void print (ArrayList<T> L1)
	{
		print("printing List\n");
		int i=0;
		while (i<L1.size())
		{
			System.out.println(i+"# "+L1.get(i));
			i++;
		}
	}
	
	public Double[] primitiveDouble2Double(double[] d)
	{
		Double[] d2 = new Double[d.length];
		
		int i=0;
		while (i<d.length)
		{
			d2[i] =d[i];
			i++;
		}
		
		return d2;
	}
}
