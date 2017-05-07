
public class DayData {
	
	
	private String day;
	private String hour;
	private double consumption=0f; //ustawiane Prosument.loadData()
	private double generation=0f; //ustawiane Prosument.loadData() Bez mnzonika
	private double trueGeneration=0f; //generation * mnoznik, ustawione przy ustawianu mnoznika

	private double stanBateriiNaPoczatkuSlotu=0f;
	
	//ile do zaplaty w dnaym slocie
	private double cost=0f;

	
	//sterowanie
	private double zBateriiNaKonsumpcje=0f;
	private double zBateriiNaRynek=0f;
	
	private double zGeneracjiNaKonsumpcje=0f;
	private double zGeneracjiNaRynek=0f;
	private double zGeneracjiDoBaterii=0f;

	private double zRynekNaKonsumpcje=0f;
	private double zRynekDoBaterii=0f;
	
	private double kupuj=0;
	
	double cenaNaLokalnymRynku=0;





	public void setConsumption(double consumption) {
		this.consumption = consumption;
	}

	public void setGeneration(double generation) {
		this.generation = generation;
	}

	public void setTrueGeneration(double trueGeneration) {
		this.trueGeneration = trueGeneration;
	}


	public void setStanBateriiNaPoczatkuSlotu(double stanBateriiNaPoczatkuSlotu) {
		this.stanBateriiNaPoczatkuSlotu = stanBateriiNaPoczatkuSlotu;
	}

	public void setCost(double cost) {
		this.cost = cost;
	}

	public double getzBateriiNaKonsumpcje() {
		return zBateriiNaKonsumpcje;
	}

	public void setzBateriiNaKonsumpcje(double zBateriiNaKonsumpcje) {
		this.zBateriiNaKonsumpcje = zBateriiNaKonsumpcje;
	}

	public double getzBateriiNaRynek() {
		return zBateriiNaRynek;
	}

	public void setzBateriiNaRynek(double zBateriiNaRynek) {
		this.zBateriiNaRynek = zBateriiNaRynek;
	}

	public double getzGeneracjiNaKonsumpcje() {
		return zGeneracjiNaKonsumpcje;
	}

	public void setzGeneracjiNaKonsumpcje(double zGeneracjiNaKonsumpcje) {
		this.zGeneracjiNaKonsumpcje = zGeneracjiNaKonsumpcje;
	}

	public double getzGeneracjiNaRynek() {
		return zGeneracjiNaRynek;
	}

	public void setzGeneracjiNaRynek(double zGeneracjiNaRynek) {
		this.zGeneracjiNaRynek = zGeneracjiNaRynek;
	}

	public double getzGeneracjiDoBaterii() {
		return zGeneracjiDoBaterii;
	}

	public void setzGeneracjiDoBaterii(double zGeneracjiDoBaterii) {
		this.zGeneracjiDoBaterii = zGeneracjiDoBaterii;
	}

	public double getzRynekNaKonsumpcje() {
		return zRynekNaKonsumpcje;
	}

	public void setzRynekNaKonsumpcje(double zRynekNaKonsumpcje) {
		this.zRynekNaKonsumpcje = zRynekNaKonsumpcje;
	}

	public double getzRynekDoBaterii() {
		return zRynekDoBaterii;
	}

	public void setzRynekDoBaterii(double zRynekDoBaterii) {
		this.zRynekDoBaterii = zRynekDoBaterii;
	}

	public void setKupuj(double kupuj) {
		this.kupuj = kupuj;
	}
	
	double getCenaNaLokalnymRynku()
	{
		return this.cenaNaLokalnymRynku;
	}
	
	double getKupuj()
	{
		return this.kupuj;
	}
	
	double getTrueGeneration()
	{
		return trueGeneration;
	}
	
	//---
	
	double getZRynekNaKonsumpcje()
	{
		return this.zRynekNaKonsumpcje;
	}
	
	double getZRynekDoBaterii()
	{
		return this.zRynekDoBaterii;
	}
	
	//---	
	double getZGeneracjiDoBaterii()
	{
		return zGeneracjiDoBaterii;
	}
	
	double getZGeneracjiNaKonsumpcje()
	{
		return zGeneracjiNaKonsumpcje;
	}
	
	double getZGeneracjiNaRynek()
	{
		return this.zGeneracjiNaRynek;
	}
	
	//---
	double getZBateriiNaKonsumpcje()
	{
		return zBateriiNaKonsumpcje;
	}
	
	double getZBateriiNaRynek()
	{
		return this.zBateriiNaRynek;
	}
	
	
	double getStanBateriiNaPoczatkuSlotu()
	{
		return stanBateriiNaPoczatkuSlotu;
	}
	
	String getDay()
	{
		return this.day;
	}
	
	String getHour()
	{
		return this.hour;
	}
	
	double getConsumption()
	{
		return this.consumption;
	}
	
	double getGeneration()
	{
		return this.generation;
	}
	
	double getCost()
	{
		return this.cost;
	}	
	
	//---------------------
	public String getDayHour()
	{
		return this.day+" "+this.getHour();
	}
	
	//SETTERS
	
	public void setDay(String s)
	{
		this.day=s;
	}
	
	public void setHour(String s)
	{
		this.hour=s;
	}
	

	
	public void setZBateriiNaRynek(double value)
	{
		this.zBateriiNaRynek =value;
	}
	
	
	public void setZGeneracjiNaRynek(double value)
	{
		this.zGeneracjiNaRynek =value;
	}

	public void setZRynekDoBaterii(double value)
	{
		this.zRynekDoBaterii=value;
	}
	
	public void setZRynekNaKonsumpcje(double value)
	{
		this.zRynekNaKonsumpcje=value;
	}
	
	
	public void setZGeneracjiDoBaterii(double value)
	{
		zGeneracjiDoBaterii=value;
	}
	
	public void setZBateriiNaKonsumpcje(double value)
	{
		zBateriiNaKonsumpcje=value;
	}
	
	
	
	public void setZGeneracjiNaKonsumpcje(double value)
	{
		zGeneracjiNaKonsumpcje=value;
	}
	
	public void setCenaNaLokalnymRynku(double value)
	{
		this.cenaNaLokalnymRynku = value;
	}

	
	//TODO
	//----------------------
	//OTHER
	
	public void countTrueGeneration(double mnoznik)
	{
		this.trueGeneration = generation*mnoznik;
	}
	
	//ID and a for debug
		void addDayData(DayData d2,int ID, int a)
		{
			
			DayData d1 =this;
			
			//adding hours works becaus d2 always has an hour
			d1.setHour(d2.getHour());
			d1.setDay(d2.getDay());
			
			d1.setConsumption(d1.getConsumption()+d2.getConsumption());
			d1.setGeneration(d1.getGeneration()+d2.getGeneration());
			d1.setTrueGeneration(d1.getTrueGeneration()+d2.getTrueGeneration());
			
			d1.setStanBateriiNaPoczatkuSlotu(d1.getStanBateriiNaPoczatkuSlotu()+d2.getStanBateriiNaPoczatkuSlotu());
			
			d1.setZBateriiNaKonsumpcje(d1.getZBateriiNaKonsumpcje()+d2.getZBateriiNaKonsumpcje());
			d1.setZBateriiNaRynek(d1.getZBateriiNaRynek()+d2.getZBateriiNaRynek());
			
			
			d1.setZGeneracjiNaKonsumpcje(d1.getZGeneracjiNaKonsumpcje()+d2.getZGeneracjiNaKonsumpcje());
			d1.setZGeneracjiNaRynek(d1.getZGeneracjiNaRynek()+d2.getZGeneracjiNaRynek());
			d1.setZGeneracjiDoBaterii(d1.getZGeneracjiDoBaterii()+d2.getZGeneracjiDoBaterii());
			
			d1.setZRynekNaKonsumpcje(d1.getZRynekNaKonsumpcje()+d2.getZRynekNaKonsumpcje());
			d1.setZRynekDoBaterii(d1.getZRynekDoBaterii()+d2.getZRynekDoBaterii());
			
			d1.setCost(d1.getCost()+d2.getCost());
			
			d1.setCenaNaLokalnymRynku(d2.getCenaNaLokalnymRynku());

		}
	
}
