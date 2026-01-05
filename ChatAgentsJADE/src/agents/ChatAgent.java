package agents;

import gui.ChatGui;
import jade.core.*;
import jade.core.behaviours.*;
import jade.lang.acl.ACLMessage;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.*;
import jade.domain.FIPAException;
import javax.swing.SwingUtilities;
import java.io.*;
import java.util.*;

public class ChatAgent extends Agent {
    private String nickname;
    private ChatGui gui;
    private Map<String, AID> others = new HashMap<>();

    public String getNickname() { return nickname; }

    @Override
    protected void setup() {
        nickname = (getArguments() != null && getArguments().length > 0) ? getArguments()[0].toString() : getLocalName();
        
        // 1. Inregistrare la DF
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType("chat"); sd.setName(nickname);
        dfd.addServices(sd);
        try { DFService.register(this, dfd); } catch (FIPAException e) { e.printStackTrace(); }

        // 2. Pornire Interfata Grafica
        SwingUtilities.invokeLater(() -> gui = new ChatGui(this));

        // 3. Comportament pentru primirea mesajelor
        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                ACLMessage msg = receive();
                if (msg != null) {
                    trateazaMesajPrimit(msg);
                } else {
                    block();
                }
            }
        });

        // 4. Refresh periodic al listei de destinatari
        addBehaviour(new TickerBehaviour(this, 15000) {
            @Override
            protected void onTick() { refresh(); }
        });
    }

    private void trateazaMesajPrimit(ACLMessage msg) {
        if (msg.getSender().getLocalName().equalsIgnoreCase("df") || 
            msg.getPerformative() == ACLMessage.FAILURE) {
            return;
        }

        String content = msg.getContent();
        if (content == null) return;

        if (content.equals("SHUTDOWN")) {
            doDelete();
            return;
        }

        String senderName = msg.getSender().getLocalName();
        String line;

        // Logica de etichetare (Translator, Wiki, Weather, Monitor Resurse, Ceas Global)
        if (senderName.toLowerCase().contains("translator")) {
            line = "[TRADUCERE]: " + content;
        } else if (senderName.toLowerCase().contains("wiki")) {
            line = "[WIKI]: " + content;
        } else if (senderName.toLowerCase().contains("weather")) {
            line = "[VREMEA]: " + content;
        } else if (senderName.toLowerCase().contains("monitor_resurse")) {
            line = "[HARDWARE]: " + content;
        } else if (senderName.toLowerCase().contains("ceas_global")) {
            line = "[TIMP]: " + content;
        } else if (content.startsWith("[ANUNȚ GLOBAL]")) {
            line = content; 
        } else {
            line = "[" + senderName + "]: " + content;
        }

        if (gui != null) gui.afiseazaMesaj(line);
        save(line);
    }

    private void refresh() {
        List<String> nicks = new ArrayList<>();
        searchType("chat", nicks);
        searchType("assistant", nicks);
        searchType("translator", nicks);
        searchType("encyclopedia", nicks); 
        searchType("weather", nicks);      
        searchType("system-monitor", nicks); 
        searchType("time-service", nicks); // Adaugat: cautam serviciul de timp
        
        if (gui != null) gui.seteazaDestinatari(nicks);
    }

    private void searchType(String type, List<String> nicks) {
        DFAgentDescription t = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription(); sd.setType(type);
        t.addServices(sd);
        try {
            DFAgentDescription[] res = DFService.search(this, t);
            for (DFAgentDescription d : res) {
                String foundNick = d.getName().getLocalName();
                others.put(foundNick, d.getName());
                if (!nicks.contains(foundNick)) {
                    nicks.add(foundNick);
                }
            }
        } catch (FIPAException e) {}
    }

    public void trimiteMesaj(String dest, String txt) {
        AID targetAID = others.get(dest);
        if (targetAID == null) return;

        int perf = ACLMessage.INFORM;
        String d = dest.toLowerCase();
        
        // Protocol REQUEST pentru serviciile Python
        if (d.contains("translator") || d.contains("wiki") || d.contains("weather") || 
            d.contains("monitor_resurse") || d.contains("ceas_global")) {
            perf = ACLMessage.REQUEST;
        }

        ACLMessage msg = new ACLMessage(perf);
        msg.addReceiver(targetAID);
        msg.setContent(txt);
        send(msg);

        String line = "[eu -> " + dest + "]: " + txt;
        if (gui != null) gui.afiseazaMesaj(line);
        save(line);
    }

    public void trimiteShutdownLaToti() {
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.setContent("SHUTDOWN");
        for (AID aid : others.values()) msg.addReceiver(aid);
        send(msg);
        doDelete();
    }

    private void save(String l) {
        try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("history_" + nickname + ".txt", true)))) {
            out.println(l);
        } catch (IOException e) {}
    }

    @Override
    protected void takeDown() {
        if (gui != null) gui.dispose();
        try { DFService.deregister(this); } catch (Exception e) {}
    }
}