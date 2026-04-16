import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GamePanel extends JPanel implements KeyListener {

    MazeGenerator mg;

    int playerR = 1;
    int playerC = 1;

    int steps = 0;
    int level = 1;

    public GamePanel() {
        setBackground(Color.WHITE);

        mg = new MazeGenerator(15);

        setFocusable(true);
        addKeyListener(this);
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        int cellSize = 30;

        // Draw Maze
        for (int i = 0; i < mg.rows; i++) {
            for (int j = 0; j < mg.cols; j++) {

                if (mg.maze[i][j] == '#') g.setColor(Color.BLACK);
                else if (mg.maze[i][j] == 'E') g.setColor(Color.GREEN);
                else g.setColor(Color.LIGHT_GRAY);

                g.fillRect(j * cellSize, i * cellSize, cellSize, cellSize);
            }
        }

        // Player
        g.setColor(Color.RED);
        g.fillOval(playerC * cellSize,
                playerR * cellSize,
                cellSize, cellSize);

        // Info text
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.drawString("Level: " + level + "   Steps: " + steps, 10, 20);
    }

    public void keyPressed(KeyEvent e) {

        int r = playerR;
        int c = playerC;

        if (e.getKeyCode() == KeyEvent.VK_UP) r--;
        if (e.getKeyCode() == KeyEvent.VK_DOWN) r++;
        if (e.getKeyCode() == KeyEvent.VK_LEFT) c--;
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) c++;

        if (r >= 0 && c >= 0 &&
                r < mg.rows && c < mg.cols &&
                mg.maze[r][c] != '#') {

            playerR = r;
            playerC = c;
            steps++;
        }

        // Goal reached
        if (mg.maze[playerR][playerC] == 'E') {
            JOptionPane.showMessageDialog(this, "Reached Goal!");
        }

        repaint();
    }

    public void keyReleased(KeyEvent e) {}
    public void keyTyped(KeyEvent e) {}
}