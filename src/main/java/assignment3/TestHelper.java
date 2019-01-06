package assignment3;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

public class TestHelper {
    
    private Map<String, Supplier<Boolean>> m_Tests = new HashMap<>();
    private Runnable m_Before = () -> {};
    
    public TestHelper() {
        
    }
    
    public void runTests(String... byName ) {
        
        final Set<String> failedTests = new HashSet<>();
        
        if(byName.length == 0) {
            byName = m_Tests.keySet().toArray( new String[0]);
        }
        
        for(String str : byName) {
            Supplier<Boolean> test = m_Tests.get(str);
            m_Before.run();
            boolean res = test.get();
            if(!res) {
                //throw new RuntimeException(String.format("Test '%s' failed", str));
                failedTests.add(str);
            }
        }
        
        if(failedTests.isEmpty()) {            
            System.out.println("All tests are successful");
        } else {
            for(String s : failedTests) {
                System.out.println(String.format("Test '%s' failed", s));
            }
        }
        
        
    }
    
    public void addTest(String name, Supplier<Boolean> test) {
        m_Tests.put(name, test);
    }
    
    public void runBeforeTest(Runnable r) {
        m_Before = r;
    }
    
}
