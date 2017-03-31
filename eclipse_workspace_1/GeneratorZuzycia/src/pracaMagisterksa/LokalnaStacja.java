package pracaMagisterksa;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Comparator;

public class LokalnaStacja {
	
	private String nazwaStacji;
	private String fileName;
	
	private ArrayList<String> hourList = new ArrayList<String>(); 
	private ArrayList<Float> generacjaList = new ArrayList<Float>();
	private ArrayList<Float> konsumpcjaList = new ArrayList<Float>();
	
	private int indexGeneracji;
	private int indexKonsumpcji;
	
	private int numberOfNonZeroGenerationRecords;
	private int numberOfNonZeroConsumptionRecords;
	
	private float sumaGeneracji=0;
	private float sumaKonsumpcji=0;
	
	private float energiaWtrakcie3Miesiecy;
	private Float[] wspolczynniki;
	
	private ArrayList<Float> dailyGeneracjaList = new ArrayList<Float>();
	private ArrayList<Float> dailyKonsumpcjaList = new ArrayList<Float>();
	
	
	
	
	  public static Comparator<LokalnaStacja> numberOfNonZeroConsumptionRecordsComparator = new Comparator<LokalnaStacja>() {         
		    @Override         
		    public int compare(LokalnaStacja jc1, LokalnaStacja jc2) {             

		      return (jc2.getNumberOfNonZeroConsumptionRecords() < jc1.getNumberOfNonZeroConsumptionRecords() ? -1 :                     

		              (jc2.getNumberOfNonZeroConsumptionRecords() == jc1.getNumberOfNonZeroConsumptionRecords() ? 0 : 1));           
		    }     
	  };
	  
	  public static Comparator<LokalnaStacja> numberOfNonZeroGenerationRecordsComparator = new Comparator<LokalnaStacja>() {         
		    @Override         
		    public int compare(LokalnaStacja jc1, LokalnaStacja jc2) {             

		      return (jc2.getNumberOfNonZeroGenerationRecords() < jc1.getNumberOfNonZeroGenerationRecords() ? -1 :                     
		              (jc2.getNumberOfNonZeroGenerationRecords() == jc1.getNumberOfNonZeroGenerationRecords() ? 0 : 1));           
		    }     
	  };       


	public void createDailyLists()
	{
		//System.out.println("DO ME! DO ME HARD!");
		ArrayList<Float> L1= new ArrayList<Float>();
		ArrayList<Float> L2= new ArrayList<Float>();
		
		
		float sum1=0;
		float sum2=0;

		int a=0;
		while (a<hourList.size())
		{
			sum1+=generacjaList.get(a);
			sum2+=konsumpcjaList.get(a);
			
			String hour = hourList.get(a);
			if (hour.contains("23:00"))
			{
				//System.out.println(hour);
				L1.add(sum1);
				L2.add(sum2);

				sum1=0;
				sum2=0;
			}
			
			a++;
			
		}
		
		dailyGeneracjaList=L1;
		dailyKonsumpcjaList=L2;
	}  
	  
	public float sumGeneracjaList()
	{	
		float f =sumFloatList(generacjaList);
		sumaGeneracji=f;
		
		return f;
	}
	
	public float sumKonsumpcjaList()
	{
		float f =sumFloatList(konsumpcjaList);
		sumaKonsumpcji=f;
		
		return f; 
	} 
	
	private float sumFloatList(ArrayList<Float> L1)
	{
		float sum=0f;
		int a=0;
		while (a<L1.size())
		{
			sum+=L1.get(a);
			a++;
		}
		return sum;
	}
	
	LokalnaStacja(String nazwaStacji, String fileName, 	int indexGeneracji, int indexKonsumpcji)
	{
		this.fileName=fileName;
		this.nazwaStacji=nazwaStacji;
		
		this.indexGeneracji=indexGeneracji;
		this.indexKonsumpcji=indexKonsumpcji;
	}
	
	public void updateNonZeros()
	{
		numberOfNonZeroGenerationRecords=0;
		numberOfNonZeroConsumptionRecords=0;
		
		int a=0;
		while(a<generacjaList.size())
		{
			if (generacjaList.get(a)!=0)
			{
				numberOfNonZeroGenerationRecords++;
			}
			
			if (konsumpcjaList.get(a)!=0)
			{
				numberOfNonZeroConsumptionRecords++;
			}
			
			a++;
		}
	}
	
	//drops the file the way it has been loaded
	public void dropDataPure(String outputFolder)
	{
		try {
			
			Writer writer = new FileWriter(outputFolder+"\\"+fileName+"\\"+returnIdentficationString2()+".csv");
			writer.write("godzina,generacja,konsumpcja"+System.lineSeparator());
			
			int a=0;
			while(a<hourList.size())
			{
				writer.write(hourList.get(a)+","+generacjaList.get(a)+","+konsumpcjaList.get(a)+System.lineSeparator() );
				a++;
			}
			
		
			writer.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void dropDataCore(String outputFolder)
	{
		try {
			
			Writer writer = new FileWriter(outputFolder+"\\"+returnIdentficationString2()+".csv");
			writer.write("godzina,generacja,konsumpcja"+System.lineSeparator());
			
			int a=0;
			while(a<hourList.size())
			{
				writer.write(hourList.get(a)+","+generacjaList.get(a)+","+konsumpcjaList.get(a)+System.lineSeparator() );
				a++;
			}
			
		
			writer.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String returnIdentficationString()
	{
		return nazwaStacji+" "+fileName+" generacja:"+indexGeneracji+" konsumpcja: "+indexKonsumpcji;
	}
	
	public String returnIdentficationString2()
	{
		
		String s=nazwaStacji+"_"+fileName+"_"+indexGeneracji+"_"+indexKonsumpcji;
		s=s.replace("?", "_");
		return s;
	}
	
	public void checkDataConsistency()
	{
		if (hourList.size()!=generacjaList.size())
		{
			System.out.println("hourList.size()!=generacjaList.size() "+returnIdentficationString());
		}
		
		if (hourList.size()!=konsumpcjaList.size())
		{
			System.out.println("hourList.size()!=konsumpcjaList.size() "+returnIdentficationString());
		}
	}
	
	public String print()
	{
		return(fileName+" "+nazwaStacji+" g:"+indexGeneracji+" k:"+indexKonsumpcji);
		
	}
	
	public void normalizeToTheEnergy(float energyValue)
	{	
		float mnoznikGeneracji=energyValue/sumGeneracjaList();
		float mnoznikKonsumpcji=energyValue/sumKonsumpcjaList();
				
		multiplyFloatArray(generacjaList,mnoznikGeneracji);
		multiplyFloatArray(konsumpcjaList,mnoznikKonsumpcji);
	}
	
	public void multiply(float value,Boolean generacja)
	{
		if (generacja)
		{
			multiplyFloatArray(generacjaList,value);
		}
		else
		{
			multiplyFloatArray(konsumpcjaList,value);
		}
	}
	
	private void multiplyFloatArray(ArrayList<Float> L1, float value)
	{
		int a=0;
		while (a<L1.size())
		{
			L1.set(a, L1.get(a)*value);
			a++;
		}
	}
	
	//GETTERS
	public ArrayList<Float> getDailyGeneracjaList()
	{
		return dailyGeneracjaList;
	}
	
	public ArrayList<Float> getDailyKonsumpcjaList()
	{
		return dailyKonsumpcjaList;
	}
	
	public float getSumaGeneracji()
	{
		return sumaGeneracji;
	}

	public float getSumaKonsumpcji()
	{
		return sumaKonsumpcji;
	}	
	
	public ArrayList<String> getHourList ()
	{
		return hourList;
	}
	
	String getFileName()
	{
		return fileName;
	}
	
	int getGenerationIndex()
	{
		return indexGeneracji;
	}
	
	int getConsumptionIndex()
	{
		return indexKonsumpcji;
	}
	
	public ArrayList<Float> getGeneracjaList() {
		return generacjaList;
	}
	
	public ArrayList<Float> getKonsumpcjaList() {
		return konsumpcjaList;
	}
	
	public int getNumberOfNonZeroGenerationRecords()
	{
		return numberOfNonZeroGenerationRecords;
	}
	
	public int getNumberOfNonZeroConsumptionRecords()
	{
		return numberOfNonZeroConsumptionRecords;
	}
	
	public Float[] getWspolczynniki()
	{
		return this.wspolczynniki;
	}
	
	public float getEnergiaWtrakcie3Miesiecy()
	{
		return this.energiaWtrakcie3Miesiecy;
	}
	
	//SETTERS
	public void setWspolczynniki(Float[] wspolczynniki)
	{
		this.wspolczynniki=wspolczynniki;
	}
	
	public void setFilename(String fileName)
	{
		this.fileName=fileName;
	}
	
	public void setEnergiaWtrakcie3Miesiecy(float value)
	{
		this.energiaWtrakcie3Miesiecy=value;
	}
	
	//Other
	
	public void addConsumptionElement(String hour, Float valueToBeAdded)
	{
		if (hourList.size()==konsumpcjaList.size())
		{
			hourList.add(hour);
		}
		
		Float valueToBeAddedAsFloat=valueToBeAdded;
		konsumpcjaList.add(valueToBeAddedAsFloat);
	}
	
	public void addConsumptionElement(String hour, String valueToBeAdded)
	{
		if (hourList.size()==konsumpcjaList.size())
		{
			hourList.add(hour);
		}
		
		Float valueToBeAddedAsFloat=Float.parseFloat(inputControl(valueToBeAdded));
		
		konsumpcjaList.add(valueToBeAddedAsFloat);
	}
	
	
	public void addGenerationElement(String hour, Float valueToBeAdded)
	{
		if (hourList.size()==generacjaList.size())
		{
			hourList.add(hour);
		}
		
		Float valueToBeAddedAsFloat=valueToBeAdded;
		generacjaList.add(valueToBeAddedAsFloat);
	}
	
	public void addGenerationElement(String hour, String valueToBeAdded)
	{
		if (hourList.size()==generacjaList.size())
		{
			hourList.add(hour);
		}
		
		Float valueToBeAddedAsFloat=Float.parseFloat(inputControl(valueToBeAdded));
		generacjaList.add(valueToBeAddedAsFloat);
	}
	
	String inputControl(String input)
	{
		if (input.equals("-----"))
		{
			return "0.0";
		}
		
		if (input.contains("\""))
		{
			input=input.replace("\"", "");
		}
		
		if (input.contains("*"))
		{
			input=input.replace("*", "");
		}

		if (input.contains("#"))
		{
			input=input.replace("#", "");
		}
		
		return input;
	}
	
	
	
}
