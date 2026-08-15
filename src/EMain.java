import java.util.function.Function;
import java.util.stream.*;
import java.util.function.Predicate;
import java.util.*;
class Employee{
    final int id,yearsOfService;
    final String name,department;
    final double salary;
    public Employee(int id, String name, String department, double salary, int yearsOfService) {
        this.id = id;
        this.yearsOfService = yearsOfService;
        this.name = name;
        this.department = department;
        this.salary = salary;
    }
    public int getId() {
        return id;
    }
    public String getName(){
        return name;
    }
    public String getDepartment(){
        return department;
    }
    public double getSalary(){
        return salary;
    }
    public int getYearsOfService() {
        return yearsOfService;
    }
}
class EmployeeFilter{
    private final List<Employee> employees;
    public EmployeeFilter(List<Employee> employees) {
        this.employees =employees;
    }
    public List<Employee> filter(Predicate<Employee> filter){
        return employees.stream().filter(filter).collect(Collectors.toList());
    }
}
class EmployeeProcessor{
    public static Function<Employee, String> formatForEmail() {
        return emp->(emp.getName()+"@"+emp.getDepartment()+"'com").toLowerCase();
    }
    public static Function<Employee, String> formatForReport() {
        return emp->"Name: "+emp.getName()+" |Dept: "+emp.getDepartment()+" | Salary: $"+emp.getSalary();
    }
}
public class EMain{
    public static void main(String[] args){
        List<Employee> employees = new ArrayList<>();
        employees.add(new Employee(1, "Alice", "Engineering", 90000, 5));
        employees.add(new Employee(2, "Bob", "Sales", 60000, 2));
        employees.add(new Employee(3, "Charlie", "Engineering", 120000, 8));
        employees.add(new Employee(4, "Diana", "HR", 55000, 3));
        employees.add(new Employee(5, "Eve", "Engineering", 150000, 10));
        employees.add(new Employee(6, "Frank", "Sales", 75000, 4));
        EmployeeFilter filterSystem = new EmployeeFilter(employees);

        System.out.println("=== 1. Engineering Employees ===");
        filterSystem.filter(emp -> "Engineering".equalsIgnoreCase(emp.getDepartment()))
                .stream()
                .map(Employee::getName)
                .forEach(System.out::println);

        System.out.println("\n=== 2. Employees with Salary > 80,000 (Email Format) ===");
        filterSystem.filter(emp -> emp.getSalary() > 80000)
                .stream()
                .map(EmployeeProcessor.formatForEmail())
                .forEach(System.out::println);

        System.out.println("\n=== 3. Employees with Years of Service >= 5 (Report Format) ===");
        filterSystem.filter(emp -> emp.getYearsOfService() >= 5)
                .stream()
                .map(EmployeeProcessor.formatForReport())
                .forEach(System.out::println);

        System.out.println("\n=== 4. Highest Paid Employee in Engineering ===");
        employees.stream()
                .filter(emp -> "Engineering".equalsIgnoreCase(emp.getDepartment()))
                .max(Comparator.comparingDouble(Employee::getSalary))
                .ifPresent(emp -> System.out.println("Name: " + emp.getName() + " | Salary: $" + emp.getSalary()));

        System.out.println("\n=== 5. Employees Grouped by Department ===");
        Map<String, List<Employee>> groupedByDept = employees.stream()
                .collect(Collectors.groupingBy(Employee::getDepartment));

        groupedByDept.forEach((dept, emps) -> {
            System.out.println("\nDepartment: " + dept);
            emps.stream().map(Employee::getName).forEach(name -> System.out.println("  - " + name));
        });
    }
}