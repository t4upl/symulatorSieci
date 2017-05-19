
public class Point {

	private double alfa=-1;
	private double beta=-1;
	private double price;
	private double iloscEnergiiDoKupienia;
	
	
	//Cosntructors
	Point ()
	{
		
	}
	
	//used in test only
	Point(double cena)
	{
		this.price=cena;
	}
	
	Point (Point p)
	{
		this.alfa=p.alfa;
		this.beta=p.beta;
		this.price=p.price;
		this.iloscEnergiiDoKupienia=p.iloscEnergiiDoKupienia;	
	}
	
	//SETTERS
	public void setPrice(double value)
	{
		this.price = value;
	}
	
	public void setIloscEnergiiDoKupienia(double value)
	{
		this.iloscEnergiiDoKupienia=value;
	}
	
	public void setAlfa(double value)
	{
		this.alfa=value;
	}
	
	public void setBeta(double value)
	{
		this.beta=value;
	}
	
	//GETTERS
	public double getPrice()
	{
		return this.price;
	}
	
	public double getIloscEnergiiDoKupienia()
	{
		return this.iloscEnergiiDoKupienia;
	}
	
	public double getAlfa()
	{
		return this.alfa;
	}
	
	public double getBeta()
	{
		return this.beta;
	}
	
	//------------
	public String toString()
	{
		String s ="price: "+price+"\nenergia: "+iloscEnergiiDoKupienia;
		String s2="alfa: "+alfa+"\nbeta: "+beta;
		return s+"\n"+s2;
	}
	
	//ustaw alfe i bete tego punktu zeby znjdowal sie na linii z p
	public void updateAlfa(Point p2)
	{
		Point p1 = this;
		
		double roznicaEnergii = p1.getIloscEnergiiDoKupienia()-p2.getIloscEnergiiDoKupienia();
		double roznicaCen = (p1.getPrice()-p2.getPrice());
		double alfa = roznicaEnergii / roznicaCen;
		
		double beta = p1.getIloscEnergiiDoKupienia()-alfa * p1.getPrice();
		
		this.alfa =alfa;
		this.beta =beta;
	}
	
}
