package net.unethicalite.plugins.SlayWood;

import com.google.inject.Provides;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.GameObject;
import net.runelite.api.Skill;
import net.runelite.api.Tile;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameTick;
import net.runelite.client.chat.ChatColorType;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.PluginDescriptor;
import net.unethicalite.api.entities.Players;
import net.unethicalite.api.entities.TileObjects;
import net.unethicalite.api.game.Game;
import net.unethicalite.api.game.Skills;
import net.unethicalite.api.items.Inventory;
import net.unethicalite.api.movement.Movement;
import net.unethicalite.api.movement.Reachable;
import net.unethicalite.api.movement.pathfinder.GlobalCollisionMap;
import net.unethicalite.api.plugins.LoopedPlugin;
import net.unethicalite.api.scene.Tiles;
import net.unethicalite.api.utils.MessageUtils;
import org.pf4j.Extension;

import javax.inject.Inject;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;


@Extension
@PluginDescriptor(
        name = "SlayWood",
        description = "progressively chops wood",
        enabledByDefault = false,
        tags =
                {
                        "tree",
                        "wood",
                        "fire",
                        "slay",
                        "slayer"
                }
)
@Slf4j

public class SlayWood extends LoopedPlugin {

    @Inject
    private SlayWoodConfig config;
    @Inject
    private GlobalCollisionMap collisionMap;

    @Provides
    private SlayWoodConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(SlayWoodConfig.class);
    }

    private int fmCooldown = 0;
    @Getter(AccessLevel.PROTECTED)
    private List<Tile> fireArea;

    @Override
    protected void startUp() {
        log.info("Script started");
        MessageUtils.addMessage("start.", ChatColorType.HIGHLIGHT);

    }

    @Override
    protected void shutDown() {
        MessageUtils.addMessage("stop.", ChatColorType.HIGHLIGHT);

    }

    private WorldPoint startLocation = null;

    @Override
    protected int loop() {
        MessageUtils.addMessage("timeout fmtick", ChatColorType.HIGHLIGHT);

        if (fmCooldown > 0) {
            return -1;
        }
        //variables and setting tree type.
        var local = Players.getLocal();
        int wclevel = Skills.getLevel(Skill.WOODCUTTING);
        TreeType selectedTreeType = selectTreeType(wclevel);
        MessageUtils.addMessage(String.valueOf(selectedTreeType), ChatColorType.HIGHLIGHT);
        MessageUtils.addMessage(String.valueOf(wclevel), ChatColorType.HIGHLIGHT);
        MessageUtils.addMessage(String.valueOf(selectedTreeType.getArea()), ChatColorType.HIGHLIGHT);
        MessageUtils.addMessage(String.valueOf(startLocation), ChatColorType.HIGHLIGHT);

        var tree = TileObjects
                .getSurrounding(selectedTreeType.getArea().getCenter(), 6, selectedTreeType.getNames())
                .stream()
                .min(Comparator.comparing(x -> x.distanceTo(local.getWorldLocation())))
                .orElse(null);
        if (tree == null ) {
            MessageUtils.addMessage("tree is null", ChatColorType.HIGHLIGHT);

        } else {
            MessageUtils.addMessage("tree is not null", ChatColorType.HIGHLIGHT);

        }


        if (!Game.isLoggedIn() || (local == null) || Movement.isWalking()) {
            return -1;
        }
        if (startLocation == null) {
            startLocation = selectedTreeType.getArea().getCenter();
        }

        if (!selectedTreeType.getArea().contains(local) || !Reachable.isInteractable(tree)) {
            MessageUtils.addMessage(String.valueOf(selectedTreeType.getArea()), ChatColorType.HIGHLIGHT);
            Movement.walkTo(selectedTreeType.getArea().getNearest());
            MessageUtils.addMessage("walkTo 1", ChatColorType.HIGHLIGHT);

            return 500;
        }

        if (local.isMoving() || local.isAnimating()) {
            return 500;
        }


        var logs = Inventory.getFirst(x -> x.getName().toLowerCase(Locale.ROOT).contains("logs"));
        if (config.makeFire()) {
            var tinderbox = Inventory.getFirst("Tinderbox");
            if (logs != null && tinderbox != null) {
                var emptyTile = fireArea == null || fireArea.isEmpty() ? null : fireArea.stream()
                        .filter(t ->
                        {
                            Tile tile = Tiles.getAt(t.getWorldLocation());
                            return tile != null && isEmptyTile(tile);
                        })
                        .min(Comparator.comparingInt(wp -> wp.distanceTo(local)))
                        .orElse(null);


                MessageUtils.addMessage("here", ChatColorType.HIGHLIGHT);

                if (fireArea.isEmpty() || emptyTile == null) {
                    fireArea = generateFireArea(6);
                    log.debug("Generating fire area");
                    MessageUtils.addMessage("debug nulling area", ChatColorType.HIGHLIGHT);
                    return 1000;
                }

                MessageUtils.addMessage("debug here.", ChatColorType.HIGHLIGHT);


                if (emptyTile != null) {
                    if (!emptyTile.getWorldLocation().equals(local.getWorldLocation())) {
                        if (local.isMoving()) {
                            return 333;
                        }

                        Movement.walk(emptyTile);
                        MessageUtils.addMessage("walking to empty tile.", ChatColorType.HIGHLIGHT);
                        return 1000;

                    }

                    if (local.isAnimating()) {
                        return 333;
                    }

                    fmCooldown = 4;
                    tinderbox.useOn(logs);
                    return 500;
                }
            }
        } else {
            if (logs != null && !local.isAnimating()) {
                logs.drop();
                return 500;
            }
        }
        if (selectedTreeType.getArea().contains(local)) {
            MessageUtils.addMessage("contains local true", ChatColorType.HIGHLIGHT);
        } else {
            MessageUtils.addMessage("walking to empty tile.", ChatColorType.HIGHLIGHT);

        }


        if (!local.isAnimating() && !local.isMoving() && selectedTreeType.getArea().contains(local) && (tree != null)) {

            tree.interact("Chop down");
            MessageUtils.addMessage("Interacting with tree.", ChatColorType.HIGHLIGHT);
            return 600;
        }

        return 650;
    }

    public static TreeType selectTreeType(int level) {
        if (level < 15) {
            return TreeType.REGULAR;
        } else if (level < 30) {
            return TreeType.OAK;
        } else {
            return TreeType.WILLOW;
        }
    }

    private List<Tile> generateFireArea(int radius) {
        return Tiles.getSurrounding(Players.getLocal().getWorldLocation(), radius).stream()
                .filter(tile -> tile != null
                        && isEmptyTile(tile)
                        && Reachable.isWalkable(tile.getWorldLocation()))
                .collect(Collectors.toUnmodifiableList());
    }

    protected boolean isEmptyTile(Tile tile) {
        return tile != null
                && TileObjects.getFirstAt(tile, a -> a instanceof GameObject) == null
                && !collisionMap.fullBlock(tile.getWorldLocation());
    }

    @Subscribe
    private void onGameTick(GameTick e) {
        if (fmCooldown > 0) {
            fmCooldown--;
        }
    }

}