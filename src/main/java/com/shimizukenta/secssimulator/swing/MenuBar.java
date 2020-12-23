package com.shimizukenta.secssimulator.swing;

import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

public class MenuBar extends JMenuBar {
	
	private static final long serialVersionUID = 5162563643626620249L;
	
	private final SwingSecsSimulator simm;
	
	public MenuBar(SwingSecsSimulator parent) {
		super();
		
		this.simm = parent;
		
		addMenu(
				menu("File",
						item("Set Connfig...", ev -> {
							
							//TODO
						}),
						separator(),
						item("Load Config...", ev -> {
							
							//TODO
						}),
						item("Save Config...", ev -> {
							
							//TODO
						}),
						separator(),
						item("Start logging...", ev -> {
							
							//TODO
						}),
						item("Stop logging", ev -> {
							
							//TODO
						}),
						separator(),
						item("Quit", ev -> {
							simulator().notifyApplicationQuit();
						})
						),
				menu("Communicator",
						item("Open Communicator", ev -> {
							simulator().openCommunicator();
						}),
						item("Close Communicator", ev -> {
							simulator().closeCommunicator();
						})
						),
				menu("SML",
						item("Add SML...", ev -> {
							
							//TODO
						}),
						separator(),
						item("Show Direct Sender", ev -> {
							simulator().showSendSmlDirectFrame();
						})
						),
				menu("Macro",
						item("Show Macro window", ev -> {
							
						}),
						separator(),
						item("Add Macro...", ev -> {
							
							//TODO
						})
						)
				);
	}
	
	private void addMenu(JMenu... menus) {
		for ( JMenu m : menus ) {
			this.add(m);
		}
	}
	
	private static JMenu menu(String name, JMenuItem... items) {
		JMenu m = new JMenu(name);
		for ( JMenuItem i : items ) {
			if ( i instanceof InnerSeparator ) {
				m.addSeparator();
			} else {
				m.add(i);
			}
		}
		return m;
	}
	
	private static JMenuItem item(String name,  ActionListener l) {
		JMenuItem i = new JMenuItem(name);
		i.addActionListener(l);
		return i;
	}
	
	private static JMenuItem separator() {
		return singletonSeparator;
	}
	
	private static class InnerSeparator extends JMenuItem {
		
		private static final long serialVersionUID = 4586831531873641876L;
		
		private InnerSeparator() {
			super();
		}
	}
	
	private static final InnerSeparator singletonSeparator = new InnerSeparator();
	
	protected SwingSecsSimulator simulator() {
		return simm;
	}
	
	protected SwingSecsSimulatorConfig config() {
		return simm.config();
	}
	
}
