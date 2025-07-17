import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Employee implements Serializable {
    private static final long serialVersionUID = 1L;

    private final int employeeId;
    private final String name;
    private String department;
    private int annualLeaveBalance;
    private int sickLeaveBalance;
    private final List<LeaveRequest> leaveHistory = new ArrayList<>();

    public Employee(int employeeId, String name, String department,
            int annualLeaveBalance, int sickLeaveBalance) {
        this.employeeId = employeeId;
        this.name = name;
        this.department = department;
        this.annualLeaveBalance = annualLeaveBalance;
        this.sickLeaveBalance = sickLeaveBalance;
    }

    // Getters
    public int getEmployeeId() {
        return employeeId;
    }

    public String getName() {
        return name;
    }

    public String getDepartment() {
        return department;
    }

    public int getAnnualLeaveBalance() {
        return annualLeaveBalance;
    }

    public int getSickLeaveBalance() {
        return sickLeaveBalance;
    }

    public List<LeaveRequest> getLeaveHistory() {
        return leaveHistory;
    }

    // Setters
    public void setDepartment(String department) {
        this.department = department;
    }

    public void setAnnualLeaveBalance(int balance) {
        this.annualLeaveBalance = balance;
    }

    public void setSickLeaveBalance(int balance) {
        this.sickLeaveBalance = balance;
    }

    public void addLeaveRequest(LeaveRequest request) {
        leaveHistory.add(request);
    }

    public void deductLeaveDays(LeaveRequest.LeaveType type, int days) {
        switch (type) {
            case ANNUAL -> annualLeaveBalance -= days;
            case SICK -> sickLeaveBalance -= days;
            default -> throw new IllegalArgumentException("Unexpected value: " + type);
        }
    }

    public void displayInfo() {
        System.out.printf("""
                Employee ID: %d
                Name: %s
                Department: %s
                Annual Leave: %d days
                Sick Leave: %d days
                """, employeeId, name, department, annualLeaveBalance, sickLeaveBalance);
    }
}