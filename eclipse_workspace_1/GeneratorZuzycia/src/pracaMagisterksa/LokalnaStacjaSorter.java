package pracaMagisterksa;

import java.util.ArrayList;
import java.util.Collections;

public class LokalnaStacjaSorter {

	  private ArrayList<LokalnaStacja> localList = new ArrayList<>();
	  
	  public ArrayList<LokalnaStacja> getLocalList()
	  {
		  return localList;
	  }
	  
	  public LokalnaStacjaSorter(ArrayList<LokalnaStacja> localList) 
	  {         
		    this.localList = localList;     
	  }    
	  
	  public ArrayList<LokalnaStacja> getNumberOfNonZeroConsumption() {         
		    Collections.sort(localList, LokalnaStacja.numberOfNonZeroConsumptionRecordsComparator);         
		    return localList;     
		  } 	  
	  
	  public ArrayList<LokalnaStacja> getNumberOfNonZeroGeneration() {         
		    Collections.sort(localList, LokalnaStacja.numberOfNonZeroGenerationRecordsComparator);         
		    return localList;     
		  } 	  

}
