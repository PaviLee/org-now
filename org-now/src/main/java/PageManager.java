import java.awt.*;
import java.util.Stack;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * Creates a JFrame to contain JPanels and has methods to handle page addition,
 * removal, and display.
 *
 * @author Pavi Lee
 * @version September 5, 2020
 */
public class PageManager {

    private static String blank = "Blank";

    private JFrame frame;
    private JPanel mainPanel;
    private CardLayout cardLayout;
    private Stack<String> pages;

    /**
     * Creates a PageManager.
     *
     * @param x      int top left x coordinate of the JFrame
     * @param y      int top left y coordinate of the JFrame
     * @param width  int width of the JFrame
     * @param height int height of the JFrame
     */
    public PageManager(int x, int y, int width, int height) {

        cardLayout = new CardLayout();

        frame = new JFrame();
        frame.setBounds(x, y, 0, 0);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        mainPanel = new JPanel();
        mainPanel.setPreferredSize(new Dimension(width, height));
        mainPanel.setLayout(cardLayout);
        frame.add(mainPanel);
        frame.pack();

        frame.setVisible(true);

        pages = new Stack<String>();

        JPanel blank = new JPanel();
        blank.setBackground(new Color(0, 0, 0, 0));
        mainPanel.add(PageManager.blank, blank);
        pages.add(PageManager.blank);
    }

    /**
     * Returns JFrame frame.
     *
     * @return JFrame frame.
     */
    public JFrame getJFrame() {
        return frame;
    }

    /**
     * Adds the inputed JPanel to the available pages and displays it.
     *
     * @param panel JPanel panel
     */
    public void addPage(JPanel panel) {
        String name = panel.getClass().getSimpleName();
        mainPanel.add(name, panel);
        pages.push(name);
        display();
    }

    /**
     * Removes the current page from the available pages.
     */
    public void removePage() {
        if (!pages.isEmpty() && !blank.equals(pages.peek())) {
            pages.pop();
        }
    }

    /**
     * Displays the current page.
     */
    public void display() {
        if (!pages.isEmpty()) {
            cardLayout.show(mainPanel, pages.peek());
        }
    }
}