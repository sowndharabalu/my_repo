package New3;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

record Transaction(String id, TransactionType type, double amount, LocalDateTime timeStamp, String category){}
enum TransactionType{
    CREDIT,
    DEBIT;
}

class InsufficientFundsException extends Exception{
    InsufficientFundsException(String message){
        super(message);
    }
}

class Account{
    private final String accountNumber;
    private final String holderName;
    private double balance;
    private final List<Transaction> transactions=new ArrayList<>();
    public Account(String accountNumber, String holderName){
        this.accountNumber=accountNumber;
        this.holderName=holderName;
        this.balance=0;
    }

    public void deposit(double amount, String category){
        if(amount<=0){
            throw new IllegalArgumentException("Amount must be positive");
        }
        this.balance+=amount;
        transactions.add(new Transaction(accountNumber, TransactionType.DEBIT, amount, LocalDateTime.now(), category));
    }
    public void withdraw(double amount, String category) throws InsufficientFundsException{
        if(amount<=0){
        throw new IllegalArgumentException("Amount must be positive");
        }
        if(amount>balance){
            throw new InsufficientFundsException("Insufficient funds");
        }
        this.balance-=amount;
        transactions.add(new Transaction(accountNumber, TransactionType.CREDIT, amount, LocalDateTime.now(), category));
    }
    public double getBalance(){
        return balance;
    }
    public List<Transaction> getTransactions(){
        return Collections.unmodifiableList(transactions);
    }
    public String getAccountNumber(){
        return accountNumber;
    }
    public String getHolderName(){
        return holderName;
    }
}

class TransactionAnalyzer{
    static double getTotalCredits(List<Account> accounts ){
        return accounts.stream().flatMap(a->a.getTransactions().stream()).filter(t->t.type()==TransactionType.CREDIT).mapToDouble(Transaction::amount).sum();
    }
    static double getTotalDebits(List<Account> accounts){
        return accounts.stream().flatMap(a->a.getTransactions().stream()).filter(t->t.type()==TransactionType.DEBIT).mapToDouble(Transaction::amount).sum();
    }
    static List<Transaction> getLargeTransactions(List<Account> accounts,double threshold){
        return accounts.stream().flatMap(a->a.getTransactions().stream()).filter(t->t.amount()>threshold).sorted(Comparator.comparingDouble(Transaction::amount)).toList();
    }
    static Map<String,Double> sumByCategory(List<Account> accounts){
        return accounts.stream().flatMap(a->a.getTransactions().stream()).collect(Collectors.groupingBy(t->t.category(),Collectors.summingDouble(Transaction::amount)));
    }
    static Map<Month,Double> getMonthlySpending(List<Account> accounts){
        return accounts.stream().flatMap(a->a.getTransactions().stream()).collect(Collectors.groupingBy(t->t.timeStamp().getMonth(),Collectors.summingDouble(Transaction::amount)));
    }
    static Optional<Account> findMostActiveAccount(List<Account> accounts){
        return accounts.stream().max(Comparator.comparingInt(a->a.getTransactions().size()));
    }
    static double getAverageTransactionAmount(Account account){
        return account.getTransactions().stream().mapToDouble(Transaction::amount).average().orElse(0.0);
    }
    static List<Transaction> filterTransactions(List<Account> accounts, Predicate<Transaction> filter){
        return transactions.stream().filter(filter).collect(Collectors.toList());
    }
}

public class Main2 {
    public static void main(String[] args) throws InsufficientFundsException{
        Account acc1 = new Account("A001", "Alice");
        Account acc2 = new Account("A002", "Bob");

        acc1.deposit(5000, "Salary");
        acc1.withdraw(2000, "Rent");
        acc1.withdraw(500, "Food");

        acc2.deposit(3000, "Salary");
        acc2.withdraw(1000, "Shopping");
        acc2.withdraw(300, "Food");

        List<Account> accounts = List.of(acc1, acc2);

        System.out.println("Total credits: " + TransactionAnalyzer.getTotalCredits(accounts));
        System.out.println("Total debits: " + TransactionAnalyzer.getTotalDebits(accounts));
        System.out.println("Large transactions: " + TransactionAnalyzer.getLargeTransactions(accounts, 1000));
        System.out.println("By category: " + TransactionAnalyzer.sumByCategory(accounts));
        System.out.println("Monthly spending: " + TransactionAnalyzer.getMonthlySpending(accounts));
        System.out.println("Most active: " + TransactionAnalyzer.findMostActiveAccount(accounts));
        System.out.println("Avg transaction (Alice): " + TransactionAnalyzer.getAverageTransactionAmount(acc1));
        System.out.println("Filtered: " + TransactionAnalyzer.filterTransactions(accounts, t -> t.amount() > 500));
    }
}
