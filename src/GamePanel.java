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

    int timeLeft;

    Timer gameTimer;

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

    // =========================
    // BUTTON AREAS
    // =========================

    Rectangle homeButton =
            new Rectangle(20,20,120,40);

    Rectangle pathButton =
            new Rectangle(160,20,180,40);

    Rectangle pauseButton =
            new Rectangle(740,20,120,40);

    Rectangle resumeButton =
            new Rectangle(330,300,240,55);

    Rectangle restartButton =
            new Rectangle(330,380,240,55);

    Rectangle menuButton =
            new Rectangle(330,460,240,55);

    // =========================
    // CONSTRUCTOR
    // =========================

    public GamePanel(GameFrame frame)
    {
        this.frame = frame;

        setBackground(new Color(235,235,235));

        setFocusable(true);

        addKeyListener(this);

        addMouseListener(this);

        setLevel(1);

        sound.playLoop("bg.wav");
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

        showPath = false;

        shortestPath.clear();

        mg = new MazeGenerator(10 + level * 5);

        generateCoins(level * 3);

        frame.gameState = GameState.PLAYING;

        startTimer(true);

        repaint();
    }

    // =========================
    // TIMER
    // =========================

    void startTimer(boolean resetTime)
    {
        if(resetTime)
        {
            timeLeft = 60 + level * 10;
        }

        if(gameTimer != null)
        {
            gameTimer.stop();
        }

        gameTimer = new Timer(1000, e -> {

            if(frame.gameState != GameState.PLAYING)
            {
                return;
            }

            timeLeft--;

            if(timeLeft <= 0)
            {
                frame.gameState = GameState.GAME_OVER;

                gameTimer.stop();

                sound.stopBackground();

                JOptionPane.showMessageDialog(
                        this,
                        "Time Up!"
                );

                frame.showHome();
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

        Graphics2D g2 = (Graphics2D) g;

        g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );

        int topMargin = 90;

        int panelWidth = getWidth();

        int panelHeight = getHeight();

        int cellSize = Math.min(
                panelWidth / mg.cols,
                (panelHeight - topMargin) / mg.rows
        );

        int offsetX =
                (panelWidth - mg.cols * cellSize) / 2;

        int offsetY =
                topMargin +
                        (panelHeight - topMargin -
                                mg.rows * cellSize) / 2;

        // =========================
        // TOP BAR
        // =========================

        g.setColor(new Color(25,25,25));

        g.fillRect(0,0,getWidth(),80);

        g.setColor(Color.WHITE);

        g.setFont(new Font(
                "Arial",
                Font.BOLD,
                22
        ));

        g.drawString(
                "LEVEL : " + level,
                360,
                30
        );

        g.drawString(
                "STEPS : " + steps,
                360,
                60
        );

        g.drawString(
                "SCORE : " + score,
                560,
                30
        );

        g.drawString(
                "TIME : " + timeLeft,
                560,
                60
        );

        // =========================
        // BUTTONS
        // =========================

        drawButton(g, homeButton, "HOME");

        drawButton(
                g,
                pathButton,
                showPath ? "HIDE PATH" : "SHOW PATH"
        );

        drawButton(
                g,
                pauseButton,
                frame.gameState == GameState.PAUSED
                        ? "RESUME"
                        : "PAUSE"
        );


        // =========================
        // DRAW MAZE
        // =========================

        for(int i = 0; i < mg.rows; i++)
        {
            for(int j = 0; j < mg.cols; j++)
            {
                if(mg.maze[i][j] == '#')
                {
                    g.setColor(new Color(25,25,25));
                }
                else if(mg.maze[i][j] == 'E')
                {
                    g.setColor(new Color(0,200,80));
                }
                else
                {
                    g.setColor(new Color(230,230,230));
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
        // PATH
        // =========================

        if(showPath)
        {
            g.setColor(new Color(0,0,255,90));

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
                    offsetX + coin.y * cellSize + 4,
                    offsetY + coin.x * cellSize + 4,
                    cellSize - 8,
                    cellSize - 8
            );
        }

        // =========================
        // PLAYER
        // =========================

        g.setColor(new Color(220,40,40));

        g.fillOval(
                offsetX + playerC * cellSize + 2,
                offsetY + playerR * cellSize + 2,
                cellSize - 4,
                cellSize - 4
        );

        // =========================
        // PAUSE SCREEN
        // =========================

        if(frame.gameState == GameState.PAUSED)
        {
            g.setColor(new Color(0,0,0,170));

            g.fillRect(0,0,getWidth(),getHeight());

            g.setColor(Color.WHITE);

            g.setFont(new Font(
                    "Arial",
                    Font.BOLD,
                    50
            ));

            g.drawString(
                    "GAME PAUSED",
                    250,
                    200
            );

            drawButton(g, resumeButton, "RESUME");

            drawButton(g, restartButton, "RESTART");

            drawButton(g, menuButton, "MAIN MENU");
        }
    }

    // =========================
    // BUTTON DRAW METHOD
    // =========================

    void drawButton(
            Graphics g,
            Rectangle r,
            String text
    )
    {
        g.setColor(new Color(45,45,45));

        g.fillRoundRect(
                r.x,
                r.y,
                r.width,
                r.height,
                20,
                20
        );

        g.setColor(Color.WHITE);

        g.setFont(new Font(
                "Arial",
                Font.BOLD,
                20
        ));

        FontMetrics fm = g.getFontMetrics();

        int textX =
                r.x + (r.width - fm.stringWidth(text))/2;

        int textY =
                r.y + ((r.height + fm.getAscent())/2) - 5;

        g.drawString(text, textX, textY);
    }

    // =========================
    // KEY EVENTS
    // =========================

    public void keyPressed(KeyEvent e)
    {
        // =========================
        // ESCAPE = PAUSE
        // =========================

        if(e.getKeyCode() == KeyEvent.VK_ESCAPE)
        {
            if(frame.gameState == GameState.PLAYING)
            {
                frame.gameState = GameState.PAUSED;
            }
            else if(frame.gameState == GameState.PAUSED)
            {
                frame.gameState = GameState.PLAYING;
            }

            repaint();

            return;
        }

        if(frame.gameState != GameState.PLAYING)
        {
            return;
        }

        // =========================
        // SAVE
        // =========================

        if(e.getKeyCode() == KeyEvent.VK_S)
        {
            saveGame();
        }

        // =========================
        // LOAD
        // =========================

        if(e.getKeyCode() == KeyEvent.VK_L)
        {
            loadGame();
        }

        // =========================
        // SHOW PATH
        // =========================

        if(e.getKeyCode() == KeyEvent.VK_P)
        {
            showPath = !showPath;

            if(showPath)
            {
                findShortestPath();
            }

            repaint();

            return;
        }

        // =========================
        // PLAYER MOVEMENT
        // =========================

        int r = playerR;

        int c = playerC;

        if(e.getKeyCode() == KeyEvent.VK_UP)
        {
            r--;
        }

        if(e.getKeyCode() == KeyEvent.VK_DOWN)
        {
            r++;
        }

        if(e.getKeyCode() == KeyEvent.VK_LEFT)
        {
            c--;
        }

        if(e.getKeyCode() == KeyEvent.VK_RIGHT)
        {
            c++;
        }

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

            Iterator<Point> it =
                    coins.iterator();

            while(it.hasNext())
            {
                Point coin = it.next();

                if(
                        coin.x == playerR &&
                                coin.y == playerC
                )
                {
                    score += 10;

                    sound.playEffect("coin.wav");

                    it.remove();
                }
            }
        }

        repaint();

        // =========================
        // WIN
        // =========================

        if(mg.maze[playerR][playerC] == 'E')
        {
            frame.gameState = GameState.GAME_OVER;

            gameTimer.stop();

            sound.stopBackground();

            sound.playEffect("win.wav");

            Timer winTimer = new Timer(5000, et ->
            {
                sound.stopEffect();
            });

            winTimer.setRepeats(false);

            winTimer.start();


            String[] options = {
                    "Next Level",
                    "Replay",
                    "Home",
                    "Exit"
            };

            int choice = JOptionPane.showOptionDialog(
                    this,
                    "LEVEL COMPLETED!",
                    "SUCCESS",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    null,
                    options,
                    options[0]
            );

            // =========================
            // NEXT LEVEL
            // =========================

            if(choice == 0)
            {
                level++;

                if(level > frame.unlockedLevel)
                {
                    frame.unlockedLevel = level;
                }

                setLevel(level);

                sound.playLoop("bg.wav");
            }

            // =========================
            // REPLAY
            // =========================

            else if(choice == 1)
            {
                setLevel(level);

                sound.playLoop("bg.wav");
            }

            // =========================
            // HOME
            // =========================

            else if(choice == 2)
            {
                frame.showHome();

                sound.playLoop("bg.wav");
            }

            // =========================
            // EXIT
            // =========================

            else
            {
                System.exit(0);
            }
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

        Queue<Point> q =
                new LinkedList<>();

        q.add(new Point(playerR, playerC));

        visited[playerR][playerC] = true;

        while(!q.isEmpty())
        {
            Point p = q.poll();

            if(mg.maze[p.x][p.y] == 'E')
            {
                shortestPath.clear();

                Point cur = p;

                while(cur != null)
                {
                    shortestPath.add(cur);

                    cur =
                            parent[cur.x][cur.y];
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
    // SAVE GAME
    // =========================

    void saveGame()
    {
        try(PrintWriter pw =
                    new PrintWriter("save.txt"))
        {
            pw.println(level);

            pw.println(score);

            pw.println(steps);

            pw.println(timeLeft);

            pw.println(playerR);

            pw.println(playerC);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    // =========================
    // LOAD GAME
    // =========================

    void loadGame()
    {
        try(Scanner sc =
                    new Scanner(new File("save.txt")))
        {
            level = sc.nextInt();

            score = sc.nextInt();

            steps = sc.nextInt();

            timeLeft = sc.nextInt();

            playerR = sc.nextInt();

            playerC = sc.nextInt();

            mg = new MazeGenerator(
                    10 + level * 5
            );

            generateCoins(level * 3);

            frame.gameState =
                    GameState.PLAYING;

            startTimer(false);

            repaint();
        }
        catch(Exception e)
        {
            JOptionPane.showMessageDialog(
                    this,
                    "NO SAVE FOUND!"
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

        // =========================
        // PAUSE BUTTON
        // =========================

        if(pauseButton.contains(x,y))
        {
            if(frame.gameState == GameState.PLAYING)
            {
                frame.gameState = GameState.PAUSED;
            }
            else if(frame.gameState == GameState.PAUSED)
            {
                frame.gameState = GameState.PLAYING;
            }

            repaint();

            return;
        }

        // =========================
        // PAUSE MENU BUTTONS
        // =========================

        if(frame.gameState == GameState.PAUSED)
        {
            if(resumeButton.contains(x,y))
            {
                frame.gameState =
                        GameState.PLAYING;
            }

            else if(restartButton.contains(x,y))
            {
                setLevel(level);
            }

            else if(menuButton.contains(x,y))
            {
                frame.showHome();
            }

            repaint();

            return;
        }

        if(frame.gameState != GameState.PLAYING)
        {
            return;
        }

        // =========================
        // HOME BUTTON
        // =========================

        if(homeButton.contains(x,y))
        {
            frame.showHome();
        }

        // =========================
        // PATH BUTTON
        // =========================

        if(pathButton.contains(x,y))
        {
            showPath = !showPath;

            if(showPath)
            {
                findShortestPath();
            }

            repaint();
        }


    }

    public void mousePressed(MouseEvent e) {}

    public void mouseReleased(MouseEvent e) {}

    public void mouseEntered(MouseEvent e) {}

    public void mouseExited(MouseEvent e) {}
}

