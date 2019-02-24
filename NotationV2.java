import java.util.ArrayList;

/**
 * Convert mathematical expressions in infix notation (a+b) to postfix notation (ab+) and vice-versa.
 * Includes methods for evaluating expressions, as well.
 * 
 * Version 2.0 - includes support for multiple-digit numbers and decimals 
 * (operands must be delineated by whitespace when in postfix notation)
 * 
 * 
 * 
 * @author Mike Meyers
 * @version 2.0
 *
 */
public class NotationV2 {
	
	/**
	 * Default constructor. Object has no variables to initialize.
	 */
	public NotationV2() {
	}
	
	/**
	 * Convert an infix expression to a postfix expression
	 * @param infix the infix-notated expression to a postfix-notated expression
	 * @return the postfix-notated expression
	 * @throws InvalidNotationFormatExceptionV2 if the input format is found to be invalid
	 */
	public static String convertInfixToPostfix(String infix) throws InvalidNotationFormatExceptionV2 {
		
		//Confirm that the expression contains only valid characters
		if(!hasValidCharacters(infix)) throw new InvalidNotationFormatExceptionV2(
				"The expression may only contain numbers/letters, brackets/parens, and +, -, *, /, *");
		
		//Confirm that the expression does not contain unbalanced parentheses or brackets
		if(!isBalanced(infix)) throw new InvalidNotationFormatExceptionV2();
		
		//Confirm that the expression does not contain sequential operators
		if(hasSequentialOperators(infix)) throw new InvalidNotationFormatExceptionV2();
		
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
		
		//Flag if any entered decimal has an extra decimal point
		Boolean hasDecimal = false;
		
		//Look through each character of the char array
		for (char ch : chars) {
			

			//Pull out numbers and decimal points
			if(Character.isDigit(ch) || ch == '.') {
				if(ch == '.') {
					if(hasDecimal == true) throw new InvalidNotationFormatException();
					else hasDecimal = true;
				}
				nextDoubleString += ch;
			}

			//If an operator is encountered, add the previous number
			//as a discrete String to the String array
			else {
				if(nextDoubleString.length() > 0) {
					strings.add(nextDoubleString);
					nextDoubleString = "";
					hasDecimal = false;
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
			} //end for
			}
			
			//If the stack underflows on a pop, we can assume the input expression was improperly formatted
			catch (StackUnderflowException e) {
				throw new InvalidNotationFormatExceptionV2();
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
	 * @throws InvalidNotationFormatException if the input is found to be improperly formatted
	 */
	public static String convertPostfixToInfix(String postfix) throws InvalidNotationFormatExceptionV2{
		
		//Check that the expression contains only valid characters
		if(!hasValidCharacters(postfix)) throw new InvalidNotationFormatExceptionV2(
				"The expression may only contain numbers/letters, brackets/parens, and +, -, *, /, *");
		
		//Check that the expression is balanced in terms of delimiters
		if(!isBalanced(postfix)) throw new InvalidNotationFormatExceptionV2();
		
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
		
		//Flag to verify if a decimal number has an extra decimal point
		boolean hasDecimal = false;
		
		//Look through each character of the char array
		for (char ch : chars) {
			//Pull out numbers and decimal points
			if(Character.isDigit(ch) || ch == '.') {
				if (ch == '.') {
					if (hasDecimal == true) throw new InvalidNotationFormatException();
					else hasDecimal = true;
				}
				nextDoubleString += ch;
			}

			//If an operator is encountered, add the previous number
			//as a discrete String to the String array
			else {
				if(nextDoubleString.length() > 0) {
					strings.add(nextDoubleString);
					nextDoubleString = "";
					hasDecimal = false;
				}
				
				//Add the operator to the String array
				strings.add(Character.toString(ch)); 
			}
		}
		
		//At conclusion of for loop, add the last Double in sequence (if it exists)
		if(nextDoubleString.length() > 0) {
			strings.add(nextDoubleString);
			nextDoubleString = "";
			hasDecimal = false;
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
			
			//If the stack underflows because an operand wasn't present, the input expression was
			//improperly formatted
			catch (StackUnderflowException e) {
				throw new InvalidNotationFormatExceptionV2();
			}
		}
		
		//We are left with a single Node on the stack, which is our final, full expression
		return operandStack.peek();
		
	}
	
	/**
	 * Read an infix-notated expression and evaluate it numerically
	 * @param infixExpr the infix-notated expression for evaluation
	 * @return the evaluated expression value
	 * @throws InvalidNotationFormatExceptionV2 if infixExpr is found to be improperly formatted
	 */
	public static double evaluateInfixExpression(String infixExpr) throws InvalidNotationFormatExceptionV2{

		//Confirm that the expression consists of only valid characters
		if(!hasValidCharacters(infixExpr)) throw new InvalidNotationFormatExceptionV2(
				"The expression may only contain numbers/letters, brackets/parens, and +, -, *, /, *");
		
		//Confirm that the expression does not have any unbalanced braces or parens
		if(!isBalanced(infixExpr)) throw new InvalidNotationFormatExceptionV2();
		
		//Confirm that the expression does not have any sequential operators
		if(hasSequentialOperators(infixExpr)) throw new InvalidNotationFormatExceptionV2();
		
		//Create Stacks for holding operators and operands
		MyStack<String> operands = new MyStack<>();
		MyStack<String> operators = new MyStack<>();
		
		//Convert String to a character array
		char[] chars = infixExpr.toCharArray();
		
		//Create ArrayList for holding String tokens
		ArrayList<String> strings = new ArrayList<>();
		
		//Create empty String for building operands that are found
		String nextDoubleString = "";
		
		//Flag to verify that each decimal number only has one decimal point
		boolean hasDecimal = false;
		
		//Look through each character of the char array
		for (char ch : chars) {
			//Pull out numbers and decimal points
			if(Character.isDigit(ch) || ch == '.') {
				if (ch == '.') {
					if (hasDecimal == true) throw new InvalidNotationFormatException();
					else hasDecimal = true;
				}
				nextDoubleString += ch;
			}

			//If an operator is encountered, add the previous number
			//as a discrete String to the String array
			else {
				if(nextDoubleString.length() > 0) {
					strings.add(nextDoubleString);
					nextDoubleString = "";
					hasDecimal = false;
				}
				
				//Add the operator to the String array
				strings.add(Character.toString(ch)); 
			}
		}
		
		//At conclusion of for loop, add the last Double in sequence
		if(nextDoubleString.length() > 0) {
			strings.add(nextDoubleString);
			nextDoubleString = "";
			hasDecimal = false;
		}
		
		//Iterate through the ArrayList of tokens and separate into Stacks
		try {
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
		}
		
		//If the stack underflows during the above operations, we can assume the input was improperly formatted
		catch (StackUnderflowException e) {
			throw new InvalidNotationFormatExceptionV2();
		}
		
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
	 * @throws InvalidNotationFormatExceptionV2 if the input is found to be improperly formatted
	 */
	public static double evaluatePostfixExpression(String postfixExpr) throws InvalidNotationFormatExceptionV2{
		
		//Confirm that the expression consists of only valid characters
		if(!hasValidPostfixCharacters(postfixExpr)) throw new InvalidNotationFormatExceptionV2(
				"The expression may only contain numbers/letters, and +, -, *, /, *");
		
		//Confirm that the expression does not have any unbalanced braces or parens
		if(!isBalanced(postfixExpr)) throw new InvalidNotationFormatExceptionV2();
		
		//Convert String to a character array
		char[] chars = postfixExpr.toCharArray();
		
		//Create ArrayList for holding String tokens
		ArrayList<String> strings = new ArrayList<>();
		
		//Create empty String for building operands that are found
		String nextDoubleString = "";
		
		//Flag to verify that each decimal only has one decimal point
		boolean hasDecimal = false;
		
		//Look through each character of the char array
				for (char ch : chars) {
					//Pull out numbers and decimal points
					if(Character.isDigit(ch) || ch == '.') {
						if (ch == '.') {
							if (hasDecimal == true) throw new InvalidNotationFormatException();
							else hasDecimal = true;
						}
						nextDoubleString += ch;
					}

					//If an operator is encountered, add the previous number
					//as a discrete String to the String array
					else {
						if(nextDoubleString.length() > 0) {
							strings.add(nextDoubleString);
							nextDoubleString = "";
							hasDecimal = false;
						}
						
						//Add the operator to the String array
						strings.add(Character.toString(ch)); 
					}
				}
				
				//At conclusion of for loop, add the last Double in sequence
				if(nextDoubleString.length() > 0) {
					strings.add(nextDoubleString);
					nextDoubleString = "";
					hasDecimal = false;
				}
		
		//Create Stack for operands and variables for operations
		MyStack<Double> operandStack = new MyStack<>();
		double result, thisVal, nextVal;
		
		for (String str : strings) {
			switch (str) {

			//Skip whitespace
			case " " : break;
			case "+" : case "-" : case "*" : case "/" : case "^" :
				
				try {
					thisVal = operandStack.pop();
					nextVal = operandStack.pop();
				}
				
				//If the pops cause the stack to underflow, the entered expression was invalid
				catch (StackUnderflowException e) {
					throw new InvalidNotationFormatExceptionV2();
				}
				
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
	 * Check if a String contains two operators in sequence. Used to validate infix expressions
	 * @param str the String to examine
	 * @return true if the String has sequential operators. False if the string is properly formatted
	 */
	public static boolean hasSequentialOperators (String str) {
		//Convert String to token array to eliminate whitespace
		String[] array = str.split(" ");
		for (int i = 0; i < array.length - 1; i++) {
			
			//If a token is of length 1, it is an operator, paren, or single-digit operator and can be examined
			if (array[i].length() == 1) {
				char toExamine = array[i].charAt(0);
				char toExamineNext;
				
				//If the next token is a multiple-digit operand, this one can be skipped
				if (array[i+1].length() > 1) continue;
				else toExamineNext = array[i+1].charAt(0);
			
				//Skip over operand characters
				if (Character.isDigit(toExamine)) continue;
				else if (Character.isAlphabetic(str.charAt(i))) continue;
				
				//Skip over closed braces
				else if (isClosedBrace(toExamine)) continue;
				
				//Skip over brace pairs
				//Note: this does not confirm the braces are balanced. Use isBalanced(str) for balance check
				else if (isBrace(array[i]) && (isBrace(toExamineNext) || isClosedBrace(toExamineNext))) continue;
				
				//If the character falls under none of the above rules, it must be an open brace or an operator.
				//As such, it may not be followed by another operator or closed brace (for nonbrace operators)
				else {
					if (isOperator(toExamineNext) || isClosedBrace(toExamineNext)) return true;
				}
			}
		}
		
		//If above tests are passed, there are no sequential operators
		return false;
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
	 * Determine if a passed String is a single left (open) brace or paren character
	 * @param str the String to be examined
	 * @return true if the passed String is a left brace or parenthesis, false otherwise
	 */
	public static boolean isBrace(String str) {
		if (str.equals("(") || str.equals("{") || str.equals("[")) return true;
		
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
class InvalidNotationFormatExceptionV2 extends RuntimeException {
	
	public InvalidNotationFormatExceptionV2() {
		super("The entered expression contains an invalid notation and cannot be processed.");
	}
	
	public InvalidNotationFormatExceptionV2(String message) {
		super(message);
	}
}
