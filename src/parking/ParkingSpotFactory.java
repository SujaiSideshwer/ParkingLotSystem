package parking;

public class ParkingSpotFactory {
    public static ParkingSpot createSpot(SpotSize size, String spotId){
        switch(size){
            case SMALL: return new SmallSpot(spotId);
            case MEDIUM: return new MediumSpot(spotId);
            case LARGE: return new LargeSpot(spotId);
            default: throw new IllegalArgumentException("Unknown size: " + size);
        }
    }
}
