
import javax.swing.*;
import java.awt.*;

public class GameFrame extends JFrame
{
    GameFrame()
    {
        setFont(new Font("Arial", Font.BOLD,59));
        setTitle("Maze Runner");
        setSize(800 ,800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);//center screen
        add(new GamePanel());
        setVisible(true);

    }
}
