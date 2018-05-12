import java.util.*;

public class ConnectionSearch{

    public ArrayList<String> Vert = new ArrayList<>();
    private boolean WorkIsDone = false;
    public ArrayList<Comparator> Devices = new ArrayList<>();
    public int NumberOfNodes = 0;
    public ArrayList<String> IpAddr = new ArrayList<>();
    private String NodeInterface = "0";

    private void GettingFDB(){

        for(int i = 0; i < NumberOfNodes; i++){
            Devices.add(i, new Comparator());
        }
        NumberOfNodes -= 1;
        int Reverse = NumberOfNodes;
        for (Comparator C : Devices) {
            C.IP = IpAddr.get(NumberOfNodes-Reverse);
            C.TableComplete();
            Reverse--;
        }
    }

    private void FindingNodes(){
        for (Comparator C : Devices) {
            for (Map.Entry<String, String> entry : C.fdb_table.entrySet()) {
                if(entry.getValue().equals(NodeInterface)){
                    C.DeviceID =  entry.getKey();
                }
            }
        }
    }

    private void RemoveNodes(){
        for (Comparator C : Devices) {
            for (Iterator<Map.Entry<String, String>> it = C.fdb_table.entrySet().iterator(); it.hasNext(); ) {
                Map.Entry<String, String> entry = it.next();
                if (entry.getValue().equals(NodeInterface)) {
                    it.remove();
                }
            }
        }
    }

    private void FindingDirectConnections(){
        for (Comparator C : Devices) {
            for (Map.Entry<String, String> entry : C.fdb_table.entrySet()) {
                String Interface = entry.getValue();
                int Matches = 0;
                for (Map.Entry<String, String> entry1 : C.fdb_table.entrySet()) {
                    if (Interface.equals(entry1.getValue())) Matches++;
                }

                if(Matches == 1) {
                    C.NodeConnections.put(entry.getKey(), C.DeviceID);
                    if(!Vert.contains(entry.getKey())) {
                        Vert.add(entry.getKey());
                    }
                    C.NodeInterfaces.put(entry.getKey(), Interface);
                }
            }
        }
    }

    private void DeleteAlreadyKnownConnections(){
        for (Comparator C : Devices) {
            for (Map.Entry<String, String> entry : C.NodeConnections.entrySet()) {
                String Key = entry.getKey();
                for(Comparator C1 : Devices){
                    for (Iterator<Map.Entry<String, String>> it = C1.fdb_table.entrySet().iterator(); it.hasNext(); ) {
                        Map.Entry<String, String> entry1 = it.next();
                        if (entry1.getKey().equals(Key)) {
                            it.remove();
                        }
                    }
                }
            }
        }
    }

    public void Compare() {

        for (Comparator C : Devices) {
            FindingDirectConnections();
            DeleteAlreadyKnownConnections();
        }
    }


    public void Search(){
        GettingFDB();
        FindingNodes();
        RemoveNodes();
        Compare();
        WorkIsDone = true;
        if(WorkIsDone){
            graph Topology = new graph();
            Topology.graphDraw(this);
        }

    }

}


