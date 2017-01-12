import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class Main {
    public static void main(String[] args) throws IOException{

        Quilt q = new Quilt();
        try {
            q.start();
        } catch (IOException ex) {
            System.err.println("Invalid input!");
        }
    }
}

class Quilt {
    //Private Variables
    private int _numTasks, _numPatterns, _numOrders;
    private Pattern[] _patterns;
    private Stack<Pattern> _stack;
    private Queue<String> _orders;

    //Constructor
    public Quilt(){
        this._stack = new Stack<>();
        this._orders = new LinkedList<>();
    }

    //Methods
    // Gets the input of the user to create the needed Quilts
    public void start() throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        // Get the number of tasks
        _numTasks = Integer.parseInt(br.readLine());
        if(!((_numTasks >= 1) && (_numTasks <= 100))) System.err.print("1 <= numTasks <= 100");
        for(int j = 0; j < _numTasks; j++){
            // Get the number of basic patterns
            _numPatterns = Integer.parseInt(br.readLine());
            if(!((_numPatterns >= 1) && (_numPatterns <= 10))) System.err.print("1 <= numPatterns <= 10");

            // Create the list of basic patterns
            _patterns = new Pattern[_numPatterns];

            // Fill the list with basic patterns, always consisting out of 4 ASCII Characters (/, |, \, -, +)
            for(int i = 0; i < _numPatterns; i++){
                Pattern p = new Pattern(new char[][]{br.readLine().toCharArray(), br.readLine().toCharArray()});
                _patterns[i] = p;
            }

            // Get the number of orders
            _numOrders = Integer.parseInt(br.readLine());
            if(!((_numOrders >= 1) && (_numOrders <= 100))) System.err.print("1 <= numOrders <= 100");

            // Get the orders
            for(int i = 0; i < _numOrders; i++) {
                _orders.add(br.readLine());
            }

            // Execute this task its orders
            executeInstructions();
        }
    }

    // Executes the orders of the current task
    private void executeInstructions(){
        String input;
        // Iterate all the orders of the current task
        for(int i = 0; i < _numOrders; i++) {
            // Get the first order in the queue and execute it
            input = _orders.poll();
            switch(input){
                case "draai": rotate();
                    break;
                case "naai": sew();
                    break;
                case "teken": draw();
                    break;
                case "stop": return;
                default: addPatternToStack(Integer.parseInt(input) - 1);
            }
        }
    }

    // Add a rag with the i-th pattern on top of the stack.
    private void addPatternToStack(int i) {
        // Push the pattern[i] to the top of the stack
        _stack.push(_patterns[i]);
    }

    // Sews together the 2 upper rags from the stack (upper rag on the left), and puts the result on top of the stack.
    // ++  +  ||  ===> ++||
    // --     //       --//
    private void sew() {
        // Pop the upper 2 rags from the stack
        Pattern leftRagPattern = _stack.pop();
        Pattern rightRagPattern = _stack.pop();

        // Get both their arrays, widths and heights
        char[][] leftRag = leftRagPattern.getPattern();
        char[][] rightRag = rightRagPattern.getPattern();

        int leftRagHeight = leftRag.length;
        int leftRagWidth = leftRag[0].length;

        int rightRagHeight = rightRag.length;
        int rightRagWidth = rightRag[0].length;

        // Create a new array based on the previous two rags
        char[][] newRag = new char[leftRagHeight][leftRagWidth + rightRagWidth];
        // If both character arrays are the same height, allow to add
        if(leftRagHeight == rightRagHeight)
            // For the whole height of the left rag, add the two arrays together
            for(int i = 0; i < leftRagHeight; i++){
                for(int j = 0; j < leftRagWidth; j++)
                    newRag[i][j] = leftRag[i][j];
                for(int k = 0; k < rightRagWidth; k++)
                    newRag[i][leftRagWidth+k] = rightRag[i][k];
            }
        else System.err.print("Not allowed to add 2 patterns of a different height");

        // Create a new rag based on the new array, and push it to the top of the stack
        Pattern newRagPattern = new Pattern(newRag);
        _stack.push(newRagPattern);
    }

    // Rotates the rag on top of the stack clockwise.
    private void rotate() {
        // Pop the top rag off the stack
        Pattern oldRagPattern = _stack.pop();

        // Get the old array and create a new one with the same height and width to fill
        char[][] oldRag = oldRagPattern.getPattern();
        char[][] newRag = new char[oldRag[0].length][oldRag.length];

        // Rotate the array 90 degrees clockwise (transpose and reverse)
        for(int i=0; i<oldRag[0].length; i++){
            for(int j=oldRag.length-1; j>=0; j--){
                // Replace the characters corresponding to their character after rotating 90 degrees
                newRag[i][oldRag.length-1-j] = replacingChar(oldRag[j][i]);
            }
        }

        // Create a new pattern using the new-made array and push it to the top of the stack
        Pattern newRagPattern = new Pattern(newRag);
        _stack.push(newRagPattern);
    }

    // Replace the current character corresponding to it's character after rotating 90 degrees
    private char replacingChar(char c){
        switch (c){
            case '/': return '\\';
            case '\\': return '/';
            case '-': return '|';
            case '|': return '-';
            default: return c;
        }
    }

    // Draws the top of the stack and leaves open a line (\r\n) if needed.
    private void draw() {
        char[][] pattern = _stack.peek().getPattern();

        for (int i = 0; i < pattern.length; i++) {
            System.out.println(pattern[i]);
        }
        if (_orders.contains("teken") || _orders.peek().equals("stop"))
            System.out.println();
    }
}

class Pattern {
    // Variables
    private char[][] _pattern;

    // Constructor
    public Pattern(char[][] pattern){
        this._pattern = pattern;
    }

    // Methods
    public char[][] getPattern () {
        return _pattern;
    }
}
