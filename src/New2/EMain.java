package New2;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

enum Category{
    ELECTRONICS,CLOTHING,BOOKS,FOOD
}

record Product(String id, String name, double price, Category category) {}
record OrderItem(Product product,int quantity) {}

class Order{
    enum OrderStatus{
        PENDING,CONFIRMED,DELIVERED,CANCELED,SHIPPED;
    }
    private final String OrderId;
    private final List<OrderItem> items=new ArrayList<>();
    private OrderStatus status;
    private LocalDateTime createdAt;

    Order(String orderId){
        this.OrderId=orderId;
        this.createdAt=LocalDateTime.now();
        this.status=OrderStatus.PENDING;
    }
    public List<OrderItem> getItems(){
        return Collections.unmodifiableList(this.items);
    }
    public void  addItem(OrderItem item) {
        if (this.status == OrderStatus.PENDING) {
        this.items.add(item);
        }
    }
    public OrderStatus getStatus(){
        return this.status;
    }
    public LocalDateTime getCreatedAt(){
        return this.createdAt;
    }
    public boolean confirm(){
        this.status=OrderStatus.CONFIRMED;
        return this.status.equals(OrderStatus.CONFIRMED);
    }
    public double totalAmount(){
        return items.stream().mapToDouble(i->i.product().price()*i.quantity()).sum();
    }
    public List<Product> getProductsByCategory(Category category){
        return items.stream().map(OrderItem::product).filter(p->p.category().equals(category)).toList();
    }
}

class OrderProcessor{
    static double calculateRevenue(List<Order> orders, Predicate<Order> filter){
        return orders.stream().filter(filter).mapToDouble(Order::totalAmount).sum();
    }
    static Function<Double,Double> priceCalculator(){
        Function<Double,Double> tax=price->(price*1.18);
        Function<Double,Double> discount=price->{
            if(price > 1000) {
                return price - (price / 10);
            }
            return price;
        };
        return tax.andThen(discount);
    }
    static Optional<Product> findMostExpensiveProduct(List<Order> orders) throws EmptyOrderException{
        if(orders==null || orders.isEmpty()){
            throw new EmptyOrderException("Empty order");
        }
        return orders.stream().flatMap(order -> order.getItems().stream()).map(OrderItem::product).max(Comparator.comparingDouble(Product::price));
    }
    static Map<Category,Double> averageOrderValueByCategory(List<Order> orders){
        return orders.stream().flatMap(order -> order.getItems().stream()).collect(Collectors.groupingBy(item->item.product().category(),TreeMap::new,Collectors.averagingDouble(item->item.product().price())));
    }
}
class EmptyOrderException extends Exception{
    public EmptyOrderException(String message){
        super(message);
    }
}
public class EMain {
    public static void main(String[] args) {
        Product laptop = new Product("P1", "Laptop", 50000, Category.ELECTRONICS);
        Product mouse = new Product("P2", "Mouse", 500, Category.ELECTRONICS);
        Product shirt = new Product("P3", "Shirt", 1500, Category.CLOTHING);
        Product book = new Product("P4", "Java Book", 800, Category.BOOKS);

        Order order1 = new Order("O1");
        order1.addItem(new OrderItem(laptop, 1));
        order1.addItem(new OrderItem(mouse, 2));
        order1.confirm();

        Order order2 = new Order("O2");
        order2.addItem(new OrderItem(shirt, 3));
        order2.confirm();

        Order order3 = new Order("O3");
        order3.addItem(new OrderItem(book, 5));

        List<Order> orders = List.of(order1, order2, order3);

        double revenue = OrderProcessor.calculateRevenue(orders, o -> o.getStatus() == Order.OrderStatus.CONFIRMED);
        System.out.println("Revenue (confirmed): " + revenue);

        Function<Double, Double> calculator = OrderProcessor.priceCalculator();
        System.out.println("Price with tax & discount: " + calculator.apply(50000.0));  // 50000*1.18=59000, >1000 so *0.9=53100

        try {
            Optional<Product> expensive = OrderProcessor.findMostExpensiveProduct(orders);
            expensive.ifPresentOrElse(
                    p -> System.out.println("Most expensive: " + p.name()),
                    () -> System.out.println("No products found")
            );
        } catch (EmptyOrderException e) {
            System.out.println("Error: " + e.getMessage());
        }

        System.out.println("Avg by category: " + OrderProcessor.averageOrderValueByCategory(orders));
    }
}
