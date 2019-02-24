import java.util.ArrayList;

public class MyQueue<T> implements QueueInterface<T>{
	
	private Node firstNode, lastNode;
	private int nodeCount;
	private final int MAX_SIZE;
	
	/**
	 * Create an empty Queue containing no data. Queue will have maximum size.
	 */
	public MyQueue() {
		firstNode = null;
		lastNode = null;
		nodeCount = 0;
		MAX_SIZE = Integer.MAX_VALUE;
	}
	
	/**
	 * Create an empty Queue containing no data, but with an indicated maximum size.
	 * @param maxVal the maximum size of the Queue
	 */
	public MyQueue(int maxVal) {
		firstNode = null;
		lastNode = null;
		nodeCount = 0;
		MAX_SIZE = maxVal;
	}

	/**
	 * Create a Queue with a single data Object. Queue will have unlimited size.
	 * @param data the Object to be entered into the Queue
	 */
	public MyQueue(T data) {
		Node newNode = new Node(data);
		firstNode = newNode;
		lastNode = newNode;
		nodeCount++;
		MAX_SIZE = Integer.MAX_VALUE;
	}
	
	/**
	 * Create a Queue with a single data Object and indicated maximum size
	 * @param data the Object to be entered into the Queue
	 * @param maxVal the maximum size of the Queue
	 */
	public MyQueue(T data, int maxVal) {
		Node newNode = new Node(data);
		firstNode = newNode;
		lastNode = newNode;
		nodeCount++;
		MAX_SIZE = maxVal;
	}
	
	/**
	 * Determine if the Queue is empty (not containing data)
	 * @return true if the Queue is empty, false if there are Nodes present
	 */
	@Override
	public boolean isEmpty() {
		if (nodeCount == 0) return true;
		else return false;
	}

	/**
	 * Determine if the Queue is full
	 * @return true if the Queue is full, false if it is not full
	 */
	@Override
	public boolean isFull() {
		if(nodeCount == MAX_SIZE) return true;
		else return false;
	}

	/**
	 * Remove the first object from the queue
	 * @return the object T that was the first item in the queue
	 * @throws QueueUnderflowException if there is no data to return (if the queue is empty)
	 */
	@Override
	public T dequeue() throws QueueUnderflowException {
		//Check if the Queue is empty -- throw an Exception if so
		if(this.isEmpty()) throw new QueueUnderflowException();
		
		else {
			//Create temp object to hold return data
			T returnData = firstNode.data;
			
			//Remove the existing first Node by promoting the second Node to the first position
			firstNode = firstNode.nextNode;
			
			//Decrement the Node counter to reflect removal
			nodeCount--;
			
			//Return the data formerly contained in the first node
			return returnData;
		}
	}

	/**
	 * Determine the current size of the Queue
	 * @return the size of the Queue (number of Nodes in Queue)
	 */
	@Override
	public int size() {
		return nodeCount;
	}

	/**
	 * Add an object of type T to the Queue
	 * @param e the object of type T to be added to the Queue
	 * @throws QueueOverflowException if the Queue is full
	 */
	@Override
	public boolean enqueue(T e) throws QueueOverflowException {
		//Check if the Queue is currently full. If so, throw Exception
		if(this.isFull()) throw new QueueOverflowException();
		
		//Wrap the parameter Object in a Node. The Node's nextNode will be null
		Node newNode = new Node (e);
		
		//If there are no Nodes yet, the new Node becomes the first Node in line
		if(this.isEmpty()) {
			firstNode = newNode;
			lastNode = newNode;
			nodeCount++;
		}
		
		//If there are Nodes present, the new Node becomes the last Node in line
		else {
			lastNode.nextNode = newNode;
			lastNode = newNode;
			nodeCount++;
		}
		
		//Return 
		return true;
	}

	/**
	 * Retrieve all data stored in the Queue as a single String
	 * @param delimiter the String to be used to separate the data retrieved from each object
	 * @return the data from each object in the Queue, delimited by the indicated String
	 */
	@Override
	public String toString(String delimiter) {
		//Create empty String object for returning
		String returnString = "";
		
		//Create Node iterator for reading data
		Node iteratorNode = firstNode;
		
		//Starting with the first Node in the Queue,
		//Add each Node's data to the return, separated by the specified delimited
		for(int i = 0; i < this.size()-1; i++) {
			returnString += iteratorNode.data.toString() + delimiter;
			iteratorNode = iteratorNode.nextNode;
		}
		
		//Add the last Node's data without a terminating delimiter
		returnString+=iteratorNode.data.toString();
		
		//Return the completed String
		return returnString;
	}

	/**
	 * Fill the Queue with elements from an ArrayList. If there are already objects in the Queue,
	 * the ArrayList elements will enter the Queue from the back
	 * @param list the ArrayList of elements to be added to the Queue
	 */
	@Override
	public void fill(ArrayList<T> list){
		for (int i = 0; i < list.size(); i++) {
			
			//Copy each element in the ArrayList and add it to the Queue
			T newElement = list.get(i);
			this.enqueue(newElement);
		}
		
	}
	
	/**
	 * Return a String representation of the data in each Node of the Queue
	 * @return a single String representing the entire Queue
	 */
	@Override
	public String toString() {
		Node iteratorNode = firstNode;
		String returnString = "";
		
		//Iterate throught the Queue and add each Node's Data toString() to the returnString
		for(int i = 0; i < this.size(); i++) {
			returnString += iteratorNode.data.toString();
			iteratorNode = iteratorNode.nextNode;
		}
		
		return returnString;
		
	}
	
	/**
	 * A class for wrapping data to be entered into the Queue
	 * @author Mike Meyers
	 *
	 */
	private class Node {
		private T data; //The data to be wrapped
		private Node nextNode; //The next Node in line
		
		/**
		 * Create a Node for wrapping a given piece of data
		 * @param data the data to be contained in the Node
		 */
		public Node(T data) {
			this.data = data;
			this.nextNode = null;
		}

	}
}

/**
 * An Exception class for when an operation tries to add more data to the Queue
 * than is allowable
 * @author Mike Meyers
 *
 */
@SuppressWarnings("serial")
class QueueOverflowException extends RuntimeException{
	public QueueOverflowException() {
		super("The queue is full and the operation may not be completed");
	}
}

/**
 * An Exception class for when an operation tries to pull data that does not
 * exist in the Queue
 * @author Mike Meyers
 *
 */
@SuppressWarnings("serial")
class QueueUnderflowException extends RuntimeException {
	public QueueUnderflowException() {
		super("The queue is currently empty and the operation may not be completed");
	}
}
