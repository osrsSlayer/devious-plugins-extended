import net.runelite.client.ui.overlay.OverlayManager;
import net.unethicalite.api.plugins.Script;
import org.pf4j.Extension;

import javax.inject.Inject;
import java.time.Duration;
import java.time.Instant;

// This annotation is required in order for the client to detect it as a plugin/script.
@PluginDescriptor(name = "example tree framework", enabledByDefault = false)
@Extension
public class ExampleTree extends Script

    /**
     * @param args script quick launch arguments
     */

    @Override
    public void onStart(String... args) {
        instantiateTree();
    }

    /**
     * On start from script launcher
     */
    @Override
    public void onStart() {
        instantiateTree();
    }


    private final Tree<Main> tree = new Tree<>();
    private Branch<Main> exampleBranch;

    private void instantiateTree() {
        exampleBranch = new ExampleBranch();

        tree.addBranches(
                exampleBranch.addLeafs(new ExampleLeaf(), new ExampleLeafTwo())
        );
    }


    /**
     * onLoop is a infinite loop
     * @return gets the leaf and executes it
     */

    @Override
    public int onLoop() {

        return this.tree.onLoop();
    }

    /**
     * @return the information for the paint
     */
    @Override
    public String[] getPaintInfo() {
        return new String[] {
                getManifest().name() + " V" + getManifest().version(),
                "Current Branch: " + API.currentBranch,
                "Current Leaf: " + API.currentLeaf
        };
    }

    /**
     * Instantiate the paint object, can be customized to liking.
     */
    private final CustomPaint CUSTOM_PAINT = new CustomPaint(this,
            CustomPaint.PaintLocations.TOP_LEFT_PLAY_SCREEN, new Color[]{new Color(255, 251, 255)},
            "Trebuchet MS",
            new Color[]{new Color(50, 50, 50, 175)},
            new Color[]{new Color(28, 28, 29)},
            1, false, 5, 3, 0);

    private final RenderingHints aa = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);


    /**
     * paint for the script
     */
    @Override
    public void onPaint(Graphics g) {
        Graphics2D gg = (Graphics2D) g;
        gg.setRenderingHints(aa);

        CUSTOM_PAINT.paint(gg);
    }

}
