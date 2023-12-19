package net.unethicalite.scripts.RuneDragons.tasks.WildernessSubTasks;

import net.runelite.api.*;
import net.runelite.api.events.InteractingChanged;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.chat.ChatColorType;
import net.runelite.client.eventbus.Subscribe;
import net.unethicalite.api.entities.Players;
import net.unethicalite.api.game.Game;
import net.unethicalite.api.game.Worlds;
import net.unethicalite.api.items.Inventory;
import net.unethicalite.api.utils.MessageUtils;


public class HopWorlds implements WildernessSubTask
{
    // Remove the @Inject annotation
    private final Client client;
    private final ClientThread clientThread;

    // Add a constructor that takes Client as a parameter
    public HopWorlds(Client client, ClientThread clientThread)
    {
        this.client = client;
        this.clientThread = clientThread;
    }
    @Override
    public boolean validate()
    {
        return !Inventory.isFull() && Inventory.contains(ItemID.COINS_995);
    }

    @Override
    public int execute()
    {
        MessageUtils.addMessage("wilderness test task triggered.", ChatColorType.HIGHLIGHT);

        int wildyLevel = Game.getWildyLevel();
        Player local = Players.getLocal();
        int combatLevel = local.getCombatLevel();
        int worldsHopped = 0;


        World world = Worlds.getRandom(w -> w.isNormal() && !w.isSkillTotal());
        Player pker = Players.getNearest(player -> player != local && isDangerousPlayer(wildyLevel, combatLevel, player));
        String playerpkr = pker.getName();

        if (client == null)
        {
            System.out.println("Client is null");
            return -1;
        }


        if (pker != null && client.getGameState().equals(GameState.LOGGED_IN))
        {
        }
        return 1200;
    }

    @Subscribe
    public void onInteractingChanged(InteractingChanged event)
    {
        if (event.getSource() instanceof Player && event.getTarget() == client.getLocalPlayer())
        {
            System.out.println("You're under attack!");
        }
    }



    private boolean isDangerousPlayer(int wildyLevel, int localCombatLevel, Player player)
    {
        int playerCombatLevel = player.getCombatLevel();
        int lowerLimit = localCombatLevel - wildyLevel;
        int upperLimit = localCombatLevel + wildyLevel;
        return playerCombatLevel >= lowerLimit && playerCombatLevel <= upperLimit;
    }

    @Override
    public boolean blocking()
    {
        return false;
    }
}
