package net.unethicalite.plugins.slayChaosAlter;

import net.runelite.api.*;
import net.runelite.api.events.ClientTick;
import net.runelite.client.chat.ChatColorType;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.PluginDescriptor;
import net.unethicalite.api.commons.Time;
import net.unethicalite.api.entities.Players;
import net.unethicalite.api.entities.TileItems;
import net.unethicalite.api.entities.TileObjects;
import net.unethicalite.api.game.Combat;
import net.unethicalite.api.game.Game;
import net.unethicalite.api.game.Worlds;
import net.unethicalite.api.input.Keyboard;
import net.unethicalite.api.items.Bank;
import net.unethicalite.api.items.Equipment;
import net.unethicalite.api.items.Inventory;
import net.unethicalite.api.movement.Movement;
import net.unethicalite.api.movement.Reachable;
import net.unethicalite.api.packets.DialogPackets;
import net.unethicalite.api.plugins.LoopedPlugin;
import net.unethicalite.api.utils.MessageUtils;
import net.unethicalite.api.widgets.Dialog;
import org.pf4j.Extension;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

@Extension
@PluginDescriptor(name = "slayChaosAlter", description = "auto chaotic boner", enabledByDefault = false)
public class slayChaosAlter extends LoopedPlugin {
	@Inject
	Client client;

	@Override
	protected int loop() {
		int wildyLevel = Game.getWildyLevel();
		Player local = Players.getLocal();
		int combatLevel = local.getCombatLevel();
		Player pker = Players.getNearest(player -> player != local && isDangerousPlayer(wildyLevel, combatLevel, player));
		if (client.getGameState() != GameState.LOGGED_IN || local == null) {
			return -1;
		}

		TileObject altar = TileObjects.getNearest(411);
		Item bones = Inventory.getFirst(ItemID.DRAGON_BONES);

		if (Game.getWildyLevel() > 0) {

			TileItem wine = TileItems.getNearest(245);
			System.out.println(wine.getId());
			if (!Inventory.contains(ItemID.DRAGON_BONES) && Reachable.isInteractable(wine)) {
				wine.interact("Take");
			}

			if ((altar == null && !Movement.isWalking()) || (!Reachable.isInteractable(altar) && !Movement.isWalking())) {
				MessageUtils.addMessage("Walking to altar", ChatColorType.HIGHLIGHT);
				Movement.walkTo(2956, 3821);
			}

			if (canUseBonesOnAltar(bones, altar, local, pker)) {
				MessageUtils.addMessage("Using bones on altar", ChatColorType.HIGHLIGHT);
				bones.useOn(altar);
			}

		} else if (Inventory.contains(ItemID.DRAGON_BONES) && isWearingROD() && isWearingBurningAmulet()) {
			Item burningAmulet = Equipment.fromSlot(EquipmentInventorySlot.AMULET);
			burningAmulet.interact("Lava Maze");
			Time.sleepTick();
			Keyboard.type(1);
			Time.sleepTicks(4);
		}
		// banking
		List<Integer> ringOfDuelingIds = Arrays.asList(
				ItemID.RING_OF_DUELING8, ItemID.RING_OF_DUELING7, ItemID.RING_OF_DUELING6,
				ItemID.RING_OF_DUELING5, ItemID.RING_OF_DUELING4, ItemID.RING_OF_DUELING3,
				ItemID.RING_OF_DUELING2, ItemID.RING_OF_DUELING1
		);
		Item ringInInventory = Inventory.getFirst(item -> ringOfDuelingIds.contains(item.getId()));
		Item ringEquipped = Equipment.fromSlot(EquipmentInventorySlot.RING);
		Item burningAmulet = Inventory.getFirst(ItemID.BURNING_AMULET5);

		Item ringInventory = Inventory.getFirst(item -> ringOfDuelingIds.contains(item.getId()));
		System.out.println("Ring is " + ringInventory);

		if (ringInInventory != null && (ringEquipped == null || (ringEquipped != null ) && !ringOfDuelingIds.contains(ringEquipped.getId()))) {
			ringInInventory.interact("Wear");
		}
		if ((burningAmulet != null) && Inventory.contains(burningAmulet.getId())) {
			burningAmulet.interact("Wear");
		}

		//Opening bank
		TileObject bank = TileObjects.getNearest("Bank chest");
		System.out.println("Bank is " + bank);
		if (bank != null) {
			if (!Bank.isOpen()) {
				if (!isWearingROD() || (!Inventory.isFull() || !isWearingBurningAmulet())) {
					bank.interact("Use");
					Time.sleepTicksUntil(() -> Bank.isOpen(), 1);
				}

			}
			//withdrawing items
			if (Bank.contains(ItemID.BURNING_AMULET5) && !Inventory.isFull() && !isWearingBurningAmulet()) {
				if (!Inventory.contains(ItemID.BURNING_AMULET5)) {
				if (!Inventory.contains(ItemID.BURNING_AMULET5)) {
					Bank.withdraw(ItemID.BURNING_AMULET5, 1 , Bank.WithdrawMode.ITEM);
				}
				if (Inventory.contains(ItemID.BURNING_AMULET5) && !isWearingBurningAmulet()) {
					Item bankBurningAmulet = Bank.Inventory.getFirst(ItemID.BURNING_AMULET5);
					if (bankBurningAmulet != null && (Game.getWildyLevel() == 0)) {
						bankBurningAmulet.interact("Wear");
					}
				}

			}
			if (Bank.contains(ItemID.RING_OF_DUELING8) && !isWearingROD()) {
				if (!Inventory.isFull() && !Inventory.contains(ItemID.RING_OF_DUELING8)) {
					Bank.withdraw(ItemID.RING_OF_DUELING8, 1, Bank.WithdrawMode.ITEM);
				}

			}
			if (Bank.contains(ItemID.DRAGON_BONES) && isWearingROD() && isWearingBurningAmulet() && !Inventory.isFull()) {
				Bank.withdrawAll(ItemID.DRAGON_BONES, Bank.WithdrawMode.ITEM);
			}

			//else if bank == null
		} else if (isWearingROD() && Game.getWildyLevel() == 0) {
			Item Rod = Equipment.fromSlot(EquipmentInventorySlot.RING);
			System.out.println(Rod);
			if (Rod != null) {
				Rod.interact("Castle Wars");
			}
			Time.sleepTicks(3);
		}

		return 600; // Return the delay for the next loop iteration
	}






	@Subscribe
	private void onClientTick(ClientTick e) {
		int wildyLevel = Game.getWildyLevel();
		if (wildyLevel < 1) {
			return;
		}

		Player local = Players.getLocal();
		int combatLevel = local.getCombatLevel();
		int worldsHopped = 0;
		World world = Worlds.getRandom(w -> w.isNormal() && w.isMembers());
		Player pker = Players.getNearest(player -> player != local && isDangerousPlayer(wildyLevel, combatLevel, player));
		if (pker != null && client.getGameState().equals(GameState.LOGGED_IN)
				&& (!(client.getGameState() == GameState.HOPPING))) {
			client.hopToWorld(world);
			worldsHopped++;
			System.out.println(worldsHopped);
		}

	}

	private boolean isDangerousPlayer(int wildyLevel, int localCombatLevel, Player player) {
		int playerCombatLevel = player.getCombatLevel();
		int lowerLimit = localCombatLevel - wildyLevel;
		int upperLimit = localCombatLevel + wildyLevel;
		return playerCombatLevel >= lowerLimit && playerCombatLevel <= upperLimit;
	}

	private boolean canUseBonesOnAltar(Item bones, TileObject altar, Player local, Player pker) {
		return bones != null
				&& altar != null
				&& !local.isMoving()
				&& pker == null;
	}

	public boolean partiallyContains(String mainText, String partialText) {
		return mainText.contains(partialText);
	}

	public static boolean containsOnly(String... names) {

		List<Item> allItems = (Bank.isOpen() ? Bank.Inventory.getAll() : Inventory.getAll());
		List<String> approvedNames = new ArrayList<>();
		for (String name : names) {
			approvedNames.add(name.toLowerCase(Locale.ROOT));
		}
		for (Item i : allItems) {
			if (i == null || i.getName() == null) {
				continue;
			}
			if (!approvedNames.stream().anyMatch(aprName -> i.getName().toLowerCase(Locale.ROOT).contains(aprName))) {
				return false;
			}
		}
		return true;
	}

	//Burning amulet IDs
	List<Integer> burningAmuletIds = Arrays.asList(
			ItemID.BURNING_AMULET1,
			ItemID.BURNING_AMULET2,
			ItemID.BURNING_AMULET3,
			ItemID.BURNING_AMULET4,
			ItemID.BURNING_AMULET5
	);
	//ROD IDs
	List<Integer> ringOfDuelingIds = Arrays.asList(
			ItemID.RING_OF_DUELING8, ItemID.RING_OF_DUELING7, ItemID.RING_OF_DUELING6,
			ItemID.RING_OF_DUELING5, ItemID.RING_OF_DUELING4, ItemID.RING_OF_DUELING3,
			ItemID.RING_OF_DUELING2, ItemID.RING_OF_DUELING1
	);


	private boolean isWearingBurningAmulet() {

		Item amulet = Equipment.fromSlot(EquipmentInventorySlot.AMULET);
		if (amulet != null) {
			return burningAmuletIds.contains(amulet.getId());
		}
		return false;
	}

	private boolean isWearingROD() {
		Item ROD = Equipment.fromSlot(EquipmentInventorySlot.RING);
		if (ROD != null) {
			return ringOfDuelingIds.contains(ROD.getId());
		}
		return false;
	}

}
