package agents;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.*;
import jade.domain.FIPAException;
import java.io.*;

public class AssistantAgent extends Agent {
    @Override
    protected void setup() {
        // Inregistrare in DF
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType("assistant");
        sd.setName(getLocalName());
        dfd.addServices(sd);
        try { DFService.register(this, dfd); } catch (FIPAException e) { e.printStackTrace(); }

        addBehaviour(new CyclicBehaviour(this) {
            @Override
            public void action() {
                ACLMessage msg = receive();
                if (msg != null) {
                    if (msg.getContent().equals("SHUTDOWN")) { doDelete(); return; }
                    proceseazaCuPydantic(msg);
                } else { block(); }
            }
        });
    }

    private void proceseazaCuPydantic(ACLMessage msg) {
        String content = msg.getContent();
        String cmd = content.contains(":") ? content.split(":")[0].trim() : content;
        String pay = content.contains(":") ? content.split(":")[1].trim() : "";
        
        try {
            String input = msg.getSender().getLocalName() + "|" + cmd + "|" + pay;
            ProcessBuilder pb = new ProcessBuilder("python", "pydantic_validator.py", input);
            pb.directory(new File(System.getProperty("user.dir")));
            Process p = pb.start();
            
            BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line = in.readLine();
            
            ACLMessage reply = msg.createReply();
            reply.setPerformative(ACLMessage.INFORM);
            if (line != null && line.startsWith("OK|")) {
                reply.setContent("Pydantic OK: " + line.split("\\|")[1]);
            } else {
                reply.setContent("Pydantic Error: Comanda invalida.");
            }
            send(reply);
        } catch (Exception e) { e.printStackTrace(); }
    }

    @Override
    protected void takeDown() {
        try { DFService.deregister(this); } catch (FIPAException e) {}
    }
}