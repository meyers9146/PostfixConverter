import java.util.Scanner;

/**
 * Driver class to test the Notation version 2.0 class
 * @author Mike Meyers
 * @version 1.0
 *
 */
public class NotationDriver {
	public static void main(String[] args) {
		prompt();
	}
	
	/**
	 * Prompt the user to process infix or postfix expressions through a menu
	 */
	public static void prompt() {
		
		Scanner scan = new Scanner(System.in);

		boolean again = true; //Sentinel to allow program to loop as needed
			
		while(again == true) {
			
			//Display options
			System.out.println("Notation 2.0 Tester");
			System.out.println("Enter a function to test: ");
			System.out.println("1. convertInfixToPostfix");
			System.out.println("2. convertPostfixToInfix");
			System.out.println("3. evaluatePostfixExpression");
			System.out.println("4. evaluateInfixExpression");
			System.out.println("0. exit");
			
			//User selects an option. Repeat if an invalid selection is entered
			char selection = scan.nextLine().charAt(0);
			while (selection < '0' || selection > '4') {
				System.out.println("Please enter a number 0-5");
				selection = scan.nextLine().charAt(0);
			}
			
			//Exit if option is selected
			if(selection == '0') System.exit(0);
			
			//If 1-4 is entered, prompt for user expression entry
			System.out.println("Enter the expression: ");
			String expr = scan.nextLine();
		
		switch(selection) {
		
			//Run fundtions as selected
			case '1':
				System.out.println("Infix: " + Notation.convertInfixToPostfix(expr));
				break;
			case '2':
				System.out.println("Postfix: " + Notation.convertPostfixToInfix(expr));
				break;
			case '3':
				System.out.println("Answer: " + Notation.evaluatePostfixExpression(expr));
				break;
			case '4':
				System.out.println("Answer: " + Notation.evaluateInfixExpression(expr));
				break;
			case '0':
				again = false;
				break;
			default:
				System.out.println("Enter a number 1-4, or enter 0 to quit");
		}
		
		//Prompt to go again. User enters N or n to break the loop
		if (selection == '0') continue;
		System.out.println("Again? Y/N");
		char response = scan.nextLine().charAt(0);
		if (response == 'N' || response == 'n') again = false;
	}
		//Close scanner when no further prompts
		scan.close();
		}
	}

