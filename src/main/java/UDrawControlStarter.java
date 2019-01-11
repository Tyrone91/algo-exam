

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.Arrays;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import BoolExprParser.BoolExprParser;
import BoolExprParser.BoolExprScanner;
import BoolExprParser.Node;

public class UDrawControlStarter {

    public static final String MY_LINUX_PATH = "/home/tyrone/dev/hs/algo-exam/uDrawGraph-3.1/bin/uDrawGraph";
    public static final String MY_WINDOWS_PATH = "c:/dev/tools/uDraw(Graph)/bin/uDrawGraph";
    
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
            setSize(400, 350);
            //final JPanel content = new JPanel(new GridLayout(0, 1));
            final JPanel content = new JPanel();
            content.setLayout(new GridBagLayout());
            
            JTextField path = new JTextField(MY_WINDOWS_PATH);
            JButton search = new JButton("Open");
            
            JPanel pathContainer = new JPanel();
            pathContainer.add(path);
            pathContainer.add(search);
            
            search.addActionListener( l -> {
                JFileChooser c = new JFileChooser(System.getProperty("user.dir"));
                c.setFileSelectionMode(JFileChooser.FILES_ONLY);
                c.setMultiSelectionEnabled(false);
                c.showOpenDialog(this);
                File f = c.getSelectedFile();
                if(f == null) {
                    return;
                }
                path.setText(f.getAbsolutePath());
                connector.setUDrawPath(f.getAbsolutePath());
            });
            
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
            
            JPanel north = new JPanel( new GridBagLayout());
            final GridBagConstraints c = new GridBagConstraints();
            c.insets.top = 5;
            c.insets.left = 5;
            c.insets.bottom = 5;
            c.fill = GridBagConstraints.BOTH;
            c.anchor = GridBagConstraints.WEST;
            c.weightx = 1;
            c.gridy = 0;
            c.gridx = 0;
            north.add(path, c);
            c.insets.right = 5;
            c.insets.left = 0;
            c.gridx++;
            c.weightx = 0;
            north.add(search, c);
            
            c.insets.top = 0;
            c.insets.left = c.insets.right = 5;
            c.gridwidth = 2;
            c.weightx = 1;
            c.gridx = 0;
            c.gridy++;
            north.add(connectBttn, c);
            c.gridy++;
            north.add(graphSelection, c);
            //north.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            
            c.gridx = 0;
            c.gridy = 0;
            c.weighty = 0;
            c.weightx = 1;
            content.add(north,c );
            c.gridy++;
            c.weighty = 1;
            content.add(graphContainer,c);
            c.gridy++;
            c.weighty = 0;
            content.add(sendGraphBttn,c);
            
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
        PatriciaTree.test();
        RedBlackTree.test();
        RoBDD.test();
        
        UDrawConnector u = new UDrawConnector(MY_WINDOWS_PATH);
        ControlWindow w  = new ControlWindow(u,
                //new DebugGraph1(),
                //new DebugGraph2(),
                //new DebugGraphRedBlack(),
                //new DebugGraphRoBDD(),
                //new DebugGraphReference(),
                //new DebugGraphPatricia(),
                //new DebugGraphPatriciaRandom(),
                new GraphPatricia(),
                new RoBDDGraph(),
                new GraphRedBlack()
                );
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
            UDrawConnector.GraphNode root = new UDrawConnector.GraphNode("root");
            
            UDrawConnector.GraphNode left = new UDrawConnector.GraphNode("left");
            UDrawConnector.GraphNode right = new UDrawConnector.GraphNode("right");
            
            UDrawConnector.GraphNode grandChild = new UDrawConnector.GraphNode("grandchild");
            
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
            UDrawConnector.GraphNode root = new UDrawConnector.GraphNode("root");
            
            UDrawConnector.GraphNode x1 = new UDrawConnector.GraphNode("x");
            UDrawConnector.GraphNode y1 = new UDrawConnector.GraphNode("x");
            
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
            return "Patricia Tree";
        }

        @Override
        public String getGraphAsString(UDrawConnector connector) {
            UDrawConnector.GraphNode udraw = m_Tree.toUDraw();
            if(udraw == null) {
                return "graph(new([]))";
            }
            return udraw.toStringUDrawCommand();
        }

        @Override
        public void onActivation(UDrawConnector c, JPanel container) {
            m_Tree = new PatriciaTree();
            c.sendClearScreen();
            
            container.setLayout(new GridLayout(0, 1));
            JPanel wrapper = new JPanel(new GridBagLayout());
            final GridBagConstraints gc = new GridBagConstraints();
            
            JTextField keyInput = new JTextField();
            JButton insertBttn = new JButton("OK");
            
            JPanel controlPanel = new JPanel( new GridBagLayout());
            gc.gridx = 0;
            gc.weightx = 1;
            gc.fill = GridBagConstraints.BOTH;
            gc.insets.left = 5;
            controlPanel.add(keyInput,gc);
            gc.insets.left = 0;
            gc.insets.right = 5;
            gc.weightx = 0;
            gc.gridx++;
            controlPanel.add(insertBttn,gc);
            gc.insets.right = 0;
            
            insertBttn.setEnabled(false);
            insertBttn.addActionListener(e -> {
                wrapper.remove(controlPanel);
                
                
                
                JComponent res = insertKey(keyInput.getText(), wrapper);
                if(res != null) {
                    gc.fill = GridBagConstraints.BOTH;
                    gc.gridy++;
                    gc.weightx = 1;
                    wrapper.add(res, gc);
                }
                
                gc.fill = GridBagConstraints.BOTH;
                gc.gridy++;
                gc.weightx = 1;
                gc.insets.top = 5;
                
                wrapper.add(controlPanel,gc);
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
            
            gc.fill = GridBagConstraints.BOTH;
            gc.gridy++;
            gc.gridx = 0;
            gc.weightx = 1;
            wrapper.add(controlPanel, gc);
            JScrollPane pane = new JScrollPane(wrapper);
            pane.setVerticalScrollBarPolicy( JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
            
            container.add(pane);
            container.validate();
        }
        
        private JComponent insertKey(String key, JPanel target) {
            JLabel keyLabel = new JLabel(key);
            keyLabel.setHorizontalTextPosition(JLabel.CENTER);
            keyLabel.setHorizontalAlignment(JLabel.CENTER);
            JButton removeBttn = new JButton("X");
            
            JPanel con = new JPanel( new GridBagLayout());
            final GridBagConstraints c = new GridBagConstraints();
            c.fill = GridBagConstraints.BOTH;
            c.gridx = 0;
            c.weightx = 1;
            c.anchor = GridBagConstraints.CENTER;
            con.add(keyLabel,c);
            
            c.gridx++;
            c.weightx = 0;
            con.add(removeBttn,c);
            
            boolean res = m_Tree.insert(key);
            if(!res) {
                return null;
            }
            
            removeBttn.addActionListener(e -> {
                m_Tree.remove(key);
                target.remove(con);
                target.validate();
                target.repaint();
            });
            return con;
        }    
    }
    
    static class RoBDDGraph implements GraphSupplier {
        
        private String m_CurrentExpression;
        private JTextArea m_Output;
        
        @Override
        public String toString() {
            return "RoBDD Tree";
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
    
    static class GraphRedBlack implements GraphSupplier {
        
        private RedBlackTree<String, String> m_Tree;
        private boolean asTop234 = false;
        
        @Override
        public String toString() {
            return "Rot-Schwarz Baum";
        }

        @Override
        public String getGraphAsString(UDrawConnector connector) {
            UDrawConnector.GraphNode udraw =  asTop234 ? m_Tree.toUDrawTop234() : m_Tree.toUDraw();
            if(udraw == null) {
                return "nothing";
            }
            return udraw.toStringUDrawCommand();
        }

        @Override
        public void onActivation(UDrawConnector c, JPanel container) {
            m_Tree = new RedBlackTree<>();
            c.sendClearScreen();
            
            final GridBagConstraints gc = new GridBagConstraints();
            container.setLayout(new GridLayout(0, 1));
            JPanel wrapper = new JPanel(new GridBagLayout());
            
            JCheckBox checkbox = new JCheckBox("Top2-3-4");
            checkbox.setSelected(asTop234);
            checkbox.addChangeListener( l -> {
                asTop234 = checkbox.isSelected();
            });
            
            
            JTextField keyInput = new JTextField();
            JTextField labelInput = new JTextField();
            JButton insertBttn = new JButton("OK");
            
            JPanel controlPanel = new JPanel(new GridBagLayout());
            final GridBagConstraints gc2 = new GridBagConstraints();
            
            gc2.fill = GridBagConstraints.BOTH;
            gc2.weightx = 1;
            gc2.insets.left = 5;
            controlPanel.add(keyInput,gc2);
            gc2.insets.left = 0;
            controlPanel.add(labelInput,gc2);
            
            gc2.insets.left = 3;
            gc2.weightx = 0;
            gc2.insets.right = 5;
            controlPanel.add(insertBttn,gc2);
            
            insertBttn.setEnabled(false);
            insertBttn.addActionListener(e -> {
                wrapper.remove(controlPanel);
                JComponent res = insertKey(keyInput.getText(),labelInput.getText(), wrapper);
                if(res != null) {
                    gc.gridy++;
                    wrapper.add(res, gc);
                }
                gc.gridy++;
                wrapper.add(controlPanel, gc);
                container.validate();
                keyInput.setText("");
                labelInput.setText("");
                insertBttn.setEnabled(false);
            });
            
            keyInput.addKeyListener( new KeyAdapter() {
                @Override
                public void keyReleased(KeyEvent e) {
                    insertBttn.setEnabled(!keyInput.getText().isEmpty());
                }
            });
            
            gc.fill = GridBagConstraints.BOTH;
            gc.gridy = 0;
            gc.gridx = 0;
            gc.weightx = 1;
            gc.insets.bottom = 5;
            
            wrapper.add(checkbox,gc);
            gc.gridy++;
            wrapper.add(controlPanel,gc);
            JScrollPane pane = new JScrollPane(wrapper);
            pane.setVerticalScrollBarPolicy( JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
            
            container.add(pane);
            container.validate();
        }
        
        private JComponent insertKey(String key, String value, JPanel target) {
            JLabel keyLabel = new JLabel(key + ":" + value);
            keyLabel.setHorizontalAlignment(JLabel.CENTER);
            JButton removeBttn = new JButton("X");
            
            JPanel con = new JPanel(new GridBagLayout());
            final GridBagConstraints c = new GridBagConstraints();
            
            c.fill = GridBagConstraints.BOTH;
            c.weightx = 1;
            c.weighty = 0;
            c.gridx = 0;
            
            con.add(keyLabel,c);
            
            c.weightx = 0;
            c.gridx++;
            c.insets.left = c.insets.right=  5;
            con.add(removeBttn,c);
            
            boolean res = m_Tree.insert(key,value);
            if(!res) {
                return null;
            }
            
            removeBttn.addActionListener(e -> {
                m_Tree.remove(key);
                target.remove(con);
                target.validate();
                target.repaint();
            });
            return con;
        }    
    }

}

