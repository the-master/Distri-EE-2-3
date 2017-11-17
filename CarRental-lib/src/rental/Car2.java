/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rental;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.annotation.Generated;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

/**
 *
 * @author s0217931
 */
@Entity
public class Car2 implements Serializable{
    @Id
    @GeneratedValue
    int id;
    @OneToOne
    CarType type;
    
    @OneToMany
    List<Reservation> reservations;
    
    public int getId() {
        return id;
    }

    public CarType getType() {
        return type;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setType(CarType type) {
        this.type = type;
    }
     @Override
    public String toString(){
        return type.toString();
    }

    public boolean isAvailable(Date start, Date end) {
        if(!start.before(end))
            throw new IllegalArgumentException("Illegal given period");
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

    Iterable<Reservation> getReservations() {
        return reservations;
    }
    }
