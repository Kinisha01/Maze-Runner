import java.io.*;
import java.util.Scanner;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;


public class GamePanel extends JPanel implements KeyListener, MouseListener
{
    GameFrame frame;

    boolean paused = false;

    int timeLeft;
    Timer gameTimer;
    boolean gameOver = false;

    ArrayList<Point> shortestPath = new ArrayList<>();
    boolean showPath = false;

    ArrayList<Point> coins = new ArrayList<>();
    int score = 0;

    SoundPlayer sound = new SoundPlayer();

    MazeGenerator mg;

    int playerR = 1;
    int playerC = 1;

    int steps = 0;

    int level = 1;

    public GamePanel(GameFrame frame)
    {
        this.frame = frame;

        setBackground(Color.WHITE);

        mg = new MazeGenerator(10 + level * 5);

        generateCoins(level * 3);

        setFocusable(true);

        addKeyListener(this);

        addMouseListener(this);

        sound.playLoop("bg.wav");

        startTimer();
    }

    // =========================
    // SET LEVEL
    // =========================

    void setLevel(int level)
    {
        this.level = level;

        playerR = 1;
        playerC = 1;

        steps = 0;

        mg = new MazeGenerator(10 + level * 5);

        generateCoins(level * 3);

        startTimer();

        gameOver = false;

        repaint();
    }

    // =========================
    // TIMER
    // =========================

    void startTimer()
    {
        timeLeft = 60 + level * 10;

        if(gameTimer != null)
            gameTimer.stop();

        gameTimer = new Timer(1000, e -> {

            timeLeft--;

            if(timeLeft <= 0)
            {
                gameOver = true;

                gameTimer.stop();

                String[] options = {"Restart", "Home", "Exit"};

                int choice = JOptionPane.showOptionDialog(
                        this,
                        "Time Up!",
                        "Game Over",
                        JOptionPane.DEFAULT_OPTION,
                        JOptionPane.INFORMATION_MESSAGE,
                        null,
                        options,
                        options[0]
                );

                if(choice == 0)
                {
                    setLevel(level);
                }
                else if(choice == 1)
                {
                    frame.showHome();
                }
                else
                {
                    System.exit(0);
                }
            }

            repaint();
        });

        gameTimer.start();
    }

    // =========================
    // PAINT
    // =========================

    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);

        int topMargin = 80;

        Graphics2D g2 = (Graphics2D) g;

        g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );

        int panelWidth = getWidth();
        int panelHeight = getHeight();

        int cellSize = Math.min(
                panelWidth / mg.cols,
                (panelHeight - topMargin) / mg.rows
        );

        int offsetX = (panelWidth - mg.cols * cellSize) / 2;

        int offsetY = topMargin +
                (panelHeight - topMargin - mg.rows * cellSize) / 2;

        // =========================
        // HEADER INFO
        // =========================

        g.setColor(Color.BLACK);

        g.setFont(new Font("Arial", Font.BOLD, 24));

        String info =
                "Level: " + level +
                        "    Steps: " + steps +
                        "    Score: " + score +
                        "    Time: " + timeLeft;

        FontMetrics fm = g.getFontMetrics();

        int textWidth = fm.stringWidth(info);

        g.drawString(info, (panelWidth - textWidth) / 2, 40);

        // =========================
        // HOME BUTTON
        // =========================

        g.setColor(new Color(50,50,50));

        g.fillRoundRect(20, 20, 120, 40, 20, 20);

        g.setColor(Color.WHITE);

        g.setFont(new Font("Arial", Font.BOLD, 20));

        g.drawString("HOME", 45, 48);

        // =========================
        // PATH BUTTON
        // =========================

        g.setColor(new Color(50,50,50));

        g.fillRoundRect(160, 20, 180, 40, 20, 20);

        g.setColor(Color.WHITE);

        g.drawString("SHOW PATH", 175, 48);

        // =========================
        // DRAW MAZE
        // =========================

        for(int i = 0; i < mg.rows; i++)
        {
            for(int j = 0; j < mg.cols; j++)
            {
                if(mg.maze[i][j] == '#')
                {
                    g.setColor(new Color(30,30,30));
                }
                else if(mg.maze[i][j] == 'E')
                {
                    g.setColor(new Color(0,180,0));
                }
                else
                {
                    g.setColor(new Color(220,220,220));
                }

                g.fillRect(
                        offsetX + j * cellSize,
                        offsetY + i * cellSize,
                        cellSize,
                        cellSize
                );
            }
        }

        // =========================
        // SHORTEST PATH
        // =========================

        if(showPath)
        {
            g.setColor(new Color(0,0,255,100));

            for(Point p : shortestPath)
            {
                g.fillRect(
                        offsetX + p.y * cellSize,
                        offsetY + p.x * cellSize,
                        cellSize,
                        cellSize
                );
            }
        }

        // =========================
        // COINS
        // =========================

        for(Point coin : coins)
        {
            g.setColor(Color.YELLOW);

            g.fillOval(
                    offsetX + coin.y * cellSize,
                    offsetY + coin.x * cellSize,
                    cellSize,
                    cellSize
            );
        }

        // =========================
        // PLAYER
        // =========================

        g.setColor(new Color(200,50,50));

        g.fillOval(
                offsetX + playerC * cellSize,
                offsetY + playerR * cellSize,
                cellSize,
                cellSize
        );

        // =========================
        // PAUSE OVERLAY
        // =========================

        if(paused)
        {
            g.setColor(new Color(0,0,0,150));

            g.fillRect(0,0,getWidth(),getHeight());

            g.setColor(Color.WHITE);

            g.setFont(new Font("Arial", Font.BOLD, 40));

            g.drawString(
                    "PAUSED",
                    getWidth()/2 - 100,
                    getHeight()/2
            );
        }
    }

    // =========================
    // KEY EVENTS
    // =========================

    public void keyPressed(KeyEvent e)
    {
        // ESC → pause

        if(e.getKeyCode() == KeyEvent.VK_ESCAPE)
        {
            paused = !paused;

            if(paused)
                gameTimer.stop();
            else
                gameTimer.start();

            repaint();

            return;
        }

        if(paused || gameOver)
            return;

        // SAVE / LOAD

        if(e.getKeyCode() == KeyEvent.VK_S)
            saveGame();

        if(e.getKeyCode() == KeyEvent.VK_L)
            loadGame();

        // SHOW PATH

        if(e.getKeyCode() == KeyEvent.VK_P)
        {
            showPath = !showPath;

            if(showPath)
                findShortestPath();

            repaint();

            return;
        }

        // MOVEMENT

        if(
                e.getKeyCode() == KeyEvent.VK_UP ||
                        e.getKeyCode() == KeyEvent.VK_DOWN ||
                        e.getKeyCode() == KeyEvent.VK_LEFT ||
                        e.getKeyCode() == KeyEvent.VK_RIGHT
        )
        {
            int r = playerR;
            int c = playerC;

            if(e.getKeyCode() == KeyEvent.VK_UP) r--;
            if(e.getKeyCode() == KeyEvent.VK_DOWN) r++;
            if(e.getKeyCode() == KeyEvent.VK_LEFT) c--;
            if(e.getKeyCode() == KeyEvent.VK_RIGHT) c++;

            if(
                    r >= 0 &&
                            c >= 0 &&
                            r < mg.rows &&
                            c < mg.cols &&
                            mg.maze[r][c] != '#'
            )
            {
                playerR = r;
                playerC = c;

                steps++;

                Iterator<Point> it = coins.iterator();

                while(it.hasNext())
                {
                    Point coin = it.next();

                    if(coin.x == playerR && coin.y == playerC)
                    {
                        score += 10;

                        it.remove();
                    }
                }
            }
        }

        repaint();

        // =========================
        // WIN
        // =========================

        if(mg.maze[playerR][playerC] == 'E')
        {
            sound.stop();

            sound.playOnce("win.wav");

            String[] options = {
                    "Next Level",
                    "Replay",
                    "Home"
            };

            int choice = JOptionPane.showOptionDialog(
                    this,
                    "Level Complete!",
                    "Success",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    null,
                    options,
                    options[0]
            );

            if(choice == 0)
            {
                level++;

                if(level > frame.unlockedLevel)
                {
                    frame.unlockedLevel = level;
                }

                setLevel(level);
            }
            else if(choice == 1)
            {
                setLevel(level);
            }
            else
            {
                frame.showHome();
            }

            sound.playLoop("bg.wav");
        }
    }

    public void keyReleased(KeyEvent e) {}

    public void keyTyped(KeyEvent e) {}

    // =========================
    // COINS
    // =========================

    void generateCoins(int count)
    {
        coins.clear();

        Random rand = new Random();

        while(coins.size() < count)
        {
            int r = rand.nextInt(mg.rows);
            int c = rand.nextInt(mg.cols);

            if(
                    mg.maze[r][c] == '.' &&
                            !(r == 1 && c == 1)
            )
            {
                coins.add(new Point(r,c));
            }
        }
    }

    // =========================
    // SHORTEST PATH
    // =========================

    void findShortestPath()
    {
        boolean[][] visited =
                new boolean[mg.rows][mg.cols];

        Point[][] parent =
                new Point[mg.rows][mg.cols];

        Queue<Point> q = new LinkedList<>();

        q.add(new Point(playerR, playerC));

        visited[playerR][playerC] = true;

        while(!q.isEmpty())
        {
            Point p = q.poll();

            if(mg.maze[p.x][p.y] == 'E')
            {
                Point cur = p;

                shortestPath.clear();

                while(cur != null)
                {
                    shortestPath.add(cur);

                    cur = parent[cur.x][cur.y];
                }

                return;
            }

            int[][] dirs = {
                    {1,0},
                    {-1,0},
                    {0,1},
                    {0,-1}
            };

            for(int[] d : dirs)
            {
                int nr = p.x + d[0];
                int nc = p.y + d[1];

                if(
                        nr >= 0 &&
                                nc >= 0 &&
                                nr < mg.rows &&
                                nc < mg.cols &&
                                !visited[nr][nc] &&
                                mg.maze[nr][nc] != '#'
                )
                {
                    visited[nr][nc] = true;

                    parent[nr][nc] = p;

                    q.add(new Point(nr,nc));
                }
            }
        }
    }

    // =========================
    // SAVE / LOAD
    // =========================

    void saveGame()
    {
        try(PrintWriter pw = new PrintWriter("save.txt"))
        {
            pw.println(level);
            pw.println(score);
            pw.println(steps);
        }
        catch(Exception e) {}
    }

    void loadGame()
    {
        try(Scanner sc = new Scanner(new File("save.txt")))
        {
            level = sc.nextInt();
            score = sc.nextInt();
            steps = sc.nextInt();

            setLevel(level);
        }
        catch(Exception e)
        {
            JOptionPane.showMessageDialog(
                    this,
                    "No Save Found!"
            );
        }
    }

    // =========================
    // MOUSE EVENTS
    // =========================

    public void mouseClicked(MouseEvent e)
    {
        int x = e.getX();
        int y = e.getY();

        // HOME BUTTON

        if(
                x >= 20 &&
                        x <= 140 &&
                        y >= 20 &&
                        y <= 60
        )
        {
            frame.showHome();
        }

        // PATH BUTTON

        if(
                x >= 160 &&
                        x <= 340 &&
                        y >= 20 &&
                        y <= 60
        )
        {
            showPath = !showPath;

            if(showPath)
                findShortestPath();

            repaint();
        }
    }

    public void mousePressed(MouseEvent e) {}

    public void mouseReleased(MouseEvent e) {}

    public void mouseEntered(MouseEvent e) {}

    public void mouseExited(MouseEvent e) {}
}

