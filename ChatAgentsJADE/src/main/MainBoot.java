package main;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import javax.swing.JOptionPane;

public class MainBoot {
    public static void main(String[] args) {
        // 1. Inițializăm mediul JADE
        Runtime rt = Runtime.instance();
        Profile p = new ProfileImpl();
        p.setParameter(Profile.GUI, "true"); 

        // 2. Creăm containerul principal (Main Container)
        AgentContainer mainContainer = rt.createMainContainer(p);

        try {
            // --- LANSARE AGENȚI SERVICII (Backend) ---
            // Acești agenți nu au interfață proprie, ei doar răspund la cereri
            mainContainer.createNewAgent("asistent", "agents.AssistantAgent", new Object[]{"Asistent"}).start();
            mainContainer.createNewAgent("translator", "agents.TranslatorAgent", null).start();
            mainContainer.createNewAgent("wiki", "agents.WikiAgent", null).start();
            mainContainer.createNewAgent("weather", "agents.WeatherAgent", null).start();
            mainContainer.createNewAgent("monitor_resurse", "agents.SystemManagerAgent", null).start();
            mainContainer.createNewAgent("monitor_istoric", "agents.HistoryAgent", null).start();
            mainContainer.createNewAgent("broadcaster", "agents.BroadcasterAgent", null).start();
            mainContainer.createNewAgent("ceas_global", "agents.TimeAgent", null).start();

            // --- DIALOG LANSARE AGENȚI CHAT (Frontend) ---
            String input = JOptionPane.showInputDialog(null, 
                    "Câți agenți de chat doriți să lansați?", 
                    "Lansare Sistem Multi-Agent", 
                    JOptionPane.QUESTION_MESSAGE);

            if (input != null && !input.isEmpty()) {
                try {
                    int nrAgenti = Integer.parseInt(input);
                    for (int i = 1; i <= nrAgenti; i++) {
                        String agentID = "agentID_" + i;
                        String porecla = "Agent_" + i; 
                        mainContainer.createNewAgent(agentID, "agents.ChatAgent", new Object[]{porecla}).start();
                    }
                } catch (NumberFormatException e) {
                    System.err.println("Te rog să introduci un număr valid de agenți.");
                }
            }
            
            System.out.println(">>> Sistem pornit cu succes.");
            System.out.println(">>> Servicii active: Wiki, Weather, Translator, Hardware, Time, Istoric, Broadcaster.");

        } catch (Exception e) {
            System.err.println("Eroare la pornirea containerului JADE:");
            e.printStackTrace();
        }
    }
}