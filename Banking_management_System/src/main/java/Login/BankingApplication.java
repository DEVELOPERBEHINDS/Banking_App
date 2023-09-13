package Login;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class BankingApplication {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/Banking";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "1234";

    public static void main(String[] args) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            createAccountTable(connection);

            Scanner scanner = new Scanner(System.in);
            while (true) {
                System.out.println("Banking Application Menu:");
                System.out.println("1. Create Account");
                System.out.println("2. Deposit");
                System.out.println("3. Withdraw");
                System.out.println("4. Check Balance");
                System.out.println("5. Exit");
                System.out.print("Enter your choice: ");
                int choice = scanner.nextInt();

                switch (choice) {
                    case 1:
                        createAccount(connection, scanner);
                        break;
                    case 2:
                        deposit(connection, scanner);
                        break;
                    case 3:
                        withdraw(connection, scanner);
                        break;
                    case 4:
                        checkBalance(connection, scanner);
                        break;
                    case 5:
                        System.out.println("Exiting the application.");
                        return;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void createAccountTable(Connection connection) throws SQLException {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS accounts (" +
                "account_number VARCHAR(20) PRIMARY KEY," +
                "balance DECIMAL(10, 2) DEFAULT 0" +
                ")";
        try (PreparedStatement preparedStatement = connection.prepareStatement(createTableSQL)) {
            preparedStatement.execute();
        }
    }

    private static void createAccount(Connection connection, Scanner scanner) throws SQLException {
        System.out.print("Enter account number: ");
        String accountNumber = scanner.next();

        // Check if the account already exists
        if (accountExists(connection, accountNumber)) {
            System.out.println("Account already exists.");
            return;
        }

        String insertSQL = "INSERT INTO accounts (account_number) VALUES (?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(insertSQL)) {
            preparedStatement.setString(1, accountNumber);
            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Account created successfully.");
            } else {
                System.out.println("Account creation failed.");
            }
        }
    }

    private static boolean accountExists(Connection connection, String accountNumber) throws SQLException {
        String query = "SELECT account_number FROM accounts WHERE account_number = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, accountNumber);
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        }
    }

    private static void deposit(Connection connection, Scanner scanner) throws SQLException {
        System.out.print("Enter account number: ");
        String accountNumber = scanner.next();
        System.out.print("Enter deposit amount: ");
        double depositAmount = scanner.nextDouble();

        if (!accountExists(connection, accountNumber)) {
            System.out.println("Account does not exist.");
            return;
        }

        String updateSQL = "UPDATE accounts SET balance = balance + ? WHERE account_number = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(updateSQL)) {
            preparedStatement.setDouble(1, depositAmount);
            preparedStatement.setString(2, accountNumber);
            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Deposit successful.");
            } else {
                System.out.println("Deposit failed.");
            }
        }
    }

    private static void withdraw(Connection connection, Scanner scanner) throws SQLException {
        System.out.print("Enter account number: ");
        String accountNumber = scanner.next();
        System.out.print("Enter withdrawal amount: ");
        double withdrawalAmount = scanner.nextDouble();

        if (!accountExists(connection, accountNumber)) {
            System.out.println("Account does not exist.");
            return;
        }

        String updateSQL = "UPDATE accounts SET balance = balance - ? WHERE account_number = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(updateSQL)) {
            preparedStatement.setDouble(1, withdrawalAmount);
            preparedStatement.setString(2, accountNumber);
            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Withdrawal successful.");
            } else {
                System.out.println("Withdrawal failed. Insufficient balance.");
            }
        }
    }

    private static void checkBalance(Connection connection, Scanner scanner) throws SQLException {
        System.out.print("Enter account number: ");
        String accountNumber = scanner.next();

        String query = "SELECT balance FROM accounts WHERE account_number = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, accountNumber);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                double balance = resultSet.getDouble("balance");
                System.out.println("Account balance: $" + balance);
            } else {
                System.out.println("Account not found.");
            }
        }
    }
}
