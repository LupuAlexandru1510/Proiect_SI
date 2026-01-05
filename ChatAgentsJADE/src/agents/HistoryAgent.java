package agents;

import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import gui.HistoryGui;
import javax.swing.SwingUtilities;

public class HistoryAgent extends Agent {
    private HistoryGui gui;

    @Override
    protected void setup() {
        System.out.println("Agentul de Monitorizare Istoric a pornit.");
        
        SwingUtilities.invokeLater(() -> gui = new HistoryGui());

        // La fiecare 5 secunde, actualizăm lista de fișiere detectate pe disc
        addBehaviour(new TickerBehaviour(this, 5000) {
            @Override
            protected void onTick() {
                if (gui != null) {
                    gui.detecteazaFisiere();
                }
            }
        });
    }

    @Override
    protected void takeDown() {
        if (gui != null) gui.dispose();
    }
}