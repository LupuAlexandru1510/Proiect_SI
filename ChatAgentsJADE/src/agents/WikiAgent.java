package agents;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.*;
import java.io.*;

public class WikiAgent extends Agent {

    @Override
    protected void setup() {
        // 1. Înregistrare în Directory Facilitator (DF)
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType("encyclopedia"); // Tipul căutat de ChatAgent
        sd.setName("Wiki-Service");
        dfd.addServices(sd);
        
        try {
            DFService.register(this, dfd);
            System.out.println("WikiAgent: Înregistrat cu succes în DF.");
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 2. Comportament de ascultare mesaje REQUEST
        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                ACLMessage msg = receive();
                if (msg != null) {
                    // Procesăm doar mesajele de tip REQUEST (cele de la ChatAgent)
                    if (msg.getPerformative() == ACLMessage.REQUEST) {
                        cautaPeWiki(msg);
                    }
                } else {
                    block();
                }
            }
        });
    }

    private void cautaPeWiki(ACLMessage msg) {
        try {
            String termen = msg.getContent();
            System.out.println("WikiAgent: Caut informații despre: " + termen);

            // Calea către Python de pe calculatorul tău (din imaginea ta)
            String pythonPath = "C:\\Users\\alex2\\AppData\\Local\\Python\\pythoncore-3.14-64\\python.exe";
            
            // Construim procesul
            ProcessBuilder pb = new ProcessBuilder(pythonPath, "wiki_service.py", termen);
            
            // Setăm directorul de lucru la rădăcina proiectului
            pb.directory(new File(System.getProperty("user.dir")));
            pb.redirectErrorStream(true); // Combinăm erorile cu output-ul standard

            Process p = pb.start();
            BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
            
            // Citim răspunsul de la scriptul Python
            String result = in.readLine();
            
            ACLMessage reply = msg.createReply();
            
            if (result != null && result.startsWith("OK|")) {
                // Dacă totul e bine, trimitem textul (fără prefixul OK|)
                reply.setPerformative(ACLMessage.INFORM);
                reply.setContent(result.substring(3));
            } else {
                // Dacă scriptul a returnat o eroare sau e null
                reply.setPerformative(ACLMessage.FAILURE);
                String errorMsg = (result != null) ? result : "Scriptul Python nu a returnat nimic.";
                reply.setContent("Eroare Wiki: " + errorMsg);
                System.err.println("WikiAgent Eroare: " + errorMsg);
            }
            
            send(reply);

        } catch (Exception e) {
            // Dacă Java nu poate porni procesul (cale greșită la python.exe)
            ACLMessage reply = msg.createReply();
            reply.setPerformative(ACLMessage.FAILURE);
            reply.setContent("Eroare Sistem: Java nu a putut executa scriptul Python.");
            send(reply);
            e.printStackTrace();
        }
    }

    @Override
    protected void takeDown() {
        try {
            DFService.deregister(this);
            System.out.println("WikiAgent: Oprit și scos din DF.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}