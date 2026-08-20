import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

abstract class Person{
    private int id;
    private String name, email;
    public Person(int id, String name, String email){
        this.id = id;
        this.name = name;
        this.email = email;
    }
    public int getId(){
        return this.id;
    }
    public String getName(){
        return this.name;
    }
    public String getEmail(){
        return this.email;
    }
    abstract public String getRole();
    public String getDetails(){
        return "ID: "+id+", Name: "+name+", Email: "+email;
    }
}

class Student2 extends Person{
    private String major;
    private double gpa;
    public Student2(int id, String name, String email, String major, double gpa){
        super(id, name, email);
        this.major = major;
        this.gpa = gpa;
    }
    public String getMajor(){
        return this.major;
    }
    public double getGpa(){
        return this.gpa;
    }
    @Override
    public String getRole(){
        return "Student";
    }
    @Override
    public String getDetails(){
        return super.getDetails()+", Major: "+major+", GPA: "+gpa;
    }
}

class Professor extends Person{
    private String department;
    private double salary;
    public Professor(int id, String name, String email, String department, double salary){
        super(id, name, email);
        this.department = department;
        this.salary = salary;
    }
    public String getDepartment(){
        return department;
    }
    public double getSalary(){
        return salary;
    }
    @Override
    public String getRole(){
        return "Professor";
    }
    @Override
    public String getDetails(){
        return super.getDetails()+", Department: "+department+", Salary: "+salary;
    }
}

class University {
    private List<Person> members=new ArrayList<>();
    public void addMember(Person p){
        members.add(p);
    }
    public void printAllMembers(){
        for(Person p : members){
            System.out.println(p.getDetails());
        }
    }
    public List<Student2> getStudentsAboveGba(double gpa){
        return members.stream().filter(p->p instanceof Student2).map(p->(Student2)p).filter(s->s.getGpa()>gpa).collect(Collectors.toList());
    }
    public double getAverageProffesorSalary(){
        return members.stream().filter(p->p instanceof Professor).map(p->(Professor)p).mapToDouble(Professor::getSalary).average().orElse(0.0);
    }
    public Optional<Person> findById(int id){
        return members.stream().filter(p->p.getId()==id).findFirst();
    }
}

interface Course{
    String getCourseName();
    int getCredits();
    default String getCourseInfo(){
        return getCourseName()+" ("+getCredits()+") credits";
    }
}

interface Enrollable{
    void enroll(Course c);
    List<Course> getEnrolledCourses();
}

class EnrollableStudent extends Student2 implements Enrollable{
    private List<Course> courses=new ArrayList<>();
    public EnrollableStudent(int id, String name, String email, String department, double gpa){
        super(id, name, email, department, gpa);
    }
    @Override
    public void enroll(Course c) {
        courses.add(c);
    }
    @Override
    public List<Course> getEnrolledCourses() {
        return courses;
    }
    public String getRole(){
        return "Enrollable Student";
    }
}

public class SMain  {
    public static void main(String[] args) {
        Student2 s1=new Student2(101,"Alice","alice@gmail.com","CS",3.8);
        Student2 s2=new Student2(102,"Bob","bob@gmail.com","Math",3.2);
        Student2 s3=new Student2(103,"Charlie","charlie@gmail.com","CS",3.9);
        Professor p1=new Professor(11,"Dr.Smith","smith@gmail.com","CS",90000);
        Professor p2=new Professor(12,"Dr.Jones","jones@gmail.com","Math",85000);
        EnrollableStudent e=new EnrollableStudent(105,"Diana","diana@gmail.com","Physics",3.5);
        University un=new University();
        un.addMember(p1);
        un.addMember(p2);
        un.addMember(s1);
        un.addMember(s2);
        un.addMember(s3);
        un.addMember(e);
        un.printAllMembers();
        un.getStudentsAboveGba(3.5).forEach(s->System.out.println(s.getName()+": "+s.getGpa()));
        System.out.println("Average Salary: "+un.getAverageProffesorSalary());
        System.out.println(un.findById(105).map(Person::getDetails).orElse("Not Found"));
        System.out.println(un.findById(99).map(Person::getDetails).orElse("Not Found"));
        e.enroll(new Course);
    }
}
