package session;

import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.ejb.Stateful;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;
import rental.Car2;
import rental.CarRentalCompany;
import rental.CarType;
import rental.Quote;
import rental.RentalStore;
import rental.Reservation;
import rental.ReservationConstraints;
import rental.ReservationException;
import rental.testEntity;

@Stateful
@TransactionManagement(value = TransactionManagementType.BEAN)
public class CarRentalSession implements CarRentalSessionRemote {
    
    @PersistenceContext
    private EntityManager em;
    
    private String renter;
    private List<Quote> quotes = new LinkedList<Quote>();
 
    @Override
    public Set<String> getAllRentalCompanies() {
        return new HashSet<>(em.createQuery("SELECT c.name FROM CarRentalCompany c").getResultList());
    //new HashSet<String>(RentalStore.getRentals().keySet());
    }
    
    @Override
    public List<CarType> getAvailableCarTypes(Date start, Date end) {
        
        List<CarType> availableCarTypes ;
       availableCarTypes = em.createQuery("SELECT c.type FROM Car2 c WHERE NOT EXISTS ("
                + "SELECT r FROM c.reservations r WHERE r.endDate > :start AND :end >r.startDate )"
                + " " ).setParameter("start",start).setParameter("end",end).getResultList();
        return availableCarTypes;
    }

    @Override
    public Quote createQuote(String company, ReservationConstraints constraints) throws ReservationException {
       Quote q = em.find(CarRentalCompany.class, company).createQuote(constraints, renter);
       quotes.add(q);
       return q;
        
//        try {
//            Quote out = RentalStore.getRental(company).createQuote(constraints, renter);
//            quotes.add(out);
//            return out;
//        } catch(Exception e) {
//            throw new ReservationException(e);
//        }
    }

    @Override
    public List<Quote> getCurrentQuotes() {
        return quotes;
    }
    @Resource
    UserTransaction transaction;
     
    @Override
    public List<Reservation> confirmQuotes() throws ReservationException {
        
        
    
        List<Reservation> done = new LinkedList<Reservation>();
        try {
            transaction.begin();
                
            for (Quote quote : quotes) {
                List<Car2> availableCars;
        System.out.println( availableCars=em.createQuery("SELECT c FROM Car2 c WHERE c.type.name = :type AND NOT EXISTS ("
                + "SELECT r FROM c.reservations r WHERE r.endDate >= :start AND :end >=r.startDate "
                + ") " ).setParameter("type", quote.getCarType()).setParameter("start",quote.getStartDate()).setParameter("end",quote.getEndDate()).getResultList());
                if(availableCars.size()==0)
                    throw new ReservationException("");
                Car2 car=availableCars.get(0);
                Reservation reservation =new Reservation(quote, car.getId());
                em.persist(reservation);
                car.addReservation(reservation);
                em.persist(car);
                done.add(reservation);
                
//                done.add(em.find(CarRentalCompany.class, quote.getRentalCompany()).confirmQuote(quote));
            }
        } catch (ReservationException e) {
            try {
                transaction.rollback();
            } catch (IllegalStateException ex) {
                Logger.getLogger(CarRentalSession.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SecurityException ex) {
                Logger.getLogger(CarRentalSession.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SystemException ex) {
                Logger.getLogger(CarRentalSession.class.getName()).log(Level.SEVERE, null, ex);
            }
            
//            for(Reservation r:done)
//                em.find(CarRentalCompany.class, r.getRentalCompany()).cancelReservation(r);
            throw new ReservationException(e);
        } catch (NotSupportedException ex) {
            Logger.getLogger(CarRentalSession.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SystemException ex) {
            Logger.getLogger(CarRentalSession.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            transaction.commit();
        } catch (RollbackException ex) {
            Logger.getLogger(CarRentalSession.class.getName()).log(Level.SEVERE, null, ex);
        } catch (HeuristicMixedException ex) {
            Logger.getLogger(CarRentalSession.class.getName()).log(Level.SEVERE, null, ex);
        } catch (HeuristicRollbackException ex) {
            Logger.getLogger(CarRentalSession.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(CarRentalSession.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalStateException ex) {
            Logger.getLogger(CarRentalSession.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SystemException ex) {
            Logger.getLogger(CarRentalSession.class.getName()).log(Level.SEVERE, null, ex);
        }
        return done;
    }

    @Override
    public void setRenterName(String name) {
        if (renter != null) {
            throw new IllegalStateException("name already set");
        }
        renter = name;
    }

    @Override
    public String getCheapestCarType(Date start, Date end, String region) {
        //(SELECT c FROM CarRentalCompany c WHERE :region VALUE OF c.regions)
       
        List<CarType> results = em.createQuery("SELECT cheap FROM CarType cheap WHERE EXISTS "
               + "      (SELECT crc FROM CarRentalCompany crc WHERE :region MEMBER OF crc.regions"
               + "      AND EXISTS ("
               + "          SELECT car FROM crc.cars car WHERE car.type = cheap AND"
               + "          NOT EXISTS ("
                + "             SELECT r FROM car.reservations r WHERE r.endDate > :start AND :end >r.startDate "
                + "             ) "
               + "          )) ORDER BY cheap.rentalPricePerDay"
               ).setParameter("region", region).setParameter("start", start).setParameter("end", end).getResultList();
        
        return results.size()==0?null:results.get(0).getName();
//       em.createQuery("SELECT cheap "
//                + "FROM () comp WHERE :region MEMBER OF comp.regions JOIN comp.carTypes cheap WHERE cheap.rentalPricePerDay == MIN(cheap.rentalPricePerDay)")
    }
}