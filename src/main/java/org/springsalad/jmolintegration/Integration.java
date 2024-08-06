package org.springsalad.jmolintegration;

import javajs.util.M3;
import org.jmol.adapter.smarter.SmarterJmolAdapter;
import org.jmol.api.JmolViewer;
import org.jmol.viewer.TransformManager;
import org.jmol.viewer.Viewer;
import org.jogamp.vecmath.Matrix3f;
import org.springsalad.langevinsetup.RotateUpdateListener;
import org.springsalad.langevinsetup.RotationUpdateEvent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * A example of integrating the Jmol viewer into a CDK application.
 *
 * <p>I compiled/ran this code directly in the examples directory by doing:
 * <pre>
 * javac -classpath ../Jmol.jar CDKIntegration.java
 * java -cp .:../Jmol.jar:../../../CDK/cdk/dist/jar/cdk-all.jar CDKIntegration
 * </pre>
 *
 * @author Miguel <mth@mth.com>
 * @author Egon <egonw@jmol.org>
 */
public class Integration implements RotateUpdateListener{
    final static String strScript = "select *; spacefill on;";
    JmolViewer vwr;
    private ActionListener dp3dListener;
    private Matrix3f m3 = new Matrix3f();
    JFrame frame;
    
    public static void main(String[] argv) {
        String filename = "C:\\Users\\jmasison\\Desktop\\pdbfiles\\test\\nck.pdb";
        //String filename = "C:\\Users\\jmasison\\Desktop\\pdbfiles\\5gh9.cif";
        //String filename = "C:\\Users\\jmasison\\Desktop\\pdbfiles\\5gh9.cif";
        
        JFrame frame = new JFrame("Jmol Atom View");
        //frame.addWindowListener(new ApplicationCloser());
        Container contentPane = frame.getContentPane();
        JmolPanel jmolPanel = new JmolPanel();
        contentPane.add(jmolPanel);
        frame.setSize(700, 700);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        
        JmolViewer viewer = jmolPanel.getViewer();
        viewer.openFile(filename);
        viewer.evalString(strScript);
    }
    
    public Integration(String filename) {      
      frame = new JFrame("Jmol Atom View");
      Container contentPane = frame.getContentPane();
      JmolPanel jmolPanel = new JmolPanel();
      contentPane.add(jmolPanel);
      frame.setSize(600, 600);
      frame.setVisible(true);
      frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
      
      vwr = jmolPanel.getViewer();
      vwr.openFile(filename);
      vwr.evalString(strScript);
  }
   
  public JmolViewer getViewer(){
    return vwr;
  }
  
  public TransformManager getTM(){
  	return ((Viewer) vwr).tm;
  }
  
  public void closeWindow(){
	  frame.dispose();
  }

  // inner calss -------------------------------------------------
    @SuppressWarnings("serial")
	static class JmolPanel extends JPanel {
        JmolViewer viewer;
        SmarterJmolAdapter adapter;
        
        JmolPanel() {
            // use CDK IO
            adapter = new SmarterJmolAdapter();
            viewer = JmolViewer.allocateViewer(this, adapter);
        }
    
        public JmolViewer getViewer() {
            return viewer;
        }
        
        public TransformManager getTM(){
        	return ((Viewer) viewer).tm;
        }
    
        final Dimension currentSize = new Dimension();
        final Rectangle rectClip = new Rectangle();
        
        @Override
        @SuppressWarnings("deprecation")
        public void paint(Graphics g) {
            this.getSize(currentSize);
            g.getClipBounds(rectClip);
            viewer.setPercentVdwAtom(20);
            ((Viewer) viewer).setShowAxes(true);
            
            viewer.renderScreenImage(g, currentSize, rectClip);
        }
    }

	@Override
	public void rotationOccurred(RotationUpdateEvent event) {
		Matrix3f m3 = ((RotationUpdateEvent) event).getM3();
    	M3 mNew = M3.newA9(new float[]{
    			m3.m00, m3.m01, m3.m02,
    			m3.m10, m3.m11, m3.m12,
    			m3.m20, m3.m21, m3.m22         			
    	});
    	this.getTM().matrixRotate.setM3(mNew);
    	vwr.refresh(2, "");
		
		if(event.notifyPanel()){
			notifyListeners();
		}
	}
	
	private void notifyListeners(){
		RotationUpdateEvent e = new RotationUpdateEvent(this.m3, false);
		this.dp3dListener.actionPerformed(e);
	}

	
	public void setdp3dListener(ActionListener al){
		this.dp3dListener = al;
	}

}