package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import java.util.ArrayList;
import java.util.Objects;

import agents.ChatAgent;

public class ChatGui extends JFrame {
    private ChatAgent myAgent;

    private JComboBox<String> comboDestinatari;
    private JTextArea areaMesaje;
    private JTextField fieldInput;
    private JButton btnTrimite;
    private JButton btnExitAll;
    
    // Lista folosita pentru a verifica daca e nevoie de refresh real
    private List<String> ultimaListaPorecle = new ArrayList<>();

    public ChatGui(ChatAgent agent) {
        super("Agent Chat: " + agent.getNickname());
        this.myAgent = agent;

        // --- Configurare UI ---
        setLayout(new BorderLayout(5, 5));
        
        // Zona de mesaje
        areaMesaje = new JTextArea();
        areaMesaje.setEditable(false);
        areaMesaje.setFont(new Font("Consolas", Font.PLAIN, 13));
        areaMesaje.setBackground(new Color(245, 245, 245));
        
        JScrollPane scroll = new JScrollPane(areaMesaje);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        // Panou destinatar (ComboBox)
        comboDestinatari = new JComboBox<>();
        JPanel panelDest = new JPanel(new BorderLayout());
        panelDest.setBorder(BorderFactory.createTitledBorder("Trimite către:"));
        panelDest.add(comboDestinatari, BorderLayout.CENTER);

        // Panou Input si Butoane
        fieldInput = new JTextField();
        btnTrimite = new JButton("Trimite");
        btnTrimite.setBackground(new Color(200, 255, 200));
        
        btnExitAll = new JButton("Exit ALL");
        btnExitAll.setBackground(new Color(255, 200, 200));

        JPanel panelButoane = new JPanel(new GridLayout(1, 2, 5, 5));
        panelButoane.add(btnTrimite);
        panelButoane.add(btnExitAll);

        JPanel panelSud = new JPanel(new BorderLayout(5, 5));
        panelSud.add(fieldInput, BorderLayout.CENTER);
        panelSud.add(panelButoane, BorderLayout.SOUTH);

        // Adaugare in Frame
        add(panelDest, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
        add(panelSud, BorderLayout.SOUTH);

        // --- Listeners ---
        btnTrimite.addActionListener(e -> actionTrimite());
        fieldInput.addActionListener(e -> actionTrimite());
        
        btnExitAll.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Inchidem toti agentii?");
            if (confirm == JOptionPane.YES_OPTION) {
                myAgent.trimiteShutdownLaToti();
            }
        });

        // Dimensiuni si pozitie
        setSize(450, 550);
        setLocationRelativeTo(null);
        
        // La inchiderea ferestrei cu X, se opreste doar acest agent
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    private void actionTrimite() {
        String dest = (String) comboDestinatari.getSelectedItem();
        String txt = fieldInput.getText().trim();
        
        if (dest != null && !txt.isEmpty()) {
            myAgent.trimiteMesaj(dest, txt);
            fieldInput.setText("");
        }
    }

    public void afiseazaMesaj(String msg) {
        // Ignoram mesajele de sistem DF care incep cu "( (result" ca sa nu umplem ecranul
        if (msg == null || msg.contains("(result")) return;

        SwingUtilities.invokeLater(() -> {
            areaMesaje.append(msg + "\n");
            // Auto-scroll la ultimul mesaj
            areaMesaje.setCaretPosition(areaMesaje.getDocument().getLength());
        });
    }

    /**
     * Seteaza destinatarii. Daca lista e identica cu cea veche, nu face nimic
     * pentru a preveni resetarea selectiei utilizatorului.
     */
    public void seteazaDestinatari(List<String> nicknames) {
        // Cream o lista filtrata (fara noi insine)
        List<String> listaNoua = new ArrayList<>();
        for (String n : nicknames) {
            if (!n.equals(myAgent.getNickname())) {
                listaNoua.add(n);
            }
        }

        // Verificare: daca lista e aceeasi, NU atingem ComboBox-ul
        if (listaNoua.equals(ultimaListaPorecle)) {
            return;
        }

        // Daca lista chiar s-a schimbat:
        SwingUtilities.invokeLater(() -> {
            String selectieAnterioara = (String) comboDestinatari.getSelectedItem();
            
            comboDestinatari.removeAllItems();
            for (String porecla : listaNoua) {
                comboDestinatari.addItem(porecla);
            }

            // Restauram selectia daca agentul mai e online
            if (selectieAnterioara != null) {
                comboDestinatari.setSelectedItem(selectieAnterioara);
            }
            
            ultimaListaPorecle = new ArrayList<>(listaNoua);
        });
    }
}