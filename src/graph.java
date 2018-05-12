import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedSparseMultigraph;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.*;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.control.GraphMouseListener;
import java.awt.*;
import javax.swing.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Map;


public class graph extends JFrame{

    public void graphDraw(ConnectionSearch Searcher){
        Graph<String, String> NETWORK = new UndirectedSparseMultigraph<>();
        Layout<String, String> layout = new FRLayout<String, String>(NETWORK);
        int i = 0;
        for(String S : Searcher.Vert){
            NETWORK.addVertex(S);
        }
        for(Comparator C : Searcher.Devices){
            String V = C.DeviceID;
            NETWORK.addVertex(V);
            for (Map.Entry<String, String> entry : C.NodeConnections.entrySet()) {
                String V1 = entry.getKey();
                String V2 = entry.getValue();

                if(!NETWORK.isNeighbor(V1, V2)) {
                    NETWORK.addEdge("" + i, V1, V2);
                    i++;
                }
            }
        }

        layout.setSize(new Dimension(500,500)); // sets the initial size of the space
        VisualizationViewer<String,String> vv = new VisualizationViewer<String, String>(layout);
        vv.setPreferredSize(new Dimension(700,700)); //Sets the viewing area size
        vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller());
        vv.getRenderContext().setEdgeLabelTransformer(new ToStringLabeller<>());

        PluggableGraphMouse gm = new PluggableGraphMouse();
        gm.add(new TranslatingGraphMousePlugin(MouseEvent.BUTTON1_MASK));
        gm.add(new ScalingGraphMousePlugin(new CrossoverScalingControl(), 0, 1.1f, 0.9f));
        vv.setGraphMouse(gm);

        vv.addGraphMouseListener(new GraphMouseListener() {

            @Override
            public void graphClicked(Object v, MouseEvent me) {
                if (me.getButton() == MouseEvent.BUTTON1 && me.getClickCount() == 2) {
                    //System.out.println("Double clicked "+ v);
                    ArrayList<String> Message = new ArrayList<>();
                    String Vertex = (String) v;
                    for(Comparator C : Searcher.Devices){
                        if(Vertex.equals(C.DeviceID)){
                            Message.add("MAC устройства - " + C.DeviceID);
                            for(Map.Entry<String, String> entry : C.NodeInterfaces.entrySet()){
                                Message.add(entry.getKey() + " - " + entry.getValue());
                            }
                        }
                    }
                    if(!Message.isEmpty())JOptionPane.showMessageDialog(graph.this, Message.toArray());
                    else JOptionPane.showMessageDialog(graph.this, "Оконечное устройство");
                }
                me.consume();
            }

            @Override
            public void graphPressed(Object v, MouseEvent me) {
            }

            @Override
            public void graphReleased(Object v, MouseEvent me) {
            }
        });

        JFrame frame = new JFrame("TOPOLOGY");

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(vv);
        frame.pack();
        frame.setVisible(true);

    }

}