package com.aguscerdo.animechant.specials;

import com.aguscerdo.animechant.AnimeChantConfig;
import com.aguscerdo.animechant.chantdata.model.TextEntry;
import com.aguscerdo.animechant.specials.model.SpecWeapon;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.VarbitChanged;
import net.runelite.api.gameval.InventoryID;
import net.runelite.api.gameval.VarPlayerID;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;

import javax.inject.Inject;
import java.util.Objects;
import java.util.Optional;

@Slf4j
public class SpecialAttackPluginImpl implements SpecialAttackPlugin {

    @Inject
    private Client client;

    @Inject
    private AnimeChantConfig config;

    @Inject
    private SpecialAttackTextResolver textResolver;

    private int lastSpecPercent = -1;


    public void startUp() {
        textResolver.reloadAll();
    }


    public void shutDown() {
        lastSpecPercent = -1;

        textResolver.clear();
    }


    @Subscribe
    public void onConfigChanged(ConfigChanged event) {
        if (!event.getGroup().equals(AnimeChantConfig.GROUP)) {
            return;
        }

        if (Objects.equals(event.getKey(), AnimeChantConfig.SPECIAL_ATTACK_PRESET)) {
            textResolver.reloadPresets();
        }
        if (Objects.equals(event.getKey(), AnimeChantConfig.SPECIAL_ATTACK_OVERRIDE)) {
            textResolver.reloadOverrides();
        }
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged state) {
        GameState gs = state.getGameState();
        if (gs == GameState.LOGIN_SCREEN || gs == GameState.HOPPING || gs == GameState.CONNECTION_LOST) {
            lastSpecPercent = -1;   // reseed on next varp update
        }
    }

    public Optional<TextEntry> onVarbitChanged(VarbitChanged event) {
        if (event.getVarpId() != VarPlayerID.SA_ENERGY) return Optional.empty();

        int spec = event.getValue();
        log.debug("Varbit change SA event: {} - {} -> {}", event, lastSpecPercent, spec);

        if (lastSpecPercent != -1 && spec < lastSpecPercent) {
            lastSpecPercent = spec;
            return handleSpecialAttack();
        }

        lastSpecPercent = spec;
        return Optional.empty();
    }

    private Optional<TextEntry> handleSpecialAttack() {
        int weaponId = getWeaponId();
        if (weaponId == -1) return Optional.empty();

        SpecWeapon specWeapon = SpecWeapon.fromId(weaponId);

        return textResolver.resolve(specWeapon);
    }

    private int getWeaponId() {
        ItemContainer worn = client.getItemContainer(InventoryID.WORN);
        log.debug("Worn: {}", worn);
        if (worn == null) return -1;

        Item weapon = worn.getItem(EquipmentInventorySlot.WEAPON.getSlotIdx());
        log.debug("weapon: {}", weapon);
        if (weapon == null) return -1;

        return weapon.getId();
    }
}
