import java.time.LocalDate;
import java.util.Optional;
import java.util.*;
import java.util.stream.Collectors;

class Customer{
    private final int id;
    private final String name;
    private final Optional<String> email;
    private final String tier;
    public Customer(int id, String name, String email,  String tier) {
        this.id = id;
        this.name = name;
        this.email = Optional.ofNullable(email);
        this.tier = tier;
    }
    public int getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public Optional<String> getEmail() {
        return email;
    }
    public String getTier() {
        return tier;
    }
}
class OrderItem{
    private String productName;
    private int quantity;
    private double unitPrice;
    public OrderItem(String productName, int quantity, double unitPrice) {
        this.productName = productName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }
    public String getProductName() {
        return productName;
    }
    public int getQuantity() {
        return quantity;
    }
    public double getUnitPrice() {
        return unitPrice;
    }
    public double getTotalPrice() {
        return unitPrice * quantity;
    }
}
class Order1{
    private String orderId;
    private int customerId;
    private List<OrderItem> items;
    private LocalDate orderDate;
    public Order1(String orderId, int customerId, List<OrderItem> items) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.items = items;
        this.orderDate = LocalDate.now();
    }
    public String getOrderId() {
        return orderId;
    }
    public int getCustomerId() {
        return customerId;
    }
    public List<OrderItem> getItems() {
        return items;
    }
    public LocalDate getOrderDate() { return orderDate; }
    public double getOrderTotal(){
        return items.stream().mapToDouble(OrderItem::getTotalPrice).sum();
    }
}
class OrderRepository{
    private List<Order1> orders;
    public OrderRepository(List<Order1> orders){
        this.orders = orders;
    }
    public Optional<Order1> findById(String orderId){
        return orders.stream().filter(o->o.getOrderId().equals(orderId)).findFirst();
    }
    public List<Order1> findByCustomerId(int customerId){
        return orders.stream().filter(o->o.getCustomerId()==customerId).toList();
    }
}
class CustomerRepository{
    private List<Customer> customers;
    public CustomerRepository(List<Customer> customers){
        this.customers = customers;
    }
    public Optional<Customer> findById(int id){
        return customers.stream().filter(c->c.getId()==id).findFirst();
    }
    public Map<String, List<Customer>> groupByTier(){
        return customers.stream().collect(Collectors.groupingBy(Customer::getTier));
    }
}
class AnalyticsEngine{
    public double getTotalRevenue(List<Order1> orders){
        return orders.stream().mapToDouble(Order1::getOrderTotal).sum();
    }
    public OptionalDouble getAverageOrderValue(List<Order1> orders){
        return orders.stream().mapToDouble(Order1::getOrderTotal).average();
    }
    public Map<Integer, Double> getTopCustomersByRevenue(List<Order1> orders, int n){
        return orders.stream().collect(Collectors.groupingBy(Order1::getCustomerId,Collectors.summingDouble(Order1::getOrderTotal))).entrySet().stream().sorted(Map.Entry.<Integer,Double>comparingByValue().reversed()).limit(n).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
    public Map<String, Integer> getProductSalesCount(List<Order1> orders){
        return orders.stream().flatMap(o->o.getItems().stream()).collect(Collectors.groupingBy(OrderItem::getProductName,Collectors.summingInt(OrderItem::getQuantity)));
    }
    public List<Order1> getOrdersAbove(List<Order1> orders, double threshold){
        return orders.stream().filter(o->o.getOrderTotal()>threshold).toList();
    }
    public Map<String, Double> getCustomerTierRevenue(List<Order1> orders, CustomerRepository repo){
        return orders.stream().collect(Collectors.groupingBy(o -> repo.findById(o.getCustomerId()).map(Customer::getTier).orElse("UNKNOWN"),Collectors.summingDouble(Order1::getOrderTotal)));
    }
    public Optional<String> getMosrPopularProduct(List<Order1> orders){
        return orders.stream().flatMap(o->o.getItems().stream()).collect(Collectors.groupingBy(OrderItem::getProductName,Collectors.summingInt(OrderItem::getQuantity))).entrySet().stream().max(Map.Entry.comparingByValue()).map(Map.Entry::getKey);
    }
}
public class AMain {
    public static void main(String[] args) {
        List<Customer> customers=Arrays.asList(new Customer(1,"Alice","alice@email.com","GOLD"),
                new Customer(2,"Bob",null,"Silver"),
                new Customer(3,"Charlie","charlie@email.com","Gold"),
                new Customer(4,"Diana","diana@email.com","PLATINUM"),
                new Customer(4,"Eve",null,"BRONZE")
        );

        List<Order1> orders=Arrays.asList(
                new Order1("ORD001", 1, Arrays.asList(
                        new OrderItem("Laptop", 1, 999.00), new OrderItem("Mouse", 2, 25.00))),
                new Order1("ORD002", 2, Arrays.asList(
                        new OrderItem("Phone", 1, 699.00), new OrderItem("Case", 3, 15.00))),
                new Order1("ORD003", 1, Arrays.asList(
                        new OrderItem("Monitor", 2, 299.00), new OrderItem("Keyboard", 1, 89.00))),
                new Order1("ORD004", 3, Arrays.asList(
                        new OrderItem("Laptop", 1, 999.00), new OrderItem("Mouse", 1, 25.00), new OrderItem("Pad", 5, 10.00))),
                new Order1("ORD005", 4, Arrays.asList(
                        new OrderItem("Phone", 2, 699.00), new OrderItem("Watch", 1, 199.00))),
                new Order1("ORD006", 1, Arrays.asList(
                        new OrderItem("Cable", 10, 5.00)))
        );;
    }
}
