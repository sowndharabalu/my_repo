package New3;

import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

record Student(String id, String name, int age, Set<String> entrolledCourses){}

record Course(String code, String title, int credits, Department department){}

enum Department { CS,MATH,PHYSICS,CHEMISTRY,BIOLOGY}

class EntrollmentException extends Exception{
    EntrollmentException(String message){
        super(message);
    }

}
class University{
    private final Map<String,Student> students=new HashMap<>();
    private final Map<String,Course> courses=new HashMap<>();

    public void addStudent(Student student)throws EntrollmentException{
        Optional.of(student).filter(s->!students.containsKey(s)).orElseThrow(()->new EntrollmentException("Student already exists"));
        students.put(student.id(),student);
    }
    public void addCourse(Course course)throws EntrollmentException{
        Optional.of(course).filter(c->!courses.containsKey(c)).orElseThrow(()->new EntrollmentException("Course already exists"));
        courses.put(course.code(),course);
    }
    public void enroll(String std_id, String courseCode) throws EntrollmentException{
        Optional.of(std_id).filter(s->students.containsKey(s)).orElseThrow(()->new EntrollmentException("Student does not exist"));
        Optional.of(courseCode).filter(c->courses.containsKey(courseCode)).orElseThrow(()->new EntrollmentException("Course does not exist"));
        students.values().stream().filter(s->s.id().equals(std_id)).findFirst().ifPresent(s->s.entrolledCourses().add(courseCode));
    }
    public List<Student> getStudentsInCourse(String courseCode) {
        return students.values().stream().filter(s->s.entrolledCourses().contains(courseCode)).toList();
    }
    public List<Course> getCoursesForStudent(String studentId) {
        return students.values().stream().filter(s -> s.id().equals(studentId)).map(Student::entrolledCourses).map(s -> courses.get(s)).toList();
    }
    public int getTotalCredits(String studentId){
        return getCoursesForStudent(studentId).stream().mapToInt(Course::credits).sum();
    }
    public double getAverageAgeInDepartment(Department department){
        return students.values().stream().filter(s->s.entrolledCourses().contains(courses.values().stream().filter(c->c.department()==department))).mapToDouble(Student::age).sum();
    }
    public List<Student> findStudentsWithMinCredits(int minCredits){
        return students.values().stream().filter(s->getTotalCredits(s.id())<=minCredits).toList();
    }
    public Optional<Course> getMostPopularCourse(){
        return courses.values().stream().;
    }
}

public class Main1 {
    public static void main(String[] args) throws EntrollmentException{
        University uni = new University();

        uni.addStudent(new Student("S1", "Alice", 20, new HashSet<>()));
        uni.addStudent(new Student("S2", "Bob", 22, new HashSet<>()));
        uni.addStudent(new Student("S3", "Charlie", 21, new HashSet<>()));

        uni.addCourse(new Course("CS101", "Intro to CS", 3, Department.CS));
        uni.addCourse(new Course("CS201", "Data Structures", 4, Department.CS));
        uni.addCourse(new Course("MATH101", "Calculus", 4, Department.MATH));


        uni.enroll("S1", "CS101");
        uni.enroll("S1", "CS201");
        uni.enroll("S2", "CS101");
        uni.enroll("S3", "MATH101");


        System.out.println("Students in CS101: " + uni.getStudentsInCourse("CS101"));  // [Alice, Bob]
        System.out.println("Alice's courses: " + uni.getCoursesForStudent("S1"));      // [CS101, CS201]
        System.out.println("Alice's credits: " + uni.getTotalCredits("S1"));           // 7
        System.out.println("Avg age in CS: " + uni.getAverageAgeInDepartment(Department.CS));  // 21.0
        System.out.println("Students with 4+ credits: " + uni.findStudentsWithMinCredits(4));  // [Alice, Charlie]
        System.out.println("Most popular: " + uni.getMostPopularCourse());              // Optional[CS101]
    }
}
