package symulacja2;

public class Point {
	private float alfa=-1;
	private float beta=-1;
	private float price;
	private float iloscEnergiiDoKupienia;
	
	//SETTERS
	public void setPrice(float value)
	{
		this.price = value;
	}
	
	public void setIloscEnergiiDoKupienia(float value)
	{
		this.iloscEnergiiDoKupienia=value;
	}
	
	public void setAlfa(float value)
	{
		this.alfa=value;
	}
	
	public void setBeta(float value)
	{
		this.beta=value;
	}
	
	//GETTERS
	public float getPrice()
	{
		return this.price;
	}
	
	public float getIloscEnergiiDoKupienia()
	{
		return this.iloscEnergiiDoKupienia;
	}
	
	public float getAlfa()
	{
		return this.alfa;
	}
	
	public float getBeta()
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
	
	 
}
