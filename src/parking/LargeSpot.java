package parking;

import vehicle.Vehicle;

public class LargeSpot extends ParkingSpot{
    public LargeSpot(String id) {
        super(id, SpotSize.LARGE);
    }

    @Override
    public boolean canFit(Vehicle vehicle) {
        return vehicle.getRequiredSpotSize() == SpotSize.LARGE;
    }
}
