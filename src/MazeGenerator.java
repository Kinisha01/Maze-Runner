import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class MazeGenerator
{
    int rows, cols;
    char[][] maze;

    public MazeGenerator(int size)
    {
        rows = size;
        cols = size;
        maze = new char[rows][cols];
        generateMaze();
    }
    void generateMaze()
    {
        for(int i=0 ; i<rows ; i++)
        {
            for (int j = 0; j < cols; j++)
            {
                maze[i][j] = '#';
            }
        }
        dfs(1,1);
        addExtraPaths(rows); // more size → more complexity//----------------
        placeExit();
    }


    void placeExit()
    {
        for(int i = rows - 2; i > 1; i--)
        {
            for(int j = cols - 2; j > 1; j--)
            {
                if(maze[i][j] == '.')
                {
                    int openSides = 0;

                    if(maze[i+1][j] != '#') openSides++;
                    if(maze[i-1][j] != '#') openSides++;
                    if(maze[i][j+1] != '#') openSides++;
                    if(maze[i][j-1] != '#') openSides++;

                    if(openSides >= 1)
                    {
                        maze[i][j] = 'E';
                        return;
                    }
                }
            }
        }
    }


    //optional more paths-------------
    void addExtraPaths(int count)
    {
        Random rand = new Random();

        int created = 0;

        while(created < count)
        {
            int r = rand.nextInt(rows - 2) + 1;
            int c = rand.nextInt(cols - 2) + 1;

            // only wall cells
            if(maze[r][c] != '#')
                continue;

            // horizontal connector
            if(
                    maze[r][c - 1] == '.' &&
                            maze[r][c + 1] == '.'
            )
            {
                maze[r][c] = '.';
                created++;
            }

            // vertical connector
            else if(
                    maze[r - 1][c] == '.' &&
                            maze[r + 1][c] == '.'
            )
            {
                maze[r][c] = '.';
                created++;
            }
        }
    }


    //--------------------
    void dfs(int r, int c)
    {
        maze[r][c] = '.';
        int[][] dirs = {
                {0,2}, {0,-2}, {2,0}, {-2,0}
        };
        //Randomize maze generation
        List<int[]> directions = Arrays.asList(dirs);
        Collections.shuffle(directions);

        for(int[] d : directions) {
            int nr = r + d[0];
            int nc = c + d[1];
            if((nr > 0) && (nc > 0) && (nr < (rows - 1)) && (nc < (cols - 1)) && (maze[nr][nc] == '#')){
                maze[r+d[0]/2][c+d[1]/2] = '.';
                dfs(nr, nc);
            }
        }
    }
    void printMaze() {
        for(int i=0 ; i<rows ; i++)
        {
            for (int j = 0; j < cols; j++) {
                if(maze[i][j] == '#') System.out.print("█ ");
                else if(maze[i][j] == 'E') System.out.print("E ");
                else System.out.print("  ");
            }
            System.out.println();
        }
    }

}
