package com.shimizukenta.secssimulator;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.shimizukenta.jsonhub.JsonHub;
import com.shimizukenta.jsonhub.JsonHubBuilder;
import com.shimizukenta.secs.CollectionProperty;
import com.shimizukenta.secs.PropertyChangeListener;
import com.shimizukenta.secs.sml.SmlMessage;

/**
 * This class is implements of SmlAliasPair pool, add/remove, change-listener.
 * 
 * @author kenta-shimizu
 *
 */
public class SmlAliasPairPool {
	
	private final CollectionProperty<SmlAliasPair> pairs = CollectionProperty.newSet();
	
	public SmlAliasPairPool() {
	}
	
	/**
	 * Clar all pairs.
	 * 
	 */
	public void clear() {
		pairs.clear();
	}
	
	/**
	 * Add SmlAliasPair to pair-pool.
	 * 
	 * @param pair
	 * @return {@code true} if add success
	 */
	public boolean add(SmlAliasPair pair) {
		return pairs.add(pair);
	}
	
	/**
	 * Add SmlAliasPair to pair-pool.
	 * 
	 * @param alias of SmlMessage
	 * @param sm is SmlMessage
	 * @return {@code true} if add success
	 */
	public boolean add(CharSequence alias, SmlMessage sm) {
		return add(new SmlAliasPair(alias, sm, null));
	}
	
	/**
	 * Add SmlAliasPairs to pair-pool.
	 * 
	 * @param pairs
	 * @return {@code true} if add success
	 */
	public boolean addAll(Collection<? extends SmlAliasPair> pairs) {
		return this.pairs.addAll(pairs);
	}
	
	/**
	 * Remove SmlAliasPair from pair-pool.
	 * 
	 * @param pair
	 * @return {@code true} if remove success
	 */
	public boolean remove(SmlAliasPair pair) {
		return pairs.remove(pair);
	}
	
	/**
	 * Remove SmlAliasPair from pair-pool by alias-name.
	 * 
	 * @param alias of SmlMessage
	 * @return {@code true} if remove success.
	 */
	public boolean remove(CharSequence alias) {
		if ( alias != null ) {
			final String a = alias.toString();
			return pairs.removeIf(pair -> {
				return pair.alias().equals(a);
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
	public boolean addChangeListener(PropertyChangeListener<? super Collection<? extends SmlAliasPair>> l) {
		return pairs.addChangeListener(l);
	}
	
	/**
	 * Remove Change-Listener.
	 * 
	 * @param l as listener
	 * @return {@code true} if remove success
	 */
	public boolean removeChangeListener(PropertyChangeListener<? super Collection<? extends SmlAliasPair>> l) {
		return pairs.removeChangeListener(l);
	}
	
	/**
	 * Returns Optionl has SmlMessage if alias exist.
	 * 
	 * @param alias of SmlMessage
	 * @return Optional has SmlMessage if alias exist, otherwise {@code Optional.empty()}
	 */
	public Optional<SmlMessage> optionalAlias(CharSequence alias) {
		
		if ( alias != null ) {
			
			final String a = alias.toString();
			
			return pairs.stream()
					.filter(p -> p.alias().equals(a))
					.map(p -> p.sml())
					.findAny();
		}
		return Optional.empty();
	}
	
	/**
	 * Returns Optional has SmlMessage if stream/function has only one.
	 * 
	 * @param strm of SmlMessage stream-number
	 * @param func of SmlMessage function-number
	 * @return Optional has SmlMessage if stream/funciton has only one, otherwise {@code Optional.empty()}
	 */
	public Optional<SmlMessage> optionalOnlyOneStreamFunction(int strm, int func) {
		
		Collection<SmlMessage> smls = pairs.stream()
				.map(p -> p.sml())
				.filter(sm -> sm.getStream() == strm)
				.filter(sm -> sm.getFunction() == func)
				.collect(Collectors.toList());
		
		if ( smls.size() == 1 ) {
			return smls.stream().findAny();
		}
		
		return Optional.empty();
	}
	
	/**
	 * Returns true if has reply-messsages.
	 * 
	 * @param strm Stream-Number
	 * @param func Funciton-Number
	 * @return {@code true} if has reply-messages
	 */
	public boolean hasReplyMessages(int strm, int func) {
		return pairs.stream()
				.map(p -> p.sml())
				.anyMatch(sm -> {
					return sm.getStream() == strm
							&& sm.getFunction() == func;
				});
	}
	
	/**
	 * Returns true if has reply-messages.
	 * 
	 * @param strm Stream-Number
	 * @return {@code true} if has reply-messages
	 */
	public boolean hasReplyMessages(int strm) {
		return pairs.stream()
				.map(p -> p.sml())
				.anyMatch(sm -> {
					return sm.getStream() == strm;
				});
	}
	
	/**
	 * Returns sorted Aliases of SmlMessage.
	 * 
	 * @return sorted aliases of SmlMessage
	 */
	public List<String> aliases() {
		return pairs.stream()
				.sorted()
				.map(p -> p.alias())
				.collect(Collectors.toList());
	}
	
	/**
	 * Returns JsonHub of pairs.
	 * 
	 * @return JsonHub of pairs
	 */
	public JsonHub getJsonHub() {
		final JsonHubBuilder jhb = JsonHub.getBuilder();
		List<JsonHub> ll = pairs.stream()
				.map(a -> {
					return jhb.object(
							jhb.pair("alias", a.alias()),
							jhb.pair("path", a.path().toAbsolutePath().toString())
							);
				})
				.collect(Collectors.toList());
		return jhb.array(ll);
	}
}
