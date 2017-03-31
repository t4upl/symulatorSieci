package symulacja2;

import java.util.ArrayList;

public class Helper {
	
	public static void printStringList(ArrayList<String> L1)
	{
		System.out.println("Helper + printStringList");
		
		int a=0;
		while (a<L1.size())
		{
			System.out.println(a+": "+L1.get(a));
			a++;
		}
	}
	
	public static void printProsument2(Prosument2 prosument2)
	{
		ArrayList<DayData2> list1 = prosument2.getDayDataList();
		
		int a=0;
		while (a<list1.size())
		{
			DayData2 d = list1.get(a);
			print(d.getDay()+" "+d.getHour()+" "+d.getGeneration()+" "+d.getConsumption());
			
			a++;
		}
		
		print("printProsument2");
	}
	
	static void print(String s)
	{
		System.out.println("Helper "+s);
	}
}
