package com.shimizukenta.secssimulator.swing;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

import javax.swing.JDesktopPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.shimizukenta.secs.sml.SmlMessage;
import com.shimizukenta.secs.sml.SmlParseException;
import com.shimizukenta.secssimulator.MacroRecipePair;
import com.shimizukenta.secssimulator.SecsSimulatorLog;
import com.shimizukenta.secssimulator.SmlAliasPair;
import com.shimizukenta.secssimulator.macro.MacroRecipe;
import com.shimizukenta.secssimulator.macro.MacroRecipeParseException;
import com.shimizukenta.secssimulator.macro.MacroWorker;

public class SwingMainFrame extends JFrame {
	
	private static final long serialVersionUID = 4147107959994828227L;
	
	private final JDesktopPane desktopPane = new JDesktopPane();
	private final Collection<AbstractSwingInternalFrame> inners = new ArrayList<>();
	private final Collection<AbstractSwingDialog> dialogs = new ArrayList<>();
	
	private final JFileChooser loadConfigFileChooser = new JFileChooser();
	private final JFileChooser saveConfigFileChooser = new JFileChooser();
	private final JFileChooser loggingFileChooser = new JFileChooser();
	private final JFileChooser addSmlFileChooser = new JFileChooser();
	private final JFileChooser loadSmlFileChooser = new JFileChooser();
	private final JFileChooser saveSmlFileChooser = new JFileChooser();
	private final JFileChooser addMacroRecipeFileChooser = new JFileChooser();
	
	private final MenuBar menubar;
	private final ViewFrame viewFrame;
	private final ControlFrame controlFrame;
	private final SmlEditorFrame smlEditorFrame;
	private final MacroFrame macroFrame;
	private final FailureFrame failureFrame;
	
	private final EntryDialog entryDialog;
	private final SetConfigDialog setConfigDialog;
	private final TextViewDialog showSmlMessageDialog;
	private final TextViewDialog showMacroRecipeMessageDialog;
	
	private final SwingSecsSimulator simm;
	
	public SwingMainFrame(SwingSecsSimulator simm) {
		super("Swing SECS Simulator");
		
		this.simm = simm;
		
		
		this.loadConfigFileChooser.setDialogTitle("Load config from file");
		this.loadConfigFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		this.loadConfigFileChooser.setMultiSelectionEnabled(false);
		{
			this.loadConfigFileChooser.setAcceptAllFileFilterUsed(true);
			
			final FileFilter filter = new FileNameExtensionFilter("JSON File", "json", "JSON");
			this.loadConfigFileChooser.addChoosableFileFilter(filter);
			
			this.loadConfigFileChooser.setFileFilter(filter);
		}
		
		this.saveConfigFileChooser.setDialogTitle("Save config file");
		
		this.loggingFileChooser.setDialogTitle("Start Logging to file");
		
		this.addSmlFileChooser.setDialogTitle("Add SML from files");
		this.addSmlFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		this.addSmlFileChooser.setMultiSelectionEnabled(true);
		{
			this.addSmlFileChooser.setAcceptAllFileFilterUsed(true);
			
			final FileFilter filter = new FileNameExtensionFilter("SML File", "sml", "SML");
			this.addSmlFileChooser.addChoosableFileFilter(filter);
			
			this.addSmlFileChooser.setFileFilter(filter);
		}
		
		this.loadSmlFileChooser.setDialogTitle("Load SML from file");
		this.loadSmlFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		this.loadSmlFileChooser.setMultiSelectionEnabled(false);
		{
			this.loadSmlFileChooser.setAcceptAllFileFilterUsed(true);
			
			final FileFilter filter = new FileNameExtensionFilter("SML File", "sml", "SML");
			this.loadSmlFileChooser.addChoosableFileFilter(filter);
			
			this.loadSmlFileChooser.setFileFilter(filter);
		}
		
		this.saveSmlFileChooser.setDialogTitle("Save SML file");
		
		this.addMacroRecipeFileChooser.setDialogTitle("Add Macro-Recipe from files");
		this.addMacroRecipeFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		this.addMacroRecipeFileChooser.setMultiSelectionEnabled(true);
		{
			this.addMacroRecipeFileChooser.setAcceptAllFileFilterUsed(true);
			
			final FileFilter filter = new FileNameExtensionFilter("JSON File", "json", "JSON");
			this.addMacroRecipeFileChooser.addChoosableFileFilter(filter);
			
			this.addMacroRecipeFileChooser.setFileFilter(filter);
		}
		
		
		this.menubar = new MenuBar(simm);
		this.viewFrame = new ViewFrame(simm);
		this.controlFrame = new ControlFrame(simm);
		this.smlEditorFrame = new SmlEditorFrame(simm);
		this.macroFrame = new MacroFrame(simm);
		this.failureFrame = new FailureFrame(simm);
		
		this.entryDialog = new EntryDialog(this, simm);
		this.setConfigDialog = new SetConfigDialog(this, simm);
		this.showSmlMessageDialog = new TextViewDialog(this, "Show SML", simm);
		this.showMacroRecipeMessageDialog = new TextViewDialog(this, "Show Macro Recipe", simm);
		
		this.setJMenuBar(this.menubar);
		
		this.inners.add(this.viewFrame);
		this.inners.add(this.controlFrame);
		this.inners.add(this.smlEditorFrame);
		this.inners.add(this.macroFrame);
		this.inners.add(new LoggingFrame(simm));
		this.inners.add(this.failureFrame);
		
		this.inners.forEach(this.desktopPane::add);
		
		this.add(desktopPane);
		
		this.dialogs.add(this.entryDialog);
		this.dialogs.add(this.setConfigDialog);
		this.dialogs.add(this.showSmlMessageDialog);
		this.dialogs.add(this.showMacroRecipeMessageDialog);
		
		
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
		inners.forEach(AbstractSwingInternalFrame::dispose);
		dialogs.forEach(AbstractSwingDialog::dispose);
		super.dispose();
	}
	
	protected SwingSecsSimulator simulator() {
		return simm;
	}
	
	protected SwingSecsSimulatorConfig config() {
		return simm.config();
	}
	
	protected void showEntryDialog() {
		this.entryDialog.setVisible(true);
	}
	
	protected void showViewFrame() {
		this.viewFrame.setVisible(true);
	}
	
	protected void showControlFrame() {
		this.controlFrame.setVisible(true);
	}
	
	protected boolean showSetConfigDialog() {
		this.setConfigDialog.setVisible(true);
		return setConfigDialog.getResult();
	}
	
	protected boolean showLoadConfigDialog() {
		
		switch ( this.loadConfigFileChooser.showOpenDialog(this) ) {
		case JFileChooser.APPROVE_OPTION: {
			
			File file = this.loadConfigFileChooser.getSelectedFile();
			try {
				return simulator().loadConfig(file.toPath());
			}
			catch ( IOException e ) {
				simulator().putFailure(e);
			}
			break;
		}
		case JFileChooser.CANCEL_OPTION:
		case JFileChooser.ERROR_OPTION:
		default :{
			/* Nothing */
		}
		}
		
		return false;
	}
	
	protected void showSaveConfigDialog() {
		
		switch ( this.saveConfigFileChooser.showSaveDialog(this) ) {
		case JFileChooser.APPROVE_OPTION: {
			
			File file = this.saveConfigFileChooser.getSelectedFile();
			try {
				simulator().saveConfig(file.toPath());
			}
			catch ( IOException e ) {
				simulator().putFailure(e);
			}
			break;
		}
		case JFileChooser.CANCEL_OPTION:
		case JFileChooser.ERROR_OPTION:
		default :{
			/* Nothing */
		}
		}
	}
	
	protected void showLoggingDialog() {
		
		switch ( this.loggingFileChooser.showSaveDialog(this) ) {
		case JFileChooser.APPROVE_OPTION: {
			File file = this.loggingFileChooser.getSelectedFile();
			try {
				simulator().startLogging(file.toPath());
			}
			catch ( IOException e ) {
				simulator().putFailure(e);
			}
			catch ( InterruptedException ignore ) {
			}
			break;
		}
		case JFileChooser.CANCEL_OPTION:
		case JFileChooser.ERROR_OPTION:
		default: {
			/* Nothing */
		}
		}
	}
	
	protected void showAddSmlDialog() {
		
		switch ( this.addSmlFileChooser.showOpenDialog(this) ) {
		case JFileChooser.APPROVE_OPTION: {
			
			File[] files = this.addSmlFileChooser.getSelectedFiles();
			for ( File file : files ) {
				try {
					simulator().addSml(file.toPath());
				}
				catch ( SmlParseException e ) {
					simulator().putFailure(e);
				}
				catch ( IOException e ) {
					simulator().putFailure(e);
				}
			}
			
			break;
		}
		case JFileChooser.CANCEL_OPTION:
		case JFileChooser.ERROR_OPTION:
		default :{
			/* Nothing */
		}
		}
	}
	
	protected void showSmlEditorFrame() {
		this.smlEditorFrame.setVisible(true);
	}
	
	protected Optional<SmlMessage> showLoadSmlFileDialog() throws SmlParseException, IOException {
		
		switch ( this.loadSmlFileChooser.showOpenDialog(this) ) {
		case JFileChooser.APPROVE_OPTION: {
			
			File file = this.loadSmlFileChooser.getSelectedFile();
			
			return Optional.of(SmlAliasPair.fromFile(file.toPath()).sml());
			/* break; */
		}
		case JFileChooser.CANCEL_OPTION:
		case JFileChooser.ERROR_OPTION:
		default :{
			/* Nothing */
		}
		}
		
		return Optional.empty();
	}
	
	protected Optional<Path> showSaveSmlFileDialog() {
		
		switch ( this.saveSmlFileChooser.showSaveDialog(this) ) {
		case JFileChooser.APPROVE_OPTION: {
			
			File file = this.saveSmlFileChooser.getSelectedFile();
			return Optional.of(file.toPath());
			
			/* break; */
		}
		case JFileChooser.CANCEL_OPTION:
		case JFileChooser.ERROR_OPTION:
		default :{
			/* Nothing */
		}
		}
		
		return Optional.empty();
	}
	
	protected void showAddMacroRecipeDiralog() {
		
		switch ( this.addMacroRecipeFileChooser.showOpenDialog(this) ) {
		case JFileChooser.APPROVE_OPTION: {
			
			File[] files = this.addMacroRecipeFileChooser.getSelectedFiles();
			for ( File file : files ) {
				try {
					simulator().addMacroRecipe(file.toPath());
				}
				catch ( MacroRecipeParseException e ) {
					simulator().putFailure(e);
				}
				catch ( IOException e ) {
					simulator().putFailure(e);
				}
			}
			
			break;
		}
		case JFileChooser.CANCEL_OPTION:
		case JFileChooser.ERROR_OPTION:
		default :{
			/* Nothing */
		}
		}
	}
	
	protected void showMacroFrame() {
		this.macroFrame.setVisible(true);
	}
	
	
	protected void showSmlMessage(SmlMessage sm) {
		this.showSmlMessageDialog.showText(sm);
	}
	
	protected void showMacroRecipeMessage(MacroRecipe recipe) {
		this.showMacroRecipeMessageDialog.showText(recipe);
	}
	
	protected void putFailure(Throwable t) {
		this.failureFrame.put(t);
	}
	
	
	protected void putMessageLog(SecsSimulatorLog log) {
		this.inners.forEach(f -> {f.putMessageLog(log);});
		this.dialogs.forEach(d -> {d.putMessageLog(log);});
	}
	
	protected void notifyCommunicateStateChanged(boolean communicated) {
		this.inners.forEach(f -> {f.notifyCommunicateStateChanged(communicated);});
		this.dialogs.forEach(d -> {d.notifyCommunicateStateChanged(communicated);});
	}
	
	protected void notifyLoggingPropertyChanged(Path path) {
		this.inners.forEach(f -> {f.notifyLoggingPropertyChanged(path);});
		this.dialogs.forEach(d -> {d.notifyLoggingPropertyChanged(path);});
		this.menubar.notifyLoggingPropertyChanged(path);
	}
	
	protected void notifySmlAliasesChanged(Collection<? extends SmlAliasPair> pairs) {
		this.inners.forEach(f -> {f.notifySmlAliasesChanged(pairs);});
		this.dialogs.forEach(d -> {d.notifySmlAliasesChanged(pairs);});
	}
	
	protected void notifyMacroRecipeChanged(Collection<? extends MacroRecipePair> pairs) {
		this.inners.forEach(f -> {f.notifyMacroRecipeChanged(pairs);});
		this.dialogs.forEach(d -> {d.notifyMacroRecipeChanged(pairs);});
	}
	
	protected void notifyMacroWorkerStateChanged(MacroWorker w) {
		this.inners.forEach(f -> {f.notifyMacroWorkerStateChanged(w);});
		this.dialogs.forEach(d -> {d.notifyMacroWorkerStateChanged(w);});
	}
	
}
