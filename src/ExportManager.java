
import java.sql.*;
import java.io.*;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;

public class ExportManager {

    public static void exportToPDF() {
        String sql = "SELECT * FROM expenses";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream("Expenses_Report.pdf"));
            document.open();

            document.add(new Paragraph("Expense Report"));
            document.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(7);
            table.addCell("Name");
            table.addCell("Amount");
            table.addCell("Category");
            table.addCell("Payment Method");
            table.addCell("Date");
            table.addCell("Note");
            table.addCell("Created At");

            while (rs.next()) {
                table.addCell(rs.getString("name"));
                table.addCell(String.valueOf(rs.getDouble("amount")));
                table.addCell(rs.getString("category"));
                table.addCell(rs.getString("payment_method"));
                table.addCell(rs.getDate("date").toString());
                table.addCell(rs.getString("note"));
                table.addCell(rs.getTimestamp("created_at").toString());
            }

            document.add(table);
            document.close();
            System.out.println("✅ PDF exported: Expenses_Report.pdf");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void exportToExcel() {
        String sql = "SELECT * FROM expenses";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            XSSFWorkbook workbook = new XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet("Expenses");

            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("Name");
            header.createCell(1).setCellValue("Amount");
            header.createCell(2).setCellValue("Category");
            header.createCell(3).setCellValue("Payment Method");
            header.createCell(4).setCellValue("Date");
            header.createCell(5).setCellValue("Note");
            header.createCell(6).setCellValue("Created At");

            int rowIndex = 1;
            while (rs.next()) {
                Row row = sheet.createRow(rowIndex++);
                row.createCell(0).setCellValue(rs.getString("name"));
                row.createCell(1).setCellValue(rs.getDouble("amount"));
                row.createCell(2).setCellValue(rs.getString("category"));
                row.createCell(3).setCellValue(rs.getString("payment_method"));
                row.createCell(4).setCellValue(rs.getDate("date").toString());
                row.createCell(5).setCellValue(rs.getString("note"));
                row.createCell(6).setCellValue(rs.getTimestamp("created_at").toString());
            }

            FileOutputStream out = new FileOutputStream("Expenses_Report.xlsx");
            workbook.write(out);
            out.close();
            workbook.close();

            System.out.println("✅ Excel exported: Expenses_Report.xlsx");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}