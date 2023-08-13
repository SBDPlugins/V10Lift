package tech.sbdevelopment.v10lift.managers;

import com.cryptomorin.xseries.XMaterial;
import org.bukkit.Material;
import tech.sbdevelopment.v10lift.V10LiftPlugin;

import java.util.HashSet;

/**
 * This class contains a set with all the blocks who may not be copied
 */
public class AntiCopyBlockManager {
    private static final HashSet<XMaterial> antiCopy = new HashSet<>();

    public static void init() {
        for (String mat : V10LiftPlugin.getItems().getFile().getStringList("AntiCopyMaterials")) {
            antiCopy.add(XMaterial.matchXMaterial(mat).orElseThrow());
        }
    }

    public static void reinit() {
        antiCopy.clear();
        init();
    }

    /**
     * Check if this block may not be copied
     *
     * @param mat The material to check for
     * @return true = not copy this block
     */
    public static boolean isAntiCopy(Material mat) {
        XMaterial xmat = XMaterial.matchXMaterial(mat);
        return antiCopy.contains(xmat);
    }
}
