package assignment3;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

class Comparison<T extends Comparable<T>> {
    
    private T m_First, m_Second;
    
    private Comparison(T first, T second) {
        m_First = first;
        m_Second = second;
    }
    
    public int compare() {
        return m_First.compareTo(m_Second);
    }
    
    public boolean isLess() {
        return compare() < 0;
    }
    
    public boolean isEqual() {
        return compare() == 0;
    }
    
    public boolean isGreater() {
        return compare() > 0;
    }
    
    
    public static <T extends Comparable<T>> Comparison<T> of(T first, T second) {
        return new Comparison<T>(first, second);
    }
}

public class RedBlackTree<K extends Comparable<K>, T>{
    
    
    
    
    private Node m_Root;
    
    public Node search(K pivot) {
        Node tmp = m_Root;
        while(tmp != null) {
            Comparison<K> c = Comparison.of(pivot, tmp.key());
            if(c.isEqual()) {
                return tmp;
            }
            tmp = c.isLess() ? tmp.left() : tmp.right();
        }
        return null;
    }
    
    
    public boolean insert(K key, T obj) {
        final NodeHandler handler = new NodeHandler(m_Root);
        while(handler.hasNext()) {
            Node pivot = handler.node(NodeHandler.NODE);
            if(pivot.isToSplit() ) {
                pivot.convertNode();
                handler.split();
            }
            final Comparison<K> c = Comparison.of(key, pivot.key());
            if(c.isEqual()) {
                return false;
            }
            handler.down(c.isLess());
        }
        handler.set( new Node(key, obj), NodeHandler.NODE);
        handler.split();
        m_Root.removeRed();
        return true;
    }
    
    public boolean remove(K key) {
        final NodeHandler handler = new NodeHandler(m_Root);
        while(handler.hasNext()) {
            handler.join();
            final Node pivot = handler.node(NodeHandler.NODE);
            final Comparison<K> c = Comparison.of(key, pivot.key());
            if(c.isEqual()) {
                if(!pivot.hasRight()) {
                    handler.set(pivot.left(), NodeHandler.NODE, true);
                } else {
                    final NodeHandler handler2 = new NodeHandler(handler);
                    handler2.down(false);
                    handler.join();
                    while( handler.node(NodeHandler.NODE).hasLeft() ) {
                        handler2.down(true);
                        handler2.join();
                    }
                    handler.getNode().assign( handler2.getNode());
                    handler2.set( handler2.getNode().right(), NodeHandler.NODE, true);
                    
                }
                
                if(m_Root != null) {
                    m_Root.removeRed();
                }
                return true;
            }
            handler.down(c.isLess());
        }
        return false;
    }
    
    public boolean has(K key) {
        return search(key) != null;
    }
    
    public void print() {
        final List<List<Node>> list = new ArrayList<>();
        printHelp(m_Root, list, 0);
        
        for(List<Node> row : list) {
            for(Node n : row) {
                System.out.print(n.key() + ": " + n.data() + " - ");
            }
            System.out.println();
        }
    }
    
    private void printHelp(Node parent, List<List<Node>> rows, int depth) {
        if(parent == null) {
            return;
        }
        
        if(rows.size() <= depth ) {
            rows.add( new ArrayList<>());
        }
        rows.get(depth).add(parent);
        
        printHelp(parent.left(), rows, depth + 1);
        printHelp(parent.right(), rows, depth + 1);
        
    }
    
    private class Node {
        
        private K m_Key;
        private T m_Data;
        private Node m_Left, m_Right;
        private boolean m_Red;
        
        public Node(K key, T data) {
            m_Key = key;
            m_Data = data;
            m_Left = null;
            m_Right = null;
            m_Red = true;
        }
        
        public Node assign(final Node src) {
            m_Key = src.m_Key;
            m_Data = src.m_Data;
            return this;
        }
        
        private boolean checkRed(Node n) {
            return n != null && n.isRed();
        }
        
        public boolean leftIsRed() {
            return checkRed(left());
         }
        
        public boolean rightIsRed() {
            return checkRed(right());
        }
        
        public boolean hasLeft() {
            return m_Left != null;
        }
        
        public boolean hasRight() {
            return m_Right != null;
        }
        
        public boolean isRed() {
            return m_Red;
        }
        
        public boolean toggleRed() {
            m_Red = !m_Red;
            return m_Red;
        }
        
        public Node makeRed() {
            m_Red = true;
            return this;
        }
        
        public Node setRed(boolean val) {
            m_Red = val;
            return this;
        }
        
        public Node removeRed() {
            m_Red = false;
            return this;
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
        
        public K key() {
            return m_Key;
        }
        
        public T data() {
            return m_Data;
        }
        
        public boolean isToSplit() {
            return leftIsRed() && rightIsRed();
        }
        
        public boolean isToJoin() {
            return !isRed() && ( !hasLeft() || !left().isRed() ) && ( !hasRight() || !right().isRed());
        }
        
        public Node convertNode() {
            if(!isToSplit()) {
                return this;
            }
            left().removeRed();
            right().removeRed();
            this.makeRed();
            return this;
        }
    }
    
    private class NodeHandler {
        public static final int NODE = 0;
        public static final int PARENT = 1;
        public static final int GRAND_PARENT = 2;
        public static final int GRAND_GRAND_PARENT = 3;
        
        private Node[] m_Nodes = (RedBlackTree<K, T>.Node[]) Array.newInstance( Node.class, 4);
        
        public NodeHandler(Node node) {
            m_Nodes[NODE] = node;
        }
        
        public NodeHandler(final NodeHandler src) {
            for(int i = 0; i < m_Nodes.length; ++i) {
                m_Nodes[i] = src.m_Nodes[i];
            }
        }
        
        private Node node(int kind) {
            return m_Nodes[kind];
        }
        
        private boolean isLeftChild(Node n, int kind) {
            if(node(kind) != null) {
                return node(kind+1).left() == node(kind);
            } else {
                return Comparison.of(n.key(), node(kind+1).key()).isLess();
            }
        }
        
        public Node getNode() {
            return node(NODE);
        }
        
        public Node getParent() {
            return node(PARENT);
        }
        
        public Node getGrandParent() {
            return node(GRAND_PARENT);
        }
        
        public Node getGrandGrandParent() {
            return node(GRAND_GRAND_PARENT);
        }
        
        public NodeHandler set(Node n, int kind, boolean copyColor) {
            if(node(kind+1) == null) {
                m_Root = n;
            } else if(isLeftChild(n, kind)) {
                node(kind+1).left(n); 
            } else {
                node(kind+1).right(n);
            }
            
            if(copyColor && node(kind) != null && n != null) {
                n.setRed(node(kind).isRed());
            }
            m_Nodes[kind] = n;
            return this;
        }
        
        public NodeHandler set(Node n, int kind) {
            return set(n,kind,false);
        }
        
        void rotate(int kind) {
            Node parent = node(kind);
            Node child = node(kind-1);
            boolean childIsRed = child.isRed();
            
            if(!childIsRed) {
                if(child.hasLeft() ) {
                    child.left().removeRed();
                }
                
                if(child.hasRight() ) {
                    child.right().removeRed();
                }
                
                parent.removeRed();
                parent.left().makeRed();
                parent.right().makeRed();
            } else {
                child.setRed(parent.isRed());
                parent.setRed(childIsRed);
            }
            
            if(parent.left() == child) {
                parent.left(child.right());
                child.right(parent);
            } else {
                parent.right(child.left());
                child.left(parent);
            }
            set(child, kind);
            
        }
        
        public boolean isNull() {
            return m_Nodes[NODE] == null;
        }
        
        public boolean hasNext() {
            return !isNull();
        }
        
        public NodeHandler down(boolean left) {
            for(int i = m_Nodes.length-1; i > 0; --i) {
                m_Nodes[i] = m_Nodes[i-1];
            }
            m_Nodes[NODE] = left ? node(PARENT).left() : node(PARENT).right();
            return this;
        }
        
        public NodeHandler split() {
            Node grandparent = node(GRAND_PARENT);
            Node parent = node(PARENT);
            Node node = node(NODE);
            if(parent != null && parent.isRed()) {
                Comparison<K> grandParentToParent = Comparison.of(grandparent.key(), parent.key());
                Comparison<K> parentToNode = Comparison.of(parent.key(), node.key());
                if( grandParentToParent.isLess() != parentToNode.isLess() ) {
                    rotate(PARENT);
                }
                rotate(GRAND_PARENT);
            }
            return this;
        }
        
        public NodeHandler getNephew() {
            Node sibling = getNode() == getParent().left() ? getParent().right() : getParent().left();
            Node nephew = getNode() == getParent().left() ? sibling.left() : sibling.right();
            
            final NodeHandler handler = new NodeHandler(nephew);
            handler.m_Nodes[PARENT] = sibling;
            handler.m_Nodes[GRAND_PARENT] = getParent();
            handler.m_Nodes[GRAND_GRAND_PARENT] = getGrandParent();
            return handler;
        }
        
        public NodeHandler join() {
            if(getNode().isToJoin() ) {
                if(     
                        getParent() == null && 
                        getNode().hasLeft() && getNode().left().isToJoin() &&
                        getNode().hasRight() && getNode().right().isToJoin() 
                ){
                    getNode().left().makeRed();
                    getNode().right().makeRed();
                    
                } else if(getParent() != null) {
                    NodeHandler nephewHandler = getNephew();
                    if(nephewHandler.getParent().isRed() ) {
                        nephewHandler.rotate(GRAND_PARENT);
                        m_Nodes[GRAND_GRAND_PARENT] = getGrandParent();
                        m_Nodes[GRAND_PARENT] = nephewHandler.getGrandParent();
                        nephewHandler = getNephew();
                    }
                    
                    if(nephewHandler.getParent().isToJoin()) {
                        getNode().makeRed();
                        nephewHandler.getParent().makeRed();
                        getParent().removeRed();
                    } else {
                        
                        if(nephewHandler.hasNext() && nephewHandler.getNode().isRed() ) {
                            nephewHandler.rotate(PARENT);
                        }
                        nephewHandler.rotate(GRAND_PARENT);
                    }
                }
            }
            return this;
        }
    }
    
    public static void main(String[] args) {
        
        test();
    }
    
    public static void test() {
        RedBlackTreeTest h = new RedBlackTreeTest();
        
        h.addTest("simple insert", () -> {
            
            h.classUnderTest.insert(0, "Hello");
            h.classUnderTest.insert(1, "Juhu");
            h.classUnderTest.insert(2, "Welt");
            
            return h.has(0) && h.has(1) && h.has(2);
        });
        
        h.addTest("simple remove", () -> {
            h.classUnderTest.insert(0, "Hello");
            h.classUnderTest.insert(1, "Juhu");
            h.classUnderTest.insert(2, "Welt");
            h.classUnderTest.insert(3, "Nicht");
            
            boolean first = h.has(0) && h.has(1) && h.has(2) && h.has(3);
            
            h.classUnderTest.remove(3);
            
            boolean second = h.has(0) && h.has(1) && h.has(2) && !h.has(3);
            return first && second;
        });
        
        h.addTest("remove and add", () -> {
            h.classUnderTest.insert(0, "Hello");
            h.classUnderTest.insert(1, "Juhu");
            h.classUnderTest.insert(2, "Welt");
            h.classUnderTest.insert(3, "Nicht");
            
            boolean first = h.has(0) && h.has(1) && h.has(2) && h.has(3);
            
            h.classUnderTest.remove(3);
            
            boolean second = h.has(0) && h.has(1) && h.has(2) && !h.has(3);
            
            h.classUnderTest.insert(17, "YEAH");
            
            boolean third = h.has(0) && h.has(1) && h.has(2) && !h.has(3) && h.has(17);
            
            System.out.println("first: " + first);
            System.out.println("second: " + second);
            System.out.println("third: " + third);
            
            return first && second && third;
        });
        
        h.addTest("negative test", () -> {
            h.classUnderTest.insert(0, "Hello");
            h.classUnderTest.insert(1, "Juhu");
            h.classUnderTest.insert(2, "Welt");
            
            return h.has(4);
        });
        
        h.addTest("print-test", () -> {
            h.classUnderTest.insert(0, "Hello");
            h.classUnderTest.insert(1, "Juhu");
            h.classUnderTest.insert(2, "Welt");
            h.classUnderTest.insert(3, "Nicht");
            
            h.classUnderTest.remove(3);
            h.classUnderTest.insert(17, "YEAH");
            
            h.classUnderTest.print();
            
            return true;
        });
        
        h.addTest("print-test", () -> {
            
            
            for(int i = 0; i < 25; ++i) {
                h.classUnderTest.insert(i, String.valueOf(i));
            }
            
            for(int i = 0; i < 20; ++i) {
                h.classUnderTest.remove(i);
            }
            
            h.classUnderTest.print();
            
            return true;
        });
        
        h.runTests();
    }
    
    static class RedBlackTreeTest extends TestHelper {
        
        public RedBlackTree<Integer, String> classUnderTest;
        
        public RedBlackTreeTest() {
            runBeforeTest( () -> {
                classUnderTest = new RedBlackTree<>();
            });
        }
        
        public boolean has(int key) {
            return classUnderTest.has(key);
        }
        
    }
}