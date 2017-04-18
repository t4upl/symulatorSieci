
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
	
	//--------------------------------
	//GETTERS SETTERS
	
	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}
	
	//----------------------------
	//OTHER FUNCTIONS
	
}
