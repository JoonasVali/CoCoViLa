package ee.ioc.cs.vsle.layout.dialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;

import ee.ioc.cs.vsle.layout.LayoutManager;
import ee.ioc.cs.vsle.vclass.Scheme;

public class LayoutDialog {
	JFrame mainFrame = null;
	JButton button;
	private boolean run = false;
	private LayoutManager manager;
	private Scheme scheme;
	private static final String RUN = "Apply layout";
	private static final String STOP = "Stop";

	ActionListener action = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {			
			manager.setObjects(scheme.getObjectList());
			manager.setConnections(scheme.getConnectionList());
			run = !run;
			if (run) {
				button.setText(STOP);
				manager.execute();
			}
			else {
				button.setText(RUN);
				manager.stop();
			}
		}
	};

	public LayoutDialog(Scheme scheme, final LayoutManager manager) {
		this.manager = manager;
		this.scheme = scheme;
		mainFrame = new JFrame("Apply Layout");
		mainFrame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				if(run){
					manager.stop();
					run = false;
				}
			}
		});
		mainFrame.setAlwaysOnTop(true);		
		button = new JButton(RUN);
		button.addActionListener(action);
		mainFrame.setLocationRelativeTo(null);
		mainFrame.getContentPane().add(button);
		mainFrame.pack();
		mainFrame.setVisible(true);
	}
}
