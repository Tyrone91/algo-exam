package assignment3;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Main {

    public static final String MY_LINUX_PATH = "/home/tyrone/dev/hs/algo-exam/uDrawGraph-3.1/bin/";
    public static final String MY_WINDOWS_PATH = "c:/dev/tools/uDraw(Graph)/bin/";
    
    static interface GraphSupplier {
        
        public String getGraphAsString(UDrawConnector connector);
        public void onActivation(JPanel container);
        
    }
    
    static class ControlWindow extends JFrame {
        
        private UDrawConnector m_Connector;
        private GraphSupplier m_CurrentSupplier;
        private List<GraphSupplier> m_GraphSuppliers;
        
        public ControlWindow(UDrawConnector connector, GraphSupplier...graphSuppliers ) {
            m_GraphSuppliers = Arrays.asList(graphSuppliers);
            m_Connector = connector;
            setTitle("UDraw Control");
            setLayout(new BorderLayout());
            setSize(200, 400);
            final JPanel content = new JPanel(new GridLayout(0, 1));
            
            JButton connectBttn = new JButton("Connect");
            connectBttn.addActionListener( e -> {
                connector.connect();
                connectBttn.setEnabled(false);
            });
            
            m_Connector.onDisconnect( () -> {
                connector.end();
                connectBttn.setEnabled(true);
            });
            
            JButton sendGraphBttn = new JButton("Send Graph");
            sendGraphBttn.addActionListener( e -> {
                connector.send(m_CurrentSupplier.getGraphAsString(connector));
            });
            
            final JPanel graphContainer = new JPanel();
            
            JComboBox<GraphSupplier> graphSelection = new JComboBox<>(graphSuppliers);
            m_CurrentSupplier = graphSuppliers[0];
            graphSelection.addActionListener( e -> {
                m_CurrentSupplier = (GraphSupplier)graphSelection.getSelectedItem();
                graphContainer.removeAll();
                m_CurrentSupplier.onActivation(graphContainer);
            });
            
            content.add(connectBttn);
            content.add(graphSelection);
            content.add(graphContainer);
            content.add(sendGraphBttn);
            
            add(content, BorderLayout.CENTER);
            
            init();
            setVisible(true);
        }
        
        private void init() {
            addWindowListener( new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    m_Connector.end();
                    dispose();
                }
            });
            
            
        }
        
    }

    public static void main(String[] args) {
        //PatriciaTree.test();
        //RedBlackTree.test();
        //RoBDD.test();
        
        UDrawConnector u = new UDrawConnector(MY_WINDOWS_PATH + "uDrawGraph");
        final String graph = u.newGraph(
                u.newNode("1"),
                u.newNode("2"),
                u.newNode("3")
                );
        
        u.send(graph);
        ControlWindow w  = new ControlWindow(u, new DebugGraph1());
        
        
        System.out.println("program over");
    }
    
    static class DebugGraph1 implements GraphSupplier {

        @Override
        public String getGraphAsString(UDrawConnector u) {
            return u.newGraph(
                    u.newNode("1"),
                    u.newNode("2"),
                    u.newNode("3")
                    );
        }

        @Override
        public void onActivation(JPanel container) {
            
        }
        
        @Override
        public String toString() {
            return this.getClass().getSimpleName();
        }
        
    }
}

