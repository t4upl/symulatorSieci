
public class EVData {

	//0 - dom, 1- podroz z domu do pracy, 2 - praca, 3 - podroz z pracy do domu
	int status;

	//stan energii na poczatku slotu
	float EVdom=0;
	float EVdom_c; 
	float EVdom_EB;
	float EB_EVdom;
	float G_EVdom;
	float Zew_EVdom;
	
	
	
	public float getEVdom_c() {
		return EVdom_c;
	}

	public void setEVdom_c(float eVdom_c) {
		EVdom_c = eVdom_c;
	}

	public float getEVdom_EB() {
		return EVdom_EB;
	}

	public void setEVdom_EB(float eVdom_EB) {
		EVdom_EB = eVdom_EB;
	}

	public float getEB_EVdom() {
		return EB_EVdom;
	}

	public void setEB_EVdom(float eB_EVdom) {
		EB_EVdom = eB_EVdom;
	}

	public float getG_EVdom() {
		return G_EVdom;
	}

	public void setG_EVdom(float g_EVdom) {
		G_EVdom = g_EVdom;
	}

	public float getZew_EVdom() {
		return Zew_EVdom;
	}

	public void setZew_EVdom(float zew_EVdom) {
		Zew_EVdom = zew_EVdom;
	}

	public float getEVdom() {
		return EVdom;
	}

	public void setEVdom(float eVdom) {
		EVdom = eVdom;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	//----------
	//OTHER FUNCTIONS
	
	public String toString()
	{
			
		String EB2 ="EV "+EVdom+"\n";
		String EB_EV2 = "EB_EV "+EB_EVdom+"\n";
		String EV_EB2 ="EV_EB "+EVdom_EB+"\n";
		String Zew_EV2 ="Zew_EV "+Zew_EVdom+"\n";
		String G_EV2 = "G_EV "+G_EVdom+"\n";
		String EV_c2 = "EV_c "+EVdom_c+"\n";
		
		return EB2+EB_EV2+EV_EB2+Zew_EV2+G_EV2+EV_c2;
	}
	
	
	
}
