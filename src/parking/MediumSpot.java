package parking;

import vehicle.Vehicle;

public class MediumSpot extends ParkingSpot{
    public MediumSpot(String id) {
        super(id, SpotSize.MEDIUM);
    }

    @Override
    public boolean canFit(Vehicle vehicle) {
        return vehicle.getRequiredSpotSize() == SpotSize.MEDIUM;
    }
}
