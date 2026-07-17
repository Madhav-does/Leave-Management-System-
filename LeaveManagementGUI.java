import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;+

public class LeaveManagementGUI extends JFrame {
    static ArrayList<LeaveType> types = new ArrayList<>();
    static ArrayList<LeaveRequest> requests = new ArrayList<>();
    
    JTabbedPane tabbedPane;
    JTable leaveTypeTable, leaveRequestTable;
    DefaultTableModel typeTableModel, requestTableModel;
    
    private static final String TYPE_FILE = "D:\\LeaveManagement\\leavetypes.txt";
    private static final String REQUEST_FILE = "D:\\LeaveManagement\\leaverequests.txt";
    
    public LeaveManagementGUI() {
        createDataFolder();
        loadLeaveTypes();
        loadLeaveRequests();
        
        if (types.isEmpty()) {
            types.add(new LeaveType("Casual Leave", 12));
            types.add(new LeaveType("Sick Leave", 10));
            types.add(new LeaveType("Earned Leave", 15));
            saveLeaveTypes();
        }
        
        setTitle("Employee Leave Management System");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        tabbedPane = new JTabbedPane();
        
        tabbedPane.addTab("Leave Types", createLeaveTypePanel());
        tabbedPane.addTab("Apply Leave", createApplyLeavePanel());
        tabbedPane.addTab("My Leaves", createMyLeavesPanel());
        tabbedPane.addTab("All Leaves", createAllLeavesPanel());
        
        add(tabbedPane);
    }
    
    private void createDataFolder() {
        File folder = new File("D:\\LeaveManagement");
        if (!folder.exists()) {
            folder.mkdir();
        }
    }
    
    private void saveLeaveTypes() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(TYPE_FILE))) {
            for (LeaveType t : types) {
                writer.println(t.name + "," + t.maxDays);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error saving leave types: " + e.getMessage());
        }
    }
    
    private void loadLeaveTypes() {
        File file = new File(TYPE_FILE);
        if (!file.exists()) return;
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    types.add(new LeaveType(parts[0], Integer.parseInt(parts[1])));
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error loading leave types: " + e.getMessage());
        }
    }
    
    private void saveLeaveRequests() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(REQUEST_FILE))) {
            for (LeaveRequest r : requests) {
                writer.println(r.empId + "," + r.type + "," + r.reason + "," + r.status);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error saving leave requests: " + e.getMessage());
        }
    }
    
    private void loadLeaveRequests() {
        File file = new File(REQUEST_FILE);
        if (!file.exists()) return;
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 4) {
                    LeaveRequest r = new LeaveRequest(Integer.parseInt(parts[0]), parts[1], parts[2]);
                    r.status = parts[3];
                    requests.add(r);
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error loading leave requests: " + e.getMessage());
        }
    }
    
    private JPanel createLeaveTypePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        String[] columns = {"Leave Type", "Max Days"};
        typeTableModel = new DefaultTableModel(columns, 0);
        leaveTypeTable = new JTable(typeTableModel);
        refreshLeaveTypeTable();
        
        JScrollPane scrollPane = new JScrollPane(leaveTypeTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        JPanel formPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        JTextField nameField = new JTextField();
        JTextField daysField = new JTextField();
        JButton addButton = new JButton("Add Leave Type");
        
        formPanel.add(new JLabel("Leave Type Name:"));
        formPanel.add(nameField);
        formPanel.add(new JLabel("Max Days:"));
        formPanel.add(daysField);
        
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(formPanel, BorderLayout.CENTER);
        bottomPanel.add(addButton, BorderLayout.SOUTH);
        panel.add(bottomPanel, BorderLayout.SOUTH);
        
        addButton.addActionListener(e -> {
            String name = nameField.getText().trim();
            String daysText = daysField.getText().trim();
            
            if (name.isEmpty() || daysText.isEmpty()) {
                JOptionPane.showMessageDialog(panel, "Please fill all fields!");
                return;
            }
            
            try {
                int days = Integer.parseInt(daysText);
                types.add(new LeaveType(name, days));
                refreshLeaveTypeTable();
                saveLeaveTypes();
                refreshTypeCombo();
                nameField.setText("");
                daysField.setText("");
                JOptionPane.showMessageDialog(panel, "Leave Type Added & Saved to D:\\LeaveManagement\\");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(panel, "Max Days must be a number!");
            }
        });
        
        return panel;
    }
    
    private void refreshTypeCombo() {
        for (int i = 0; i < tabbedPane.getTabCount(); i++) {
            Component comp = tabbedPane.getComponentAt(i);
            if (comp instanceof JPanel) {
            }
        }
    }
    
    private JPanel createApplyLeavePanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        
        JTextField empIdField = new JTextField(15);
        JComboBox<String> typeCombo = new JComboBox<>();
        JTextField startDateField = new JTextField(15);
        JTextField endDateField = new JTextField(15);
        JTextArea reasonArea = new JTextArea(3, 15);
        JButton submitButton = new JButton("Submit Leave Application");
        
        startDateField.setText("YYYY-MM-DD");
        endDateField.setText("YYYY-MM-DD");
        
        for (LeaveType t : types) {
            typeCombo.addItem(t.name);
        }
        
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Employee ID:"), gbc);
        gbc.gridx = 1;
        panel.add(empIdField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Leave Type:"), gbc);
        gbc.gridx = 1;
        panel.add(typeCombo, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Start Date:"), gbc);
        gbc.gridx = 1;
        panel.add(startDateField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("End Date:"), gbc);
        gbc.gridx = 1;
        panel.add(endDateField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(new JLabel("Reason:"), gbc);
        gbc.gridx = 1;
        panel.add(new JScrollPane(reasonArea), gbc);
        
        gbc.gridx = 0; gbc.gridy = 5;
        gbc.gridwidth = 2;
        panel.add(submitButton, gbc);
        
        submitButton.addActionListener(e -> {
            String empIdText = empIdField.getText().trim();
            String leaveType = (String) typeCombo.getSelectedItem();
            String startDate = startDateField.getText().trim();
            String endDate = endDateField.getText().trim();
            String reason = reasonArea.getText().trim();
            
            if (empIdText.isEmpty() || startDate.isEmpty() || endDate.isEmpty() || reason.isEmpty()) {
                JOptionPane.showMessageDialog(panel, "Please fill all fields!");
                return;
            }
            
            try {
                int empId = Integer.parseInt(empIdText);
                String fullReason = "[" + startDate + " to " + endDate + "] " + reason;
                requests.add(new LeaveRequest(empId, leaveType, fullReason));
                saveLeaveRequests();
                empIdField.setText("");
                startDateField.setText("YYYY-MM-DD");
                endDateField.setText("YYYY-MM-DD");
                reasonArea.setText("");
                JOptionPane.showMessageDialog(panel, "Leave Applied Successfully!\nSaved to D:\\LeaveManagement\\\nStatus: Pending");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(panel, "Employee ID must be a number!");
            }
        });
        
        return panel;
    }
    
    private JPanel createMyLeavesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        JPanel searchPanel = new JPanel(new FlowLayout());
        JTextField searchField = new JTextField(10);
        JButton searchButton = new JButton("Search");
        searchPanel.add(new JLabel("Enter Employee ID:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        
        String[] columns = {"Leave Type", "Reason", "Status"};
        requestTableModel = new DefaultTableModel(columns, 0);
        leaveRequestTable = new JTable(requestTableModel);
        JScrollPane scrollPane = new JScrollPane(leaveRequestTable);
        
        panel.add(searchPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        searchButton.addActionListener(e -> {
            String text = searchField.getText().trim();
            if (text.isEmpty()) {
                JOptionPane.showMessageDialog(panel, "Enter Employee ID!");
                return;
            }
            
            try {
                int empId = Integer.parseInt(text);
                requestTableModel.setRowCount(0);
                boolean found = false;
                
                for (LeaveRequest r : requests) {
                    if (r.empId == empId) {
                        requestTableModel.addRow(new Object[]{r.type, r.reason, r.status});
                        found = true;
                    }
                }
                
                if (!found) {
                    JOptionPane.showMessageDialog(panel, "No leave applications found for ID: " + empId);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(panel, "Enter valid Employee ID!");
            }
        });
        
        return panel;
    }
    
    private JPanel createAllLeavesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        String[] columns = {"Employee ID", "Leave Type", "Reason", "Status"};
        DefaultTableModel allRequestModel = new DefaultTableModel(columns, 0);
        JTable allRequestTable = new JTable(allRequestModel);
        JScrollPane scrollPane = new JScrollPane(allRequestTable);
        
        JButton refreshButton = new JButton("Refresh");
        
        refreshButton.addActionListener(e -> {
            allRequestModel.setRowCount(0);
            for (LeaveRequest r : requests) {
                allRequestModel.addRow(new Object[]{r.empId, r.type, r.reason, r.status});
            }
            if (requests.isEmpty()) {
                JOptionPane.showMessageDialog(panel, "No leave applications yet.");
            }
        });
        
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(refreshButton, BorderLayout.SOUTH);
        
        return panel;
    }
    
    void refreshLeaveTypeTable() {
        typeTableModel.setRowCount(0);
        for (LeaveType t : types) {
            typeTableModel.addRow(new Object[]{t.name, t.maxDays});
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new LeaveManagementGUI().setVisible(true);
        });
    }
}

class LeaveType {
    String name;
    int maxDays;
    
    LeaveType(String name, int maxDays) {
        this.name = name;
        this.maxDays = maxDays;
    }
}

class LeaveRequest {
    int empId;
    String type;
    String reason;
    String status;
    
    LeaveRequest(int empId, String type, String reason) {
        this.empId = empId;
        this.type = type;
        this.reason = reason;
        this.status = "Pending";
    }
}