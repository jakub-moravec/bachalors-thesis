package GUI;

import model.KnimeWorkflowNode;
import model.KnimeNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.util.HashMap;

/**
 * Obrazovka grafického rozhraní aplikace pro změnu parametrů KNIME uzlu.
 * @author moravja8@fel.cvut.cz
 */
class EditNodeParametersFrame {
    private static Logger log = LoggerFactory.getLogger(EditNodeParametersFrame.class);

    private KnimeWorkflowNode workflow = null;

    private JPanel mainPanel;
    private JComboBox nodeComboBox;
    private JComboBox parametersComboBox;
    private JTextArea parameterValueTextArea = new JTextArea();
    private JTextArea outputTextArea = new JTextArea();

    private GridBagConstraints gridBagConstraints = new GridBagConstraints();

    EditNodeParametersFrame(KnimeWorkflowNode workflow) {
        this.workflow = workflow;
    }

    /**
     * Vytvoří a zobrazí obrazovku.
     */
    void init(){
        JFrame frame = new JFrame("Edit node parameters");

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

        nodesInit();
        parametresInit();
        parameterValueTextareaInit();
        setParameterButtonInit();
        outputTextAreaInit();

        frame.setContentPane(mainPanel);
    }

    private JComboBox initComboBox(JComboBox comboBox){
        comboBox = new JComboBox();
        comboBox.setSize(200, 30);
        return comboBox;
    }

    private void nodesInit(){
        nodeComboBox = initComboBox(nodeComboBox);

        nodesRefresh();

        nodeComboBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                parametresRefresh((KnimeNode) nodeComboBox.getSelectedItem());
            }
        });

        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridx = 0;
        mainPanel.add(new JLabel("Select node:"), gridBagConstraints);

        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridx = 1;
        mainPanel.add(nodeComboBox, gridBagConstraints);
    }

    private void parametresInit(){
        parametersComboBox = initComboBox(parametersComboBox);

        parametresRefresh((KnimeNode) nodeComboBox.getSelectedItem());

        parametersComboBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                KnimeNode node = (KnimeNode) nodeComboBox.getSelectedItem();
                parameterValueTextArea.setText(node.getParameters().get(parametersComboBox.getSelectedItem()));

                outputTextArea.setText("");
                outputTextArea.update(outputTextArea.getGraphics());
            }
        });

        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridx = 0;
        mainPanel.add(new JLabel("Select parameter:"), gridBagConstraints);

        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridx = 1;
        mainPanel.add(parametersComboBox, gridBagConstraints);
    }

    private void nodesRefresh() {
        nodeComboBox.removeAllItems();
        try {
            for (KnimeNode node : workflow.getNodes()){
                nodeComboBox.addItem(node);
            }
        } catch (Exception e) {
            log.error("No nodes were found.", e);
            outputTextArea.setText("No nodes were found!");
            outputTextArea.update(outputTextArea.getGraphics());
        }
    }

    private void parametresRefresh(KnimeNode node) {
        if(node == null){
            return;
        }

        parametersComboBox.removeAllItems();

        HashMap<String, String> parameters = node.getParameters();

        for (String key : parameters.keySet()) {
            parametersComboBox.addItem(key);
        }
    }

    private void parameterValueTextareaInit(){
        parameterValueTextArea.setVisible(true);
        parameterValueTextArea.setEnabled(true);
        parameterValueTextArea.setLineWrap (true);
        parameterValueTextArea.setMinimumSize(new Dimension(290, 150));
        parameterValueTextArea.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));

        JScrollPane sp = new JScrollPane(parameterValueTextArea);
        sp.setMinimumSize(parameterValueTextArea.getMinimumSize());


        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridx = 0;
        mainPanel.add(new JLabel("Parameter value:"), gridBagConstraints);

        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = 2;
        mainPanel.add(sp, gridBagConstraints);
        gridBagConstraints.gridwidth = 1;
    }

    private void setParameterButtonInit() {
        JButton setParameterButton = new JButton("Set parameter");

        setParameterButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                KnimeNode node = (KnimeNode) nodeComboBox.getSelectedItem();

                if(node == null){
                    return;
                }

                node.updateParametres((String) parametersComboBox.getSelectedItem(), parameterValueTextArea.getText());

                log.info("Setting parameter " + parametersComboBox.getSelectedItem() +  " of workflow  "
                        + workflow + " to " + parameterValueTextArea.getText());

                outputTextArea.setText("Parameter updated successfully.");
                outputTextArea.update(outputTextArea.getGraphics());
            }
        });

        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = 2;
        mainPanel.add(setParameterButton, gridBagConstraints);
        gridBagConstraints.gridwidth = 1;
    }

    private void outputTextAreaInit(){
        outputTextArea.setVisible(true);
        outputTextArea.setEnabled(false);
        outputTextArea.setLineWrap (true);
        outputTextArea.setMinimumSize(new Dimension(290, 150));
        outputTextArea.setDisabledTextColor(new Color(50, 50, 255));
        outputTextArea.setBackground(mainPanel.getBackground());

        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = 2;
        mainPanel.add(outputTextArea, gridBagConstraints);
        gridBagConstraints.gridwidth = 1;
    }
}
