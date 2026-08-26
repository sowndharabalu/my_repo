import java.time.LocalDateTime;
import java.util.*;

class MenuItem {
    private final String name, category;
    private final double price;
    public MenuItem(String name, String category, double price) {
        this.name = name;
        this.category = category;
        this.price = price;
    }

    public String getName() { return name; }
    public double getPrice() { return price; }
    public String getCategory() { return category; }
    @Override
    public String toString() {
        return String.format("%s (%s) - $%.2f", name, category, price);
    }
}

class Restaurant {
    private final int id;
    private final String name;
    private final double rating;
    private Map<String, MenuItem> menu;
    public Restaurant(int id, String name, double rating) {
        this.id = id;
        this.name = name;
        this.rating = rating;
        this.menu = new HashMap<>();
    }

    public void addMenuItem(MenuItem menuItem) {
        menu.put(menuItem.getName(), menuItem);
    }
    public int getId() { return id; }
    public String getName() { return name; }
    public double getRating() { return rating; }
    public List<MenuItem> getMenu() {
        return menu.values().stream().toList();
    }
    @Override
    public String toString() {
        return name + " (" + rating + "⭐)";
    }
}

class Order2 implements Comparable<Order2> {
    private final String orderid;
    private final int customeraId, restaurantId, priority;
    private final long timestamp;
    List<MenuItem> items;
    public Order2(String orderid, int customeraId, int restaurantId, List<MenuItem> items, int priority) {
        this.orderid = orderid;
        this.customeraId = customeraId;
        this.restaurantId = restaurantId;
        this.priority = priority;
        this.items = List.copyOf(items);
        this.timestamp = System.currentTimeMillis();
    }

    public String getOrderid() { return orderid; }
    public int getCustomerId() { return customeraId; }
    public int getRestaurantId() { return restaurantId; }
    public int getPriority() { return priority; }
    public long getTimestamp() { return timestamp; }
    public List<MenuItem> getItems() { return items; }
    public double getOrderTotal() {
        return items.stream().mapToDouble(MenuItem::getPrice).sum();
    }
    @Override
    public int compareTo(Order2 o1) {
        return Integer.compare(this.priority, o1.getPriority());
    }
    @Override
    public String toString() {
        return "Order [" + orderid + "] for Customer " + customeraId + " (Priority: " + priority + ", Total: $" + String.format("%.2f", getOrderTotal()) + ")";
    }
}

class FoodDeliveryPlatform {
    private final Map<Integer, Restaurant> restaurants = new HashMap<>();
    private final Map<Integer, List<Order2>> customerOrderHistory = new HashMap<>();
    private final PriorityQueue<Order2> pendingOrders = new PriorityQueue<>(Comparator.naturalOrder());
    private final Set<Integer> activeCustomers = new HashSet<>();
    private final NavigableMap<Double, Set<Restaurant>> restaurantsByRating = new TreeMap<>();
    private final Deque<String> availableAgents = new ArrayDeque<>();
    private final LinkedHashMap<String, LocalDateTime> recentSearches = new LinkedHashMap<>(16, 0.75f, true) {
        @Override
        protected boolean removeEldestEntry(Map.Entry<String, LocalDateTime> eldest) {
            return size() > 5;
        }
    };

    public void addRestaurant(Restaurant restaurant) {
        restaurants.put(restaurant.getId(), restaurant);
        restaurantsByRating.computeIfAbsent(restaurant.getRating(), k -> new HashSet<>()).add(restaurant);
    }
    public void addAgent(String agentName) {
        availableAgents.offerLast(agentName);
    }
    public void placeOrder(Order2 order) {
        pendingOrders.offer(order);
        customerOrderHistory.computeIfAbsent(order.getCustomerId(), k -> new ArrayList<>()).add(order);
        activeCustomers.add(order.getCustomerId());
    }
    public Optional<Order2> processNextOrder() {
        Order2 order = pendingOrders.poll();
        if (order != null) {
            activeCustomers.remove(order.getCustomerId());
            return Optional.of(order);
        }
        return Optional.empty();
    }
    public List<Restaurant> getRestaurantsAboveRating(double rating) {
        return restaurantsByRating.tailMap(rating, true).values().stream().flatMap(Set::stream).toList();
    }
    public List<Order2> getCustomerHistory(int customerId) {
        return customerOrderHistory.getOrDefault(customerId, Collections.emptyList());
    }
    public Optional<String> assignAgent() {
        return Optional.ofNullable(availableAgents.poll());
    }
    public void releaseAgent(String name) {
        if (name != null) {
            availableAgents.offerLast(name);
        }
    }
    public void recordSearch(String customerId) {
        recentSearches.put(customerId, LocalDateTime.now());
    }
    public List<String> getRecentSearches(int n) {
        List<String> list = new ArrayList<>(recentSearches.keySet());
        Collections.reverse(list);
        return list.subList(0, Math.min(n, list.size()));
    }
    public List<Restaurant> getTopRatedRestaurants(int n) {
        return restaurantsByRating.descendingMap().values().stream().flatMap(Set::stream).limit(n).toList();
    }
}

public class FMain {
    public static void main(String[] args) {
        Restaurant r1 = new Restaurant(1, "Spice Hub", 4.5);
        Restaurant r2 = new Restaurant(2, "Burger Barn", 3.8);
        Restaurant r3 = new Restaurant(3, "Green Bowl", 4.9);
        Restaurant r4 = new Restaurant(4, "Pizza Palace", 4.2);

        r1.addMenuItem(new MenuItem("Chicken Tikka Masala", "Main Course", 14.99));
        r1.addMenuItem(new MenuItem("Garlic Naan", "Sides", 3.49));
        r2.addMenuItem(new MenuItem("Double Bacon Smash", "Main Course", 11.99));
        r2.addMenuItem(new MenuItem("Onion Rings", "Sides", 5.99));
        r3.addMenuItem(new MenuItem("Quinoa Power Salad", "Main Course", 12.50));
        r3.addMenuItem(new MenuItem("Avocado Toast", "Breakfast", 8.99));
        r4.addMenuItem(new MenuItem("Pepperoni Supreme", "Main Course", 18.99));
        r4.addMenuItem(new MenuItem("Garlic Knots", "Sides", 6.99));

        FoodDeliveryPlatform fd = new FoodDeliveryPlatform();

        for (Restaurant r : Arrays.asList(r1, r2, r3, r4)) {
            fd.addRestaurant(r);
        }

        fd.addAgent("Agent 001");
        fd.addAgent("Agent 002");
        fd.addAgent("Agent 003");

        List<Order2> orders = List.of(
                new Order2("ORD001", 101, 1, r1.getMenu(), 3),
                new Order2("ORD002", 102, 2, r2.getMenu(), 1),
                new Order2("ORD003", 101, 3, r3.getMenu(), 2),
                new Order2("ORD004", 103, 1, r1.getMenu(), 5), // Highest Priority (5)
                new Order2("ORD005", 104, 4, r4.getMenu(), 1),
                new Order2("ORD006", 102, 3, r3.getMenu(), 4)
        );

        for (Order2 o : orders) {
            fd.placeOrder(o);
        }

        System.out.println("Processing Next Order:");
        fd.processNextOrder().ifPresent(System.out::println);

        System.out.println("\nCustomer 101 Order History:");
        fd.getCustomerHistory(101).forEach(System.out::println);

        System.out.println("\nRestaurants Rated Above 4.0:");
        fd.getRestaurantsAboveRating(4.0).forEach(System.out::println);

        System.out.println("\nAgent Assignment Test:");
        String a1 = fd.assignAgent().orElse(null);
        String a2 = fd.assignAgent().orElse(null);
        System.out.println("Assigned: " + a1 + " and " + a2);
        fd.releaseAgent(a2);
        System.out.println("Re-assigned: " + fd.assignAgent().orElse("No one"));

        System.out.println("\nRecent Searches:");
        fd.recordSearch("User_101");
        fd.recordSearch("User_102");
        fd.recordSearch("User_103");
        fd.recordSearch("User_101");
        fd.recordSearch("User_104");

        fd.getRecentSearches(3).forEach(System.out::println);

        System.out.println("\nTop 2 Rated Restaurants");
        fd.getTopRatedRestaurants(2).forEach(System.out::println);
    }
}