package New4;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

enum Department{
    ENGINEERING,SALES,MARKETING,HR,FINANCE;
}
record Employee(String id, String name, Department dept, double salary, int performanceRating, LocalDate joinDate){}

class InvalidDataException extends Exception{
    public InvalidDataException(String message){
        super(message);
    }
}

class EmployeeAnalyzer{
    static void validateEmployee(Employee emp) throws InvalidDataException{
        Optional.ofNullable(emp).orElseThrow(()->new InvalidDataException("Employee not found"));
        Optional.of(emp).filter(e->e.salary()>=0).orElseThrow(()->new InvalidDataException("Salary amount cannot be negative"));
        Optional.of(emp).filter(e->e.performanceRating()>=1 && e.performanceRating()<=10).orElseThrow(()->new InvalidDataException("Performance rating should be between 1 and 10"));
        Optional.of(emp).filter(e->e.joinDate()!=null && !e.joinDate().isAfter(LocalDate.now())).orElseThrow(()->new InvalidDataException("Join date cannot be null or in the future"));
    }
    static Map<Department,Double> getAverageSalaryByDept(List<Employee> employees){
        return employees.stream().filter(e->e.performanceRating()>=7).collect(Collectors.groupingBy(Employee::dept,Collectors.averagingDouble(Employee::salary)));
    }
    static Map<Department,Optional<Employee>> getTopPerformerByDept(List<Employee> employees){
        return employees.stream().collect(Collectors.groupingBy(Employee::dept,Collectors.maxBy(Comparator.comparingInt(Employee::performanceRating))));
    }
    static List<Employee> getSortedEmployees(List<Employee> employees){
        return employees.stream().sorted(Comparator.comparingInt(Employee::performanceRating).reversed().thenComparingDouble(Employee::salary).reversed().thenComparing(Employee::name)).toList();
    }
    static Map<String,Integer> getExperienceYears(List<Employee> employees){
        return employees.stream().collect(Collectors.toMap(Employee::name,e->(int)ChronoUnit.YEARS.between(e.joinDate(),LocalDate.now()),(exist,replacement)->exist));
    }
    static Map<Boolean,List<Employee>> partitionByPerformance(List<Employee> employees){
        return employees.stream().collect(Collectors.partitioningBy(e->e.performanceRating()>=7));
    }
    static Map<Department, DoubleSummaryStatistics> getSalaryStatsByDept(List<Employee> employees){
        return employees.stream().collect(Collectors.groupingBy(Employee::dept,Collectors.summarizingDouble(Employee::salary)));
    }
    static List<Employee> findAboveAverageSalary(List<Employee> employees){
        Map<Department,Double> result = employees.stream().collect(Collectors.groupingBy(Employee::dept,Collectors.averagingDouble(Employee::salary)));
        return employees.stream().filter(e->e.salary()>result.getOrDefault(e.dept(),0.0)).toList();
    }
}
public class Main1 {
    public static void main(String[] args) throws InvalidDataException{
        List<Employee> employees = List.of(
                new Employee("E1", "Alice", Department.ENGINEERING, 90000, 8, LocalDate.of(2020, 5, 15)),
                new Employee("E2", "Bob", Department.ENGINEERING, 75000, 6, LocalDate.of(2021, 3, 10)),
                new Employee("E3", "Charlie", Department.SALES, 65000, 9, LocalDate.of(2019, 8, 20)),
                new Employee("E4", "Diana", Department.SALES, 70000, 7, LocalDate.of(2022, 1, 5)),
                new Employee("E5", "Eve", Department.HR, 55000, 5, LocalDate.of(2023, 6, 12))
        );

        for(Employee e:employees){
            EmployeeAnalyzer.validateEmployee(e);
        }

        System.out.println(EmployeeAnalyzer.getTopPerformerByDept(employees));
        System.out.println(EmployeeAnalyzer.getAverageSalaryByDept(employees));
        System.out.println(EmployeeAnalyzer.getSalaryStatsByDept(employees));
        System.out.println(EmployeeAnalyzer.getExperienceYears(employees));
        System.out.println(EmployeeAnalyzer.getSortedEmployees(employees));
        System.out.println(EmployeeAnalyzer.findAboveAverageSalary(employees));
        System.out.println(EmployeeAnalyzer.partitionByPerformance(employees));
    }
}
