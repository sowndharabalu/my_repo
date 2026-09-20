package New4;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;

class OutOfStockException extends Exception {
    public OutOfStockException(String message) { super(message); }
}
class DuplicateItemException extends Exception {
    public DuplicateItemException(String message) { super(message); }
}

class ProductNotFoundException extends Exception {
    public ProductNotFoundException(String message) { super(message); }
}

abstract class Product {
    private final String id;
    private final String name;
    private final double price;
    private int stock;

    public  Product(String id, String name, double price, int stock) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.stock = stock;
    }
    public String getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public double getPrice() {
        return price;
    }
    public int getStock() {
        return stock;
    }
    abstract double calculateDiscount();
    public void reduceStock(int quantity) throws OutOfStockException {
        if (stock < quantity) {
            throw new OutOfStockException("out of stock for quantity , avaiable: " + stock);
        }
        stock -= quantity;
    }
    public void reStock(int quantity){
        stock += quantity;
    }
    @Override
    public String toString() {
        return getClass().getSimpleName() + "{id='" + id + "', name='" + name + "', price=" + price + ", stock=" + stock + "}";
    }
}

class Electronics extends Product {
    public Electronics(String id, String name, double price, int stock) {
        super(id, name, price, stock);
    }
    @Override
    double calculateDiscount() {
        return getPrice() * 0.1;
    }
}

class Clothing extends Product {
    public Clothing(String id, String name, double price, int stock) {
        super(id, name, price, stock);
    }
    @Override
    double calculateDiscount() {
        return getPrice() * 0.2;
    }
}

class Grocery extends Product {
    private final LocalDate expiryDate;
    public Grocery(String id, String name, double price, int stock, LocalDate expiryDate) {
        super(id, name, price, stock);
        this.expiryDate = expiryDate;
    }
    @Override
    double calculateDiscount() {
        if (expiryDate.isBefore(LocalDate.now())) {
            return getPrice() * 0.5;
        }
        return 0;
    }
}

class Inventory<T extends Product> {
    private final Map<String, T> items = new HashMap<>();
    public void addProduct(T product) throws DuplicateItemException{
        Optional.ofNullable(product).filter(p->p!=null && !items.containsKey(p.getId())).orElseThrow(() -> new DuplicateItemException("Product is null or already exists"));
        items.put(product.getId(), product);
    }
    public Optional<T> getProduct(String id){
        return Optional.ofNullable(items.get(id));
    }
    public double sellProduct(String id, int quantity) throws ProductNotFoundException, OutOfStockException{
        T product=getProduct(id).orElseThrow(() -> new ProductNotFoundException("Product ID does not match: "+id));
        product.reduceStock(quantity);
        return (product.getPrice()-product.calculateDiscount())*quantity;
    }
    public List<T> getLowStockProducts(int threshold){
        return items.values().stream().filter(t->t.getStock()<threshold).toList();
    }
    public double getTotalValue(){
        return items.values().stream().mapToDouble(t->t.getStock()*t.getPrice()).sum();
    }
    public List<T> getProductsByDiscount(){
        return items.values().stream().sorted(Comparator.comparingDouble(T::calculateDiscount).reversed()).toList();
    }
    public <R> List<R> applyToAll(Function<T, R> function){
        return items.values().stream().map(item->function.apply(item)).toList();
    }
    public <R extends T> List<R> getByType(Class<R> type){
        return items.values().stream().filter(type::isInstance).map(type::cast).toList();
    }
    public void restock(String id, int quantity) throws ProductNotFoundException{
        Optional.ofNullable(id).filter(i->items.containsKey(id)).orElseThrow(() -> new ProductNotFoundException("Product ID does not match: "+id));
        items.get(id).reStock(quantity);
    }
}

public class Main2 {
    public static void main(String[] args) throws OutOfStockException, DuplicateItemException, ProductNotFoundException {
        Inventory<Product> inventory = new Inventory<>();

        inventory.addProduct(new Electronics("E1", "Laptop", 50000, 10));
        inventory.addProduct(new Electronics("E2", "Phone", 30000, 5));
        inventory.addProduct(new Clothing("C1", "Shirt", 1500, 20));
        inventory.addProduct(new Grocery("G1", "Milk", 50, 30, LocalDate.now().minusDays(1)));  // expired

        inventory.getProduct("E1").ifPresent(p->System.out.println(p.getName()));
        System.out.println("Cost :"+inventory.sellProduct("E2",3));
        System.out.println("Low stock products: "+inventory.getLowStockProducts(5));
        System.out.println("Total value : "+inventory.getTotalValue());
        System.out.println("Product with discount: "+inventory.getProductsByDiscount());
        System.out.println(inventory.applyToAll(Product::calculateDiscount));
        System.out.println(inventory.getByType(Electronics.class));
        inventory.restock("E2",5);
    }
}
