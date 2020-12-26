package com.shimizukenta.secssimulator.swing;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Vector;
import java.util.stream.Collectors;

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
	
	private boolean recipesUpdated;
	private boolean workersUpdated;
	
	public MacroFrame(SwingSecsSimulator parent) {
		super(parent, "Macro", true, true, true, true);
		
		this.recipesUpdated = false;
		this.workersUpdated = false;
		
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
		this.workerList.addMouseListener(new MouseAdapter() {
			
			@Override
			public void mouseClicked(MouseEvent ev) {
				if ( ev.getClickCount() == 2 ) {
					eraseWorkerButton.doClick();
				}
			}
		});
		
		this.addRecipeButton = new JButton("Add...");
		this.addRecipeButton.addActionListener(ev -> {
			simulator().showAddMacroRecipeDiralog();
		});
		
		this.removeRecipeButton = new JButton("Remove");
		this.removeRecipeButton.addActionListener(ev -> {
			String alias = this.recipeList.getSelectedValue();
			if ( alias != null ) {
				simulator().removeMacroRecipe(alias);
			}
		});
		
		this.showRecipeButton = new JButton("Show");
		this.showRecipeButton.addActionListener(ev -> {
			String alias = this.recipeList.getSelectedValue();
			if ( alias != null ) {
				simulator().optionalMacroRecipeAlias(alias).ifPresent(r -> {
					simulator().showMacroRecipeMessage(r);
				});
			}
		});
		
		this.runRecipeButton = new JButton("Run");
		this.runRecipeButton.addActionListener(ev -> {
			String alias = this.recipeList.getSelectedValue();
			if ( alias != null ) {
				simulator().optionalMacroRecipeAlias(alias).ifPresent(r -> {
					try {
						simulator().startMacro(r);
					}
					catch ( InterruptedException ignore ) {
					}
				});
			}
		});
		
		this.eraseWorkerButton = new JButton("Erase");
		this.eraseWorkerButton.setEnabled(false);
		this.eraseWorkerButton.addActionListener(ev -> {
			this.eraseMacroWorker();
		});
		
		this.cancelWorkerButton = new JButton("Cancel");
		this.cancelWorkerButton.setEnabled(false);
		this.cancelWorkerButton.addActionListener(ev -> {
			this.cancelMacroWorker();
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
	
	private final Object syncRecipes = new Object();
	
	@Override
	protected void notifyMacroRecipeChanged(Collection<? extends MacroRecipePair> pairs) {
		synchronized ( this.syncRecipes ) {
			this.recipesUpdated = true;
			this.updateRecipeListView();
		}
	}
	
	private final List<MacroWorker> workers = new ArrayList<>();
	
	@Override
	protected void notifyMacroWorkerStateChanged(MacroWorker w) {
		synchronized ( this.workers ) {
			
			if ( ! this.workers.contains(w) ) {
				this.workers.add(w);
				Collections.sort(this.workers);
			}
			
			this.workersUpdated = true;
			this.updateWorkerListView();
		}
	}
	
	private void eraseMacroWorker() {
		synchronized ( this.workers ) {
			int index = this.workerList.getSelectedIndex();
			if ( index >= 0 ) {
				MacroWorker w = this.workers.get(index);
				if ( w.isDone() ) {
					this.workers.remove(w);
					this.workersUpdated = true;
					this.updateWorkerListView();
				}
			}
		}
	}
	
	private void cancelMacroWorker() {
		synchronized ( this.workers ) {
			int index = this.workerList.getSelectedIndex();
			if ( index >= 0 ) {
				this.workers.get(index).cancel(true);
			}
		}
	}
	
	@Override
	public void setVisible(boolean aFlag) {
		
		super.setVisible(aFlag);
		
		if ( aFlag ) {
			this.updateRecipeListView();
			this.updateWorkerListView();
		}
	}
	
	private void updateRecipeListView() {
		synchronized ( this.syncRecipes ) {
			if ( this.isVisible() && this.recipesUpdated ) {
				
				boolean hasRecipes = ! simulator().macroRecipeAliases().isEmpty();
				
				if ( hasRecipes ) {
					
					this.recipeList.setListData(
							new Vector<>(simulator().macroRecipeAliases())
							);
					
				} else {
					
					this.recipeList.setListData(new Vector<>());
				}
				
				this.removeRecipeButton.setEnabled(hasRecipes);
				this.showRecipeButton.setEnabled(hasRecipes);
				this.runRecipeButton.setEnabled(hasRecipes);
				
				this.recipesUpdated = false;
				this.repaint();
			}
		}
	}
	
	private void updateWorkerListView() {
		synchronized ( this.workers ) {
			if ( this.isVisible() && this.workersUpdated ) {
				
				boolean hasWorkers = ! this.workers.isEmpty();
				
				if ( hasWorkers ) {
					
					this.workerList.setListData(
							new Vector<>(
									this.workers.stream()
									.sorted()
									.map(w -> w.toString())
									.collect(Collectors.toList()))
							);
				
				} else {
					
					this.workerList.setListData(new Vector<>());
				}
				
				this.eraseWorkerButton.setEnabled(hasWorkers);
				this.cancelWorkerButton.setEnabled(hasWorkers);
				
				this.workersUpdated = false;
				this.repaint();
			}
		}
	}
	
}
