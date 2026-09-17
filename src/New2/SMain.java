package New2;

import java.util.*;
import java.util.stream.Collectors;

record Student(String id, String name, Map<String,Integer> grades){}

class GradeAnalzer{
    static double getStudentAverage(Student s){
        return s.grades().values().stream().filter(Objects::nonNull).mapToInt(Integer::intValue).average().orElse(0.0);
    }
    static Optional<Student> findTopScorer(List<Student> students,String subject){
        return students.stream().filter(s->s.grades().containsKey(subject) && s.grades().get(subject)!=null).max(Comparator.comparing(s->s.grades().get(subject)));
    }
    static List<Student> sortByAverage(List<Student> students){
        return students.stream().sorted(Comparator.comparingDouble(GradeAnalzer::getStudentAverage).reversed()).toList();
    }
    private static String getGrade(Object avg){
        return switch (avg){
            case Double t when t>=90 -> "A";
            case Double t when t>=80 -> "B";
            case Double t when t>=70 -> "C";
            case Double t when t>=60 -> "D";
            default -> "F";
        };
    }
    static Map<String,List<Student>> groupByGradeCategory(List<Student> students){
        return students.stream().filter(Objects::nonNull).collect(Collectors.groupingBy(s->getGrade(getStudentAverage(s)),TreeMap::new,Collectors.toList()));
    }
    static Set<String> getAllSubjects(List<Student> students){
        return students.stream().filter(Objects::nonNull).flatMap(s->s.grades().keySet().stream()).collect(Collectors.toSet());
    }
    static List<Student> findFailingStudents(List<Student> students){
        return students.stream().filter(Objects::nonNull).filter(s->s.grades()!=null && s.grades().values().stream().filter(Objects::nonNull).anyMatch(score->score<60)).toList();
    }
}
public class SMain {
    public static void main(String[] args) {
        Student s1 = new Student("S1", "Alice", Map.of("Math", 95, "Science", 88, "English", 92));
        Student s2 = new Student("S2", "Bob", Map.of("Math", 45, "Science", 78, "English", 65));
        Student s3 = new Student("S3", "Charlie", Map.of("Math", 82, "Science", 91, "English", 79));
        Student s4 = new Student("S4", "Diana", Map.of("Math", 58, "Science", 62, "English", 55));

        List<Student> students = List.of(s1, s2, s3, s4);

        System.out.println("Alice: "+GradeAnalzer.getStudentAverage(s1));
        GradeAnalzer.findTopScorer(students, "Math").ifPresent(s->System.out.println(s.name()));
        System.out.println(GradeAnalzer.sortByAverage(students));
        System.out.println(GradeAnalzer.groupByGradeCategory(students));
        System.out.println(GradeAnalzer.getAllSubjects(students));
        System.out.println(GradeAnalzer.findFailingStudents(students));
    }
}
