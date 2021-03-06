import java.io.BufferedReader;
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
	
	//------------------
	//OTHER
	
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
				String[] s2 = line.split("#");
				
				//pozbyj sie :00 z konca (w pliku godizny sa trzymane w formacie HH:mm:ss)
				String wyjazd = s2[1].substring(0, s2[1].length()-3);
				String przyjazd = s2[2].substring(0, s2[2].length()-3);
				
				L1.add(wyjazd);
				L1.add(przyjazd);

				
				//print(line);
				//print(wyjazd);
				//print(przyjazd);
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
