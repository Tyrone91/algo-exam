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

import assignment3.UDrawConnector.Node;

public class Main {

    public static final String MY_LINUX_PATH = "/home/tyrone/dev/hs/algo-exam/uDrawGraph-3.1/bin/";
    public static final String MY_WINDOWS_PATH = "c:/dev/tools/uDraw(Graph)/bin/";
    
    static interface GraphSupplier {
        
        public String getGraphAsString(UDrawConnector connector);
        public void onActivation(UDrawConnector connector, JPanel container);
        
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
                m_CurrentSupplier.onActivation(connector, graphContainer);
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
        ControlWindow w  = new ControlWindow(u, new DebugGraph1(), new DebugGraph2(), new DebugGraphRedBlack(), new DebugGraphRoBDD());
        
        
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
        public void onActivation(UDrawConnector c, JPanel container) {
            
        }
        
        @Override
        public String toString() {
            return this.getClass().getSimpleName();
        }
        
    }
    
    static class DebugGraph2 implements GraphSupplier {
        
        
        
        @Override
        public String toString() {
            return this.getClass().getSimpleName();
        }

        @Override
        public String getGraphAsString(UDrawConnector connector) {
            UDrawConnector.Node root = new Node("root");
            
            Node left = new Node("left");
            Node right = new Node("right");
            
            Node grandChild = new Node("grandchild");
            
            root.addChild(left.addChild(grandChild), right);
            
            return root.toStringUDrawCommand();
        }

        @Override
        public void onActivation(UDrawConnector c, JPanel container) {
            c.sendClearScreen();
        }
        
    }
    
    static class DebugGraphRedBlack implements GraphSupplier {
        
        
        
        @Override
        public String toString() {
            return this.getClass().getSimpleName();
        }

        @Override
        public String getGraphAsString(UDrawConnector connector) {
            RedBlackTree<String, String> tree = new RedBlackTree<>();
            
            tree.insert("key1", "hi");
            tree.insert("key2", "Juhu");
            tree.insert("key3", "Hello");
            tree.insert("key4", "Bye");
            tree.insert("key5", "foo");
            tree.insert("key6", "something");
            tree.insert("key7", "something");
            tree.insert("key8", "something");
            
            return tree.toUDraw().toStringUDrawCommand();
        }

        @Override
        public void onActivation(UDrawConnector c, JPanel container) {
            c.sendClearScreen();
        }
        
    }
    
    static class DebugGraphRoBDD implements GraphSupplier {
        
        
        
        @Override
        public String toString() {
            return this.getClass().getSimpleName();
        }

        @Override
        public String getGraphAsString(UDrawConnector connector) {
            RoBDD t = new RoBDD();
            Function x =  t.genVar( t.indexOf("x"));
            Function y =  t.genVar( t.indexOf("y"));
            Function z =  t.genVar( t.indexOf("z"));

            Function or1 = t.or(x,y);
            Function or2 = t.or( t.not(z), t.not(y));
            Function f = t.and(or1, or2);
            
            return t.toUDraw(f).toStringUDrawCommand();
        }

        @Override
        public void onActivation(UDrawConnector c, JPanel container) {
            c.sendClearScreen();
        }
        
    }
}

