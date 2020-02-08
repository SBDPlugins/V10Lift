package nl.SBDeveloper.V10Lift.Managers;

import nl.SBDeveloper.V10Lift.Utils.XMaterial;
import org.bukkit.Material;

import java.util.ArrayList;

public class ForbiddenBlockManager {
    private ArrayList<XMaterial> forbidden = new ArrayList<>();

    public ForbiddenBlockManager() {
        //TODO Add more forbidden materials
        forbidden.add(XMaterial.ACACIA_DOOR);
        forbidden.add(XMaterial.BIRCH_DOOR);
        forbidden.add(XMaterial.DARK_OAK_DOOR);
        forbidden.add(XMaterial.IRON_DOOR);
        forbidden.add(XMaterial.JUNGLE_DOOR);
        forbidden.add(XMaterial.OAK_DOOR);
        forbidden.add(XMaterial.SPRUCE_DOOR);
        forbidden.add(XMaterial.BLACK_BED);
        forbidden.add(XMaterial.BLUE_BED);
        forbidden.add(XMaterial.BROWN_BED);
        forbidden.add(XMaterial.CYAN_BED);
        forbidden.add(XMaterial.GRAY_BED);
        forbidden.add(XMaterial.GREEN_BED);
        forbidden.add(XMaterial.LIGHT_BLUE_BED);
        forbidden.add(XMaterial.LIGHT_GRAY_BED);
        forbidden.add(XMaterial.LIME_BED);
        forbidden.add(XMaterial.MAGENTA_BED);
        forbidden.add(XMaterial.ORANGE_BED);
        forbidden.add(XMaterial.PINK_BED);
        forbidden.add(XMaterial.PURPLE_BED);
        forbidden.add(XMaterial.RED_BED);
        forbidden.add(XMaterial.WHITE_BED);
        forbidden.add(XMaterial.YELLOW_BED);
        forbidden.add(XMaterial.ACACIA_SAPLING);
        forbidden.add(XMaterial.BAMBOO_SAPLING);
        forbidden.add(XMaterial.BIRCH_SAPLING);
        forbidden.add(XMaterial.DARK_OAK_SAPLING);
        forbidden.add(XMaterial.JUNGLE_SAPLING);
        forbidden.add(XMaterial.OAK_SAPLING);
        forbidden.add(XMaterial.SPRUCE_SAPLING);
        forbidden.add(XMaterial.TNT);
        forbidden.add(XMaterial.PISTON);
        forbidden.add(XMaterial.PISTON_HEAD);
        forbidden.add(XMaterial.MOVING_PISTON);
        forbidden.add(XMaterial.STICKY_PISTON);
    }

    public boolean isForbidden(Material mat) {
        XMaterial xmat = XMaterial.matchXMaterial(mat);
        return forbidden.contains(xmat);
    }
}
