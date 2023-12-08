import java.awt.*;
import java.awt.geom.Line2D;
import javax.swing.*;

/*
    Algorithm to find how to colour a map using 3 colours:

    1. Assign a random colour to the state with the most borders
    2. Pick a one of the countries that neighbor it with the least amount of neighbors
        a. Assign it one of the 2 remaining colours
        b. Pick a country that borders it with 2 coloured neighbors and is uncoloured
        loop back to a until there are no bordering countries that are uncoloured
    3. loop through all countries and check if they are coloured
            if not coloured, pick one of the remaining colours left by its neighbors
                if no neighbors, assign first colour option in list/array of colours
*/

public class Main {
    private static String intToAlphabet(int i) {
        if (i > 0 && i < 27) {
            return String.valueOf((char)(i + 'A' - 1)); //integer to char manipulation and then to String
        } else {
            return null;
        }
    }

    public static class directions {
        public static class dir {
            public int x;
            public int y;

            private dir(int x_component, int y_component) {
                x = x_component;
                y = y_component;
            }
        }
        public static final dir UP_LEFT = new dir(-1,-1);
        public static final dir UP = new dir(0,-1);
        public static final dir UP_RIGHT = new dir(1,-1);
        public static final dir LEFT = new dir(-1,0);
        public static final dir RIGHT = new dir(1,0);
        public static final dir DOWN_LEFT = new dir(-1,1);
        public static final dir DOWN = new dir(0,1);
        public static final dir DOWN_RIGHT = new dir(1,1);
    }
    public static class cNode {
        public final Color uncoloured = Color.DARK_GRAY;
        public final Color coloured = Color.ORANGE;
        public Color colour;
        public String id;
        public final int x, y, size_x, size_y;
        public cNode[] Bordering;
        public directions.dir[] BorderDirs; // Border directions
        public cNode[] UnvisitedBordering;

        public cNode(String n_id, int x_coordinate, int y_coordinate) {
            id = n_id;
            x = x_coordinate;
            y = y_coordinate;
            size_x = 60;
            size_y = 60;
            colour =  uncoloured;
        }
        public void setBordering(cNode[] arr) {
            Bordering = arr;
            UnvisitedBordering = arr;
        }
        public void BorderVisited(cNode visited_state) {
            int newlength = UnvisitedBordering.length-1; // removing one element, so one shorter
            cNode[] tmp = new cNode[newlength];
            int index = 0;
            for (Main.cNode cNode : UnvisitedBordering) {
                if (cNode != visited_state && index < tmp.length) {
                    tmp[index] = cNode;
                    index++;
                }
            }
            UnvisitedBordering = tmp;
        }

        public void setColour(Color newColour) {
            colour = newColour;
            for (cNode state : UnvisitedBordering) {  // Only needed for uncoloured neighbors
                state.BorderVisited(this);
            }
        }

        public cNode[] findBorders(cNode[][] grid, int row, int column) {
            int numRows = grid.length;
            int numCols = grid[0].length;

            directions.dir[] dirs = {
                    directions.UP, directions.RIGHT, directions.DOWN, directions.LEFT, directions.DOWN_LEFT, directions.DOWN_RIGHT, directions.UP_LEFT, directions.UP_RIGHT
            };

            int validNeighborCount = 0;

            for (directions.dir direction : dirs) { // finds num of valid neighbors
                int newX = column + direction.x;
                int newY = row + direction.y;

                if (newX >= 0 && newX < numCols && newY >= 0 && newY < numRows) {
                    validNeighborCount++;
                }
            }

            cNode[] validNeighbors = new cNode[validNeighborCount];

            int index = 0;
            for (directions.dir dir : dirs) {
                int newX = column + dir.x;
                int newY = row + dir.y;

                if (newX >= 0 && newX < numCols && newY >= 0 && newY < numRows) {
                    validNeighbors[index] = grid[newY][newX];
                    index++;
                }
            }

            return validNeighbors;
        }
    }

    public static void main(String[] args) {
        final Color[] map_colours = {Color.RED, Color.GREEN, Color.BLUE, Color.MAGENTA};
        // Create Nodes
        final int grid_size_x = 4; // Max 26
        final int grid_size_y = 4;

        cNode[][] Grid = new cNode[grid_size_y][grid_size_x];
        for (int row=0; row<grid_size_y; row++) {
            for (int column=0; column<grid_size_x; column++) {
                String Node_ID = intToAlphabet(column) + row;
                int x_coordinate = 10 + column*100;
                int y_coordinate = 10 + row*100;
                Grid[row][column] = new cNode(Node_ID, x_coordinate, y_coordinate);
            }
        }

        // Borders
        for (int row=0; row<Grid.length; row++) {
            for (int column=0; column<Grid[row].length; column++) {
                cNode[] Borders = Grid[row][column].findBorders(Grid, row, column);
                Grid[row][column].setBordering(Borders);
            }
        }

        // Array of all states
        cNode[] states = new cNode[Grid.length * Grid[0].length];
        int statex = 0;
        int statey = 0;
        for (int i=0; i<Grid.length * Grid[0].length; i++) {
            if (statex == Grid[0].length) {
                statex = 0;
                statey++;
            }
            states[i] = Grid[statey][statex];
            statex++;
        }

        JFrame fr = new JFrame();
        fr.setBounds(10, 10, 500, 500);
        fr.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        // Colouring algorithm
        boolean coloured = false;
        cNode start_state = states[0];
        for (cNode state : states) { // find state with most borders
            if (state.Bordering.length > start_state.Bordering.length) {
                start_state = state;
                System.out.println("Newstart: "+start_state.id);
            }
        }
        System.out.println("\nFINAL START: "+start_state.id+"------------------\n");
        start_state.setColour(map_colours[0]);
        cNode current_state = start_state;

        while(!coloured) {
            if (current_state.UnvisitedBordering.length != 0) {
                cNode next_state = current_state.UnvisitedBordering[0];
                for (cNode bordering : current_state.UnvisitedBordering) {
                    if (bordering.UnvisitedBordering.length < current_state.UnvisitedBordering.length) {
                        next_state = bordering; // update next state if bordering state has fewer uncoloured borders
                    }
                }
                current_state = next_state;
            } else {
                coloured = true;  // end the while loop
            }
        }


        JPanel pn = new JPanel() {
            @Override
            public void paint(Graphics g) {
                Graphics2D g2=(Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                for (cNode state : states) {
                    g2.setColor(state.colour);
                    g2.fillOval(state.x, state.y, state.size_x, state.size_y);
                    for (cNode Border : state.Bordering) {

                        int p1_x = state.x+state.size_x/2;
                        int p1_y = state.y+state.size_y/2;

                        int p2_x = p1_x + ((Border.x+Border.size_x/2)-p1_x)/2;
                        int p2_y = p1_y + ((Border.y+Border.size_y/2)-p1_y)/2;
                        Line2D line = new Line2D.Float(p1_x, p1_y, p2_x, p2_y);
                        g2.draw(line);
                    }
                }
            }
        };

        //Title
        JLabel title = new JLabel("4 Coloured graph");
        title.setFont(new Font("Arial", Font.BOLD, 16));
        title.setBounds(10, 400, 500, 25);
        title.setForeground(Color.darkGray);
        fr.add(title);

        // Add State Labels (state ids)
        for (cNode state : states) {
            int padAdj = state.size_x/8 * state.id.length();// Padding adjustment depending on id length
            JLabel newLabel = new JLabel(state.id);
            newLabel.setFont(new Font("Arial", Font.BOLD, 20));
            newLabel.setBounds(state.x+state.size_x/2-padAdj, state.y, state.size_x, state.size_y);
            newLabel.setForeground(Color.WHITE);
            fr.add(newLabel);
        }

        fr.add(pn);
        fr.setVisible(true);
        System.out.println("Displayed Successfully.");
    }
}