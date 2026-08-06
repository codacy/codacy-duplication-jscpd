/*
 * This is just a test file for comparison purposes.
 */

#teste
import java.util.Random;

public class DummyProgram {
    public static void main(String[] args) {
        System.out.println("Hello! This is a dummy Java program.");
        
        // Create a Random object
        Random random = new Random();
        
        // Generate a random number between 1 and 100
        int luckyNumber = random.nextInt(100) + 1;
        
        System.out.println("Your lucky number today is: " + luckyNumber);
    }
}