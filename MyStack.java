import java.util.ArrayList;

public class MyStack<T> implements StackInterface<T> {
	
	public Node topNode;
	public final int MAX_SIZE;
	public int nodeCount;
	
	/**
	 * Create a Stack with default values. Stack starts empty with maximum possible size
	 */
	public MyStack() {
		topNode = null;
		MAX_SIZE = Integer.MAX_VALUE;
		nodeCount = 0;
	}
	
	/**
	 * Create a Stack with default values and a predetermined maximum size
	 * @param size the intended maximum size of the Stack
	 */
	public MyStack(int size) {
		topNode = null;
		MAX_SIZE = size;
		nodeCount = 0;
	}
	
	/**
	 * Create a Stack with a single data point. Stack will be set to the maximum possible size
	 * @param data the Object to be entered into the Stack
	 */
	public MyStack(T data) {
		//Wrap the data in a Node
		Node newNode = new Node(data);
		
		//Enter the Node into the stack
		topNode = newNode;
		nodeCount = 1;
		MAX_SIZE = Integer.MAX_VALUE;
		
		
	}

	/**
	 * Create a Stack with a single data point and indicated maximum size
	 * @param data the Object to be entered into the Stack
	 * @param size the intended maximum size of the Stack
	 */
	public MyStack(T data, int size) {
		//Wrap the data in a Node
		Node newNode = new Node(data);
		
		//Enter the Node into the stack
		topNode = newNode;
		nodeCount = 1;
		MAX_SIZE = size;
	}
	
	/**
	 * Determine if the Stack is empty (containing no data)
	 * @return true if the Stack is empty or false if the Stack contains data
	 */
	@Override
	public boolean isEmpty() {
		if(nodeCount == 0) return true;
		else return false;
	}

	/**
	 * Determine if the Stack is full (cannot admit more data)
	 * @return true if the Stack is full, or false if the Stack is not full
	 */
	@Override
	public boolean isFull() {
		if(nodeCount == this.MAX_SIZE) return true; 
		else return false;
	}

	/**
	 * Remove the top Node from the Stack
	 * @return the Object wrapped in the top Node of the Stack
	 * @throws StackUnderflowException if there is no top Node (if the Stack is empty)
	 */
	@Override
	public T pop() throws StackUnderflowException {
		//If the Stack is empty, throw Exception
		if(this.isEmpty()) throw new StackUnderflowException("The stack is empty. The operation may not be completed");
		
		else {
			//Retrieve the Object from the top Node of the Stack 
			T returnData = topNode.data;
			
			//Remove the top Node and promote second Node
			topNode = topNode.nextNode;
			nodeCount--;
			
			//Return retrieved data
			return returnData;
		}
	}

	/**
	 * Retrieve the Object data from the top of the stack without removing the Node
	 * @return the Object wrapped in the top Node of the Stack
	 * @throws StackUnderflowException if there is no top Node (if the Stack is empty)
	 */
	@Override
	public T peek() throws StackUnderflowException {
		//If the Stack is empty, throw Exception
		if(this.isEmpty()) throw new StackUnderflowException("The stack is empty. The operation may not be completed");
		
		else {
			//Retrieve the data from the top Node of the stack
			T returnData = topNode.data;
			return returnData;
		}
	}

	/**
	 * Determine the current size of the Stack
	 * @return the current size of the Stack
	 */
	@Override
	public int size() {
		return nodeCount;
	}

	/**
	 * Add an Object to the top of the Stack
	 * @param e the Object to be added to the Stack
	 * @throws StackOverflowException if there is no more room in the Stack (Stack is full)
	 */
	@Override
	public boolean push(T e) throws StackOverflowException {
		//If the stack is full, throw Exception
		if(this.isFull()) throw new StackOverflowException("The stack is full. The operation may not be completed");
		
		else {
			Node newNode = new Node(e, topNode);
			topNode = newNode;
			nodeCount++;
			return true;
		}
	}

	/**
	 * Create a single String containing all data contained in the stack, starting at the bottom of the Stack.
	 * Each Object's data will be separated by the indicated delimiter String
	 * @param delimiter the String used to separate each Object's data on return
	 * @return a single String of all Stack object data
	 */
	@Override
	public String toString(String delimiter) {
		//Invert the stack by pushing Nodes to a new Stack
		MyStack<T> invertedStack = new MyStack<T>(this.size());
		while(!this.isEmpty()) {
			invertedStack.push(this.pop());
		}
		
		//Create empty String for returning
		String returnString = "";
		
		//Create an iterator for traversing the Stack
		Node iteratorNode = invertedStack.topNode;
		
		//Traverse the Stack and append the individual data Strings to the return String
		for(int i = 0; i < invertedStack.size() - 1; i++) {
			returnString += iteratorNode.data.toString() + delimiter;
			iteratorNode = iteratorNode.nextNode;
		}
		
		//Add the bottom Node's data to the returnString (prevents extra terminating delimiter)
		returnString += iteratorNode.data.toString();
		
		//Restore the original Stack so that it isn't destroyed
		while(!invertedStack.isEmpty()) {
			this.push(invertedStack.pop());
		}
		
		//Return the finished String to the function caller
		return returnString;
	}
	

	/**
	 * Create a single String containing all data contained in the stack, starting with the bottom Node
	 * @return a single String of all Stack object data
	 */
	@Override
	public String toString() {
		//Invert the Stack by pushing Nodes to a new Stack
		MyStack<T> invertedStack = new MyStack<T>(this.size());
		while(!this.isEmpty()) {
			invertedStack.push(this.pop());
		}
		
		//Create empty String for returning
		String returnString = "";
		
		//Create an iterator for traversing the Stack
		Node iteratorNode = invertedStack.topNode;
		
		//Traverse the Stack and append the individual data Strings to the return String
		for(int i = 0; i < invertedStack.size(); i++) {
			returnString += iteratorNode.data.toString();
			iteratorNode = iteratorNode.nextNode;
		}
		
		//Restore the original Stack so that it isn't destroyed
		while(!invertedStack.isEmpty()) {
			this.push(invertedStack.pop());
		}
		
		//Return the finished String to the function caller
		return returnString;
	}

	/**
	 * Populate a Stack with data contained in an existing Arraylist.
	 * The first item in the ArrayList will be the first entered in the Stack (at the bottom)
	 * @param list the ArrayList of data to add to the Stack
	 */
	@Override
	public void fill(ArrayList<T> list) throws StackOverflowException {
		//Iterate through the ArrayList. Copy each item, and add the copy to the Stack
		for(int i = 0; i < list.size(); i++) {
			T newItem = list.get(i);
			this.push(newItem);
		}
		
	}
	
	/**
	 * A class for wrapping data to be entered into the Stack
	 * @author Mike Meyers
	 *
	 */
	private class Node {
		private T data; //The data to be wrapped
		private Node nextNode; //The next Node in line
		
		/**
		 * Create a Node for wrapping a given piece of data. Used as the bottom Node of the stack
		 * @param data the data to be contained in the Node
		 */
		public Node(T data) {
			this.data = data;
			this.nextNode = null;
		}

		/**
		 * Create a Node that points to the next Node in the Stack
		 * @param data the data to be contained in the Node
		 * @param topNode the next Node in the Stack
		 */
		public Node(T data, Node topNode) {
			this.data = data;
			this.nextNode = topNode;
		}

	}
}

/**
 * Exception class for warning the client when they are attempting to access Stack data that doesn't exist
 * @author Mike Meyers
 *
 */
@SuppressWarnings("serial")
class StackUnderflowException extends RuntimeException {
	
	/**
	 * Default StackUnderflowException message
	 */
	public StackUnderflowException() {
		super("The operation may not be performed");
	}
	
	/**
	 * Create a new StackUnderflowException with a specific message
	 * @param e the message to return to the client
	 */
	public StackUnderflowException(String e) {
		super(e);
	}
}


/**
 * Exception class for warning the client when they are attempting to add to a full Stack
 * @author Mike Meyers
 *
 */
@SuppressWarnings("serial")
class StackOverflowException extends RuntimeException {
	
	/**
	 * Default StackOverflow message
	 */
	public StackOverflowException() {
		super("The operation may not be performed;");
	}
	
	/**
	 * Create a new StackoverflowException with a specific message
	 * @param e the message to return to the client
	 */
	public StackOverflowException(String e) {
		super(e);
	}
}


