package me.deltaorion.consumescrolls;

import java.util.*;

public class ScrollPool {

    private final Map<String, ScrollDefinition> scrolls;
    private final Map<Rarity, List<ScrollDefinition>> byRarity;
    private final ConsumeScrollPlugin plugin;

    public ScrollPool(ConsumeScrollPlugin plugin) {
        this.plugin = plugin;
        this.scrolls = new HashMap<>();
        byRarity = new HashMap<>();
        for(Rarity r : Rarity.values()) {
            byRarity.put(r,new ArrayList<>());
        }
    }

    public void addScroll(ScrollDefinition definition) {
        this.scrolls.put(definition.getName(),definition);
        this.byRarity.get(definition.getRarity()).add(definition);
    }

    public ScrollDefinition getScroll(String name) {
        return this.scrolls.get(name);
    }

    public Collection<ScrollDefinition> getScrolls() {
        return Collections.unmodifiableCollection(scrolls.values());
    }

    public List<ScrollDefinition> getScrollByRarity(Rarity rarity) {
        return byRarity.get(rarity);
    }
}
