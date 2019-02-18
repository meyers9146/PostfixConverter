import java.util.Scanner;

public class NotationDriver {
	public static void main(String[] args) {
		Scanner scan = new Scanner(System.in);
	
	
	System.out.println("Enter infix expresson: ");
	String infix = scan.nextLine();
	
	String infix2 = Notation.convertInfixToPostfix(infix);
	
	
	System.out.println("Postfix: " + infix2);
	
	System.out.println("Back to infix: " + Notation.convertPostfixToInfix(infix2));
	
	scan.close();
	}
}
