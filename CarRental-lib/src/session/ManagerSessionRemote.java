package session;

import java.util.List;
import java.util.Set;
import javax.ejb.Remote;
import rental.CarType;
import rental.Car2;
import rental.Res;
@Remote
public interface ManagerSessionRemote {
    
    public Set<CarType> getCarTypes(String company);
    
    public Set<Integer> getCarIds(String company,String type);
    
    public int getNumberOfReservations(String company, String type, int carId);
    
    public int getNumberOfReservations(String company, String type);
    
    public void createCartype(String name,int numberOfSeats,float trunkspace,double pricePerDay,boolean smokingAllowed);
    
//    public void createCar(CarType t);
    public void createRentalCompany(String company,List<String> regions,List<Car2> cars);
    public List<String> companies();

    public CarType getMostPopularCarTypeIn(String carRentalCompanyName, int year);

    public Set<String> getBestClients();
}