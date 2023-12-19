package net.unethicalite.scripts.RuneDragons;

import net.runelite.api.Client;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.PanelComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

import javax.inject.Inject;
import java.awt.*;
import java.time.Duration;

public class SlayRuneDragonsOverlay extends Overlay
{

    private final Client client;
    private final slayRuneDragons script;
    private final PanelComponent panelComponent = new PanelComponent();

    @Inject
    public SlayRuneDragonsOverlay(Client client, slayRuneDragons script)
    {
        this.client = client;
        this.script = script;
        setPosition(OverlayPosition.ABOVE_CHATBOX_RIGHT);
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        // Set the font for the script name
        graphics.setFont(new Font("Arial", Font.BOLD, 20));

        // Custom semi-transparent background
        panelComponent.setBackgroundColor(new Color(2, 0, 0, 140));

        // Set the PanelComponent to a preferred size
        panelComponent.setPreferredSize(new Dimension(200, 0));

        panelComponent.getChildren().clear();

        // Add the script name to the overlay
        panelComponent.getChildren().add(TitleComponent.builder()
                .text("SlayRevs")
                .color(Color.GREEN)
                .build());

        // Now, change the font size for the runtime
        graphics.setFont(new Font("Arial", Font.PLAIN, 16));

        // And add the runtime to the overlay
        Duration runtime = script.getScriptRuntime();
        panelComponent.getChildren().add(LineComponent.builder()
                .left("Total runtime:")
                .right(formatDuration(runtime))
                .build());

        return panelComponent.render(graphics);
    }

    // Formatting Duration to a more readable format
    private static String formatDuration(Duration duration)
    {
        final long hours = duration.toHours();
        final long mins = (duration.minusHours(hours).toMinutes());
        final long secs = duration.minusHours(hours).minusMinutes(mins).getSeconds();

        return String.format("%02d:%02d:%02d", hours, mins, secs);
    }
}