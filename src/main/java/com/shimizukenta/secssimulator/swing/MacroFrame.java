package com.shimizukenta.secssimulator.swing;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collection;

import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import com.shimizukenta.secssimulator.MacroRecipePair;
import com.shimizukenta.secssimulator.macro.MacroWorker;

public class MacroFrame extends AbstractSwingInnerFrame {
	
	private static final long serialVersionUID = 9163669972439175262L;
	
	private final JList<String> recipeList;
	private final JList<String> workerList;
	
	private final JButton addRecipeButton;
	private final JButton removeRecipeButton;
	private final JButton showRecipeButton;
	private final JButton runRecipeButton;
	
	private final JButton eraseWorkerButton;
	private final JButton cancelWorkerButton;
	
	public MacroFrame(SwingSecsSimulator parent) {
		super(parent, "Macro", true, true, true, true);
		
		this.recipeList = new JList<>();
		this.recipeList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.recipeList.addMouseListener(new MouseAdapter() {
			
			@Override
			public void mouseClicked(MouseEvent ev) {
				if ( ev.getClickCount() == 2 ) {
					runRecipeButton.doClick();
				}
			}
		});
		
		this.workerList = new JList<>();
		this.workerList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		this.addRecipeButton = new JButton("Add...");
		this.addRecipeButton.addActionListener(ev -> {
			simulator().showAddMacroRecipeDiralog();
		});
		
		this.removeRecipeButton = new JButton("Remove");
		this.removeRecipeButton.addActionListener(ev -> {
			
			//TODO
		});
		
		this.showRecipeButton = new JButton("Show");
		this.showRecipeButton.addActionListener(ev -> {
			
			//TODO
		});
		
		this.runRecipeButton = new JButton("Run");
		this.runRecipeButton.addActionListener(ev -> {
			
			//TODO
		});
		
		this.eraseWorkerButton = new JButton("Erase");
		this.eraseWorkerButton.addActionListener(ev -> {
			
			//TODO
		});
		
		this.cancelWorkerButton = new JButton("Cancel");
		this.cancelWorkerButton.addActionListener(ev -> {
			
			//TODO
		});
		
		this.setDefaultCloseOperation(HIDE_ON_CLOSE);
		
		this.setBounds(
				(config().screenWidth()  * 20 / 100),
				(config().screenHeight() * 10 / 100),
				(config().screenWidth()  * 50 / 100),
				(config().screenHeight() * 70 / 100));
		
		this.setLayout(defaultGridLayout(2, 1));
		
		{
			JPanel p = borderPanel();
			
			p.setBorder(defaultTitledBorder("Recipe"));
			
			{
				JPanel pp = borderPanel();
				
				{
					JPanel ppp = lineBoxPanel();
					
					ppp.add(this.runRecipeButton);
					
					pp.add(ppp, BorderLayout.EAST);
				}
				{
					JPanel ppp = lineBoxPanel();
					
					ppp.add(this.addRecipeButton);
					ppp.add(this.removeRecipeButton);
					ppp.add(this.showRecipeButton);
					
					pp.add(ppp, BorderLayout.WEST);
				}
				
				p.add(pp, BorderLayout.NORTH);
			}
			{
				final JScrollPane scrollPane = new JScrollPane(
						this.recipeList,
						JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
						JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
				
				p.add(scrollPane, BorderLayout.CENTER);
			}
			
			this.add(p);
		}
		{
			JPanel p = borderPanel();
			
			p.setBorder(defaultTitledBorder("Worker"));
			
			{
				JPanel pp = borderPanel();
				
				{
					JPanel ppp = lineBoxPanel();
					
					ppp.add(this.cancelWorkerButton);
					
					pp.add(ppp, BorderLayout.EAST);
				}
				{
					JPanel ppp = lineBoxPanel();
					
					ppp.add(this.eraseWorkerButton);
					
					pp.add(ppp, BorderLayout.WEST);
				}
				
				p.add(pp, BorderLayout.NORTH);
			}
			{
				final JScrollPane scrollPane = new JScrollPane(
						this.workerList,
						JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
						JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
				
				p.add(scrollPane, BorderLayout.CENTER);
			}
			
			this.add(p);
		}
		
	}
	
	@Override
	protected void notifyMacroRecipeChanged(Collection<? extends MacroRecipePair> pairs) {
		
		//TODO
	}
	
	@Override
	protected void notifyMacroWorkerStateChanged(MacroWorker w) {
		
		//TODO
	}


}
