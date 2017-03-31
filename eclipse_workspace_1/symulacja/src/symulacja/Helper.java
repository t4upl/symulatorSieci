package symulacja;

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
}
