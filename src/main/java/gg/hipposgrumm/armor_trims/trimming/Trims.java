package gg.hipposgrumm.armor_trims.trimming;

import com.mojang.logging.LogUtils;

import java.util.ArrayList;
import java.util.List;

public enum Trims {
    COAST,
    DUNE,
    EYE,
    HOST,
    RAISER,
    RIB,
    SENTRY,
    SHAPER,
    SILENCE,
    SNOUT,
    SPIRE,
    TIDE,
    VEX,
    WARD,
    WAYFINDER,
    WILD,
    NETHERITE_UPGRADE;

    private static List<String> nonexistantIllegals = new ArrayList<String>();

    public String getId() {
        return this.name().toLowerCase();
    }

    public static Trims getValueOf(String name) {
        if (!nonexistantIllegals.contains(name)) {
            try {
                return valueOf(name.toUpperCase());
            } catch (IllegalArgumentException e) {
                LogUtils.getLogger().warn("There is no trim named: " + name.toUpperCase());
                nonexistantIllegals.add(name);
            }
        }
        return COAST;
    }
}
