# TrackTally - Manager 💸

**TrackTally** is a desktop-based expense tracking application built using Java Swing and MySQL.  
It allows users to add, view, delete, and export their daily expenses in both PDF and Excel formats.

## 🔧 Features

- Add daily expenses with Name, Amount, Category, Payment Method, Date & Note
- Real-time storage in MySQL database via JDBC
- Auto-formatted date input (dd-MM-yyyy)
- View all records in a styled JTable
- Export reports as:
  - 📄 PDF (iText Library)
  - 📊 Excel (Apache POI)
- Clean and user-friendly UI using Java Swing

## 💻 Tech Stack

- Java (Swing for GUI)
- MySQL (Database)
- JDBC (Connectivity)
- iText 5.5.13.2 (PDF Export)
- Apache POI (Excel Export)

## 🛠️ How to Run

1. Clone the repo  
2. Create the `expenses` table in MySQL using the schema provided  
3. Make sure `lib/` folder contains all `.jar` dependencies:
   - mysql-connector-java
   - itextpdf
   - poi, poi-ooxml, xmlbeans, etc.
4. Compile and run using VS Code or command line:

```bash
javac -cp "lib/*" -d bin src/*.java
java -cp "lib/*:bin" MainDashboard


## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.