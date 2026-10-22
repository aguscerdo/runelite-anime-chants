package com.aguscerdo.animechants.specials;

import com.aguscerdo.animechants.AnimeChantsConfig;
import com.aguscerdo.animechants.TextAndSoundService;
import com.aguscerdo.animechants.chantdata.model.TextEntry;
import com.aguscerdo.animechants.specials.model.SpecWeapon;
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
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
public class SpecialAttackPluginImpl implements SpecialAttackPlugin {
    @Inject
    private Client client;

    @Inject
    private AnimeChantsConfig config;

    @Inject
    private SpecialAttackTextResolver textResolver;

    @Inject
    private TextAndSoundService textAndSoundService;

    private int specPercent = -1;

    private final Map<SpecWeapon, Integer> stackCounter = new HashMap<>();


    public void startUp() {
        textResolver.reloadAll(true);
    }


    public void shutDown() {
        specPercent = -1;

        textResolver.clear();
        stackCounter.clear();
    }


    @Subscribe
    public void onConfigChanged(ConfigChanged event) {
        if (!event.getGroup().equals(AnimeChantsConfig.GROUP)) {
            return;
        }

        if (Objects.equals(event.getKey(), AnimeChantsConfig.SPECIAL_ATTACK_PRESET)) {
            textResolver.reloadPresets(false);
        } else if (Objects.equals(event.getKey(), AnimeChantsConfig.SPECIAL_ATTACK_OVERRIDE)) {
            textResolver.reloadOverrides();
        }
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged state) {
        GameState gs = state.getGameState();
        if (gs == GameState.LOGIN_SCREEN || gs == GameState.HOPPING || gs == GameState.CONNECTION_LOST) {
            specPercent = -1;
            stackCounter.clear();
        }
    }


    @Subscribe
    public void onVarbitChanged(VarbitChanged event) {
        switch (event.getVarpId()) {
            case VarPlayerID.SA_ENERGY:
                handleSpecialAttack(event);
                return;
            case VarPlayerID.SOULREAPER_STACKS:
                handleAltSpecStacks(event, SpecWeapon.SOULREAPER_AXE);
                return;

        }

        switch (event.getVarbitId()) {
            case VarbitID.DRAGONFIRE_SHIELD_RECHARGE:
                handleAltSpecTimer(event, SpecWeapon.DRAGONFIRE_SHIELD);
                return;
            case VarbitID.DRAGONFIRE_WARD_RECHARGE:
                handleAltSpecTimer(event, SpecWeapon.DRAGONFIRE_WARD);
                return;
            case VarbitID.RONDACHE_CHARGES:
                handleAltSpecStacks(event, SpecWeapon.RONDACHE);
                return;
        }
    }


    private void handleSpecialAttack(VarbitChanged event) {
        int newSpec = event.getValue();
        int currentSpec = this.specPercent;
        this.specPercent = newSpec;

        if (!config.specialAttacksEnabled()) return;

        if (currentSpec == -1 || newSpec >= currentSpec) {
            return;
        }

        int weaponId = getWeaponId();
        if (weaponId == -1) return;

        SpecWeapon specWeapon = SpecWeapon.fromId(weaponId);

        textResolver.resolve(specWeapon).ifPresent(this::processTextEntry);
    }


    private void handleAltSpecTimer(VarbitChanged event, SpecWeapon specWeapon) {
        int newValue = event.getValue();

        if (!config.specialAttacksEnabled()) return;
        if (!isWearing(specWeapon)) return;

        // Setting initial value, gaining charges or emptying.
        if (newValue != 24) {
            return;
        }

        textResolver.resolve(specWeapon).ifPresent(this::processTextEntry);
    }

    private void handleAltSpecStacks(VarbitChanged event, SpecWeapon specWeapon) {
        int newValue = event.getValue();

        Integer oldValue = stackCounter.put(specWeapon, newValue);
        if (oldValue == null) return;

        if (!config.specialAttacksEnabled()) return;
        if (!isWearing(specWeapon)) return;

        // Setting initial value, gaining charges or emptying.
        if (newValue != 0 || oldValue - newValue < 2) return;

        textResolver.resolve(specWeapon).ifPresent(this::processTextEntry);
    }

    private void processTextEntry(TextEntry textEntry) {
        textAndSoundService.processTextEntry(textEntry, config.shoutSpecialAttacks(), config.specialAttackSoundsEnabled());
    }


    private int getWeaponId() {
        ItemContainer worn = client.getItemContainer(InventoryID.WORN);
        if (worn == null) return -1;

        Item weapon = worn.getItem(EquipmentInventorySlot.WEAPON.getSlotIdx());
        if (weapon == null) return -1;

        return weapon.getId();
    }

    /**
     * Returns true if spec weapon is being worn.
     */
    private boolean isWearing(SpecWeapon specWeapon) {
        ItemContainer worn = client.getItemContainer(InventoryID.WORN);
        if (worn == null) return false;

        Item weapon = worn.getItem(EquipmentInventorySlot.WEAPON.getSlotIdx());
        if (weapon != null && SpecWeapon.fromId(weapon.getId()) == specWeapon) {
            return true;
        }

        Item shield = worn.getItem(EquipmentInventorySlot.SHIELD.getSlotIdx());
        if (shield != null && SpecWeapon.fromId(shield.getId()) == specWeapon) {
            return true;
        }

        return false;
    }
}
