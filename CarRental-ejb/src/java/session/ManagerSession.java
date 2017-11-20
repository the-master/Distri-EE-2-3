package session;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import rental.Car2;
import rental.CarRentalCompany;
import rental.CarType;
import rental.RentalStore;
import rental.Res;
import rental.Reservation;

@Stateless
public class ManagerSession implements ManagerSessionRemote {
    
    @PersistenceContext
    EntityManager em;
    @Override
    public Set<CarType> getCarTypes(String company) {
        return new HashSet<>(em.createQuery("SELECT c FROM CarRentalCompany a JOIN a.carTypes c WHERE a.name=:name").setParameter("name", company).getResultList());
//        return new HashSet(em.find(CarRentalCompany.class, company).getAllTypes());
//        return new HashSet<CarType>(em.createQuery("SELECT a FROM CarType a").getResultList());
        
        
//        try {
//            return new HashSet<CarType>(RentalStore.getRental(company).getAllTypes());
//        } catch (IllegalArgumentException ex) {
//            Logger.getLogger(ManagerSession.class.getName()).log(Level.SEVERE, null, ex);
//            return null;
//        }
    }
    
    
    @Override
    public Set<Integer> getCarIds(String company, String type) {
        final HashSet<Integer> rv = new HashSet<>();
        return new HashSet<Integer>(em.createQuery("SELECT c.id FROM Car c").getResultList());
//        .forEach(new Consumer() {
//
//            @Override
//            public void accept(Object t) {
//                rv.add(((Car2)t).getId());
//                System.out.println(t);
//            }
//        });
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
        return em.createQuery("SELECT r FROM Reservation r JOIN CAR2 c WHERE r MEMBER OF c.reservations AND c.id=:id").setParameter("id", id).getResultList().size();
//        try {
//            return RentalStore.getRental(company).getCar(id).getReservations().size();
//        } catch (IllegalArgumentException ex) {
//            Logger.getLogger(ManagerSession.class.getName()).log(Level.SEVERE, null, ex);
//            return 0;
//        }
    }

    @Override
    public int getNumberOfReservations(String company, String type) {
        
//        em.createQuery("SELECT r FROM Reservation r JOIN (SELECT  car FROM CarRentalCompany c JOIN c.cars car WHERE c.name=:name AND car.type.name=:type) c WHERE" )
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
    @Override
    public void createRentalCompany(String company,List<String> regions,List<Car2> cars)
    {
        System.out.println("test");
        CarRentalCompany c = new CarRentalCompany();
        System.out.println(c);
        c.setName(company);
        c.setRegions(regions);
        
        em.persist(c);
        em.flush();
      for(Car2 car:cars)
      {
          if(!em.contains(car.getType())){
          em.persist(car.getType());
          em.flush();
          c.addCarType(car.getType());
          }
          Car2 ca=new Car2();
          em.persist(ca);
          em.flush();
          ca.setType(car.getType());
          em.persist(ca);
          em.flush();
          c.addCar(ca);
//          em.persist(c);
//          em.flush();
          System.out.println("done with 1 car");
//          c.addCarType(car.getType());
//          Car ca=new Car();
//          ca.setType(car.getType());
//          em.clear();
//          em.persist(ca);
//          em.flush();
          
//          c.addCar(car);
      }
      em.flush();
    }
    public List<String> companies(){
        List<String> rv = new ArrayList<>();
        for(CarRentalCompany c: (List<CarRentalCompany>)em.createQuery("SELECT c FROM CarRentalCompany c").getResultList())
            rv.add(c.getName());
        return rv;
    }

    @Override
    public CarType getMostPopularCarTypeIn(String carRentalCompanyName, int year) {
//        em.createQuery(null);
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Set<String> getBestClients() {
        //List<Object[]> result = em.createQuery("SELECT r.carRenter, r.total FROM (Reservation r INNER JOIN (SELECT r2.carRenter, SUM(r2.rentalPrice) total FROM Reservation r2 GROUP BY r2.carRenter) AS total )").getResultList();
        List<Object[]> query = em.createQuery("SELECT r.carRenter, SUM(r.rentalPrice) FROM Reservation r GROUP BY r.carRenter").getResultList();
        System.out.println("Printing best clients"); 
        HashSet<String> result = new HashSet<>();
        double max = Double.MIN_VALUE;
        for (Object[] x : query) {
            double price = (double) x[1];
            if (price > max) {
                max = price;
                result.clear();
                result.add((String) x[0]);
            } else if (price == max) {
                result.add((String) x[0]);
            }
        }
        
        return result;
    }

}