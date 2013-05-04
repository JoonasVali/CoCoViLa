package ee.ioc.cs.vsle.layout.dialog;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SpringLayout;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * <b>Programm:</b> WaveGradient<br>
 * <b>Copyright:</b> 2002 Andreas Gohr, Frank Schubert<br>
 * <b>License:</b> GPL2 or higher<br>
 * <br>
 * <b>Info:</b> This JSlider uses doubles for its values
 * Edited by Joonas Vali for CoCoViLa(2013)
 */
@SuppressWarnings("serial")
public class DoubleJSlider extends JSlider{
  private final double step;
  SpringLayout layout = new SpringLayout();
  private JPanel panel = new JPanel(layout);
  private JLabel label = new JLabel();
  private JLabel defaultVal = new JLabel("[R]");
  /**
   * Constructor - initializes with 0.0,100.0,50.0
   */
  public DoubleJSlider(){
    this(0.0, 1.0, 0.0, 0.01);  	
  }

  /**
   * Constructor
   */
  public DoubleJSlider(double min, double max, final double val,double step){
    super();
    this.step=step;
    setDoubleMinimum(min);
    setDoubleMaximum(max);
    setDoubleValue(val);
    setMinorTickSpacing(1);
    setPaintTicks(true);
    label.setText(Double.toString(getDoubleValue()));
       
    defaultVal.setForeground(Color.RED);
    defaultVal.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    
    this.setPreferredSize(new Dimension(150, 30));
    panel.add(defaultVal);
    panel.add(label);    
    panel.add(this);
    
    layout.putConstraint(SpringLayout.WEST, defaultVal, 0, SpringLayout.WEST, panel);
    layout.putConstraint(SpringLayout.WEST, label, 20, SpringLayout.WEST, panel);
    layout.putConstraint(SpringLayout.WEST, this, 60, SpringLayout.WEST, panel);
    
    
    defaultVal.addMouseListener(new MouseListener(){
			@Override
      public void mouseClicked(MouseEvent e) {  }
			@Override
      public void mousePressed(MouseEvent e) {  }
			@Override
      public void mouseReleased(MouseEvent e) { 
				setDoubleValue(val); 
			}
			@Override
      public void mouseEntered(MouseEvent e) {  }
			@Override
      public void mouseExited(MouseEvent e) {  }    	
    });
    
    this.addChangeListener(new ChangeListener(){
			@Override
      public void stateChanged(ChangeEvent e) {
				label.setText(Double.toString(getDoubleValue()));
      }    	
    });
  }

  /**
   * returns Maximum in double precision
   */
  public double getDoubleMaximum() {
    return( getMaximum()*step );
  }

  /**
   * returns Minimum in double precision
   */
  public double getDoubleMinimum() {
    return( getMinimum()*step );
  }

  /**
   * returns Value in double precision
   */
  public double getDoubleValue() {
    return( getValue()*step );
  }

  /**
   * sets Maximum in double precision
   */
  public void setDoubleMaximum(double max) {
    setMaximum((int)(max/step));
  }

  /**
   * sets Minimum in double precision
   */
  public void setDoubleMinimum(double min) {
    setMinimum((int)(min/step));
  }

  /**
   * sets Value in double precision
   */
  public void setDoubleValue(double val) {
    setValue((int)(val/step));
    setToolTipText(Double.toString(val));
  } 
  
  public JPanel getComposition(){
  	return panel;
  }
}
