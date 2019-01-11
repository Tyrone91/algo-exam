package assignment3;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;

public class UDrawConnector implements Runnable {
    
    
    private static final int UDRAW_PORT = 2542;
    
    private Socket m_Socket;
    private PrintWriter m_Writer;
    private BufferedReader m_Input;
    private Process m_Process;
    private Queue<String> m_Messages = new LinkedList<>();
    private String m_UdrawPath;
    private Runnable m_OnDisconnect = () -> {};
    
    public UDrawConnector(final String appPath) {
        m_UdrawPath = appPath;
    }
    
    public void connect() {
        try{
            System.out.println("udraw path:_" + m_UdrawPath);
            ProcessBuilder p = new ProcessBuilder(m_UdrawPath, "-server");
            m_Process = p.start();
            m_Socket = new Socket("127.0.0.1", UDRAW_PORT);
            m_Writer = new PrintWriter(m_Socket.getOutputStream(), true);
            //m_Output = new BufferedWriter( new OutputStreamWriter(m_Socket.getOutputStream() ));
            m_Input = new BufferedReader(new InputStreamReader(m_Socket.getInputStream()));
            run();
        } catch(Exception e) {
            e.printStackTrace(System.err);
        }
    }
    
    public void end() {
        System.out.println("ending");
        try {
            send("menu(file(close))");
            if(m_Writer != null) m_Writer.close();
            if(m_Socket != null) m_Socket.close();
            if(m_Process != null) m_Process.destroy();
        } catch (IOException e) {
            e.printStackTrace(System.err);
        }
        
    }
    
    public String newNode(String id) {
        return String.format("l(\"%s\",n(\"%s\",%s,%s))", id, "NODE", "[]", "[]");
        
    }
    
    public String newNode(String id, String... nodes) {
        
        return String.format("l(\"%s\",n(\"%s\",%s,%s))", id, "NODE", "[]", "[]");
    }
    
    public String edge(String id, String node) {
        return "";
    }
    
    public String newGraph(String... nodes) {
        return String.format("graph(new([%s]))", Arrays.stream(nodes).collect(Collectors.joining(",")));
                
    }
    
    public void setUDrawPath(String path) {
        m_UdrawPath = path;
    }
    
    private void send2(String msg) {
        try {
            System.out.println("sending: " + msg);
            //m_Output.write(msg);
            //m_Output.flush();
            m_Writer.println(msg);
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }
    
    public synchronized void send(String msg) {
        //m_Messages.add(msg);
        if(ready) {
            send2(msg);
            ready = false;
        } else {
            System.out.println("waiting: " + msg);
            m_Messages.add(msg);
        }
    }
    
    public void forceSend(String msg) {
        send2(msg);
    }
    
    public void onDisconnect(Runnable run) {
        m_OnDisconnect = run;
    }
    
    public void sendClearScreen() {
        send("menu(file(new)) ");
    }
        
    
    private boolean ready = false;
    
    @Override
    public void run() {
        try {
            new Thread( () -> {
                try {
                    String line = null;
                    while( (line = m_Input.readLine()) != null ) {
                        System.out.println("reading: " + line);
                        if(!ready && "ok".equals(line)) {
                            ready = true;
                            if(!m_Messages.isEmpty() ) {                                
                                send(m_Messages.poll());
                            }
                        }
                        
                        if("disconnect".equals(line)) {
                            m_OnDisconnect.run();
                        }
                    }
                    System.out.println("ending reading");
                } catch(Exception e) {
                    System.out.println(e);
                    m_OnDisconnect.run();
                }
                System.out.println("thread over");
            }).start();
            
        } catch(Exception e) {
            e.printStackTrace(System.err);
        }
        
    }
    
    @Override
    public void finalize() {
       end();
    }
    
    public static class GraphNode {
        
        public class Attributes {
            
            public String color = null;
            public String displayname = null;
            public String edgecolor = null;
            public String edgename = null;
            
            public Attributes() {
                
            }
            
            @Override
            public String toString() {
                StringBuilder b = new StringBuilder();
                b.append("[");
                
                if(color != null) {
                    b.append(set("COLOR", color));
                }
                
                if(displayname != null) {
                    b.append(set("OBJECT", displayname));
                }
                
                int comma = b.lastIndexOf(",");
                if(comma == -1) {
                    b.append("]");
                    return b.toString();
                }
                
                return b.substring(0, comma) + "]";
                
            }
            
            public String edgeattr() {
                StringBuilder b = new StringBuilder();
                b.append("[");
                
                if(edgecolor != null) {
                    b.append(set("EDGECOLOR", edgecolor));
                }
                
                if(edgename != null) {
                    b.append(set("OBJECT", edgename));
                }
                
                int comma = b.lastIndexOf(",");
                if(comma == -1) {
                    b.append("]");
                    return b.toString();
                }
                
                return b.substring(0, comma) + "]";
            }
            
            private String set(String key, String val) {
                return String.format("a(\"%s\",\"%s\"),", key,val);
            }
        }
        
        private List<GraphNode> m_Children;
        private String m_NodeID;
        private Attributes m_Attributes = new Attributes();
        private GraphNode m_Root = null;
        private Set<String> m_VisitedNodes = new HashSet<>();
        
        public GraphNode(String id) {
            m_NodeID = id;
            m_Children = new ArrayList<>();
        }
        
        public boolean isRoot() {
            return m_Root == null;
        }
        
        public GraphNode root() {
            if(isRoot()) {
                return this;
            }
            return m_Root;
        }
        
        private void setRoot(GraphNode n) {
            m_Root = n;
            m_Children.forEach( c -> c.setRoot(n));
        }
        
        public GraphNode addChild(GraphNode...nodes) {
            Arrays.stream(nodes).forEach(m_Children::add);
            Arrays.stream(nodes).forEach( n -> n.setRoot(root()));
            
            return this;
        }
        
        public Attributes attr() {
            return m_Attributes;
        }
        
        private String id() {
            return this.m_NodeID;
        }
        
        private List<GraphNode> children() {
            return m_Children;
        }
        
        private boolean isLeaf() {
            return children().isEmpty();
        }
        
        @Override
        public String toString() {
            if(isRoot() ) {                
                m_VisitedNodes.clear();
                m_VisitedNodes.add(id());
            }
            
            if(isLeaf()) {
                return emptyNode(this);
            }
            
            String children = children().stream().map( n -> n.toString(id())).collect(Collectors.joining(","));
            
            String attr = m_Attributes.toString();
            
            return String.format("l(\"%s\",n(\"%s\",%s, [%s]))", id(), "NODE", attr, children);
        }
        
        public String toString(String parentId) {
            if(root().m_VisitedNodes.contains(id()) ) {
                //return String.format("r(\"%s\")", id());
                final String ref = String.format("r(\"%s\")", id());
                return String.format("l(\"%s\",e(\"EDGE\",%s,%s))", edgeid(parentId, this), m_Attributes.edgeattr() , ref);
            }
            root().m_VisitedNodes.add(id());
            return String.format("l(\"%s\",e(\"EDGE\",%s,%s))", edgeid(parentId, this), m_Attributes.edgeattr() ,this.toString());
        }
        
        public String toStringUDrawCommand() {
            return String.format("graph(new([%s]))", this.toString());
        }
    }
    
    private static String emptyNode(GraphNode n) {
        return String.format("l(\"%s\",n(\"%s\",%s,%s))", n.id(), "NODE", n.m_Attributes.toString(), "[]");
    }
    
    public static class GraphNodeEmptyTree extends GraphNode {

        public GraphNodeEmptyTree() {
            super("NO_TREE_SO _EMPTY");
        }
        
        @Override
        public String toString() {
            return "[]";
        }
        
        @Override
        public String toStringUDrawCommand() {
            return String.format("graph(new(%s))", this.toString());
        }
        
    }
    
    private static String edgeid(String parent, GraphNode child) {
        return "edge_" + parent + "_" + child.id();
    }
    
}
