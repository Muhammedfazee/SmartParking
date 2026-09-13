# Smart Parking System - Spring Boot Implementation

A comprehensive backend system for managing smart parking lots with vehicle entry/exit management, parking space allocation, and fee calculation.

## Features

✅ **Automatic Parking Spot Allocation** - Allocates parking spots based on vehicle size
✅ **Check-In & Check-Out Tracking** - Records entry and exit times
✅ **Dynamic Fee Calculation** - Calculates fees based on parking duration and vehicle type
✅ **Real-Time Availability Updates** - Updates spot availability in real-time
✅ **Concurrency Handling** - Handles multiple simultaneous vehicle operations
✅ **Parking History** - Maintains complete transaction history
✅ **REST API** - Full RESTful API for parking operations

## Architecture Overview

### Technology Stack
- **Framework**: Spring Boot 3.1.5
- **Language**: Java 17
- **Database**: H2 (in-memory, easily replaceable with MySQL/PostgreSQL)
- **ORM**: JPA/Hibernate
- **Build**: Gradle 8.4

### Project Structure

```
SmartParking/
├── pom.xml                                 # Maven configuration
├── src/main/java/com/smartparking/
│   ├── SmartParkingApplication.java       # Main application entry point
│   ├── entity/                             # Domain entities
│   │   ├── VehicleType.java               # Vehicle type enum
│   │   ├── ParkingSpot.java               # Parking spot entity
│   │   ├── Vehicle.java                   # Vehicle entity
│   │   └── ParkingTransaction.java        # Parking transaction entity
│   ├── repository/                         # Data access layer
│   │   ├── ParkingSpotRepository.java
│   │   ├── VehicleRepository.java
│   │   └── ParkingTransactionRepository.java
│   ├── service/                            # Business logic layer
│   │   ├── ParkingManagementService.java  # Main orchestration service
│   │   ├── ParkingSpotService.java        # Spot management
│   │   └── VehicleService.java            # Vehicle management
│   ├── controller/                         # REST API endpoints
│   │   └── ParkingController.java
│   ├── dto/                                # Data Transfer Objects
│   │   ├── CheckInRequest.java
│   │   ├── CheckInResponse.java
│   │   ├── CheckOutResponse.java
│   │   └── ParkingAvailabilityDto.java
│   ├── exception/                          # Custom exceptions
│   │   ├── NoParkingSpotAvailableException.java
│   │   ├── ResourceNotFoundException.java
│   │   └── VehicleAlreadyParkedException.java
│   └── util/                               # Utility classes
│       └── FeeCalculationUtil.java
└── src/main/resources/
    └── application.properties              # Spring Boot configuration
```

## Entity Model

### VehicleType Enum
```
MOTORCYCLE: 1 spot, $5/hour
CAR:        1 spot, $10/hour
SUV:        1 spot, $12/hour
BUS:        2 spots, $20/hour
```

### Database Schema

**parking_spots**
- id: Primary Key
- spotNumber: Unique identifier (e.g., F1S01)
- floorNumber: Floor location
- available: Availability status
- supportedVehicleType: Type of vehicle the spot can accommodate
- currentOccupant: Reference to active parking transaction

**vehicles**
- id: Primary Key
- licensePlate: Unique vehicle identifier
- vehicleType: Type of vehicle
- ownerName: Vehicle owner name
- contactNumber: Contact information

**parking_transactions**
- id: Primary Key
- vehicleId: Foreign key to vehicle
- parkingSpotId: Foreign key to parking spot
- entryTime: Check-in timestamp
- exitTime: Check-out timestamp
- fees: Calculated parking fee
- isActive: Transaction status

## Setup Instructions

### Prerequisites
- Java 17 or higher
- Gradle 8.4+ (or use the included Gradle wrapper)

### Installation

1. **Clone the repository**
   ```bash
   cd SmartParking
   ```

2. **Build the project**
   ```bash
   # On Windows
   gradlew.bat build
   
   # On Linux/Mac
   ./gradlew build
   ```

3. **Run the application**
   ```bash
   # On Windows
   gradlew.bat bootRun
   
   # On Linux/Mac
   ./gradlew bootRun
   ```

   Or using Java directly:
   ```bash
   java -jar build/libs/smart-parking-system-1.0.0.jar
   ```

The application will start on `http://localhost:8080`

## API Endpoints

### Check-In Vehicle
**POST** `/api/parking/check-in`

Request:
```json
{
  "licensePlate": "ABC123",
  "vehicleType": "CAR",
  "ownerName": "John Doe",
  "contactNumber": "555-1234"
}
```

Response:
```json
{
  "transactionId": 1,
  "licensePlate": "ABC123",
  "spotNumber": "F1S01",
  "floorNumber": 1,
  "entryTime": "2024-01-15T10:30:00",
  "message": "Vehicle checked in successfully. Parking spot: F1S01"
}
```

### Check-Out Vehicle
**POST** `/api/parking/check-out/{licensePlate}`

Response:
```json
{
  "transactionId": 1,
  "licensePlate": "ABC123",
  "spotNumber": "F1S01",
  "entryTime": "2024-01-15T10:30:00",
  "exitTime": "2024-01-15T13:45:00",
  "parkingDurationHours": 3.0,
  "fees": 30.00,
  "message": "Vehicle checked out successfully. Total fee: $30.00"
}
```

### Get Parking Availability
**GET** `/api/parking/availability`

Response:
```json
[
  {
    "vehicleType": "CAR",
    "totalSpots": 28,
    "availableSpots": 22,
    "occupiedSpots": 6,
    "occupancyPercentage": 21.43
  },
  {
    "vehicleType": "MOTORCYCLE",
    "totalSpots": 13,
    "availableSpots": 11,
    "occupiedSpots": 2,
    "occupancyPercentage": 15.38
  }
]
```

### Get Active Transactions
**GET** `/api/parking/active-transactions`

Response:
```json
[
  {
    "id": 1,
    "vehicle": { "id": 1, "licensePlate": "ABC123", ... },
    "parkingSpot": { "id": 1, "spotNumber": "F1S01", ... },
    "entryTime": "2024-01-15T10:30:00",
    "exitTime": null,
    "fees": null,
    "isActive": true
  }
]
```

### Get Vehicle History
**GET** `/api/parking/history/{licensePlate}`

### System Health Check
**GET** `/api/parking/health`

## Key Design Patterns & Features

### 1. **Parking Spot Allocation Algorithm**
- Uses a greedy approach to find the first available spot
- Can be enhanced with sophisticated algorithms like:
  - Nearest to exit
  - Lowest floor preference
  - Parking duration prediction

### 2. **Fee Calculation**
- Based on hourly rates per vehicle type
- Rounds up to the nearest hour
- Minimum charge of $2.00 for any parking

### 3. **Concurrency Handling**
- Spring's `@Transactional` annotation ensures ACID properties
- Database-level locking prevents overselling of spots
- Proper exception handling for concurrent access attempts

### 4. **Database Design**
- Normalized schema to eliminate redundancy
- Proper indexing on frequently queried fields
- Foreign key constraints maintain data integrity

## Configuration

Edit `src/main/resources/application.properties` to customize:

- **Server Port**: `server.port=8080`
- **Database**: Currently H2 (in-memory)
- **JPA/Hibernate**: Auto-create tables on startup
- **Logging**: Configured for debugging

### Switching to MySQL/PostgreSQL

Replace H2 configuration with:

**For MySQL:**
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/smartparking
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.datasource.username=root
spring.datasource.password=password
spring.jpa.database-platform=org.hibernate.dialect.MySQL8Dialect
```

**For PostgreSQL:**
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/smartparking
spring.datasource.driver-class-name=org.postgresql.Driver
spring.datasource.username=postgres
spring.datasource.password=password
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
```

## Default Parking Lot Configuration

The system initializes with the following structure:

**Floor 1 & 2:**
- 10 CAR spots
- 5 MOTORCYCLE spots
- 2 BUS spots

**Floor 3:**
- 8 CAR spots
- 3 MOTORCYCLE spots
- 1 BUS spot

**Total: 65 parking spots**

## Exception Handling

- **NoParkingSpotAvailableException**: Thrown when no spots available for vehicle type
- **ResourceNotFoundException**: Thrown when vehicle or transaction not found
- **VehicleAlreadyParkedException**: Thrown when vehicle already has active parking

All exceptions are caught and return appropriate HTTP status codes with error messages.

## Testing the System

### Using cURL

**Check-In:**
```bash
curl -X POST http://localhost:8080/api/parking/check-in \
  -H "Content-Type: application/json" \
  -d '{
    "licensePlate": "ABC123",
    "vehicleType": "CAR",
    "ownerName": "John Doe",
    "contactNumber": "555-1234"
  }'
```

**Check-Out:**
```bash
curl -X POST http://localhost:8080/api/parking/check-out/ABC123
```

**Get Availability:**
```bash
curl http://localhost:8080/api/parking/availability
```

## Future Enhancements

1. **Advanced Allocation Algorithms**
   - Machine learning-based spot prediction
   - Thermal imaging for actual occupancy verification

2. **Payment Integration**
   - Online payment processing
   - Multiple payment methods

3. **Real-Time Notifications**
   - WebSocket support for live updates
   - SMS/Email alerts for customers

4. **Analytics Dashboard**
   - Revenue tracking
   - Peak hour analysis
   - Space utilization reports

5. **Mobile App Integration**
   - QR code scanning
   - Mobile reservations

6. **Multi-Lot Support**
   - Support multiple parking lots
   - Lot-specific configurations

## License

This project is open source and available under the MIT License.

## Support

For issues, questions, or contributions, please create an issue or pull request.

---

**Smart Parking System v1.0.0** - Built with Spring Boot 🚗
