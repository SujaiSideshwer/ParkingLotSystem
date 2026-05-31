package floor;

import parking.ParkingSpot;
import vehicle.Vehicle;

import java.util.ArrayList;
import java.util.List;

public class Floor {
    private int floorNumber;
    private List<ParkingSpot> spots;

    public Floor(int floorNumber) {
        this.floorNumber = floorNumber;
        this.spots = new ArrayList<>();
    }

    public void addSpot(ParkingSpot spot){
        spots.add(spot);
    }

    public ParkingSpot findAvailableSpot(Vehicle vehicle){
        return spots.stream()
                .filter(s -> s.isAvailable() && s.canFit(vehicle))
                .findFirst()
                .orElse(null);
    }

    public boolean isFull(){
        return spots.stream().noneMatch(ParkingSpot::isAvailable);
    }

    public int getFloorNumber(){
        return floorNumber;
    }

    public List<ParkingSpot> getSpots() {
        return spots;
    }
}
