package rental;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import rental.CarType;
import rental.Reservation;
@Entity
public class Car implements  Serializable{
    @Id
    @GeneratedValue
    private int id;
    
    @OneToOne
    private CarType type;
//    
//    @OneToMany(cascade=CascadeType.ALL)
    private Set<Reservation> reservations;

    /***************
     * CONSTRUCTOR *
     ***************/
    public Car(){
        
    }
    public Car(int uid, CarType type) {
    	this.id = uid;
        this.type = type;
//        this.reservations = new HashSet<Reservation>();
    }

    /******
     * ID *
     ******/
    
    public int getId() {
    	return id;
    }
    
    /************
     * CAR TYPE *
     ************/
    
    public CarType getType() {
        return type;
    }
	
    public void setType(CarType type) {
	this.type = type;
    }
    
    /****************
     * RESERVATIONS *
     ****************/
    @Override
    public String toString(){
        return type.toString();
    }

    public boolean isAvailable(Date start, Date end) {
        if(!start.before(end))
            throw new IllegalArgumentException("Illegal given period");
//
        for(Reservation reservation : reservations) {
            if(reservation.getEndDate().before(start) || reservation.getStartDate().after(end))
                continue;
            return false;
        }
        return true;
    }
    
    public void addReservation(Reservation res) {
        reservations.add(res);
    }
    
    public void removeReservation(Reservation reservation) {
        // equals-method for Reservation is required!
        reservations.remove(reservation);
    }
//    @OneToMany(cascade=CascadeType.ALL)
    public Set<Reservation> getReservations() {
        return reservations;
    }

    public void setId(int id) {
        this.id = id;
    }

   
}