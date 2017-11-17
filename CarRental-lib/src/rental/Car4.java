/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rental;

import java.io.Serializable;
import javax.annotation.Generated;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

/**
 *
 * @author s0217931
 */
@Entity
public class Car4 implements Serializable{
    @Id
    @GeneratedValue
    int id;
    @OneToOne
    CarType type;
    public Car4(){
        
    }
    public Car4(int i,CarType t){
        type=t;
    }
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
    
    }
