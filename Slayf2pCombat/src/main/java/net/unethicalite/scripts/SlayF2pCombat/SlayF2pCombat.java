package net.unethicalite.scripts.SlayF2pCombat;

import com.google.inject.Inject;
import net.runelite.api.*;
import net.runelite.client.chat.ChatColorType;
import net.runelite.client.eventbus.Subscribe;
import net.unethicalite.api.commons.Time;
import net.unethicalite.api.coords.Area;
import net.unethicalite.api.coords.RectangularArea;
import net.unethicalite.api.entities.Players;
import net.unethicalite.api.events.ExperienceGained;
import net.unethicalite.api.game.Combat;
import net.unethicalite.api.game.GameThread;
import net.unethicalite.api.game.Vars;
import net.unethicalite.api.items.Inventory;
import net.unethicalite.api.movement.Movement;
import net.unethicalite.api.movement.Reachable;
import net.unethicalite.api.packets.DialogPackets;
import net.unethicalite.api.packets.WidgetPackets;
import net.unethicalite.api.plugins.Script;
import net.unethicalite.api.utils.MessageUtils;
import net.unethicalite.api.widgets.Dialog;
import net.unethicalite.scripts.SlayF2pCombat.tasks.ScriptTask;
import net.runelite.client.plugins.PluginDescriptor;
import org.pf4j.Extension;

import java.util.*;
import java.util.function.Supplier;

// This annotation is required in order for the client to detect it as a plugin/script.
@PluginDescriptor(name = "SlayF2pCombat", enabledByDefault = false)
@Extension
public class SlayF2pCombat extends Script {
	private static final ScriptTask[] TASKS = new ScriptTask[]{

	};

	/**
	 * Gets executed whenever a script starts.
	 * Can be used to for example initialize script settings, or perform tasks before starting the loop logic.
	 *
	 * @param args any script arguments passed to the script, separated by spaces.
	 */
	@Override
	public void onStart(String... args) {
	}


	@Inject
	private Client client;

	private static final int ALL_SETTINGS_WIDGET_ID = 7602208;
	private static final int MENU_TAB_WIDGET_ID = 8781847;
	private static final int OPTION_WIDGET_ID = 8781843;
	private static final int CLOSE_BUTTON_WIDGET_ID = 8781828;
	private boolean settingsFixed = false;


	@Override
	protected int loop() {

		if (isSettingsFixed()) {

			if (Dialog.isOpen() && Dialog.canContinue()) {
				Dialog.continueSpace();
			}

			Area chicken = new RectangularArea(3172, 3301, 3183, 3290);
			Player local = Players.getLocal();

			List<NPC> chickenNPCs = client.getNpcs();
			NPC Chicken = Combat.getAttackableNPC(npc -> npc.getName() != null
					&& !npc.isInteracting()
					&& !npc.isDead()
					&& chicken.contains(npc)
					&& npc.getName().equals("Chicken"));


			if (Movement.isWalking() && (chickenNPCs != null) || local.isInteracting()) {
				return -1;
			}

			Item shield = Inventory.getFirst(ItemID.WOODEN_SHIELD);
			Item weapon = Inventory.getFirst(ItemID.BRONZE_SWORD);
			if (Inventory.contains(ItemID.BRONZE_SWORD) && Inventory.contains(ItemID.BRONZE_SWORD)) {
				weapon.interact("Wield");
				shield.interact("Wield");
				MessageUtils.addMessage("here now", ChatColorType.HIGHLIGHT);

				return -2;

			}

			if (!chicken.contains(local) && !local.isMoving()
					|| (Chicken == null)
					|| !Reachable.isInteractable(Chicken)) {
				Movement.walkTo(chicken.getRandomTile());
			}

			Chicken.interact("Attack");
			Time.sleepTick();
			return -3;
		}
		settings();
		return -2;

	}

	@Subscribe
	public void onExperienceChanged(ExperienceGained event) {
		if (event.getSkill() == Skill.ATTACK || event.getSkill() == Skill.STRENGTH || event.getSkill() == Skill.DEFENCE) {
			suggestTraining();
		}
	}

	public void suggestTraining() {
		int strength = client.getRealSkillLevel(Skill.STRENGTH);
		int attack = client.getRealSkillLevel(Skill.ATTACK);
		int defence = client.getRealSkillLevel(Skill.DEFENCE);

		int minLevel = Math.min(Math.min(strength, attack), defence);
		int trainUntil = ((minLevel / 10) + 1) * 10;

		if (strength < trainUntil) {
			Combat.setAttackStyle(Combat.AttackStyle.SECOND);

		} else if (attack < trainUntil) {
			Combat.setAttackStyle(Combat.AttackStyle.FIRST);
		} else if (defence < trainUntil)
			Combat.setAttackStyle(Combat.AttackStyle.FOURTH);
	}


	public boolean isSettingsFixed() {
		return settingsFixed;
	}

	private void queueWidgetAction(int menuTab, List<Integer> options, int widgetId) {
		MessageUtils.addMessage("Menu tab: " + menuTab);
		GameThread.invoke(() -> {
			WidgetPackets.queueWidgetAction1Packet(ALL_SETTINGS_WIDGET_ID, -1, -1);
			WidgetPackets.queueWidgetAction1Packet(MENU_TAB_WIDGET_ID, -1, menuTab);
			for (int option : options) {
				MessageUtils.addMessage("Option: " + option);
				WidgetPackets.queueWidgetAction1Packet(widgetId, -1, option);
			}
			WidgetPackets.queueWidgetAction1Packet(CLOSE_BUTTON_WIDGET_ID, -1, -1);
		});
	}

	public int settings() {
		if (Dialog.isOpen()) {
			DialogPackets.closeInterface();
			return 600;
		}

		// Create and define the settings categories
		List<SettingsCategory> categories = Arrays.asList(
				new SettingsCategory(1, 8781845, new HashMap<Integer, Supplier<Boolean>>() {{
					put(21, () -> Vars.getVarp(168) != 0); // Disable Music
					put(42, () -> Vars.getVarp(169) != 0); // Disable Sound effect
					put(63, () -> Vars.getVarp(872) != 0); // Disable Area sound
				}}),
				new SettingsCategory(2, OPTION_WIDGET_ID, new HashMap<Integer, Supplier<Boolean>>() {{
					put(1, () -> Vars.getVarp(1074) == 0); // Disable profanity filter
				}}),
				new SettingsCategory(3, OPTION_WIDGET_ID, new HashMap<Integer, Supplier<Boolean>>() {{
					put(7, () -> Vars.getBit(5542) == 0); // Enable shift dropping
					put(39, () -> Vars.getBit(4681) == 0); // Enable escape close interface
				}}),
				new SettingsCategory(4, OPTION_WIDGET_ID, new HashMap<Integer, Supplier<Boolean>>() {{
					put(4, () -> Vars.getBit(12378) == 0); // Enable hide roofs
				}}),
				new SettingsCategory(5, OPTION_WIDGET_ID, new HashMap<Integer, Supplier<Boolean>>() {{
					put(1, () -> Vars.getBit(4180) == 1); // Disable accept aid
					put(5, () -> Vars.getBit(14819) == 1); // Disable Make-x darts
					put(6, () -> Vars.getBit(5697) == 0); // Enable Ammo-picking behaviour
					put(7, () -> Vars.getBit(5698) == 0); // Enable Rune-picking behaviour
					put(9, () -> Vars.getBit(14197) == 0); // Disable baba yaga camera
					put(10, () -> Vars.getBit(4814) == 0); // Disable fishing trawler came
					put(11, () -> Vars.getBit(14198) == 0); // Disable barrows camera
				}}),
				new SettingsCategory(6, OPTION_WIDGET_ID, new HashMap<Integer, Supplier<Boolean>>() {{
					put(14, () -> Vars.getBit(13037) == 1); // Disable store button
					put(15, () -> Vars.getBit(Varbits.WIKI_ENTITY_LOOKUP) == 0); // Disable wiki lookup
					put(16, () -> Vars.getBit(5368) == 0); // Disable  activity adviser
					put(19, () -> Vars.getBit(13130) == 0); // Disable trade delays
					put(21, () -> Vars.getBit(Varbits.DISABLE_LEVEL_UP_INTERFACE) == 0); // disable level up interface
				}}),
				new SettingsCategory(7, OPTION_WIDGET_ID, new HashMap<Integer, Supplier<Boolean>>() {{
					put(32, () -> Vars.getBit(4100) == 0); // Disable world hop warning
					put(39, () -> Vars.getBit(14700) == 0); // Disable GE Buy warning
					put(40, () -> Vars.getBit(14701) == 0); // Disable GE Sell warning
				}})
		);

		for (SettingsCategory category : categories) {
			category.applySettings();
		}

		settingsFixed = true;
		return 1000;
	}

	private class SettingsCategory {
		int menuTab;
		int widgetId;
		Map<Integer, Supplier<Boolean>> conditions;

		public SettingsCategory(int menuTab, int widgetId, Map<Integer, Supplier<Boolean>> conditions) {
			this.menuTab = menuTab;
			this.widgetId = widgetId;
			this.conditions = conditions;
		}

		public void applySettings() {
			List<Integer> optionsToQueue = new ArrayList<>();
			for (Map.Entry<Integer, Supplier<Boolean>> entry : conditions.entrySet()) {
				if (entry.getValue().get()) {
					optionsToQueue.add(entry.getKey());
				}
			}

			if (!optionsToQueue.isEmpty()) {
				queueWidgetAction(menuTab, optionsToQueue, widgetId);
				for (Integer option : optionsToQueue) {
					Time.sleepUntil(() -> !conditions.get(option).get(), 5000);
				}
			}
		}
	}
}
