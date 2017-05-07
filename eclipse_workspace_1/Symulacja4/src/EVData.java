
public class EVData {

	//0 - dom, 1- podroz z domu do pracy, 2 - praca, 3 - podroz z pracy do domu
	int status=0;

	//stan energii na poczatku slotu
	float EV=0;
	float EV_c=0; 
	float EV_EB=0;
	float EB_EV=0;
	float G_EV=0;
	float Zew_EV=0;
	
	float EM_EV=0;
	float EV_EM=0;
	
	//zmiennna binarna mowiaca czy EV kupuej czy sprzedaje
	int EVbinKupuj=0;
	
	//--------------------------------
	//GETTERS SETTERS
		
	public int getStatus() {
		return status;
	}

	public int getEVbinKupuj() {
		return EVbinKupuj;
	}

	public void setEVbinKupuj(int eVbinKupuj) {
		EVbinKupuj = eVbinKupuj;
	}

	public float getEM_EV() {
		return EM_EV;
	}

	public void setEM_EV(float eM_EV) {
		EM_EV = eM_EV;
	}

	public float getEV_EM() {
		return EV_EM;
	}

	public void setEV_EM(float eV_EM) {
		EV_EM = eV_EM;
	}

	public float getEV() {
		return EV;
	}

	public void setEV(float eV) {
		EV = eV;
	}

	public float getEV_c() {
		return EV_c;
	}

	public void setEV_c(float eV_c) {
		EV_c = eV_c;
	}

	public float getEV_EB() {
		return EV_EB;
	}

	public void setEV_EB(float eV_EB) {
		EV_EB = eV_EB;
	}

	public float getEB_EV() {
		return EB_EV;
	}

	public void setEB_EV(float eB_EV) {
		EB_EV = eB_EV;
	}

	public float getG_EV() {
		return G_EV;
	}

	public void setG_EV(float g_EV) {
		G_EV = g_EV;
	}

	public float getZew_EV() {
		return Zew_EV;
	}

	public void setZew_EV(float zew_EV) {
		Zew_EV = zew_EV;
	}

	public void setStatus(int status) {
		this.status = status;
	}
		
	
}