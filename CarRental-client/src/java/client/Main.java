package client;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.naming.InitialContext;
import javax.persistence.EntityManager;
import rental.CarType;
import rental.Reservation;
import rental.ReservationConstraints;
import rental.ReservationException;
import session.CarRentalSessionRemote;
import session.ManagerSessionRemote;

public class Main extends AbstractTestManagement<CarRentalSessionRemote, ManagerSessionRemote> {

    EntityManager em;
    
    public Main(String scriptFile) {
        super(scriptFile);
    }

    public static void main(String[] args) throws Exception {
        Main m = new Main("trips");

        ManagerSessionRemote managerSession= m.getNewManagerSession("test", "test");
        CarRentalSessionRemote rentals=m.getNewReservationSession("timeh");
        RentalCompanyLoader.CrcData d = RentalCompanyLoader.loadData("hertz.csv");
        managerSession.createRentalCompany("Hertz", d.regions, d.cars);
        d = RentalCompanyLoader.loadData("dockx.csv");
        managerSession.createRentalCompany("Dockx", d.regions, d.cars);
        m.run();    
    }

    @Override
    protected Set<String> getBestClients(ManagerSessionRemote ms) throws Exception {
        return ms.getBestClients();
    }

    @Override
    protected String getCheapestCarType(CarRentalSessionRemote session, Date start, Date end, String region) throws Exception {
       return session.getCheapestCarType(start,end,region); 
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
                System.out.println(session.createQuote(company, new ReservationConstraints(start, end, carType, region)));
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