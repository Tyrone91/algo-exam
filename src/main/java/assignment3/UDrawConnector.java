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
    
    private static final String MY_WINDOWS_PATH = "C:\\dev\\tools\\uDraw(Graph\\en\\";
    private static final String U_DRAW_APPLICATION_PATH = "c:/dev/tools/uDraw(Graph)";
    private static final int UDRAW_PORT = 2542;
    
    private Socket m_Socket;
    private BufferedWriter m_Output;
    private BufferedReader m_Input;
    private Process m_Process;
    private Queue<String> m_Messages = new LinkedList<>();
    
    public UDrawConnector() {
        try{
            final String udrawPath = U_DRAW_APPLICATION_PATH;
            ProcessBuilder p = new ProcessBuilder(udrawPath + "/bin/uDrawGraph.exe", "-server");
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
    
    public static void main(String[] args) throws InterruptedException {
        
        final UDrawConnector udraw = new UDrawConnector();
        final String graph = udraw.newGraph(
                udraw.newNode("1"),
                udraw.newNode("2"),
                udraw.newNode("3")
                );
        
        Queue<String> messages = new LinkedList<>();
        
       new Thread(udraw).start();
       
       
       udraw.send("nothing");
       udraw.send("nothing");
       udraw.send(graph);
    //   udraw.send("nothing");
  //     udraw.send("nothing");
//       udraw.send("nothing");
       
       
    }
}
