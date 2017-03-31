package generatorSamochodowElektrycznych;

public class main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		
		String inputFolder="C:\\Users\\Administrator\\Desktop\\inputSamochodow";
		String inputFileName="1";
		
		String inputDistirbutionFolder="C:\\Users\\Administrator\\Desktop\\inputSamochodow";
		String inputDistirbutionFile="Metro2";
		
		String outputFolder="C:\\Users\\Administrator\\Desktop\\dane_output2";
		String outputFilename="2";
		
		int liczbaProbek=1;
		
		int a=0;
		while (a<liczbaProbek)
		{
			outputFilename=String.valueOf(a+1);
			
			EVgenerator EVgenerator = new EVgenerator();
			EVgenerator.run(inputFolder, inputFileName, inputDistirbutionFolder, inputDistirbutionFile, outputFolder, outputFilename);
			a++;
		}
	}

}
