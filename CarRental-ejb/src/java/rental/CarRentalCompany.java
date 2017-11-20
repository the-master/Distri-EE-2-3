package rental;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.CascadeType;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.PersistenceContext;

@Entity
public class CarRentalCompany implements Serializable {
    
    @Id
    private String name;
    
    private static Logger logger = Logger.getLogger(CarRentalCompany.class.getName());
    
    
    @OneToMany()
    private List<Car2> cars;
    
    
    
    @OneToMany()
    private Set<CarType> carTypes;
    @ElementCollection
    private List<String> regions;

	
    /***************
     * CONSTRUCTOR *
     ***************/

    public CarRentalCompany(String name, List<String> regions, List<Car2> cars) {
        logger.log(Level.INFO, "<{0}> Car Rental Company {0} starting up...", name);
        setName(name);
        this.cars = cars;
        setRegions(regions);
        for (Car2 car : cars) {
            carTypes.add(car.getType());
        }
    }
    
    public CarRentalCompany() {
        
    }

    /********
     * NAME *
     ********/
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /***********
     * Regions *
     **********/
    public void setRegions(List<String> regions) {
        this.regions = regions;
    }
    
    public List<String> getRegions() {
        return this.regions;
    }

    /*************
     * CAR TYPES *
     *************/
    
    public Collection<CarType> getAllTypes() {
        return carTypes;
    }

    public CarType getType(String carTypeName) {
        for(CarType type:carTypes){
            if(type.getName().equals(carTypeName))
                return type;
        }
        throw new IllegalArgumentException("<" + carTypeName + "> No cartype of name " + carTypeName);
    }

    public boolean isAvailable(String carTypeName, Date start, Date end) {
        logger.log(Level.INFO, "<{0}> Checking availability for car type {1}", new Object[]{name, carTypeName});
        return getAvailableCarTypes(start, end).contains(getType(carTypeName));
    }

    public Set<CarType> getAvailableCarTypes(Date start, Date end) {
        Set<CarType> availableCarTypes = new HashSet<CarType>();
        for (Car2 car : cars) {
            if (car.isAvailable(start, end)) {
                availableCarTypes.add(car.getType());
            }
        }
        return availableCarTypes;
    }

    /*********
     * CARS *
     *********/
    
    public Car2 getCar(int uid) {
        for (Car2 car : cars) {
            if (car.getId() == uid) {
                return car;
            }
        }
        throw new IllegalArgumentException("<" + name + "> No car with uid " + uid);
    }

    public Set<Car2> getCars(CarType type) {
        Set<Car2> out = new HashSet<Car2>();
        for (Car2 car : cars) {
            if (car.getType().equals(type)) {
                out.add(car);
            }
        }
        return out;
    }
    
     public Set<Car2> getCars(String type) {
        Set<Car2> out = new HashSet<Car2>();
        for (Car2 car : cars) {
            if (type.equals(car.getType().getName())) {
                out.add(car);
            }
        }
        return out;
    }
     //TODO remove
//    private List<Car2> getAvailableCars(String carType, Date start, Date end) {
//        List<Car2> availableCars=null;
//        
////        System.out.println( availableCars=em.createQuery("SELECT c FROM Car2 c WHERE NOT EXISTS ("
////                + "SELECT r FROM c.reservations r WHERE r.endDate > :start AND :end >r.startDate "
////                + ") " ).setParameter("start",start).setParameter("end",end).getResultList());
////        
//        System.out.println(em);
//        availableCars=em.createQuery(
//                "SELECT c FROM Car2 c WHERE NOT EXISTS ("
//                + "SELECT r FROM c.reservations r )"
//                + "" ).getResultList();
//        
////        List<Car2> availableCars = new LinkedList<Car2>();
////        for (Car2 car : cars) {
////            if (car.getType().getName().equals(carType) && car.isAvailable(start, end)) {
////                availableCars.add(car);
////            }
////        }
//        return availableCars;
//    }

    /****************
     * RESERVATIONS *
     ****************/
    
    public Quote createQuote(ReservationConstraints constraints, String guest)
            throws ReservationException {
        logger.log(Level.INFO, "<{0}> Creating tentative reservation for {1} with constraints {2}",
                new Object[]{name, guest, constraints.toString()});

        try{
        if (!this.regions.contains(constraints.getRegion()) || !isAvailable(constraints.getCarType(), constraints.getStartDate(), constraints.getEndDate())) {
            throw new ReservationException("<" + name
                    + "> No cars available to satisfy the given constraints.");
        }}catch(IllegalArgumentException e)
        { throw new ReservationException(e);
        }
		
        CarType type = getType(constraints.getCarType());

        double price = calculateRentalPrice(type.getRentalPricePerDay(), constraints.getStartDate(), constraints.getEndDate());

        return new Quote(guest, constraints.getStartDate(), constraints.getEndDate(), getName(), constraints.getCarType(), price);
    }

    // Implementation can be subject to different pricing strategies
    private double calculateRentalPrice(double rentalPricePerDay, Date start, Date end) {
        return rentalPricePerDay * Math.ceil((end.getTime() - start.getTime())
                / (1000 * 60 * 60 * 24D));
    }
//
//    public Reservation confirmQuote(Quote quote) throws ReservationException {
//        logger.log(Level.INFO, "<{0}> Reservation of {1}", new Object[]{name, quote.toString()});
//        List<Car2> availableCars = getAvailableCars(quote.getCarType(), quote.getStartDate(), quote.getEndDate());
//        if (availableCars.isEmpty()) {
//            throw new ReservationException("Reservation failed, all cars of type " + quote.getCarType()
//                    + " are unavailable from " + quote.getStartDate() + " to " + quote.getEndDate());
//        }
//        Car2 car = availableCars.get((int) (Math.random() * availableCars.size()));
//
//        Reservation res = new Reservation(quote, car.getId());
//        car.addReservation(res);
//        return res;
//    }

    public void cancelReservation(Reservation res) {
        logger.log(Level.INFO, "<{0}> Cancelling reservation {1}", new Object[]{name, res.toString()});
        getCar(res.getCarId()).removeReservation(res);
    }
    
    public Set<Reservation> getReservationsBy(String renter) {
        logger.log(Level.INFO, "<{0}> Retrieving reservations by {1}", new Object[]{name, renter});
        Set<Reservation> out = new HashSet<Reservation>();
        for(Car2 c : cars) {
            for(Reservation r : c.getReservations()) {
                if(r.getCarRenter().equals(renter))
                    out.add(r);
            }
        }
        return out;
    }
    public void addCarType(CarType type){
        this.carTypes.add(type);
    }
    public void addCar(Car2 car) {
        this.cars.add(car);
        
//        System.out.println(car.getType().getId());
//       CarType t =this.getType(car.getType().getName());
//        System.out.println("#"+t.getId());
//        Car c = new Car();
//        c.setType(this.getType(car.getType().getName()));
//        this.cars.add(car);
    }

    
}