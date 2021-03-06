import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;

public class Loader extends CoreClass {
	//Singleton shit
	private static Loader instance = null;
	private Loader() 
	{
	}
	
	public static Loader getInstance() {
	      if(instance == null) {
	         instance = new Loader();
	      }
	      return instance;
	}
	
	public ArrayList<Float> loadPrices()
	{
		ArrayList<Float> outputList = new ArrayList<Float>();
		String path = Stale.folderZDanymi+"\\ceny\\pierwszeCeny_"+Stale.scenariusz+".csv";
		
		//print(path);
		//getInput("loadPrices");
		
		BufferedReader br = null;
		int indeksFinalnejCeny =3;

		try {
			br = new BufferedReader(new FileReader(path));
			
			int counter=0;
			int emptyLineCounter=0;
			
			String line;
			while ((line=br.readLine())!=null)
			{
				
				if (line.equals(""))
				{
					emptyLineCounter++;
				}
				
				if (emptyLineCounter==1)
				{	
					if(counter>1)
					{
				
					//print(line);
					String[] s2 = line.split(",");
					float value=Float.parseFloat(s2[indeksFinalnejCeny]);
					//print(value);
					
					outputList.add(value);
					}
					counter++;
				}
				
			}	
			
			br.close();			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
		return outputList;

	}
	
	//wczytuje dane z pliku cars/ID_cars i zwrac alise (#0- godizna wyjazdu, #1 -godizna powrotu)
	//godizny zwracane w formacie HH:MM
	ArrayList<String> loadCars(int ID)
	{
		String path = Stale.folderZDanymi+"\\cars\\"+ID+"_cars.txt";
		ArrayList<String> L1 = new ArrayList<String>();
		
		BufferedReader br = null;
		
		try {
			br = new BufferedReader(new FileReader(path));
			
			String line;
			
			int a=0;
			//wczytujesz tylko peirwsza linei stad warunek na a<1
			while ((line=br.readLine())!=null && a<1)
			{
				print("CARS "+line);
				String[] s2 = line.split("#");
				
				//pozbyj sie :00 z konca (w pliku godizny sa trzymane w formacie HH:mm:ss)
				String wyjazd = s2[1].substring(0, s2[1].length()-3);
				String przyjazd = s2[2].substring(0, s2[2].length()-3);
				
				L1.add(wyjazd);
				L1.add(przyjazd);

				a++;
			}
			
			br.close();
		}
		catch(Exception e)
		{
			print("Error in loadCars");
		}
		
		return L1;
	}	

	
}
