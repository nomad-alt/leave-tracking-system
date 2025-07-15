import java.time.LocalDate;

public class LeaveTrackingSystem {
    public static void main(String[] args) {
        // Create an employee
        Employee emp1 = new Employee(101, "John Doe", "Engineering", 20, 10);
        emp1.displayInfo();

        System.out.println("\n---\n");

        // Create a leave request
        LeaveRequest request1 = new SickLeaveRequest(
                1,
                emp1,
                LocalDate.of(2025, 8, 1),
                LocalDate.of(2025, 8, 5),
                "Flu recovery",
                false);

        request1.displayDetails();

        System.out.println("\nApproval Process:");
        if (request1.approve("HR Manager")) {
            System.out.println("Leave approved successfully");
        } else {
            System.out.println("Leave approval failed");
        }

        System.out.println("\nUpdated Status:");
        request1.displayDetails();
        request1.displayStatusHistory();
    }
}