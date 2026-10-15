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
import net.runelite.api.gameval.VarbitID;
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

    private int specPercent = -1;

    // Goes to zero as it is charged.
    private int dfsRecharge = -1;
    private int dfwRecharge = -1;


    public void startUp() {
        textResolver.reloadAll();
    }


    public void shutDown() {
        specPercent = -1;

        textResolver.clear();
    }


    @Subscribe
    public void onConfigChanged(ConfigChanged event) {
        if (!event.getGroup().equals(AnimeChantConfig.GROUP)) {
            return;
        }

        if (Objects.equals(event.getKey(), AnimeChantConfig.SPECIAL_ATTACK_PRESET)) {
            textResolver.reloadPresets();
        } else if (Objects.equals(event.getKey(), AnimeChantConfig.SPECIAL_ATTACK_OVERRIDE)) {
            textResolver.reloadOverrides();
        }
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged state) {
        GameState gs = state.getGameState();
        if (gs == GameState.LOGIN_SCREEN || gs == GameState.HOPPING || gs == GameState.CONNECTION_LOST) {
            specPercent = -1;
        }
    }


    // TODO handle wyvern shield via animations or chat text.
    public Optional<TextEntry> onVarbitChanged(VarbitChanged event) {
        if (event.getVarpId() == VarPlayerID.SA_ENERGY) {
            return handleSpecialAttack(event);
        }

        if (event.getVarbitId() == VarbitID.DRAGONFIRE_SHIELD_RECHARGE
                && event.getVarbitId() == VarbitID.DRAGONFIRE_WARD_RECHARGE
        ) {
            return handleAltSpecialAttack(event);
        }

        return Optional.empty();


    }


    private Optional<TextEntry> handleSpecialAttack(VarbitChanged event) {
        int newSpec = event.getValue();
        int currentSpec = this.specPercent;
        this.specPercent = newSpec;

        if (!config.specialAttacksEnabled()) return Optional.empty();

        if (currentSpec == -1 || newSpec >= currentSpec) {
            return Optional.empty();
        }

        int weaponId = getWeaponId();
        if (weaponId == -1) return Optional.empty();

        SpecWeapon specWeapon = SpecWeapon.fromId(weaponId);

        return textResolver.resolve(specWeapon);
    }

    private Optional<TextEntry> handleAltSpecialAttack(VarbitChanged event) {
        int newValue = event.getValue();
        int oldValue;
        SpecWeapon specWeapon;

        if (event.getVarbitId() == VarbitID.DRAGONFIRE_SHIELD_RECHARGE) {
            oldValue = dfsRecharge;
            dfsRecharge = newValue;
            specWeapon = SpecWeapon.DRAGONFIRE_SHIELD;
        } else if (event.getVarbitId() == VarbitID.DRAGONFIRE_WARD_RECHARGE) {
            oldValue = dfwRecharge;
            dfwRecharge = newValue;
            specWeapon = SpecWeapon.DRAGONFIRE_WARD;
        } else {
            return Optional.empty();
        }

        if (!config.specialAttacksEnabled()) return Optional.empty();

        // Setting initial value, gaining charges or emptying.
        if (oldValue == -1 || newValue - oldValue > 1) {
            return Optional.empty();
        }

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
