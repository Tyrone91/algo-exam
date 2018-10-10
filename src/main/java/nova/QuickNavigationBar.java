package nova;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class QuickNavigationBar extends JComponent {

    private Map<String, NavEntry> m_Entries;

    public QuickNavigationBar(){
        setLayout( new FlowLayout(FlowLayout.LEFT));
        m_Entries = new HashMap<>();
    }

    public QuickNavigationBar addNavEntry(String title, String rep, Runnable command ){

        NavEntry nav = m_Entries.get(title);
        if(nav == null){
            nav = new NavEntry(title);
            m_Entries.put(title, nav);
        }
        nav.addEntry(rep, command);
        return this;
    }
    
    public QuickNavigationBar addNavEntry(String title, JComponent customComp){
        NavEntry nav = m_Entries.get(title);
        if(nav == null){
            nav = new NavEntry(title);
            m_Entries.put(title, nav);
        }
        nav.addEntry(customComp);
        return this;
    }

    public void update(){
        removeAll();
        setBorder( BorderFactory.createMatteBorder(0, 0, 1, 0, Color.BLACK));
        m_Entries.values().forEach(NavEntry::update);
        m_Entries.values().forEach(this::add);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        //g.fillRect(0, 0, getWidth() , getHeight());
        g.drawLine(0, getHeight(), getWidth(), getHeight());
    }

    public class NavEntry extends JComponent {

        private Collection<JComponent> m_Entries;
        private String m_Title;

        public NavEntry(String title){
            setLayout(new BorderLayout());
            m_Title = title;
            m_Entries = new ArrayList<>();
            //setPreferredSize( new Dimension(200, 50));
        }
        

        public void addEntry(String rep, Runnable action){
            JButton bttn = UtilsUI.createBttn(rep,action);
            bttn.setFont(UtilsUI.mediumFont());
            m_Entries.add(bttn);
        }
        
        public void addEntry(JComponent custom){
            m_Entries.add(custom);
        }

        public void update(){
            removeAll();
            
            setBorder(
                BorderFactory.createRaisedBevelBorder()
            );
            
            
            JLabel titleLabel = new JLabel(m_Title);
            
            titleLabel.setFont(UtilsUI.smallFont());
            JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT,3,0));
            m_Entries.forEach(actionsPanel::add);

            add(titleLabel, BorderLayout.WEST);
            add(actionsPanel, BorderLayout.CENTER);
        }
    }

    
}