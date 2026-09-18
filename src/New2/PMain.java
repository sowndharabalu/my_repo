package New2;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

enum Category1 {
    ELECTRONICS,CLOTHING,BOOKS,FOOD;
}
enum OrderStatus1 {
    PENDING,CONFIRMED,CANCELED,SHIPPED,DELIVERED;
}
record Product1(String id,String name,double price,Category1 category,int stock) {}
record OrderItem1(Product1 product,int quantity) {
    public double totalPrice() {return product.price()*quantity;}
    public boolean isInStock() {return product.stock()>=quantity;}
}

class OutOfStockException extends Exception {
    public OutOfStockException(String message) {
        super(message);
    }
}

class Order1 {
    private final String orderId;
    private final List<OrderItem1> items;
    private OrderStatus1 status;
    private final LocalDateTime createdAt;
    public Order1(String orderId) {
        this.orderId = orderId;
        this.items = new ArrayList<>();
        this.status = OrderStatus1.PENDING;
        this.createdAt = LocalDateTime.now();
    }

    public void addItem(OrderItem1 item) throws OutOfStockException,IllegalStateException {
        if(item.isInStock()){
            throw new OutOfStockException("Cannot add item to stock");
        }
        if (status!=OrderStatus1.PENDING){
            throw new IllegalStateException("Cannot add item to order");
        }
        items.add(item);
    }
    public boolean confirm(){
        if(status==OrderStatus1.PENDING){
            status = OrderStatus1.CONFIRMED;
            return true;
        }
        return false;
    }
    public boolean ship(){
        if(status==OrderStatus1.CONFIRMED){
            status = OrderStatus1.SHIPPED;
            return true;
        }
        return false;
    }
    public boolean deliver(){
        if(status==OrderStatus1.SHIPPED){
            status = OrderStatus1.DELIVERED;
            return true;
        }
        return false;
    }
    public boolean cancel(){
        if(status==OrderStatus1.PENDING|| status==OrderStatus1.CONFIRMED){
            status = OrderStatus1.CANCELED;
            return true;
        }
        return false;
    }
    public String getId(){
        return orderId;
    }
    public List<OrderItem1> getItems() {
        return Collections.unmodifiableList(items);
    }
    public OrderStatus1 getStatus(){
        return status;
    }
    public LocalDateTime getCreatedAt(){
        return createdAt;
    }
    public double getTotalAmount(){
        return items.stream().map(OrderItem1::totalPrice).reduce(0.0, Double::sum);
    }
    public List<Product1> getProductsByCategory(Category1 category){
        return items.stream().map(OrderItem1::product).filter(p->category.equals(p.category())).collect(Collectors.toList());
    }
}

class OrderManager{
    static Optional<Order1> findMostExpensiveOrder(List<Order1> orders){
        return orders.stream().max(Comparator.comparing(Order1::getTotalAmount));
    }
    static double getTotalRevenue(List<Order1> orders){
        return orders.stream().filter(order->order.getStatus() == OrderStatus1.SHIPPED || order.getStatus()==OrderStatus1.CONFIRMED).mapToDouble(Order1::getTotalAmount).sum();
    }
    static List<Order1> sortByDate(List<Order1> orders){
        return orders.stream().sorted(Comparator.comparing(Order1::getCreatedAt)).collect(Collectors.toList());
    }
    static Map<OrderStatus1,List<Order1>> groupByStatus(List<Order1> orders){
        return orders.stream().collect(Collectors.groupingBy(Order1::getStatus));
    }
    static double getAverageOrderValue(List<Order1> orders){
        return orders.stream().mapToDouble(Order1::getTotalAmount).average().getAsDouble();
    }
    static List<Order1> findOrdersWithProduct(List<Order1> orders,String productId){
        return orders.stream().filter(o->o.getId().equals(productId)).collect(Collectors.toList());
    }
    static List<Order1> filterOrders(List<Order1> orders, Predicate<Order1> condition){
        return orders.stream().filter(condition).collect(Collectors.toList());
    }
}

public class PMain {
    public static void main(String[] args) throws OutOfStockException {
        Product1 laptop = new Product1("P1", "Laptop", 50000, Category1.ELECTRONICS, 10);
        Product1 mouse = new Product1("P2", "Mouse", 500, Category1.ELECTRONICS, 50);
        Product1 shirt = new Product1("P3", "Shirt", 1500, Category1.CLOTHING, 0);

        Order1 order1 = new Order1("O1");
        order1.addItem(new OrderItem1(laptop, 1));
        order1.addItem(new OrderItem1(mouse, 2));
        order1.confirm();
        order1.ship();

        Order1 order2 = new Order1("O2");
        try {
            order2.addItem(new OrderItem1(shirt, 1));
        } catch (OutOfStockException e) {
            System.out.println("Caught: " + e.getMessage());
        }
        order2.addItem(new OrderItem1(mouse, 1));
        order2.confirm();

        Order1 order3 = new Order1("O3");
        order3.addItem(new OrderItem1(laptop, 2));

        List<Order1> orders = List.of(order1,order2,order3);

        System.out.println("Most expensive: " + OrderManager.findMostExpensiveOrder(orders));
        System.out.println("Total revenue: " + OrderManager.getTotalRevenue(orders));
        System.out.println("Sorted by date: " + OrderManager.sortByDate(orders));
        System.out.println("By status: " + OrderManager.groupByStatus(orders));
        System.out.println("Average value: " + OrderManager.getAverageOrderValue(orders));
        System.out.println("Orders with P1: " + OrderManager.findOrdersWithProduct(orders, "P1"));
        System.out.println("Filtered: " + OrderManager.filterOrders(orders, o -> o.getStatus() == OrderStatus1.CONFIRMED));
    }
}
