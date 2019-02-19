import java.util.ArrayList;

/**
 * Convert mathematical expressions in infix notation (a+b) to postfix notation (ab+) and vice-versa.
 * Includes methods for evaluating expressions, as well.
 * 
 * Version 2.0 - includes support for multiple-digit numbers and decimals 
 * (operands must be delineated by whitespace when in postfix notation)
 * 
 * @author Mike Meyers
 * @version 2.0
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
	 */
	public static String convertInfixToPostfix(String infix) throws ArithmeticException{
		
		//Confirm that the expression contains only valid characters
		if(!hasValidCharacters(infix)) throw new InvalidNotationFormatException(
				"The expression may only contain numbers/letters, brackets/parens, and +, -, *, /, *");
		
		//Confirm that the expression does not contain unbalanced parentheses or brackets
		if(!isBalanced(infix)) throw new UnbalancedExpressionException();
		
		/*
		Convert the single String to a String array. 
		First, by splitting the String into characters,
		Then checking each character for an operator or operand.
		This separates out multiple-digit and decimal operands as
		Full numbers, instead of single characters
		*/
		char[] chars = infix.toCharArray();
		ArrayList<String> strings = new ArrayList<>();
		String nextDoubleString = "";
		
		//Look through each character of the char array
		for (char ch : chars) {
			//Pull out numbers and decimal points
			if(Character.isDigit(ch) || ch == '.') {
				nextDoubleString += ch;
			}

			//If an operator is encountered, add the previous number
			//as a discrete String to the String array
			else {
				if(nextDoubleString.length() > 0) {
					strings.add(nextDoubleString);
					nextDoubleString = "";
				}
				
				//Add the operator to the String array
				strings.add(Character.toString(ch)); 
			}
		}
		
		//At conclusion of for loop, add the last Double in sequence
		if(nextDoubleString.length() > 0) {
			strings.add(nextDoubleString);
			nextDoubleString = "";
		}
		
		//Create output String
		String returnString = "";
		
		//Create separate Stack for operators
		MyStack<String> operatorStack = new MyStack<>();
		
		//Iterate through the String array and sort the tokens
		for (int i = 0; i < strings.size(); i++) {
			switch (strings.get(i)) {
				case " " : //skip whitespace characters
					break;
				
				case "^" : //exponents always get pushed
					operatorStack.push(strings.get(i));
					break;
				
				case "+" : // + and - operators have lowest precedence, so they get pushed unless there are already + or -
				case "-" : // operators to begin with
					while(!operatorStack.isEmpty() && !isBrace(operatorStack.peek())) {
						returnString += operatorStack.pop() + " ";
					}
					operatorStack.push(strings.get(i));
				break;
				
				case "*" : // *, /, +, and - operators will all push the stack. Only ^ operators have higher precedence
				case "/" :
					while(!operatorStack.isEmpty() && operatorStack.peek().equals("^")) {
						returnString += operatorStack.pop() + " ";
					}
					while(!operatorStack.isEmpty() && (operatorStack.peek().equals("*") || operatorStack.peek().equals("/"))) {
						returnString += operatorStack.pop() + " ";
					}
					operatorStack.push(strings.get(i));
				break;
				
				case "(" : // ( parentheses and brackets always get pushed
				case "{" :
					operatorStack.push(strings.get(i));
				break;
				
				case ")" : // ) parentheses pop all operators until the ( parenthesis is found
					while(!(operatorStack.peek().equals("("))) {
						returnString += operatorStack.pop() + " ";
					}
					operatorStack.pop(); //Pop the extra ( when it is found
				break;
					
				case "}" : // } braces pop all operators until the { brace is found
					while(!(operatorStack.peek().equals("{"))) {
						returnString += operatorStack.pop() + " ";
					}
					operatorStack.pop(); //Pop the extra { when it is found
				break;
				
				case "]" : // ] brackets pop all operators until the [ bracket is found
					while(!(operatorStack.peek().equals("["))) {
						returnString += operatorStack.pop() + " ";
					}
					operatorStack.pop(); //Pop the extra [ when it is found
				break;
				
				//Anything that isn't an operator is by default an operand
				default:
					returnString += strings.get(i) + " ";
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
	 */
	public static String convertPostfixToInfix(String postfix) {
		
		//Check that the expression contains only valid characters
		if(!hasValidCharacters(postfix)) throw new InvalidNotationFormatException(
				"The expression may only contain numbers/letters, brackets/parens, and +, -, *, /, *");
		
		//Check that the expression is balanced in terms of delimiters
		if(!isBalanced(postfix)) throw new UnbalancedExpressionException();
		
		/*
		Convert the single String to a String array. 
		First, by splitting the String into characters,
		Then checking each character for an operator or operand.
		This separates out multiple-digit and decimal operands as
		full numbers, instead of single characters
		*/
		char[] chars = postfix.toCharArray();
		ArrayList<String> strings = new ArrayList<>();
		String nextDoubleString = "";
		
		//Look through each character of the char array
		for (char ch : chars) {
			//Pull out numbers and decimal points
			if(Character.isDigit(ch) || ch == '.') {
				nextDoubleString += ch;
			}

			//If an operator is encountered, add the previous number
			//as a discrete String to the String array
			else {
				if(nextDoubleString.length() > 0) {
					strings.add(nextDoubleString);
					nextDoubleString = "";
				}
				
				//Add the operator to the String array
				strings.add(Character.toString(ch)); 
			}
		}
		
		//At conclusion of for loop, add the last Double in sequence (if it exists)
		if(nextDoubleString.length() > 0) {
			strings.add(nextDoubleString);
			nextDoubleString = "";
		}
		
		//Create Stack for operands
		MyStack<String> operandStack = new MyStack<>();
		
		/*
		Iterate through the String array. Operands will be added to their Stack.
		As operators are found, pop the previous two operands and add
		on to the return String to form the infix expression
		*/
		for(int i = 0; i < strings.size(); i++) {
			switch (strings.get(i)) {
			
			//Skip whitespace
			case " " :
				break;
			
			case "+" : //Add and subtract operators operate on the previous two items in the stack
			case "-" : //To preserve precedence, the result will always be put into parentheses
				String val1 = operandStack.pop();
				String val2 = operandStack.pop();
				operandStack.push("(" + val2 + " " + strings.get(i) + " " + val1 + ")"); //Result pushes back onto stack
				break;
				
			case "*" : //Multiply and divide operators operate on the previous two items in the stack
			case "/" : //Precedence is automatically preserved in this manner
				val1 = operandStack.pop();
				val2 = operandStack.pop();
				operandStack.push(val2 + " " + strings.get(i) + " " + val1); //Result pushes back onto stack
				break;
				
			case "^" : //Exponent operator operates on the previous two items in the stack
				val1 = operandStack.pop();
				val2 = operandStack.pop();
				operandStack.push(val2 + " ^ " + val1); //Result pushes back onto stack
				break;
				
			default : //Operands go onto the operand stack in the order they are encountered
				operandStack.push(strings.get(i));
				break;
			}
		}
		
		//We are left with a single Node on the stack, which is our final, full expression
		return operandStack.peek();
		
	}
	
	/**
	 * Read an infix-notated expression and evaluate it numerically
	 * @param infixExpr the infix-notated expression for evaluation
	 * @return the evaluated expression value
	 */
	public static double evaluateInfixExpression(String infixExpr) {

		//Confirm that the expression consists of only valid characters
		if(!hasValidCharacters(infixExpr)) throw new IllegalArgumentException(
				"The expression may only contain numbers/letters, brackets/parens, and +, -, *, /, *");
		
		//Confirm that the expression does not have any unbalanced braces or parens
		if(!isBalanced(infixExpr)) throw new UnbalancedExpressionException();
		
		
		//Create Stacks for holding operators and operands
		MyStack<String> operands = new MyStack<>();
		MyStack<String> operators = new MyStack<>();
		
		//Convert String to a character array
		char[] chars = infixExpr.toCharArray();
		
		//Create ArrayList for holding String tokens
		ArrayList<String> strings = new ArrayList<>();
		
		//Create empty String for building operands that are found
		String nextDoubleString = "";
		
		//Look through each character of the char array
		for (char ch : chars) {
			//Pull out numbers and decimal points
			if(Character.isDigit(ch) || ch == '.') {
				nextDoubleString += ch;
			}

			//If an operator is encountered, add the previous number
			//as a discrete String to the String array
			else {
				if(nextDoubleString.length() > 0) {
					strings.add(nextDoubleString);
					nextDoubleString = "";
				}
				
				//Add the operator to the String array
				strings.add(Character.toString(ch)); 
			}
		}
		
		//At conclusion of for loop, add the last Double in sequence
		if(nextDoubleString.length() > 0) {
			strings.add(nextDoubleString);
			nextDoubleString = "";
		}
		
		//Iterate through the ArrayList of tokens and separate into Stacks
		for(String str : strings) {
			switch (str) {
				
				//Skip whitespace
				case " " : 
					break;
			
				case "+" :
				case "-" :
					if (operators.isEmpty()) operators.push(str);
					else {
						while (!operators.isEmpty() && (!isBrace(operators.peek()))) {
							String val1 = operands.pop();
							String val2 = operands.pop();
							double result = operate(val1, val2, operators.pop());
							operands.push(Double.toString(result));
						}
						operators.push(str);
					}
					break;
					
				case "*" :
				case "/" :
					if (operators.isEmpty()) operators.push(str);
					else {
						while (!operators.isEmpty() && !(operators.peek().equals("+") || operators.peek().equals("-"))) {
							String val1 = operands.pop();
							String val2 = operands.pop();
							double result = operate(val1, val2, operators.pop());
							operands.push(Double.toString(result));
						}
						operators.push(str);
					}
					break;
					
				case "^" :
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
						String val1 = operands.pop();
						String val2 = operands.pop();
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
		} // end for
		
		//Once the entire ArrayList is read through, empty the remaining operators in the Stack
		while (!operators.isEmpty()) {
			String val1 = operands.pop();
			String val2 = operands.pop();
			double result = operate(val1, val2, operators.pop());
			operands.push(Double.toString(result));
		}
		
		//When everything has been run, we are left with a single item in the operands stack. This is our answer
		return Double.parseDouble(operands.peek());
	}
	
	/**
	 * Read a postfix-notated expression and evaluate it numerically
	 * @param postfixExpr the postfix-notated expression for evaluation
	 * @return the evaluated expression value
	 */
	public static double evaluatePostfixExpression(String postfixExpr) {
		
		//Confirm that the expression consists of only valid characters
		if(!hasValidPostfixCharacters(postfixExpr)) throw new IllegalArgumentException(
				"The expression may only contain numbers/letters, and +, -, *, /, *");
		
		//Confirm that the expression does not have any unbalanced braces or parens
		if(!isBalanced(postfixExpr)) throw new UnbalancedExpressionException();
		
		//Convert String to a character array
		char[] chars = postfixExpr.toCharArray();
		
		//Create ArrayList for holding String tokens
		ArrayList<String> strings = new ArrayList<>();
		
		//Create empty String for building operands that are found
		String nextDoubleString = "";
		
		//Look through each character of the char array
				for (char ch : chars) {
					//Pull out numbers and decimal points
					if(Character.isDigit(ch) || ch == '.') {
						nextDoubleString += ch;
					}

					//If an operator is encountered, add the previous number
					//as a discrete String to the String array
					else {
						if(nextDoubleString.length() > 0) {
							strings.add(nextDoubleString);
							nextDoubleString = "";
						}
						
						//Add the operator to the String array
						strings.add(Character.toString(ch)); 
					}
				}
				
				//At conclusion of for loop, add the last Double in sequence
				if(nextDoubleString.length() > 0) {
					strings.add(nextDoubleString);
					nextDoubleString = "";
				}
		
		//Create Stack for operands
		MyStack<Double> operandStack = new MyStack<>();
		double result;
		
		for (String str : strings) {
			switch (str) {

			//Skip whitespace
			case " " : break;
			case "+" : case "-" : case "*" : case "/" : case "^" :
				double thisVal = operandStack.pop();
				double nextVal = operandStack.pop();
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
			
			if (valid == false) return false;
		}
		
		return true;
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
					if (!openParenStack.pop().equals('(')) isBalanced = false;
					break;
				case '}' :
					if (!openParenStack.pop().equals('{')) isBalanced = false;
					break;
				case ']' :
					if (!openParenStack.pop().equals('[')) isBalanced = false;
					break;
					
				//Non-brace characters are ignored	
				default: 
					break;
			}
		}
		
		return isBalanced;
	}
	
	/**
	 * Determine if a passed String is a single left (open) brace or paren character
	 * @param str the String to be examined
	 * @return true if the passed String is a left brace or parenthesis, false otherwise
	 */
	public static boolean isBrace(String str) {
		if (str.equals("(") || str.equals("{") || str.equals("[")) return true;
		
		else return false;
	}
	
	/**
	 * Evaluate a two-operand expression
	 * @param stringA the first operand
	 * @param stringB the second operand
	 * @param operator the operator indicating the function to be performed
	 * @return the result of the performed function
	 */
	public static double operate(String stringA, String stringB, String operator) {
		double result = 0;
		
		double a = Double.parseDouble(stringA);
		double b = Double.parseDouble(stringB);
		
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
		}
		
		return result;
	}
}

@SuppressWarnings("serial")
class UnbalancedExpressionException extends ArithmeticException {
	
	public UnbalancedExpressionException() {
		super("The expression is unbalanced. Recheck brackets and parentheses and try again");
	}
	
	public UnbalancedExpressionException(String message) {
		super(message);
	}
}

@SuppressWarnings("serial")
class InvalidNotationFormatException extends RuntimeException {
	
	public InvalidNotationFormatException() {
		super("The entered expression contains an invalid notation and cannot be processed.");
	}
	
	public InvalidNotationFormatException(String message) {
		super(message);
	}
}
