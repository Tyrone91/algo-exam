package assignment3;

import java.lang.reflect.Array;
import java.util.function.Function;

public class PatriciaTree {
    
    private static boolean left(char key, int bitPos) {
        return (key & (1 << bitPos)) == 0;
    }
    
    private Node m_Root;
    
    public PatriciaTree() {
        // TODO Auto-generated constructor stub
    }
    
    public boolean search(char c) {
        return new NodeHandler(m_Root).search(c).is(c);
    }
    
    private boolean stringwrapper(String str, Function<Character, Boolean> function) {
        for(int i = 0; i < str.length(); ++i) {
            char c = str.charAt(i);
            if(!function.apply(c)) {
                return false;
            }
        }
        return true;
    }
    
    public boolean search(String str) {
        return stringwrapper(str, this::search);
    }
    
    public boolean insert(String str) {
        return stringwrapper(str, this::insert);
    }
    
    public boolean remove(String str) {
        return stringwrapper(str, this::remove);
    }
    
    public boolean has(String str) {
        return search(str);
    }
    
    public boolean insert(char c) {
        NodeHandler handler = new NodeHandler(m_Root).search(c);
        int index =  0;
        if(!handler.hasNext()) {
            if(handler.getParent() != null) {
                while( PatriciaTree.left(c, index) == PatriciaTree.left(handler.getParent().key(), index) && index < handler.getParent().position() ) {
                    ++index;
                }
                if(index == handler.getParent().position()) {
                    ++index;
                }
            }
        } else if(handler.getNode().key() != c) {
            while(PatriciaTree.left(c,index) == PatriciaTree.left(handler.getNode().key(), index) ) {
                ++index;
            }
        } else {
            return false;
        }
        handler = new NodeHandler(m_Root).search(c,index);
        handler.set( new Node(c, index, handler.getNode() ), NodeHandler.NODE);
        return true;
        
    }
    
    public boolean remove(char c) {
        NodeHandler handler = new NodeHandler(m_Root).search(c);
        if(!handler.hasNext() || handler.getNode().key() != c) {
            return false;
        } else {
            NodeHandler handler2 = new NodeHandler(handler.getParent()).search(handler.getParent().key());
            handler.getNode().m_Key = handler.getParent().key();
            handler2.set(handler.getNode(), NodeHandler.NODE);
            handler.set(handler.silbing(NodeHandler.NODE), NodeHandler.PARENT);     
        }
        return true;
    }
    
    public boolean has(char c) {
        return search(c);
    }
    
    private class Node {
        
        private char m_Key;
        private int m_Position;
        private Node m_Left, m_Right;
        
        public Node(char key, int pos, Node succesor) {
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
        
        public Node(char key, int pos) {
            this(key, pos, null);
        }
        
        public char key() {
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
        
        public NodeHandler search(char c, int maxPos) {
            int lastPos = -1;
            while(hasNext() && bitCheck(lastPos, maxPos)) {
                lastPos = getNode().position();
                down(left(c,lastPos));
            }
            return this;
        }
        
        public NodeHandler search(char c) {
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
                return PatriciaTree.left(n.key(),  node(kind+1).position() );
            }
        }
        
        public boolean is(char c) {
            if(!hasNext()) {
                return false;
            }
            return getNode().key() == c;
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
    
    public static void main(String[] args) {
        test();
    }
    
    public static void test() {
        PatriciaTest test = new PatriciaTest();
        
        test.addTest("simple char insert", () -> {
            test.classUnderTest.insert('a');
            return test.classUnderTest.has('a') && !test.classUnderTest.has('b');
        });
        
        test.addTest("insert and remove char", () -> {
            test.insert('a');
            test.insert('b');
            test.insert('c');
            
            boolean first = test.has('a') && test.has('b') && test.has('c');
            
            test.remove('b');
            
            boolean second = test.has('a') && !test.has('b') && test.has('c');
            
            test.insert('d');
            
            boolean third = test.has('a') && !test.has('b') && test.has('c') && test.has('d');
            
            return first && second && third;
        });
        
        test.addTest("string insert", () -> {
            test.insert("Helo");
            
            return test.has("Helo") && !test.has("juhu");
        });
        
        test.addTest("string insert char compare", () -> {
           test.insert("helo");
           
           return test.has('h') && test.has('e') &test.has('l') && test.has("lo");
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
        
        public boolean has(char c) {
            return classUnderTest.has(c);
        }
        
        public boolean insert(char c) {
            return classUnderTest.insert(c);
        }
        
        public boolean remove(char c) {
            return classUnderTest.remove(c);
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