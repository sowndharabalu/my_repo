import java.io.*;
import java.util.Base64;

public class SecureUser implements Serializable {
    private static final long serialVersionUID = 2L;
    private String username, email;
    private transient String password;
    private transient int loginCount=0;
    public SecureUser(String username, String email, String password){
        this.username = username;
        this.email = email;
        this.password = password;
        this.loginCount +=1;
    }
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        String encrypted= Base64.getEncoder().encodeToString(password.getBytes());
        out.writeObject(encrypted);
    }
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        String encrypted= (String) in.readObject();
        this.password = new String(Base64.getDecoder().decode(encrypted));
        loginCount =0;
    }
    public void serialize(String fileName) throws IOException {
        try(ObjectOutputStream out=new ObjectOutputStream(new FileOutputStream(fileName))) {
            out.writeObject(this);
        }
    }
    public static SecureUser deserialize(String fileName) throws IOException, ClassNotFoundException {
        try(ObjectInputStream in=new ObjectInputStream(new FileInputStream(fileName))) {
            SecureUser user = (SecureUser) in.readObject();
            return user;
        }
    }
    public String getUsername() {
        return username;
    }
    public String getPassword() {
        return password;
    }
    public String getEmail() {
        return email;
    }
    public int getLoginCount() {
        return loginCount;
    }
}
class Main2{
    public static void main(String[] args) throws IOException ,ClassNotFoundException{
        SecureUser s=new SecureUser("Balu","balu@gmail.com","balu1234");
        System.out.println("before serializing");
        System.out.println("Username: "+s.getUsername()+"\nPassword: "+s.getPassword()+"\nEmail: "+s.getEmail()+"\nLoginCount: "+s.getLoginCount());
        s.serialize("user.ser");
        SecureUser r=SecureUser.deserialize("user.ser");
        System.out.println("\nafter serializing");
        System.out.println("Username: "+r.getUsername()+"\nPassword: "+r.getPassword()+"\nEmail: "+r.getEmail()+"\nLoginCount: "+r.getLoginCount());
        if(r.getPassword().equals(s.getPassword()) && r.getLoginCount()==0){
            System.out.println("\nverified successfully!");
        }
    }
}
