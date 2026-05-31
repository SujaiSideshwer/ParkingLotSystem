package vehicle;

import parking.SpotSize;

public class Bike extends Vehicle{
    public Bike(String licensePlate) {
        super(licensePlate, VehicleType.BIKE);
    }

    @Override
    public SpotSize getRequiredSpotSize() {
        return SpotSize.SMALL;
    }
}
