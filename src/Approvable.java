public interface Approvable {
    boolean approve(String approver);

    boolean deny(String approver, String reason);
}