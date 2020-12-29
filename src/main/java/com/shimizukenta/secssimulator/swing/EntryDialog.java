package com.shimizukenta.secssimulator.swing;

import java.awt.Frame;

import javax.swing.JButton;
import javax.swing.JDialog;

public class EntryDialog extends AbstractSwingDialog {
	
	private static final long serialVersionUID = 5613631321377387725L;
	
	private final JButton toLoadConfig;
	private final JButton toSetConfig;
	
	public EntryDialog(Frame owner, SwingSecsSimulator simm) {
		super(simm, owner, "Welcome", true);
		
		this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		
		this.setLayout(defaultGridLayout(2, 1));
		
		this.toLoadConfig = new JButton("Load config");
		this.toLoadConfig.addActionListener(ev -> {
			boolean f = simulator().showLoadConfigDialog();
			if ( f ) {
				setVisible(false);
			}
		});
		
		this.toSetConfig = new JButton("Set config");
		this.toSetConfig.addActionListener(ev -> {
			boolean f = simulator().showSetConfigDialog();
			if ( f ) {
				setVisible(false);
			}
		});
		
		this.add(this.toLoadConfig);
		this.add(this.toSetConfig);
	}
	
	@Override
	public void setVisible(boolean aFlag) {
		if ( aFlag ) {
			this.setSize(
					(this.getOwner().getWidth()  * 25 / 100),
					(this.getOwner().getHeight() * 25 / 100)
					);
			
			this.setLocationRelativeTo(this.getOwner());
		}
		super.setVisible(aFlag);
	}
	
}
