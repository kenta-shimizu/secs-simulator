package com.shimizukenta.secssimulator.swing;

import java.awt.BorderLayout;
import java.awt.Color;
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
import javax.swing.ListSelectionModel;

import com.shimizukenta.secssimulator.MacroRecipePair;
import com.shimizukenta.secssimulator.macro.MacroWorker;

public class MacroFrame extends AbstractSwingInternalFrame {
	
	private static final long serialVersionUID = 9163669972439175262L;
	
	private final JList<String> recipeList;
	private final JList<String> workerList;
	
	private final JButton addRecipeButton;
	private final JButton removeRecipeButton;
	private final JButton showRecipeButton;
	private final JButton runRecipeButton;
	
	private final JButton removeWorkerButton;
	private final JButton abortWorkerButton;
	
	private boolean recipesUpdated;
	private boolean workersUpdated;
	
	public MacroFrame(SwingSecsSimulator parent) {
		super(parent, "Macro", true, true, true, true);
		
		this.recipesUpdated = false;
		this.workersUpdated = false;
		
		{
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
			
			final Color bgColor = this.recipeList.getBackground();
			final Color fgColor = this.recipeList.getForeground();
			
			this.config().darkMode().addChangeListener(dark -> {
				if ( dark ) {
					this.recipeList.setBackground(this.config().defaultDarkAreaBackGroundColor());
					this.recipeList.setForeground(this.config().defaultDarkAreaForeGroundColor());
				} else {
					this.recipeList.setBackground(bgColor);
					this.recipeList.setForeground(fgColor);
				}
			});
		}
		
		{
			this.workerList = new JList<>();
			this.workerList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			this.workerList.addMouseListener(new MouseAdapter() {
				
				@Override
				public void mouseClicked(MouseEvent ev) {
					if ( ev.getClickCount() == 2 ) {
						removeWorkerButton.doClick();
					}
				}
			});
			
			final Color bgColor = this.workerList.getBackground();
			final Color fgColor = this.workerList.getForeground();
			
			this.config().darkMode().addChangeListener(dark -> {
				if ( dark ) {
					this.workerList.setBackground(this.config().defaultDarkAreaBackGroundColor());
					this.workerList.setForeground(this.config().defaultDarkAreaForeGroundColor());
				} else {
					this.workerList.setBackground(bgColor);
					this.workerList.setForeground(fgColor);
				}
			});
		}
		
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
		
		this.removeWorkerButton = new JButton("Remove");
		this.removeWorkerButton.setEnabled(false);
		this.removeWorkerButton.addActionListener(ev -> {
			this.removeMacroWorker();
		});
		
		this.abortWorkerButton = new JButton("Abort");
		this.abortWorkerButton.setEnabled(false);
		this.abortWorkerButton.addActionListener(ev -> {
			this.abortMacroWorker();
		});
		
		this.setDefaultCloseOperation(HIDE_ON_CLOSE);
		
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
				p.add(defaultScrollPane(this.recipeList), BorderLayout.CENTER);
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
					
					ppp.add(this.abortWorkerButton);
					
					pp.add(ppp, BorderLayout.EAST);
				}
				{
					JPanel ppp = lineBoxPanel();
					
					ppp.add(this.removeWorkerButton);
					
					pp.add(ppp, BorderLayout.WEST);
				}
				
				p.add(pp, BorderLayout.NORTH);
			}
			{
				p.add(defaultScrollPane(this.workerList), BorderLayout.CENTER);
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
	
	private void removeMacroWorker() {
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
	
	private void abortMacroWorker() {
		synchronized ( this.workers ) {
			int index = this.workerList.getSelectedIndex();
			if ( index >= 0 ) {
				this.workers.get(index).cancel(true);
			}
		}
	}
	
	@Override
	public void setVisible(boolean aFlag) {
		
		if ( aFlag ) {
			
			int w = this.getDesktopPane().getWidth();
			int h = this.getDesktopPane().getHeight();
			
			this.setBounds(
					(w * 25 / 100),
					(h * 10 / 100),
					(w * 50 / 100),
					(h * 70 / 100));
		}
		
		super.setVisible(aFlag);
		this.moveToFront();
		
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
				
				this.removeWorkerButton.setEnabled(hasWorkers);
				this.abortWorkerButton.setEnabled(hasWorkers);
				
				this.workersUpdated = false;
				this.repaint();
			}
		}
	}
	
}
