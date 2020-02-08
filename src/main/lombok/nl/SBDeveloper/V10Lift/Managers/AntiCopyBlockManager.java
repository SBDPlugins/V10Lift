package nl.SBDeveloper.V10Lift.Managers;

import nl.SBDeveloper.V10Lift.Utils.XMaterial;
import org.bukkit.Material;

import java.util.ArrayList;

public class AntiCopyBlockManager {
    private ArrayList<XMaterial> antiCopy = new ArrayList<>();

    public AntiCopyBlockManager() {
        //TODO Add more anti copy materials
        antiCopy.add(XMaterial.REDSTONE_TORCH);
        antiCopy.add(XMaterial.REDSTONE_WALL_TORCH);
        antiCopy.add(XMaterial.REPEATER);
        antiCopy.add(XMaterial.COMPARATOR);
        antiCopy.add(XMaterial.REDSTONE_WIRE);
        antiCopy.add(XMaterial.ACACIA_BUTTON);
        antiCopy.add(XMaterial.BIRCH_BUTTON);
        antiCopy.add(XMaterial.DARK_OAK_BUTTON);
        antiCopy.add(XMaterial.JUNGLE_BUTTON);
        antiCopy.add(XMaterial.OAK_BUTTON);
        antiCopy.add(XMaterial.SPRUCE_BUTTON);
        antiCopy.add(XMaterial.STONE_BUTTON);
        antiCopy.add(XMaterial.TORCH);
        antiCopy.add(XMaterial.ACACIA_TRAPDOOR);
        antiCopy.add(XMaterial.BIRCH_TRAPDOOR);
        antiCopy.add(XMaterial.DARK_OAK_TRAPDOOR);
        antiCopy.add(XMaterial.JUNGLE_TRAPDOOR);
        antiCopy.add(XMaterial.OAK_TRAPDOOR);
        antiCopy.add(XMaterial.SPRUCE_TRAPDOOR);
        antiCopy.add(XMaterial.IRON_TRAPDOOR);
        antiCopy.add(XMaterial.ACACIA_PRESSURE_PLATE);
        antiCopy.add(XMaterial.BIRCH_PRESSURE_PLATE);
        antiCopy.add(XMaterial.DARK_OAK_PRESSURE_PLATE);
        antiCopy.add(XMaterial.JUNGLE_PRESSURE_PLATE);
        antiCopy.add(XMaterial.OAK_PRESSURE_PLATE);
        antiCopy.add(XMaterial.SPRUCE_PRESSURE_PLATE);
        antiCopy.add(XMaterial.STONE_PRESSURE_PLATE);
        antiCopy.add(XMaterial.HEAVY_WEIGHTED_PRESSURE_PLATE);
        antiCopy.add(XMaterial.LIGHT_WEIGHTED_PRESSURE_PLATE);
        antiCopy.add(XMaterial.ACACIA_SIGN);
        antiCopy.add(XMaterial.BIRCH_SIGN);
        antiCopy.add(XMaterial.DARK_OAK_SIGN);
        antiCopy.add(XMaterial.JUNGLE_SIGN);
        antiCopy.add(XMaterial.OAK_SIGN);
        antiCopy.add(XMaterial.SPRUCE_SIGN);
        antiCopy.add(XMaterial.ACACIA_WALL_SIGN);
        antiCopy.add(XMaterial.BIRCH_WALL_SIGN);
        antiCopy.add(XMaterial.DARK_OAK_WALL_SIGN);
        antiCopy.add(XMaterial.JUNGLE_WALL_SIGN);
        antiCopy.add(XMaterial.OAK_WALL_SIGN);
        antiCopy.add(XMaterial.SPRUCE_WALL_SIGN);
        antiCopy.add(XMaterial.RAIL);
        antiCopy.add(XMaterial.POWERED_RAIL);
        antiCopy.add(XMaterial.DETECTOR_RAIL);
        antiCopy.add(XMaterial.ACTIVATOR_RAIL);
        antiCopy.add(XMaterial.LADDER);
    }

    public boolean isAntiCopy(Material mat) {
        XMaterial xmat = XMaterial.matchXMaterial(mat);
        return antiCopy.contains(xmat);
    }
}
