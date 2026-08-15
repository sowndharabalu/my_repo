import java.util.ArrayList;
import java.util.Scanner;

class Scratch {
    public static void main(String[] args) {
        double avg,tot=0;
        ArrayList<Double> mark=new ArrayList<>();
        Scanner sc=new Scanner(System.in);
        System.out.println("Enter no of subjects:");
        int n=sc.nextInt();
        for(int i=0;i<n;i++){
            System.out.printf("Please enter the mark for subject %d:",i+1);
            mark.add(sc.nextDouble());
            tot+=mark.get(i);
        }
        avg=tot/mark.size();
        System.out.printf("Total mark is: %.2f%n",tot);
        System.out.printf("Average mark is: %.2f%n",avg);
        if (avg<50){
            System.out.println("Grade: F");
        } else if (avg>=50 && avg<=65) {
            System.out.println("Grade: C");
        } else if (avg>65 && avg<=80) {
            System.out.println("Grade: B");
        } else if (avg>80 && avg<=100) {
            System.out.println("Grade: A");
        }else {
            System.out.println("Invalid marks!");
        }
    }
}