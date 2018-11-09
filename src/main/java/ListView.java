

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.Arrays;
import java.util.Collection;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class ListView extends JComponent {

    private int m_ElementHeight;
    private JComponent m_ContentContainer;
    private JComponent m_ContentWrapper;

    public ListView(int elementHeight, JComponent... content){
        this(elementHeight, Arrays.asList(content));
    }

    public ListView(int elementHeight, Collection<JComponent> content){
        m_ElementHeight = elementHeight;
        m_ContentContainer = initImageContainer(content);

        m_ContentWrapper = new JPanel();
        //m_ContentWrapper.setLayout(new BorderLayout());
        m_ContentWrapper.add(m_ContentContainer, BorderLayout.CENTER);

        JScrollPane pane = new JScrollPane(m_ContentWrapper);
        pane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        setLayout( new BorderLayout());
        add(pane, BorderLayout.CENTER);
        m_ContentContainer.setBackground( Color.BLACK);
    }

    private JComponent initImageContainer(JComponent container, Collection<? extends JComponent> content){
        container.removeAll();
        container.setLayout( new GridLayout(0, 1, 0, 0));
        
        for(JComponent c : content){
            container.add(c);
        }
        int width = (int)content.stream()
            .map(JComponent::getPreferredSize)
            .mapToDouble(Dimension::getWidth)
            .max().orElse( getPreferredSize().getWidth());

        Dimension d = new Dimension( width, m_ElementHeight * content.size());
        container.setPreferredSize(d);
        return container;
    }

    private JComponent initImageContainer(Collection<JComponent> content){
        return initImageContainer( new JPanel(), content);
    }

    public void updateList(Collection<? extends JComponent> content){
        initImageContainer(m_ContentContainer, content);
        int width = (int)content.stream()
            .map(JComponent::getPreferredSize)
            .mapToDouble(Dimension::getWidth)
            .max().orElse(200);
        m_ContentWrapper.setPreferredSize( new Dimension( width, m_ElementHeight * content.size()));
    }
}