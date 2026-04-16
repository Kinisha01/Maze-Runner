import java.util.Random;

public class MazeGenerator {

    int rows, cols;
    char[][] maze;

    public MazeGenerator(int size) {
        rows = size;
        cols = size;
        maze = new char[rows][cols];

        generateMaze();
    }

    void generateMaze() {
        Random rand = new Random();

        // Fill with walls
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                maze[i][j] = '#';
            }
        }

        // Create path
        int r = 1, c = 1;
        maze[r][c] = '.';

        while (r < rows - 2 || c < cols - 2) {

            if (r < rows - 2 && (c == cols - 2 || rand.nextBoolean())) {
                r++;
            } else {
                c++;
            }

            maze[r][c] = '.';
        }

        // Random open spaces
        for (int i = 1; i < rows - 1; i++) {
            for (int j = 1; j < cols - 1; j++) {
                if (maze[i][j] == '#' && rand.nextInt(3) == 0) {
                    maze[i][j] = '.';
                }
            }
        }

        // Start & End
        maze[1][1] = '.';
        maze[rows - 2][cols - 2] = 'E';
    }
}