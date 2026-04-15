package learn;
import java.util.Scanner;
import java.util.ArrayList;
import java.io.FileWriter;
import java.io.File;
class Book{
    private String title;
    private String author;
    private boolean isAvailable;
    public Book(String new_title,String new_author,boolean available){
        title=new_title;
        author=new_author;
        isAvailable=available;
    }
    public void borrowBook(){
        isAvailable=false;
    }
    public void returnBook(){
        isAvailable=true;
    }
    public String getDeatails(){
        return title+" by "+author+"(Available: "+isAvailable+")";
    }
    public String getTitle(){
        return title;
    }
    public String getauthor(){
        return author;
    }
    public boolean checkAvailability(){
        return isAvailable;
    }
}
class Library{
    private ArrayList<Book> inventory;
    public Library(){
            inventory=new ArrayList<Book>();
            loadData();
            }
    public void addBooks(Book newBook){
        inventory.add(newBook);
    }
    public void displayBooks(){
        for(Book b:inventory){
            System.out.println(b.getDeatails());
        }
    }
    public void checkOutBook(String searchTitle){
        for(Book b:inventory){
            if(b.getTitle().equals(searchTitle)){
                if(b.checkAvailability()==true){
                b.borrowBook();
                System.out.println("You succesfully borrowed "+searchTitle);
                return;
            }else{
                    System.out.println("Sorry, that book is already checked out.");
                }
            }
        }System.out.println("Sorry,we don't have that book in our library.");
    }
    public boolean storeBook(String bookName){
        for(Book b:inventory){
            if(b.getTitle().equals(bookName)){
                b.returnBook();
                return true;
            }
        }
        return false;
    }
    public void saveData(){
        try{
        FileWriter writer=new FileWriter("library.txt");
        for(Book b:inventory){
            String data=b.getTitle()+","+b.getauthor()+","+b.checkAvailability();
            writer.write(data+"\n");
        }
        writer.close();
        System.out.println("Library data is saved successfully!");
        }catch(Exception e){
            System.out.println("An Error occurred while saving data:"+e);
        }
    }
    public void loadData(){
        try {
            File f = new File("library.txt");
            if (!f.exists()) {
                return; 
            }
            Scanner sc = new Scanner(f);
            while (sc.hasNextLine()) {
                String line = sc.nextLine(); 
                String[] parts = line.split(",");        
                String savedTitle = parts[0];
                String savedAuthor = parts[1];
                boolean savedAvailablility;
                if(parts[2].equals("true")){
                    savedAvailablility=true;
                }else{
                    savedAvailablility=false;
                }
                Book b=new Book(savedTitle,savedAuthor,savedAvailablility);
                inventory.add(b);
            }
            sc.close();
        } catch (Exception e) {
            System.out.println("Error loading data: " + e);
        }
    }
}
public class Main{
    public static void main(String[] args){
        Library lib=new Library();
        Scanner s=new Scanner(System.in);
        boolean isTrue=true;
        while(isTrue){
        System.out.println("--Welcome to the Library--");
        System.out.println("1.View all Books:");
        System.out.println("2.Borrow a Book:");
        System.out.println("3.Return a Book:");
        System.out.println("4.Exit:");
        int choice=s.nextInt();
        s.nextLine();
        switch(choice){
            case 1:
               lib.displayBooks();
               break;
            case 2:
               System.out.println("Enter Book name:");
               String bookName=s.nextLine();
               lib.checkOutBook(bookName);
               break;
            case 3:
               System.out.println("Enter Book Name:");
               String returnBook=s.nextLine();
               boolean con=lib.storeBook(returnBook);
               if (con==true){
                   System.out.println("Thank you, your book was returned");
               }else{
                   System.out.println("sorry, this book does not belong to our library.");
               }
               break;
            case 4:
               System.out.println("Thank you for visisting!");
               isTrue=false;
               lib.saveData();
               break;
            default:
                   System.out.println("sorry, invalid choice!:");
                   break;
        }
        s.close();
        }
    }
}