package client;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import javax.naming.InitialContext;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import rental.CarType;
import rental.Quote;
import rental.Reservation;
import rental.ReservationConstraints;
import rental.ReservationException;
import rental.testEntity;
import session.CarRentalSessionRemote;
import session.ManagerSessionRemote;

public class Main extends AbstractTestManagement<CarRentalSessionRemote, ManagerSessionRemote> {

    EntityManager em;
    
    public Main(String scriptFile) {
        super(scriptFile);
    }

    public static void main(String[] args) throws Exception {
        // TODO: use updated manager interface to load cars into companies
//    test1();
        Main m = new Main("trips");

        ManagerSessionRemote managerSession= m.getNewManagerSession("test", "test");
        CarRentalSessionRemote rentals=m.getNewReservationSession("timeh");
        RentalCompanyLoader.CrcData d = RentalCompanyLoader.loadData("hertz.csv");
        managerSession.createRentalCompany("hertz", d.regions, d.cars);
        d = RentalCompanyLoader.loadData("dockx.csv");
        managerSession.createRentalCompany("dockx", d.regions, d.cars);
        System.out.println(rentals.getAvailableCarTypes(new Date(0),new Date(10)));
        m.run();
//        System.out.println(m.getCheapestCarType(rentals, new Date(0   ),new Date(100), "Brussels"));
//      ses.createCartype("merc", 1, 1.1f, 11, true);
//      ses.createCartype("merc2", 1, 1.1f, 11, true);
//      
//      for(CarType t :ses.getCarTypes("test"))
//      {
//          ses.createCar(t);
//          ses.createCar(t);
//      }
//  
//      
      }
//      for(String s:ses.getReservations())
       static void test1() throws Exception{
               Main m =new Main("trips");//.run();
      ManagerSessionRemote managerSession= m.getNewManagerSession("test", "test");
      CarRentalSessionRemote rentalSession=m.getNewReservationSession("timmeh");
        RentalCompanyLoader.CrcData d = RentalCompanyLoader.loadData("hertz.csv");
        System.out.println(d.regions);
        managerSession.createRentalCompany("hertz", d.regions, d.cars);
        d = RentalCompanyLoader.loadData("dockx.csv");
        managerSession.createRentalCompany("dockx", d.regions, d.cars);
        System.out.println(managerSession.companies());
        System.out.println(managerSession.getCarTypes("hertz"));
        
        System.out.println(rentalSession.getAllRentalCompanies());
        rentalSession.createQuote("hertz", new ReservationConstraints(new Date(0), new Date(100),"Economy", "Brussels"));
        List<Reservation> reservations=rentalSession.confirmQuotes();
        System.out.println(reservations);
       }

    @Override
    protected Set<String> getBestClients(ManagerSessionRemote ms) throws Exception {
        return ms.getBestClients();
//        return new HashSet<String>();
    }

    @Override
    protected String getCheapestCarType(CarRentalSessionRemote session, Date start, Date end, String region) throws Exception {
       return session.getCheapestCarType(start,end,region); 
//        for(String comp :session.getAllRentalCompanies())
//          for(CarType type:session.getAvailableCarTypes(start, end))
//              session.createQuote(comp, new ReservationConstraints(start, end, type.getName(), region));
//       double bestprice = Double.MAX_VALUE;
//       String rv = null
//            ;
//       for(Quote q : session.getCurrentQuotes())
//           if(q.getRentalPrice()<bestprice)
//           {
//               rv=q.getCarType();
//               bestprice=q.getRentalPrice();
//           }
//       return rv;
    }

    @Override
    protected CarType getMostPopularCarTypeIn(ManagerSessionRemote ms, String carRentalCompanyName, int year) throws Exception {
        return ms.getMostPopularCarTypeIn(carRentalCompanyName,year);
        
                    
    }

    @Override
    protected CarRentalSessionRemote getNewReservationSession(String name) throws Exception {
        InitialContext c = new InitialContext();
        CarRentalSessionRemote rv = (CarRentalSessionRemote) c.lookup(CarRentalSessionRemote.class.getName());
        rv.setRenterName(name);
        return rv;
    }

    @Override
    protected ManagerSessionRemote getNewManagerSession(String name, String carRentalName) throws Exception {
        InitialContext c = new InitialContext();
        return (ManagerSessionRemote) c.lookup(ManagerSessionRemote.class.getName());
    }

    @Override
    protected void checkForAvailableCarTypes(CarRentalSessionRemote session, Date start, Date end) throws Exception {
        for (CarType cartype : session.getAvailableCarTypes(start, end)) {
            System.out.println(cartype.getName());
        }
    }

    @Override
    protected void addQuoteToSession(CarRentalSessionRemote session, String name, Date start, Date end, String carType, String region) throws Exception {
        
        
        HashSet<String> rentalCompanies = (HashSet) session.getAllRentalCompanies();
        for (String company : rentalCompanies) {
            try {
                session.createQuote(company, new ReservationConstraints(start, end, carType, region));
                return;
            } catch (ReservationException e) {
                
            }
        }
        throw new ReservationException("Could not add quote to session");
    }

    @Override
    protected List<Reservation> confirmQuotes(CarRentalSessionRemote session, String name) throws Exception {
        return session.confirmQuotes();
    }

    @Override
    protected int getNumberOfReservationsForCarType(ManagerSessionRemote ms, String carRentalName, String carType) throws Exception {
        return ms.getNumberOfReservations(carRentalName, carType);
    }
}