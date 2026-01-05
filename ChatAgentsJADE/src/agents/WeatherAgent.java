package agents;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.*;
import java.io.*;

public class WeatherAgent extends Agent {
    @Override
    protected void setup() {
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType("weather");
        sd.setName("Weather-Service");
        dfd.addServices(sd);
        
        try { DFService.register(this, dfd); } 
        catch (Exception e) { e.printStackTrace(); }

        System.out.println("WeatherAgent: Inregistrat cu succes.");

        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                ACLMessage msg = receive();
                if (msg != null && msg.getPerformative() == ACLMessage.REQUEST) {
                    obtineVremea(msg);
                } else { block(); }
            }
        });
    }

    private void obtineVremea(ACLMessage msg) {
        try {
            String pythonPath = "C:\\Users\\alex2\\AppData\\Local\\Python\\pythoncore-3.14-64\\python.exe";
            ProcessBuilder pb = new ProcessBuilder(pythonPath, "weather_service.py", msg.getContent());
            pb.directory(new File(System.getProperty("user.dir")));
            
            Process p = pb.start();
            BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream(), "UTF-8"));
            String result = in.readLine();

            ACLMessage reply = msg.createReply();
            if (result != null && result.startsWith("OK|")) {
                reply.setPerformative(ACLMessage.INFORM);
                reply.setContent(result.substring(3));
            } else {
                reply.setPerformative(ACLMessage.FAILURE);
                reply.setContent("Eroare la serviciul meteo.");
            }
            send(reply);
        } catch (Exception e) { e.printStackTrace(); }
    }
}