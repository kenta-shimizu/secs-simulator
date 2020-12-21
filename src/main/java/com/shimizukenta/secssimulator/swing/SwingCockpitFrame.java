package com.shimizukenta.secssimulator.swing;

import java.beans.PropertyVetoException;

import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;

import com.shimizukenta.secssimulator.SecsSimulatorLog;

public class SwingCockpitFrame extends JFrame {
	
	private static final long serialVersionUID = 4147107959994828227L;
	
	private final JDesktopPane desktopPane = new JDesktopPane();
	private final JInternalFrame viewerFrame = new JInternalFrame("Viewer");
	
	private final SwingSecsSimulator simm;
	
	public SwingCockpitFrame(SwingSecsSimulator simm) {
		super("Swing SECS Simulator");
		
		this.simm = simm;
		
		this.setSize(800, 450);
		
		this.add(desktopPane);
		
		this.viewerFrame.setBounds(5, 5, 500, 400);
		this.viewerFrame.setMaximizable(true);
		this.viewerFrame.setIconifiable(true);
		this.viewerFrame.setClosable(false);
		this.viewerFrame.setResizable(true);
		
		this.desktopPane.add(viewerFrame);
		
		this.viewerFrame.setVisible(true);
		
		this.setLocationRelativeTo(null);
	}
	
	@Override
	public void dispose() {
		
		//TOOD
		
		super.dispose();
	}
	
	public void putLog(SecsSimulatorLog log) {
		
		//TODO
	}
	
}
