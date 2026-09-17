package New2;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

enum TransactionType{
    DEPOSIT,WITHDRAWAL,TRANSFER_IN,TRANSFER_OUT;
}
record Transaction(String id, TransactionType type, double amount, LocalDateTime timeStamp){}

class BankAccount{
    private final String accountNumber;
    private final String holderName;
    private double balance;
    private final List<Transaction> transactions=new ArrayList<>();
    public BankAccount(String accountNumber, String holderName){
        this.accountNumber=accountNumber;
        this.holderName=holderName;
        this.balance=0;
    }
    public void deposit(double amount) throws IllegalArgumentException{
        if(amount<0){
            throw new IllegalArgumentException("Amount cannot be negative");
        }
        balance+=amount;
        transactions.add(new Transaction(accountNumber, TransactionType.DEPOSIT, amount, LocalDateTime.now()));
    }
    public void withdraw(double amount) throws IllegalArgumentException,InsufficientFundsException{
        if(amount<=0){
            throw new IllegalArgumentException("Amount cannot be negative");
        }
        if(amount>balance){
            throw new InsufficientFundsException("Insufficient funds");
        }
        balance-=amount;
        transactions.add(new Transaction(accountNumber, TransactionType.WITHDRAWAL, amount, LocalDateTime.now()));
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
    public String toString(){
        return "BankAccount{id='" + accountNumber + "', holder='" + holderName + "', balance=" + balance + "}";
    }
}

class InsufficientFundsException extends Exception{
    public InsufficientFundsException(String message){
        super(message);
    }
}

class Bank{
    static Optional<BankAccount> findRichestAccount(List<BankAccount> accounts){
        return accounts.stream().max(Comparator.comparingDouble(BankAccount::getBalance));
    }
    static double getTotalBalance(List<BankAccount> accounts){
        return accounts.stream().mapToDouble(BankAccount::getBalance).sum();
    }
    static List<Transaction> getAllTransactionsSorted(List<BankAccount> accounts){
        return accounts.stream().flatMap(a->a.getTransactions().stream()).sorted(Comparator.comparing(Transaction::timeStamp).reversed()).toList();
    }
    static Map<TransactionType,Double> sumByTransactionType(List<BankAccount> accounts){
        return accounts.stream().flatMap(a->a.getTransactions().stream()).collect(Collectors.groupingBy(Transaction::type,Collectors.summingDouble(Transaction::amount)));
    }
    static List<BankAccount> findAccounts(List<BankAccount> accounts, Predicate<BankAccount> filter){
        return accounts.stream().filter(filter).collect(Collectors.toList());
    }
}

public class BMain {
    public static void main(String[] args) throws InsufficientFundsException {
        BankAccount acc1 = new BankAccount("A001", "Alice");
        BankAccount acc2 = new BankAccount("A002", "Bob");
        BankAccount acc3 = new BankAccount("A003", "Charlie");

        acc1.deposit(5000);
        acc1.withdraw(2000);

        acc2.deposit(10000);
        acc2.withdraw(3000);

        acc3.deposit(1500);

        List<BankAccount> accounts = List.of(acc1, acc2, acc3);
        Bank.findRichestAccount(accounts).ifPresentOrElse(System.out::println, ()->{System.out.println("Account not found");});
        System.out.println(Bank.getTotalBalance(accounts));
        Bank.getAllTransactionsSorted(accounts).forEach(System.out::println);
        System.out.println(Bank.sumByTransactionType(accounts));
        Bank.findAccounts(accounts,b->b.getBalance()>4000).forEach(System.out::println);
    }
}
