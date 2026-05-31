package parking;

import vehicle.Vehicle;

public class SmallSpot extends ParkingSpot{
    public SmallSpot(String id) {
        super(id, SpotSize.SMALL);
    }

    @Override
    public boolean canFit(Vehicle vehicle) {
        return vehicle.getRequiredSpotSize() == SpotSize.SMALL;
    }
}
