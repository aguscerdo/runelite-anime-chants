package com.aguscerdo.animechants.spells;

import com.aguscerdo.animechants.spells.model.AutoCastSpell;
import com.aguscerdo.animechants.spells.model.Spell;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.MenuAction;
import net.runelite.api.Player;
import net.runelite.api.gameval.VarbitID;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;

/**
 * Tracks pending manual casts and recognises autocasts. Outputs {@link Spell}s only.
 */
@Slf4j
@Singleton
public class SpellCastDetectorImpl implements SpellCastDetector {
    private static final Set<MenuAction> NON_INTERRUPTING_ACTIONS = EnumSet.of(
            MenuAction.EXAMINE_OBJECT, MenuAction.EXAMINE_NPC,
            MenuAction.EXAMINE_ITEM_GROUND,
            MenuAction.EXAMINE_WORLD_ENTITY,
            MenuAction.RUNELITE, MenuAction.RUNELITE_HIGH_PRIORITY,
            MenuAction.RUNELITE_LOW_PRIORITY, MenuAction.RUNELITE_OVERLAY,
            MenuAction.RUNELITE_OVERLAY_CONFIG, MenuAction.RUNELITE_INFOBOX,
            MenuAction.RUNELITE_PLAYER, MenuAction.RUNELITE_WIDGET
    );

    /**
     * A spell the player has clicked that hasn't been launched (animated) yet.
     */
    private static final class PendingCast {
        final Spell spell;
        final int expiresTick;

        PendingCast(Spell spell, int expiresTick) {
            this.spell = spell;
            this.expiresTick = expiresTick;
        }
    }

    @Inject
    private Client client;

    private Optional<PendingCast> pendingCast = Optional.empty();

    @Override
    public void reset() {
        pendingCast = Optional.empty();
    }

    @Override
    public void processManualCastEventIntent(Spell spell, MenuAction action) {
        // Unknown spell, means a click may cancel the pending event.
        if (spell.isUnknown()) {
            if (NON_INTERRUPTING_ACTIONS.contains(action)) return;

            // CANCEL only matters if it's deselecting a targeting spell, not just closing a menu
            if (action == MenuAction.CANCEL && !client.isWidgetSelected()) return;

            // walk, object/NPC/item interaction, widget clicks, etc.
            pendingCast = Optional.empty();
            return;
        }

        // Selecting a targeted spell (WIDGET_TARGET) isn't a cast yet; only the click
        // on the target (WIDGET_TARGET_ON_*) is. Don't arm on the selection click.
        if (action == MenuAction.WIDGET_TARGET) {
            log.debug("Ignoring widget targeted spell {}", spell);

            pendingCast = Optional.empty();
            return;
        }

        pendingCast = Optional.of(new PendingCast(spell, client.getTickCount() + spell.timeoutTicks()));
    }

    @Override
    public Optional<Spell> handleManualSpell(int animationId) {
        if (pendingCast.isEmpty()) {
            return Optional.empty();
        }

        PendingCast currentPendingCast = this.pendingCast.get();
        if (!currentPendingCast.spell.animationIdMatches(animationId)) {
            return Optional.empty();
        }

        // Either the spell is correct or expired, clean up either way.
        this.pendingCast = Optional.empty();

        if (client.getTickCount() > currentPendingCast.expiresTick) {
            log.debug("Pending cast expired, ignoring: {} vs {}", client.getTickCount(), currentPendingCast.expiresTick);
            return Optional.empty();
        }

        return Optional.of(currentPendingCast.spell);
    }

    @Override
    public Optional<Spell> handleAutoCast() {
        int varbit = client.getVarbitValue(VarbitID.AUTOCAST_SPELL);
        if (varbit == 0) {
            return Optional.empty();
        }

        Player me = client.getLocalPlayer();
        if (me == null || me.getInteracting() == null) {
            return Optional.empty();
        }

        log.debug("Autocast animation {} with autocast varbit {}", me.getAnimation(), varbit);

        Spell spell = AutoCastSpell.spellFromAutoCast(varbit);
        if (spell.isUnknown()) {
            log.debug("Unmapped autocast varbit {}", varbit);
            return Optional.empty();
        }

        int animationId = me.getAnimation();
        if (!spell.animationIdMatches(animationId)) {
            log.debug("Animation ID does not match autocast: {} - {}", spell, animationId);
            return Optional.empty();
        }

        return Optional.of(spell);
    }
}
