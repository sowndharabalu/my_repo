package New2;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

abstract class Media{
    private final String id;
    private final String title;
    private final int year;
    public  Media(String id, String title, int year) {
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
    public abstract double getValue();
}

class Book extends Media{
    private final String author;
    private final int pages;
    public  Book(String id, String title, int year, String author, int pages) {
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
    public double getValue() {
        return pages*0.5;
    }
}

class DVD extends Media{
    private final int durationMinutes;
    public DVD(String id, String title, int year, int durationMinutes) {
        super(id, title, year);
        this.durationMinutes = durationMinutes;
    }
    @Override
    public double getValue() {
        return durationMinutes*0.2;
    }
}

class Library<T extends Media>{
    private final List<T> items=new ArrayList<>();
    public void addItem(T item) throws DuplicateItemException{
        boolean exists=items.stream().anyMatch(i->i.getId().equals(item.getId()));
        if(exists){
            throw new DuplicateItemException("Duplicate item: "+item.getId());
        }
        items.add(item);
    }
    public Optional<T> findById(String id){
        return items.stream().filter(i->i.getId().equals(id)).findFirst();
    }
    public List<T> getItemsByYear(){
        return items.stream().sorted(Comparator.comparingInt(Media::getYear).reversed()).toList();
    }
    public double getTotalValue(){
        return items.stream().mapToDouble(i->i.getValue()).sum();
    }
    public List<T> filter(Predicate<T> condition){
        return items.stream().filter(condition).toList();
    }
}

class DuplicateItemException extends Exception{
    public DuplicateItemException(String message){
        super(message);
    }
}

public class LMain {
    public static void main(String[] args) throws DuplicateItemException{
        Library<Media> library=new Library<>();

        Book b1=new Book("B1","Java Programming",2010,"Author A",500);
        Book b2=new Book("B2","Python Basics",2021,"Author B",300);
        DVD d1=new DVD("D1","Movie Night",2019,120);

        library.addItem(b1);
        library.addItem(b2);
        library.addItem(d1);

        try {
            library.addItem(new Book("B1","Duplicate",2022,"Author C",200));
        }catch (DuplicateItemException e){
            System.out.println("Caught: "+e.getMessage());
        }

        Optional<Media> found=library.findById("B1");
        found.ifPresentOrElse(
                media -> System.out.println("Found: "+ media.getTitle()),
                ()->System.out.println("Not found")
        );

        System.out.println("By year: "+library.getItemsByYear());

        System.out.println("Total value: "+library.getTotalValue());

        List<Media> booksOnly=library.filter(m->m instanceof Book);
        System.out.println("Books only: "+booksOnly);
    }
}
