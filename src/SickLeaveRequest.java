import java.time.LocalDate;

public class SickLeaveRequest extends LeaveRequest {
    private final boolean hasMedicalCertificate;

    public SickLeaveRequest(int requestId, Employee employee,
            LocalDate startDate, LocalDate endDate,
            String reason, boolean hasMedicalCertificate) {
        super(requestId, employee, startDate, endDate, reason);
        this.hasMedicalCertificate = hasMedicalCertificate;
    }

    public boolean hasMedicalCertificate() {
        return hasMedicalCertificate;
    }

    @Override
    public LeaveType getLeaveType() {
        return LeaveType.SICK;
    }

    @Override
    public boolean isValid() {
        if (hasMedicalCertificate) {
            return true;
        }
        return getNumberOfDays() <= 3;
    }
}