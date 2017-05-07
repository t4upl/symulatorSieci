import java.util.ArrayList;

public class ProsumentEV extends Prosument {

	//lista zawiera dane dotyczace uzycia akumualtorow EV
	private ArrayList<EVData> eVDataList = new ArrayList<>();

	
	//-----------------------
	public ArrayList<EVData> getEVDataList() {
		return eVDataList;
	} 
	
	
	@Override
	public void createEmptyDataList(int size)
	{	
		super.createEmptyDataList(size);
		
		int a=0;
		while (a<size)
		{
			EVData ev = new EVData();
			eVDataList.add(ev);
			a++;
		}
	}
	
	
}
