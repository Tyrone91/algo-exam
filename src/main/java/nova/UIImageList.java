package nova;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JLayer;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;

public class UIImageList extends JComponent implements Controller.StartUpListener{
    
    private static final int PREVIEW_WIDTH = 200;
    private static final int PREVIEW_HEIGHT = 200;
    private ImageHandler m_Handler;
    private Controller m_Controller;

    public UIImageList(Controller controller, ImageHandler source){
        m_Controller = controller;
        m_Handler = source;
        
        setPreferredSize( new Dimension( PREVIEW_WIDTH , PREVIEW_HEIGHT));
        controller.addStartUpListener(this);
        setLayout( new BoxLayout(this,BoxLayout.Y_AXIS));
        
        updateImageList();
        source.addNewImageListener( (img, handler ) -> {
            updateImageList();
        });
    }
    
    private void applyMouseListener(UIAlgoImage image){
        image.addMouseListener( new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                m_Controller.showImage(image.getSource());
            }
        });
    }
    
    @Override
    public void onStartUp(Controller controller) {
        setSize( new Dimension( PREVIEW_WIDTH , getParent().getHeight()));
        setPreferredSize( new Dimension( PREVIEW_WIDTH , getParent().getHeight()));
        getParent().validate();
    }
    

    public void updateImageList(){
        final Dimension previewSize = new Dimension(PREVIEW_WIDTH, PREVIEW_HEIGHT);
        removeAll();
        final GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 1;
        c.gridheight = 1;
        c.anchor = GridBagConstraints.NORTH;
        c.fill = GridBagConstraints.HORIZONTAL;
        //c.insets.bottom = 5;
        //c.insets.left = 5;
        //c.insets.right = 5;
        c.weightx = 1;
        c.weighty = 0;
        m_Handler.getImages().stream()
            .map(UIAlgoImage::new)
            .map(SelectableImage::new)
            .forEach( img -> {
                //img.setSize(previewSize);
                img.setPreferredSize(previewSize);
                applyMouseListener(img.m_Background);
                add(img);
                c.gridy++;
                System.out.println("adding img at: " + c.gridy);
            });
        c.fill = GridBagConstraints.BOTH;   
        c.weighty = 1;
        System.out.println("adding placeholder");
        //add( new  JLabel("t"), c);
        //setSize( new Dimension( PREVIEW_WIDTH + 200, m_Handler.size() * PREVIEW_HEIGHT));
        setPreferredSize( new Dimension( PREVIEW_WIDTH + 50, m_Handler.size() * PREVIEW_HEIGHT));
        validate();
        repaint();
    }

    private class SelectableImage extends JComponent {

        private UIAlgoImage m_Background;

        public SelectableImage(UIAlgoImage bg){
            setLayout( new BorderLayout());
            final Dimension INITIAL_SIZE = new Dimension(200,200);
            final JComponent controlPane = controlPane();
            final JLayeredPane pane = new JLayeredPane();

            m_Background = bg;

            setSize(INITIAL_SIZE);
            bg.setSize(INITIAL_SIZE);
            pane.setSize(INITIAL_SIZE);
            controlPane.setSize(INITIAL_SIZE);
            
            pane.add(controlPane, 100);
            pane.add(bg, JLayeredPane.DEFAULT_LAYER);
            pane.setBackground(Color.red);

            System.out.println("Constructor call");
            setBackground(Color.yellow);
            add(pane, BorderLayout.CENTER);
            validate();
            addComponentListener( new ComponentAdapter() {
                @Override
                public void componentResized(ComponentEvent e) {
                    //pane.setSize( SelectableImage.this.getSize() );
                    //controlPane.setSize( SelectableImage.this.getSize());
                    //bg.setSize( SelectableImage.this.getSize());
                }
            });
        }

        private JComponent controlPane(){
            final JPanel panel = new JPanel(new BorderLayout());
            panel.add(positionControls(), BorderLayout.EAST );
            panel.setBackground(new Color(0, 0, 0, 0));
            //panel.setOpaque(false);
            return panel;
        }

        private JComponent positionControls(){
            final JPanel panel = new JPanel();
            panel.setOpaque(false);
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            final JButton bttnUp = UtilsUI.createBttn("⇧", () -> {
                m_Handler.moveImageUp(m_Background.getSource());
                updateImageList();
            });
            
            final JButton bttnDown = UtilsUI.createBttn("⇩", () -> {
                m_Handler.moveImageDown(m_Background.getSource());
                updateImageList();
            });
            panel.add(bttnUp);
            panel.add(bttnDown);            
            return panel;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.setColor(Color.CYAN);
            g.drawRect(0, 0, getWidth(), getHeight());
        }
    }
}