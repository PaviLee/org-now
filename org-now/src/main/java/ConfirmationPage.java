import javax.swing.*;
import javax.swing.plaf.ColorUIResource;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.LinkedList;
import java.util.List;

/**
 * GUI Class: Handles GUI and Action events for selecting events to transfer
 * data to
 * Google Calendar.
 *
 * @author Pavi Lee
 * @version September 5, 2020
 */
public class ConfirmationPage extends BackgroundPanel implements ActionListener {

    private List<EventInfo> eventInfoList;
    private JList list;
    private JButton confirmation;

    public ConfirmationPage(List<EventInfo> eventInfoList) {
        this.eventInfoList = eventInfoList;

        this.setBackground(Resources.transparent);
        this.setLayout(new BorderLayout());

        this.add(Box.createRigidArea(new Dimension(0, 150)),
                BorderLayout.NORTH);
        this.add(Box.createRigidArea(new Dimension(0, 150)),
                BorderLayout.SOUTH);
        this.add(Box.createRigidArea(new Dimension(100, 0)), BorderLayout.EAST);
        this.add(Box.createRigidArea(new Dimension(100, 0)),
                BorderLayout.WEST);

        JPanel centerContainer = new JPanel();
        centerContainer.setBackground(Resources.transparent);
        centerContainer.setLayout(new BoxLayout(centerContainer,
                BoxLayout.Y_AXIS));
        this.add(centerContainer, BorderLayout.CENTER);

        JLabel title = new JLabel("Please select the events to import:");
        title.setForeground(Resources.secondary);
        title.setFont(Resources.secondaryFont);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerContainer.add(title);

        centerContainer.add(Box.createRigidArea(new Dimension(0, 25)));

        list = new JList(eventInfoList.toArray());
        centerContainer.add(list);

        centerContainer.add(Box.createRigidArea(new Dimension(0, 25)));

        confirmation = new JButton("Finalize Import");
        confirmation.setForeground(Resources.secondary);
        confirmation.setBackground(Resources.accent);
        confirmation.setFont(Resources.bodyFont);
        confirmation.setAlignmentX(Component.CENTER_ALIGNMENT);
        confirmation.addActionListener(this);
        centerContainer.add(confirmation);
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
        if (o.equals(confirmation)) {
            int[] indexes = list.getSelectedIndices();
            EventInfo[] ref = eventInfoList.toArray(new EventInfo[0]);
            List<EventInfo> updatedEventInfoList = new LinkedList<EventInfo>();

            for (int i : indexes) {
                updatedEventInfoList.add(ref[i]);
            }

            try {
                Calendar.addEventsToCalendar(updatedEventInfoList);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            } catch (GeneralSecurityException generalSecurityException) {
                generalSecurityException.printStackTrace();
            }

            displayErrorMessage("Events added!");
            Main.PAGE_MANAGER.removePage();
            Main.PAGE_MANAGER.display();
        }
    }
}
