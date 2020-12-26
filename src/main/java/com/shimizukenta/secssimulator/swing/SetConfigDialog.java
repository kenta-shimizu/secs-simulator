package com.shimizukenta.secssimulator.swing;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

public class SetConfigDialog extends AbstractSwingDialog {
	
	private static final long serialVersionUID = 3639672355647254120L;
	
	private final ButtonGroup protocolGroup;
	private final JRadioButton hsmsSsPassiveRadio;
	private final JRadioButton hsmsSsActiveRadio;
	private final JRadioButton secs1OnTcpIpRadio;
	private final JRadioButton secs1OnTcpIpRecvRadio;
	
	private final JTextField deviceIdText;
	
	private final ButtonGroup hostEquipGroup;
	private final JRadioButton hostRadio;
	private final JRadioButton equipRadio;
	
	private final JCheckBox masterMode;
	
	private final JCheckBox autoReply;
	private final JCheckBox autoReplyS9Fy;
	private final JCheckBox autoReplySxF0;
	
	private final JButton okButton;
	
	
	public SetConfigDialog(Frame owner, SwingSecsSimulator simm) {
		super(simm, owner, "Set Config", true);
		
		this.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
		
		this.hsmsSsPassiveRadio = new JRadioButton("HSMS-SS-Passive", true);
		this.hsmsSsActiveRadio = new JRadioButton("HSMS-SS-Active", false);
		this.secs1OnTcpIpRadio = new JRadioButton("SECS-I-on-TCP/IP", false);
		this.secs1OnTcpIpRecvRadio = new JRadioButton("SECS-I-on-TCP/IP-Receiver", false);
		this.protocolGroup = new ButtonGroup();
		this.protocolGroup.add(this.hsmsSsPassiveRadio);
		this.protocolGroup.add(this.hsmsSsActiveRadio);
		this.protocolGroup.add(this.secs1OnTcpIpRadio);
		this.protocolGroup.add(this.secs1OnTcpIpRecvRadio);
		
		this.deviceIdText = new JTextField("10", 5);
		this.deviceIdText.setHorizontalAlignment(JTextField.RIGHT);
		
		this.equipRadio = new JRadioButton("Equip", true);
		this.hostRadio = new JRadioButton("Host", false);
		this.hostEquipGroup = new ButtonGroup();
		this.hostEquipGroup.add(this.equipRadio);
		this.hostEquipGroup.add(this.hostRadio);
		
		this.masterMode = new JCheckBox("Master-mode (SECS-I)", true);
		
		
		this.autoReply = new JCheckBox("Auto-reply", true);
		this.autoReplyS9Fy = new JCheckBox("Auto-reply-S9Fy", false);
		this.autoReplySxF0 = new JCheckBox("Auto-reply-SxF0", false);
		
		this.okButton = new JButton("OK");
		this.okButton.addActionListener(ev -> {
			setConfig();
			this.setVisible(false);
		});
		
		this.setLayout(defaultBorderLayout());
		
		{
			final JTabbedPane tabbedpane = new JTabbedPane();
			
			{
				List<Component> comps = new ArrayList<>();
				
				{
					JPanel p = gridPanel(4, 1);
					p.setBorder(defaultTitledBorder("Protocol"));
					p.add(this.hsmsSsPassiveRadio);
					p.add(this.hsmsSsActiveRadio);
					p.add(this.secs1OnTcpIpRadio);
					p.add(this.secs1OnTcpIpRecvRadio);
					comps.add(p);
				}
				{
					//TODO
					//socket-address
				}
				{
					JPanel p = flowPanel(FlowLayout.LEFT);
					p.add(new JLabel("Device-ID: "));
					p.add(this.deviceIdText);
					comps.add(p);
				}
				{
					JPanel p = gridPanel(2, 1);
					p.setBorder(defaultTitledBorder("Equip/Host"));
					p.add(this.equipRadio);
					p.add(this.hostRadio);
					comps.add(p);
				}
				{
					comps.add(this.masterMode);
				}
				
				Component p = compactStackPanel(comps, BorderLayout.NORTH, BorderLayout.NORTH);
				tabbedpane.add("General", p);
			}
			{
				List<Component> comps = new ArrayList<>();
				
				{
					//TOOD
				}
				{
					//TODO
				}
				
				Component c = compactStackPanel(comps, BorderLayout.NORTH, BorderLayout.WEST);
				tabbedpane.add("Timer", c);
			}
			{
				List<Component> comps = Arrays.asList(
						this.autoReply,
						this.autoReplyS9Fy,
						this.autoReplySxF0
						);
				
				Component c = compactStackPanel(comps, BorderLayout.NORTH, BorderLayout.WEST);
				tabbedpane.add("Other", c);
			}
			
			this.add(tabbedpane, BorderLayout.CENTER);
		}
		{
			JPanel p = flowPanel(FlowLayout.CENTER);
			
			p.add(this.okButton);
			
			this.add(p, BorderLayout.SOUTH);
		}
		
		updateView();
		
		this.pack();
	}
	
	@Override
	public void setVisible(boolean aFlag) {
		if ( aFlag ) {
			this.setLocationRelativeTo(this.getOwner());
		}
		super.setVisible(aFlag);
	}
	
	private void updateView() {
		
		//TODO
	}
	
	private void setConfig() {
		
		//TODO
	}
}
