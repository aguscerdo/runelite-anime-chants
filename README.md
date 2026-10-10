# Anime Chant Plugin

Are spells to boring for you? Just casting fire strike isn't exciting anymore?

The Anime Chant plugin makes you feel like a Main Character yourself! When a spell is cast your character will display
text for it.
Spell to text can be chosen from presets and/or manual overrides.

[//]: # (TODO image of explosion cast)

## Config Options

### **Enable Spell Chanting**

When enabled, displays chants when casting spells. Display nothing when disabled.

Default: enabled

### **Spell Text Preset**

Choose from a list of pre-made Spell-to-Text conversions. For a the full lists of spells refer to
the [Github Repo](https://github.com/aguscerdo/runelite-anime-chants/tree/master/src/main/resources/com/aguscerdo/animechant/chantdata).

* `None` - No presets, only overrides.
* `Spell Name` - Chant the name of the spell. E.g. "High Level Alchemy"
* `Fantasy` - Basic fantasy names of spells. E.g. "Fireball"
* `Anime Mix` - Mix of many different anime series. E.g. "Arise...", "Zoltraak", "Explosion"
* `Frieren` - Spell names from "Frieren". E.g. "Zoltraak", "Sorganeil"
* `Palworld` - Palworld Skills. E.g. "Hydro Jet", "Ignis Blast"
* `Demon Slayer` - Different breathing stances and Blood Demon Arts. E.g. "Water Breathing, First Form: Water Surface
  Slash"

Default: Fantasy

### **Custom Spell Overrides**

Custom user overrides for spell, spell categories and sound files.

Each line contains one `=` separate name to text mapping as follows:

```
fire strike=Single Spell Text
fire strike file=Single Spell File.wav
Demonbane=Spell Category Text
Demonbane=Additional text entry
```

**Usage Notes:**

- Multiple entries with the same name will all be used.
    - e.g. Two Fire Strike lines will be accepted, text is chosen randomly each time.
- Multiple file paths for the same spell or category will be applied in order.
    - For example:
    ```
    demonbane=text1
    demonbane=text2
    demonbane file=file1.wav
    demonbane file=file2.wav 
    
    Will result in:
      (demonbane -> (text1, file1.wav), demonbane -> (text2, file2.wav))
    ```
- User overrides take priority over chant data or spell names.
- Setting a spell to <b>None</b> or <b>empty</b> will disable it.
- Invalid lines will be ignored.

Default: empty

### **Spell text delay (ticks)**

Number of ticks from last display before showing text for the same spell.

This can be used to avoid spamming on auto cast. For example, if set to 10 ticks, a spell text will not be displayed
unless 10 ticks have passed from the last time it was displayed.

Default: 0

### **Spell text delay from last cast (ticks)**

Number of ticks from the last time a spell was **cast** before displaying text again. When set, at least `N` ticks must
have gone by from the last time this spell was cast to display text.

This can be used to display text only the first time a spell is cast if it may be cast a lot. For example it can be used
to avoid spamming on Fire Wave auto cast unless initiating combat.

Default: 0

### **Shout Spells**

Appends a `!` to the text if it doesn't have one already.

Wanna feel like Asta?

Default: disabled

### **Fall back to Spell Name**

If enabled, chant the spell name if no spell text is found from presets or user overrides.

This is allows text coverage for spells outside presets and overrides.

Default disabled

### **Duration Ticks**

Number of ticks text will be displayed over the character.

* Default: 3
* Minimum: 3
* Maximum: 15

### **Scratch Pad**

A convinient place to copy and paste stuff. It is not used.