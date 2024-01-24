import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

class Transaction implements Serializable {
    private double amount;
    private String description;
    private String date;

    public Transaction(double amount, String description, String date) {
        this.amount = amount;
        this.description = description;
        this.date = date;
    }

    public double getAmount() {
        return amount;
    }

    @Override
    public String toString() {
        return "Amount: " + amount + ", Description: " + description + ", Date: " + date;
    }
}

class FinanceManager {
    private List<Transaction> transactions;

    public FinanceManager() {
        this.transactions = new ArrayList<>();
    }

    public void saveTransactionsToFile() {
        try (FileOutputStream fileOut = new FileOutputStream("transactions.dat");
             ObjectOutputStream objectOut = new ObjectOutputStream(fileOut)) {
            objectOut.writeObject(transactions);
            System.out.println("Transactions saved to file.");
        } catch (IOException e) {
            System.err.println("Error saving transactions: " + e.getMessage());
        }
    }

    public void loadTransactionsFromFile() {
        try (FileInputStream fileIn = new FileInputStream("transactions.dat");
             ObjectInputStream objectIn = new ObjectInputStream(fileIn)) {
            List<Transaction> loadedTransactions = (List<Transaction>) objectIn.readObject();
            transactions.addAll(loadedTransactions);
            System.out.println("Transactions loaded from file.");
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading transactions: " + e.getMessage());
        }
    }

    public void addTransaction(Transaction transaction) {
        transactions.add(transaction);
    }

    public double calculateBalance() {
        double balance = 0;
        for (Transaction transaction : transactions) {
            balance += transaction.getAmount();
        }
        return balance;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }
}

public class PersonalFinanceManager {
    private static volatile boolean stopSaveThread = false;

    public static void main(String[] args) {
        FinanceManager manager = new FinanceManager();
        Scanner scanner = new Scanner(System.in);

        Thread saveThread = new Thread(() -> {
            while (!stopSaveThread) {
                manager.saveTransactionsToFile();
                try {
                    Thread.sleep(60000); // Save data every minute (adjust as needed)
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        saveThread.start();

        manager.loadTransactionsFromFile();

        while (true) {
            System.out.println("Personal Finance Manager");
            System.out.println("1. Add Expense");
            System.out.println("2. Add Income");
            System.out.println("3. View Transactions");
            System.out.println("4. Calculate Balance");
            System.out.println("5. Exit");
            System.out.print("Enter your choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    System.out.print("Enter Expense Amount: ");
                    double expenseAmount = scanner.nextDouble();
                    scanner.nextLine(); // Consume newline
                    System.out.print("Enter Expense Description: ");
                    String expenseDescription = scanner.nextLine();
                    System.out.print("Enter Expense Date (YYYY-MM-DD): ");
                    String expenseDate = scanner.nextLine();

                    Transaction expense = new Transaction(-expenseAmount, expenseDescription, expenseDate);
                    manager.addTransaction(expense);
                    System.out.println("Expense added successfully.");
                    break;

                case 2:
                    System.out.print("Enter Income Amount: ");
                    double incomeAmount = scanner.nextDouble();
                    scanner.nextLine(); // Consume newline
                    System.out.print("Enter Income Description: ");
                    String incomeDescription = scanner.nextLine();
                    System.out.print("Enter Income Date (YYYY-MM-DD): ");
                    String incomeDate = scanner.nextLine();

                    Transaction income = new Transaction(incomeAmount, incomeDescription, incomeDate);
                    manager.addTransaction(income);
                    System.out.println("Income added successfully.");
                    break;

                case 3:
                    List<Transaction> transactions = manager.getTransactions();
                    if (transactions.isEmpty()) {
                        System.out.println("No transactions recorded yet.");
                    } else {
                        System.out.println("Transactions:");
                        for (Transaction transaction : transactions) {
                            System.out.println(transaction);
                        }
                    }
                    break;

                case 4:
                    double balance = manager.calculateBalance();
                    System.out.println("Current Balance: " + balance);
                    break;

                case 5:
                    System.out.println("Exiting the program.");
                    stopSaveThread = true;
                    try {
                        saveThread.join(); // Wait for the save thread to finish
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    scanner.close();
                    return;

                default:
                    System.out.println("Invalid choice. Please enter a valid option.");
            }
        }
    }
}