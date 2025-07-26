import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.*;

public class MainDashboard extends JFrame {
    JTable table;
    DefaultTableModel model;
    JTextField nameField, amountField, dateField, noteField;
    JComboBox<String> categoryBox, paymentBox;
    JButton addBtn, deleteBtn, exportPdfBtn, exportExcelBtn;

    public MainDashboard() {
        setTitle("TrackTally_Manager");
        setSize(900, 600);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel top = new JPanel(new GridLayout(3, 4, 10, 10));
        nameField = new JTextField();
        amountField = new JTextField();
        dateField = new JTextField();

        ((PlainDocument) dateField.getDocument()).setDocumentFilter(new AutoDateFilter());

        categoryBox = new JComboBox<>(new String[]{"Food", "Transport", "Shopping", "Bills"});
        paymentBox = new JComboBox<>(new String[]{"Cash", "Card", "UPI"});
        noteField = new JTextField();

        top.add(new JLabel("Name")); top.add(nameField);
        top.add(new JLabel("Amount")); top.add(amountField);
        top.add(new JLabel("Category")); top.add(categoryBox);
        top.add(new JLabel("Payment Method")); top.add(paymentBox);
        top.add(new JLabel("Date (dd-MM-yyyy)")); top.add(dateField);
        top.add(new JLabel("Note")); top.add(noteField);

        model = new DefaultTableModel(new String[]{"ID","Name","Amount","Category","Payment","Date","Note","Created At"}, 0);
        table = new JTable(model);
        loadExpenses();

        JPanel ctrl = new JPanel();
        addBtn = new JButton("Add Expense");
        deleteBtn = new JButton("Delete Selected");
        exportPdfBtn = new JButton("Export PDF");
        exportExcelBtn = new JButton("Export Excel");
        ctrl.add(addBtn); ctrl.add(deleteBtn); ctrl.add(exportPdfBtn); ctrl.add(exportExcelBtn);

        add(top, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(ctrl, BorderLayout.SOUTH);

        addBtn.addActionListener(e -> { addExpense(); loadExpenses(); });
        deleteBtn.addActionListener(e -> { deleteExpense(); loadExpenses(); });
        exportPdfBtn.addActionListener(e -> ExportManager.exportToPDF());
        exportExcelBtn.addActionListener(e -> ExportManager.exportToExcel());

        setVisible(true);
    }

    private void loadExpenses() {
        model.setRowCount(0);
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM expenses")) {
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getDouble("amount"),
                    rs.getString("category"),
                    rs.getString("payment_method"),
                    rs.getDate("date").toString(),
                    rs.getString("note"),
                    rs.getTimestamp("created_at").toString()
                });
            }
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    private void addExpense() {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
               "INSERT INTO expenses(name,amount,category,payment_method,date,note) VALUES(?,?,?,?,?,?)")) {

            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            String inputDate = dateField.getText().trim();
            if (inputDate.isEmpty()) {
                JOptionPane.showMessageDialog(this, "❌ Date field is empty!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            try {
                java.util.Date parsed = sdf.parse(inputDate);
                ps.setString(1, nameField.getText());
                ps.setDouble(2, Double.parseDouble(amountField.getText()));
                ps.setString(3, categoryBox.getSelectedItem().toString());
                ps.setString(4, paymentBox.getSelectedItem().toString());
                ps.setDate(5, new java.sql.Date(parsed.getTime()));
                ps.setString(6, noteField.getText());
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "✅ Expense added!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "❌ Invalid date format! Use dd-MM-yyyy", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    private void deleteExpense() {
        int sel = table.getSelectedRow();
        if (sel >= 0) {
            int id = (int) model.getValueAt(sel, 0);
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement("DELETE FROM expenses WHERE id = ?")) {
                ps.setInt(1, id);
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "✅ Deleted!");
            } catch (Exception ex) { ex.printStackTrace(); }
        }
    }

    public static void main(String[] args) {
        new MainDashboard();
    }

    class AutoDateFilter extends DocumentFilter {
        public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
            if (string == null) return;
            replace(fb, offset, 0, string, attr);
        }

        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
            if (text == null) return;
            StringBuilder sb = new StringBuilder(fb.getDocument().getText(0, fb.getDocument().getLength()));
            sb.replace(offset, offset + length, text);
            String newText = sb.toString().replaceAll("[^0-9]", "");
            if (newText.length() > 8) return;

            StringBuilder formatted = new StringBuilder();
            for (int i = 0; i < newText.length(); i++) {
                formatted.append(newText.charAt(i));
                if (i == 1 || i == 3) formatted.append('-');
            }
            fb.replace(0, fb.getDocument().getLength(), formatted.toString(), attrs);
        }
    }
}
