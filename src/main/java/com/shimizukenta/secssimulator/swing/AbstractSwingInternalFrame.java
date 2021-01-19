package com.shimizukenta.secssimulator.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.nio.file.Path;
import java.util.Collection;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import com.shimizukenta.secssimulator.MacroRecipePair;
import com.shimizukenta.secssimulator.SecsSimulatorLog;
import com.shimizukenta.secssimulator.SmlAliasPair;
import com.shimizukenta.secssimulator.macro.MacroWorker;

public class AbstractSwingInternalFrame extends JInternalFrame {
	
	private static final long serialVersionUID = -2705517943548868191L;
	
	private final SwingSecsSimulator simm;
	
	public AbstractSwingInternalFrame(SwingSecsSimulator parent) {
		super();
		this.simm = parent;
		setDarkInternalFrame();
	}

	public AbstractSwingInternalFrame(SwingSecsSimulator parent, String title) {
		super(title);
		this.simm = parent;
		setDarkInternalFrame();
	}

	public AbstractSwingInternalFrame(
			SwingSecsSimulator parent,
			String title,
			boolean resizable) {
		
		super(title, resizable);
		this.simm = parent;
		setDarkInternalFrame();
	}

	public AbstractSwingInternalFrame(
			SwingSecsSimulator parent,
			String title,
			boolean resizable,
			boolean closable) {
		
		super(title, resizable, closable);
		this.simm = parent;
		setDarkInternalFrame();
	}

	public AbstractSwingInternalFrame(
			SwingSecsSimulator parent,
			String title,
			boolean resizable,
			boolean closable,
			boolean maximizable) {
		
		super(title, resizable, closable, maximizable);
		this.simm = parent;
		setDarkInternalFrame();
	}

	public AbstractSwingInternalFrame(
			SwingSecsSimulator parent,
			String title,
			boolean resizable,
			boolean closable,
			boolean maximizable,
			boolean iconifiable) {
		
		super(title, resizable, closable, maximizable, iconifiable);
		this.simm = parent;
		setDarkInternalFrame();
	}
	
	private void setDarkInternalFrame() {
		final Color bgColor = this.getContentPane().getBackground();
		this.config().darkMode().addChangeListener(dark -> {
			if ( dark ) {
				this.getContentPane().setBackground(this.config().defaultDarkPanelBackGroundColor());
			} else {
				this.getContentPane().setBackground(bgColor);
			}
		});
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
	
	protected TitledBorder defaultTitledBorder(String title) {
		
		final TitledBorder ttb = new TitledBorder(
				new EtchedBorder(EtchedBorder.LOWERED),
				title,
				TitledBorder.LEFT,
				TitledBorder.TOP);
		
		final Color ttColor = ttb.getTitleColor();
		
		this.config().darkMode().addChangeListener(dark -> {
			if ( dark ) {
				ttb.setTitleColor(this.config().defaultDarkAreaForeGroundColor());
			} else {
				ttb.setTitleColor(ttColor);
			}
		});
		
		return ttb;
	}
	
	protected JTextArea defaultTextArea() {
		
		final JTextArea area = new JTextArea("");
		
		final Color bgColor = area.getBackground();
		final Color fgColor = area.getForeground();
		
		this.config().darkMode().addChangeListener(dark -> {
			if ( dark ) {
				area.setBackground(this.config().defaultDarkAreaBackGroundColor());
				area.setForeground(this.config().defaultDarkAreaForeGroundColor());
			} else {
				area.setBackground(bgColor);
				area.setForeground(fgColor);
			}
		});

		return area;
	}
	
	protected JCheckBox defaultCheckBox(String text, boolean selected) {
		
		final JCheckBox box = new JCheckBox(text, selected);
		
		box.setOpaque(false);
		
		final Color fgColor = box.getForeground();
		
		this.config().darkMode().addChangeListener(dark -> {
			if ( dark ) {
				box.setForeground(this.config().defaultDarkAreaForeGroundColor());
			} else {
				box.setForeground(fgColor);
			}
		});
		
		return box;
	}
	
	protected static final JScrollPane defaultScrollPane(Component view) {
		return new JScrollPane(
				view,
			JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
			JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
	}
	
}
