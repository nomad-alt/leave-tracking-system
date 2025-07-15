# Leave Tracking System

A Java-based application for managing employee leave requests using advanced object-oriented programming principles including inheritance, polymorphism, interfaces, and inner classes.

## Features

- **Employee Management**

  - Track leave balances (annual, sick, etc.)
  - Deduct leaves automatically upon approval
  - Department-specific information

- **Leave Request System**

  - Multiple leave types with specialized validation
  - Sick leave (requires medical certificate for >3 days)
  - Annual leave
  - Maternity/Paternity leave (future implementation)
  - Unpaid leave

- **Approval Workflow**

  - Standardized approval interface
  - Status tracking with history
  - Automatic balance deduction

- **Advanced Features**
  - Polymorphic request processing
  - Status change history (using inner classes)
  - Abstract base class for common functionality

## Enhanced Class Structure

### Core Classes

1. **Employee**

   - Stores employee information
   - Manages leave balances
   - Handles leave deductions

2. **LeaveRequest (Abstract)**

   - Base class for all leave types
   - Implements `Approvable` interface
   - Contains `StatusChange` inner class
   - Defines common fields and methods

3. **SickLeaveRequest**

   - Extends `LeaveRequest`
   - Specialized medical certificate validation
   - Custom approval logic

4. **Approvable (Interface)**
   - Standardizes approval process
   - `approve()` and `deny()` methods

## Getting Started

### Prerequisites

- Java JDK 17 or later (for modern date/time API)
- Maven (optional)

### Installation

````bash
git clone https://github.com/nomad-alt/leave-tracking-system
cd leave-tracking-system

### Running the Application

```bash
javac *.java
java LeaveTrackingSystem
````

### Example Usage

```bash
// Create employee
Employee emp = new Employee(101, "Jane Smith", "HR", 15, 8);

// Create sick leave request
LeaveRequest sickLeave = new SickLeaveRequest(
    1, emp,
    LocalDate.of(2025, 9, 1),
    LocalDate.of(2025, 9, 3),
    "Flu recovery",
    false
);

// Process approval
if (sickLeave.approve("HR Manager")) {
    System.out.println("Leave approved!");
} else {
    System.out.println("Approval failed");
}

// View status history
sickLeave.displayStatusHistory();
```
