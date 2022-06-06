import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.concurrent.SubmissionPublisher;

public class GUI implements KeyListener, ActionListener
{
    private String[][] winMatrix;
    private Maze maze;
    private JFrame frame;
    private JPanel sliderPanel;
    private JPanel textFieldPanel;
    private JPanel submitPanel;
    private JPanel mazePanel;
    private JPanel timerPanel;
    private JPanel tipPanel;
    private JTextField characterSelector;
    private JSlider slider;
    private TimerClass timer;

    public GUI()
    {
        timer = new TimerClass();
        winMatrix = new String[6][45];
    }

    public void initialize()
    {
        frame = new JFrame("MAZE");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setFocusable(true);
        frame.addKeyListener(this);

        slider = new JSlider(30, 70);
        slider.setMinorTickSpacing(5);
        slider.setMajorTickSpacing(10);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        slider.setSnapToTicks(true);

        sliderPanel = new JPanel();
        JLabel sliderText = new JLabel("What size do you want your maze to be?");
        sliderPanel.add(sliderText);
        sliderPanel.add(slider);
        frame.add(sliderPanel, BorderLayout.NORTH);

        characterSelector = new JTextField(1);
        JLabel textFieldText = new JLabel("What letter/number do you want to be? ");

        textFieldPanel = new JPanel();
        textFieldPanel.add(textFieldText, BorderLayout.WEST);
        textFieldPanel.add(characterSelector, BorderLayout.EAST);
        frame.add(textFieldPanel, BorderLayout.CENTER);

        submitPanel = new JPanel();
        JButton submitButton = new JButton("Submit");
        submitButton.addActionListener(this);
        submitPanel.add(submitButton, BorderLayout.CENTER);
        frame.add(submitPanel, BorderLayout.SOUTH);

        frame.pack();
        frame.setVisible(true);

    }

    public void reload()
    {
        frame.remove(timerPanel);
        timerPanel = new JPanel();
        JLabel timerLabel = new JLabel("Time: " + timer.getTime());
        timerPanel.add(timerLabel);

        frame.remove(mazePanel);
        mazePanel = new JPanel(new GridLayout(maze.getSize(), maze.getSize(), 0, -5));
        for (int i = 0; i < maze.getMaze().length; i++)
        {
            for (int j = 0; j < maze.getMaze()[0].length; j++)
            {
                JLabel l = new JLabel(maze.getMaze()[i][j]);
                mazePanel.add(l);
            }
        }

        frame.remove(tipPanel);
        tipPanel = new JPanel();
        JLabel tipLabel = new JLabel("Tip: Press SPACE to go back to the beginning!");
        tipPanel.add(tipLabel);
        frame.add(timerPanel, BorderLayout.NORTH);
        frame.add(mazePanel);
        frame.add(tipPanel, BorderLayout.SOUTH);

        SwingUtilities.updateComponentTreeUI(frame);
    }

    private void showWinningScreen()
    {
        frame.setSize(560, 220);
        youWinInitializer();
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new GridLayout(winMatrix.length, winMatrix[0].length, 0, 0));
        for (int i = 0; i < winMatrix.length; i++)
        {
            for (int j = 0; j < winMatrix[0].length; j++)
            {
                JLabel l = new JLabel(winMatrix[i][j]);
                textPanel.add(l);
            }
        }
        frame.add(textPanel);

    }

    public void youWinInitializer()
    {
        stringIntoArray(0, "Y   Y  OOO  U   U     W     W III N   N    !!");
        stringIntoArray(1, " Y Y  O   O U   U     W     W  I  NN  N    !!");
        stringIntoArray(2, "  Y   O   O U   U     W  W  W  I  N N N    !!");
        stringIntoArray(3, "  Y   O   O U   U      W W W   I  N  NN      ");
        stringIntoArray(4, "  Y    OOO   UUU        W W   III N   N    !!");
        stringIntoArray(5, "                                             ");
    }

    public void stringIntoArray(int row, String str)
    {
        for (int i = 0; i < str.length(); i++)
        {
            winMatrix[row][i] = str.substring(i, i+1);
        }
    }

    public void animateYouWin(String direction)
    {
        if (direction.equals("up"))
        {
            for (int i = 0; i < 5; i++)
            {
                for (int j = 0; j < 45; j++)
                {
                    winMatrix[i][j] = winMatrix[i+1][j];
                }
            }
        }
        else if (direction.equals("down"))
        {
            for (int i = 5; i > 0; i--)
            {
                for (int j = 0; j < 45; j++)
                {
                    winMatrix[i][j] = winMatrix[i-1][j];
                }
            }
        }
    }

    @Override
    public void keyPressed(KeyEvent e)
    {
        //System.out.println(frame.getHeight() + " " + frame.getWidth());
        int keyCode = e.getKeyCode();
        boolean win = false;
        if (keyCode == KeyEvent.VK_W) {
            win = maze.move("up");
        } else if (keyCode == KeyEvent.VK_S) {
            win = maze.move("down");
        } else if (keyCode == KeyEvent.VK_A) {
            win = maze.move("left");
        } else if (keyCode == KeyEvent.VK_D) {
            win = maze.move("right");
        } else if (keyCode == KeyEvent.VK_SPACE) {
            maze.moveFace(new Coordinate(1, 0));
        } else if (keyCode == KeyEvent.VK_P) {
            maze.moveFace(new Coordinate(maze.getSize()-2, maze.getSize()-2));
        } else if (keyCode == KeyEvent.VK_U) {
            System.out.println(frame.getWidth() + " " + frame.getHeight());
        }
        if (keyCode != KeyEvent.VK_U)
            reload();
        if (win)
        {
            frame.remove(timerPanel);
            frame.remove(mazePanel);
            frame.remove(tipPanel);
            timer.stopTimer();
            showWinningScreen();
        }
    }

    @Override
    public void keyReleased(KeyEvent e)
    {

    }

    @Override
    public void keyTyped(KeyEvent e)
    {

    }

    public void actionPerformed(ActionEvent e)
    {
        int size = slider.getValue();
        maze = new Maze(size);
        maze.setFace(characterSelector.getText());
        maze.makeMaze(new Coordinate(1, 1));
        maze.makeMaze(new Coordinate(size - 2, size - 2));
        maze.cleanUpMaze();
        mazePanel = new JPanel(new GridLayout(maze.getSize(), maze.getSize(), 0, -5));
        frame.remove(sliderPanel);
        frame.remove(textFieldPanel);
        frame.remove(submitPanel);
        frame.add(mazePanel);
        frame.setSize(size * 10 + 25, size * 10 + 55);
        timer.startTimer();
        timerPanel = new JPanel();
        tipPanel = new JPanel();
        reload();
    }
}
