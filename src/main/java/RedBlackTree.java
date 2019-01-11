

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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
                    handler2.join();
                    while( handler2.node(NodeHandler.NODE).hasLeft() ) {
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
        
        @SuppressWarnings("unchecked")
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

            if(!getNode().isToJoin() ) {
                return this;
            }
            

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
            return this;
        }
    }
    
    public static void main(String[] args) {
        
        test();
    }
    
    public static void test() {
        RedBlackTreeTest h = new RedBlackTreeTest();
        
        h.addTest("simple insert", () -> {
            
            h.classUnderTest.insert(String.valueOf(0), "Hello");
            h.classUnderTest.insert(String.valueOf(1), "Juhu");
            h.classUnderTest.insert(String.valueOf(2), "Welt");
            
            return h.has(0) && h.has(1) && h.has(2);
        });
        
        h.addTest("simple remove", () -> {
            h.classUnderTest.insert(String.valueOf(0), "Hello");
            h.classUnderTest.insert(String.valueOf(1), "Juhu");
            h.classUnderTest.insert(String.valueOf(2), "Welt");
            h.classUnderTest.insert(String.valueOf(4), "Nicht");
            
            boolean first = h.has(0) && h.has(1) && h.has(2) && h.has(4);
            
            h.classUnderTest.remove(String.valueOf(4));
            
            boolean second = h.has(0) && h.has(1) && h.has(2) && !h.has(4);
            return first && second;
        });
        
        h.addTest("remove and add", () -> {
            h.classUnderTest.insert(String.valueOf(0), "Hello");
            h.classUnderTest.insert(String.valueOf(1), "Juhu");
            h.classUnderTest.insert(String.valueOf(2), "Welt");
            h.classUnderTest.insert(String.valueOf(3), "Nicht");
            
            boolean first = h.has(0) && h.has(1) && h.has(2) && h.has(3);
            
            h.classUnderTest.remove(String.valueOf(3));
            
            boolean second = h.has(0) && h.has(1) && h.has(2) && !h.has(3);
            
            h.classUnderTest.insert(String.valueOf(17), "YEAH");
            
            boolean third = h.has(0) && h.has(1) && h.has(2) && !h.has(3) && h.has(17);
            
            return first && second && third;
        });
        
        h.runTests();
    }
    
    public UDrawConnector.GraphNode toUDraw() {
        if(m_Root == null) {
            return new UDrawConnector.GraphNodeEmptyTree();
        }
        return toUDraw(m_Root);
    }
    
    public UDrawConnector.GraphNode toUDrawTop234() {
        if(m_Root == null) {
            return new UDrawConnector.GraphNodeEmptyTree();
        }
        return toUDrawTop234(m_Root);
    }
    
    public UDrawConnector.GraphNode toUDrawTop234(Node n) {
        List<String> id = new ArrayList<>();
        List<Node> nextNodes = new ArrayList<>();
        id.add(n.key().toString());
        
        if(n.leftIsRed() ) {
            id.add(n.left().key().toString());
            nextNodes.add(n.left().left());
            nextNodes.add(n.left().right());
        } else {
            nextNodes.add(n.left());
        }
        
        if(n.rightIsRed() ) {
            id.add(n.right().key().toString());
            nextNodes.add(n.right().left());
            nextNodes.add(n.right().right());
        } else {
            nextNodes.add(n.right());
        }
        
        UDrawConnector.GraphNode me = new UDrawConnector.GraphNode(id.stream().collect(Collectors.joining("_")));
        me.attr().displayname = id.stream()
                .sorted()
                .collect(Collectors.joining(" "));
        nextNodes.stream()
            .filter(Objects::nonNull)
            .map(this::toUDrawTop234)
            .forEach(me::addChild);
        
        return me;
    }
    
    private UDrawConnector.GraphNode toUDraw(Node n) {
        
        final UDrawConnector.GraphNode me = new UDrawConnector.GraphNode(n.key().toString());
        me.attr().displayname = n.key().toString() + " = " + n.data().toString();
        me.attr().edgecolor = n.isRed() ? "#e20000" : "#000000";
        if(n.hasLeft()) {
            me.addChild(toUDraw(n.left()));
        }
        
        if(n.hasRight()) {
            me.addChild(toUDraw(n.right()));
        }
        
        return me;
    }
    
    static class RedBlackTreeTest extends TestHelper {
        
        public RedBlackTree<String, String> classUnderTest;
        
        public RedBlackTreeTest() {
            runBeforeTest( () -> {
                classUnderTest = new RedBlackTree<>();
            });
        }
        
        public boolean has(String key) {
            return classUnderTest.has(key);
        }

        public boolean has(int key) {
            return classUnderTest.has(String.valueOf(key));
        }
        
    }
}
