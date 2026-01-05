package gui;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class HistoryGui extends JFrame {
    private JTextArea areaIstoric;
    private JComboBox<String> comboAgenti;

    public HistoryGui() {
        super("Monitor Istoric - Agenți Activi");
        setLayout(new BorderLayout(10, 10));

        areaIstoric = new JTextArea();
        areaIstoric.setEditable(false);
        areaIstoric.setFont(new Font("Consolas", Font.PLAIN, 12));
        areaIstoric.setBackground(new Color(240, 240, 240));
        
        comboAgenti = new JComboBox<>();
        JButton btnRefreshFisiere = new JButton("Actualizează listă fișiere");
        JButton btnIncarca = new JButton("Vezi Istoric");

        JPanel panelSus = new JPanel(new FlowLayout());
        panelSus.add(new JLabel("Alege istoric:"));
        panelSus.add(comboAgenti);
        panelSus.add(btnIncarca);
        panelSus.add(btnRefreshFisiere);

        add(panelSus, BorderLayout.NORTH);
        add(new JScrollPane(areaIstoric), BorderLayout.CENTER);

        // Evenimente
        btnRefreshFisiere.addActionListener(e -> detecteazaFisiere());
        btnIncarca.addActionListener(e -> afiseazaContinut());

        setSize(600, 450);
        setLocationRelativeTo(null);
        setVisible(true);
        
        detecteazaFisiere();
    }

    public void detecteazaFisiere() {
        String selectieCurenta = (String) comboAgenti.getSelectedItem();
        comboAgenti.removeAllItems();
        
        File folder = new File(".");
        // Căutăm doar fișierele history_Agent_X.txt și history_Asistent.txt
        File[] files = folder.listFiles((dir, name) -> name.startsWith("history_") && name.endsWith(".txt"));
        
        if (files != null) {
            for (File f : files) {
                comboAgenti.addItem(f.getName());
            }
        }
        
        if (selectieCurenta != null) {
            comboAgenti.setSelectedItem(selectieCurenta);
        }
    }

    private void afiseazaContinut() {
        String numeFisier = (String) comboAgenti.getSelectedItem();
        if (numeFisier == null) return;

        try {
            List<String> linii = Files.readAllLines(Paths.get(numeFisier));
            areaIstoric.setText("");
            for (String l : linii) {
                areaIstoric.append(l + "\n");
            }
        } catch (Exception e) {
            areaIstoric.setText("Fișierul este gol sau nu a fost încă generat.");
        }
    }
}