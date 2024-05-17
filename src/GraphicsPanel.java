import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class GraphicsPanel extends JPanel implements KeyListener, MouseListener, ActionListener {
    private BufferedImage background;
    private Player player;
    private boolean[] pressedKeys;
    private ArrayList<Coin> coins;
    private Timer timer;
    private int time;
    private JButton resetButton;
    private boolean stageTwo = false;
    private JButton pauseButton;
    private boolean gamePaused = false;

    public GraphicsPanel(String name) {
        try {
            background = ImageIO.read(new File("src/background.png"));
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        player = new Player("src/marioleft.png", "src/marioright.png", name);
        coins = new ArrayList<>();
        pressedKeys = new boolean[128];
        time = 0;
        timer = new Timer(1000, this); // this Timer will call the actionPerformed interface method every 1000ms = 1 second
        resetButton = new JButton("RESET");
        resetButton.setFocusable(false);
        pauseButton = new JButton("PAUSE");
        pauseButton.setFocusable(false);
        timer.start();
        addKeyListener(this);
        addMouseListener(this);
        add(resetButton);
        resetButton.addActionListener(this);
        add(pauseButton);
        pauseButton.addActionListener(this);
        setFocusable(true); // this line of code + one below makes this panel active for keylistener events
        requestFocusInWindow(); // see comment above
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);  // just do this
        g.drawImage(background, 0, 0, null);  // the order that things get "painted" matter; we put background down first
        g.drawImage(player.getPlayerImage(), player.getxCoord(), player.getyCoord(), null);

        if (player.getScore() >= 10 && !stageTwo) {
            try {
                background = ImageIO.read(new File("src/background2.png"));
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
            player.setPlayerImageRight("src/mariofrogright.png");
            player.setPlayerImageLeft("src/mariofrogleft.png");
            stageTwo = true;
            coins.clear();
        }

        if (player.getScore() == 0 && stageTwo) {
            try {
                background = ImageIO.read(new File("src/background.png"));
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
            player.setPlayerImageRight("src/marioright.png");
            player.setPlayerImageLeft("src/marioleft.png");
            stageTwo = false;
            coins.clear();
        }

        // this loop does two things:  it draws each Coin that gets placed with mouse clicks,
        // and it also checks if the player has "intersected" (collided with) the Coin, and if so,
        // the score goes up and the Coin is removed from the arraylist
        if (!gamePaused) {
            for (int i = 0; i < coins.size(); i++) {
                Coin coin = coins.get(i);
                g.drawImage(coin.getImage(), coin.getxCoord(), coin.getyCoord(), null); // draw Coin
                if (player.playerRect().intersects(coin.coinRect())) { // check for collision
                    player.collectCoin();
                    coins.remove(i);
                    i--;
                }
            }
        }

        // draw score
        g.setFont(new Font("Courier New", Font.BOLD, 24));
        g.drawString(player.getName() + "'s Score: " + player.getScore(), 20, 40);
        g.drawString("Time: " + time, 20, 70);
        resetButton.setLocation(20, 80);
        pauseButton.setLocation(20, 120);

        // player moves left (A)
        if (pressedKeys[65] && !gamePaused) {
            player.faceLeft();
            player.moveLeft();
        }

        // player moves right (D)
        if (pressedKeys[68] && !gamePaused) {
            player.faceRight();
            player.moveRight();
        }

        // player moves up (W)
        if (pressedKeys[87] && !gamePaused) {
            player.moveUp();
        }

        // player moves down (S)
        if (pressedKeys[83] && !gamePaused) {
            player.moveDown();
        }
    }

    // ----- KeyListener interface methods -----
    public void keyTyped(KeyEvent e) { } // unimplemented

    public void keyPressed(KeyEvent e) {
        // see this for all keycodes: https://stackoverflow.com/questions/15313469/java-keyboard-keycodes-list
        // A = 65, D = 68, S = 83, W = 87, left = 37, up = 38, right = 39, down = 40, space = 32, enter = 10
        int key = e.getKeyCode();
        pressedKeys[key] = true;
    }

    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        pressedKeys[key] = false;
    }

    // ----- MouseListener interface methods -----
    public void mouseClicked(MouseEvent e) { }  // unimplemented; if you move your mouse while clicking,
    // this method isn't called, so mouseReleased is best

    public void mousePressed(MouseEvent e) { } // unimplemented

    public void mouseReleased(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {  // left mouse click
            Point mouseClickLocation = e.getPoint();
            Coin coin = new Coin(mouseClickLocation.x, mouseClickLocation.y);
            coins.add(coin);
        } else {
            Point mouseClickLocation = e.getPoint();
            if (player.playerRect().contains(mouseClickLocation)) {
                player.turn();
            }
        }
    }

    public void mouseEntered(MouseEvent e) { } // unimplemented

    public void mouseExited(MouseEvent e) { } // unimplemented

    // ACTIONLISTENER INTERFACE METHODS: used for buttons AND timers!
    public void actionPerformed(ActionEvent e) {
        JButton button = (JButton) e.getSource();
        if (e.getSource() instanceof Timer && !gamePaused) {
            time++;
        }
        else if (button == resetButton && !gamePaused) {
            player.setScore(0);
            player.setxCoord(50);
            player.setyCoord(435);
        }
        else if (button == pauseButton) {
            if (gamePaused) {
                pauseButton.setText("PAUSE");

            }
            pauseButton.setText("PAUSED");
            gamePaused = true;

        }
    }
}
