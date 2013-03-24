package ee.ioc.cs.vsle.layout.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import ee.ioc.cs.vsle.layout.LayoutManager;
import ee.ioc.cs.vsle.vclass.Scheme;
import ee.joonasvali.graps.edges.BreakpointManager;
import ee.joonasvali.graps.layout.forcelayout.ForceLayoutConfiguration;

public class LayoutDialog {
	JFrame mainFrame = null;
	JButton layoutButton, applyButton, addBreakpointsButton;
	
	JTextField pullForceField;
	JTextField pushForceField;
	JTextField speedField;
	
	private boolean run = false;
	private LayoutManager manager;
	private Scheme scheme;
	private BreakpointManager bpManager;
	
	private static final Color ERROR_COLOR = new Color(255, 220, 220);
	private static final double VERY_SMALL = 0.00000000000001d;
	
	private static final String RUN = "Run";
	private static final String PAUSE = "Pause";
	
	private ForceLayoutConfiguration configuration;
	private JPanel mainPanel = new JPanel(new BorderLayout());
	private JPanel topPanel = new JPanel(new FlowLayout()); 
	private JPanel configurationPanel = new JPanel(new GridLayout(0, 2)); 
	private JPanel bottomPanel = new JPanel(new FlowLayout()); 
	
	ActionListener toggleRunAction = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {			
			manager.setObjects(scheme.getObjectList());
			manager.setConnections(scheme.getConnectionList());
			run = !run;
			if (run) {
				layoutButton.setText(PAUSE);
				applyAction.actionPerformed(null);
				manager.execute();				
			}
			else {
				layoutButton.setText(RUN);
				manager.stop();
			}
			addBreakpointsButton.setEnabled(!run);
		}
	};
	
	ActionListener addBreakpointsAction = new ActionListener(){
		@Override
    public void actionPerformed(ActionEvent e) {
			bpManager.clearBreakpoints();
			bpManager.makeBreakPoints();
			manager.applyBreakpoints();
			scheme.repaint();
    }		
	};
	
	ActionListener applyAction = new ActionListener() {		
		@Override
		public void actionPerformed(ActionEvent e) {
			manager.getLayout().addBeforeNextIteration(new Runnable(){
				@Override
        public void run() {
					try{
						configuration.setCoulombRepulseStrength(Math.max(VERY_SMALL, Double.parseDouble(pushForceField.getText())));						
						setBackground(pushForceField, Color.WHITE);		
					} catch(Exception ignore){ 
						setBackground(pushForceField, ERROR_COLOR);							
					}
					
					try{
						configuration.setStringStrength(Math.max(VERY_SMALL, Double.parseDouble(pullForceField.getText())));
						setBackground(pullForceField, Color.WHITE);		
					} catch(Exception ignore){ 
						setBackground(pullForceField, ERROR_COLOR);
					}
					
					try{
						configuration.setSleepTimeBetweenIterations(Math.max(1, Integer.parseInt(speedField.getText())));
						setBackground(speedField, Color.WHITE);		
					} catch(Exception ignore){ 
						setBackground(speedField, ERROR_COLOR);
					}
        }				
			});
		}
	};

	private void setBackground(final JTextField field, final Color color){
		try {
      SwingUtilities.invokeAndWait(new Runnable(){
      	public void run() { field.setBackground(color); };           	
      });
    }
    catch (InvocationTargetException e) {  }
    catch (InterruptedException e) {  }	
	}
	
	public LayoutDialog(Scheme scheme, final LayoutManager manager) {		
		this.manager = manager;		
		this.bpManager = new BreakpointManager(manager.getGraph());
		this.configuration = manager.getConfiguration();
		this.scheme = scheme;
		initFrame(manager);				
		initComponents();		
		
		layoutButton.addActionListener(toggleRunAction);
		applyButton.addActionListener(applyAction);
		addBreakpointsButton.addActionListener(addBreakpointsAction);
		
		mainFrame.setLocationRelativeTo(null);
		mainFrame.getContentPane().add(mainPanel);
		
		mainPanel.add(topPanel, BorderLayout.NORTH);
		mainPanel.add(configurationPanel, BorderLayout.CENTER);
		mainPanel.add(bottomPanel, BorderLayout.SOUTH);
		
		topPanel.add(new JLabel("Force Layout"));
		
		configurationPanel.add(new JLabel("Hooke's pull constant:"));
		configurationPanel.add(pullForceField);
		configurationPanel.add(new JLabel("Coulomb push force constant:"));
		configurationPanel.add(pushForceField);
		configurationPanel.add(new JLabel("Animation sleep time (speed):"));
		configurationPanel.add(speedField);
		
		bottomPanel.add(layoutButton);
		bottomPanel.add(applyButton);
		bottomPanel.add(addBreakpointsButton);	
		mainFrame.setVisible(true);
	}

	private void initFrame(final LayoutManager manager) {
	  mainFrame = new JFrame("Apply Layout");
		mainFrame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				if(run){
					manager.stop();
					run = false;
				}
			}
		});
		mainFrame.setSize(new Dimension(400, 200));
		mainFrame.setAlwaysOnTop(true);
  }

	private void initComponents() {
	  layoutButton = new JButton(RUN);
	  applyButton = new JButton("Apply");
	  addBreakpointsButton = new JButton("Add breakpoints");
	  pullForceField = new JTextField(Double.toString(configuration.getStringStrength()));
	  pushForceField = new JTextField(Double.toString(configuration.getCoulombRepulseStrength()));
	  speedField = new JTextField(Integer.toString(configuration.getSleepTimeBetweenIterations()));
  }
}
