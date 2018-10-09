package nova;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;

public class UIImageList extends JComponent implements Controller.StartUpListener{
    
    private static final int PREVIEW_WIDTH = 200;
    private static final int PREVIEW_HEIGHT = 200;
    private ImageHandler m_Handler;
    private Controller m_Controller;
    private ListView m_ListView;

    public UIImageList(Controller controller, ImageHandler source){
        m_Controller = controller;
        m_Handler = source;
        m_ListView = new ListView(200, Collections.emptyList());
        
        /*
        m_ListView.setBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY,2),
                "Images"
            )
        );
        */
        m_ListView.setPreferredSize(new Dimension(PREVIEW_WIDTH + 20, getHeight() ));

        setLayout( new BorderLayout());
        add(m_ListView, BorderLayout.CENTER);

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
        setPreferredSize( new Dimension( PREVIEW_WIDTH + 25, getParent().getHeight()));
        getParent().validate();
    }
    

    public void updateImageList(){
        Collection<SelectableImage> images = m_Handler.getImages().stream()
            .map(UIAlgoImage::new)
            .map(SelectableImage::new)
            .collect(Collectors.toList());

        images.stream()
            .map(SelectableImage::getImageBackground)
            .forEach(this::applyMouseListener);

        m_ListView.updateList(images);
        
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

            setPreferredSize(INITIAL_SIZE);
            setSize(INITIAL_SIZE);
            bg.setSize(INITIAL_SIZE);
            pane.setSize(INITIAL_SIZE);
            controlPane.setSize(INITIAL_SIZE);
            
            
            JPanel p = new JPanel( new BorderLayout());
            p.setPreferredSize( INITIAL_SIZE);
            p.setSize(INITIAL_SIZE);
            p.setOpaque(false);
            
            JPanel p2 = new JPanel();
            p2.setOpaque(false);
            
            JCheckBox box = new JCheckBox();
            box.setOpaque(false);
            box.addActionListener( event -> {
                boolean result = m_Controller.getShuffleManager().toggleImage(m_Background.getSource());
                box.setSelected(result);
            });
            box.setSelected(m_Controller.getShuffleManager().has(m_Background.getSource()));
            
            p2.add(box);
            p.add(p2, BorderLayout.WEST);
            
            pane.add( p, 0);
            pane.add(controlPane, 1);
            pane.add(bg, 2);
            
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
            
            final JPanel p = new JPanel();
            p.setBackground( new Color(0,0,0,0));
            //p.add(checkBox);
            panel.add(p, BorderLayout.WEST);
            
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

            final JButton bttnRemove = UtilsUI.createBttn("✗", () -> {
                m_Handler.removeImage(m_Background.getSource());
                updateImageList();
            });
            
            panel.add(bttnUp);
            panel.add(bttnDown);   
            panel.add(bttnRemove);         
            return panel;
        }

        public UIAlgoImage getImageBackground(){
            return m_Background;
        }
    }
}