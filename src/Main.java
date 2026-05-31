import floor.Floor;
import parking.MediumSpot;
import parking.ParkingSpotFactory;
import parking.SmallSpot;
import parking.SpotSize;
import parkinglot.ParkingLot;
import ticket.Ticket;
import vehicle.Vehicle;
import vehicle.VehicleFactory;
import vehicle.VehicleType;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        ParkingLot lot = ParkingLot.getInstance();
        Floor floor = new Floor(1);
        for(int i = 0; i<=5; i++){
            floor.addSpot(ParkingSpotFactory.createSpot(SpotSize.SMALL, "S" + i));
            floor.addSpot(ParkingSpotFactory.createSpot(SpotSize.MEDIUM, "M" + i));
            floor.addSpot(ParkingSpotFactory.createSpot(SpotSize.LARGE, "L" + i));
        }
        lot.addFloor(floor);

        int threadCount = 12;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        List<Future<String>> futures = new ArrayList<>();
        ConcurrentLinkedQueue<String> ticketIds = new ConcurrentLinkedQueue<>();

        for(int i = 0; i<threadCount; i++){
            final int id = 1;
            futures.add(executor.submit(()->{
                Vehicle vehicle = VehicleFactory.createVehicle(
                        pickType(id), "TN" + String.format("%02d", id) + "AB" + id
                );
                Ticket ticket = lot.parkVehicle(vehicle);
                if(ticket != null){
                    ticketIds.add(ticket.getTicketId());
                    System.out.printf("[Thread %d] Parked %s -> ticket %s%n",
                            id, vehicle.getType(), ticket.getTicketId());
                    return ticket.getTicketId();
                } else {
                    System.out.printf("[Thread %d] No spot available for %s%n",
                            id, vehicle.getType());
                    return null;
                }
            }));
        }

        for(Future<String> f:futures){
            try{
                f.get();
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
        }

        System.out.println("\n-----Releasing spots-------\n");
        TimeUnit.SECONDS.sleep(2);
        futures.clear();

        for(String ticketId : ticketIds){
            futures.add(executor.submit(() -> {
                double fee = lot.releaseSpot(ticketId);
                System.out.printf("[Release] ticket %s -> fee Rs%.2f%n",
                        ticketId, fee);
                return ticketId;
            }));
        }

        for(Future<String> f : futures){
            try{
                f.get();
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
        }

        executor.shutdown();
        System.out.println("\nAll threads done. Lot full: " + lot.isFull());

    }

    private static VehicleType pickType(int i){
        return switch(i%3){
            case 0 -> VehicleType.BIKE;
            case 1 -> VehicleType.CAR;
            default -> VehicleType.TRUCK;
        };
    }
}