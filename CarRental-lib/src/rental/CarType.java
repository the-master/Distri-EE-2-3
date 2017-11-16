package rental;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class CarType implements Serializable {
    
    @Id
    @GeneratedValue
    int id;
    
    private String name;
    
    
    private int nbOfSeats;
    private boolean smokingAllowed;
    private double rentalPricePerDay;
    //trunk space in liters
    private float trunkSpace;

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNbOfSeats(int nbOfSeats) {
        this.nbOfSeats = nbOfSeats;
    }

    public void setSmokingAllowed(boolean smokingAllowed) {
        this.smokingAllowed = smokingAllowed;
    }

    public void setRentalPricePerDay(double rentalPricePerDay) {
        this.rentalPricePerDay = rentalPricePerDay;
    }

    public void setTrunkSpace(float trunkSpace) {
        this.trunkSpace = trunkSpace;
    }
    
    
    
    /***************
     * CONSTRUCTOR *
     ***************/
    // Needed for entity class
    public CarType() {
        
    }
//    public static CarType createCar(String name, int nbOfSeats, float trunkSpace, double rentalPricePerDay, boolean smokingAllowed){
//      CarType t = new CarType();
//      return t;
//    }
    public CarType(String name, int nbOfSeats, float trunkSpace, double rentalPricePerDay, boolean smokingAllowed) {
        this.name = name;
        this.nbOfSeats = nbOfSeats;
        this.trunkSpace = trunkSpace;
        this.rentalPricePerDay = rentalPricePerDay;
        this.smokingAllowed = smokingAllowed;
    }
    
   

    public String getName() {
    	return name;
    }
    
    public int getNbOfSeats() {
        return nbOfSeats;
    }
    
    public boolean isSmokingAllowed() {
        return smokingAllowed;
    }

    public double getRentalPricePerDay() {
        return rentalPricePerDay;
    }
    
    public float getTrunkSpace() {
    	return trunkSpace;
    }
    public int getId(){
        return this.id;
    }
    
    /*************
     * TO STRING *
     *************/
    
    @Override
    public String toString() {
    	return String.format("Car type: %s \t[seats: %d, price: %.2f, smoking: %b, trunk: %.0fl]" , 
                getName(), getNbOfSeats(), getRentalPricePerDay(), isSmokingAllowed(), getTrunkSpace());
    }

    @Override
    public int hashCode() {
        final int prime = 31;
	int result = 1;
	result = prime * result + ((name == null) ? 0 : name.hashCode());
	return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
	if (obj == null)
            return false;
	if (getClass() != obj.getClass())
            return false;
	CarType other = (CarType) obj;
	if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
	return true;
    }
}