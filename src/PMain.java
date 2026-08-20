import java.util.*;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@interface ValidProducts {
    double minPrice() default 0.0;
    String[] allowedCategories() default {};
}

class Supplier {
    private final String name;
    private final Optional<String> email;

    public Supplier(String name, String email) {
        this.name = name;
        this.email = Optional.ofNullable(email);
    }
    public String getName() {
        return name;
    }
    public Optional<String> getEmail() {
        return email;
    }
}

@ValidProducts(minPrice = 10.0, allowedCategories = {"Electronics", "Furniture"})
class Products {
    int id;
    String name;
    Optional<String> category;
    Optional<Double> price;
    Optional<Supplier> supplier;

    public Products(int id, String name, String category, Double price, Supplier supplier) {
        this.id = id;
        this.name = name;
        this.category = Optional.ofNullable(category);
        this.price = Optional.ofNullable(price);
        this.supplier = Optional.ofNullable(supplier);
    }
    public int getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public Optional<String> getCategory() {
        return category;
    }
    public Optional<Double> getPrice() {
        return price;
    }
    public Optional<Supplier> getSupplier() {
        return supplier;
    }
}

class Inventory1 {
    private final List<Products> products;

    public Inventory1(List<Products> products) {
        this.products = products;
    }
    public Optional<Products> findById(int id) {
        return products.stream().filter(p -> p.getId() == id).findFirst();
    }
    public List<Products> findByCategory(String category) {
        return products.stream().filter(p -> p.getCategory().filter(c -> c.equalsIgnoreCase(category)).isPresent()).toList();
    }
    public double getAveragePrice() {
        return products.stream().map(Products::getPrice).flatMap(Optional::stream).mapToDouble(Double::doubleValue).average().orElse(0.0);
    }
    public Optional<String> getSupplierEmail(int ProductsId) {
        return findById(ProductsId).flatMap(Products::getSupplier).flatMap(Supplier::getEmail);
    }
}

class ProductsValidator {
    public static boolean validate(Products Products) {
        Class<?> clazz = Products.getClass();
        if (!clazz.isAnnotationPresent(ValidProducts.class)) {
            return true;
        }
        ValidProducts annotation = clazz.getAnnotation(ValidProducts.class);
        boolean isPriceValid = Products.getPrice().filter(p -> p >= annotation.minPrice()).isPresent();
        if (!isPriceValid) {
            return false;
        }
        String[] allowed = annotation.allowedCategories();
        if (allowed.length > 0) {
            return Products.getCategory().filter(cat -> Arrays.stream(allowed).anyMatch(cat::equalsIgnoreCase)).isPresent();
        }
        return true;
    }
}

public class PMain {
    public static void main(String[] args) {
        Supplier techCorp = new Supplier("TechCorp", "orders@techcorp.com");
        Supplier gadgetInc = new Supplier("GadgetInc", null);
        Supplier woodWorks = new Supplier("WoodWorks", "sales@woodworks.com");

        Products p1 = new Products(1, "Laptop", "Electronics", 999.99, techCorp);
        Products p2 = new Products(2, "Mouse", "Electronics", 29.99, gadgetInc);
        Products p3 = new Products(3, "Desk", "Furniture", 199.50, woodWorks);
        Products p4 = new Products(4, "Unknown", null, null, null);
        Products p5 = new Products(5, "Paper", "Stationery", 5.00, null);

        Inventory1 inventory = new Inventory1(List.of(p1, p2, p3, p4, p5));

        System.out.println("--- 1. Find Products ID 1 ---");
        inventory.findById(1).map(Products::getName).ifPresent(name -> System.out.println("Found: " + name));

        System.out.println("\n--- 2. Find Products ID 99 ---");
        String p99Name = inventory.findById(99).map(Products::getName).orElse("Not found");
        System.out.println("ID 99: " + p99Name);

        System.out.println("\n--- 3. Electronics Products ---");
        inventory.findByCategory("Electronics").forEach(p -> System.out.println("- " + p.getName()));

        System.out.println("\n--- 4. Average Price ---");
        System.out.printf("Average Price: $%.2f%n", inventory.getAveragePrice());

        System.out.println("\n--- 5. Supplier Emails ---");
        String email1 = inventory.getSupplierEmail(1).orElse("No email");
        System.out.println("Products 1 Email: " + email1);

        String email2 = inventory.findById(2).map(p -> p.getSupplier().flatMap(Supplier::getEmail).orElse("No email")).orElse("No supplier");
        System.out.println("Products 2 Email: " + email2);

        String email5 = inventory.findById(5).flatMap(Products::getSupplier).flatMap(Supplier::getEmail).orElseGet(() -> inventory.findById(5).flatMap(Products::getSupplier).isPresent() ? "No email" : "No supplier");
        System.out.println("Products 5 Email: " + email5);

        System.out.println("\n--- 6. Validation Results ---");
        List.of(p1, p2, p3, p4, p5).forEach(p -> {
            boolean isValid = ProductsValidator.validate(p);
            System.out.println(p.getName() + " (ID " + p.getId() + "): " + (isValid ? "PASS" : "FAIL"));
        });
    }
}