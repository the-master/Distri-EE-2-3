package session;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import rental.Car;
import rental.CarRentalCompany;
import rental.CarType;
import rental.RentalStore;
import rental.Reservation;

@Stateless
public class ManagerSession implements ManagerSessionRemote {
    
    @PersistenceContext
    EntityManager em;
    @Override
    public Set<CarType> getCarTypes(String company) {
        return new HashSet<CarType>(em.createQuery("SELECT a FROM CarType a").getResultList());
        
        
//        try {
//            return new HashSet<CarType>(RentalStore.getRental(company).getAllTypes());
//        } catch (IllegalArgumentException ex) {
//            Logger.getLogger(ManagerSession.class.getName()).log(Level.SEVERE, null, ex);
//            return null;
//        }
    }
    @Override
    public void createCar(CarType type){
        em.persist(new Car(0, type));
    }
    @Override
    public Set<Integer> getCarIds(String company, String type) {
        final HashSet<Integer> rv = new HashSet<>();
        em.createQuery("SELECT c FROM Car c").getResultList().forEach(new Consumer() {

            @Override
            public void accept(Object t) {
                rv.add(((Car)t).getId());
                System.out.println(t);
            }
        });
        return rv;
      //        Set<Integer> out = new HashSet<Integer>();
//        try {
//            for(Car c: RentalStore.getRental(company).getCars(type)){
//                out.add(c.getId());
//            }
//        } catch (IllegalArgumentException ex) {
//            Logger.getLogger(ManagerSession.class.getName()).log(Level.SEVERE, null, ex);
//            return null;
//        }
//        return out;
    }

    @Override
    public int getNumberOfReservations(String company, String type, int id) {
//        try {
//            return RentalStore.getRental(company).getCar(id).getReservations().size();
//        } catch (IllegalArgumentException ex) {
//            Logger.getLogger(ManagerSession.class.getName()).log(Level.SEVERE, null, ex);
//            return 0;
//        }
    return 0;
    }

    @Override
    public int getNumberOfReservations(String company, String type) {
        Set<Reservation> out = new HashSet<Reservation>();
//        try {
//            for(Car c: RentalStore.getRental(company).getCars(type)){
//                out.addAll(c.getReservations());
//            }
//        } catch (IllegalArgumentException ex) {
//            Logger.getLogger(ManagerSession.class.getName()).log(Level.SEVERE, null, ex);
//            return 0;
//        }
//        return out.size();
        return 0;
    }

    @Override
    public void createCartype(String name, int numberOfSeats, float trunkspace, double pricePerDay, boolean smokingAllowed) {
        CarType t =new CarType(name, numberOfSeats, trunkspace, pricePerDay, smokingAllowed); 
        em.persist(t);
        
    }

}