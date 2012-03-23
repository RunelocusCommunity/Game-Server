package org.runetekk;

/**
 * An {@link Item} instance represents an in-game item.
 * 
 * @author Fabian M. <mail.fabianm@gmail.com>
 */
public class Item {
	
	/**
	 * The id of this {@link Item}.
	 */
	int id;
	
	/**
	 * The amount of items.
	 */
	int amount;

	/**
	 * Constructs a new {@link Item} instance.
	 */
	public Item() {
		this(0, 1);
	}

	/**
	 * Constructs a new {@link Item} instance.
	 * 
	 * @param id The id of this item.
	 */
	public Item(int id) {
		this(id, 1);
	}
	
	/**
	 * Constructs a new {@link Item} instance.
	 * 
	 * @param id The id of this item.
	 * @param amount The amount of items.
	 */
	public Item(int id, int amount) {
		if (amount < 0)
			return; // TODO Throw exception
		this.id = id;
		this.amount = amount;
	}
	
	/**
	 * Returns the id of this {@link Item}.
	 * 
	 * @return The id of this {@link Item}.
	 */
	public int getId() {
		return this.id;
	}
	
	/**
	 * Returns the amount of items.
	 * 
	 * @return The amount of items.
	 */
	public int getAmount() {
		return this.amount;
	}
	

}
