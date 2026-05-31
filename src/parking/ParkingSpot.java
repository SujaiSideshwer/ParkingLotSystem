package parking;

import vehicle.Vehicle;

public abstract class ParkingSpot {
    protected String id;
    protected SpotSize type;
    protected boolean isOccupied;
    protected Vehicle parkedVehicle;

    public ParkingSpot(String id, SpotSize type) {
        this.id = id;
        this.type = type;
        this.isOccupied = false;
    }

    public abstract boolean canFit(Vehicle vehicle);

    public boolean assignVehicle(Vehicle vehicle){
        if(!isOccupied && canFit(vehicle)){
            this.parkedVehicle = vehicle;
            this.isOccupied = true;
            return true;
        }
        return false;
    }

    public void removeVehicle(){
        this.parkedVehicle = null;
        this.isOccupied = false;
    }

    public boolean isAvailable(){
        return !isOccupied;
    }

    public String getSpotId(){
        return id;
    }

    public SpotSize getSize(){
        return type;
    }

    public Vehicle getParkedVehicle(){
        return parkedVehicle;
    }
}
