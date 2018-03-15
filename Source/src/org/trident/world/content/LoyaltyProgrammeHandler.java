package org.trident.world.content;

import org.trident.model.Difficulty;
import org.trident.model.Flag;
import org.trident.model.Item;
import org.trident.util.Misc;
import org.trident.world.content.dialogue.Dialogue;
import org.trident.world.content.dialogue.DialogueExpression;
import org.trident.world.content.dialogue.DialogueManager;
import org.trident.world.content.dialogue.DialogueType;
import org.trident.world.entity.player.Player;

public class LoyaltyProgrammeHandler {

	public static void reset(Player p) {
		p.getAttributes().setLoyaltyProductSelected(new Object[] {"", 0, 0});
		p.getPacketSender().sendString(16522, "Selected:");
		p.getPacketSender().sendString(40522, "Selected:");
		p.getPacketSender().sendString(18454, "Selected:");
		p.getPacketSender().sendString(16482, ""+p.getPointsHandler().getLoyaltyProgrammePoints());
		p.getPacketSender().sendString(40482, ""+p.getPointsHandler().getLoyaltyProgrammePoints());
		p.getPacketSender().sendString(49474, ""+p.getPointsHandler().getLoyaltyProgrammePoints());
		p.getPacketSender().sendString(44482, ""+p.getPointsHandler().getLoyaltyProgrammePoints());
	}

	public static boolean handleShop(Player p, int actionButton) {
		int interfaceId = p.getAttributes().getInterfaceId();
		if(interfaceId == TITLE_SHOP_INTERFACE) { //Handle the title shop interface buttons
			if(p.getAttributes().getLoyaltyProductSelected() == null)
				p.getAttributes().setLoyaltyProductSelected(new Object[] {"", 0, 0});
			switch(actionButton) {
			case 17645:
				DialogueManager.start(p, 440);
				p.getAttributes().setDialogueAction(405);
				break;
			case 16452:
				p.getAttributes().getLoyaltyProductSelected()[0] = "Sir"; 
				p.getAttributes().getLoyaltyProductSelected()[1] = 1000;
				p.getAttributes().getLoyaltyProductSelected()[2] = 1;
				break;
			case 16454:
				p.getAttributes().getLoyaltyProductSelected()[0] = "Lady"; 
				p.getAttributes().getLoyaltyProductSelected()[1] = 1000;
				p.getAttributes().getLoyaltyProductSelected()[2] = 2;
				break;
			case 16456:
				p.getAttributes().getLoyaltyProductSelected()[0] = "King"; 
				p.getAttributes().getLoyaltyProductSelected()[1] = 3000;
				p.getAttributes().getLoyaltyProductSelected()[2] = 3;
				break;
			case 16458:
				p.getAttributes().getLoyaltyProductSelected()[0] = "Queen"; 
				p.getAttributes().getLoyaltyProductSelected()[1] = 5000;
				p.getAttributes().getLoyaltyProductSelected()[2] = 4;
				break;
			case 16460:
				p.getAttributes().getLoyaltyProductSelected()[0] = "Gangster"; 
				p.getAttributes().getLoyaltyProductSelected()[1] = 3000;
				p.getAttributes().getLoyaltyProductSelected()[2] = 5;
				break;
			case 16462:
				p.getAttributes().getLoyaltyProductSelected()[0] = "Peedo"; 
				p.getAttributes().getLoyaltyProductSelected()[1] = 8000;
				p.getAttributes().getLoyaltyProductSelected()[2] = 6;
				break;
			case 16464:
				p.getAttributes().getLoyaltyProductSelected()[0] = "Demon"; 
				p.getAttributes().getLoyaltyProductSelected()[1] = 10000;
				p.getAttributes().getLoyaltyProductSelected()[2] = 7;
				break;
			case 16466:
				p.getAttributes().getLoyaltyProductSelected()[0] = "Angel"; 
				p.getAttributes().getLoyaltyProductSelected()[1] = 10000;
				p.getAttributes().getLoyaltyProductSelected()[2] = 8;
				break;
			case 16468:
				p.getAttributes().getLoyaltyProductSelected()[0] = "Unstoppable"; 
				p.getAttributes().getLoyaltyProductSelected()[1] = 15000;
				p.getAttributes().getLoyaltyProductSelected()[2] = 9;
				break;
			case 16470:
				p.getAttributes().getLoyaltyProductSelected()[0] = "Nerd";
				p.getAttributes().getLoyaltyProductSelected()[1] = 20000;
				p.getAttributes().getLoyaltyProductSelected()[2] = 10;
				break;
			case 16472:
				p.getAttributes().getLoyaltyProductSelected()[0] = "Psychopath"; 
				p.getAttributes().getLoyaltyProductSelected()[1] = 25000;
				p.getAttributes().getLoyaltyProductSelected()[2] = 11;
				break;
			case 16474:
				p.getAttributes().getLoyaltyProductSelected()[0] = "Immortal"; 
				p.getAttributes().getLoyaltyProductSelected()[1] = 30000;
				p.getAttributes().getLoyaltyProductSelected()[2] = 12;
				break;
			case 16518:
			case 16512:
				reset(p);
				p.getPacketSender().sendInterfaceRemoval();
				break;
			case 16515:
				reset(p);
				p.getPacketSender().sendInterface(COSTUME_SHOP_INTERFACE);
				break;
			case 16517:
				reset(p);
			//	p.getPacketSender().sendInterface(SPINS_SHOP_INTERFACE);
				break;
			case 16516:
				reset(p);
				p.getPacketSender().sendInterface(RECOLOUR_SHOP_INTERFACE);
				break;
			case 16510:
				if(((String) p.getAttributes().getLoyaltyProductSelected()[0]).length() > 0 && (int) p.getAttributes().getLoyaltyProductSelected()[1] > 0 && (int) p.getAttributes().getLoyaltyProductSelected()[2] > 0) {
					if(p.getAttributes().getLoyaltyTitle() == (int)p.getAttributes().getLoyaltyProductSelected()[2]) {
						p.getPacketSender().sendMessage("You already own this title.");
						return true;
					}
					if(p.getPointsHandler().getLoyaltyProgrammePoints() < (int) p.getAttributes().getLoyaltyProductSelected()[1]) {
						p.getPacketSender().sendMessage("You need at least "+p.getAttributes().getLoyaltyProductSelected()[1]+" Loyalty points to buy this title.");
						return true;
					}
					p.getPointsHandler().setLoyaltyProgrammePoints(-(int) p.getAttributes().getLoyaltyProductSelected()[1], true);
					p.getAttributes().setLoyaltyTitle((int) p.getAttributes().getLoyaltyProductSelected()[2]);
					p.getUpdateFlag().flag(Flag.APPEARANCE);
					p.getPointsHandler().refreshPanel();
					p.getPacketSender().sendMessage("You have bought the title '"+(String) p.getAttributes().getLoyaltyProductSelected()[0]+"'.");
					reset(p);
					return true;
				} else 
					p.getPacketSender().sendMessage("Please select a valid option first.");
				break;
			}
			String s = ((String)p.getAttributes().getLoyaltyProductSelected()[0]).length() > 0 ? ""+p.getAttributes().getLoyaltyProductSelected()[0]+" ("+p.getAttributes().getLoyaltyProductSelected()[1]+")" : "";
			p.getPacketSender().sendString(16522, "Selected: "+s);
			return true;
		} else if(interfaceId == COSTUME_SHOP_INTERFACE) {
			switch(actionButton) {
			case 17646:
				DialogueManager.start(p, 440);
				p.getAttributes().setDialogueAction(405);
				break;
			case -25084:
				p.getAttributes().getLoyaltyProductSelected()[0] = "Frog"; 
				p.getAttributes().getLoyaltyProductSelected()[1] = 1000;
				p.getAttributes().getLoyaltyProductSelected()[2] = 1;
				break;
			case -25082:
				p.getAttributes().getLoyaltyProductSelected()[0] = "Mime"; 
				p.getAttributes().getLoyaltyProductSelected()[1] = 1000;
				p.getAttributes().getLoyaltyProductSelected()[2] = 2;
				break;
			case -25080:
				p.getAttributes().getLoyaltyProductSelected()[0] = "Zombie"; 
				p.getAttributes().getLoyaltyProductSelected()[1] = 2000;
				p.getAttributes().getLoyaltyProductSelected()[2] = 3;
				break;
			case -25078:
				p.getAttributes().getLoyaltyProductSelected()[0] = "Warlock"; 
				p.getAttributes().getLoyaltyProductSelected()[1] = 3000;
				p.getAttributes().getLoyaltyProductSelected()[2] = 4;
				break;
			case -25076:
				p.getAttributes().getLoyaltyProductSelected()[0] = "Jester"; 
				p.getAttributes().getLoyaltyProductSelected()[1] = 3000;
				p.getAttributes().getLoyaltyProductSelected()[2] = 5;
				break;
			case -25074:
				p.getAttributes().getLoyaltyProductSelected()[0] = "Skeleton"; 
				p.getAttributes().getLoyaltyProductSelected()[1] = 5000;
				p.getAttributes().getLoyaltyProductSelected()[2] = 6;
				break;
			case -25072:
				p.getAttributes().getLoyaltyProductSelected()[0] = "Sled (Item)"; 
				p.getAttributes().getLoyaltyProductSelected()[1] = 7500;
				p.getAttributes().getLoyaltyProductSelected()[2] = 7;
				break;
			case -25070:
				p.getAttributes().getLoyaltyProductSelected()[0] = "Basket (Item)"; 
				p.getAttributes().getLoyaltyProductSelected()[1] = 7500;
				p.getAttributes().getLoyaltyProductSelected()[2] = 8;
				break;
			case -25068:
				p.getAttributes().getLoyaltyProductSelected()[0] = "Witchdoctor"; 
				p.getAttributes().getLoyaltyProductSelected()[1] = 13000;
				p.getAttributes().getLoyaltyProductSelected()[2] = 9;
				break;
			case -25066:
				p.getAttributes().getLoyaltyProductSelected()[0] = "Santa"; 
				p.getAttributes().getLoyaltyProductSelected()[1] = 15000;
				p.getAttributes().getLoyaltyProductSelected()[2] = 10;
				break;
			case -25064:
				p.getAttributes().getLoyaltyProductSelected()[0] = "Reaper Hood (Item)"; 
				p.getAttributes().getLoyaltyProductSelected()[1] = 15000;
				p.getAttributes().getLoyaltyProductSelected()[2] = 11;
				break;
			case -25062:
				p.getAttributes().getLoyaltyProductSelected()[0] = "Investigator"; 
				p.getAttributes().getLoyaltyProductSelected()[1] = 17500;
				p.getAttributes().getLoyaltyProductSelected()[2] = 12;
				break;
			case -25026:
				if(((String) p.getAttributes().getLoyaltyProductSelected()[0]).length() > 0 && (int) p.getAttributes().getLoyaltyProductSelected()[1] > 0 && (int) p.getAttributes().getLoyaltyProductSelected()[2] > 0) {
					if(p.getPointsHandler().getLoyaltyProgrammePoints() < (int) p.getAttributes().getLoyaltyProductSelected()[1]) {
						p.getPacketSender().sendMessage("You need at least "+p.getAttributes().getLoyaltyProductSelected()[1]+" Loyalty points to buy this title.");
						return true;
					}
					Costume costume = Costume.forId((int) p.getAttributes().getLoyaltyProductSelected()[2]-1);
					if(p.getInventory().getFreeSlots() < costume.getItems().length) {
						p.getPacketSender().sendMessage("You need at least "+costume.getItems().length+" free inventory slots to purchase this.");
						return true;
					}
					p.getPointsHandler().setLoyaltyProgrammePoints(-(int) p.getAttributes().getLoyaltyProductSelected()[1], true);
					for(Item item : costume.getItems())
						p.getInventory().add(item);
					p.getInventory().refreshItems();
					p.getPointsHandler().refreshPanel();
					p.getPacketSender().sendMessage("Purchase successful.");
					reset(p);
					return true;
				} else 
					p.getPacketSender().sendMessage("Please select a valid option first.");
				break;
			case -25024:
			case -25018:
				reset(p);
				p.getPacketSender().sendInterfaceRemoval();
				break;
			case 40514:
				reset(p);
				p.getPacketSender().sendInterface(TITLE_SHOP_INTERFACE);
				break;
			case -25019:
				reset(p);
			//	p.getPacketSender().sendInterface(SPINS_SHOP_INTERFACE);
				break;
			case 40516:
				reset(p);
				p.getPacketSender().sendInterface(RECOLOUR_SHOP_INTERFACE);
				break;
			}
			String s = ((String)p.getAttributes().getLoyaltyProductSelected()[0]).length() > 0 ? ""+p.getAttributes().getLoyaltyProductSelected()[0]+" ("+p.getAttributes().getLoyaltyProductSelected()[1]+")" : "";
			p.getPacketSender().sendString(40522, "Selected: "+s);
			return true;
		} else if(interfaceId == SPINS_SHOP_INTERFACE) {
			switch(actionButton) {
			case -16048:
				reset(p);
				p.getPacketSender().sendInterface(TITLE_SHOP_INTERFACE);
				break;
			case -16047:
				reset(p);
				p.getPacketSender().sendInterface(COSTUME_SHOP_INTERFACE);
				break;
			case -16084:

				break;
			default:
				reset(p);
				break;
			}
		} else if(interfaceId == RECOLOUR_SHOP_INTERFACE) {
			switch(actionButton) {
			case 17647:
				DialogueManager.start(p, 440);
				p.getAttributes().setDialogueAction(405);
				break;
			case 44514:
				reset(p);
				p.getPacketSender().sendInterface(TITLE_SHOP_INTERFACE);
				break;
			case 44515:
				reset(p);
				p.getPacketSender().sendInterface(COSTUME_SHOP_INTERFACE);
				break;
			case -21018:
				reset(p);
				p.getPacketSender().sendInterfaceRemoval();
				break;
			case -21084:
				p.getAttributes().setLoyaltyProductSelected(new Object[] {Recolour.DARK_BOW, getCurrentItem(p, Recolour.DARK_BOW), 2000});
				break;
			case -21082:
				p.getAttributes().setLoyaltyProductSelected(  new Object[] {Recolour.ABYSSAL_WHIP, getCurrentItem(p, Recolour.ABYSSAL_WHIP), 2000});
				break;
			case -21080:
				p.getAttributes().setLoyaltyProductSelected( new Object[] {Recolour.ROBIN_HOOD_HAT, getCurrentItem(p, Recolour.ROBIN_HOOD_HAT), 3000});
				break;
			case -21078:
				p.getAttributes().setLoyaltyProductSelected( new Object[] {Recolour.INFINITY_TOP, getCurrentItem(p, Recolour.INFINITY_TOP), 4000});
				break;
			case -21076:
				p.getAttributes().setLoyaltyProductSelected( new Object[] {Recolour.INFINITY_BOTTOMS, getCurrentItem(p, Recolour.INFINITY_BOTTOMS), 4000});
				break;
			case -21074:
				p.getAttributes().setLoyaltyProductSelected( new Object[] {Recolour.STAFF_OF_LIGHT, getCurrentItem(p, Recolour.STAFF_OF_LIGHT), 5000});
				break;
			case -21026:
				if(p.getAttributes().getLoyaltyProductSelected()[0] == null || !(p.getAttributes().getLoyaltyProductSelected()[0] instanceof Recolour))
					return true;
				if((int) p.getAttributes().getLoyaltyProductSelected()[2] < 0)
					return true;
				recolurDialogue(p, (int)p.getAttributes().getLoyaltyProductSelected()[2], (Recolour)p.getAttributes().getLoyaltyProductSelected()[0], (Item) p.getAttributes().getLoyaltyProductSelected()[1]);
				break;
			}
			if(p.getAttributes().getLoyaltyProductSelected()[0] == null || !(p.getAttributes().getLoyaltyProductSelected()[0] instanceof Recolour))
				return false;
			String recolour = ((Recolour) p.getAttributes().getLoyaltyProductSelected()[0]).toString();
			p.getPacketSender().sendString(18454, "Selected: "+recolour);
		}
		return false;
	}

	/*
	 * Recolouring ,gets the current recolored item you have
	 */
	public static Item getCurrentItem(Player player, Recolour recolour) {
		Item item = null;
		for(Item items : recolour.getItems()) {
			if(player.getInventory().contains(items.getId()) && items.getId() > 0) {
				item = items;
				break;
			}
		}
		return item;
	}

	/*
	 * Opens the recolour dialogue with options to choose
	 */
	public static void recolurDialogue(Player player, int lp, final Recolour recolour, Item currentItem) {
		if(recolour == null)
			return;
		if(player.getPointsHandler().getLoyaltyProgrammePoints() < lp) {
			player.getPacketSender().sendMessage("You need at least "+lp+" Loyalty points to do this.");
			return;
		}
		if(currentItem == null) {
			player.getPacketSender().sendMessage("You do not have "+Misc.anOrA(recolour.toString())+" "+recolour.toString()+" in your inventory.");
			return;
		}
		player.getAttributes().setLoyaltyProductSelected( new Object[] {(Recolour) recolour, (Item) currentItem, (int) lp});
		player.getAttributes().setDialogueAction(300);
		DialogueManager.start(player, new Dialogue() {

			@Override
			public DialogueType type() {
				return DialogueType.OPTION;
			}

			@Override
			public int npcId() {
				return -1;
			}

			@Override
			public DialogueExpression animation() {
				return null;
			}

			@Override
			public String[] dialogue() {
				return recolour.getOptions();
			}
		});
	}

	public static void recolourItem(Player player, int optionIndex) {
		try {
			player.getPacketSender().sendInterfaceRemoval();
			Recolour recolour = (Recolour) player.getAttributes().getLoyaltyProductSelected()[0];
			Item currentItem = (Item) player.getAttributes().getLoyaltyProductSelected()[1];
			if(currentItem == null || !player.getInventory().contains(currentItem.getId())) {
				player.getPacketSender().sendMessage("You don't have a "+recolour.toString()+" to color.");
				return;
			}
			int cost = (int) player.getAttributes().getLoyaltyProductSelected()[2];
			Item recolourItem = recolour.getItems()[optionIndex];
			if(recolourItem == null || recolourItem.getId() < 0)
				return;
			if(player.getPointsHandler().getLoyaltyProgrammePoints() < cost) {
				player.getPacketSender().sendMessage("You need at least "+cost+" Loyalty points to do this.");
				return;
			}
			if(currentItem.getId() == recolourItem.getId()) {
				player.getPacketSender().sendMessage("Your "+recolour.toString()+" already has this color.");
				return;
			}
			player.getPointsHandler().setLoyaltyProgrammePoints(-cost, true);
			player.getInventory().delete(currentItem);
			player.getInventory().add(recolourItem);
			player.getPointsHandler().refreshPanel();
		} catch(Exception e) {
			player.getPacketSender().sendInterfaceRemoval();
			player.getPacketSender().sendMessage("An error occured. Please try again later.");
			e.printStackTrace();
		}
		//reset(player);
	}
	
	public static void givePoints(Player player) {
		if(player.getAttributes().getInactiveTimer() < 15 && player.getDifficulty() != null) {
			int gain = player.getDifficulty() == Difficulty.EASY ? 50 : player.getDifficulty() == Difficulty.NORMAL ? 100 : 150;
			player.getPacketSender().sendMessage("You've just received "+gain+" Loyalty points. Continue to play for more!");
			player.getPointsHandler().setLoyaltyProgrammePoints(gain, true);
			player.getPacketSender().sendString(16482, ""+player.getPointsHandler().getLoyaltyProgrammePoints());
			player.getPacketSender().sendString(40482, ""+player.getPointsHandler().getLoyaltyProgrammePoints());
		}
	}

	public static final int TITLE_SHOP_INTERFACE = 16450;
	private static final int COSTUME_SHOP_INTERFACE = 40450;
	private static final int SPINS_SHOP_INTERFACE = 49450;
	private static final int RECOLOUR_SHOP_INTERFACE = 44450;

	public static enum Recolour {
		DARK_BOW(new Item[] {new Item(11235), new Item(15703), new Item(15702), new Item(15701), new Item(15704)}, new String[] {"Normal Dark Bow", "White Dark Bow", "Blue Dark Bow", "Yellow Dark Bow", "Green Dark Bow"}),
		ABYSSAL_WHIP(new Item[] {new Item(4151), new Item(15441), new Item(15442), new Item(15443), new Item(-1)}, new String[] {"Normal Abyssal Whip", "Yellow Abyssal Whip", "Blue Abyssal Whip", "White Abyssal Whip", ""}),
		
		ROBIN_HOOD_HAT(new Item[] {new Item(2581), new Item(20950), new Item(20951), new Item(20952), new Item(20949)}, new String[] {"Normal Robin Hood Hat", "Yellow Robin Hood Hat", "Blue Robin Hood Hat", "White Robin Hood Hat", "Red Robin Hood Hat"}),
		
		INFINITY_TOP(new Item[] {new Item(6916), new Item(15600), new Item(15606), new Item(15612), new Item(15618)}, new String[] {"Normal Infinity Top", "White Infinity Top", "Blue Infinity Top", "Red Infinity Top", "Brown Infinity Top"}),
		INFINITY_BOTTOMS(new Item[] {new Item(6924), new Item(15604), new Item(15610), new Item(15616), new Item(15622)}, new String[] {"Normal Infinity Bottoms", "White Infinity Bottoms", "Blue Infinity Bottoms", "Red Infinity Bottoms", "Brown Infinity Bottoms"}),
		
		STAFF_OF_LIGHT(new Item[] {new Item(15486), new Item(21003), new Item(21000), new Item(21001), new Item(21002)}, new String[] {"Normal Staff of Light", "Yellow Staff of Light", "Blue Staff of Light", "Red Staff of Light", "Green Staff of Light"});
		
		Recolour(Item[] items, String options[]) {
			this.items = items;
			this.options = options;
		}

		private Item[] items;

		public Item[] getItems() {
			return items;
		}

		private String[] options;

		public String[] getOptions() {
			return options;
		}

		public static Recolour forId(int id) {
			for(Recolour recolour : Recolour.values()) {
				if(recolour.ordinal() == id)
					return recolour;
			}
			return null;
		}

		@Override
		public String toString() {
			return Misc.formatText(this.name().toLowerCase());
		}
	}

	public static enum Costume {
		FROG(new Item[] {new Item(6188), new Item(10954), new Item(10956), new Item(10958)}),
		MIME(new Item[] {new Item(3057), new Item(3058), new Item(3059), new Item(3060), new Item(3061)}),
		ZOMBIE(new Item[] {new Item(7594), new Item(7592), new Item(7593), new Item(7595), new Item(7596)}),
		WARLOCK(new Item[] {new Item(14076), new Item(14077), new Item(14081)}),
		JESTER(new Item[] {new Item(10840), new Item(10836), new Item(6858), new Item(6859), new Item(10837), new Item(10838), new Item(10839)}),
		SKELETON(new Item[] {new Item(9925), new Item(9924), new Item(9923), new Item(9922), new Item(9921)}),
		SLED(new Item[] {new Item(4084)}),
		BASKET(new Item[] {new Item(4565)}),
		WITCHDOCTOR(new Item[] {new Item(20046), new Item(20044), new Item(20045)}),
		SANTA(new Item[]{new Item(1050), new Item(14595), new Item(14603), new Item(14602), new Item(14605)}),
		GRIM_REAPER(new Item[]{new Item(11789)}),
		INVESTIGATOR(new Item[]{new Item(19708), new Item(19706), new Item(19707)});

		Costume(Item[] items) {
			this.items = items;
		}

		private Item[] items;

		private Item[] getItems() {
			return items;
		}

		public static Costume forId(int id) {
			for(Costume costume : Costume.values()) {
				if(costume.ordinal() == id)
					return costume;
			}
			return null;
		}
	}
}
