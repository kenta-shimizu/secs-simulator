package com.shimizukenta.secssimulator.swing;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.GridLayout;
import java.awt.Window;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import com.shimizukenta.secssimulator.MacroRecipePair;
import com.shimizukenta.secssimulator.SecsSimulatorLog;
import com.shimizukenta.secssimulator.SmlAliasPair;
import com.shimizukenta.secssimulator.macro.MacroWorker;

public abstract class AbstractSwingDialog extends JDialog {
	
	private static final long serialVersionUID = -3750411035483191459L;
	
	private final SwingSecsSimulator simm;
	
	public AbstractSwingDialog(SwingSecsSimulator simm) {
		super();
		this.simm = simm;
	}

	public AbstractSwingDialog(SwingSecsSimulator simm, Frame owner) {
		super(owner);
		this.simm = simm;
	}

	public AbstractSwingDialog(SwingSecsSimulator simm, Dialog owner) {
		super(owner);
		this.simm = simm;
	}

	public AbstractSwingDialog(SwingSecsSimulator simm, Window owner) {
		super(owner);
		this.simm = simm;
	}

	public AbstractSwingDialog(SwingSecsSimulator simm, Frame owner, boolean modal) {
		super(owner, modal);
		this.simm = simm;
	}

	public AbstractSwingDialog(SwingSecsSimulator simm, Frame owner, String title) {
		super(owner, title);
		this.simm = simm;
	}

	public AbstractSwingDialog(SwingSecsSimulator simm, Dialog owner, boolean modal) {
		super(owner, modal);
		this.simm = simm;
	}

	public AbstractSwingDialog(SwingSecsSimulator simm, Dialog owner, String title) {
		super(owner, title);
		this.simm = simm;
	}

	public AbstractSwingDialog(SwingSecsSimulator simm, Window owner, ModalityType modalityType) {
		super(owner, modalityType);
		this.simm = simm;
	}

	public AbstractSwingDialog(SwingSecsSimulator simm, Window owner, String title) {
		super(owner, title);
		this.simm = simm;
	}

	public AbstractSwingDialog(SwingSecsSimulator simm, Frame owner, String title, boolean modal) {
		super(owner, title, modal);
		this.simm = simm;
	}

	public AbstractSwingDialog(SwingSecsSimulator simm, Dialog owner, String title, boolean modal) {
		super(owner, title, modal);
		this.simm = simm;
	}

	public AbstractSwingDialog(SwingSecsSimulator simm, Window owner, String title, ModalityType modalityType) {
		super(owner, title, modalityType);
		this.simm = simm;
	}

	public AbstractSwingDialog(SwingSecsSimulator simm, Frame owner, String title, boolean modal, GraphicsConfiguration gc) {
		super(owner, title, modal, gc);
		this.simm = simm;
	}

	public AbstractSwingDialog(SwingSecsSimulator simm, Dialog owner, String title, boolean modal, GraphicsConfiguration gc) {
		super(owner, title, modal, gc);
		this.simm = simm;
	}

	public AbstractSwingDialog(SwingSecsSimulator simm, Window owner, String title, ModalityType modalityType, GraphicsConfiguration gc) {
		super(owner, title, modalityType, gc);
		this.simm = simm;
	}
	
	
	public SwingSecsSimulator simulator() {
		return simm;
	}
	
	public SwingSecsSimulatorConfig config() {
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
	
	protected void putFailure(Throwable t) {
		simulator().putFailure(t);
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
	
	protected static final JScrollPane defaultScrollPane(Component view) {
		return new JScrollPane(
				view,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
	}
	
	protected static JPanel compactStackPanel(Object borderStack, Component... comps) {
		return compactStackPanel(borderStack, Arrays.asList(comps));
	}
	
	protected static JPanel compactStackPanel(Object borderStack, List<? extends Component> comps) {
		return innerCompactStackPanel(new LinkedList<>(comps), borderStack);
	}
	
	private static JPanel innerCompactStackPanel(LinkedList<? extends Component> ll, Object borderStack) {
		if ( ll.isEmpty() ) {
			return emptyPanel();
		} else {
			Component c = ll.removeFirst();
			JPanel p = borderPanel();
			p.add(c, borderStack);
			p.add(innerCompactStackPanel(ll, borderStack), BorderLayout.CENTER);
			return p;
		}
	}
	
}
