package com.shimizukenta.secssimulator;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import com.shimizukenta.jsonhub.JsonHub;
import com.shimizukenta.jsonhub.JsonHubBuilder;
import com.shimizukenta.secs.CollectionProperty;
import com.shimizukenta.secs.PropertyChangeListener;
import com.shimizukenta.secssimulator.macro.MacroRecipe;

/**
 * This class is implements of MacroRecipePair pool, add/remove, change-listener.
 * 
 * @author kenta-shimizu
 *
 */
public class MacroRecipePairPool {
	
	private final CollectionProperty<MacroRecipePair> pairs = CollectionProperty.newSet();
	
	public MacroRecipePairPool() {
	}
	
	/**
	 * Clear all pairs.
	 * 
	 */
	public void clear() {
		pairs.clear();
	}
	
	/**
	 * Add MacroRecipePair to pair-pool.
	 * 
	 * @param pair
	 * @return {@code true} if add success
	 */
	public boolean add(MacroRecipePair pair) {
		return pairs.add(pair);
	}
	
	/**
	 * Add MacroRecipePairs to pair-pool.
	 * 
	 * @param pairs
	 * @return {@code true} if add success
	 */
	public boolean addAll(Collection<? extends MacroRecipePair> pairs) {
		return this.pairs.addAll(pairs);
	}
	
	/**
	 * Remove MacroRecipePair from pair-pool.
	 * 
	 * @param pair
	 * @return {@code true} if remove success
	 */
	public boolean remove(MacroRecipePair pair) {
		return pairs.remove(pair);
	}
	
	/**
	 * Remove MacroRecipePair from pair-pool by alias-name.
	 * 
	 * @param alias of SmlMessage
	 * @return {@code true} if remove success.
	 */
	public boolean remove(CharSequence alias) {
		if ( alias != null ) {
			final String a = alias.toString();
			return pairs.removeIf(pair -> {
				return pair.recipe().alias().equals(a);
			});
		}
		return false;
	}
	
	/**
	 * Add Change-Listener.
	 * 
	 * @param l as listener
	 * @return {@code true} if add success
	 */
	public boolean addChangeListener(PropertyChangeListener<? super Collection<? extends MacroRecipePair>> l) {
		return pairs.addChangeListener(l);
	}
	
	/**
	 * Remove Change-Listener.
	 * 
	 * @param l as listener
	 * @return {@code true} if remove success
	 */
	public boolean removeChangeListener(PropertyChangeListener<? super Collection<? extends MacroRecipePair>> l) {
		return pairs.removeChangeListener(l);
	}
	
	/**
	 * Returns Aliases of MacroRecipe.
	 * 
	 * @return aliases of MacroRecipe
	 */
	public List<String> aliases() {
		return pairs.stream()
				.map(a -> a.recipe())
				.map(a -> a.alias())
				.collect(Collectors.toList());
	}
	
	/**
	 * Returns Optionl has MacroRecipe if alias exist.
	 * 
	 * @param alias of MacroRecipe
	 * @return Optional has MacroRecipe if alias exist, otherwise {@code Optional.empty()}
	 */
	public Optional<MacroRecipe> optionalAlias(CharSequence alias) {
		
		if ( alias != null ) {
			
			final String s = alias.toString();
			
			return pairs.stream()
					.map(a -> a.recipe())
					.filter(a -> a.alias().equals(s))
					.findAny();
		}
		return Optional.empty();
	}
	
	/**
	 * Returns JsonHub of pairs.
	 * 
	 * @return JsonHub of pairs
	 */
	public JsonHub getJsonHub() {
		final JsonHubBuilder jhb = JsonHub.getBuilder();
		List<JsonHub> ll = pairs.stream()
				.filter(a -> Objects.nonNull(a.path()))
				.map(a -> {
					return jhb.object(
							jhb.pair("alias", a.recipe().alias()),
							jhb.pair("path", a.path().toAbsolutePath().normalize().toString())
							);
				})
				.collect(Collectors.toList());
		return jhb.array(ll);
	}

}
