package learn;
import java.util.Scanner;
public class Character{
    String name;
    int health;
    int level=1;
    public Character(String c_name,int c_health){
        name=c_name;
        health=c_health;
    }
    public void takeDamage(int damageAmount){
		    health-=damageAmount;
	            System.out.printf("%s took %d damage! Health is now:%d%n",name,damageAmount,health);
		}
    public int attack(){
                int min = 10;
                int max = 25;
                int hit=(int)(Math.random() * (max - min + 1) + min);
                return hit;
                }
}
class Player extends Character{
                public Player(String p_name,int p_health){
                    super(p_name,p_health);
                }
                String[] inventory={"Magic Potion", "Fire Bomb", "Golden Apple"};
                public void heal(int healAmount){
                    health+=healAmount;
                    System.out.printf("%s got healed for %d! Health is now:%d%n",name,healAmount,health);
                }
                public void showInventory(){
                    System.out.println("--- INVENTORY ---");
                    for(int i=0;i<inventory.length;i++){
                        System.out.println(i+1+"."+inventory[i]);
                    }
}
}
class Enemy extends Character{
                public Enemy(String e_name,int e_health){
                    super(e_name,e_health);
                }
              
}
class Game{
	public static void main(String args[]) {
            Scanner s=new Scanner(System.in);
            Player p1=new Player("Hero",100);
            Enemy e1=new Enemy("villan",50);
            while(p1.health>0 && e1.health>0){
                System.out.println("___YOUR TURN___");
                System.out.println("Press 1 to Attack");
                System.out.println("Press 2 to Heal");
                System.out.println("Press 3 to use Items");
                int choice=s.nextInt();
                if(choice==1){
                    int rd =p1.attack();
                    e1.takeDamage(rd);
                }else if(choice==2){
                    p1.heal(15);
                }else if(choice==3){
                    p1.showInventory();
                    int n=s.nextInt();
                    n-=1;
                    if(p1.inventory[n].equals("Magic Potion")){
                        p1.heal(25);
                    }else if(p1.inventory[n].equals("Fire Bomb")){
                        e1.takeDamage(25);
                    }else if(p1.inventory[n].equals("Golden Apple")){
                        p1.heal(50);
                    }
                }
                if(e1.health>0){
                    int rd=e1.attack();
                    p1.takeDamage(rd);
                }
            }
            if(p1.health<=0){
                    System.out.println("Game Over! The Villain wins");
                }else{
                    System.out.println("Victory! The Hero defeated the Villain");
                }
            s.close();
        }
}