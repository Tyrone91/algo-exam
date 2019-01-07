package assignment3;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import java.util.stream.Collectors;

public class UDrawConnector implements Runnable {
    
    
    private static final int UDRAW_PORT = 2542;
    
    private Socket m_Socket;
    private BufferedWriter m_Output;
    private BufferedReader m_Input;
    private Process m_Process;
    private Queue<String> m_Messages = new LinkedList<>();
    
    public UDrawConnector(final String appPath) {
        try{
            System.out.format("using: '%s'\n", appPath);
            ProcessBuilder p = new ProcessBuilder(appPath, "-server");
            m_Process = p.start();
            m_Socket = new Socket("127.0.0.1", UDRAW_PORT);
            m_Output = new BufferedWriter( new OutputStreamWriter(m_Socket.getOutputStream()));
            m_Input = new BufferedReader(new InputStreamReader(m_Socket.getInputStream()));
        } catch(Exception e) {
            e.printStackTrace(System.err);
        }
    }
    
    public void end() {
        try {
            m_Output.close();
            m_Socket.close();
            m_Process.destroy();
        } catch (IOException e) {
            e.printStackTrace(System.err);
        }
        
    }
    
    public String newNode(String id) {
        return String.format("l(\"%s\",n(\"%s\",%s,%s))", id, "NODE", "[]", "[]");
        
    }
    
    public String newGraph(String... nodes) {
        return String.format("graph(new([%s]))", Arrays.stream(nodes).collect(Collectors.joining(",")));
                
    }
    
    private void send2(String msg) {
        try {
            m_Output.write(msg);
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }
    
    public synchronized void send(String msg) {
        //m_Messages.add(msg);
        if(ready) {
            System.out.println("sending: " + msg);
            send2(msg);
        } else {
            System.out.println("waiting");
            m_Messages.add(msg);
        }
    }
        
    
    private boolean ready = false;
    
    @Override
    public void run() {
        try {
            new Thread( () -> {
                try {
                    String line = null;
                    while( (line = m_Input.readLine()) != null ) {
                        System.out.println("reading");
                        System.out.println(line);
                        if(!ready && "ok".equals(line)) {
                            ready = true;
                            m_Messages.forEach(this::send);
                        }
                    }
                    System.out.println("ending reading");
                } catch(Exception e) {
                    System.out.println(e);
                }
                
            }).start();
            
        } catch(Exception e) {
            e.printStackTrace(System.err);
        }
        
    }
    
    @Override
    public void finalize() {
       end();
    }
    
}
