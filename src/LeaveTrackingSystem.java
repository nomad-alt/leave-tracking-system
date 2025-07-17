import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.*;

public class LeaveTrackingSystem {
    private final Map<Integer, Employee> employees = new HashMap<>();
    private final Map<Integer, LeaveRequest> leaveRequests = new HashMap<>();
    private final Queue<LeaveRequest> pendingApprovals = new LinkedList<>();
    private final Set<String> departments = new HashSet<>();
    private int nextRequestId = 1;

    private static final String DATA_DIR = "leavetracker_data";
    private static final String EMPLOYEES_FILE = DATA_DIR + "/employees.csv";
    private static final String REQUESTS_FILE = DATA_DIR + "/leave_requests.csv";
    private static final String BACKUP_DIR = DATA_DIR + "/backups";

    public LeaveTrackingSystem() {
        initializeFileStructure();
        loadEmployees();
        loadLeaveRequests();
    }

    private void initializeFileStructure() {
        try {
            Files.createDirectories(Paths.get(DATA_DIR));
            Files.createDirectories(Paths.get(BACKUP_DIR));
        } catch (IOException e) {
            System.err.println("Error creating directory structure: " + e.getMessage());
        }
    }

    // Employee file operations
    private void loadEmployees() {
        Path path = Paths.get(EMPLOYEES_FILE);
        if (!Files.exists(path))
            return;

        try (BufferedReader reader = Files.newBufferedReader(path)) {
            reader.lines().skip(1).forEach(line -> {
                String[] parts = line.split(",");
                if (parts.length == 5) {
                    int id = Integer.parseInt(parts[0]);
                    String name = parts[1];
                    String dept = parts[2];
                    int annualLeave = Integer.parseInt(parts[3]);
                    int sickLeave = Integer.parseInt(parts[4]);

                    Employee emp = new Employee(id, name, dept, annualLeave, sickLeave);
                    employees.put(id, emp);
                    departments.add(dept);
                }
            });
        } catch (IOException e) {
            System.err.println("Error loading employees: " + e.getMessage());
        }
    }

    public void saveEmployees() {
        Path path = Paths.get(EMPLOYEES_FILE);
        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            writer.write("ID,Name,Department,AnnualLeave,SickLeave\n");
            for (Employee emp : employees.values()) {
                writer.write(String.format("%d,%s,%s,%d,%d\n",
                        emp.getEmployeeId(),
                        emp.getName(),
                        emp.getDepartment(),
                        emp.getAnnualLeaveBalance(),
                        emp.getSickLeaveBalance()));
            }
        } catch (IOException e) {
            System.err.println("Error saving employees: " + e.getMessage());
        }
    }

    public Employee getEmployee(int employeeId) {
        return employees.get(employeeId);
    }

    // Leave Request file operations
    private void loadLeaveRequests() {
        Path path = Paths.get(REQUESTS_FILE);
        if (!Files.exists(path))
            return;

        try (BufferedReader reader = Files.newBufferedReader(path)) {
            reader.lines().skip(1).forEach(line -> {
                String[] parts = line.split(",", -1);
                if (parts.length >= 8) {
                    int reqId = Integer.parseInt(parts[0]);
                    int empId = Integer.parseInt(parts[1]);
                    LocalDate start = LocalDate.parse(parts[2]);
                    LocalDate end = LocalDate.parse(parts[3]);
                    String reason = parts[4];
                    LeaveRequest.Status status = LeaveRequest.Status.valueOf(parts[5]);
                    LeaveRequest.LeaveType type = LeaveRequest.LeaveType.valueOf(parts[6]);
                    boolean hasCert = Boolean.parseBoolean(parts[7]);

                    Employee emp = employees.get(empId);
                    if (emp != null) {
                        LeaveRequest request;
                        if (type == LeaveRequest.LeaveType.SICK) {
                            request = new SickLeaveRequest(reqId, emp, start, end, reason, hasCert);
                        } else if (type == LeaveRequest.LeaveType.ANNUAL) {
                            request = new AnnualLeaveRequest(reqId, emp, start, end, reason);
                        } else {
                            return;
                        }
                        request.status = status;
                        leaveRequests.put(reqId, request);
                        if (status == LeaveRequest.Status.PENDING) {
                            pendingApprovals.add(request);
                        }
                        nextRequestId = Math.max(nextRequestId, reqId + 1);
                    }
                }
            });
        } catch (IOException e) {
            System.err.println("Error loading leave requests: " + e.getMessage());
        }
    }

    public void saveLeaveRequests() {
        Path path = Paths.get(REQUESTS_FILE);
        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            writer.write("RequestID,EmployeeID,StartDate,EndDate,Reason,Status,Type,MedicalCertificate\n");
            for (LeaveRequest req : leaveRequests.values()) {
                writer.write(String.format("%d,%d,%s,%s,%s,%s,%s,%b\n",
                        req.getRequestId(),
                        req.getEmployee().getEmployeeId(),
                        req.getStartDate(),
                        req.getEndDate(),
                        req.getReason(),
                        req.getStatus(),
                        req.getLeaveType(),
                        (req instanceof SickLeaveRequest) ? ((SickLeaveRequest) req).hasMedicalCertificate() : false));
            }
        } catch (IOException e) {
            System.err.println("Error saving leave requests: " + e.getMessage());
        }
    }

    // Backup functionality
    public void createBackup() {
        LocalDate today = LocalDate.now();
        String backupName = String.format("backup_%s.zip", today);
        Path backupPath = Paths.get(BACKUP_DIR, backupName);

        try {
            Files.copy(Paths.get(EMPLOYEES_FILE), backupPath.resolveSibling("employees_" + today + ".csv"));
            Files.copy(Paths.get(REQUESTS_FILE), backupPath.resolveSibling("requests_" + today + ".csv"));
            System.out.println("Backup created successfully");
        } catch (IOException e) {
            System.err.println("Error creating backup: " + e.getMessage());
        }
    }

    // Existing methods with save calls added
    public void addEmployee(Employee employee) {
        employees.put(employee.getEmployeeId(), employee);
        departments.add(employee.getDepartment());
        saveEmployees();
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
        saveLeaveRequests();
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
            saveLeaveRequests();
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

    public void saveToBinaryFile(String filename) {
        Path path = Paths.get(DATA_DIR, filename);
        try (ObjectOutputStream oos = new ObjectOutputStream(
                Files.newOutputStream(path))) {

            oos.writeObject(new ArrayList<>(employees.values()));
            oos.writeObject(new ArrayList<>(leaveRequests.values()));
            oos.writeInt(nextRequestId);
            System.out.println("Data saved to binary file successfully");
        } catch (IOException e) {
            System.err.println("Error saving binary data: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public void loadFromBinaryFile(String filename) {
        if (filename == null || filename.trim().isEmpty()) {
            System.err.println("Error: Filename cannot be null or empty");
            return;
        }

        Path path = Paths.get(DATA_DIR, filename);
        if (!Files.exists(path)) {
            System.err.println("Error: File " + path + " does not exist");
            return;
        }

        try (ObjectInputStream ois = new ObjectInputStream(
                Files.newInputStream(path))) {

            // Read data from file
            Object empData = ois.readObject();
            Object reqData = ois.readObject();
            Object idData = ois.readObject();

            // Validate loaded data types
            if (!(empData instanceof List) || !(reqData instanceof List) || !(idData instanceof Integer)) {
                System.err.println("Error: Invalid data format in binary file");
                return;
            }

            List<Employee> empList = (List<Employee>) empData;
            List<LeaveRequest> reqList = (List<LeaveRequest>) reqData;
            nextRequestId = (Integer) idData;

            // Clear current data
            employees.clear();
            leaveRequests.clear();
            pendingApprovals.clear();
            departments.clear();

            // Load employees with validation
            for (Employee emp : empList) {
                if (emp != null) {
                    employees.put(emp.getEmployeeId(), emp);
                    if (emp.getDepartment() != null) {
                        departments.add(emp.getDepartment());
                    }
                }
            }

            // Load leave requests with validation
            for (LeaveRequest req : reqList) {
                if (req != null && employees.containsKey(req.getEmployee().getEmployeeId())) {
                    leaveRequests.put(req.getRequestId(), req);
                    if (req.getStatus() == LeaveRequest.Status.PENDING) {
                        pendingApprovals.add(req);
                    }
                    nextRequestId = Math.max(nextRequestId, req.getRequestId() + 1);
                }
            }

            System.out.println("Data loaded successfully from " + path);
            System.out.println("Loaded " + employees.size() + " employees and " +
                    leaveRequests.size() + " leave requests");

        } catch (InvalidClassException e) {
            System.err.println("Error: Incompatible class versions - " + e.getMessage());
        } catch (StreamCorruptedException e) {
            System.err.println("Error: Corrupted binary file - " + e.getMessage());
        } catch (IOException e) {
            System.err.println("IO Error loading binary data: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            System.err.println("Error: Class not found - " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error loading data: " + e.getMessage());
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

        // Create backup
        system.createBackup();

        // Save to binary file
        system.saveToBinaryFile("system_data.dat");

        // Show employee history
        System.out.println("\nJohn's Leave History:");
        system.getEmployeeLeaveHistory(101).forEach(LeaveRequest::displayDetails);
    }
}