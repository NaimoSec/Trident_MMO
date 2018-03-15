package org.trident.model;

import org.trident.world.entity.Entity;


/**
 * Represents a projectile being shot from one position to specified
 * destination.
 * 
 * @author relex lawl
 */

public class Projectile extends Entity {

	/**
	 * The Projectile constructor.
	 * @param position		The starting projectile position.
	 * @param destination	The projectile's destination.
	 * @param graphic		The graphic of the projectile.
	 */
	public Projectile(Position position, Position destination, Graphic graphic) {
		super(position);
		this.destination = destination;
		this.graphic = graphic;
		this.delay = 0;
		this.angle = 50;
		this.speed = 5;
	}
	
	/**
	 * The Projectile constructor.
	 * @param position		The starting projectile position.
	 * @param destination	The projectile's destination.
	 * @param graphic		The graphic of the projectile.
	 * @param angle			The angle the projectile will have.
	 * @param speed			The velocity of the projectile.
	 */
	public Projectile(Position position, Position destination, Graphic graphic, int delay, int angle, int speed) {
		super(position);
		this.destination = destination;
		this.graphic = graphic;
		this.delay = delay;
		this.angle = angle;
		this.speed = speed;
	}
	
	/**
	 * The Projectile constructor.
	 * @param position		The starting projectile position.
	 * @param destination	The projectile's destination.
	 * @param graphic		The graphic of the projectile.
	 * @param angle			The angle the projectile will have.
	 * @param speed			The velocity of the projectile.
	 * @param slope			The slope of the projectile.
	 */
	public Projectile(Position position, Position destination, Graphic graphic, int delay, int angle, int speed, int slope) {
		super(position);
		this.destination = destination;
		this.graphic = graphic;
		this.delay = delay;
		this.angle = angle;
		this.speed = speed;
		this.slope = slope;
	}
	
	/**
	 * The starting projectile's destination.
	 */
	private final Position destination;
	
	/**
	 * The graphic of the projectile.
	 */
	private final Graphic graphic;
	
	/**
	 * The delay before showing the projectile.
	 */
	private int delay;
	
	/**
	 * The angle the projectile will have.
	 */
	private final int angle;
	
	/**
	 * The velocity of the projectile.
	 */
	private final int speed;
	
	/**
	 * The slope of the projectile
	 */
	private int slope = 16;
	
	/**
	 * Gets the destination/final pinpoint the projectile
	 * will reach.
	 * @return	The destination.
	 */
	public Position getDestination() {
		return destination;
	}
	
	/**
	 * Gets the graphic the projectile will be representing.
	 * @return	The graphic to show as a projectile.
	 */
	public Graphic getGraphic() {
		return graphic;
	}
	
	/**
	 * Gets the angle of the projectile.
	 * @return	The projectile's shown angle.
	 */
	public int getAngle() {
		return angle;
	}
	
	/**
	 * Gets the speed of the projectile.
	 * @return	The velocity of the projectile.
	 */
	public int getSpeed() {
		return speed;
	}

	/**
	 * Gets the delay of the projectile.
	 * @return	The projectile's display delay.
	 */
	public int getDelay() {
		return delay;
	}
	
	/**
	 * Gets the slope of the projectile.
	 * @return	The projectile's slope.
	 */
	public int getSlope() {
		return slope;
	}

	public void setDelay(int i) {
		this.delay = i;
	}
}
