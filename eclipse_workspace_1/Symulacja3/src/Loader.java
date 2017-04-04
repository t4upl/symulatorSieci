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

	
}