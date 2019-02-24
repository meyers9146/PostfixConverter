import java.util.ArrayList;

/**
 * Convert mathematical expressions in infix notation (a+b) to postfix notation (ab+) and vice-versa.
 * Includes methods for evaluating expressions as well.
 * 
 * Version 1.0 - only works with single-digit numbers, but does not require whitespace delineation for entered expressions
 * 
 * @author Mike Meyers
 * @version 1.0
 *
 */
public class Notation {
	
	/**
	 * Default constructor. Object has no variables to initialize.
	 */
	public Notation() {
	}
	
	/**
	 * Convert an infix expression to a postfix expression
	 * @param infix the infix-notated expression to a postfix-notated expression
	 * @return the postfix-notated expression
	 * @throws InvalidNotationFormatException if the input String is invalid
	 */
	public static String convertInfixToPostfix(String infix) throws InvalidNotationFormatException{
		
		//Confirm that the expression contains only valid characters
		if(!hasValidCharacters(infix)) throw new InvalidNotationFormatException(
				"The expression may only contain numbers/letters, brackets/parens, and +, -, *, /, *");
		
		//Confirm that the expression does not contain unbalanced parentheses or brackets
		if(!isBalanced(infix)) throw new InvalidNotationFormatException(
				"The expression contains unbalanced parentheses or brackets");
		
		//Check for improper notation in the form of sequential operator symbols (ex: 5++4)
		if(hasSequentialOperators(infix)) throw new InvalidNotationFormatException(
				"The expression may not contain sequential operators");
		
		
		//Convert the single String to a String array. 
		String[] strArray = infix.split("");
		ArrayList<String> strings = new ArrayList<>();
		for (String str : strArray) {
			strings.add(str);
		}
				
		//Create output String
		String returnString = "";
		
		//Create separate Stack for operators
		MyStack<String> operatorStack = new MyStack<>();
		
		//Iterate through the String array and sort the tokens
		for (int i = 0; i < strings.size(); i++) {
			try {
			switch (strings.get(i)) {
				case " " : //skip whitespace characters
					break;
				
				case "^" : //exponents always get pushed
					operatorStack.push(strings.get(i));
					break;
				
				case "+" : // + and - operators have lowest precedence, so they get pushed unless there are already + or -
				case "-" : // operators to begin with
					while(!operatorStack.isEmpty() && !isBrace(operatorStack.peek())) {
						returnString += operatorStack.pop();
					}
					operatorStack.push(strings.get(i));
				break;
				
				case "*" : // *, /, +, and - operators will all push the stack. Only ^ operators have higher precedence
				case "/" :
					while(!operatorStack.isEmpty() && operatorStack.peek().equals("^")) {
						returnString += operatorStack.pop();
					}
					while(!operatorStack.isEmpty() && (operatorStack.peek().equals("*") || operatorStack.peek().equals("/"))) {
						returnString += operatorStack.pop();
					}
					operatorStack.push(strings.get(i));
				break;
				
				case "(" : // ( parentheses and brackets always get pushed
				case "{" :
					operatorStack.push(strings.get(i));
				break;
				
				case ")" : // ) parentheses pop all operators until the ( parenthesis is found
					while(!(operatorStack.peek().equals("("))) {
						returnString += operatorStack.pop();
					}
					operatorStack.pop(); //Pop the extra ( when it is found
				break;
					
				case "}" : // } braces pop all operators until the { brace is found
					while(!(operatorStack.peek().equals("{"))) {
						returnString += operatorStack.pop();
					}
					operatorStack.pop(); //Pop the extra { when it is found
				break;
				
				case "]" : // ] brackets pop all operators until the [ bracket is found
					while(!(operatorStack.peek().equals("["))) {
						returnString += operatorStack.pop();
					}
					operatorStack.pop(); //Pop the extra [ when it is found
				break;
				
				//Anything that isn't an operator is by default an operand
				default:
					returnString += strings.get(i);
			} //End for
			}
			
			//If the above operations cause the Stack to underflow, we can assume that the
			//input string was improperly formatted
			catch (StackUnderflowException e) {
				throw new InvalidNotationFormatException();
			}
		}
		
		//When the for loop is complete, empty what remains in the Stack
		while(!operatorStack.isEmpty()) returnString += operatorStack.pop();
		
		return returnString;
	}
	
	/**
	 * Convert an postfix expression to an infix expression
	 * @param postfix the postfix-notated expression to an infix-notated expression
	 * @return the infix-notated expression
	 * @throws InvalidNotationFormatException if the input String is found to be invalid
	 */
	public static String convertPostfixToInfix(String postfix) throws InvalidNotationFormatException{
		
		//Check that the expression contains only valid characters
		if(!hasValidCharacters(postfix)) throw new InvalidNotationFormatException(
				"The expression may only contain numbers/letters, brackets/parens, and +, -, *, /, *");
		
		//Check for braces. Postfix notation does not use parentheses or brackets
		for(int i = 0; i < postfix.length(); i++) {
			if (isBrace(postfix.charAt(i)) || isClosedBrace(postfix.charAt(i))) {
				throw new InvalidNotationFormatException(
						"Postfix notation should not have brackets, braces, or parentheses");
			}
		}
		
		//Convert the single String to a String array. 
		String[] strArray = postfix.split("");
		ArrayList<String> strings = new ArrayList<>();
		for (String str : strArray) {
			strings.add(str);
		}
		
		//Create Stack for operands and variables for operation
		MyStack<String> operandStack = new MyStack<>();
		String val1, val2;
		
		/*
		Iterate through the String array. Operands will be added to their Stack.
		As operators are found, pop the previous two operands and add
		on to the return String to form the infix expression
		*/
		for(int i = 0; i < strings.size(); i++) {
			
			try {
				switch (strings.get(i)) {
				
				//Skip whitespace
				case " " :
					break;
				
				case "+" : //Add and subtract operators operate on the previous two items in the stack
				case "-" : //To preserve precedence, the result will always be put into parentheses
					val1 = operandStack.pop();
					val2 = operandStack.pop();
					operandStack.push("(" + val2 + strings.get(i) + val1 + ")"); //Result pushes back onto stack
					break;
					
				case "*" : //Multiply and divide operators operate on the previous two items in the stack
				case "/" : //Precedence is automatically preserved in this manner
					val1 = operandStack.pop();
					val2 = operandStack.pop();
					operandStack.push("(" + val2 + strings.get(i) + val1 + ")"); //Result pushes back onto stack
					break;
					
				case "^" : //Exponent operator operates on the previous two items in the stack
					val1 = operandStack.pop();
					val2 = operandStack.pop();
					operandStack.push(val2 + "^" + val1); //Result pushes back onto stack
					break;
					
				default : //Operands go onto the operand stack in the order they are encountered
					operandStack.push(strings.get(i));
					break;
				}
			}
			
			//If a pop is attempted on an empty operand stack, the input expression was improperly formatted
			catch (StackUnderflowException e) {
				throw new InvalidNotationFormatException();
			}
		}
		
		//We are left with a single Node on the stack, which is our final, full expression.
		//If there are multiple Nodes on the stack, we've run into a notation issue
		if (operandStack.size() > 1) throw new InvalidNotationFormatException();
		else return operandStack.peek();
		
	}
	
	/**
	 * Read an infix-notated expression and evaluate it numerically
	 * @param infixExpr the infix-notated expression for evaluation
	 * @return the evaluated expression value
	 * @throws InvalidNotationFormatException if the input String is found to be invalid
	 */
	public static double evaluateInfixExpression(String infixExpr) throws InvalidNotationFormatException{

		//Confirm that the expression consists of only valid characters
		if(!hasValidCharacters(infixExpr)) throw new InvalidNotationFormatException(
				"The expression may only contain numbers/letters, brackets/parens, and +, -, *, /, *");
		
		//Confirm that the expression does not have any unbalanced braces or parens
		if(!isBalanced(infixExpr)) throw new InvalidNotationFormatException(
				"The expression contains unbalanced parentheses or brackets");
		
		//Confirm that the expression does not have any operators in sequence (ex: a++b)
		if(hasSequentialOperators(infixExpr)) throw new InvalidNotationFormatException(
				"The expression may not contain sequential operators");
		
		
		//Create Stacks for holding operators and operands
		MyStack<String> operands = new MyStack<>();
		MyStack<String> operators = new MyStack<>();
		
		//Convert String to a character array
		char[] chars = infixExpr.toCharArray();
		
		//Create ArrayList for holding String tokens
		ArrayList<String> strings = new ArrayList<>();
		for (char ch : chars) strings.add(Character.toString(ch));
		
		//Create String variables for holding individual operands
		String val1, val2;
		
		//Iterate through the ArrayList of tokens and separate into Stacks
		for(String str : strings) {
			
			//Using a try statement as all operations require two pops of the operand stack.
			//Any invalid notations may be caught when the double-pop throws a StackUnderflowException
			try {
			switch (str) {
				
				//Skip whitespace
				case " " : 
					break;
			
				case "+" : //+ and - characters operate on the top two operands of the operand stack
				case "-" :
					if (operators.isEmpty()) operators.push(str);
					else {
						while (!operators.isEmpty() && (!isBrace(operators.peek()))) {
							val1 = operands.pop();
							val2 = operands.pop();
							double result = operate(val1, val2, operators.pop());
							operands.push(Double.toString(result));
						}
						operators.push(str);
					}
					break;
					
				case "*" : //* and / operators operate on the top two operands of the operand stack
				case "/" : //unless there is a lower-precedence operator already on the operator stack
					if (operators.isEmpty() || isBrace(operators.peek())) operators.push(str);
					else {
						while (!operators.isEmpty() && !(operators.peek().equals("+") || operators.peek().equals("-"))) {
							val1 = operands.pop();
							val2 = operands.pop();
							double result = operate(val1, val2, operators.pop());
							operands.push(Double.toString(result));
						}
						operators.push(str);
					}
					break;
					
				case "^" : //^ gets pushed onto the operator stack
					operators.push(str);
					break;
				
				case "(" : //Open parens and braces go onto the operator stack
				case "{" :
				case "[" :
					operators.push(str);
					break;
			
				case ")" : //When a closed paren or brace is found, go through the operators in the stack and operate
				case "}" : //on the top two operands of the operand stack. The result goes on top of the operand stack
				case "]" :
					while (!isBrace(operators.peek())) {
						String operator = operators.pop();
						val1 = operands.pop();
						val2 = operands.pop();
						double result = operate(val1, val2, operator);
						operands.push(Double.toString(result));
					}
					operators.pop(); //Pop the open brace from the stack once found
					break;
				//Operands get pushed onto the stack as they are encountered
				default:
					operands.push(str);
					break;
			} //end switch
			} //end try
		catch (StackUnderflowException e) {
			throw new InvalidNotationFormatException();
			}
		}  // end for
		//Once the entire ArrayList is read through, empty the remaining operators in the Stack
		while (!operators.isEmpty()) {
			val1 = operands.pop();
			val2 = operands.pop();
			double result = operate(val1, val2, operators.pop());
			operands.push(Double.toString(result));
		}
		
		//When everything has been run, we are left with a single item in the operands stack. This is our answer.
		//If there is an extra operand on the stack that wasn't used, we've run into an notation problem.
		if (operands.size() > 1) throw new InvalidNotationFormatException();
		return Double.parseDouble(operands.peek());
	}
	
	/**
	 * Read a postfix-notated expression and evaluate it numerically
	 * @param postfixExpr the postfix-notated expression for evaluation
	 * @return the evaluated expression value
	 * @throws InvalidNotationFormatException if the input String is invalid
	 */
	public static double evaluatePostfixExpression(String postfixExpr) throws InvalidNotationFormatException{
		
		//Confirm that the expression consists of only valid characters
		if(!hasValidPostfixCharacters(postfixExpr)) throw new InvalidNotationFormatException(
				"The expression may only contain numbers/letters, and +, -, *, /, *");
		
		//Check for braces. Postfix notation does not use parentheses or brackets
		for(int i = 0; i < postfixExpr.length(); i++) {
			if (isBrace(postfixExpr.charAt(i)) || isClosedBrace(postfixExpr.charAt(i))) {
				throw new InvalidNotationFormatException(
						"Postfix notation should not have brackets, braces, or parentheses");
			}
		}
		
		//Convert String to a character array
		char[] chars = postfixExpr.toCharArray();
		
		//Create ArrayList for holding String tokens
		ArrayList<String> strings = new ArrayList<>();
		
		//Convert the array from characters to Strings
		for (char ch : chars) strings.add(Character.toString(ch));
		
		
		//Create Stack for operands and variables for operation
		MyStack<Double> operandStack = new MyStack<>();
		double thisVal, nextVal, result;
		
		for (String str : strings) {
			switch (str) {

			//Skip whitespace
			case " " : break;
			case "+" : case "-" : case "*" : case "/" : case "^" :
				
				//If a pop is attempted and there were no preceding operands, the entered expression was improperly formatted
				try {
					thisVal = operandStack.pop();
					nextVal = operandStack.pop();
				}
				//Pop operations will underflow the Stack if the input string was improperly formatted
				catch (StackUnderflowException e) {
					throw new InvalidNotationFormatException();
				}
				
				//Perform operations as operators are found
				switch (str) {
					case "+" : result = nextVal + thisVal;
					operandStack.push(result);
					break;
					case "-" : result = nextVal - thisVal;
					operandStack.push(result);
					break;
					case "*" : result = nextVal * thisVal;
					operandStack.push(result);
					break;
					case "/" : result = nextVal / thisVal;
					operandStack.push(result);
					break;
					case "^" : result = Math.pow(nextVal, thisVal);
					operandStack.push(result);
					break;
				}
				break;
			default:
				//safe parse because hasValidCharacters() and the above switch statement have already checked for invalid characters
				operandStack.push(Double.parseDouble(str));
				break;
			}
		}
		
		//We are left with a single operand left on the stack, which is our final answer.
		//If there are additional oeprands left that were not operated upon, then we've run into
		//a notation problem
		if(operandStack.size() > 1) throw new InvalidNotationFormatException();
		return operandStack.peek();
	}
	
	/**
	 * Check if an expression contains only valid characters. Checks for numbers, variables (alphabetic characters),
	 * operators and braces
	 * @param str the expression to be validated
	 * @return true if the expression is valid. False if invalid characters are found
	 */
	public static boolean hasValidCharacters(String str) {
		char[] chars = str.toCharArray();
		
		for (char ch : chars) {
			
			Boolean valid = false;
			
			//Skip whitespace and decimals
			if (Character.isWhitespace(ch) || ch == '.') continue;
			
			//Check if character is an operand
			else if (Character.isDigit(ch) || Character.isAlphabetic(ch)) { 
				valid = true;
			}
			
			//Check if character is an operator
			else if (ch == '+' || ch == '-' || ch == '*' || ch == '/' || ch == '^') {
				valid = true;
			}
			
			//Check if character is a paren or bracket
			else if (ch == '(' || ch == ')' || ch == '{' || ch == '}' || ch == '[' || ch == ']') {
				valid = true;
			}
			
			if (valid == false) return false;
		}
		
		return true;
	}
	
	/**
	 * Check if a postfix expression has invalid characters. Functions identically to hasValidCharacters
	 * except postfix expressions may not contain brackets or parentheses
	 * @param str the String expression to be evaluated
	 * @return true if the expression contains valid characters, invalid if there are invalid characters found
	 */
	public static boolean hasValidPostfixCharacters(String str) {
		char[] chars = str.toCharArray();
		Boolean valid = false;
		
		for (char ch : chars) {
			
			//Skip whitespace and decimals
			if (Character.isWhitespace(ch) || ch == '.') continue;
			
			//Check if character is an operand
			else if (Character.isDigit(ch) || Character.isAlphabetic(ch)) { 
				valid = true;
			}
			
			//Check if character is an operator
			else if (ch == '+' || ch == '-' || ch == '*' || ch == '/' || ch == '^') {
				valid = true;
			}
			
		}
		
		//Return whether a valid character was found or not
		return valid;
	}
	
	/**
	 * Determine if an entered expression is balanced (as all things should be), with parentheses,
	 * braces, and brackets properly paired up.
	 * Method DOES NOT check if other characters are valid
	 * @param str the expression to be checked for balance
	 * @return true if the expression is properly balanced, false if otherwise
	 */
	public static boolean isBalanced(String str) {
		
		//Create a Stack for storing braces/brackets/parens as we find them
		MyStack<Character> openParenStack = new MyStack<>();
		
		//Convert the String to a char array
		char[] chars = str.toCharArray();
		
		//Create sentinel Boolean value for returning
		boolean isBalanced = true;
		
		for(char ch : chars) {
			switch (ch) {
				
				//Push any open (left) brace to the stack
				case '(' : 
				case '{' :
				case '[' :
					openParenStack.push(ch);
					break;
				
				//If a closed (right) brace is found, its opposite must be on the top of the Stack.
				//If that's not the case, then the expression is not balanced.
				case ')' :
					if (openParenStack.isEmpty() || !openParenStack.pop().equals('(')) isBalanced = false;
					break;
				case '}' :
					if (openParenStack.isEmpty() || !openParenStack.pop().equals('{')) isBalanced = false;
					break;
				case ']' :
					if (openParenStack.isEmpty() || !openParenStack.pop().equals('[')) isBalanced = false;
					break;
					
				//Non-brace characters are ignored	
				default: 
					break;
			}
		}
		
		return isBalanced;
	}
	
	/**
	 * Check if a String contains two operators in sequence. Used to validate infix expressions
	 * @param str the String to examine
	 * @return true if the String has sequential operators. False if the string is properly formatted
	 */
	public static boolean hasSequentialOperators(String str) {
		//Remove whitespace present in the string
		str.replace(" ", "");
		
		for (int i = 0; i < str.length() - 1; i++) {
			
			//Skip over operand characters
			if (Character.isDigit(str.charAt(i))) continue;
			else if (Character.isAlphabetic(str.charAt(i))) continue;
			
			//Skip over closed braces
			else if (isClosedBrace(str.charAt(i))) continue;
			
			//Skip over brace pairs
			//Note: this does not confirm the braces are balanced. Use isBalanced(str) for balance check
			else if (isBrace(str.charAt(i)) && (isBrace(str.charAt(i+1)) || isClosedBrace(str.charAt(i+1)))) continue;
			
			//If the character falls under none of the above rules, it must be an open brace or an operator.
			//As such, it may not be followed by another operator or closed brace (for nonbrace operators)
			else {
				if (isOperator(str.charAt(i+1)) || isClosedBrace(str.charAt(i+1))) return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Determine if a given character is an operator symbol. 
	 * @param ch the character to be examined
	 * @return true if the character is an operator, false if not
	 */
	public static boolean isOperator (char ch) {
		if (ch == '+' || ch == '-' || ch == '*' || ch == '/' || ch == '^') return true;
		
		//Return false if no match found
		else return false;
	}
	
	/**
	 * Determine if a passed String is a left (open) brace or paren character
	 * @param str the String to be examined
	 * @return true if the passed String is a brace or parenthesis, false otherwise
	 */
	public static boolean isBrace(String str) {
		if (str.equals("(") || str.equals("{") || str.equals("[")) return true;
		
		//Return false if no match found
		else return false;
	}
	
	/**
	 * Determine if a passed character is a left (open) brace or paren character
	 * @param ch the character to be examined
	 * @return true if the passed character is a left brace or parenthesis, false otherwise
	 */
	public static boolean isBrace(char ch) {
		if (ch == '(' || ch == '{' || ch == '[') return true;
		
		//Return false if no match
		else return false;
	}
	
	/**
	 * Determine if a passed character is a right (closed) brace or paren character
	 * @param ch the character to be examined
	 * @return true if the passed character is a right brace or parenthesis, false otherwise
	 */
	public static boolean isClosedBrace(char ch) {
		if (ch == ')' || ch == '}' || ch == ']') return true;
		
		//Return false if no match
		else return false;
	}
	
	/**
	 * Evaluate a two-operand expression
	 * @param stringA the first operand
	 * @param stringB the second operand
	 * @param operator the operator indicating the function to be performed
	 * @throws RuntimeException if an incorrect operator character is passed to the method
	 * @return the result of the performed function
	 */
	public static double operate(String stringA, String stringB, String operator) throws ArithmeticException{
		double result = 0;
		
		double a = Double.parseDouble(stringA);
		double b = Double.parseDouble(stringB);
		
		//Operate on the two variables per the type of operator passed
		switch (operator) {
			case "+" : 
				result = a + b;
				break;
			case "-" :
				result = b - a;
				break;
			case "*" :
				result = a * b;
				break;
			case "/" :
				result = b / a;
				break;
			case "^" :
				result = Math.pow(b,  a);
			default :
				throw new ArithmeticException ("Incorrect operator passed to operate method");
		}
		
		return result;
	}
}

/**
 * An Exception class for when the program detects that it has been passed an improperly-formatted
 * String, either in postfix or infix notation.
 * 
 * The individual methods elsewhere in the class can detect specific cases and return relevant messages
 * for the client to correct their notation. For general catch-all throws, a default message is provided.
 * 
 * @author Mike Meyers
 *
 */
@SuppressWarnings("serial")
class InvalidNotationFormatException extends RuntimeException {
	
	/**
	 * Constructor with default message
	 */
	public InvalidNotationFormatException() {
		super("The entered expression contains an invalid notation and cannot be processed.");
	}
	
	/**
	 * constructor with a custom message
	 * @param message The message to pass along with the thrown InvalidNotationFormatException
	 */
	public InvalidNotationFormatException(String message) {
		super(message);
	}
}
