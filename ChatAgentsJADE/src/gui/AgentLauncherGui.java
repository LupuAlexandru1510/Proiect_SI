package gui;

import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import javax.swing.*;
import java.awt.*;

public class AgentLauncherGui extends JFrame {
    private AgentContainer mainContainer;
    private JTextField fieldNume;
    private JTextField fieldPorecla;

    public AgentLauncherGui(AgentContainer container) {
        super("JADE Agent Launcher");
        this.mainContainer = container;

        setLayout(new GridLayout(4, 2, 10, 10));
        ((JPanel)getContentPane()).setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        add(new JLabel("ID Agent (ex: user3):"));
        fieldNume = new JTextField();
        add(fieldNume);

        add(new JLabel("Porecla (ex: Andrei):"));
        fieldPorecla = new JTextField();
        add(fieldPorecla);

        JButton btnChat = new JButton("Lansează Chat Agent");
        btnChat.addActionListener(e -> lanseazaAgent("agents.ChatAgent"));
        add(btnChat);

        JButton btnAsistent = new JButton("Lansează Asistent");
        btnAsistent.addActionListener(e -> lanseazaAgent("agents.AssistantAgent"));
        add(btnAsistent);

        setSize(400, 250);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    private void lanseazaAgent(String className) {
        String id = fieldNume.getText().trim();
        String porecla = fieldPorecla.getText().trim();

        if (id.isEmpty() || porecla.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Completează ID și Porecla!");
            return;
        }

        try {
            // Folosim AgentController pentru a crea o instanta noua in containerul JADE
            AgentController ac = mainContainer.createNewAgent(id, className, new Object[]{porecla});
            ac.start();
            
            fieldNume.setText("");
            fieldPorecla.setText("");
            System.out.println("Agent " + id + " lansat cu succes.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Eroare la lansare: " + ex.getMessage());
        }
    }
}