import javax.swing.*;
import javax.swing.plaf.ColorUIResource;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

/**
 * GUI Class: Handles GUI and Action events for the main page.
 *
 * @author Pavi Lee
 * @version September 5, 2020
 */
public class MainPage extends BackgroundPanel implements ActionListener {

    private JComboBox labelSelector;
    private JButton importNow;

    public MainPage() throws GeneralSecurityException, IOException {
        this.setBackground(Resources.transparent);
        this.setLayout(new BorderLayout());

        this.add(Box.createRigidArea(new Dimension(0, 200)),
                BorderLayout.NORTH);
        this.add(Box.createRigidArea(new Dimension(0, 200)),
                BorderLayout.SOUTH);
        this.add(Box.createRigidArea(new Dimension(100, 0)), BorderLayout.EAST);
        this.add(Box.createRigidArea(new Dimension(100, 0)),
                BorderLayout.WEST);

        JPanel centerContainer = new JPanel();
        centerContainer.setBackground(Resources.transparent);
        centerContainer.setLayout(new BoxLayout(centerContainer,
                BoxLayout.Y_AXIS));
        this.add(centerContainer, BorderLayout.CENTER);

        JPanel textContainer = new JPanel();
        textContainer.setBackground(Resources.transparent);
        textContainer.setLayout(new BoxLayout(textContainer, BoxLayout.Y_AXIS));
        centerContainer.add(textContainer);

        JLabel title = new JLabel("OrgNow");
        title.setForeground(Resources.secondary);
        title.setFont(Resources.headerFont);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        textContainer.add(title);

        labelSelector = new JComboBox();
        List<String> labels = Gmail.getLabels();
        for (String label : labels) {
            labelSelector.addItem(label);
        }
        labelSelector.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerContainer.add(labelSelector);

        centerContainer.add(Box.createRigidArea(new Dimension(0, 25)));

        importNow = new JButton("Import Now");
        importNow.setForeground(Resources.secondary);
        importNow.setBackground(Resources.accent);
        importNow.setFont(Resources.bodyFont);
        importNow.setAlignmentX(Component.CENTER_ALIGNMENT);
        importNow.addActionListener(this);
        centerContainer.add(importNow);
    }

    private void displayErrorMessage(String errorMessage) {
        UIManager.put("OptionPane.background", new ColorUIResource(255, 255,
                255));
        UIManager.put("Panel.background", new ColorUIResource(255, 255, 255));
        UIManager.put("OptionPane.buttonFont", Resources.bodyFont);
        UIManager.put("Button.background", Resources.accent);

        String message =
                "<html><body><div width='230px' align='center'>" + errorMessage + "</div></body></html>";
        JLabel messageLabel = new JLabel(message);
        messageLabel.setFont(Resources.bodyFont);
        JOptionPane.showMessageDialog(Main.PAGE_MANAGER.getJFrame(),
                messageLabel, "",
                JOptionPane.PLAIN_MESSAGE);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();
        if (o.equals(importNow)) {
            String label = (String) labelSelector.getSelectedItem();
            List<EventInfo> eventInfoList = null;
            try {
                eventInfoList = Gmail.getEventList(label);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            } catch (GeneralSecurityException generalSecurityException) {
                generalSecurityException.printStackTrace();
            }
            if (eventInfoList == null) {
                displayErrorMessage("No messages with events under " + label +
                        ".");
            } else {
                Main.PAGE_MANAGER.addPage(new ConfirmationPage(eventInfoList));
                Main.PAGE_MANAGER.display();
            }
        }
    }
}
