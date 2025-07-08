import java.time.LocalDate;

public class LeaveRequest {
    // Enum for leave types
    public enum LeaveType {
        ANNUAL, SICK, MATERNITY, PATERNITY, UNPAID
    }

    // Enum for request status
    public enum Status {
        PENDING, APPROVED, REJECTED, CANCELLED
    }

    // Attributes
    private int requestId;
    private Employee employee;
    private LeaveType leaveType;
    private LocalDate startDate;
    private LocalDate endDate;
    private Status status;
    private String reason;

    // Constructor
    public LeaveRequest(int requestId, Employee employee, LeaveType leaveType,
            LocalDate startDate, LocalDate endDate, String reason) {
        this.requestId = requestId;
        this.employee = employee;
        this.leaveType = leaveType;
        this.startDate = startDate;
        this.endDate = endDate;
        this.reason = reason;
        this.status = Status.PENDING; // Default status is PENDING
    }

    // Getters and Setters
    public int getRequestId() {
        return requestId;
    }

    public Employee getEmployee() {
        return employee;
    }

    public LeaveType getLeaveType() {
        return leaveType;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public Status geStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    // Method to calculate number of leave days
    public int getNumberOfDays() {
        return (int) startDate.datesUntil(endDate.plusDays(1)).count();
    }

    // Method to display leave request details
    public void displayLeaveRequest() {
        System.out.println("Leave Request ID: " + requestId);
        System.out.println("Employee: " + employee.getName());
        System.out.println("Leave Type: " + leaveType);
        System.out.println("Start Date: " + startDate);
        System.out.println("End Date: " + endDate);
        System.out.println("Number of Days: " + getNumberOfDays());
        System.out.println("Status: " + status);
        System.out.println("Reason: " + reason);
    }
}
