import java.util.*;
import java.util.stream.Collectors;
class Product {
    private int id;
    private String name, category;
    private double price;
    public Product(int id, String name, String category, double price) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.price = price;
    }
    public int getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public String getCategory() {
        return category;
    }
    public double getPrice() {
        return price;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return id == product.id;
    }
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
class Order {
    private final String orderid,customerEmail;
    private final List<Product> products;
    private final double timeStamp;
    public Order(String orderid, String customerEmail, List<Product> products) {
        this.orderid = orderid;
        this.customerEmail = customerEmail;
        this.products = products;
        this.timeStamp = System.currentTimeMillis();
    }
    public String getOrderid() {
        return orderid;
    }
    public String getCustomerEmail() {
        return customerEmail;
    }
    public List<Product> getProducts() {
        return products;
    }
}
class Inventory {
    Map<Integer, Product> products=new HashMap<>();
    public void addProduct(Product p) {
        products.put(p.getId(), p);
    }
    public Optional<Product> findProduct(int pid) {
        return Optional.ofNullable(products.get(pid));
    }
    public List<Product> findByCategory(String category) {
        return products.values().stream().filter(p->p.getCategory().equalsIgnoreCase(category)).toList();
    }
}
class OrderProcessor {
    Queue<Order> pendingOrders=new ArrayDeque<>();
    Set<String> allOrderIds=new HashSet<>();
    Map<String, List<Order>> customerOrderHistory=new HashMap<>();
    public boolean placeOrder(Order order){
        String orderId=order.getOrderid();
        if(allOrderIds.contains(orderId)){
            return false;
        }
        allOrderIds.add(orderId);
        pendingOrders.add(order);
        customerOrderHistory.computeIfAbsent(order.getCustomerEmail(),k->new ArrayList<>()).add(order);
        return true;
    }
    public Optional<Order> processNextOrder(){
        return Optional.ofNullable(pendingOrders.poll());
    }
    public List<Order> getCustomerHistory(String email) {
        return customerOrderHistory.getOrDefault(email, new ArrayList<>());
    }
    public boolean isOrderProcessed(String orderId){
        return allOrderIds.contains(orderId);
    }
}
class SalesAnalytics{
    Map<String,Double> categoryRevenue=new TreeMap<>();
    public void recordSale(Order order){
        order.getProducts().forEach(p->categoryRevenue.merge(p.getCategory(), p.getPrice(), Double::sum));
    }
    public Map<String, Double> getTopCategories(int n){
        return categoryRevenue.entrySet().stream().sorted(Map.Entry.<String,Double>comparingByValue().reversed()).limit(n).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,(a,b)->a,LinkedHashMap::new));
    }
    public double getTotalRevenue(){
        return categoryRevenue.values().stream().mapToDouble(Double::doubleValue).sum();
    }
}
public class Main1 {
    public static void main(String[] args) {
        Inventory inventory=new Inventory();
        Product p1=new Product(1,"Laptop","Electronics",999.99);
        Product p2=new Product(2,"Mouse","Electronics",29.99);
        Product p3=new Product(3,"Desk","Furniture",199.50);
        Product p4=new Product(4,"Chair","Furniture",89.99);
        Product p5=new Product(5,"Notebook","Stationery",5.99);
        Product p6=new Product(6,"Pen","Stationery",1.50);
        Arrays.asList(p1,p2,p3,p4,p5,p6).forEach(inventory::addProduct);
        Order o1=new Order("ORD001","alice@gmail.com",Arrays.asList(inventory.findProduct(1).orElseThrow(),inventory.findProduct(2).orElseThrow()));
        Order o2=new Order("ORD002","bob@gmail.com",Arrays.asList(inventory.findProduct(3).orElseThrow(),inventory.findProduct(4).orElseThrow()));
        Order o3=new Order("ORD003","alice@gmail.com",Arrays.asList(inventory.findProduct(5).orElseThrow(),inventory.findProduct(6).orElseThrow()));
        Order o4=new Order("ORD004","alice@gmail.com",Arrays.asList(inventory.findProduct(1).orElseThrow()));
        OrderProcessor processor=new OrderProcessor();
        Arrays.asList(o1,o2,o3,o4).forEach(order -> {
            boolean placed=processor.placeOrder(order);
            System.out.println(placed ? "Placed: "+order.getOrderid():"Rejected: "+order.getOrderid());
        });
        boolean dup=processor.placeOrder(o1);
        System.out.println(dup ? "Placed: "+o1.getOrderid():"Rejected: "+o1.getOrderid()+" (duplicate)");
        List<Order> processed=new ArrayList<>();
        while (!processor.pendingOrders.isEmpty()){
            processor.processNextOrder().ifPresent(order->{
                System.out.println("Processed: "+order.getOrderid()+" | "+order.getCustomerEmail());
                processed.add(order);
            });
        }
        List<Order> aliceHistory=processor.getCustomerHistory("alice@gmail.com");
        System.out.println("\nAlice has "+aliceHistory.size()+" orders");
        aliceHistory.forEach(order->{
            String products=order.getProducts().stream().map(Product::getName).collect(Collectors.joining(", "));
            System.out.println(" "+order.getOrderid()+": "+products);
        });
        SalesAnalytics analytics=new SalesAnalytics();
        processed.forEach(analytics::recordSale);
        System.out.println("\nCategory Revenue:");
        analytics.categoryRevenue.forEach((category,revenue)->System.out.println(" "+category+": $"+revenue));
        analytics.getTopCategories(2).forEach((category,revenue)->System.out.println(" "+category+": $"+revenue));
        System.out.println("\nTotal Revenue: $"+analytics.getTotalRevenue());
        System.out.println("\nORD001 processed? "+processor.isOrderProcessed("ORD001"));
        System.out.println("\nORD999 processed? "+processor.isOrderProcessed("ORD999"));
    }
}