import java.io.*;
import java.util.Scanner;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;

public class GamePanel extends JPanel implements KeyListener
{
    boolean paused = false;

    int timeLeft;
    Timer gameTimer;
    boolean gameOver = false;

    ArrayList<Point> shortestPath = new ArrayList<>();
    boolean showPath = false;

    ArrayList<Point> coins = new ArrayList<>();
    int score =0;

    SoundPlayer sound = new SoundPlayer();
    MazeGenerator mg;
    int cellSize = 25;

    int playerR = 1;
    int playerC = 1;

    int steps =0 ;

    int level =1;


    public GamePanel()
    {
        setBackground(Color.WHITE);
        mg = new MazeGenerator(10+level*5);
        generateCoins(level*3);
        setFocusable(true);
        addKeyListener(this);
        sound.playLoop("bg.wav");
        startTimer();
    }

    //timer-------------------
    void startTimer() {
        timeLeft = 60 + level * 10;

        if(gameTimer != null) gameTimer.stop();

        gameTimer = new Timer(1000, e -> {
            timeLeft--;

            if(timeLeft <= 0) {
                gameOver = true;
                gameTimer.stop();
                JOptionPane.showMessageDialog(this, "Time Up!");
            }

            repaint();
        });

        gameTimer.start();
    }
//----------------------------

    protected void paintComponent(Graphics g)
    {
        int topMargin = 60;

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        int panelWidth = getWidth();
        int panelHeight = getHeight();

        int cellSize = Math.min(panelWidth/mg.cols , (panelHeight-topMargin)/mg.rows);

        int offsetX = (panelWidth - mg.cols * cellSize) / 2;
        int offsetY = topMargin + (panelHeight - topMargin - mg.rows * cellSize) / 2;

        super.paintComponent(g);

        for(int i=0; i<mg.rows ; i++)
        {
            for(int j=0 ; j<mg.cols ; j++)
            {
                if(mg.maze[i][j] == '#')
                {
                    g.setColor(new Color(30,30,30));
                }
                else if (mg.maze[i][j] == 'E')
                {
                    g.setColor(new Color(0,180,3));
                }
                else
                {
                    g.setColor(new Color(220,220,220));
                }

                g.fillRect(offsetX+j*cellSize, offsetY+i*cellSize , cellSize,cellSize);
            }
        }
        for(Point coin : coins)
        {
            g.setColor(Color.YELLOW);
            g.fillOval(offsetX+coin.y*cellSize , offsetY+coin.x*cellSize , cellSize , cellSize);
        }

        g.setColor(new Color(200,50,50));
        g.fillOval(offsetX+playerC*cellSize, offsetY+playerR*cellSize,cellSize, cellSize);


        g.setFont(new Font("Arial", Font.BOLD,27));
        String info = "Level: "+level+"    Steps: "+steps+"    Score: "+score+"    Time: "+timeLeft;

        FontMetrics fm = g.getFontMetrics();
        int textWidth = fm.stringWidth(info);
        g.setColor(Color.BLACK);

        g.drawString(info, (panelWidth-textWidth)/2 , 30);
        // shortest path ------------
        if(showPath) {
            g.setColor(new Color(0,0,255,100));
            for(Point p : shortestPath) {
                g.fillRect(offsetX + p.y*cellSize,
                        offsetY + p.x*cellSize,
                        cellSize, cellSize);
            }
        }
        //-----------------------------------------
        //pause----------------
        if(paused) {
            g.setColor(new Color(0,0,0,150));
            g.fillRect(0,0,getWidth(),getHeight());

            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 40));
            g.drawString("PAUSED", getWidth()/2 - 100, getHeight()/2);
        }
//------------------------------
    }

    public void keyPressed(KeyEvent e)
    {
        if(e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            paused = !paused;

            if(paused) gameTimer.stop();
            else gameTimer.start();

            repaint();
            return;
        }

        if(paused || gameOver) return;

        if(e.getKeyCode() == KeyEvent.VK_S) saveGame();
        if(e.getKeyCode() == KeyEvent.VK_L) loadGame();

        if(e.getKeyCode()==KeyEvent.VK_UP ||
                e.getKeyCode()==KeyEvent.VK_DOWN ||
                e.getKeyCode()==KeyEvent.VK_LEFT ||
                e.getKeyCode()==KeyEvent.VK_RIGHT) {

            int r = playerR;
            int c = playerC;

            if(e.getKeyCode()==KeyEvent.VK_UP) r--;
            if(e.getKeyCode()==KeyEvent.VK_DOWN) r++;
            if(e.getKeyCode()==KeyEvent.VK_LEFT) c--;
            if(e.getKeyCode()==KeyEvent.VK_RIGHT) c++;

            if(r >= 0 && c >= 0 && r < mg.rows && c < mg.cols
                    && mg.maze[r][c] != '#') {

                playerR = r;
                playerC = c;
                steps++;

                Iterator<Point> it = coins.iterator();
                while(it.hasNext()) {
                    Point coin = it.next();
                    if(coin.x == playerR && coin.y == playerC) {
                        score += 10;
                        it.remove();
                    }
                }
            }
        }

        repaint();

        if(mg.maze[playerR][playerC] == 'E')
        {
            sound.stop();
            sound.playOnce("win.wav");

            Timer timer = new Timer(5000, t -> {
                sound.playLoop("bg.wav");
            });
            timer.setRepeats(false);
            timer.start();
            JOptionPane.showMessageDialog(this, "You Win!!");
            playerR = 1;
            playerC = 1;
            steps = 0;
            level++;
            startTimer();
            gameOver = false;
            mg = new MazeGenerator(10+level*5);
            generateCoins(level*3);
            repaint();
        }
        //shortest path --------------
        if(e.getKeyCode() == KeyEvent.VK_P) {
            showPath = !showPath;
            if(showPath) findShortestPath();
        }
        //-----------
    }
    public void keyReleased(KeyEvent e)
    {

    }
    public void keyTyped(KeyEvent e)
    {

    }
    void generateCoins(int count)
    {
        coins.clear();
        Random rand = new Random();

        while(coins.size() < count)
        {
            int r = rand.nextInt(mg.rows);
            int c = rand.nextInt(mg.cols);

            if(mg.maze[r][c] == '.'
                    && !(r == 1 && c == 1)
                    && !(r == mg.rows-2 && c == mg.cols-2))
            {
                coins.add(new Point(r,c));
            }
        }
    }
    //---------------------
    void findShortestPath() {

        boolean[][] visited = new boolean[mg.rows][mg.cols];
        Point[][] parent = new Point[mg.rows][mg.cols];

        java.util.Queue<Point> q = new LinkedList<>();
        q.add(new Point(1,1));
        visited[1][1] = true;

        while(!q.isEmpty()) {
            Point p = q.poll();

            if(mg.maze[p.x][p.y] == 'E') {
                // reconstruct path
                Point cur = p;
                shortestPath.clear();

                while(cur != null) {
                    shortestPath.add(cur);
                    cur = parent[cur.x][cur.y];
                }
                return;
            }

            int[][] dirs = {{1,0},{-1,0},{0,1},{0,-1}};
            for(int[] d : dirs) {
                int nr = p.x + d[0];
                int nc = p.y + d[1];

                if(nr>=0 && nc>=0 && nr<mg.rows && nc<mg.cols &&
                        !visited[nr][nc] && mg.maze[nr][nc] != '#') {

                    visited[nr][nc] = true;
                    parent[nr][nc] = p;
                    q.add(new Point(nr,nc));
                }
            }
        }
    }

    void saveGame() {
        try(PrintWriter pw = new PrintWriter("save.txt")) {
            pw.println(level);
            pw.println(score);
            pw.println(steps);
        } catch(Exception e) {}
    }

    void loadGame() {
        try(Scanner sc = new Scanner(new File("save.txt"))) {
            level = sc.nextInt();
            score = sc.nextInt();
            steps = sc.nextInt();

            mg = new MazeGenerator(10 + level * 5);
            generateCoins(level * 3);

            playerR = 1;
            playerC = 1;

            startTimer();

        } catch(Exception e) {
            JOptionPane.showMessageDialog(this, "No Save Found!");
        }
    }

}
