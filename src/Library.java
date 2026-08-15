import java.util.ArrayList;

interface Borrowable{
    public void borrowbook(Book book);
    public void returnBook(Book book);
    default boolean isOverdue(int days){
        if(days>14) {
            return true;
        }
        return false;
    }
}
abstract class Book{
    private String title,author,isbn;
    private boolean isAvailable;
    public Book(String title, String author, String isbn, boolean isAvailable) {
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.isAvailable = isAvailable;
    }
    public String getTitle() {
        return title;
    }
    public String getAuthor() {
        return author;
    }
    public String getIsbn() {
        return isbn;
    }
    public boolean isAvailable() {
        return isAvailable;
    }
    public void setAvailable(boolean available) {
        isAvailable = available;
    }
    abstract public String getBookType();
}
class FictionBook extends Book{
    private String genre;
    public FictionBook(String title, String author, String isbn,boolean isAvailable, String genre) {
        super(title, author, isbn,isAvailable);
        this.genre = genre;
    }
    @Override
    public String getBookType() {
        return genre;
    }
}
class Non_FictionBook extends Book{
    private String subject;
    public Non_FictionBook(String title, String author, String isbn,boolean isAvailable,String subject) {
        super(title, author, isbn,isAvailable);
        this.subject = subject;
    }
    @Override
    public String getBookType() {
        return subject;
    }
}
class LibraryMember implements Borrowable{
    static int Tot_Mem = 0;
    private final int memberId;
    private String name;
    private ArrayList<Book> borrowedbooks;
    public LibraryMember(int memberId, String name) {
        this.memberId = memberId;
        this.name = name;
        borrowedbooks = new ArrayList<>();
        Tot_Mem++;
    }
    public void printBooks() {
        System.out.printf("Name: %s%nID: %d%nBrorrowed Books:%n",name,memberId);
        for(Book book:borrowedbooks) {
            System.out.println("Book Title:"+book.getTitle());
            System.out.println("Book Author:"+book.getAuthor());
            System.out.println("Book ID:"+book.getIsbn());
            System.out.println("Book Type:"+book.getBookType());
            System.out.println();
        }
    }
    @Override
    public void borrowbook(Book book) {
        if(book.isAvailable()){
            borrowedbooks.add(book);
            book.setAvailable(false);
        }
    }
    @Override
    public void returnBook(Book book) {
        if(!book.isAvailable()){
            borrowedbooks.remove(book);
            book.setAvailable(true);
        }
    }
    public static int getTotalMembers(){
        return Tot_Mem;
    }
}
public class Library {
    public static void main(String[] args) {
        FictionBook f1=new FictionBook("ps","kalki","b11",true,"fiction");
        FictionBook f2=new FictionBook("oddysy","non","b12",true,"fiction");
        Non_FictionBook n1=new Non_FictionBook("evolution_thorey","charles darvin","b21",true,"non-fiction");
        Non_FictionBook n2=new Non_FictionBook("wings_of_fire","abdhul kalam","b22",true,"non-fiction");
        Book[] books = {f1,f2,n1,n2};
        LibraryMember l1=new LibraryMember(1,"balu");
        LibraryMember l2=new LibraryMember(2,"json");
        l1.borrowbook(books[0]);
        l1.borrowbook(books[1]);
        l2.borrowbook(books[2]);
        LibraryMember[] l={l1,l2};
        for(LibraryMember m:l){
            m.printBooks();
        }
        System.out.println("\nTotal members: "+LibraryMember.getTotalMembers());
    }
}