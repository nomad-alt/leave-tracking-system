import java.time.LocalDate;

public class LeaveTrackingSystem {
    public static void main(String[] args) {
        // Create an employee
        Employee emp1 = new Employee(101, "John Doe", "Engineering", 20, 10);
        emp1.displayEmployeeInfo();

        System.out.println("\n---\n");

        // Create a leave request
        LeaveRequest request1 = new LeaveRequest(
                1,
                emp1,
                LeaveRequest.LeaveType.ANNUAL,
                LocalDate.of(2025, 8, 1),
                LocalDate.of(2025, 8, 5),
                "Family vacation");

        request1.displayLeaveRequest();
    }
}
