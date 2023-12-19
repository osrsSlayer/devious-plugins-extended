package net.unethicalite.plugins.SlayWood;

import net.runelite.api.Client;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.PanelComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

import javax.inject.Inject;
import java.awt.Dimension;
import java.awt.Graphics2D;

public class SlayRevsOverlay extends Overlay {

    private final Client client;
    private final PanelComponent panelComponent = new PanelComponent();

    @Inject
    public SlayRevsOverlay(Client client) {
        this.client = client;
        setPosition(OverlayPosition.ABOVE_CHATBOX_RIGHT);
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        panelComponent.getChildren().clear();
        panelComponent.getChildren().add(TitleComponent.builder().text("SlayRevs Running").build());
        return panelComponent.render(graphics);
    }
}