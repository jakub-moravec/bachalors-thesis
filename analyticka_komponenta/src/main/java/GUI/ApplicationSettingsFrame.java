package GUI;

import DAO.DaoFactory;
import model.KnimeNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.ServiceFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;

/**
 * Obrazovka grafického rozhraní aplikace pro nastavení parametrů aplikace.
 * @author moravja8@fel.cvut.cz
 */
class ApplicationSettingsFrame {
    private static Logger log = LoggerFactory.getLogger(ApplicationSettingsFrame.class);

    private JPanel mainPanel;
    private JComboBox propertyCombobox;
    private JTextArea propertyValueTextarea = new JTextArea();
    private JTextArea outputTextArea = new JTextArea();

    private GridBagConstraints gridBagConstraints = new GridBagConstraints();

    /**
     * Vytvoří a zobrazí obrazovku.
     */
    void init(){
        JFrame frame = new JFrame("Configure application");

        frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
        frame.pack();
        frame.setSize(400, 300);
        frame.setResizable(false);
        frame.setVisible(true);

        mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new Insets(5,5,5,5);

        propertiesInit();
        propertyValueTextareaInit();
        setPropertyButtonInit();
        outputTextAreaInit();

        frame.setContentPane(mainPanel);
    }

    private JComboBox initComboBox(JComboBox comboBox){
        comboBox = new JComboBox();
        comboBox.setSize(200, 30);
        return comboBox;
    }

    private void propertiesInit(){
        propertyCombobox = initComboBox(propertyCombobox);

        propertiesRefresh();
        propertyValueRefresh((String) propertyCombobox.getSelectedItem());

        propertyCombobox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                propertyValueRefresh((String) propertyCombobox.getSelectedItem());
                outputTextArea.setText("");
                outputTextArea.update(outputTextArea.getGraphics());
            }
        });

        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridx = 0;
        mainPanel.add(new JLabel("Select property:"), gridBagConstraints);

        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridx = 1;
        mainPanel.add(propertyCombobox, gridBagConstraints);
    }

    private void propertyValueRefresh(String key){
        propertyValueTextarea.setText(DaoFactory.getPropertiesDao().get(key));
        propertyValueTextarea.update(propertyValueTextarea.getGraphics());
    }

    private void propertiesRefresh() {
        propertyCombobox.removeAllItems();
        for (String key : ServiceFactory.getPropertiesService().getPropertiesList()){
            propertyCombobox.addItem(key);
        }
    }

    private void propertyValueTextareaInit(){
        propertyValueTextarea.setVisible(true);
        propertyValueTextarea.setEnabled(true);
        propertyValueTextarea.setLineWrap (true);
        propertyValueTextarea.setMinimumSize(new Dimension(290, 150));
        propertyValueTextarea.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));

        JScrollPane sp = new JScrollPane(propertyValueTextarea);
        sp.setMinimumSize(propertyValueTextarea.getMinimumSize());


        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridx = 0;
        mainPanel.add(new JLabel("Property value:"), gridBagConstraints);

        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = 2;
        mainPanel.add(sp, gridBagConstraints);
        gridBagConstraints.gridwidth = 1;
    }

    private void setPropertyButtonInit() {
        JButton setPropertyButton = new JButton("Set property");

        setPropertyButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String property = (String) propertyCombobox.getSelectedItem();

                DaoFactory.getPropertiesDao().set(property, propertyValueTextarea.getText());

                DaoFactory.getPropertiesDao().save();

                log.info("Application property " + property + " was set to " + propertyValueTextarea.getText());

                outputTextArea.setText("Property updated successfully.");
                outputTextArea.update(outputTextArea.getGraphics());
            }
        });

        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = 2;
        mainPanel.add(setPropertyButton, gridBagConstraints);
        gridBagConstraints.gridwidth = 1;
    }

    private void outputTextAreaInit(){
        outputTextArea.setVisible(true);
        outputTextArea.setEnabled(false);
        outputTextArea.setLineWrap (true);
        outputTextArea.setMinimumSize(new Dimension(290, 150));
        outputTextArea.setDisabledTextColor(new Color(50, 50, 255));
        outputTextArea.setBackground(mainPanel.getBackground());

        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = 2;
        mainPanel.add(outputTextArea, gridBagConstraints);
        gridBagConstraints.gridwidth = 1;
    }
}
