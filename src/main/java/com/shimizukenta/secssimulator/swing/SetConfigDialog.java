package com.shimizukenta.secssimulator.swing;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.util.ArrayList;
import java.util.List;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.function.Consumer;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import com.shimizukenta.secs.hsmsss.HsmsSsCommunicatorConfig;
import com.shimizukenta.secs.secs1ontcpip.Secs1OnTcpIpCommunicatorConfig;
import com.shimizukenta.secssimulator.SecsSimulatorException;
import com.shimizukenta.secssimulator.SecsSimulatorProtocol;

public class SetConfigDialog extends AbstractSwingDialog {
	
	private static final long serialVersionUID = 3639672355647254120L;
	
	private static class NumberTextField extends JTextField {
		
		private static final long serialVersionUID = -7951884917128959768L;

		public NumberTextField(String text, int columns) {
			super(text, columns);
			this.setHorizontalAlignment(JTextField.RIGHT);
		}
		
		public void setValue(int v) {
			this.setText(String.valueOf(v));
		}
		
		public void setValue(float v) {
			this.setText(String.valueOf(v));
		}
		
		public OptionalInt optionalInt() {
			String s = this.getText();
			if ( (s != null) && (! s.isEmpty()) ) {
				if (! s.contains(".")) {
					try {
						int v = Integer.parseInt(s);
						return OptionalInt.of(v);
					}
					catch ( NumberFormatException giveup ) {
					}
				}
			}
			return OptionalInt.empty();
		}
		
		public OptionalDouble optionalDouble() {
			String s = this.getText();
			if ( (s != null) && (! s.isEmpty()) ) {
				try {
					double v = Double.parseDouble(s);
					return OptionalDouble.of(v);
				}
				catch ( NumberFormatException giveup ) {
				}
			}
			return OptionalDouble.empty();
		}
	};
	
	private final ButtonGroup protocolGroup;
	private final JRadioButton hsmsSsPassiveRadio;
	private final JRadioButton hsmsSsActiveRadio;
	private final JRadioButton secs1OnTcpIpRadio;
	private final JRadioButton secs1OnTcpIpRecvRadio;
	
	private final NumberTextField deviceIdText;
	
	private final ButtonGroup hostEquipGroup;
	private final JRadioButton hostRadio;
	private final JRadioButton equipRadio;
	
	private final JCheckBox masterMode;
	
	private final NumberTextField t1Text;
	private final NumberTextField t2Text;
	private final NumberTextField t3Text;
	private final NumberTextField t4Text;
	private final NumberTextField t5Text;
	private final NumberTextField t6Text;
	private final NumberTextField t7Text;
	private final NumberTextField t8Text;
	private final NumberTextField retryText;
	private final JCheckBox linktest;
	private final NumberTextField linktestText;

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
		
		this.deviceIdText = new NumberTextField("10", 5);
		
		this.equipRadio = new JRadioButton("Equip", true);
		this.hostRadio = new JRadioButton("Host", false);
		this.hostEquipGroup = new ButtonGroup();
		this.hostEquipGroup.add(this.equipRadio);
		this.hostEquipGroup.add(this.hostRadio);
		
		this.masterMode = new JCheckBox("Master-mode (SECS-I)", true);
		
		this.t1Text = new NumberTextField("1", 4);
		this.t2Text = new NumberTextField("1", 4);
		this.t3Text = new NumberTextField("1", 4);
		this.t4Text = new NumberTextField("1", 4);
		this.t5Text = new NumberTextField("1", 4);
		this.t6Text = new NumberTextField("1", 4);
		this.t7Text = new NumberTextField("1", 4);
		this.t8Text = new NumberTextField("1", 4);
		this.retryText = new NumberTextField("3", 2);
		this.linktest = new JCheckBox();
		this.linktestText = new NumberTextField("1", 4);
		
		this.autoReply = new JCheckBox("Auto-reply", true);
		this.autoReplyS9Fy = new JCheckBox("Auto-reply-S9Fy", false);
		this.autoReplySxF0 = new JCheckBox("Auto-reply-SxF0", false);
		
		this.okButton = new JButton("OK");
		this.okButton.addActionListener(ev -> {
			synchronized ( this ) {
				setConfig();
				this.setVisible(false);
			}
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
					
					JPanel p = borderPanel();
					p.setBorder(defaultTitledBorder("Timeout"));
					
					List<Component> ll = new ArrayList<>();
					
					{
						JPanel pp = gridPanel(8, 1);
						
						for ( int i = 1; i <= 8; ++i ) {
							JLabel l = new JLabel("T" + i + ":");
							l.setHorizontalAlignment(JLabel.LEFT);
							l.setVerticalAlignment(JLabel.CENTER);
							
							pp.add(l);
						}
						
						ll.add(pp);
					}
					{
						JPanel pp = gridPanel(8, 1);
						
						pp.add(t1Text);
						pp.add(t2Text);
						pp.add(t3Text);
						pp.add(t4Text);
						pp.add(t5Text);
						pp.add(t6Text);
						pp.add(t7Text);
						pp.add(t8Text);
						
						ll.add(pp);
					}
					
					Component cc = compactStackPanel(ll, BorderLayout.WEST, BorderLayout.WEST);
					p.add(cc, BorderLayout.CENTER);
					
					comps.add(p);
				}
				{
					JPanel p = flowPanel(FlowLayout.LEFT);
					{
						JLabel l = new JLabel("Retry (SECS-I): ");
						l.setHorizontalAlignment(JLabel.RIGHT);
						p.add(l);
					}
					
					p.add(this.retryText);
					
					comps.add(p);
				}
				{
					//TODO
					//linktest
				}
				
				Component c = compactStackPanel(comps, BorderLayout.NORTH, BorderLayout.WEST);
				tabbedpane.add("Timer", c);
			}
			{
				List<Component> comps = new ArrayList<>();
				
				comps.add(this.autoReply);
				comps.add(this.autoReplyS9Fy);
				comps.add(this.autoReplySxF0);
				
				Component c = compactStackPanel(comps, BorderLayout.NORTH, BorderLayout.NORTH);
				tabbedpane.add("Other", c);
			}
			
			this.add(tabbedpane, BorderLayout.CENTER);
		}
		{
			JPanel p = flowPanel(FlowLayout.CENTER);
			
			p.add(this.okButton);
			
			this.add(p, BorderLayout.SOUTH);
		}
	}
	
	@Override
	public void setVisible(boolean aFlag) {
		if ( aFlag ) {
			updateView();
			
			this.setSize(
					(this.getOwner().getWidth()  * 50 / 100),
					(this.getOwner().getHeight() * 90 / 100)
					);
			
			this.setLocationRelativeTo(this.getOwner());
		}
		
		super.setVisible(aFlag);
	}
	
	private void updateView() {
		
		final HsmsSsCommunicatorConfig hsmsSs = config().hsmsSsCommunicatorConfig();
		final Secs1OnTcpIpCommunicatorConfig secs1 = config().secs1OnTcpIpCommunicatorConfig();
		
		switch ( config().protocol().get()) {
		case HSMS_SS_PASSIVE: {
			this.hsmsSsPassiveRadio.setSelected(true);
			break;
		}
		case HSMS_SS_ACTIVE: {
			this.hsmsSsActiveRadio.setSelected(true);
			break;
		}
		case SECS1_ON_TCP_IP: {
			this.secs1OnTcpIpRadio.setSelected(true);
			break;
		}
		case SECS1_ON_TCP_IP_RECEIVER: {
			this.secs1OnTcpIpRecvRadio.setSelected(true);
			break;
		}
		default: {
			/* Nothing */
		}
		}
		
		//TODO
		//socketaddress
		
		this.deviceIdText.setValue(hsmsSs.deviceId().intValue());
		
		if ( hsmsSs.isEquip().booleanValue() ) {
			this.equipRadio.setSelected(true);
		} else {
			this.hostRadio.setSelected(true);
		}
		
		this.masterMode.setSelected(secs1.isMaster().booleanValue());
		
		this.t1Text.setValue(hsmsSs.timeout().t1().floatValue());
		this.t2Text.setValue(hsmsSs.timeout().t2().floatValue());
		this.t3Text.setValue(hsmsSs.timeout().t3().floatValue());
		this.t4Text.setValue(hsmsSs.timeout().t4().floatValue());
		this.t5Text.setValue(hsmsSs.timeout().t5().floatValue());
		this.t6Text.setValue(hsmsSs.timeout().t6().floatValue());
		this.t7Text.setValue(hsmsSs.timeout().t7().floatValue());
		this.t8Text.setValue(hsmsSs.timeout().t8().floatValue());
		
		this.retryText.setValue(secs1.retry().intValue());
		
		//TODO
		//linktest
		
		
		this.autoReply.setSelected(config().autoReply().booleanValue());
		this.autoReplyS9Fy.setSelected(config().autoReplyS9Fy().booleanValue());
		this.autoReplySxF0.setSelected(config().autoReplySxF0().booleanValue());
	}
	
	private void setConfig() {
		if ( this.hsmsSsPassiveRadio.isSelected() ) {
			
			config().protocol(SecsSimulatorProtocol.HSMS_SS_PASSIVE);
			
		} else if ( this.hsmsSsActiveRadio.isSelected() ) {
			
			config().protocol(SecsSimulatorProtocol.HSMS_SS_ACTIVE);
			
		} else if ( this.secs1OnTcpIpRadio.isSelected() ) {
			
			config().protocol(SecsSimulatorProtocol.SECS1_ON_TCP_IP);
			
		} else if ( this.secs1OnTcpIpRecvRadio.isSelected() ) {
			
			config().protocol(SecsSimulatorProtocol.SECS1_ON_TCP_IP_RECEIVER);
		}
		
		//TODO
		//socketaddress
		
		try {
			
			//TODO
			int v = this.deviceIdText.optionalInt()
					.orElseThrow(() -> new SecsSimulatorException(""));
			
			if ( v < 0 || v >= 65536 ) {
				
				//TODO
				throw new SecsSimulatorException("devid x");
			}
			
			config().deviceId(v);
		}
		catch ( SecsSimulatorException e ) {
			
			//TODO
		}
		
		if ( this.equipRadio.isSelected() ) {
			
			config().isEquip(true);
			
		} else if ( this.hostRadio.isSelected() ) {
			
			config().isEquip(false);
		}
		
		config().isMaster(this.masterMode.isSelected());
		
		setTime(this.t1Text, config()::timeoutT1, "T1");
		setTime(this.t2Text, config()::timeoutT2, "T2");
		setTime(this.t3Text, config()::timeoutT3, "T3");
		setTime(this.t4Text, config()::timeoutT4, "T4");
		setTime(this.t5Text, config()::timeoutT5, "T5");
		setTime(this.t6Text, config()::timeoutT6, "T6");
		setTime(this.t7Text, config()::timeoutT7, "T7");
		setTime(this.t8Text, config()::timeoutT8, "T8");
		
		try {
			
			//TODO
			int v = this.retryText.optionalInt()
					.orElseThrow(() -> new SecsSimulatorException(""));
			
			if ( v < 0 ) {
				
				throw new SecsSimulatorException("retry < 0");
			}
			
			config().retry(v);
		}
		catch ( SecsSimulatorException e ) {
			
			//TODO
		}
		
		if ( this.linktest.isSelected() ) {
			
			//TODO
			//linktest
			
		} else {
			
			config().notLinktest();
		}
		
		config().autoReply().set(this.autoReply.isSelected());
		config().autoReplyS9Fy().set(this.autoReplyS9Fy.isSelected());
		config().autoReplySxF0().set(this.autoReplySxF0.isSelected());
		
	}
	
	private void setTime(NumberTextField t, Consumer<Float> c, String timeName) {
		try {
			double v = t.optionalDouble()
					.orElseThrow(() -> new SecsSimulatorException(""));
			
			if ( v <= 0.0D ) {
				throw new SecsSimulatorException("timeout-x");
			}
			
			c.accept(Double.valueOf(v).floatValue());
		}
		catch ( SecsSimulatorException e ) {
			
			//TODO
		}
	}
}
