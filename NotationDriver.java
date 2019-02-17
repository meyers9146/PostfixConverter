import java.util.Scanner;

public class NotationDriver {
	public static void main(String[] args) {
		Scanner scan = new Scanner(System.in);
	
	
	System.out.println("Enter infix expresson: ");
	String infix = scan.nextLine();
	
	
	System.out.println("Postfix: " + Notation.convertInfixToPostfix(infix));
	
	/*
	System.out.println("Enter postfix expression for evaluation: ");
	infix = scan.nextLine();
	
	System.out.println("Value: " + Notation.evaluatePostfixExpression(infix));
	*/
	
	scan.close();
	}
}
