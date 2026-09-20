package New3;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

abstract class LibraryItem {
    private final String id;
    private final String title;
    private final int year;
    public LibraryItem(String id, String title, int year) {
        this.id = id;
        this.title = title;
        this.year = year;
    }
    public String getId() {
        return id;
    }
    public String getTitle() {
        return title;
    }
    public int getYear() {
        return year;
    }
    abstract double getValue();
}

class Book extends LibraryItem {
    private final String author;
    private final int pages;
    public Book(String id, String title, int year, String author, int pages) {
        super(id, title, year);
        this.author = author;
        this.pages = pages;
    }
    public String getAuthor() {
        return author;
    }
    public int getPages() {
        return pages;
    }
    @Override
    double getValue() {
        return pages*0.5;
    }
}

class DVD extends LibraryItem {
    private final int durationMinutes;
    public DVD(String id, String title, int year, int durationMinutes) {
        super(id, title, year);
        this.durationMinutes = durationMinutes;
    }
    public int getDurationMinutes() {
        return durationMinutes;
    }
    @Override
    double getValue() {
        return durationMinutes*0.2;
    }
}

class Magazine extends LibraryItem {
    private final int issueNumber;
    public Magazine(String id, String title, int year, int issueNumber) {
        super(id, title, year);
        this.issueNumber = issueNumber;
    }
    public int getIssueNumber() {
        return issueNumber;
    }
    @Override
    double getValue() {
        return issueNumber*2;
    }
}

class DuplicateItemException extends Exception {
    public DuplicateItemException(String message) { super(message); }
}

class Library<T extends LibraryItem> {
    private final List<T> items = new ArrayList<>();

    public void addItem(T item) throws DuplicateItemException{
        Optional.of(item).filter(i->items.stream().noneMatch(exist->exist.getId().equals(i.getId()))).orElseThrow(()->new DuplicateItemException("Duplicate item: "+item.getId()));
        items.add(item);
    }
    public Optional<T> findById(String id){
        return items.stream().filter(i->i.getId().equals(id)).findFirst();
    }
    public List<T> getItemsByYear(){
        return items.stream().sorted(Comparator.comparingInt(LibraryItem::getYear).reversed()).toList();
    }
    public double getTotalValue() {
        return items.stream().mapToDouble(LibraryItem::getValue).sum();
    }
    public List<T> filter(Predicate<T> condition){
        return items.stream().filter(condition).toList();
    }
    public <R extends T> List<R> getItemsByType(Class<R> type){
        return items.stream().filter(type::isInstance).map(type::cast).toList();
    }
    public Map<Integer, List<T>> groupByYear(){
        return items.stream().collect(Collectors.groupingBy(LibraryItem::getYear));
    }
}

public class Main3 {
    public static void main(String[] args) throws DuplicateItemException{
        Library<LibraryItem> library = new Library<>();

        library.addItem(new Book("B1", "Java Programming", 2020, "Author A", 500));
        library.addItem(new Book("B2", "Python Basics", 2021, "Author B", 300));
        library.addItem(new DVD("D1", "Movie Night", 2019, 120));
        library.addItem(new Magazine("M1", "Tech Weekly", 2022, 50));

        try {
            library.addItem(new Book("B1", "Duplicate", 2022, "Author C", 200));
        } catch (DuplicateItemException e) {
            System.out.println("Caught: " + e.getMessage());
        }

        System.out.println("Find B1: " + library.findById("B1"));
        System.out.println("By year: " + library.getItemsByYear());
        System.out.println("Total value: " + library.getTotalValue());
        System.out.println("Filter books: " + library.filter(i -> i instanceof Book));
        System.out.println("Books only: " + library.getItemsByType(Book.class));
        System.out.println("By year: " + library.groupByYear());
    }
}
