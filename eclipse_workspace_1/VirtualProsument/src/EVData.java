
public class EVData {

	//0 - dom, 1- podroz z domu do pracy, 2 - praca, 3 - podroz z pracy do domu
	int status;

	float EVdom=0;
	float EVdom_c; 
	float EVdom_EB;
	float EB_EVdom;
	float G_EVdom;
	float Zew_EVdom;
	
	/*float EVpraca=0;
	float EVpraca_c;
	float EVpraca_EB;
	float EB_EVpraca;
	float G_EVpraca;
	float Zew_EVpraca;*/
	
	
	
	public float getEVdom() {
		return EVdom;
	}

	public void setEVdom(float eVdom) {
		EVdom = eVdom;
	}

	/*
	public float getEVpraca() {
		return EVpraca;
	}

	public void setEVpraca(float eVpraca) {
		EVpraca = eVpraca;
	}*/

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}


	
	
	
}
