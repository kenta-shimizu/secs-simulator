package com.shimizukenta.secssimulator.swing;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.nio.file.Path;
import java.util.Collection;

import javax.swing.BoxLayout;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import com.shimizukenta.secssimulator.MacroRecipePair;
import com.shimizukenta.secssimulator.SecsSimulatorLog;
import com.shimizukenta.secssimulator.SmlAliasPair;
import com.shimizukenta.secssimulator.macro.MacroWorker;

public class AbstractSwingInnerFrame extends JInternalFrame {
	
	private static final long serialVersionUID = -2705517943548868191L;
	
	private final SwingSecsSimulator simm;
	
	public AbstractSwingInnerFrame(SwingSecsSimulator parent) {
		super();
		this.simm = parent;
	}

	public AbstractSwingInnerFrame(SwingSecsSimulator parent, String title) {
		super(title);
		this.simm = parent;
	}

	public AbstractSwingInnerFrame(
			SwingSecsSimulator parent,
			String title,
			boolean resizable) {
		
		super(title, resizable);
		this.simm = parent;
	}

	public AbstractSwingInnerFrame(
			SwingSecsSimulator parent,
			String title,
			boolean resizable,
			boolean closable) {
		
		super(title, resizable, closable);
		this.simm = parent;
	}

	public AbstractSwingInnerFrame(
			SwingSecsSimulator parent,
			String title,
			boolean resizable,
			boolean closable,
			boolean maximizable) {
		
		super(title, resizable, closable, maximizable);
		this.simm = parent;
	}

	public AbstractSwingInnerFrame(
			SwingSecsSimulator parent,
			String title,
			boolean resizable,
			boolean closable,
			boolean maximizable,
			boolean iconifiable) {
		
		super(title, resizable, closable, maximizable, iconifiable);
		this.simm = parent;
	}
	
	protected final SwingSecsSimulator simulator() {
		return simm;
	}
	
	protected final SwingSecsSimulatorConfig config() {
		return simm.config();
	}
	
	protected void putMessageLog(SecsSimulatorLog log) {
		/* Override-if-use */
	}
	
	protected void notifyCommunicateStateChanged(boolean communicated) {
		/* Override-if-use */
	}
	
	protected void notifyLoggingPropertyChanged(Path path) {
		/* Override-if-use */
	}
	
	protected void notifySmlAliasesChanged(Collection<? extends SmlAliasPair> pairs) {
		/* Override-if-use */
	}
	
	protected void notifyMacroRecipeChanged(Collection<? extends MacroRecipePair> pairs) {
		/* Override-if-use */
	}
	
	protected void notifyMacroWorkerStateChanged(MacroWorker w) {
		/* Override-if-use */
	}
	
	
	protected static BorderLayout defaultBorderLayout() {
		return new BorderLayout(2, 2);
	}
	
	protected static GridLayout defaultGridLayout(int rows, int cols) {
		return new GridLayout(rows, cols, 2, 2);
	}
	
	protected static FlowLayout defaultFlowLayout(int align) {
		return new FlowLayout(align, 2, 2);
	}
	
	protected static JPanel emptyPanel() {
		JPanel p = new JPanel();
		p.setOpaque(false);
		return p;
	}
	
	protected static JPanel borderPanel() {
		JPanel p = new JPanel(defaultBorderLayout());
		p.setOpaque(false);;
		return p;
	}
	
	protected static JPanel gridPanel(int rows, int cols) {
		JPanel p = new JPanel(defaultGridLayout(rows, cols));
		p.setOpaque(false);
		return p;
	}
	
	protected static JPanel flowPanel(int align) {
		JPanel p = new JPanel(defaultFlowLayout(align));
		p.setOpaque(false);
		return p;
	}
	
	protected static JPanel lineBoxPanel() {
		return boxPanel(BoxLayout.LINE_AXIS);
	}
	
	protected static JPanel pageBoxPanel() {
		return boxPanel(BoxLayout.PAGE_AXIS);
	}
	
	protected static JPanel boxPanel(int axis) {
		JPanel p = new JPanel();
		BoxLayout layout = new BoxLayout(p, axis);
		p.setLayout(layout);
		p.setOpaque(false);
		return p;
	}
	
	protected static TitledBorder defaultTitledBorder(String title) {
		return new TitledBorder(
				new EtchedBorder(EtchedBorder.LOWERED),
				title,
				TitledBorder.LEFT,
				TitledBorder.TOP);
	}
	
}
