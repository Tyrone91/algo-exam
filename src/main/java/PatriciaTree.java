

import java.lang.reflect.Array;
import java.util.HashSet;
import java.util.Set;

public class PatriciaTree {

    private static final int CHAR_SIZE = 16;

    private static int bitAt(String src, int pos) {
        int index = pos / CHAR_SIZE;
        char pivotChar = src.length() > index ? src.charAt(index) : '\0';
        int  bitPos = pos % CHAR_SIZE;
        return (pivotChar & (1 << bitPos));
    }
    
    private static boolean left(String key, int bitPos) {
        return bitAt(key, bitPos) == 0;
    }
    
    private Node m_Root;
    
    public PatriciaTree() {
    }
    
    public boolean search(String c) {
        return new NodeHandler(m_Root).search(c).is(c);
    }
    
    public boolean insert(String c) {
        NodeHandler handler = new NodeHandler(m_Root).search(c);
        int index =  0;
        if(!handler.hasNext()) {
            if(handler.getParent() != null) {
                while( 
                        index < handler.getParent().position() &&
                        PatriciaTree.left(c, index) == PatriciaTree.left(handler.getParent().key(), index) ) {
                    ++index;
                }
                if(index == handler.getParent().position()) {
                    ++index;
                }
            }
        } else if(!handler.getNode().key().equals(c)) {
            while(PatriciaTree.left(c,index) == PatriciaTree.left(handler.getNode().key(), index)) {
                ++index;
            }
        } else {
            return false;
        }
        handler = new NodeHandler(m_Root).search(c,index);
        handler.set( new Node(c, index, handler.getNode() ), NodeHandler.NODE);
        return true;
        
    }
    
    public boolean remove(String c) {
        NodeHandler handler = new NodeHandler(m_Root).search(c);
        if(!handler.hasNext() || !handler.getNode().key().equals(c)) {
            return false;
        } else {
            NodeHandler handler2 = new NodeHandler(handler.getParent()).search(handler.getParent().key());
            handler.getNode().m_Key = handler.getParent().key();
            handler2.set(handler.getNode(), NodeHandler.NODE);
            handler.set(handler.silbing(NodeHandler.NODE), NodeHandler.PARENT);     
        }
        return true;
    }
    
    public boolean has(String c) {
        return search(c);
    }
    
    private class Node {
        
        private String m_Key;
        private int m_Position;
        private Node m_Left, m_Right;
        
        public Node(String key, int pos, Node succesor) {
            m_Key = key;
            m_Position =  pos;
            if(PatriciaTree.left(key,pos)) {
                m_Left = this;
                m_Right = succesor;
            } else {
                m_Left = succesor;
                m_Right = this;
            }
        }
        
        public String key() {
            return m_Key;
        }
        
        public int position() {
            return m_Position;
        }
        
        public Node left() {
            return m_Left;
        }
        
        public Node right() {
            return m_Right;
        }
        
        public Node left(Node newChild) {
            m_Left = newChild;
            return this;
        }
        
        public Node right(Node newChild) {
            m_Right = newChild;
            return this;
        }
    }
    
    private class NodeHandler {
        
        public static final int NODE = 0, PARENT = 1;
        
        private Node[] m_Nodes = (Node[]) Array.newInstance(Node.class, 3);
        
        public NodeHandler(Node n) {
            m_Nodes[NODE] = n;
        }
        
        public boolean hasNext() {
            return getNode() != null;
        }
        
        private boolean bitCheck(int last, int max) {
            return last < getNode().position() && max > getNode().position();
        }
        
        public NodeHandler search(String c, int maxPos) {
            int lastPos = -1;
            while(hasNext() && bitCheck(lastPos, maxPos)) {
                lastPos = getNode().position();
                down(left(c,lastPos));
            }
            return this;
        }
        
        public NodeHandler search(String c) {
            //return search(c, c.length() * CHAR_SIZE);
            return search(c, Integer.MAX_VALUE);
        }
        
        public Node getNode() {
            return m_Nodes[NODE];
        }
        
        public Node getParent() {
            return m_Nodes[PARENT];
        }
        
        public NodeHandler down(boolean left) {
            for(int i = m_Nodes.length-1; i > 0; --i) {
                m_Nodes[i] = m_Nodes[i-1];
            }
            m_Nodes[NODE] = left ? getParent().left() :  getParent().right();
            return this;
        }
        
        public boolean isLeft(Node n, int kind) {
            if(node(kind) != null) {
                return node(kind + 1).left() == node(kind); 
            } else {
                if(n.key().length() <= node(kind+1).position() ) {
                    return false;
                }
                return PatriciaTree.left(n.key(),  node(kind+1).position() );
            }
        }
        
        public boolean is(String c) {
            if(!hasNext()) {
                return false;
            }
            return getNode().key().equals(c);
        }
        
        public Node node(int kind) {
            return m_Nodes[kind];
        }
        
        public Node silbing(int kind) {
            Node parent = node(kind + 1);
            Node node =  node(kind);
            return parent.left() == node ? parent.right() : parent.left();
        }
        
        public NodeHandler set(Node n, int kind) {
            if(node(kind+1) == null) {
                m_Root = n;
            } else if( isLeft(n, kind)) {
                node(kind+1).left(n);
            } else {
                node(kind+1).right(n);
            }
            m_Nodes[kind] = n;
            return this;
        }
    }
    
    public UDrawConnector.GraphNode toUDraw() {
        if(m_Root == null ) {
            return new UDrawConnector.GraphNodeEmptyTree();
        }
        return toUDraw(m_Root, new HashSet<>());
    }
    
    private UDrawConnector.GraphNode toUDraw(Node n, Set<String> consumed) {
        UDrawConnector.GraphNode me = new UDrawConnector.GraphNode(n.key());
        me.attr().displayname = n.key() + "[" + n.position()+"]";
        
        if(consumed.contains(n.key())) {
            return me;
        }
        consumed.add(n.key());
        
        if(n.left() != null) {
            UDrawConnector.GraphNode l = toUDraw(n.left(), consumed);
            l.attr().edgename = "0";
            me.addChild(l);
        }
        
        if(n.right() != null) {
            UDrawConnector.GraphNode r = toUDraw(n.right(), consumed);
            r.attr().edgename = "1";
            me.addChild(r);
        }
        
        
        return me;
    }
    
    public static void main(String[] args) {
        test();
    }
    
    public static void test() {
        PatriciaTest test = new PatriciaTest();
        
        test.addTest("simple string insert", () -> {
            test.classUnderTest.insert("a");
            return test.classUnderTest.has("a") && !test.classUnderTest.has("b");
        });
        
        test.addTest("insert and remove char", () -> {
            test.insert("a");
            test.insert("b");
            test.insert("c");
            
            boolean first = test.has("a") && test.has("b") && test.has("c");
            
            test.remove("b");
            
            boolean second = test.has("a") && !test.has("b") && test.has("c");
            
            test.insert("d");
            
            boolean third = test.has("a") && !test.has("b") && test.has("c") && test.has("d");
            
            return first && second && third;
        });
        
        test.addTest("string insert", () -> {
            test.insert("Helo");
            
            return test.has("Helo") && !test.has("juhu");
        });
        
        test.addTest("string insert char compare", () -> {
           test.insert("hello");
           
           return test.has("hello") && !test.has("helo");
        });
        
        test.runTests();
    }
    
    static class PatriciaTest extends TestHelper {
        
        public PatriciaTree classUnderTest;
        
        public PatriciaTest() {
            this.runBeforeTest( () -> {
                classUnderTest = new PatriciaTree();
            });
        }

        
        public boolean has(String c) {
            return classUnderTest.has(c);
        }
        
        public boolean insert(String c) {
            return classUnderTest.insert(c);
        }
        
        public boolean remove(String c) {
            return classUnderTest.remove(c);
        }
    }
}
