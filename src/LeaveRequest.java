import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public abstract class LeaveRequest implements Approvable {
    protected final int requestId;
    protected final Employee employee;
    protected final LocalDate startDate;
    protected final LocalDate endDate;
    protected Status status;
    protected final String reason;
    protected final List<StatusChange> statusHistory = new ArrayList<>();

    public enum Status {
        PENDING, APPROVED, REJECTED, CANCELLED
    }

    public enum LeaveType {
        ANNUAL, SICK, MATERNITY, PATERNITY, UNPAID
    }

    public LeaveRequest(int requestId, Employee employee,
            LocalDate startDate, LocalDate endDate, String reason) {
        this.requestId = requestId;
        this.employee = employee;
        this.startDate = startDate;
        this.endDate = endDate;
        this.reason = reason;
        this.status = Status.PENDING;
        recordStatusChange("CREATED", Status.PENDING.toString(), "System");
    }

    public abstract LeaveType getLeaveType();

    public abstract boolean isValid();

    public int getNumberOfDays() {
        return (int) startDate.datesUntil(endDate.plusDays(1)).count();
    }

    public void displayDetails() {
        System.out.printf("""
                Request ID: %d
                Employee: %s
                Leave Type: %s
                Dates: %s to %s (%d days)
                Status: %s
                Reason: %s
                """, requestId, employee.getName(), getLeaveType(),
                startDate, endDate, getNumberOfDays(), status, reason);
    }

    // Inner class for status tracking
    public class StatusChange {
        private final String fromStatus;
        private final String toStatus;
        private final LocalDate changeDate;
        private final String changedBy;

        public StatusChange(String fromStatus, String toStatus, String changedBy) {
            this.fromStatus = fromStatus;
            this.toStatus = toStatus;
            this.changeDate = LocalDate.now();
            this.changedBy = changedBy;
        }

        public String getChangeSummary() {
            return String.format("[%s] %s → %s by %s",
                    changeDate, fromStatus, toStatus, changedBy);
        }
    }

    public void recordStatusChange(String fromStatus, String toStatus, String changedBy) {
        statusHistory.add(new StatusChange(fromStatus, toStatus, changedBy));
    }

    public void displayStatusHistory() {
        System.out.println("Status History for Request #" + requestId);
        statusHistory.forEach(change -> System.out.println(change.getChangeSummary()));
    }

    @Override
    public boolean approve(String approver) {
        if (!isValid())
            return false;
        recordStatusChange(status.toString(), Status.APPROVED.toString(), approver);
        status = Status.APPROVED;
        employee.deductLeaveDays(getLeaveType(), getNumberOfDays());
        return true;
    }

    @Override
    public boolean deny(String approver, String reason) {
        recordStatusChange(status.toString(), Status.REJECTED.toString(), approver);
        status = Status.REJECTED;
        return true;
    }
}