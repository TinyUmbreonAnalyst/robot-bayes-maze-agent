

import java.awt.event.*;
import java.awt.Color;
import java.awt.Graphics;
import java.util.*;
import javax.swing.JComponent;
import javax.swing.JFrame;
import java.io.*;
import java.net.*;




// This class draws the probability map and value iteration map that you create to the window
// You need only call updateProbs() and updateValues() from your theRobot class to update these maps
class mySmartMap extends JComponent implements KeyListener {
    public static final int NORTH = 0;
    public static final int SOUTH = 1;
    public static final int EAST = 2;
    public static final int WEST = 3;
    public static final int STAY = 4;

    int currentKey;

    int winWidth, winHeight;
    double sqrWdth, sqrHght;
    Color gris = new Color(170,170,170);
    Color myWhite = new Color(220, 220, 220);
    World mundo;
    
    int gameStatus;

    double[][] probs;
    double[][] vals;
    
    public mySmartMap(int w, int h, World wld) {
        mundo = wld;
        probs = new double[mundo.width][mundo.height];
        vals = new double[mundo.width][mundo.height];
        winWidth = w;
        winHeight = h;
        
        sqrWdth = (double)w / mundo.width;
        sqrHght = (double)h / mundo.height;
        currentKey = -1;
        
        addKeyListener(this);
        
        gameStatus = 0;
    }
    
    public void addNotify() {
        super.addNotify();
        requestFocus();
    }
    
    public void setWin() {
        gameStatus = 1;
        repaint();
    }
    
    public void setLoss() {
        gameStatus = 2;
        repaint();
    }
    
    public void updateProbs(double[][] _probs) {
        for (int y = 0; y < mundo.height; y++) {
            for (int x = 0; x < mundo.width; x++) {
                probs[x][y] = _probs[x][y];
            }
        }
        
        repaint();
    }
    
    public void updateValues(double[][] _vals) {
        for (int y = 0; y < mundo.height; y++) {
            for (int x = 0; x < mundo.width; x++) {
                vals[x][y] = _vals[x][y];
            }
        }
        
        repaint();
    }

    public void paint(Graphics g) {
        paintProbs(g);
        //paintValues(g);
    }

    public void paintProbs(Graphics g) {
        double maxProbs = 0.0;
        int mx = 0, my = 0;
        for (int y = 0; y < mundo.height; y++) {
            for (int x = 0; x < mundo.width; x++) {
                if (probs[x][y] > maxProbs) {
                    maxProbs = probs[x][y];
                    mx = x;
                    my = y;
                }
                if (mundo.grid[x][y] == 1) {
                    g.setColor(Color.black);
                    g.fillRect((int)(x * sqrWdth), (int)(y * sqrHght), (int)sqrWdth, (int)sqrHght);
                }
                else if (mundo.grid[x][y] == 0) {
                    //g.setColor(myWhite);
                    
                    int col = (int)(255 * Math.sqrt(probs[x][y]));
                    if (col > 255)
                        col = 255;
                    g.setColor(new Color(255-col, 255-col, 255));
                    g.fillRect((int)(x * sqrWdth), (int)(y * sqrHght), (int)sqrWdth, (int)sqrHght);
                }
                else if (mundo.grid[x][y] == 2) {
                    g.setColor(Color.red);
                    g.fillRect((int)(x * sqrWdth), (int)(y * sqrHght), (int)sqrWdth, (int)sqrHght);
                }
                else if (mundo.grid[x][y] == 3) {
                    g.setColor(Color.green);
                    g.fillRect((int)(x * sqrWdth), (int)(y * sqrHght), (int)sqrWdth, (int)sqrHght);
                }
            
            }
            if (y != 0) {
                g.setColor(gris);
                g.drawLine(0, (int)(y * sqrHght), (int)winWidth, (int)(y * sqrHght));
            }
        }
        for (int x = 0; x < mundo.width; x++) {
                g.setColor(gris);
                g.drawLine((int)(x * sqrWdth), 0, (int)(x * sqrWdth), (int)winHeight);
        }
        
        //System.out.println("repaint maxProb: " + maxProbs + "; " + mx + ", " + my);
        
        g.setColor(Color.green);
        g.drawOval((int)(mx * sqrWdth)+1, (int)(my * sqrHght)+1, (int)(sqrWdth-1.4), (int)(sqrHght-1.4));
        
        if (gameStatus == 1) {
            g.setColor(Color.green);
            g.drawString("You Won!", 8, 25);
        }
        else if (gameStatus == 2) {
            g.setColor(Color.red);
            g.drawString("You're a Loser!", 8, 25);
        }
    }
    
    public void paintValues(Graphics g) {
        double maxVal = -99999, minVal = 99999;
        //int mx = 0, my = 0;
        
        for (int y = 0; y < mundo.height; y++) {
            for (int x = 0; x < mundo.width; x++) {
                if (mundo.grid[x][y] != 0)
                    continue;
                
                if (vals[x][y] > maxVal)
                    maxVal = vals[x][y];
                if (vals[x][y] < minVal)
                    minVal = vals[x][y];
            }
        }
        if (minVal == maxVal) {
            maxVal = minVal+1;
        }

        int offset = winWidth+20;
        for (int y = 0; y < mundo.height; y++) {
            for (int x = 0; x < mundo.width; x++) {
                if (mundo.grid[x][y] == 1) {
                    g.setColor(Color.black);
                    g.fillRect((int)(x * sqrWdth)+offset, (int)(y * sqrHght), (int)sqrWdth, (int)sqrHght);
                }
                else if (mundo.grid[x][y] == 0) {
                    //g.setColor(myWhite);
                    
                    //int col = (int)(255 * Math.sqrt((vals[x][y]-minVal)/(maxVal-minVal)));
                    int col = (int)(255 * (vals[x][y]-minVal)/(maxVal-minVal));
                    if (col > 255)
                        col = 255;
                    g.setColor(new Color(255-col, 255-col, 255));
                    g.fillRect((int)(x * sqrWdth)+offset, (int)(y * sqrHght), (int)sqrWdth, (int)sqrHght);
                }
                else if (mundo.grid[x][y] == 2) {
                    g.setColor(Color.red);
                    g.fillRect((int)(x * sqrWdth)+offset, (int)(y * sqrHght), (int)sqrWdth, (int)sqrHght);
                }
                else if (mundo.grid[x][y] == 3) {
                    g.setColor(Color.green);
                    g.fillRect((int)(x * sqrWdth)+offset, (int)(y * sqrHght), (int)sqrWdth, (int)sqrHght);
                }
            
            }
            if (y != 0) {
                g.setColor(gris);
                g.drawLine(offset, (int)(y * sqrHght), (int)winWidth+offset, (int)(y * sqrHght));
            }
        }
        for (int x = 0; x < mundo.width; x++) {
                g.setColor(gris);
                g.drawLine((int)(x * sqrWdth)+offset, 0, (int)(x * sqrWdth)+offset, (int)winHeight);
        }
    }

    
    public void keyPressed(KeyEvent e) {
        //System.out.println("keyPressed");
    }
    public void keyReleased(KeyEvent e) {
        //System.out.println("keyReleased");
    }
    public void keyTyped(KeyEvent e) {
        char key = e.getKeyChar();
        //System.out.println(key);
        
        switch (key) {
            case 'w':
                currentKey = NORTH;
                break;
            case 's':
                currentKey = SOUTH;
                break;
            case 'a':
                currentKey = WEST;
                break;
            case 'd':
                currentKey = EAST;
                break;
            case ' ':
                currentKey = STAY;
                break;
        }
    }
}


// This is the main class that you will add to in order to complete the lab
public class theRobot extends JFrame {
    // Mapping of actions to integers
    public static final int NORTH = 0;
    public static final int SOUTH = 1;
    public static final int EAST = 2;
    public static final int WEST = 3;
    public static final int STAY = 4;

    Color bkgroundColor = new Color(230,230,230);
    
    static mySmartMap myMaps; // instance of the class that draw everything to the GUI
    String mundoName;
    
    World mundo; // mundo contains all the information about the world.  See World.java
    double moveProb, sensorAccuracy;  // stores probabilies that the robot moves in the intended direction
                                      // and the probability that a sonar reading is correct, respectively
    
    // variables to communicate with the Server via sockets
    public Socket s;
	public BufferedReader sin;
	public PrintWriter sout;
    
    // variables to store information entered through the command-line about the current scenario
    boolean isManual = false; // determines whether you (manual) or the AI (automatic) controls the robots movements
    boolean knownPosition = false;
    int startX = -1, startY = -1;
    int decisionDelay = 250;
    int stayCount = 0;

    // store your probability map (for position of the robot in this array
    double[][] probs;
    
    // store your computed value of being in each state (x, y)
    double[][] Vs;
    
    public theRobot(String _manual, int _decisionDelay) {
        // initialize variables as specified from the command-line
        if (_manual.equals("automatic"))
            isManual = false;
        else
            isManual = true;
        decisionDelay = _decisionDelay;
        
        // get a connection to the server and get initial information about the world
        initClient();
    
        // Read in the world
        mundo = new World(mundoName);
        
        // set up the GUI that displays the information you compute
        int width = 500;
        int height = 500;
        int bar = 20;
        setSize(width,height+bar);
        getContentPane().setBackground(bkgroundColor);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(0, 0, width, height+bar);
        myMaps = new mySmartMap(width, height, mundo);
        getContentPane().add(myMaps);
        
        setVisible(true);
        setTitle("Probability and Value Maps");
        
        doStuff(); // Function to have the robot move about its world until it gets to its goal or falls in a stairwell
    }
    
    // this function establishes a connection with the server and learns
    //   1 -- which world it is in
    //   2 -- it's transition model (specified by moveProb)
    //   3 -- it's sensor model (specified by sensorAccuracy)
    //   4 -- whether it's initial position is known.  if known, its position is stored in (startX, startY)
    public void initClient() {
        int portNumber = 3333;
        String host = "localhost";
        
        try {
			s = new Socket(host, portNumber);
            sout = new PrintWriter(s.getOutputStream(), true);
			sin = new BufferedReader(new InputStreamReader(s.getInputStream()));
            
            mundoName = sin.readLine();
            moveProb = Double.parseDouble(sin.readLine());
            sensorAccuracy = Double.parseDouble(sin.readLine());
            System.out.println("Need to open the mundo: " + mundoName);
            System.out.println("moveProb: " + moveProb);
            System.out.println("sensorAccuracy: " + sensorAccuracy);
            
            // find out of the robots position is know
            String _known = sin.readLine();
            if (_known.equals("known")) {
                knownPosition = true;
                startX = Integer.parseInt(sin.readLine());
                startY = Integer.parseInt(sin.readLine());
                System.out.println("Robot's initial position is known: " + startX + ", " + startY);
            }
            else {
                System.out.println("Robot's initial position is unknown");
            }
        } catch (IOException e) {
            System.err.println("Caught IOException: " + e.getMessage());
        }
    }

    // function that gets human-specified actions //changed to WASD and space for my own sanity
    // 'i' specifies the movement up
    // ',' specifies the movement down
    // 'l' specifies the movement right
    // 'j' specifies the movement left
    // 'k' specifies the movement stay
    int getHumanAction() {
        System.out.println("Reading the action selected by the user");
        while (myMaps.currentKey < 0) {
            try {
                Thread.sleep(50);
            }
            catch(InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
        int a = myMaps.currentKey;
        myMaps.currentKey = -1;
        
        System.out.println("Action: " + a);
        
        return a;
    }
    
    // initializes the probabilities of where the AI is
    void initializeProbabilities() {
        probs = new double[mundo.width][mundo.height];
        // if the robot's initial position is known, reflect that in the probability map
        if (knownPosition) {
            for (int y = 0; y < mundo.height; y++) {
                for (int x = 0; x < mundo.width; x++) {
                    if ((x == startX) && (y == startY))
                        probs[x][y] = 1.0;
                    else
                        probs[x][y] = 0.0;
                }
            }
        }
        else {  // otherwise, set up a uniform prior over all the positions in the world that are open spaces
            int count = 0;
            
            for (int y = 0; y < mundo.height; y++) {
                for (int x = 0; x < mundo.width; x++) {
                    if (mundo.grid[x][y] == 0)
                        count++;
                }
            }
            
            for (int y = 0; y < mundo.height; y++) {
                for (int x = 0; x < mundo.width; x++) {
                    if (mundo.grid[x][y] == 0)
                        probs[x][y] = 1.0 / count;
                    else
                        probs[x][y] = 0;
                }
            }
        }
        
        myMaps.updateProbs(probs);
    }
    
    // Note: sonars is a bit string with four characters, specifying the sonar reading in the direction of North, South, East, and West
    //       For example, the sonar string 1001, specifies that the sonars found a wall in the North and West directions, but not in the South and East directions
    private void updateProbabilities(int action, String sonars) {
        // your code
        double normSum = 0.0;
        double newProbs[][] = new double[mundo.width][mundo.height];
        for (int x = 0; x < mundo.width; x++) {
            for (int y = 0; y < mundo.height; y++) {
                if (mundo.grid[x][y] != 1) {
                    double newBelief = transitionModel(action, x, y);
                    newProbs[x][y] = sensorModel(sonars, x, y, newBelief);
                    normSum += newProbs[x][y];
                } else {
                    newProbs[x][y] = 0.0;
                }
            }
        }
        probs = newProbs;
        for (int x = 0; x < mundo.width; x++) {
            for (int y = 0; y < mundo.height; y++) {
                probs[x][y] /= normSum;
            }
        }
        myMaps.updateProbs(probs); // call this function after updating your probabilities so that the
                                   //  new probabilities will show up in the probability map on the GUI
    }

    private double sensorModel(String sonars, int x, int y, double beliefPrime) {
        double totalProb = 1.0;
        int[] r = new int[2];
        for (int d = 0; d < 4; d++){
            int v = sonars.charAt(d) - '0';
            setDirection(d, r, x, y);
            int sensorValue = getSensorValue(r);
            if (v == sensorValue) {
                totalProb *= sensorAccuracy;
            }
            else {
                totalProb *= 1- sensorAccuracy;
            }
        }
        return totalProb * beliefPrime;
    }

    private int getSensorValue(int[] r) {
        if(mundo.grid[r[0]][r[1]] == 1) {
            return 1;
        }
        return 0;
    }

    private double transitionModel(int action, int x, int y) {
        int negAct = neg(action);
        double unProb = (1.0 - moveProb) / 4;
        int prevVar[][] = getPrevVar(x, y);
        double stayProb = prevVar[5][0] * unProb; //if you wrong move, and hit a wall, you don't move.
        double totalProb = 0.0;
        for (int a = 0; a < 5; ++a) { //Order is according to negative. That is: South, North, West, East, Stay
            if (prevVar[a][2] != 0) {
                continue; //can't move from wall or well/goal
            }
            double prob = unProb;
            if (negAct == a) {
                prob = moveProb;
            }
            if (a == STAY) {
                prob += stayProb;
            }
            totalProb += probs[prevVar[a][0]][prevVar[a][1]] * prob;
        }
        return totalProb;
    }

    //Has to be opposite of action because you are checking from possible previous states to current state
    private int neg(int action){
        switch (action) {
            case NORTH:
                return SOUTH;
            case SOUTH:
                return NORTH;
            case EAST:
                return WEST;
            case WEST:
                return EAST;
            default:
                return STAY;
        }
    }

    //returns array of 2 results, xpos, ypos, at corresponding location.
    private int[][] getPrevVar(int x, int y) {
        int results[][] = new int[6][3];
        int wallCount = 0;
        for (int i = 0; i < 5; ++i) {
            int[] r = results[i];
            setDirection(i, r, x, y);
            if (!inBounds(r[0], r[1])) {
                r[2] = 1; //Assume wall here. You can't move from OOB anyway
            } else {
               r[2] = mundo.grid[r[0]][r[1]]; 
            }
            if (r[2] == 1){
                wallCount++;
            }
        }
        results[5][0] = wallCount; //kinda hacky, but java really doesn't like multiple types of array types, unlike python or javascript
        return results;
    }

    private void setDirection(int a, int[] r, int x, int y) {
        switch (a) {
            case NORTH:
                r[0] = x;
                r[1] = y - 1;
                break;
            case SOUTH:
                r[0] = x;
                r[1] = y + 1;
                break;
            case EAST:
                r[0] = x + 1;
                r[1] = y;
                break;
            case WEST:
                r[0] = x - 1;
                r[1] = y;
                break;
            default:
                r[0] = x;
                r[1] = y;
                break;
        }
    }

    private boolean inBounds(int x, int y) {
        return x >= 0 && y >= 0 && y < mundo.height && x < mundo.width;
    }

    
    // This is the function you'd need to write to make the robot move using your AI;
    int automaticAction() {
        double[] dirUtilTotal = new double[5];
        for (int x = 0; x < mundo.width; ++x) {
            for(int y = 0; y < mundo.height; ++y) {
                if (mundo.grid[x][y] != 0 || probs[x][y] == 0) {
                    continue;
                }
                double unProb = (1.0 - moveProb) / 4;
                int actionValues[][] = getPrevVar(x, y);
                double initProb = actionValues[5][0] * unProb;
                for (int act = 0; act < 5; act ++) {
                    double stayProb = initProb;
                    if (actionValues[act][2] == 1) {
                        stayProb += moveProb - unProb;
                    }
                    for (int a = 0; a < 5; a++) {
                        if (actionValues[a][2] == 1) {
                            continue; //prob will be 0;
                        }
                        double prob = unProb;
                        if (a == act) {
                            prob = moveProb;
                        }
                        if (a == STAY) {
                            prob += stayProb;
                        }
                        //add onto the total Expected Utility for trying to move with action act to be probabiliy you are at a location * 
                        //probability the robot will move to a square * utility at that square
                        dirUtilTotal[act] += probs[x][y] * prob * Vs[actionValues[a][0]][actionValues[a][1]];
                    }  
                }
            }
        }
        dirUtilTotal[4] -= .1; //discourage staying in one place
        Integer[] rank = {0, 1, 2, 3, 4};
        Arrays.sort(rank, (x, y) -> (int) Math.signum(dirUtilTotal[y] - dirUtilTotal[x]));
        int best = rank[0].intValue();
        if(best == 4) {
            stayCount++;
            if (stayCount >= 3) {
                return rank[1].intValue();
            }
        } else { 
            stayCount = 0;
        }
        return best; 
    }

    double[][] copyGrid(double[][] a) {
        double[][] copy = new double[a.length][a[0].length];
        for (int x = 0; x < a.length; ++x) {
            for(int y = 0; y < a[x].length; ++y) {
                copy[x][y] = a[x][y];
            }
        }
        return copy;
    }


    double[][] initializeValues() {
        var values = new double[mundo.width][mundo.height];
        for (int x = 0; x < mundo.width; ++x) {
            for(int y = 0; y < mundo.height; ++y) {
                values[x][y] = 0;
            }
        }
        return values;
    }

    double computeBestAction(double[][] util, int val, int x, int y) {
        if(val != 0) {
            return 0;
        }
        double unProb = (1.0 - moveProb) / 4;
        int prevVar[][] = getPrevVar(x, y); //gives location of different moves, and the value of that position at that location
        double max = Double.MIN_VALUE;
        for (int act = 0; act < 5; act++) { //action you go for
            double total = 0;
            double stayProb = prevVar[5][0] * unProb; //wall count;
            if (prevVar[act][2] == 1) {
                stayProb += moveProb - unProb;
            }
            for (int a = 0; a < 5; a++) {// action you get
                if (prevVar[a][2] == 1) {
                    continue; //prob will be 0;
                }
                double prob = unProb;
                if (a == act) {
                    prob = moveProb;
                }
                if (a == STAY) {
                    prob += stayProb;
                }
                total += prob * util[prevVar[a][0]][prevVar[a][1]];
            }
            max = Math.max(total, max);
        }
        return max;
    }

    void valueIteration() { //use Vs
        double [] rewards = {-5, -20, -1000 * (1- moveProb), 1000};
        Vs = initializeValues();
        double gamma = .99;
        double maxChange = 0;
        double epsilon = .00001;
        do {
            maxChange = 0;
            double[][] utilities = copyGrid(Vs);
            for (int x = 0; x < mundo.width; ++x) {
                for(int y = 0; y < mundo.height; ++y) {
                    int val = mundo.grid[x][y];
                    if (val != 1) {
                        Vs[x][y] = rewards[val] + gamma * computeBestAction(utilities, val, x, y);
                    }
                    maxChange = Math.max(maxChange, Math.abs(Vs[x][y] - utilities[x][y]));
                }
            }
        } while (maxChange >= epsilon);  
        
    }
    
    void doStuff() {
        int action;
        
        valueIteration();  //Compute best utilites for all locations (rn, about quartic time. Maybe less.)
        initializeProbabilities();  // Initializes the location (probability) map
        for (int i = 0; i < Vs.length; i++) {
            System.out.println(Arrays.toString(Vs[i]));
        }
        
        while (true) {
            try {
                if (isManual)
                    action = getHumanAction();  // get the action selected by the user (from the keyboard)
                else
                    action = automaticAction(); 
                
                sout.println(action); // send the action to the Server
                
                // get sonar readings after the robot moves
                String sonars = sin.readLine();
                //System.out.println("Sonars: " + sonars);
            
                updateProbabilities(action, sonars); 
                
                if (sonars.length() > 4) {  // check to see if the robot has reached its goal or fallen down stairs
                    if (sonars.charAt(4) == 'w') {
                        System.out.println("I won!");
                        myMaps.setWin();
                        break;
                    }
                    else if (sonars.charAt(4) == 'l') {
                        System.out.println("I lost!\nGAME OVER YEAH!!!");
                        myMaps.setLoss();
                        break;
                    }
                }
                else {
                    // here, you'll want to update the position probabilities
                    // since you know that the result of the move as that the robot
                    // was not at the goal or in a stairwell
                    double normalization = 1.0; //total sum should be 1 at this point

                    for (int x = 0; x < mundo.width; ++x){
                        for(int y = 0; y < mundo.height; ++y){
                            if (mundo.grid[x][y] != 0) {
                                normalization -= probs[x][y];
                                probs[x][y] = 0.0;
                            }
                        }
                    }
                    for (int x = 0; x < mundo.width; ++x){
                        for(int y = 0; y < mundo.height; ++y){
                            if (mundo.grid[x][y] == 0) {
                                probs[x][y] /= normalization;
                            }
                        }
                    }

                }
                Thread.sleep(decisionDelay);  // delay that is useful to see what is happening when the AI selects actions
                                              // decisionDelay is specified by the send command-line argument, which is given in milliseconds
            }
            catch (IOException e) {
                System.out.println(e);
            }
            catch(InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
    }

    // java theRobot [manual/automatic] [delay]
    public static void main(String[] args) {
        new theRobot(args[0], Integer.parseInt(args[1]));  // starts up the robot
    }
}