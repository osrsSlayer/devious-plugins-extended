package net.unethicalite.plugins.Slaytrade;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.client.chat.ChatColorType;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.PluginDescriptor;
import net.unethicalite.api.commons.Time;
import net.unethicalite.api.entities.Players;
import net.unethicalite.api.game.Game;
import net.unethicalite.api.items.Inventory;
import net.unethicalite.api.items.Trade;
import net.unethicalite.api.plugins.LoopedPlugin;
import net.unethicalite.api.utils.MessageUtils;

import net.unethicalite.plugins.Slaytrade.SlayTradeConfig;
import org.pf4j.Extension;

import javax.inject.Inject;

import static net.unethicalite.api.commons.Time.sleep;
import static net.unethicalite.api.commons.Time.sleepUntil;
import static net.unethicalite.api.items.Trade.accept;
import static net.unethicalite.api.items.Trade.hasAccepted;

@Extension
@PluginDescriptor(
        name = "SlayTrade",
        description = "trade acceptor hopefully get turned into a mule script",
        enabledByDefault = false,
        tags =
                {
                        "mule",
                        "trade",
                        "slay"
                }
)
@Slf4j

public class SlayTrade extends LoopedPlugin {
    @Inject
    private Client client;

    @Inject
    private SlayTradeConfig config;

    @Provides
    private SlayTradeConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(SlayTradeConfig.class);
    }

    @Override
    protected void startUp() {
        log.info("Script started");
        MessageUtils.addMessage("start.", ChatColorType.HIGHLIGHT);

    }

    @Override
    protected void shutDown() {
        MessageUtils.addMessage("stop.", ChatColorType.HIGHLIGHT);

    }

    @Override
    protected int loop() {
        String tradeTo = config.Account();

        var local = Players.getLocal();
        if (!Game.isLoggedIn() || (local == null)) {
            MessageUtils.addMessage("can not find player " + tradeTo, ChatColorType.HIGHLIGHT);
            sleep(2500);
            return -1;

        }
        int coinCount = Inventory.getCount(true, 995);
        MessageUtils.addMessage(String.valueOf(coinCount), ChatColorType.HIGHLIGHT);

        //must be tested as unsure if config inputs will work.
        //one way to find out
        //todo
        //to add setting account user:pass via text file for accs to mule to
        //add fury's settings fixer.
        if ((Players.getNearest(tradeTo) != null) && (coinCount < config.muleAmount()) && !Trade.isOpen()) {
            MessageUtils.addMessage("need to trade player! for coins!", ChatColorType.HIGHLIGHT);
            MessageUtils.addMessage("Looking for " + tradeTo + " to trade with", ChatColorType.HIGHLIGHT);
            MessageUtils.addMessage((config.muleAmount() - Inventory.getCount(true, 995)) + " needed", ChatColorType.HIGHLIGHT);


            Players.getNearest(tradeTo).interact("Trade with");
            sleepUntil(() -> Trade.isOpen(), 5000);
        }

        if (Trade.isOpen() && hasAccepted(false)) {
            Time.sleepUntil(() -> Trade.isOpen(), 2500);
        }

        if (Trade.isOpen() && hasAccepted(true)) {
            accept();
        }

        return -2;
    }
}
