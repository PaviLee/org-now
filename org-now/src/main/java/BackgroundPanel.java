import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

/**
 * GUI Class: A specialized JPanel that is used to display an image as its
 * background.
 *
 * @author Pavi Lee
 * @version September 5, 2020
 */
public class BackgroundPanel extends JPanel {

    private static final long serialVersionUID = -4623646467273651538L;

    private BufferedImage bImage;

    /**
     * Creates a BackgroundPanel.
     */
    public BackgroundPanel() {
        super();
        try {
            bImage = ImageIO.read(getClass().getResource("/images/background" +
                    ".png"));
            bImage = resize(bImage, 1100, 700);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns a resized version of the inputed image.
     *
     * @param img    BufferedImage to resize
     * @param width  new width
     * @param height new height
     * @return a resized BufferedImage.
     */
    private static BufferedImage resize(BufferedImage img, int width,
										int height) {
        Image temp = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        BufferedImage resized = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = resized.createGraphics();
        g2d.drawImage(temp, 0, 0, null);
        g2d.dispose();
        return resized;
    }

    @Override
    /*
     * (non-Javadoc)
     *
     * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
     */
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(bImage, 0, 0, this);
    }
}