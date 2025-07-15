import java.time.LocalDate;

public class SickLeaveRequest extends LeaveRequest {
    private final boolean hasMedicalCertificate;

    public SickLeaveRequest(int requestId, Employee employee,
            LocalDate startDate, LocalDate endDate,
            String reason, boolean hasMedicalCertificate) {
        super(requestId, employee, startDate, endDate, reason);
        this.hasMedicalCertificate = hasMedicalCertificate;
    }

    @Override
    public LeaveType getLeaveType() {
        return LeaveType.SICK;
    }

    @Override
    public boolean isValid() {
        int days = getNumberOfDays();
        if (days > 3 && !hasMedicalCertificate) {
            System.out.println("Medical certificate required for sick leave > 3 days");
            return false;
        }
        if (employee.getSickLeaveBalance() < days) {
            System.out.println("Insufficient sick leave balance");
            return false;
        }
        return true;
    }
}