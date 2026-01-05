package agents;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.*;
import java.io.*;

public class TranslatorAgent extends Agent {
    @Override
    protected void setup() {
        // Inregistrare in DF pentru ca ceilalti agenti sa il gaseasca
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType("translator");
        sd.setName("Translator-Service");
        dfd.addServices(sd);
        
        try {
            DFService.register(this, dfd);
            System.out.println("TranslatorAgent: Inregistrat cu succes in DF.");
        } catch (Exception e) {
            e.printStackTrace();
        }

        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                // Translatorul asteapta mesaje de tip REQUEST
                ACLMessage msg = receive();
                if (msg != null) {
                    if (msg.getPerformative() == ACLMessage.REQUEST) {
                        executaTraducerea(msg);
                    }
                } else {
                    block();
                }
            }
        });
    }

    private void executaTraducerea(ACLMessage msg) {
        try {
            String textToTranslate = msg.getContent();
            // Apelam scriptul Python creat de tine
            ProcessBuilder pb = new ProcessBuilder("python", "translator_service.py", textToTranslate);
            Process p = pb.start();
            
            BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String result = in.readLine(); // Citim linia de tip OK|traducere

            ACLMessage reply = msg.createReply();
            if (result != null && result.startsWith("OK|")) {
                reply.setPerformative(ACLMessage.INFORM);
                reply.setContent(result.split("\\|")[1]);
            } else {
                reply.setPerformative(ACLMessage.FAILURE);
                reply.setContent("Eroare Python: " + (result != null ? result : "Fara raspuns"));
            }
            send(reply);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void takeDown() {
        try { DFService.deregister(this); } catch (Exception e) {}
    }
}