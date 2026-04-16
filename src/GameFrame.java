import javax.swing.*;

public class GameFrame extends JFrame {

    public GameFrame() {
        setTitle("Maze Runner - Phase 2");
        setSize(700, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        add(new GamePanel());

        setVisible(true);
    }
}