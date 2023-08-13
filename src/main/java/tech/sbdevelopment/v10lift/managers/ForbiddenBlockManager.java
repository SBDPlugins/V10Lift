package tech.sbdevelopment.v10lift.managers;

import com.cryptomorin.xseries.XMaterial;
import org.bukkit.Material;
import tech.sbdevelopment.v10lift.V10LiftPlugin;

import java.util.HashSet;

/**
 * This class contains a set with all the blocks who may not be placed in a lift
 */
public class ForbiddenBlockManager {
    private static final HashSet<XMaterial> forbidden = new HashSet<>();

    public static void init() {
        for (String mat : V10LiftPlugin.getItems().getFile().getStringList("ForbiddenMaterials")) {
            forbidden.add(XMaterial.matchXMaterial(mat).orElseThrow());
        }
    }

    public static void reinit() {
        forbidden.clear();
        init();
    }

    /**
     * Check if this block may not be placed in a lift
     *
     * @param mat The material to check for
     * @return true = not place this block
     */
    public static boolean isForbidden(Material mat) {
        XMaterial xmat = XMaterial.matchXMaterial(mat);
        return forbidden.contains(xmat);
    }
}
