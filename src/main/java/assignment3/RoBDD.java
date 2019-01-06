package assignment3;

import java.util.HashMap;
import java.util.Map;

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
    
    public RoBDD() {
        m_FoundFunctions = new HashMap<>();
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
    
    Function ite(Function _if, Function _then, Function _else) {
        if(_if.istrue() ) {
            return _then;
        } else if( _if.isfalse() ) {
            return _else;
        } else if( _then.istrue() && _else.isfalse()) {
            return _if;
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
}
