import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class SpikedBall {
    private int xCoord;
    private int yCoord;
    private BufferedImage image;

    public SpikedBall(int x, int y) {
        xCoord = x;
        yCoord = y;
        try {
            image = ImageIO.read(new File("src/spikedball.png"));
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public int getxCoord() {
        return xCoord;
    }

    public int getyCoord() {
        return yCoord;
    }

    public BufferedImage getImage() {
        return image;
    }

    public Rectangle spikedBallRect() {
        int imageHeight = getImage().getHeight();
        int imageWidth = getImage().getWidth();
        Rectangle rect = new Rectangle(xCoord, yCoord, imageWidth, imageHeight);
        return rect;
    }
}
