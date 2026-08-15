import java.io.*;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        Map<String, BankAccount> map = new HashMap<>();
        map.put("ACC001", new BankAccount("ACC001", 5000.0));
        map.put("ACC002", new BankAccount("ACC002", 2500.0));
        map.put("ACC003", new BankAccount("ACC003", 100.0));
        try (FileWriter fw = new FileWriter("transactions.txt");
             BufferedWriter bf = new BufferedWriter(fw)) {
            bf.write("ACC001,2000.0,DEPOSIT"); bf.newLine();
            bf.write("ACC002,3000.0,WITHDRAWAL"); bf.newLine();
            bf.write("ACC003,5000.0,WITHDRAWAL"); bf.newLine();
            bf.write("ACC001,-100.0,DEPOSIT"); bf.newLine();
            bf.write("BAD_LINE_NO_COMMA"); bf.newLine();
            bf.write("ACC002,500.0,UNKNOWN_TYPE"); bf.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        TransactionProcessor tp = new TransactionProcessor(map);
        try {
            tp.processFile("transactions.txt");
        } catch (InvalidTransactionException e) {
            e.printStackTrace();
        }
    }
}

class InsufficientFundsException extends Exception {
    private final double deficit;
    public InsufficientFundsException(String message, double deficit) {
        super(message);
        this.deficit = deficit;
    }
    public double getDeficit() {
        return deficit;
    }
}

class InvalidTransactionException extends RuntimeException {
    private final String transactionLine;
    public InvalidTransactionException(String message) {
        super(message);
        this.transactionLine = null;
    }
    public InvalidTransactionException(String message, Throwable cause) {
        super(message, cause);
        this.transactionLine = null;
    }
    public InvalidTransactionException(String message, String transactionLine) {
        super(message);
        this.transactionLine = transactionLine;
    }
    public String getTransactionLine() {
        return transactionLine;
    }
}

class BankAccount {
    private final String accountId;
    private double balance;
    public BankAccount(String accountId, double balance) {
        this.accountId = accountId;
        this.balance = balance;
    }
    public void deposit(double amount) throws InvalidTransactionException {
        if (amount <= 0) {
            throw new InvalidTransactionException("Invalid amount: " + amount);
        }
        balance += amount;
    }
    public void withdraw(double amount) throws InvalidTransactionException, InsufficientFundsException {
        if (amount <= 0) {
            throw new InvalidTransactionException("Invalid amount: " + amount);
        }
        if (amount > balance) {
            double deficit = amount - balance;
            throw new InsufficientFundsException("Deficit: " + deficit, deficit);
        }
        balance -= amount;
    }
    public double getBalance() {
        return balance;
    }
}

class TransactionProcessor {
    private final Map<String, BankAccount> map;
    public TransactionProcessor(Map<String, BankAccount> map) {
        this.map = map;
    }
    public void processFile(String path) {
        try (BufferedReader br = new BufferedReader(new FileReader(path));
             TransactionLogger logger = new TransactionLogger("log.txt")) {
            String line;
            while ((line = br.readLine()) != null) {
                try {
                    String[] lineArray = line.split(",");
                    if (lineArray.length != 3) {
                        throw new InvalidTransactionException("Invalid format", line);
                    }
                    String accountId = lineArray[0];
                    double amount = Double.parseDouble(lineArray[1]);
                    String type = lineArray[2];
                    if (amount <= 0) {
                        throw new InvalidTransactionException("Invalid amount: " + amount, line);
                    }
                    if (!map.containsKey(accountId)) {
                        throw new InvalidTransactionException("Account not found: " + accountId, line);
                    }
                    BankAccount account = map.get(accountId);
                    if (type.equals("DEPOSIT")) {
                        account.deposit(amount);
                    } else if (type.equals("WITHDRAWAL")) {
                        account.withdraw(amount);
                    } else {
                        throw new InvalidTransactionException("Unknown type: " + type, line);
                    }
                    String success = String.format(
                            "Processed: [%s] - [%s] of %.2f | Balance: [%.2f]",
                            accountId, type, amount, account.getBalance()
                    );
                    System.out.println(success);
                    logger.log(success);
                } catch (InvalidTransactionException e) {
                    System.out.println("[ERROR] [InvalidTransactionException]: [" + e.getMessage() + "]");
                    if (e.getTransactionLine() != null) {
                        System.out.println("  Raw line: " + e.getTransactionLine());
                    }
                } catch (InsufficientFundsException e) {
                    System.out.println("[ERROR] [InsufficientFundsException]: [" + e.getMessage() + "]");
                } catch (Exception e) {
                    System.out.println("[ERROR] [Unexpected Exception]: [" + e.getMessage() + "]");
                }
            }
        } catch (IOException e) {
            throw new InvalidTransactionException("File read failed", e);
        }
    }
}

class TransactionLogger implements AutoCloseable {
    private final BufferedWriter bw;
    public TransactionLogger(String logFile) throws IOException {
        bw = new BufferedWriter(new FileWriter(logFile));
    }
    public void log(String message) throws IOException {
        bw.write(message);
        bw.newLine();
        bw.flush();
    }
    @Override
    public void close() throws IOException {
        if (bw != null) {
            bw.close();
        }
        System.out.println("Logger closed");
    }
}