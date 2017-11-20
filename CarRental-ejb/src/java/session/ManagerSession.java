package session;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import rental.Car2;
import rental.CarRentalCompany;
import rental.CarType;

@Stateless
public class ManagerSession implements ManagerSessionRemote {
    
    @PersistenceContext
    EntityManager em;
    @Override
    public Set<CarType> getCarTypes(String company) {
        return new HashSet<>(em.createQuery("SELECT c FROM CarRentalCompany a JOIN a.carTypes c WHERE a.name=:name").setParameter("name", company).getResultList());
    }
    
    
    @Override
    public Set<Integer> getCarIds(String company, String type) {
        final HashSet<Integer> rv = new HashSet<>();
        return new HashSet<Integer>(em.createQuery("SELECT c.id FROM Car c").getResultList());
    }

    @Override
    public int getNumberOfReservations(String company, String type, int id) {
        return em.createQuery("SELECT r FROM Reservation r JOIN CAR2 c WHERE r MEMBER OF c.reservations AND c.id=:id").setParameter("id", id).getResultList().size();
    }

    @Override
    public int getNumberOfReservations(String company, String type) {
        List<Object> count = em.createQuery("SELECT COUNT(r) FROM Reservation r WHERE r.carType = :type AND r.rentalCompany = :company ").setParameter("company", company).setParameter("type", type).getResultList();
        return count.size()>0 ? ((Long) count.get(0)).intValue() : 0;
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
          System.out.println("done with 1 car");
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
       List<Object[]> result= em.createQuery(
                "SELECT carType, COUNT(res) "
                + "FROM CarType cartype, CarRentalCompany comp ,Reservation res "
               + "WHERE comp.name=:name AND cartype MEMBER OF comp.carTypes "
                 + "AND res.carType = cartype.name "
                +"AND ( ( :yearStart <= res.startDate AND res.startDate <=:yearEnd ) OR ( :yearStart<=res.endDate AND res.endDate <= :yearEnd ) ) GROUP BY carType "
                ).setParameter("name", carRentalCompanyName).setParameter("yearStart", new Date(year-1900,0,0)).setParameter("yearEnd", new Date(year-1900,11,30)).getResultList(); 
        Long max = Long.MIN_VALUE;
        CarType biggest = null;
        for (Object[] o : result) {
            CarType ct = (CarType) o[0];
            Long nb = (Long) o[1];
            if (nb > max) {
                max = nb;
                biggest = ct;
            }
        }
        return biggest;
    }

    @Override
    public Set<String> getBestClients() {
        List<Object[]> query = em.createQuery("SELECT r.carRenter, COUNT(r.carRenter) FROM Reservation r GROUP BY r.carRenter").getResultList();
        HashSet<String> result = new HashSet<>();
        Long max = Long.MIN_VALUE;
        for (Object[] x : query) {
            Long price = (Long) x[1];
            if (price > max) {
                max = price;
                result.clear();
                result.add((String) x[0]);
            } else if (price.equals(max)) {
                result.add((String) x[0]);
            }
        }
        return result;
    }

}