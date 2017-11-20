package session;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
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
        
    return ((Long)  em.createQuery("SELECT count(res) FROM Reservation res WHERE res.rentalCompany =:comp AND res.carType = :type").setParameter("type", type).setParameter("comp", company).getSingleResult()).intValue();
//        Set<Reservation> out = new HashSet<Reservation>();
//        try {
//            for(Car c: RentalStore.getRental(company).getCars(type)){
//                out.addAll(c.getReservations());
//            }
//        } catch (IllegalArgumentException ex) {
//            Logger.getLogger(ManagerSession.class.getName()).log(Level.SEVERE, null, ex);
//            return 0;
//        }
//        return out.size();
//        return 0;
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
        return (CarType) em.createQuery(
                "SELECT type "
                + "FROM CarType type, CarRentalCompany comp ,Reservation res "
               + "WHERE comp.name=:name AND type MEMBER OFF comp.carTypes "
                 + "AND res.carType = type.name AND MAX(count(res))"
                + " AND ((:yearStart <= res.startDate AND res.startDate <=yearEnd)OR(:yearStart<=res.endDate AND red.endDate <= yearEND))"
                ).setParameter("yearStart", new Date(0,0,year)).setParameter("yearEnd", new Date(30,11,year)).setParameter("name", carRentalCompanyName).getSingleResult();
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Set<String> getBestClients() {
        List<String> result = em.createQuery("SELECT r.carRenter FROM Reservation r GROUP BY r.carRenter ORDER BY SUM(r.rentalPrice)").getResultList();
        System.out.println("Printing best clients"); //ORDER BY SUM(r.rentalPrice) GROUP BY r.carRenter
        for (String s : result) {
            System.out.println(s);
        }
        return null;
    }

}