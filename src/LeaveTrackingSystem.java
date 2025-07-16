import java.time.LocalDate;
import java.util.*;

public class LeaveTrackingSystem {
    private final Map<Integer, Employee> employees = new HashMap<>();
    private final Map<Integer, LeaveRequest> leaveRequests = new HashMap<>();
    private final Queue<LeaveRequest> pendingApprovals = new LinkedList<>();
    private final Set<String> departments = new HashSet<>();
    private int nextRequestId = 1;

    public void addEmployee(Employee employee) {
        employees.put(employee.getEmployeeId(), employee);
        departments.add(employee.getDepartment());
    }

    public Employee getEmployee(int employeeId) {
        return employees.get(employeeId);
    }

    public LeaveRequest createLeaveRequest(Employee employee, LocalDate startDate,
            LocalDate endDate, String reason, LeaveRequest.LeaveType type,
            boolean hasMedicalCertificate) {
        LeaveRequest request;
        switch (type) {
            case SICK:
                request = new SickLeaveRequest(nextRequestId++, employee,
                        startDate, endDate, reason, hasMedicalCertificate);
                break;
            case ANNUAL:
                request = new AnnualLeaveRequest(nextRequestId++, employee,
                        startDate, endDate, reason);
                break;
            default:
                throw new IllegalArgumentException("Unsupported leave type");
        }

        leaveRequests.put(request.getRequestId(), request);
        pendingApprovals.add(request);
        return request;
    }

    public void processPendingRequests(String approver) {
        while (!pendingApprovals.isEmpty()) {
            LeaveRequest request = pendingApprovals.poll();
            if (request.isValid()) {
                request.approve(approver);
            } else {
                request.deny(approver, "Invalid request");
            }
        }
    }

    public List<LeaveRequest> getEmployeeLeaveHistory(int employeeId) {
        Employee employee = employees.get(employeeId);
        return employee != null ? employee.getLeaveHistory() : Collections.emptyList();
    }

    public void displayDepartmentStats() {
        System.out.println("\nDepartment Statistics:");
        for (String dept : departments) {
            long pending = leaveRequests.values().stream()
                    .filter(r -> r.getEmployee().getDepartment().equals(dept))
                    .filter(r -> r.getStatus() == LeaveRequest.Status.PENDING)
                    .count();
            System.out.printf("%s: %d pending requests%n", dept, pending);
        }
    }

    public static void main(String[] args) {
        LeaveTrackingSystem system = new LeaveTrackingSystem();

        // Add employees
        system.addEmployee(new Employee(101, "John Doe", "Engineering", 20, 10));
        system.addEmployee(new Employee(102, "Jane Smith", "HR", 25, 8));

        // Create leave requests
        Employee john = system.getEmployee(101);
        system.createLeaveRequest(john, LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(3), "Flu",
                LeaveRequest.LeaveType.SICK, false);

        Employee jane = system.getEmployee(102);
        system.createLeaveRequest(jane, LocalDate.now().plusDays(5),
                LocalDate.now().plusDays(10), "Vacation",
                LeaveRequest.LeaveType.ANNUAL, false);

        // Process requests
        system.processPendingRequests("HR Manager");

        // Display statistics
        system.displayDepartmentStats();

        // Show employee history
        System.out.println("\nJohn's Leave History:");
        system.getEmployeeLeaveHistory(101).forEach(LeaveRequest::displayDetails);
    }
}