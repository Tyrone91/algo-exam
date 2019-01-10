package assignment3;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import BoolExprParser.BoolExprParser;
import BoolExprParser.BoolExprScanner;
import BoolExprParser.Node;
import assignment3.UDrawConnector.GraphNode;

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
        ControlWindow w  = new ControlWindow(u,
                new DebugGraph1(),
                new DebugGraph2(),
                new DebugGraphRedBlack(),
                new DebugGraphRoBDD(),
                new DebugGraphReference(),
                new DebugGraphPatricia(),
                new DebugGraphPatriciaRandom(),
                new GraphPatricia(),
                new RoBDDGraph()
                );
        
        
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
            UDrawConnector.GraphNode root = new GraphNode("root");
            
            GraphNode left = new GraphNode("left");
            GraphNode right = new GraphNode("right");
            
            GraphNode grandChild = new GraphNode("grandchild");
            
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
            
            return t.toUDraw(f, true).toStringUDrawCommand();
        }

        @Override
        public void onActivation(UDrawConnector c, JPanel container) {
            c.sendClearScreen();
        }
        
    }
    
    static class DebugGraphReference implements GraphSupplier {
        
        
        
        @Override
        public String toString() {
            return this.getClass().getSimpleName();
        }

        @Override
        public String getGraphAsString(UDrawConnector connector) {
            GraphNode root = new GraphNode("root");
            
            GraphNode x1 = new GraphNode("x");
            GraphNode y1 = new GraphNode("x");
            
            root.addChild(x1);
            x1.addChild(y1);
            
            return root.toStringUDrawCommand();
        }

        @Override
        public void onActivation(UDrawConnector c, JPanel container) {
            c.sendClearScreen();
        }
        
    }
    
    static class DebugGraphPatricia implements GraphSupplier {
        
        
        
        @Override
        public String toString() {
            return this.getClass().getSimpleName();
        }

        @Override
        public String getGraphAsString(UDrawConnector connector) {
            PatriciaTree t = new PatriciaTree();

            t.insert("S");
            t.insert("H");
            t.insert("X");
            
            t.insert("P");
            t.insert("R");
            t.insert("T");
                        
            return t.toUDraw().toStringUDrawCommand();
        }

        @Override
        public void onActivation(UDrawConnector c, JPanel container) {
            c.sendClearScreen();
        }
        
    }
    
    static class DebugGraphPatriciaRandom implements GraphSupplier {
        
        
        
        @Override
        public String toString() {
            return this.getClass().getSimpleName();
        }

        @Override
        public String getGraphAsString(UDrawConnector connector) {
            char[] abc = new char[26 * 2];
            for(int i = 0; i < abc.length / 2; ++i) {
                abc[i] = (char)('a' + i);
            }
            
            for(int i = 0; i < abc.length / 2; ++i) {
                abc[i + abc.length/2] = (char)('A' + i);
            }
            
            PatriciaTree t = new PatriciaTree();
            
            int MAX_INSERTS = 30;
            int MIN_WORD = 2;
            int MAX_WORD = 8;
            for(int i = 0; i <= MAX_INSERTS; ++i) {
                StringBuilder word = new StringBuilder();
                int length = (int)(Math.random() * (MAX_WORD - MIN_WORD)) + MIN_WORD;
                for(int j = 0; j <= length; ++j) {
                    int index = (int)(Math.random() * abc.length);
                    word.append(abc[index]);
                }
                t.insert(word.toString());
            }
            
            return t.toUDraw().toStringUDrawCommand();
        }

        @Override
        public void onActivation(UDrawConnector c, JPanel container) {
            c.sendClearScreen();
        }
        
    }
    
    static class GraphPatricia implements GraphSupplier {
        
        private PatriciaTree m_Tree;
        
        @Override
        public String toString() {
            return this.getClass().getSimpleName();
        }

        @Override
        public String getGraphAsString(UDrawConnector connector) {
            GraphNode udraw = m_Tree.toUDraw();
            if(udraw == null) {
                return "nothing";
            }
            return udraw.toStringUDrawCommand();
            
            /*
            m_Tree.insert("gh");
            m_Tree.insert("ghgh");
            m_Tree.insert("ghghh");
            m_Tree.insert("ghghhhh");
            
            m_Tree.remove("ghghh");
            */
            
            /*
            m_Tree.insert("f");
            m_Tree.insert("b");
            m_Tree.insert("j");
            m_Tree.insert("h");
            //m_Tree.remove("ghghhhh");
            
            return m_Tree.toUDraw().toStringUDrawCommand();*/
        }

        @Override
        public void onActivation(UDrawConnector c, JPanel container) {
            m_Tree = new PatriciaTree();
            c.sendClearScreen();
            
            container.setLayout(new GridLayout(0, 1));
            JPanel wrapper = new JPanel(new GridLayout(0, 1));
            
            JTextField keyInput = new JTextField();
            JButton insertBttn = new JButton("OK");
            
            JPanel controlPanel = new JPanel(new GridLayout(1, 2));
            controlPanel.add(keyInput);
            controlPanel.add(insertBttn);
            
            insertBttn.setEnabled(false);
            insertBttn.addActionListener(e -> {
                wrapper.remove(controlPanel);
                insertKey(keyInput.getText(), wrapper);
                wrapper.add(controlPanel);
                container.validate();
                keyInput.setText("");
                insertBttn.setEnabled(false);
            });
            
            keyInput.addKeyListener( new KeyAdapter() {
                @Override
                public void keyReleased(KeyEvent e) {
                    insertBttn.setEnabled(!keyInput.getText().isEmpty());
                }
            });
            
            wrapper.add(controlPanel);
            JScrollPane pane = new JScrollPane(wrapper);
            pane.setVerticalScrollBarPolicy( JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
            
            container.add(pane);
            container.validate();
        }
        
        private void insertKey(String key, JPanel target) {
            JLabel keyLabel = new JLabel(key);
            JButton removeBttn = new JButton("X");
            
            JPanel con = new JPanel();
            
            con.add(keyLabel);
            con.add(removeBttn);
            
            boolean res = m_Tree.insert(key);
            if(!res) {
                return;
            }
            
            target.add(con);
            removeBttn.addActionListener(e -> {
                m_Tree.remove(key);
                target.remove(con);
                target.validate();
            });
        }    
    }
    
    static class RoBDDGraph implements GraphSupplier {
        
        private String m_CurrentExpression;
        private JTextArea m_Output;
        
        @Override
        public String toString() {
            return this.getClass().getSimpleName();
        }

        @Override
        public String getGraphAsString(UDrawConnector connector) {
            m_Output.setText("");
            String res = "nothing";
            try {
                res = createTreeFrom(m_CurrentExpression);
            } catch(Throwable e) {
                m_Output.setText(e.toString());
            }
            
            return res;
        }
        
        @Override
        public void onActivation(UDrawConnector connector, JPanel container) {
            container.setLayout(new GridLayout(0, 1));
            m_CurrentExpression = "";
            m_Output = new JTextArea("");
            m_Output.setEditable(false);
            
            JTextField input = new JTextField();
            input.addKeyListener( new KeyAdapter() {
                @Override
                public void keyReleased(KeyEvent e) {
                    m_CurrentExpression = input.getText();
                }
            });
            container.add(input);
            container.add(m_Output);
            container.validate();
        }
        
        private String createTreeFrom(String str) throws Exception {
            BoolExprParser p = new BoolExprParser(new BoolExprScanner(new java.io.StringReader(str)));
            java_cup.runtime.Symbol s = p.parse();
            Node n = (Node)s.value;
            
            RoBDD t = new RoBDD();
            Function r = eval(t,n);
            return t.toUDraw(r, true).toStringUDrawCommand();
        }
        
        private Function eval(RoBDD tree, Node n) {
            if(n.type() == Node.Type.VAR) {
                return tree.genVar(tree.indexOf(n.name()));
            }
            
            
            if(n.type() == Node.Type.NOT) {
                Function left = eval(tree, n.left());
                return tree.not(left);
            }
            
            if(n.type() == Node.Type.AND) {
                Function left = eval(tree, n.left());
                Function right = eval(tree, n.right());
                return tree.and(left, right);
            }
            
            if(n.type() == Node.Type.OR) {
                Function left = eval(tree, n.left());
                Function right = eval(tree, n.right());
                return tree.or(left, right);
            }
            
            if(n.type() == Node.Type.EQUIV) {
                Function left = eval(tree, n.left());
                Function right = eval(tree, n.right());
                return tree.equivalent(left, right);
            }
            
            if(n.type() == Node.Type.IMPLIES) {
                Function left = eval(tree, n.left());
                Function right = eval(tree, n.right());
                return tree.implies(left, right);
            }
            
            System.out.println("should not happen");
            return null;
        }
    }

}

