package net.unethicalite.plugins.SlayWood;

import lombok.Getter;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.unethicalite.api.coords.RectangularArea;

@Getter
public enum TreeType {
    REGULAR(1, "Tree", new RectangularArea(3266, 3335, 3275, 3342, 0)),
    OAK(15, "Oak tree", new RectangularArea(3255, 3339, 3263, 3347, 0)),
    WILLOW(30, "Willow tree", new RectangularArea(3253, 3354, 3259, 3349, 0));

    private final int level;
    private final String names;
    private final RectangularArea area;

    TreeType(int level, String names, RectangularArea area) {
        this.level = level;
        this.names = names;
        this.area = area;
    }


}
