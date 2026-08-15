import java.util.Arrays;

public class Student {
    public double calculateavg(int[] marks) {
        int sum=0;
        for(int x:marks) {
            sum+=x;
        }
        var avg=sum/marks.length;
        return avg;
    }
    public char getGrade(double avg){
        char grade;
        if(avg>=90){
            grade='A';
        }else if(avg>=80){
            grade='B';
        }else if(avg>=70){
            grade='C';
        }else if(avg>=60) {
            grade = 'D';
        }else {
            grade = 'F';
        }
        return grade;
    }
    public String getResult(int[] marks) {
        for(int x:marks) {
            if(x<40) {
                return "FAIL";
            }
        }
        return "PASS";
    }
    public void printReport(String name, int roll_no, int[] marks) {
        StringBuilder a=new StringBuilder();
        double avg=calculateavg(marks);
        a.append(String.format("Name: %s%nRoll No: %d%nMarks: %s%nAverage Score: %.2f%nGrade: %c%nResult: %s%n",name,roll_no, Arrays.toString(marks),avg,getGrade(avg),getResult(marks)));
        System.out.println(a);
    }
    public static void main(String[] args) {
        Student s1=new Student();
        Student s2=new Student();
        Student s3=new Student();
        s1.printReport("Sam",100,new int[]{60,40,65,90,74});
        s2.printReport("Balu",137,new int[]{70,84,66,95,79});
        s3.printReport("Json",153,new int[]{66,93,65,86,63});
    }
}