import java.time.LocalDate;
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
    private final String productName;
    private final int quantity;
    private final double unitPrice;
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
    private final String orderId;
    private final int customerId;
    private final List<OrderItem> items;
    private final LocalDate orderDate;
    public Order1(String orderId, int customerId, List<OrderItem> items, LocalDate orderDate) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.items = List.copyOf(items);
        this.orderDate = orderDate;
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
    private final List<Order1> orders;
    public OrderRepository(List<Order1> orders){
        this.orders = List.copyOf(orders);
    }
    public Optional<Order1> findById(String orderId){
        return orders.stream().filter(o->o.getOrderId().equals(orderId)).findFirst();
    }
    public List<Order1> findByCustomerId(int customerId){
        return orders.stream().filter(o->o.getCustomerId()==customerId).toList();
    }
}
class CustomerRepository{
    private final List<Customer> customers;
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
        return orders.stream().collect(Collectors.groupingBy(Order1::getCustomerId,Collectors.summingDouble(Order1::getOrderTotal))).entrySet().stream().sorted(Map.Entry.<Integer,Double>comparingByValue().reversed()).limit(n).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,(a,b)->a,LinkedHashMap::new));
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
    public Optional<String> getMostPopularProduct(List<Order1> orders){
        return orders.stream().flatMap(o->o.getItems().stream()).collect(Collectors.groupingBy(OrderItem::getProductName,Collectors.summingInt(OrderItem::getQuantity))).entrySet().stream().max(Map.Entry.comparingByValue()).map(Map.Entry::getKey);
    }
}
public class AMain {
    public static void main(String[] args) {
        List<Customer> customers=List.of(new Customer(1,"Alice","alice@email.com","GOLD"),
                new Customer(2,"Bob",null,"SILVER"),
                new Customer(3,"Charlie","charlie@email.com","Gold"),
                new Customer(4,"Diana","diana@email.com","PLATINUM"),
                new Customer(5,"Eve",null,"BRONZE")
        );

        List<Order1> orders=List.of(
                new Order1("ORD001", 1, List.of(
                        new OrderItem("Laptop", 1, 999.00), new OrderItem("Mouse", 2, 25.00)),LocalDate.of(2024,1,15)),
                new Order1("ORD002", 2, List.of(
                        new OrderItem("Phone", 1, 699.00), new OrderItem("Case", 3, 15.00)),LocalDate.of(2024,1,16)),
                new Order1("ORD003", 1, List.of(
                        new OrderItem("Monitor", 2, 299.00), new OrderItem("Keyboard", 1, 89.00)),LocalDate.of(2024,1,17)),
                new Order1("ORD004", 3, List.of(
                        new OrderItem("Laptop", 1, 999.00), new OrderItem("Mouse", 1, 25.00), new OrderItem("Pad", 5, 10.00)),LocalDate.of(2024,1,18)),
                new Order1("ORD005", 4, List.of(
                        new OrderItem("Phone", 2, 699.00), new OrderItem("Watch", 1, 199.00)),LocalDate.of(2024,1,19)),
                new Order1("ORD006", 1, List.of(
                        new OrderItem("Cable", 10, 5.00)),LocalDate.of(2024,1,20))
        );

        CustomerRepository cr=new  CustomerRepository(customers);
        OrderRepository or=new  OrderRepository(orders);

        AnalyticsEngine engine=new AnalyticsEngine();

        System.out.printf("Total Revenue: $%.2f%n%n",engine.getTotalRevenue(orders));

        engine.getAverageOrderValue(orders).ifPresentOrElse(avg->System.out.printf("Average: $%.2f%n%n",avg),()->System.out.println("Average: $0.0\n"));

        System.out.println("Top 2 customers");
        engine.getTopCustomersByRevenue(orders,2).forEach((id,rev)->System.out.printf("Customer ID: %d, Revenue: %.2f%n",id,rev));
        System.out.println();

        System.out.printf("Sales count:");
        engine.getProductSalesCount(orders).entrySet().stream().sorted(Map.Entry.<String,Integer>comparingByValue().reversed()).forEach(e->System.out.printf("%s -> %d%n",e.getKey(),e.getValue()));
        System.out.println();

        System.out.println("Orders > $1000");
        engine.getOrdersAbove(orders,1000).forEach(o->System.out.printf("%s: %.2f",o.getOrderId(),o.getOrderTotal()));
        System.out.println();

        System.out.println("Customer by Revenue:");
        engine.getCustomerTierRevenue(orders,cr).forEach((t,rev)->System.out.printf("%s -> %.2f%n",t,rev));
        System.out.println();

        engine.getMostPopularProduct(orders).ifPresentOrElse(p->System.out.println("Most popular product: "+p),()->System.out.println("None"));
        System.out.println();

        System.out.println("Customers with no email:");
        customers.stream().filter(c->c.getEmail().isEmpty()).forEach(c->System.out.println(c.getName()));
        System.out.println();

        System.out.println("Group Customers by Tier:");
        cr.groupByTier().forEach((t,c)->{
            List<String> names=c.stream().map(Customer::getName).toList();
            System.out.println(t+" -> "+names);
        });
        System.out.println();
    }
}