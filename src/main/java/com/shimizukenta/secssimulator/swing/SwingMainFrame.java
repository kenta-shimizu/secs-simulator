package com.shimizukenta.secssimulator.swing;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.JDesktopPane;
import javax.swing.JFrame;

import com.shimizukenta.secssimulator.MacroRecipePair;
import com.shimizukenta.secssimulator.SecsSimulatorLog;
import com.shimizukenta.secssimulator.SmlAliasPair;
import com.shimizukenta.secssimulator.macro.MacroWorker;

public class SwingMainFrame extends JFrame {
	
	private static final long serialVersionUID = 4147107959994828227L;
	
	private final JDesktopPane desktopPane = new JDesktopPane();
	private final Collection<AbstractSwingInnerFrame> inners = new ArrayList<>();
	
	private final SwingSecsSimulator simm;
	
	public SwingMainFrame(SwingSecsSimulator simm) {
		super("Swing SECS Simulator");
		
		this.simm = simm;
		
		this.inners.add(new ViewerFrame(simm));
		
		this.inners.forEach(this.desktopPane::add);
		
		if ( config().darkMode() ) {
			
			//HOOK
		}
		
		if ( config().fullScreen() ) {
			
			//HOOK
			
		} else {
			
			this.setSize(config().screenWidth(), config().screenHeight());
			this.add(desktopPane);
			this.setLocationRelativeTo(null);
		}
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
	
	protected void putMessageLog(SecsSimulatorLog log) {
		this.inners.forEach(f -> {f.putMessageLog(log);});
	}
	
	protected void notifyCommunicateStateChanged(boolean communicated) {
		this.inners.forEach(f -> {f.notifyCommunicateStateChanged(communicated);});
	}
	
	protected void notifyLoggingPropertyChanged(Path path) {
		this.inners.forEach(f -> {f.notifyLoggingPropertyChanged(path);});
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
