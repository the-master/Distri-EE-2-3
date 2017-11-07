package client;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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

    @PersistenceContext
    EntityManager em;
    
    public Main(String scriptFile) {
        super(scriptFile);
    }

    public static void main(String[] args) throws Exception {
        // TODO: use updated manager interface to load cars into companies
        Main m =new Main("trips");//.run();
        CarRentalSessionRemote session = m.getNewReservationSession("blabla");
        testEntity test = new testEntity(1,"name");
        session.createTest(test);
        testEntity test2 = session.getTest(1);
        System.out.println(test2.getName());
    }

    @Override
    protected Set<String> getBestClients(ManagerSessionRemote ms) throws Exception {
        return new HashSet<String>();
    }

    @Override
    protected String getCheapestCarType(CarRentalSessionRemote session, Date start, Date end, String region) throws Exception {
        
        for(String comp :session.getAllRentalCompanies())
          for(CarType type:session.getAvailableCarTypes(start, end))
              session.createQuote(comp, new ReservationConstraints(start, end, type.getName(), region));
       double bestprice = Double.MAX_VALUE;
       String rv = null
            ;
       for(Quote q : session.getCurrentQuotes())
           if(q.getRentalPrice()<bestprice)
           {
               rv=q.getCarType();
               bestprice=q.getRentalPrice();
           }
       return rv;
    }

    @Override
    protected CarType getMostPopularCarTypeIn(ManagerSessionRemote ms, String carRentalCompanyName, int year) throws Exception {
        CarType mostPopular=null;
        int popularity=0;
        int temp;
        for(CarType type:ms.getCarTypes(carRentalCompanyName))
            if(popularity<(temp=ms.getNumberOfReservations(carRentalCompanyName, type.getName())))
            {
                popularity =temp;
                mostPopular=type;
            }
           return mostPopular;
                    
    }

    @Override
    protected CarRentalSessionRemote getNewReservationSession(String name) throws Exception {
        InitialContext c = new InitialContext();
        return (CarRentalSessionRemote) c.lookup(CarRentalSessionRemote.class.getName());
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