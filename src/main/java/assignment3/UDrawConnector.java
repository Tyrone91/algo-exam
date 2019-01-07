package assignment3;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
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
    
}
