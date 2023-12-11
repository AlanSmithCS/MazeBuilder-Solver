import java.awt.*;
import java.awt.geom.Line2D;
import javax.swing.*;
import java.util.Random; // for Pseudorandom numbers

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
        public static final dir UP = new dir(0,-1);
        public static final dir LEFT = new dir(-1,0);
        public static final dir RIGHT = new dir(1,0);
        public static final dir DOWN = new dir(0,1);
        public static final dir[] directions_arr = {UP, LEFT, RIGHT, DOWN};

        public static dir opposite(dir direction) {
            int index = 0;
            for (dir d : directions_arr) {
                if (d == direction) {
                    break;
                }
                index++;
            }
            return directions_arr[directions_arr.length -1 -index];
        }
    }
    public static class cNode {
        public static class type {
            String id;
            Color colour;
            public type(String type_name, Color type_colour) {
                id = type_name;
                colour = type_colour;
            }
        }

        public static final type default_Node = new type("Default", Color.DARK_GRAY);
        public static final type start_Node = new type("Start", Color.ORANGE);
        public static final type path_Node = new type("Path", Color.BLUE);
        public static final type end_Node = new type("End", Color.GREEN);
        public Color colour;
        public String id;
        public type n_type;
        public final int x, y, size_x, size_y;
        public cNode[] Bordering;
        public directions.dir[] BorderDirs; // Border directions
        public cNode[] UnvisitedBordering;

        public cNode(String n_id, int x_coordinate, int y_coordinate) {
            id = n_id;
            x = x_coordinate;
            y = y_coordinate;
            size_x = 20;
            size_y = 20;
            n_type = default_Node;
            colour =  n_type.colour;
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
        public void addNeighbor(cNode visited_state, directions.dir Facing) {
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
        /*
        public cNode[] findBorders(cNode[][] grid, int row, int column) {
            int numRows = grid.length;
            int numCols = grid[0].length;

            directions.dir[] dirs = directions.directions_arr;

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
         */
    }

    public static void main(String[] args) {
        // Create Nodes
        final int grid_size_x = 20; // Max 26 (due to letters in the alphabet)
        final int grid_size_y = 20;

        cNode[][] Grid = new cNode[grid_size_y][grid_size_x];
        for (int row=0; row<grid_size_y; row++) {
            for (int column=0; column<grid_size_x; column++) {
                //Node ID
                String Node_ID = intToAlphabet(column+1) + row;
                int x_coordinate = 10 + column*35;
                int y_coordinate = 10 + row*35;
                Grid[row][column] = new cNode(Node_ID, x_coordinate, y_coordinate);
            }
        }
        // Must be done after the creation of the array, otherwise the neighbors will be null
        for (int row=0; row<grid_size_y; row++) {
            for (int column=0; column<grid_size_x; column++) {
                //Borders
                //cNode[] Borders = Grid[row][column].findBorders(Grid, row, column);
                cNode[] Borders = {};
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
        fr.setBounds(10, 10, 20+(grid_size_x*Grid[0][0].size_x)+((grid_size_x)*15), 42+(grid_size_y*Grid[0][0].size_y)+((grid_size_y)*15));
        fr.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        // Maze creation algorithm
        boolean generated = false;
        cNode begin_node = Grid[grid_size_y/2][grid_size_x/2];
        begin_node.n_type = cNode.start_Node;
        begin_node.setColour(begin_node.n_type.colour);

        System.out.println("\nStart Node: "+begin_node.id+"| Neighboring:");
        for (cNode Border : begin_node.Bordering) {
            System.out.print(" "+Border.id);
        }

        cNode current_Node = begin_node;
        /*
        while(!generated) {
            if (current_Node.UnvisitedBordering.length != 0) {
                cNode next_state = current_Node.UnvisitedBordering[0];
                for (cNode bordering : current_Node.UnvisitedBordering) {
                    if (bordering.UnvisitedBordering.length < current_Node.UnvisitedBordering.length) {
                        next_state = bordering; // update next state if bordering state has fewer uncoloured borders
                    }
                }
                current_Node = next_state;
            } else {
                generated = true;  // end the while loop
            }
        }
         */


        JPanel pn = new JPanel() {
            @Override
            public void paint(Graphics g) {
                Graphics2D g2=(Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setStroke(new BasicStroke(5));
                for (cNode state : states) {
                    g2.setColor(state.colour);
                    g2.fillRect(state.x, state.y, state.size_x, state.size_y);
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

        // Add State Labels (state ids)
        for (cNode state : states) {
            int padAdj = state.size_x/8 * state.id.length();// Padding adjustment depending on id length
            JLabel newLabel = new JLabel(state.id);
            newLabel.setFont(new Font("Arial", Font.BOLD, 9));
            newLabel.setBounds(state.x+state.size_x/2-padAdj, state.y, state.size_x, state.size_y);
            newLabel.setForeground(Color.WHITE);
            fr.add(newLabel);
        }
        fr.setBackground(Color.BLACK);
        fr.add(pn);
        fr.setVisible(true);
        System.out.println("\nDisplayed Successfully.");
    }
}