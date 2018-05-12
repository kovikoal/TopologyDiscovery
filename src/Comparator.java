import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import  java.util.regex.*;

public class Comparator{


    public Map<String, String> NodeConnections = new HashMap<>();
    public Map<String, String> NodeInterfaces = new HashMap<>();
    public String DeviceID;
    public String IP;
    private Map<String, String> MACMap = new HashMap<>();
    public Map<String, String> fdb_table = new HashMap<>();
    private Map<String, String> InterfaceMap = new HashMap<>();
    private static final String[] list1 = {"1.3.6.1.2.1.1.3","1.3.6.1.2.1.2.2.1.1","1.3.6.1.2.1.2.2.1.2"};
    private static final String[] list2 = {"1.3.6.1.2.1.1.3","1.3.6.1.2.1.17.4.3.1.1","1.3.6.1.2.1.17.4.3.1.2"};
    private static final String MacPattern = "\\w\\w\\:\\w\\w\\:\\w\\w\\:\\w\\w\\:\\w\\w\\:\\w\\w";

    public static boolean MacCheck(String MAC) {
        Pattern p = Pattern.compile(MacPattern);
        Matcher m = p.matcher(MAC);
        return m.matches();
    }

    public void InterfaceDiscovery() {
        Bulk Request = new Bulk(IP);
        try {
            try {
                Request.start();
                Request.test(list1);
            } finally {
                Request.stop();
            }
        } catch (IOException e) {
            System.out.println(e.toString());
        }
        String TempVal1 = "0";
        String TempVal2 = "0";
        for (String Row : Request.Table) {
            if(Request.Table.indexOf(Row) == 0) continue;
            if(Request.Table.indexOf(Row) != 0){
                if(Row.length() < 10){
                    TempVal1 = Row;
                    continue;
                }
                if(Row.length() > 10 || !TempVal1.equals("0")){
                    TempVal2 = Row;
                    InterfaceMap.put(TempVal1, TempVal2);
                }
            }
        }
    }

    public void MACDiscovery() {
        Bulk Request = new Bulk(IP);
        try {
            try {
                Request.start();
                Request.test(list2);
            } finally {
                Request.stop();
            }
        } catch (IOException e) {
            System.out.println(e.toString());
        }
        String MacTemp = "0";
        for(String Row : Request.Table){
            if(MacCheck(Row)){
                MacTemp = Row;
                continue;
            }
            if(MacTemp != "0"){
                String Interface = Row;
                MACMap.put(MacTemp, Interface);
                MacTemp = "0";
            }
        }
    }

    public void TableComplete(){
        MACDiscovery();
        InterfaceDiscovery();
        for (Map.Entry<String, String> entry : MACMap.entrySet()){
            String Value = entry.getValue();
            String Key = entry.getKey();
            for (Map.Entry<String, String> entry1 : InterfaceMap.entrySet()){
                String Value1 = entry1.getValue();
                String Key1 = entry1.getKey();
                if(Key1.equals(Value)){
                    fdb_table.put(Key, Value1);
                }
            }
            if(Value.equals("0")) fdb_table.put(Key, Value);
        }
    }
}

