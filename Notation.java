import java.util.ArrayList;

public class Notation {
	
	/**
	 * Default constructor TODO: add more if needed
	 */
	public Notation() {
	}
	
	/**
	 * Convert an infix expression to a postfix expression
	 * @param infix the infix-notated expression to a postfix-notated expression
	 * @return the postfix-notated expression
	 */
	public static String convertInfixToPostfix(String infix) throws ArithmeticException{
		if(!hasValidCharacters(infix)) throw new InvalidNotationFormatException(
				"The expression may only contain numbers/letters, brackets/parens, and +, -, *, /, *");
		
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
			//TODO: this is wrong
			case "+" :
			case "-" :
				String val1 = operandStack.pop();
				String val2 = operandStack.pop();
				operandStack.push("(" + val2 + " " + strings.get(i) + " " + val1 + ")");
				break;
				
			case "*" :
			case "/" :
				val1 = operandStack.pop();
				val2 = operandStack.pop();
				operandStack.push(val2 + " " + strings.get(i) + " " + val1);
				break;
				
			case "^" :
				val1 = operandStack.pop();
				val2 = operandStack.pop();
				operandStack.push(val2 + " ^ " + val1);
				break;
				
			default :
				operandStack.push(strings.get(i));
				break;
			}
		}
		
		return operandStack.peek();
		
	}
	
	/**
	 * Read an infix-notated expression and evaluate it numerically
	 * @param infixExpr the infix-notated expression for evaluation
	 * @return the evaluated expression value
	 */
	public static double evaluateInfixExpression(String infixExpr) {
		
	}
	
	/**
	 * Read a postfix-notated expression and evaluate it numerically
	 * @param postfixExpr the postfix-notated expression for evaluation
	 * @return the evaluated expression value
	 */
	public static double evaluatePostfixExpression(String postfixExpr) {
		if(!hasValidCharacters(postfixExpr)) throw new IllegalArgumentException(
				"The expression may only contain numbers/letters, brackets/parens, and +, -, *, /, *");
		
		if(!isBalanced(postfixExpr)) throw new UnbalancedExpressionException();
		
		//Convert String to a character array
		String[] strings = postfixExpr.split(" ");
		
		
		//Create separate Stacks for operators and operands
		MyStack<Double> operandStack = new MyStack<>();
		double result;
		
		for (String str : strings) {
			switch (str) {
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
		return operandStack.pop();
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
