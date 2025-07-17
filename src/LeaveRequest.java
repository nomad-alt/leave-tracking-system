import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public abstract class LeaveRequest implements Approvable, Serializable {
    private static final long serialVersionUID = 1L;
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

    public Status getStatus() {
        return status;
    }

    public int getRequestId() {
        return requestId;
    }

    public Employee getEmployee() {
        return employee;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public String getReason() {
        return reason;
    }

    public void displayDetails() {
        System.out.printf("""
                Request ID: %d
                Employee: %s (ID: %d)
                Leave Type: %s
                Dates: %s to %s (%d days)
                Status: %s
                Reason: %s
                """, requestId, employee.getName(), employee.getEmployeeId(),
                getLeaveType(), startDate, endDate, getNumberOfDays(), status, reason);
    }

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
        if (!isValid()) {
            return false;
        }
        recordStatusChange(status.toString(), Status.APPROVED.toString(), approver);
        status = Status.APPROVED;
        employee.deductLeaveDays(getLeaveType(), getNumberOfDays());
        employee.addLeaveRequest(this);
        return true;
    }

    @Override
    public boolean deny(String approver, String reason) {
        recordStatusChange(status.toString(), Status.REJECTED.toString(), approver);
        status = Status.REJECTED;
        employee.addLeaveRequest(this);
        return true;
    }
}