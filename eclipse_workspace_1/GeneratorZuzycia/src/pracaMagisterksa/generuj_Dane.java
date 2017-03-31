package pracaMagisterksa;


import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;


public class generuj_Dane {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		
		SampleGenerator3 sampleGenerator = new SampleGenerator3(); 
		
		//String pathToWorkSheet="C:\\Users\\Administrator\\Desktop\\dane\\OZE1.csv";
		String pathToFolderWithData="C:\\Users\\Administrator\\Desktop\\dane";
		String pathToOutputfolder="C:\\Users\\Administrator\\Desktop\\dane_output";
		Boolean debug = true;
		
		int liczbaProbek=1;

		sampleGenerator.run(pathToFolderWithData,pathToOutputfolder, debug,liczbaProbek);


		
		System.out.println("GenerujDane -end");
		
		

		
		
	}

		


	

}
