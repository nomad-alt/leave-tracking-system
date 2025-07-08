public class Employee {
    // Attributes
    private int employeeId;
    private String name;
    private String department;
    private int annualLeaveBalance;
    private int sickLeaveBalance;

    // Constructor
    public Employee(int employeeId, String name, String department,
            int annualLeaveBalance, int sickLeaveBalance) {
        this.employeeId = employeeId;
        this.name = name;
        this.department = department;
        this.annualLeaveBalance = annualLeaveBalance;
        this.sickLeaveBalance = sickLeaveBalance;
    }

    // Getters and setters
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

    public void setAnnualLeaveBalance(int annualLeaveBalance) {
        this.annualLeaveBalance = annualLeaveBalance;
    }

    public int getSickLeaveBalance() {
        return sickLeaveBalance;
    }

    public void setSickLeaveBalance(int sickLeaveBalance) {
        this.sickLeaveBalance = sickLeaveBalance;
    }

    // Method to display employee information
    public void displayEmployeeInfo() {
        System.out.println("Employee ID: " + employeeId);
        System.out.println("Name: " + name);
        System.out.println("Department: " + department);
        System.out.println("Annual Leave Balance: " + annualLeaveBalance + " days");
        System.out.println("Sick Leave Balance: " + sickLeaveBalance + " days");
    }
}