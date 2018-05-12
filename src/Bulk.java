import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.Target;
import org.snmp4j.TransportMapping;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.event.ResponseListener;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.Integer32;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;

public class Bulk implements ResponseListener {

    private String IPAdress;
    private final static String SNMP_COMMUNITY = "public";
    private final static int    SNMP_RETRIES   = 3;
    private final static long   SNMP_TIMEOUT   = 3000L;
    private final static int    BULK_SIZE      = 50;
    private String[] RetTable;
    public ArrayList<String> Table = new ArrayList<>();
    private Snmp snmp = null;
    private TransportMapping transport = null;

    private Set<Integer32> requests = new HashSet<Integer32>();

    public void onResponse(ResponseEvent event) {
        Integer32 requestId = event.getRequest().getRequestID();
        PDU response = event.getResponse();
        if (response != null) {
            String answer = response.toString();
            String[] answers = answer.split("VBS");
            String[] answers_2 = answers[1].split("]");
            RetTable = answers_2[0].split(";");
            for(int i = 0; i < RetTable.length; i++){
                String[] tempval = RetTable[i].split("=");
                RetTable[i] = tempval[1].substring(1);
                Table.add(RetTable[i]);
            } return;
        } else {
            synchronized (requests) {
                if (requests.contains(requestId)) {
                    System.out.println("Timeout exceeded");
                }
            }
        }
        synchronized (requests) {
            requests.remove(requestId);
        }
    }

    public void test(String[] oid_list) throws IOException {
        Target t = getTarget(IPAdress);
        send(t, oid_list);
    }

    private void send(Target target, String[] oids) throws IOException {
        PDU pdu = new PDU();
        for (String oid: oids) {
            pdu.add(new VariableBinding(new OID(oid)));
        }
        pdu.setType(PDU.GETBULK);
        pdu.setMaxRepetitions(BULK_SIZE);
        pdu.setNonRepeaters(1);
        ResponseEvent event = snmp.send(pdu, target, null);
        synchronized (requests) {
            requests.add(pdu.getRequestID());
        }
        onResponse(event);
    }

    private Target getTarget(String address) {
        Address targetAddress = GenericAddress.parse(address);
        CommunityTarget target = new CommunityTarget();
        target.setCommunity(new OctetString(SNMP_COMMUNITY));
        target.setAddress(targetAddress);
        target.setRetries(SNMP_RETRIES);
        target.setTimeout(SNMP_TIMEOUT);
        target.setVersion(SnmpConstants.version2c);
        return target;
    }

    public void start() throws IOException {
        transport = new DefaultUdpTransportMapping();
        snmp = new Snmp(transport);
        transport.listen();
    }

    public void stop() throws IOException {
        try {
            if (transport != null) {
                transport.close();
                transport = null;
            }
        } finally {
            if (snmp != null) {
                snmp.close();
                snmp = null;
            }
        }
    }

    public Bulk(String IP) {
        this.IPAdress = IP;
    }
}





