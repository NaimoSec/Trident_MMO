package org.trident.world.entity;

import java.util.AbstractCollection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class EntityContainer<E extends Entity> extends AbstractCollection<E> {
	
	/** The maximum amount of elements that can be in this container. */
	private int capacity;
	/** The current amount of elements that are in this container. */
	private int size;
	/** The actual elements that are in this container. */
	private E[] elements;
	/**
	 * Create a new {@link EntityContainer} with the specified capacity.
	 *
	 * @param capacity
	 * the maximum amount of entities this container is allowed to
	 * hold.
	 */
	@SuppressWarnings("unchecked")
	public EntityContainer(int capacity) {
		this.capacity = capacity + 1;
		this.size = 0;
		this.elements = (E[]) new Entity[capacity + 1];
	}
	
	@Override
	public boolean add(E e) {
		// Determine the next free slot and validate it.
		int slot = determineSlot();
		if (slot == -1) {
			System.out.println("Could not add entity "+e+" to the world.");
			return false;
		}
		// The slot has passed the checks, so add the entity to that slot.
		elements[slot] = Objects.requireNonNull(e);
		elements[slot].setIndex(slot);
		size++;
		return true;
	}
	
	@Override
	public boolean contains(Object o) {
		// Validate the argued Object.
		if (!(o instanceof Entity))
			return false;
		// Determine if the entity is in this container.
		Entity e = (Entity) o;
		return !slotFree(e.getIndex());
	}
	
	@Override
	public boolean remove(Object o) {
		// Validate the argued Object.
		if (o == null)
			throw new NullPointerException();
		if (!(o instanceof Entity))
			return false;
		// Remove the entity from the container.
		Entity e = (Entity) o;
		if (!slotFree(e.getIndex())) {
			//elements[e.getIndex()].setUnregistered(true);
			elements[e.getIndex()] = null;
			size--;
			return true;
		}
		return false;
	}
	
	/**
	 * Retrieves the element on the argued slot.
	 *
	 * @param slot
	 * the slot to retrieve the element on.
	 * @return the element on the slot, or <code>null</code> if no element is on
	 * the slot.
	 */
	public E get(int slot) {
		return elements[slot];
	}
	/**
	 * Determines if the argued slot is free, meaning it currently has no
	 * elements on it.
	 *
	 * @param slot
	 * the slot to check is free or not.
	 * @return <code>true</code> if the slot is free, <code>false</code>
	 * otherwise.
	 */
	public boolean slotFree(int slot) {
		return elements[slot] == null;
	}
	@Override
	public Object[] toArray() {
		throw new UnsupportedOperationException(
				"Access to the backing array is denied!");
	}
	@Override
	public <T> T[] toArray(T[] a) {
		throw new UnsupportedOperationException(
				"Access to the backing array is denied!");
	}
	@SuppressWarnings("unchecked")
	@Override
	public void clear() {
		elements = (E[]) new Entity[capacity];
		size = 0;
	}
	@Override
	public int size() {
		return size;
	}
	/**
	 * The maximum amount of elements that can be in this container. This method
	 * returns an amount equal to the length of {@link #elements}.
	 *
	 * @return the maximum amount of elements that can be in this container.
	 */
	public int capacity() {
		return elements.length;
	}
	/**
	 * Iterates through the array of elements to determine the index of the
	 * first free slot found.
	 *
	 * @return the index of the free slot, or <tt>-1</tt> if the container is
	 * full.
	 */
	private int determineSlot() {
		for (int slot = 1; slot < elements.length; slot++) {
			if (slotFree(slot)) {
				return slot;
			}
		}
		return -1;
	}
	/**
	 * Gets the amount of free slots left in this container. This method returns
	 * a value equal to subtracting the {@link #capacity} by the {@link #size}.
	 *
	 * @return the amount of free slots left in this container.
	 */
	public int remainingSize() {
		return capacity() - size();
	}
	/**
	 * Determines if this container cannot accept anymore elements.
	 *
	 * @return <code>true</code> if this container is full, <code>false</code>
	 * otherwise.
	 */
	public boolean isFull() {
		return size >= capacity;
	}
	/**
	 * {@inheritDoc}
	 *
	 * This implementation automatically excludes all elements with a value of
	 * <code>null</code>.
	 */
	@Override
	public void forEach(Consumer<? super E> action) {
		for (E e : elements) {
			if (e == null)
				continue;
			action.accept(e);
		}
	}
	/**
	 * Iterates through the backing array and finds the first element that
	 * matches the argued {@link Predicate}.
	 *
	 * @param p
	 * the predicate that will be used to find the element.
	 * @return the optional representing the found element.
	 */
	public Optional<E> search(Predicate<? super E> p) {
		for (E e : elements) {
			if (p.test(e))
				return Optional.ofNullable(e);
		}
		return Optional.empty();
	}
	/**
	 * {@inheritDoc}
	 *
	 * This is a fail-safe iterator implementation, meaning that modification of
	 * the collection while performing an enhanced loop will not throw a
	 * {@link ConcurrentModificationException}.
	 */
	@Override
	public Iterator<E> iterator() {
		return new Iterator<E>() {
			/** The current index we are iterating on. */
			private int currentIndex;
			/** The last index we iterated over. */
			private int lastElementIndex = -1;
			@Override
			public boolean hasNext() {
				return !(currentIndex + 1 > elements.length);
			}
			@Override
			public E next() {
				if (currentIndex >= elements.length) {
					throw new ArrayIndexOutOfBoundsException(
							"Can only call 'next()' in amount to 'backingArray.length'.");
				}
				int i = currentIndex;
				currentIndex++;
				return elements[lastElementIndex = i];
			}
			@Override
			public void remove() {
				if (lastElementIndex < 0) {
					throw new IllegalStateException(
							"Can only call 'remove()' once in call to 'next()'.");
				}
				EntityContainer.this.remove(elements[lastElementIndex]);
				currentIndex = lastElementIndex;
				lastElementIndex = -1;
			}
		};
	}
}