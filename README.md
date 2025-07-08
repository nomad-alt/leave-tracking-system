# Leave Tracking System

A Java-based application for managing employee leave requests using object-oriented programming principles.

## Features

- Employee management with leave balances
- Leave request submission and tracking
- Multiple leave types (Annual, Sick, etc.)
- Request status tracking (Pending/Approved/Rejected)
- Automatic calculation of leave days

## Class Structure

### 1. Employee Class

- Stores employee information (ID, name, department)
- Tracks leave balances (annual and sick leave)
- Methods to view and update employee data

### 2. LeaveRequest Class

- Manages leave request details
- Uses enums for leave types and statuses
- Calculates number of leave days between dates
- Tracks request reason and approval status

## Getting Started

### Prerequisites

- Java JDK 8 or later
- Basic Java development environment

### Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/yourusername/leave-tracking-system.git
   ```

### Running the Application

```bash
javac LeaveTrackingSystem.java
java LeaveTrackingSystem
```

### Example Usage

```bash
// Create an employee
Employee emp1 = new Employee(101, "John Doe", "Engineering", 20, 10);

// Create a leave request
LeaveRequest request1 = new LeaveRequest(
   1,
   emp1,
   LeaveRequest.LeaveType.ANNUAL,
   LocalDate.of(2023, 8, 1),
   LocalDate.of(2023, 8, 5),
   "Family vacation"
);

// Display information
emp1.displayEmployeeInfo();
request1.displayLeaveRequest();
```
