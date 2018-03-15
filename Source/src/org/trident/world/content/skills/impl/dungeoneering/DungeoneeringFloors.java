package org.trident.world.content.skills.impl.dungeoneering;
import org.trident.model.GameObject;
import org.trident.model.Position;

/**
 * I couldn't be arsed to put all npc spawns in the enum.
 * @author Gabbe
 */
public enum DungeoneeringFloors {

	FIRST_FLOOR(new Position(2451, 4935), new Position(2448, 4939), new GameObject[] {new GameObject(45803, new Position(2455, 4940)), new GameObject(1767, new Position(2477, 4940))});

	DungeoneeringFloors(Position entrance, Position smuggler, GameObject[] objects) {
		this.entrance = entrance;
		this.smugglerPosition = smuggler;
		this.objects = objects;
	}

	private Position entrance, smugglerPosition;

	public Position getEntrance() {
		return entrance;
	}

	public Position getSmugglerPosition() {
		return smugglerPosition;
	}

	private GameObject[] objects;

	public GameObject[] getObjects() {
		return objects;
	}

	public static DungeoneeringFloors forId(int id) {
		for(DungeoneeringFloors floors : DungeoneeringFloors.values()) {
			if(floors != null && floors.ordinal() == id) {
				return floors;
			}
		}
		return null;
	}
}