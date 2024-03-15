import java.util.Scanner;
import java.sql.*;
import java.util.Random;

class operations {
    // -----------Initialization----------------

    // Creating Scanner object
    Scanner sc = new Scanner(System.in);

    // Strings for connection
    public String url = "jdbc:mysql://localhost:3306/banking";
    public String url1 = "jdbc:mysql://localhost:3306/banking_backup";
    public String username, username1;
    public String password, password1;
    public static String userID;
    public String query, query1;
    public int new_bal, new_bal1, bal, bal1;

    // Creating constructor to initialize username and password
    operations(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // New Constructor for creating fresh new object
    operations(String user, String pass, int a) {
        username1 = user;
        password1 = pass;
    }

    public void check_conn() {
        try {
            Connection conn = DriverManager.getConnection(url, username, password);
            Statement stm = conn.createStatement();

            System.out.println("User authenticated...!");

        } catch (Exception e) {
            System.out.println("Invalid Credentials!!");
            System.exit(0);
        }
    }

    public boolean check_conn1() {
        try {
            Connection conn = DriverManager.getConnection(url1, username1, password1);
            Statement stm = conn.createStatement();

            System.out.println("\nLogin successfull...!\n");
            return true;

        } catch (Exception e) {
            System.out.println("Invalid Credentials!!");
            return false;

        }
    }

    // ------------Operations---------------
    public int show_balance(String user) {
        try {
            Connection conn = DriverManager.getConnection(url, username, password);
            Statement stm = conn.createStatement();

            query = "select balance from data where user = '" + user + "'";
            ResultSet rs = stm.executeQuery(query);

            rs.next();
            return rs.getInt("Balance");

        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public String id_generator() {
        String alphabets = "abcdefghijklmnopqrstuvwxyz", num = "0123456789", final_str;
        Random rand = new Random();

        int length = 5;
        char[] text = new char[length - 3]; // array for 2 alphabets
        char[] nums = new char[length - 2]; // array for 3 numbers

        // Generating String using nested for loop
        for (int i = 0; i < length - 2; i++) {
            for (int a = 0; a < length - 3; a++) {
                text[a] = alphabets.charAt(rand.nextInt(24));
            }
            for (int b = 0; b < length - 2; b++) {
                nums[b] = num.charAt(rand.nextInt(10));
            }
        }

        final_str = "" + text[0] + text[1] + nums[0] + nums[1] + nums[2];

        // Checking database to avoid Duplicate entry constraint violation
        try {
            Connection conn = DriverManager.getConnection(url, username, password);
            Statement stm = conn.createStatement();

            query = "select count(Name) from data where user = '" + final_str + "'";
            ResultSet rs = stm.executeQuery(query);
            rs.next();

            if (rs.getInt("count(Name)") == 0) {
                return final_str;
            } else {
                id_generator();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "null";
        }
        return final_str;

    }

    public String tran_id_generator() {
        String alphabets = "ABCDEFGHIJKLMNOPQRSTUVWXYZ", num = "0123456789", final_str;
        Random rand = new Random();

        int length = 10;
        char[] text = new char[length - 6]; // array for 4 alphabets
        char[] nums = new char[length - 4]; // array for 6 numbers

        // Generating String using nested for loop
        for (int i = 0; i < length - 4; i++) {
            for (int a = 0; a < length - 6; a++) {
                text[a] = alphabets.charAt(rand.nextInt(24));
            }
            for (int b = 0; b < length - 4; b++) {
                nums[b] = num.charAt(rand.nextInt(10));
            }
        }

        final_str = "" + text[0] + text[1] + text[2] + text[3] + nums[0] + nums[1] + nums[2] + nums[3] + nums[4]
                + nums[5];

        // Checking database to avoid Duplicate entry constraint violation
        try {
            Connection conn = DriverManager.getConnection(url, username, password);
            Statement stm = conn.createStatement();

            query = "select count(amount) from transac where tran_id = '" + final_str + "'";
            ResultSet rs = stm.executeQuery(query);
            rs.next();

            if (rs.getInt("count(amount)") == 0) {
                return final_str;
            } else {
                tran_id_generator();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "null";
        }
        return final_str;

    }

    public boolean check_user(String ID, String pass) {
        try {
            int ret;
            Connection conn = DriverManager.getConnection(url, username, password);
            Statement stm = conn.createStatement();

            query = "select count(Name) from data where user = '" + ID + "' && password = '" + pass + "'";
            ResultSet rs = stm.executeQuery(query);

            rs.next();
            if (rs.getInt("count(Name)") == 1) {
                System.out.println("User authorized..!");
                userID = ID;
                ret = rs.getInt("count(Name)");
            } else {
                System.out.println("\n----X----Invalid username or password..!----X----");
                ret = rs.getInt("count(Name)");
            }

            switch (ret) {
                case 0:
                    return false;

                case 1:
                    return true;

                default:
                    return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void transfer() {

        try {
            Connection conn = DriverManager.getConnection(url, username, password);

            Statement stm = conn.createStatement();
            Statement stm1 = conn.createStatement();
            Statement stm3 = conn.createStatement();
            Statement stm4 = conn.createStatement();
            Statement stm5 = conn.createStatement();
            Statement stm6 = conn.createStatement();

            String a = "";
            
            int b = 9;
            System.out.print("\nEnter id of the reciever: ");
            String receiver = sc.next();

            // Check for receiver name in database
            try {
                ResultSet rs3 = stm3.executeQuery("select Name from data where user = '" + receiver + "'");
                rs3.next();
                a = rs3.getString("Name");
                b = 0;
            } catch (Exception e) {
                System.out.println("The account you entered does not exist...!\nPlease try again: ");
                b = 1;
            }

            if (b == 1) {
                transfer();
            }

            System.out.print("Enter amount to send: ");
            int amount = sc.nextInt();

            // ----------------------------------Money Transfer------------------------------
            // Check for self transfer
            if (receiver == userID) {
                System.out.println("You cannot transfer money to your own account..!");
                return;
            }

            // Re-authenticate
            System.out.print("Enter your password again: ");
            String pass = sc.next();

            if (check_user(userID, pass)) {

                query = "select balance from data where user = '" + userID + "'";
                ResultSet rs = stm.executeQuery(query);
                query1 = "select balance from data where user = '" + receiver + "'";
                ResultSet rs1 = stm1.executeQuery(query1);

                // balance of userID
                rs.next();
                bal = rs.getInt("balance");

                // balance of receiver
                rs1.next();
                bal1 = rs1.getInt("balance");

                if (bal < amount) {
                    System.out.println("\n-----------Your balance is insufficient..!-----------\n");
                    return;
                } else {
                    // Deducting money from userID's balance
                    new_bal = bal - amount;
                    query = "update data set balance = " + new_bal + " where user = '" + userID + "'";
                    stm.executeUpdate(query);

                    // Adding money to receiver's balance
                    new_bal1 = bal1 + amount;
                    query1 = "update data set balance = " + new_bal1 + " where user = '" + receiver + "'";
                    stm.executeUpdate(query1);

                    // Sort database for Last In First out
                    ResultSet rs3 =  stm5.executeQuery("select count(tran_id) from transac");
                    rs3.next();
                    int sr_no = rs3.getInt("count(tran_id)") + 1;

                    // -----------------------ID generation-----------------------
                    String ID = tran_id_generator();
                    query = "insert into transac values('" + ID + "', '" + userID + "', '" + receiver + "', curdate(), curtime(), " + amount + ", " + sr_no + ")";
                    stm4.executeUpdate(query);

                    System.out.println("\n");
                    System.out.println(amount + " rupees transferred to " + a + " successfully..!");
                    System.out.println("Transaction ID: " + ID);

                    ResultSet rs6 = stm6.executeQuery("SELECT * FROM transac ORDER BY sr_no DESC");
                    rs6.next();
                }

            } else {
                System.out.println("Wrong password..!");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void add_user() {
        try {
            Connection conn = DriverManager.getConnection(url, username, this.password);
            Statement stm = conn.createStatement();

            String id = id_generator();

            System.out.print("\nPlease enter name of the user: ");
            String name = sc.next();
            System.out.println("Enter a strong password: ");
            String user_pass = sc.next();
            System.out.println("Re-enter password: ");
            String user_pass1 = sc.next();

            if (user_pass.matches(user_pass1)) {
                query = "insert into data values('" + name + "', '" + id + "', '" + user_pass + "', 0)";
                System.out.println(query);
                stm.executeUpdate(query);
                System.out.println("\nUser added successfully..!");
                System.out.println("The user's id is: " + id + ", please keep for future reference...");

            } else {
                System.out.println("The passwords do not match..!\nPlease try again..!");
                add_user();
            }

        } catch (Exception e) {
            System.out.println("Duplicate entry..!");
        }
    }
}

public class banking extends operations {
    banking(String username, String password) {
        super(username, password);
    }

    public static void main(String[] args) {
        // Creating Scanner class
        Scanner sc = new Scanner(System.in);

        // ----------------------/-------------------------
        // Taking database credentials for authentication

        // System.out.print("Enter your username: ");
        String username = "manager";
        // System.out.print("Enter your password: ");
        String password = "management";
        // ----------------------/-------------------------

        // Creating object by initializing username and password
        operations op = new operations(username, password);

        // Checking connection
        op.check_conn();

        System.out.println("---------Welcome to Bank Management System-----------");
        // Taking User Credentials
        System.out.print("Please enter your ID: ");
        String id = sc.nextLine();
        System.out.print("Please enter your password: ");
        String pass = sc.nextLine();

        // Checking the id and password of user in database
        if (op.check_user(id, pass)) {

        } else {
            System.out.println("\n2 more attempts...\n");
            System.out.print("Enter your ID: ");
            id = sc.nextLine();
            System.out.print("Enter your password: ");
            pass = sc.nextLine();

            if (op.check_user(id, pass)) {

            } else {
                System.out.println("\n1 more attempt...\n");
                System.out.print("Enter your ID: ");
                id = sc.nextLine();
                System.out.print("Enter your password: ");
                pass = sc.nextLine();
                op.check_user(id, pass);
            }
        }

        // While loop for menu
        while(true) {
            System.out.println("-----------Bank Management System-----------\n");
            System.out.println("Choose your option: ");
            System.out.println("1. Check Balance");
            System.out.println("2. Transfer funds");
            System.out.println("3. Add new user");
            System.out.println("4. Exit");

            int choice = sc.nextInt();

            switch (choice) {
                case 1:
                    System.out.println("Your current balance is: " + op.show_balance(userID));
                    break;

                case 2:
                    op.transfer();
                    break;

                case 3:
                    // Creating new object to check authentication
                    try {
                        System.out.print("\nPlease enter the database password to continue: ");
                        String dat_pass = sc.next();
                        operations auth = new operations("root", dat_pass, 0);
                        if (auth.check_conn1()) {
                            op.add_user();
                        } else {
                            System.out.println("-------X---Invalid password---X------");
                            System.out.println("2 attempts left...");
                            System.out.print("\nPlease enter the database password to continue: ");
                            dat_pass = sc.next();
                            operations auth1 = new operations("root", dat_pass, 0);
                            if (auth1.check_conn1()) {
                                op.add_user();
                            } else {
                                System.out.println("-------X---Invalid password---X------");
                                System.out.println("1 attempt left");
                                System.out.print("\nPlease enter the database password to continue: ");
                                dat_pass = sc.next();
                                operations auth2 = new operations("root", dat_pass, 0);
                                if (auth2.check_conn1()) {
                                    op.add_user();
                                } else {
                                    System.out.println("The password you entered is invalid...!");
                                }
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                case 4:
                    System.out.println("Vedant Patil\nvedant5489@gmail.com");
                    System.exit(0);
                    break;

                default:
                    break;
            }
        }

    }
}