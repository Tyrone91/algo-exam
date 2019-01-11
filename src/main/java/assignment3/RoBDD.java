package assignment3;

import java.util.HashMap;
import java.util.Map;

import assignment3.UDrawConnector.GraphNode;

class Function {
    
    private static final int TRUE = 0x7fffffff;
    private static final int FALSE = TRUE -1 ;
    
    private final int m_Variable;
    private final Function m_Then, m_Else;
    
    public Function(boolean b) {
        m_Variable = b ? TRUE : FALSE;
        m_Then = m_Else = null;
    }
    
    public Function(int var, Function _then, Function _else) {
        m_Variable = var;
        m_Then = _then;
        m_Else = _else;
    }
    
    private boolean equals(int var) {
        return var == m_Variable;
    }
    
    public Function getThen(int var) {
        return this.equals(var) ? m_Then : this;
    }
    
    public Function getElse(int var) {
        return this.equals(var) ? m_Else : this;
    }
    
    public int getVariable() {
        return m_Variable;
    }
    
    public boolean istrue() {
        return this.equals(TRUE);
    }
    
    public boolean isfalse() {
        return this.equals(FALSE);
    }
    
    public boolean isconstant() {
        return istrue() || isfalse();
    }
    
    @Override
    public boolean equals(Object obj) {
        if(obj == null) {
            return false;
        }
        
        if( !(obj instanceof Function)) {
            return false;
        }
        
        Function other = (Function)obj;
        
        return 
                other.m_Else == m_Else &&
                other.m_Then == m_Then &&
                other.m_Variable == m_Variable;
    }
    
}

class Triple {
    
    private final int m_Variable;
    private final Function m_Then;
    private final Function m_Else;
    
    private Triple(int var, Function _then, Function _else ) {
        m_Variable = var;
        m_Then = _then;
        m_Else = _else;
    }
    
    public static Triple of(int var, Function _then, Function _else ) {
        return new Triple(var, _then, _else);
    }
    
    @Override
    public boolean equals(Object obj) {
        if(obj == null) {
            return false;
        }
        
        if ( !(obj instanceof Triple)) {
            return false;
        }
        
        Triple other = (Triple) obj;
        
        return 
                other.m_Variable == m_Variable &&
                other.m_Then == m_Then &&
                other.m_Else == m_Else;
     }
    
    @Override
    public int hashCode() {
        return m_Variable ^ m_Then.hashCode() ^ m_Else.hashCode();
    }
}

public class RoBDD {
    
    private static int min(int varif, int varthen, int varelse) {
        int tmp = Math.min(varif, varthen);
        return Math.min(tmp, varelse);
    }
    
    private static int min(Function _if, Function _then, Function _else) {
        return min(_if.getVariable(), _then.getVariable(), _else.getVariable());
    }
    
    private static Function thenOf(Function f, int var) {
        return f.getThen(var);
    }
    
    private static Function elseOf(Function f, int var) {
        return f.getElse(var);
    }
    
    private final Function m_True = new Function(true);
    private final Function m_False = new Function(false);
    private Map<Triple, Function> m_FoundFunctions;
    private int m_FreeIndex = 0;

    private Map<String,Integer> m_NameToFunction;
    private Map<Integer,String> m_FunctionToName;
    
    public RoBDD() {
        m_FoundFunctions = new HashMap<>();
        m_NameToFunction = new HashMap<>();
        m_FunctionToName = new HashMap<>();
    }

    public String nameOf(int var) {
        return m_FunctionToName.get(var);
    }

    public int indexOf(String name) {
        if(m_NameToFunction.containsKey(name)) {
            return m_NameToFunction.get(name);
        }
        int index = m_FreeIndex++;
        m_NameToFunction.put(name, index);
        m_FunctionToName.put(index,name);
        return index;
    }
    
    public Function genTrue() {
        return m_True;
    }
    
    public Function genFalse() {
        return m_False;
    }
    
    public Function genVar(int i) {
        final Triple t = Triple.of(i, genTrue(), genFalse());
        Function former = m_FoundFunctions.get(t);
        if(former == null) {
            former = new Function(i, genTrue(), genFalse());
            m_FoundFunctions.put(t, former);
        }
        return former;
        
    }

    Function or(Function f, Function g) { // ite(f,1,g) = f ∧ 1 ∨ 	!f ∧ g
        return ite(f, genTrue(), g);
    }

    Function not(Function f) { // ite(f,0,1) = f ∧ 0 ∨ 	!f ∧ 1
        return ite(f, genFalse(), genTrue());
    }

    Function and(Function f, Function g) { //  ite(f,g,0) = f ∧ g ∨ 	!f ∧ 0
        return ite(f,g, genFalse());
    }

    Function implies(Function f, Function g) { // ite(f,g,1) = f ∧ g ∨ 	!f ∧ 1
        return ite(f,g, genTrue()); 
    }

    Function equivalent(Function f, Function g) {
        return ite(f,g, not(g));
    }
    
    Function ite(Function _if, Function _then, Function _else) {
        if(_if.istrue() ) { // ite(1,g,h) = g
            return _then;
        } else if( _if.isfalse() ) { // ite(0,g,h) = h
            return _else;
        } else if( _then.istrue() && _else.isfalse()) { // ite(f,1,0) = f
            return _if;
        } else if(_then == _else) { // ite(f,g,g) = g
            return _then;
        } else {
            final int var = min(_if, _then, _else);
            final Function T = ite( thenOf(_if, var), thenOf(_then, var), thenOf(_else, var));
            final Function E = ite( elseOf(_if, var), elseOf(_then, var), elseOf(_else, var));
            
            if(T.equals(E)) {
                return T;
            }
            final Triple t = Triple.of(var, T, E);
            Function former = m_FoundFunctions.get(t);
            if(former == null) {
                former = new Function(var, T, E);
                m_FoundFunctions.put(t, former);
            }
            return former;
        }
    }
    
    public GraphNode toUDraw() {
        int var = m_NameToFunction.values().stream().findFirst().get();
        return toUDraw(m_FoundFunctions.get(Triple.of(var, genTrue(), genFalse())), true);
    }
    
    public GraphNode toUDraw(Function f, boolean isTrue) {
        
        int var = f.getVariable();
        String postfix = isTrue ? "_true" : "_false";
        if(f.isconstant()) {
            postfix = "";
        }
        GraphNode me = new GraphNode(String.valueOf(var) + postfix);
        if(f.isconstant() ){
            me.attr().displayname = f.istrue() ? "TRUE" :  "FALSE";
            return me;
        }
        
        GraphNode _then = toUDraw(f.getThen(var), true);
        GraphNode _else = toUDraw(f.getElse(var), false);
        
        _then.attr().edgename = "true";
        _else.attr().edgename = "false";
        me.addChild(_then, _else);
        
        me.attr().displayname = m_FunctionToName.get(var);
        
        return me;
    }

    public static void test() {
        RoBDDTest t = new RoBDDTest();

        t.addTest("simple or", () -> {
            // a = true;
            // b = false
            // c = a v b
            
            Function x =  t.a.genVar( t.a.indexOf("x"));
            Function y =  t.a.genVar( t.a.indexOf("y"));
            Function z =  t.a.genVar( t.a.indexOf("z"));

            Function or1 = t.a.or(x,y);
            Function or2 = t.a.or( t.a.not(z), t.a.not(y));
            Function f = t.a.and(or1, or2);

            
            //x.getThen(t.a.m_True);

            System.out.println(t.a.nameOf(1));
            System.out.println(t.a.indexOf("y"));

            System.out.println(f.getThen(t.a.indexOf("x")).getVariable());
            Function exspectedY = f.getThen(t.a.indexOf("x"));
            final boolean first = exspectedY.getVariable() == t.a.indexOf("y");

            Function expectedTrue = exspectedY.getElse(t.a.indexOf("y"));
            boolean second = expectedTrue == t.a.m_True;

            Function expectedZ = exspectedY.getThen(t.a.indexOf("y"));
            boolean third = expectedZ.getVariable() == t.a.indexOf("z");

            return first && second && third;
        });

        t.runTests();
    }

    static class RoBDDTest extends TestHelper {

        public RoBDD a;

        public RoBDDTest() {
            this.runBeforeTest( () -> {
                a = new RoBDD();
            });
        }
    }


}
