package pracaMagisterksa;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;

public class Helper {

	public static void dropFinal(String folderName, ArrayList<LokalnaStacja> L1 )
	{
		System.out.println("--------\nHelper dropFinal");
		
		int a=0;
		while (a<L1.size())
		{
			dropFinal(folderName,L1.get(a));
			a++;
		}
	}
	
	static String floatArrayToString(Float[] f,String separator)
	{
		String s="";
		int a=0;
		while (a<f.length-1)
		{
			s+=f[a].toString()+separator;
			a++;
		}
		s+=f[a].toString();
		
		return s;
		
	}
	
	static void dropFinal(String folderName, LokalnaStacja ls)
	{
		ArrayList<Float> L1 =new ArrayList<Float> (ls.getDailyGeneracjaList());
		Collections.sort(L1);
		String pierwszyKwantylGeneracja = String.valueOf(L1.get(L1.size()/4));
		String trzeciKwantylGeneracja = String.valueOf(L1.get(3*L1.size()/4));
		
		L1 =new ArrayList<Float> (ls.getDailyKonsumpcjaList());
		Collections.sort(L1);
		String pierwszyKwantylKonsumpcja = String.valueOf(L1.get(L1.size()/4));
		String trzeciKwantylKonsumpcja = String.valueOf(L1.get(3*L1.size()/4));
		
		try {
			
			Writer writer = new FileWriter(folderName+"\\"+ls.getFileName()+".csv");
			writer.write("WSPOLCZYNNIK:,"+floatArrayToString(ls.getWspolczynniki(),",")+System.lineSeparator());
			writer.write("TRZYMIESIECZNE ZUZYCIE[kWh]:,"+ls.getEnergiaWtrakcie3Miesiecy()+System.lineSeparator());
			
			writer.write("Generacja pierwszy kwantyl:,"+pierwszyKwantylGeneracja+System.lineSeparator());
			writer.write("Generacja trzeci kwantyl:,"+trzeciKwantylGeneracja+System.lineSeparator());
			writer.write("Konsumpcja pierwszy kwantyl:,"+pierwszyKwantylKonsumpcja+System.lineSeparator());
			writer.write("Konsumpcja trzeci kwantyl:,"+trzeciKwantylKonsumpcja+System.lineSeparator());
			
			writer.write("hour,generacja,konsumpcja,generacja[dzien],konsumpcja[dzien]"+System.lineSeparator());

			
			writer.write("###"+System.lineSeparator());
			
			
			ArrayList<String> hourList =ls.getHourList();
			ArrayList<Float> generacjaList =ls.getGeneracjaList();
			ArrayList<Float> konsumpcjaList =ls.getKonsumpcjaList();
			
			
			String dailyGeneration="";
			String dailyKonsumption="";
			int dailyCounter=0;
			
			int a=0;
			while(a<ls.getHourList().size())
			{
				if (a%24==0)
				{
					dailyGeneration = String.valueOf(ls.getDailyGeneracjaList().get(dailyCounter));
					dailyKonsumption = String.valueOf(ls.getDailyKonsumpcjaList().get(dailyCounter));
					dailyCounter++;
				}
				else
				{
					dailyGeneration="";
					dailyKonsumption="";
				}
				
				writer.write(hourList.get(a)+","+generacjaList.get(a)+","+konsumpcjaList.get(a)
				+","+dailyGeneration+","+dailyKonsumption+System.lineSeparator() );
				a++;
			}
			
		
			writer.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
