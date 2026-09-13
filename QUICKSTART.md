# Smart Parking System - Quick Start Guide

## Getting Started

### 1. Build and Run the Application

```bash
# Navigate to the project directory
cd SmartParking

# Build the project
# On Windows
gradlew.bat build

# On Linux/Mac
./gradlew build

# Run the application
# On Windows
gradlew.bat bootRun

# On Linux/Mac
./gradlew bootRun
```

The application will start on `http://localhost:8080`

### 2. Verify System is Running

```bash
curl http://localhost:8080/api/parking/health
```

Expected response:
```json
{
  "status": "UP",
  "message": "Smart Parking System is running"
}
```

## Testing the API

### Check Parking Availability

```bash
curl http://localhost:8080/api/parking/availability
```

### Scenario 1: Vehicle Check-In

**Request:**
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

**Response:**
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

### Scenario 2: Get Active Transactions

```bash
curl http://localhost:8080/api/parking/active-transactions
```

### Scenario 3: Vehicle Check-Out

**Request:**
```bash
curl -X POST http://localhost:8080/api/parking/check-out/ABC123
```

**Response:**
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

### Scenario 4: Get Vehicle History

```bash
curl http://localhost:8080/api/parking/history/ABC123
```

## Complete Test Workflow

### Test 1: Multiple Vehicle Check-Ins

```bash
# Vehicle 1 - Car
curl -X POST http://localhost:8080/api/parking/check-in \
  -H "Content-Type: application/json" \
  -d '{"licensePlate": "CAR001", "vehicleType": "CAR", "ownerName": "Alice", "contactNumber": "555-0001"}'

# Vehicle 2 - Motorcycle
curl -X POST http://localhost:8080/api/parking/check-in \
  -H "Content-Type: application/json" \
  -d '{"licensePlate": "BIKE001", "vehicleType": "MOTORCYCLE", "ownerName": "Bob", "contactNumber": "555-0002"}'

# Vehicle 3 - SUV
curl -X POST http://localhost:8080/api/parking/check-in \
  -H "Content-Type: application/json" \
  -d '{"licensePlate": "SUV001", "vehicleType": "SUV", "ownerName": "Charlie", "contactNumber": "555-0003"}'

# Vehicle 4 - Bus
curl -X POST http://localhost:8080/api/parking/check-in \
  -H "Content-Type: application/json" \
  -d '{"licensePlate": "BUS001", "vehicleType": "BUS", "ownerName": "Diana", "contactNumber": "555-0004"}'
```

### Test 2: Check Updated Availability

```bash
curl http://localhost:8080/api/parking/availability
```

You should see reduced available spots and increased occupancy percentages.

### Test 3: Duplicate Check-In (Should Fail)

```bash
curl -X POST http://localhost:8080/api/parking/check-in \
  -H "Content-Type: application/json" \
  -d '{"licensePlate": "CAR001", "vehicleType": "CAR", "ownerName": "Alice", "contactNumber": "555-0001"}'
```

Expected error:
```json
{
  "errorCode": "VEHICLE_ALREADY_PARKED",
  "message": "Vehicle with license plate CAR001 is already parked",
  "status": 409,
  "timestamp": "2024-01-15T10:35:00"
}
```

### Test 4: Check-Out Vehicles

```bash
# Check out vehicle 1
curl -X POST http://localhost:8080/api/parking/check-out/CAR001

# Check out vehicle 2
curl -X POST http://localhost:8080/api/parking/check-out/BIKE001
```

### Test 5: Verify Spots are Released

```bash
curl http://localhost:8080/api/parking/availability
```

Available spots should increase for CAR and MOTORCYCLE types.

### Test 6: Get Vehicle History

```bash
curl http://localhost:8080/api/parking/history/CAR001
```

Should show all historical transactions for the vehicle.

## Fee Calculation Examples

Based on the configured rates:
- **Motorcycle**: $5/hour
- **Car**: $10/hour
- **SUV**: $12/hour
- **Bus**: $20/hour

Examples:
- 30 minutes in a Car spot = $10 (rounded up to 1 hour)
- 2.5 hours in a Car spot = $30 (rounded up to 3 hours)
- 45 minutes in a Bus spot = $20 (rounded up to 1 hour)

## Database Access

### H2 Console

Access the H2 database console at:
```
http://localhost:8080/h2-console
```

**Connection Details:**
- JDBC URL: `jdbc:h2:mem:smartparkingdb`
- Username: `sa`
- Password: (leave empty)

**Useful Queries:**

```sql
-- View all parking spots
SELECT * FROM parking_spots;

-- View all vehicles
SELECT * FROM vehicles;

-- View all parking transactions
SELECT * FROM parking_transactions;

-- View active transactions
SELECT * FROM parking_transactions WHERE is_active = true;

-- View available spots by type
SELECT supported_vehicle_type, COUNT(*) as available_count 
FROM parking_spots 
WHERE available = true 
GROUP BY supported_vehicle_type;
```

## Troubleshooting

### Application won't start
- Ensure Java 17+ is installed: `java -version`
- Check if port 8080 is available
- Review logs for configuration errors

### No parking spots available
- System initializes with 65 parking spots (distributed across 3 floors)
- Check `/api/parking/availability` to verify spot distribution
- Ensure vehicles have been checked out to free up spots

### Connection errors
- Verify application is running: `curl http://localhost:8080/api/parking/health`
- Check firewall settings
- Ensure correct port number in requests (default: 8080)

## Next Steps

1. Review the API documentation in [README.md](README.md)
2. Explore the codebase structure
3. Run unit and integration tests:
   ```bash
   # On Windows
   gradlew.bat test
   
   # On Linux/Mac
   ./gradlew test
   ```
4. Deploy to production environment (configure MySQL/PostgreSQL as needed)
5. Implement additional features based on requirements

## Support

For detailed information, refer to the main [README.md](README.md) file.
