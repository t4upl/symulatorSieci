import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Corelation {

	ArrayList<Float> x = new ArrayList<Float>();
	ArrayList<Float> y = new ArrayList<Float>();
	ArrayList<Float> xx = new ArrayList<Float>();
	ArrayList<Float> yy = new ArrayList<Float>();
	
	
	//test from http://www.statisticshowto.com/how-to-compute-pearsons-correlation-coefficients/
	public void test()
	{
		x =   new ArrayList<>( (Arrays.asList(43f,21f,25f,42f,57f,59f)));
		y =   new ArrayList<>( (Arrays.asList(99f,65f,79f,75f,87f,81f)));
	}
	
	public void run()
	{
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
		
		
	};
	
	void print(String value)
	{
		System.out.println(value);
	}
	
	void print(float value)
	{
		System.out.println(value);
	}
	
	
}