import javax.swing.*;
import java.awt.*;

public class HomePanel extends JPanel
{
    GameFrame frame;

    public HomePanel(GameFrame frame)
    {
        this.frame = frame;

        setLayout(new BorderLayout());

        // =========================
        // TITLE PANEL
        // =========================

        JPanel titlePanel = new JPanel();

        titlePanel.setBackground(new Color(20,20,20));

        titlePanel.setPreferredSize(new Dimension(900,120));

        titlePanel.setLayout(new GridLayout(2,1));

        JLabel title = new JLabel(
                "MAZE RUNNER",
                SwingConstants.CENTER
        );

        title.setForeground(Color.WHITE);

        title.setFont(new Font("Arial", Font.BOLD, 42));

        JLabel subtitle = new JLabel(
                "Java Swing Based Maze Game",
                SwingConstants.CENTER
        );

        subtitle.setForeground(Color.LIGHT_GRAY);

        subtitle.setFont(new Font("Arial", Font.PLAIN, 22));

        titlePanel.add(title);
        titlePanel.add(subtitle);

        add(titlePanel, BorderLayout.NORTH);

        // =========================
        // LEVEL PANEL
        // =========================

        JPanel levelPanel = new JPanel();

        levelPanel.setBackground(new Color(240,240,240));

        levelPanel.setBorder(
                BorderFactory.createEmptyBorder(
                        30,30,30,30
                )
        );

        levelPanel.setLayout(
                new GridLayout(0,3,20,20)
        );

        // =========================
        // DYNAMIC LEVELS
        // =========================

        int totalLevels =
                Math.max(frame.unlockedLevel + 3, 12);

        for(int i = 1; i <= totalLevels; i++)
        {
            JButton levelButton =
                    new JButton("Level " + i);

            levelButton.setFont(
                    new Font(
                            "Arial",
                            Font.BOLD,
                            24
                    )
            );

            levelButton.setFocusPainted(false);

            levelButton.setPreferredSize(
                    new Dimension(200,100)
            );

            // =========================
            // LOCK SYSTEM
            // =========================

            if(i > frame.unlockedLevel)
            {
                levelButton.setEnabled(false);

                levelButton.setText(
                        "🔒 Locked"
                );
            }

            else
            {
                levelButton.setBackground(
                        new Color(60,150,255)
                );

                levelButton.setForeground(
                        Color.WHITE
                );
            }

            int level = i;

            levelButton.addActionListener(e ->
            {
                frame.startLevel(level);
            });

            levelPanel.add(levelButton);
        }

        // =========================
        // SCROLL SUPPORT
        // =========================

        JScrollPane scrollPane =
                new JScrollPane(levelPanel);

        scrollPane.setBorder(null);

        add(scrollPane, BorderLayout.CENTER);
    }
}