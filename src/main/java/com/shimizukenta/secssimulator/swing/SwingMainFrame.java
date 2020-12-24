package com.shimizukenta.secssimulator.swing;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.shimizukenta.secs.sml.SmlMessage;
import com.shimizukenta.secssimulator.MacroRecipePair;
import com.shimizukenta.secssimulator.SecsSimulatorLog;
import com.shimizukenta.secssimulator.SmlAliasPair;
import com.shimizukenta.secssimulator.macro.MacroWorker;

public class SwingMainFrame extends JFrame {
	
	private static final long serialVersionUID = 4147107959994828227L;
	
	private final JDesktopPane desktopPane = new JDesktopPane();
	private final Collection<AbstractSwingInnerFrame> inners = new ArrayList<>();
	
	private final MenuBar menubar;
	private final SendSmlDirectFrame sendSmlDirectFrame;
	private final MacroFrame macroFrame;
	
	private final SwingSecsSimulator simm;
	
	public SwingMainFrame(SwingSecsSimulator simm) {
		super("Swing SECS Simulator");
		
		this.simm = simm;
		
		this.menubar = new MenuBar(simm);
		this.sendSmlDirectFrame = new SendSmlDirectFrame(simm);
		this.macroFrame = new MacroFrame(simm);
		
		this.setJMenuBar(this.menubar);
		
		this.inners.add(new ViewFrame(simm));
		this.inners.add(new ControlFrame(simm));
		this.inners.add(this.sendSmlDirectFrame);
		this.inners.add(this.macroFrame);
		this.inners.add(new LoggingFrame(simm));
		
		this.inners.forEach(this.desktopPane::add);
		
		this.add(desktopPane);
		
		if ( config().fullScreen() ) {
			
			//HOOK
			
		} else {
			
			this.setSize(config().screenWidth(), config().screenHeight());
			this.setLocationRelativeTo(null);
		}
		
		config().darkMode().addChangeListener(dark -> {
			
			if ( dark ) {
				
				//HOOK
				
			} else {
				
				//HOOK
			}
		});
	}
	
	@Override
	public void dispose() {
		inners.forEach(AbstractSwingInnerFrame::dispose);
		super.dispose();
	}
	
	protected SwingSecsSimulator simulator() {
		return simm;
	}
	
	protected SwingSecsSimulatorConfig config() {
		return simm.config();
	}
	
	
	protected void showSetConfigDialog() {
		
		//TODO
	}
	
	protected void showSaveConfigDialog() {
		
		//TODO
	}
	
	protected void showLoadConfigDialog() {
		
		//TODO
	}
	
	protected void showLoggingDialog() {
		
		//TODO
	}
	
	protected void showAddSmlDialog() {
		
		//TODO
	}
	
	protected void showSendSmlDirectFrame() {
		this.sendSmlDirectFrame.setVisible(true);
	}
	
	protected void showAddMacroRecipeDiralog() {
		
		//TODO
	}
	
	protected void showMacroFrame() {
		this.macroFrame.setVisible(true);
	}
	
	
	
	protected void showSml(SmlMessage sm) {
		JTextArea textarea = new JTextArea(sm.toString());
		textarea.setEditable(false);
		
		final JScrollPane scrollPane = new JScrollPane(
				textarea,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		JOptionPane.showMessageDialog(this, scrollPane);
	}
	
	
	protected void putMessageLog(SecsSimulatorLog log) {
		this.inners.forEach(f -> {f.putMessageLog(log);});
	}
	
	protected void notifyCommunicateStateChanged(boolean communicated) {
		this.inners.forEach(f -> {f.notifyCommunicateStateChanged(communicated);});
	}
	
	protected void notifyLoggingPropertyChanged(Path path) {
		this.inners.forEach(f -> {f.notifyLoggingPropertyChanged(path);});
		this.menubar.notifyLoggingPropertyChanged(path);
	}
	
	protected void notifySmlAliasesChanged(Collection<? extends SmlAliasPair> pairs) {
		this.inners.forEach(f -> {f.notifySmlAliasesChanged(pairs);});
	}
	
	protected void notifyMacroRecipeChanged(Collection<? extends MacroRecipePair> pairs) {
		this.inners.forEach(f -> {f.notifyMacroRecipeChanged(pairs);});
	}
	
	protected void notifyMacroWorkerStateChanged(MacroWorker w) {
		this.inners.forEach(f -> {f.notifyMacroWorkerStateChanged(w);});
	}
	
}
