# Parking Lot System

A Java-based parking lot management system built as an object-oriented design exercise. This project demonstrates core OOP principles alongside three classic design patterns: **Singleton**, **Strategy and Factory**.

---

## Table of Contents

1. [Overview](#overview)
2. [Project Structure](#project-structure)
3. [Class Design](#class-design)
4. [Design Patterns](#design-patterns)
5. [How It Works](#how-it-works)
6. [Concurrency & Thread Safety](#concurrency--thread-safety)
7. [Testing](#testing)
8. [Getting Started](#getting-started)

---

## Overview

The system models a multi-floor parking lot that can accommodate three vehicle types (bikes, cars, trucks) across three spot sizes (small, medium, large). It issues tickets on entry, calculates fees on exit using a pluggable pricing strategy, and handles concurrent vehicle arrivals safely.

---

## Project Structure

```
src/
│
├── enums/
│   ├── VehicleType.java      #BIKE, CAR, TRUCK
│   ├── SpotSize.java         #SMALL, MEDIUM, LARGE
│   └── PaymentStatus.java    #PENDING, PAID, FAILED
│
├── vehicle/
│   ├── Vehicle.java          #Abstract Base
│   ├── Bike.java
│   ├── Car.java
│   └── Truck.java
│
├── spot/
│   ├── ParkingSpot.java          #Abstract Base
│   ├── SmallSpot.java
│   ├── MediumSpot.java
│   └── LargeSpot.java
│
├── core/
│   ├── Floor.java          #Abstract Base
│   ├── Ticket.java
│   └── ParkingLot.java
│
├── pricing/
│   ├── PricingStrategy.java          #Abstract Base
│   ├── HourlyPricing.java
│   └── FlatRatePricing.java
│
├── payment/
│   └── PaymentService.java
│
├── factory/
│   ├── VehicleFactory.java
│   └── ParkingSpotFactory.java
│
└── test/
    └── ParkingLotConcurrencyTest.java
```
---

## Class Design

The system is built around two inheritance hierarchies and a set of collaborating service classes.

### Vehicle hierarchy

`Vehicle` is an abstract class holding the license plate and vehicle type. Each subclass declares the spot size it requires via getRequiredSpotSize()

```
Vehicle (abstract)
├── Bike  -> requires SpotSize.SMALL
├── Car   -> requires SpotSize.SMALL
└── Truck -> requires SpotSize.LARGE
```

### ParkingSpot hierarchy

`ParkingSpot` is an abstract class managing ing occupancy state. Each subclass implements canFit(Vehicle)", which compares the vehicle's required size against the spot's own size. Spot-matching logic lives here close to the data rather than in Parkinglot'.

```
ParkingSpot (abstract)
├── SmallSpot  -> accepts Bike
├── MediumSpot -> accepts Car
└── LargeSpot  -> accepts Truck
```

### Core structure

```
ParkingLot (Singleton)
└── List<Floor>
     └── List<ParkingSpot>
```

`ParkingLot` is the entry point for all operations: parking a vehicle, releasing a spot, and checking lot capacity. It holds a 'Map<String, Ticket> of active tickets for 0(1) lookup on exit.

`Floor` provides findAvailableSpot(Vehicle)` a stream filter over its spots that returns the first available spot that fits the vehicle.

`Ticket` records the vehicle, spot, entry time, and exit time. getDurationMinutes() computes elapsed time from entry to exit (or now, if still parked), which feeds directly into the pricing calculation.

---

## Design Patterns

### 1. Singleton Parking Lot

**Problem:** A parking lot is a single physical entity. Multiple instances would produce inconsistent state invisible to another. a ticket issued by one instance would be invisible to another.

**Implementation:** The constructor is private. A static getInstance() method creates the instance on first call and returns it on every subsequent call. Double-checked locking with a volatile field makes this safe under concurrent access.

```java
public class Parkinglot (
  private static volatile ParkingLot instance;
  private ParkingLot(String name) { ... }
  public static ParkingLot getInstance() {
    if (instance == null) {
      synchronized (ParkingLot.class) {
        if (instance == null) (
          new Parking Lot ("Main Lot"); instance
          }
        }
      }
    return instance;
  }
}
```
The outer null check avoids acquiring the lock on every call (performance). The inner null check inside the `synchronized` block prevents a second thread from creating a duplicate instance after the first thread finishes initialization.

---

### 2. Strategy PricingStrategy

**Problem:** Fee calculation rules vary hourly rates, flat rates, weekend surges, day passes. Hardcoding any one algorithm into PaymentService makes it impossible to change pricing without modifying service code.

**Implementation:** PricingStrategy is an interface with a single method. PaymentService holds a reference to whichever strategy is currently active and delegates all calculation to it. The strategy can be swapped at runtime via setPricingStrategy().

```java
public interface PricingStrategy{
  double calculate(long durationMinutes);
}

public class HourlyPricing implements PricingStrategy {
  public double calculate(long durationMinutes){
    long hours (long) Math.ceil(durationMinutes / 60.0);
    return hours ratePerHour;
  }
}

public class PaymentService {
  private PricingStrategy pricingStrategy;
  public double calculateFee (Ticket ticket) {
    return pricingStrategy.calculate(ticket.getDurationMinutes());
  }
  public void setPricingStrategy (PricingStrategy strategy){
    this.pricingStrategy strategy;
  }
}
```

Adding a new pricing model requires only a new class implementing PricingStrategy - no changes to PaymentService or any other existing class. This is the Open/Closed Principle in practice.

---

### 3. Factory VehicleFactory & ParkingSpotFactory

**Problem:** Call sites that use `new Car(...)` or `new SmallSpot(...)` directly are coupled to concrete classes. If construction logic grows (validation, logging, registration), it has to be duplicated across every call site.

**Implementation:** Static factory methods accept an enum value and return the appropriate concrete instance. Callers depend only on the abstract type and the enum - never on a concrete class name.

```java
public class VehicleFactory {
  public static Vehicle createVehicle(VehicleType type, String licensePlate) {
    return switch (type) {
      case BIKE new Bike(licensePlate);
      case CAR -> new Car(licensePlate);
      case TRUCK -> new Truck (licensePlate);
    };
  }
}

public class ParkingSpotFactory {
  public static ParkingSpot createSpot(SpotSize size, String spotId) {
    return switch (size) {
      case SMALL -> new SmallSpot(spotId);
      case MEDIUM-> new MediumSpot(spotId);
      case LARGE -> new LargeSpot(spotId);
    };
  }
}
```

`ParkingSpotFactory` is particularly useful when populating floors from a configuration file or database, where you have SpotSize values at runtime rather than class names at compile time.

---

## How It Works

### Parking a vehicle

1. Caller invokes lot.parkVehicle (vehicle).
2. ParkingLot iterates over floors and calls floor.findAvailableSpot(vehicle).
3. Floor streams its spots, filtering for available spots where spot.canFit(vehicle) returns true.
4. The first matching spot is assigned: 'spot.assignVehicle(vehicle) sets isoccupied true.
5. A Ticket is created with the current timestamp, stored in activeTickets, and returned to the caller.

### Releasing a spot

1. Caller invokes lot.releaseSpot (ticketId).
2. The ticket is looked up in activeTickets (0(1)).
3. ticket.markExit() records the exit timestamp.
4. paymentService.calculateFee(ticket) calls ticket.getDurationMinutes() and passes the result to the active PricingStrategy.
5. Payment is processed and ticket.paymentStatus is set to PAID.
6. spot.removeVehicle() clears the spot for the next vehicle.
7. The ticket is removed from activeTickets and the fee is returned.

---

## Concurrency & Thread Safety

Real parking lots have multiple entry and exit lanes operating simultaneously. Two threads finding the same available spot and both calling assignVehicle() would result in a double-assignment.
parkVehicle() and releaseSpot() are both synchronized, ensuring only one thread can modify spot state or the activeTickets map at a time. The ParkingLot instance field uses volatile to guarantee visibility of the initialized reference across threads.
For higher-throughput scenarios, ConcurrentHashMap for activeTickets combined with per-spot ReentrantLock would allow parallel operations on different spots the current synchronized approach serializes all parking operations through a single lock.

---

## Testing

The concurrency test uses two key primitives:

**`CountDownLatch`** - a latch initialized to 1 holds all spawned threads at a start gate. A single countDown() releases them all simultaneously, maximizing contention and the chance of exposing race conditions.

**`ConcurrentLinkedQueue`** - collects issued tickets safely across threads without additional locking.

---

## Getting Started

**Prerequisites:** Java 17+, Maven or Gradle.

**Clone and run:**

```bash
git clone https://github.com/your-username/parking-lot-system.git
cd parking-lot-system
javac -d out src/**/*.java
java -cp out test. Parking Lot Concurrency Test
```

**Quick smoke test:**

```java
Parkinglot lot = ParkingLot.getInstance();
Floor floor =  new Floor(1);
floor.addSpot(ParkingSpotFactory.createSpot(SpotSize.MEDIUM, "F1-M1"));
lot.addFloor(floor);

Vehicle car VehicleFactory.createVehicle(VehicleType. CAR, "TN01AB1234");
Ticket ticket lot.parkVehicle(car);

Thread.sleep(2000); // simulate parking duration

double fee lot.releaseSpot(ticket.getTicketId()); System.out.println("Fee charged: " + fee);
```

---

## Design Decisions Worth Noting

`canFit()` lives on ParkingSpot, not on ParkingLot. Spot-matching logic belongs close to the spot data Parkinglot shouldn't need to know the internal rules of each spot type.

`PricingStrategy` is injected into Payment Service rather than looked up statically. This makes it trivial to test Paymentservice with a mock strategy that returns a fixed fee, without any real time passing.

The Factory pattern was added deliberately rather than being part of the original design. Direct new Car(...) calls work fine for a small codebase - the factory earns its place when construction logic grows or when the concrete type is determined at runtime from external input.
