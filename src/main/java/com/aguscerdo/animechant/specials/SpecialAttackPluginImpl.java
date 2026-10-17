package com.aguscerdo.animechant.specials;

import com.aguscerdo.animechant.AnimeChantConfig;
import com.aguscerdo.animechant.TextAndSoundService;
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

@Slf4j
public class SpecialAttackPluginImpl implements SpecialAttackPlugin {
    @Inject
    private Client client;

    @Inject
    private AnimeChantConfig config;

    @Inject
    private SpecialAttackTextResolver textResolver;

    @Inject
    private TextAndSoundService textAndSoundService;

    private int specPercent = -1;
    private int sraStacks = 0;


    public void startUp() {
        textResolver.reloadAll(true);
    }


    public void shutDown() {
        specPercent = -1;
        sraStacks = 0;

        textResolver.clear();
    }


    @Subscribe
    public void onConfigChanged(ConfigChanged event) {
        if (!event.getGroup().equals(AnimeChantConfig.GROUP)) {
            return;
        }

        if (Objects.equals(event.getKey(), AnimeChantConfig.SPECIAL_ATTACK_PRESET)) {
            textResolver.reloadPresets(false);
        } else if (Objects.equals(event.getKey(), AnimeChantConfig.SPECIAL_ATTACK_OVERRIDE)) {
            textResolver.reloadOverrides();
        }
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged state) {
        GameState gs = state.getGameState();
        if (gs == GameState.LOGIN_SCREEN || gs == GameState.HOPPING || gs == GameState.CONNECTION_LOST) {
            specPercent = -1;
            sraStacks = 0;
        }
    }


    @Subscribe
    public void onVarbitChanged(VarbitChanged event) {
        if (event.getVarpId() == VarPlayerID.SA_ENERGY) {
            handleSpecialAttack(event);
            return;
        } else if (event.getVarpId() == VarPlayerID.SOULREAPER_STACKS) {
            handleSoulReaperAxe(event);
            return;
        }

        if (event.getVarbitId() == VarbitID.DRAGONFIRE_SHIELD_RECHARGE
                || event.getVarbitId() == VarbitID.DRAGONFIRE_WARD_RECHARGE
        ) {
            handleAltSpecialAttack(event);
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

    private void handleSoulReaperAxe(VarbitChanged event) {
        int newStacks = event.getValue();
        int oldStacks = sraStacks;
        sraStacks = newStacks;

        if (!config.specialAttacksEnabled()) return;
        if (newStacks != 0 || oldStacks - newStacks < 2) return;

        textResolver.resolve(SpecWeapon.SOULREAPER_AXE).ifPresent(this::processTextEntry);
    }

    private void handleAltSpecialAttack(VarbitChanged event) {
        int newValue = event.getValue();
        SpecWeapon specWeapon;

        if (event.getVarbitId() == VarbitID.DRAGONFIRE_SHIELD_RECHARGE) {
            specWeapon = SpecWeapon.DRAGONFIRE_SHIELD;
        } else if (event.getVarbitId() == VarbitID.DRAGONFIRE_WARD_RECHARGE) {
            specWeapon = SpecWeapon.DRAGONFIRE_WARD;
        } else {
            return;
        }

        if (!config.specialAttacksEnabled()) return;

        // Setting initial value, gaining charges or emptying.
        if (newValue != 24) {
            return;
        }

        textResolver.resolve(specWeapon).ifPresent(this::processTextEntry);
    }

    private void processTextEntry(TextEntry textEntry) {
        textAndSoundService.processTextEntry(textEntry, config.shoutSpecialAttacks(), config.specialAttackSoundsEnabled());
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
