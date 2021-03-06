import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Corelation extends CoreClass {

	//LICZENIE KORELACJI
	ArrayList<Float> x = new ArrayList<Float>();
	ArrayList<Float> y = new ArrayList<Float>();
	ArrayList<Float> xx = new ArrayList<Float>();
	ArrayList<Float> yy = new ArrayList<Float>();
	
	//Daty potrzeben, aby sparowac generacje i konsumpcje jezleiw chodza za x
	ArrayList<String> dates = new ArrayList<String>();
	
	
	public void readKonsumpcja(String path)
	{
		readFromProsument(path,2);
	}
	
	public void readGeneracja(String path)
	{
		readFromProsument(path,4);
	}
	
	//wcyztaj kolumne jako x pod warunkieem ze data w rekordzie == dates(i)
	//column of interest okresla ktora kolumne z pliku brac jako x
	void readFromProsument(String path,int columenOfInterest)
	{
		
		x = new ArrayList<Float>();

		String seperator ="###";
		
		Boolean isSeparatorEncountered = false;
		
		//po napotkaniu separatora trzeba jeszcze przerjsc naglowek
		int counter =0;
		

		
		print("readFromProsument path: "+path);
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(path));
			String line;
			
			// ziweksza sie keidykolwiek znaleziony jest rekord wpliku z generacjami taki jak wpis i w liscie dates
			int i=0;
			while ((line=br.readLine())!=null)
			{	
				if (isSeparatorEncountered)
				{
					
					//print(counter);
					
					//header + sprawdzanie czy dobry columenOfInterest
					if (counter==0)
					{
						/*print("header");
						print(line);
						String[] s2 = line.split(",");
						print(s2[columenOfInterest]);*/
						
						
					}
					
					if (counter>0)
					{
						//print(line);
						String[] s2 = line.split(",");
						
						//w czesci pliko cen zamienione jest meijscami data z godizna
						//wiec buduj dwa stringgi zeby porownywac z lista dates
						String dayHour=s2[0]+" "+s2[1];
						String hourDay=s2[1]+" "+s2[0];
						
						//print(dayHour);
						//print(hourDay);
						
						//print(dates.get(i));
						
						if (dayHour.equals(dates.get(i)) || hourDay.equals(dates.get(i)) )
						{
							//print(s2[columenOfInterest]);
							x.add(Float.parseFloat(s2[columenOfInterest]));
							i++;
						}
						
						
					}
					counter++;
				}
				else
				{
					if (line.equals(seperator))
					{
						isSeparatorEncountered=true;
					}
				}
				//print(line);
				//print(dates.get(i));
			}
		}
		catch (Exception e)
		{
			print("Exception inr eadGeneracja");
		}
		
		if (dates.size()!=x.size())
		{
			print("Errror Something went terribly wrong readFromProsument");
			getInput("");
		}
	}
	
	public void readCeny(String path)
	{
		String seperator ="###";
		Boolean isMainSection=false;
		int counter =0;
		
		int predykcjaIndeks=2;
		int finalnaIndeks=3;
		
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(path));
			String line;
			while ((line=br.readLine())!=null)
			{
				if (line.equals(seperator))
				{
					isMainSection=true;
				}
				
				if (isMainSection)
				{
					counter++;
					//bo header
					
					if (counter>2)
					{
						//print(line);
						String[] s2 = line.split(",");
						x.add(Float.parseFloat(s2[predykcjaIndeks]));
						y.add(Float.parseFloat(s2[finalnaIndeks]));
						
						dates.add(s2[0]+" "+s2[1]);

					}
				}
				
				//print(line);
			}
			
			br.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			print("read problem");
			e.printStackTrace();
		}


	}
	
	//test from http://www.statisticshowto.com/how-to-compute-pearsons-correlation-coefficients/
	public void test()
	{
		x =   new ArrayList<>( (Arrays.asList(43f,21f,25f,42f,57f,59f)));
		y =   new ArrayList<>( (Arrays.asList(99f,65f,79f,75f,87f,81f)));
	}
	
	//http://www.statisticshowto.com/mean-squared-error/
	public void test2()
	{	
		x =   new ArrayList<>( (Arrays.asList(43f,44f,45f,46f,47f)));
		y =   new ArrayList<>( (Arrays.asList(41f,45f,49f,47f,44f)));
	}
	
	//http://www.statisticshowto.com/how-to-find-a-linear-regression-equation/
	//#0 - a, #1 -b
	//UWAGA TEN PORTAL UZYWA NOTACJI y =bx+a,a le na wyjsciu jest tak ajk powiwnno byc (#0)*x+(#1)
	public ArrayList<Float> regressionLine()
	{
		ArrayList<Float> L1 = new ArrayList<Float>();
		
		float Ex=0;
		float Ey=0;
		float Exy=0;
		float Ex2=0;
		float Ey2=0;
		
		int i=0;
		while(i<x.size())
		{
			
			float currentX=x.get(i);
			float currentY=y.get(i);
			float currentXY=currentX*currentY;
			float currentX2 =currentX*currentX;
			float currentY2 =currentY*currentY;
			
			Ex+=currentX;
			Ey+=currentY;
			Exy+=currentXY;
			Ex2+=currentX2;
			Ey2+=currentY2;
			i++;
		}
		
		float a =Ey*Ex2-Ex*Exy;
		a=a/(Float.valueOf(x.size())*Ex2-Ex*Ex );
		
		//print("regression a: "+a);
		
		float b =Float.valueOf(x.size())*Exy-Ex*Ey;
		b=b/( Float.valueOf(x.size())*Ex2-Ex*Ex  );
		
		//print("regression b: "+b);
	
		L1.add(b);
		L1.add(a);
		return L1;
		
	}
	
	public void run()
	{
		float Ex=0;
		float Ey=0;
		float Exy=0;
		float Ex2=0;
		float Ey2=0;
		
		float error=0;
		float error2Sum=0;
		ArrayList<Float> regression=regressionLine();
		
		int i=0;
		while(i<x.size())
		{
			
			float currentX=x.get(i);
			float currentY=y.get(i);
			float currentXY=currentX*currentY;
			float currentX2 =currentX*currentX;
			float currentY2 =currentY*currentY;
			
			Ex+=currentX;
			Ey+=currentY;
			Exy+=currentXY;
			Ex2+=currentX2;
			Ey2+=currentY2;
			
			float Yprim = regression.get(0)*currentX+regression.get(1);
			error = Yprim-currentY;
			//print("ERROR "+error);
			
			error2Sum=error2Sum+error*error;
			//xx.add(x.get(i)*x.get(i));
			//yy.add(y.get(i)*y.get(i));
			i++;
		}
		
		
		
		/*print("END REPORT");
		print (Ex);
		print (Ey);
		print(Exy);
		print(Ex2);
		print(Ey2);*/
		
		int n=x.size();
		
		float corelationTop= Float.valueOf(n)*Exy-Ex*Ey;
		float corelationBottom=(Float.valueOf(n)*Ex2-(Ex*Ex))*(Float.valueOf(n)*Ey2-(Ey*Ey));
		corelationBottom=(float) Math.sqrt(corelationBottom);
		float corelationValue = corelationTop/corelationBottom;
		
		print("corelationValue: "+corelationValue);
		print("MSE "+(error2Sum/Float.valueOf(n)));
		
		
	};
	
	/*
	void print(String value)
	{
		System.out.println(value);
	}
	
	void print(float value)
	{
		System.out.println(value);
	}*/
	
	
}
