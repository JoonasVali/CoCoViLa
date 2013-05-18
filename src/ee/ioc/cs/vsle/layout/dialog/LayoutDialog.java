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
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import ee.ioc.cs.vsle.layout.LayoutManager;
import ee.ioc.cs.vsle.vclass.Scheme;
import ee.joonasvali.graps.edges.BreakpointManager;
import ee.joonasvali.graps.edges.CornerPathCalculator;
import ee.joonasvali.graps.layout.forcelayout.ForceLayoutConfiguration;
import ee.joonasvali.graps.layout.forcelayout.UpdateListener;

public class LayoutDialog {
	JFrame mainFrame = null;
	JButton layoutButton, applyButton, addBreakpointsButton;

	DoubleJSlider pullForceField;
	DoubleJSlider pushForceField;
	DoubleJSlider speedField;
	DoubleJSlider dampingField;
	DoubleJSlider centralPullField;
	JCheckBox redraw;
	JCheckBox randomize;

	private boolean run = false;
	private LayoutManager manager;
	private Scheme scheme;
	private BreakpointManager bpManager;
	private JProgressBar progressBar = new JProgressBar();

	private static final String RUN = "Run";
	private static final String PAUSE = "Pause";

	private ForceLayoutConfiguration configuration;
	private JPanel mainPanel = new JPanel(new BorderLayout());
	private JPanel topPanel = new JPanel(new FlowLayout());
	private JPanel configurationPanel = new JPanel(new GridLayout(0, 2));
	private JPanel bottomPanel = new JPanel(new FlowLayout());

	private volatile long biggestVal = Long.MIN_VALUE;

	private long t50, t75, t90, t95, t97;
	private long time;

	UpdateListener volatilityListener = new UpdateListener() {
		@Override
		public void update(final double volatility) {
			biggestVal = (long) Math.max(biggestVal, volatility);
			try {
				SwingUtilities.invokeAndWait(new Runnable() {
					@Override
					public void run() {
						int percent = (int) (100.0 * ((double) (biggestVal - volatility) / (double) biggestVal));

						if (t50 == 0 && percent >= 50) {
							t50 = System.currentTimeMillis() - time;
							System.out.println("50% = " + t50);
						}
						if (t75 == 0 && percent >= 75) {
							t75 = System.currentTimeMillis() - time;
							System.out.println("75% = " + t75);
						}
						if (t90 == 0 && percent >= 90) {
							t90 = System.currentTimeMillis() - time;
							System.out.println("90% = " + t90);
						}
						if (t95 == 0 && percent >= 95) {
							t95 = System.currentTimeMillis() - time;
							System.out.println("95% = " + t95);
						}
						if (t97 == 0 && percent >= 97) {
							t97 = System.currentTimeMillis() - time;
							System.out.println("97% = " + t97);
						}

						progressBar.setValue(percent);
						progressBar.setString(((long) volatility) + " / " + biggestVal
						    + " ( " + Math.max(0, percent) + "% )");
					}
				});
			}
			catch (Exception e) {
			}
		}
	};

	ActionListener toggleRunAction = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			manager.setObjects(scheme.getObjectList());
			manager.setConnections(scheme.getConnectionList());
			run = !run;
			if (run) {
				t50 = t75 = t90 = t95 = t97 = 0;
				time = System.currentTimeMillis();
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

	ActionListener addBreakpointsAction = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			manager.bind(scheme.getObjectList(), scheme.getConnectionList());
			manager.update(0);
			bpManager.clearBreakpoints();
			bpManager.makeBreakPoints();
			manager.applyBreakpoints();
			scheme.repaint();
		}
	};

	ActionListener applyAction = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			manager.getLayout().addBeforeNextIteration(new Runnable() {
				@Override
				public void run() {
					configuration.setStringStrength(pullForceField.getDoubleValue());
					configuration.setCoulombRepulseStrength(pushForceField
					    .getDoubleValue());
					configuration.setSleepTimeBetweenIterations((int) speedField
					    .getDoubleValue());
					configuration.setDamping(dampingField.getDoubleValue());
					configuration.setRandomizeGraph(randomize.isSelected());
					configuration.setCenterForcePullStrength(centralPullField
					    .getDoubleValue());
					manager.setDrawGraph(redraw.isSelected());
					if (!redraw.isSelected()) {
						configuration.setSleepTimeBetweenIterations(0);
					}
				}
			});
		}
	};

	public LayoutDialog(Scheme scheme, final LayoutManager manager) {
		System.out.println("TOTAL OBJECTS: " + scheme.getObjectList().size());
		this.bpManager = new BreakpointManager(manager.getGraph(),
		    CornerPathCalculator.getFactory());
		this.manager = manager;
		this.configuration = (ForceLayoutConfiguration) manager.getConfiguration();
		this.scheme = scheme;
		initFrame(manager);
		initComponents();

		manager.addVolatilityListener(volatilityListener);
		layoutButton.addActionListener(toggleRunAction);
		applyButton.addActionListener(applyAction);
		addBreakpointsButton.addActionListener(addBreakpointsAction);

		mainFrame.setLocationRelativeTo(null);
		mainFrame.getContentPane().add(mainPanel);

		JPanel sidepanel = new JPanel();
		sidepanel.setSize(50, 50);

		mainPanel.add(sidepanel, BorderLayout.EAST);
		mainPanel.add(sidepanel, BorderLayout.WEST);
		mainPanel.add(topPanel, BorderLayout.NORTH);
		mainPanel.add(configurationPanel, BorderLayout.CENTER);
		mainPanel.add(bottomPanel, BorderLayout.SOUTH);

		topPanel.add(new JLabel("Force Layout"));

		configurationPanel.add(new JLabel("Pull force constant:"));
		configurationPanel.add(pullForceField.getComposition());
		configurationPanel.add(new JLabel("Push force constant:"));
		configurationPanel.add(pushForceField.getComposition());
		configurationPanel.add(new JLabel("Central pull:"));
		configurationPanel.add(centralPullField.getComposition());
		configurationPanel.add(new JLabel("Animation sleep time (speed):"));
		configurationPanel.add(speedField.getComposition());
		configurationPanel.add(new JLabel("Cooldown rate:"));
		configurationPanel.add(dampingField.getComposition());
		configurationPanel.add(progressBar);
		configurationPanel.add(redraw);
		configurationPanel.add(new JLabel(""));
		configurationPanel.add(randomize);

		bottomPanel.add(layoutButton);
		bottomPanel.add(applyButton);
		bottomPanel.add(addBreakpointsButton);
		mainFrame.setVisible(true);
	}

	private void initFrame(final LayoutManager manager) {
		mainFrame = new JFrame("Apply Layout");
		mainFrame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				if (run) {
					manager.stop();
					run = false;
				}
			}
		});
		mainFrame.setSize(new Dimension(500, 300));
		mainFrame.setAlwaysOnTop(true);
	}

	private void initComponents() {

		progressBar.setStringPainted(true);
		redraw = new JCheckBox("Draw graph (lower performance)");
		redraw.setSelected(manager.isDrawGraph());
		randomize = new JCheckBox("Randomize graph on run");
		randomize.setSelected(((ForceLayoutConfiguration) (manager.getConfiguration())).isRandomizeGraph());
		layoutButton = new JButton(RUN);
		applyButton = new JButton("Apply");
		addBreakpointsButton = new JButton("Add breakpoints");
		pullForceField = new DoubleJSlider(0.005, 0.1,
		    configuration.getStringStrength(), 0.005);
		pushForceField = new DoubleJSlider(5, 300,
		    configuration.getCoulombRepulseStrength(), 5);
		dampingField = new DoubleJSlider(0.05, 0.95, configuration.getDamping(),
		    0.05);
		speedField = new DoubleJSlider(0, 1000,
		    configuration.getSleepTimeBetweenIterations(), 10);
		centralPullField = new DoubleJSlider(0.00, 0.05,
		    configuration.getCenterForcePullStrength(), 0.001);

	}
}
