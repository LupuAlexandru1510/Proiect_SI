package agents;

import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import javax.swing.*;
import java.awt.*;

public class BroadcasterAgent extends Agent {
    private JFrame frame;
    private JTextField msgField;

    @Override
    protected void setup() {
        System.out.println("Broadcaster Agent pornit.");
        setupGui();
    }

    private void setupGui() {
        frame = new JFrame("Global Broadcaster");
        frame.setLayout(new FlowLayout());
        
        msgField = new JTextField(20);
        JButton sendBtn = new JButton("Trimite la toți");

        sendBtn.addActionListener(e -> {
            String text = msgField.getText().trim();
            if (!text.isEmpty()) {
                trimiteLaToti(text);
                msgField.setText("");
            }
        });

        frame.add(new JLabel("Mesaj global:"));
        frame.add(msgField);
        frame.add(sendBtn);
        
        frame.setSize(350, 120);
        frame.setLocation(100, 100);
        frame.setVisible(true);
    }

    private void trimiteLaToti(String continut) {
        // Căutăm toți agenții care oferă serviciul de "chat"
        DFAgentDescription template = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setType("chat");
        template.addServices(sd);

        try {
            DFAgentDescription[] rezultate = DFService.search(this, template);
            
            if (rezultate.length > 0) {
                ACLMessage broadcast = new ACLMessage(ACLMessage.INFORM);
                broadcast.setContent("[ANUNȚ GLOBAL]: " + continut);
                
                for (DFAgentDescription agent : rezultate) {
                    // Nu ne trimitem nouă înșine dacă am fi fost agent de chat
                    broadcast.addReceiver(agent.getName());
                }
                
                send(broadcast);
                System.out.println("Mesaj trimis către " + rezultate.length + " agenți.");
            } else {
                JOptionPane.showMessageDialog(frame, "Nu s-au găsit agenți de chat online!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void takeDown() {
        if (frame != null) frame.dispose();
    }
}