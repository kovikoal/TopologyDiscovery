import java.util.ArrayList;
import java.util.List;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.regex.*;

public class GUI extends JFrame{

    private final static String IPPattern = "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
            "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
            "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
            "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";
    private List<JTextField> AddressFields = new ArrayList<JTextField>();
    private int IPCount = 0;

    public void draw(ConnectionSearch Searcher){

        final JTextField NumberText = new JTextField(10);
        JTextArea IPInstruction = new JTextArea("Введите IP-адреса устройств в формате Х.Х.Х.Х");
        final Font font = new Font("Verdana", Font.PLAIN, 14);
        IPInstruction.setFont(font);
        IPInstruction.setEnabled(false);
        JTextArea Greeting = new JTextArea("  Пожалуйста, введите количество устройств  ");
        Greeting.setFont(font);
        Greeting.setEnabled(false);
        final JButton Submit = new JButton("Ввод");

        final JFrame frame = new JFrame("TopologyDiscover");
        JPanel panel = new JPanel();
        panel.setBackground(Color.white);
        panel.add(Greeting);
        panel.add(NumberText);

        JFrame MainWindow = new JFrame("TopologyDiscover");
        JPanel panel2 = new JPanel();
        panel2.setBackground(Color.white);
        panel2.setLayout(new BoxLayout(panel2, BoxLayout.Y_AXIS));
        panel2.add(IPInstruction);
        JScrollPane scrollPane = new JScrollPane(panel2);

        NumberText.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                try{
                    int Number = Integer.parseInt(NumberText.getText());
                    Searcher.NumberOfNodes = Number;
                    NumberText.setEnabled(false);

                    for(int i = 0; i < Searcher.NumberOfNodes; i++){
                        JTextField Addres = new JTextField("IP-адрес " + (i+1), 30);
                        AddressFields.add(Addres);
                        Addres.setAlignmentX(JTextField.CENTER_ALIGNMENT);
                        Addres.setFont(font);
                        panel2.add(Addres);
                        scrollPane.revalidate();
                    }

                    panel2.add(Submit);

                    MainWindow.setVisible(true);
                    frame.setVisible(false);


                }  catch (NumberFormatException e){
                    JOptionPane.showMessageDialog(GUI.this, "Некорректный ввод");
                }
            }
        });

        Submit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int CorrectField = 0;
                for (JTextField Text : AddressFields) {
                    String IP = Text.getText();
                    if (IPCheck(IP)) {
                        CorrectField++;
                    } else {
                        JOptionPane.showMessageDialog(GUI.this, "Некорректный ввод в поле " + (AddressFields.indexOf(Text) + 1));
                    }
                }
                if (Searcher.NumberOfNodes == CorrectField) {
                    for (JTextField Text : AddressFields) {
                        String IP = Text.getText();
                        Searcher.IpAddr.add(IPCount, IP + "/161");
                        IPCount++;
                    }
                }

                if (IPCount > 0) {
                    Searcher.Search();
                    Submit.setEnabled(false);
                }
            }
        });

        MainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        MainWindow.getContentPane().add(scrollPane, BorderLayout.CENTER);
        MainWindow.setSize(500,300);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(panel, BorderLayout.NORTH);
        frame.setSize(500,200);
        frame.setVisible(true);
    }

    public static boolean IPCheck(String IP){
        Pattern p = Pattern.compile(IPPattern);
        Matcher m = p.matcher(IP);
        return m.matches();
    }

}