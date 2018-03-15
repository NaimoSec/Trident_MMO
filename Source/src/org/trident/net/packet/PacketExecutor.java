package org.trident.net.packet;

import org.trident.net.packet.impl.ButtonClickPacketListener;
import org.trident.net.packet.impl.ChangeAppearancePacketListener;
import org.trident.net.packet.impl.ChangeRelationStatusPacketListener;
import org.trident.net.packet.impl.ChangeVolumePacketListener;
import org.trident.net.packet.impl.ChatPacketListener;
import org.trident.net.packet.impl.ClientLoadingPacketListener;
import org.trident.net.packet.impl.CloseInterfacePacketListener;
import org.trident.net.packet.impl.CommandPacketListener;
import org.trident.net.packet.impl.DefaultPacketListener;
import org.trident.net.packet.impl.DialoguePacketListener;
import org.trident.net.packet.impl.DropItemPacketListener;
import org.trident.net.packet.impl.DuelAcceptancePacketListener;
import org.trident.net.packet.impl.DungeoneeringPartyInvitatationPacketListener;
import org.trident.net.packet.impl.EnterInputPacketListener;
import org.trident.net.packet.impl.EquipPacketListener;
import org.trident.net.packet.impl.ExamineItemPacketListener;
import org.trident.net.packet.impl.FinalizedMapRegionChangePacketListener;
import org.trident.net.packet.impl.FollowPlayerPacketListener;
import org.trident.net.packet.impl.IdleLogoutPacketListener;
import org.trident.net.packet.impl.ItemActionPacketListener;
import org.trident.net.packet.impl.ItemContainerActionPacketListener;
import org.trident.net.packet.impl.LendItemActionPacketListener;
import org.trident.net.packet.impl.MagicOnItemsPacketListener;
import org.trident.net.packet.impl.MagicOnPlayerPacketListener;
import org.trident.net.packet.impl.MovementPacketListener;
import org.trident.net.packet.impl.NPCOptionPacketListener;
import org.trident.net.packet.impl.ObjectActionPacketListener;
import org.trident.net.packet.impl.PickupItemPacketListener;
import org.trident.net.packet.impl.PlayerOptionPacketListener;
import org.trident.net.packet.impl.PlayerRelationPacketListener;
import org.trident.net.packet.impl.RegionChangePacketListener;
import org.trident.net.packet.impl.SendClanChatMessagePacketListener;
import org.trident.net.packet.impl.SilencedPacketListener;
import org.trident.net.packet.impl.SubmitReportPacketListener;
import org.trident.net.packet.impl.SwitchItemSlotPacketListener;
import org.trident.net.packet.impl.TradeAcceptancePacketListener;
import org.trident.net.packet.impl.UseItemPacketListener;
import org.trident.net.packet.impl.WithdrawMoneyFromPouchPacketListener;
import org.trident.world.entity.player.Player;

/**
 * Manages packets, their opcodes and their execution upon receiving them
 * from the player's channel handler.
 * 
 * @author relex lawl
 */

public class PacketExecutor {
	
	/**
	 * The packet listener arrays containing all valid packets.
	 */
	private static PacketListener[] packets = new PacketListener[256];
	
	/**
	 * Parses a Packet message received from channel handler.
	 * @param player	The player receiving the packet.
	 * @param packet	The packet to execute.
	 */
	public static void parse(Player player, Packet packet) {
		PacketListener packetListener = packets[packet.getOpcode()];
		if (packetListener == null)
			packetListener = new DefaultPacketListener();
		try {
			packetListener.execute(player, packet);
		} catch(Exception e) {
			e.printStackTrace();
			//World.deregister(player);
		}
	}
	
	static {
		for(int i = 0; i < packets.length; i++)
			packets[i] = new SilencedPacketListener();
		packets[4] = packets[230] = new ChatPacketListener();
		packets[41] = new EquipPacketListener();
		packets[87] = new DropItemPacketListener();
		packets[103] = new CommandPacketListener();	
		packets[121] = new FinalizedMapRegionChangePacketListener();	
		packets[130] = new CloseInterfacePacketListener();
		packets[185] = new ButtonClickPacketListener();
		packets[2] = new ExamineItemPacketListener();
		packets[5] = new SendClanChatMessagePacketListener();
		packets[6] = new ChangeVolumePacketListener();
		packets[7] = new WithdrawMoneyFromPouchPacketListener();
		packets[8] = new ChangeRelationStatusPacketListener();
		packets[9] = new LendItemActionPacketListener();
		packets[10] = new ClientLoadingPacketListener();
		packets[11] = new ChangeAppearancePacketListener();
		packets[12] = new DungeoneeringPartyInvitatationPacketListener();
		packets[13] = new SubmitReportPacketListener();
		packets[202] = new IdleLogoutPacketListener();
		packets[131] = new NPCOptionPacketListener();
		packets[17] = new NPCOptionPacketListener();
		packets[18] = new NPCOptionPacketListener();
		packets[21] = new NPCOptionPacketListener();
		packets[210] = new RegionChangePacketListener();
		packets[214] = new SwitchItemSlotPacketListener();
		packets[236] = new PickupItemPacketListener();
		packets[73] = new FollowPlayerPacketListener();
		packets[NPCOptionPacketListener.ATTACK_NPC] = packets[NPCOptionPacketListener.FIRST_CLICK_OPCODE] =
				new NPCOptionPacketListener();
		packets[EnterInputPacketListener.ENTER_SYNTAX_OPCODE] =
			packets[EnterInputPacketListener.ENTER_AMOUNT_OPCODE] = new EnterInputPacketListener();
		packets[UseItemPacketListener.ITEM_ON_GROUND_ITEM] = packets[UseItemPacketListener.ITEM_ON_ITEM] = 
				packets[UseItemPacketListener.ITEM_ON_NPC] = packets[UseItemPacketListener.ITEM_ON_OBJECT] = 
				packets[UseItemPacketListener.ITEM_ON_PLAYER] = new UseItemPacketListener();
		packets[UseItemPacketListener.USE_ITEM] = new UseItemPacketListener();
		packets[TradeAcceptancePacketListener.TRADE_OPCODE] = new TradeAcceptancePacketListener();
		packets[TradeAcceptancePacketListener.CHATBOX_TRADE_OPCODE] = new TradeAcceptancePacketListener();
		packets[DialoguePacketListener.DIALOGUE_OPCODE] = new DialoguePacketListener();
		packets[PlayerRelationPacketListener.ADD_FRIEND_OPCODE] = new PlayerRelationPacketListener();
		packets[PlayerRelationPacketListener.REMOVE_FRIEND_OPCODE] = new PlayerRelationPacketListener();
		packets[PlayerRelationPacketListener.ADD_IGNORE_OPCODE] = new PlayerRelationPacketListener();
		packets[PlayerRelationPacketListener.REMOVE_IGNORE_OPCODE] = new PlayerRelationPacketListener();
		packets[PlayerRelationPacketListener.SEND_PM_OPCODE] = new PlayerRelationPacketListener();
		packets[MovementPacketListener.COMMAND_MOVEMENT_OPCODE] = new MovementPacketListener();
		packets[MovementPacketListener.GAME_MOVEMENT_OPCODE] = new MovementPacketListener();
		packets[MovementPacketListener.MINIMAP_MOVEMENT_OPCODE] = new MovementPacketListener();
		packets[ObjectActionPacketListener.FIRST_CLICK] = packets[ObjectActionPacketListener.SECOND_CLICK] =
				packets[ObjectActionPacketListener.THIRD_CLICK] = packets[ObjectActionPacketListener.FOURTH_CLICK] =
				packets[ObjectActionPacketListener.FIFTH_CLICK] = new ObjectActionPacketListener();
		packets[ItemContainerActionPacketListener.FIRST_ITEM_ACTION_OPCODE] = packets[ItemContainerActionPacketListener.SECOND_ITEM_ACTION_OPCODE] = 
				packets[ItemContainerActionPacketListener.THIRD_ITEM_ACTION_OPCODE] = packets[ItemContainerActionPacketListener.FOURTH_ITEM_ACTION_OPCODE] =
				packets[ItemContainerActionPacketListener.FIFTH_ITEM_ACTION_OPCODE] = new ItemContainerActionPacketListener();
		packets[ItemActionPacketListener.SECOND_ITEM_ACTION_OPCODE] = packets[ItemActionPacketListener.THIRD_ITEM_ACTION_OPCODE] = packets[ItemActionPacketListener.FIRST_ITEM_ACTION_OPCODE] = new ItemActionPacketListener();
		packets[MagicOnItemsPacketListener.MAGIC_ON_ITEMS] = new MagicOnItemsPacketListener();
		packets[MagicOnItemsPacketListener.MAGIC_ON_GROUNDITEMS] = new MagicOnItemsPacketListener();
		packets[249] = new MagicOnPlayerPacketListener();
		packets[153] = new PlayerOptionPacketListener();
		packets[DuelAcceptancePacketListener.OPCODE] = new DuelAcceptancePacketListener();
		
	}
}
