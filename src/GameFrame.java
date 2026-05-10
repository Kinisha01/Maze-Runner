import javax.swing.*;
import java.awt.*;

public class GameFrame extends JFrame {

    CardLayout cardLayout;
    JPanel mainPanel;

    HomePanel homePanel;
    GamePanel gamePanel;

    int unlockedLevel = 3;

    GameFrame() {

        setTitle("Maze Runner");
        setSize(900, 900);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        homePanel = new HomePanel(this);
        gamePanel = new GamePanel(this);

        mainPanel.add(homePanel, "HOME");
        mainPanel.add(gamePanel, "GAME");

        add(mainPanel);

        cardLayout.show(mainPanel, "HOME");

        setVisible(true);
    }

    void startLevel(int level) {

        gamePanel.setLevel(level);

        cardLayout.show(mainPanel, "GAME");

        gamePanel.requestFocusInWindow();
    }

    void showHome() {

        homePanel = new HomePanel(this);

        mainPanel.add(homePanel, "HOME");

        cardLayout.show(mainPanel, "HOME");
    }
}

