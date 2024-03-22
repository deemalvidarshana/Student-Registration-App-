import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Pattern;

public class StudentRegistrationApp extends JFrame {
    private JTextField nameTextField, regNumberTextField, emailTextField, departmentTextField;
    private JButton addButton, updateButton, deleteButton, downloadButton;
    private JTable studentTable;
    private DefaultTableModel tableModel;

    interface Colors {
        Color BACKGROUND = Color.WHITE;
        Color BUTTON_BACKGROUND = Color.BLACK;
        Color BUTTON_FOREGROUND = Color.WHITE;
    }

    public StudentRegistrationApp() {
        setTitle("Student Registration");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 700);
        setLocationRelativeTo(null);

        // Load image
        ImageIcon icon = new ImageIcon("C:\\Users\\Dtwda\\Downloads\\Blue Yellow Elegant Professional University Logo.png");
        Image image = icon.getImage(); // transform it 
        Image newimg = image.getScaledInstance(200, 200,  java.awt.Image.SCALE_SMOOTH); // scale it the smooth way  
        icon = new ImageIcon(newimg);  // transform it back
        JLabel imageLabel = new JLabel(icon);
        

        JPanel inputPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        nameTextField = new JTextField(20);
        regNumberTextField = new JTextField(20);
        emailTextField = new JTextField(20);
        departmentTextField = new JTextField(20);

        inputPanel.add(new JLabel("    Name:"));
        inputPanel.add(nameTextField);
        inputPanel.add(new JLabel("    Registration Number:"));
        inputPanel.add(regNumberTextField);
        inputPanel.add(new JLabel("    Email ID:"));
        inputPanel.add(emailTextField);
        inputPanel.add(new JLabel("    Department:"));
        inputPanel.add(departmentTextField);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        addButton = createStyledButton("Add");
        updateButton = createStyledButton("Update");
        deleteButton = createStyledButton("Delete");
        downloadButton = createStyledButton("Download");

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(downloadButton);

        tableModel = new DefaultTableModel();
        tableModel.addColumn("Name");
        tableModel.addColumn("Registration Number");
        tableModel.addColumn("Email ID");
        tableModel.addColumn("Department");

        studentTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(studentTable);

        addButton.addActionListener(e -> addStudentRecord());
        updateButton.addActionListener(e -> updateStudentRecord());
        deleteButton.addActionListener(e -> deleteStudentRecord());
        downloadButton.addActionListener(e -> downloadStudentDetails());

        studentTable.getSelectionModel().addListSelectionListener(e -> {
            int selectedRow = studentTable.getSelectedRow();
            if (selectedRow != -1) {
                nameTextField.setText(studentTable.getValueAt(selectedRow, 0).toString());
                regNumberTextField.setText(studentTable.getValueAt(selectedRow, 1).toString());
                emailTextField.setText(studentTable.getValueAt(selectedRow, 2).toString());
                departmentTextField.setText(studentTable.getValueAt(selectedRow, 3).toString());
            }
        });

        getContentPane().setLayout(new BorderLayout(10, 10));

// Create a panel to hold inputPanel and scrollPane
    JPanel centerPanel = new JPanel(new BorderLayout());
    centerPanel.add(inputPanel, BorderLayout.NORTH); // Place inputPanel at NORTH
    centerPanel.add(scrollPane, BorderLayout.CENTER); // Place scrollPane at CENTER

    getContentPane().add(imageLabel, BorderLayout.NORTH); // Add image label to NORTH
    getContentPane().add(centerPanel, BorderLayout.CENTER); // Add centerPanel to CENTER
    getContentPane().add(buttonPanel, BorderLayout.SOUTH); // Add buttonPanel to SOUTH
    getContentPane().setBackground(Colors.BACKGROUND);

    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(Colors.BUTTON_BACKGROUND);
        button.setForeground(Colors.BUTTON_FOREGROUND);
        return button;
    }

    private void addStudentRecord() {
        String name = nameTextField.getText();
        String regNumber = regNumberTextField.getText();
        String email = emailTextField.getText();
        String department = departmentTextField.getText();

        if (name.isEmpty() || regNumber.isEmpty() || email.isEmpty() || department.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields must be entered.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!isValidEmail(email)) {
            JOptionPane.showMessageDialog(this, "Enter a valid email address.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Object[] rowData = {name, regNumber, email, department};
        tableModel.addRow(rowData);
        clearInputFields();
    }

    private void updateStudentRecord() {
        int selectedRow = studentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select a row to update.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String name = nameTextField.getText();
        String regNumber = regNumberTextField.getText();
        String email = emailTextField.getText();
        String department = departmentTextField.getText();

        if (name.isEmpty() || regNumber.isEmpty() || email.isEmpty() || department.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields must be entered.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!isValidEmail(email)) {
            JOptionPane.showMessageDialog(this, "Enter a valid email address.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        tableModel.setValueAt(name, selectedRow, 0);
        tableModel.setValueAt(regNumber, selectedRow, 1);
        tableModel.setValueAt(email, selectedRow, 2);
        tableModel.setValueAt(department, selectedRow, 3);
        clearInputFields();
    }

    private void deleteStudentRecord() {
        int selectedRow = studentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select a row to delete.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        tableModel.removeRow(selectedRow);
        clearInputFields();
    }

    private void downloadStudentDetails() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Specify a file to save");

        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            try {
                String filePath = fileChooser.getSelectedFile().getPath() + ".csv"; // Ensure file extension is CSV
                FileWriter writer = new FileWriter(filePath);

                // Write header
                for (int i = 0; i < tableModel.getColumnCount(); i++) {
                    writer.append(escapeCsv(tableModel.getColumnName(i)));
                    if (i < tableModel.getColumnCount() - 1)
                        writer.append(",");
                }
                writer.append("\n");

                // Write data
                for (int row = 0; row < tableModel.getRowCount(); row++) {
                    for (int col = 0; col < tableModel.getColumnCount(); col++) {
                        writer.append(escapeCsv(tableModel.getValueAt(row, col).toString()));
                        if (col < tableModel.getColumnCount() - 1)
                            writer.append(",");
                    }
                    writer.append("\n");
                }

                writer.close();
                JOptionPane.showMessageDialog(this, "Details downloaded successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error downloading details.", "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    // Method to escape CSV special characters
    private String escapeCsv(String value) {
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replaceAll("\"", "\"\"") + "\"";
        } else {
            return value;
        }
    }

    private boolean isValidEmail(String email) {
        String regex = "^[A-Za-z0-9+_.-]+@(.+)$";
        Pattern pattern = Pattern.compile(regex);
        return pattern.matcher(email).matches();
    }
    
    private void clearInputFields() {
        nameTextField.setText("");
        regNumberTextField.setText("");
        emailTextField.setText("");
        departmentTextField.setText("");
        studentTable.clearSelection();
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel"); // Use Nimbus look and feel
            } catch (Exception e) {
                e.printStackTrace();
            }
    
            StudentRegistrationApp app = new StudentRegistrationApp();
            app.setVisible(true);
        });
    }
    }
    