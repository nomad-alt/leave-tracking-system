import java.time.LocalDate;

public class AnnualLeaveRequest extends LeaveRequest {
    public AnnualLeaveRequest(int requestId, Employee employee,
            LocalDate startDate, LocalDate endDate, String reason) {
        super(requestId, employee, startDate, endDate, reason);
    }

    @Override
    public LeaveType getLeaveType() {
        return LeaveType.ANNUAL;
    }

    @Override
    public boolean isValid() {
        int days = getNumberOfDays();
        if (employee.getAnnualLeaveBalance() < days) {
            System.out.println("Insufficient annual leave balance");
            return false;
        }
        if (days > 14) {
            System.out.println("Annual leave cannot exceed 14 days at once");
            return false;
        }
        return employee.getAnnualLeaveBalance() >= getNumberOfDays();
    }
}