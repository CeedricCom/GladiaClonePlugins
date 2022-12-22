package me.deltaorion.consumescrolls.config;

import me.deltaorion.consumescrolls.Rarity;
import me.deltaorion.consumescrolls.ScrollDefinition;

import java.util.List;
import java.util.Map;

public class TestScrollConfig implements ScrollConfig {

    private final List<String> toolTips;
    private final boolean title;
    private String chatRewardMessage;
    private final List<ScrollDefinition> scrolls;
    private final Map<Rarity,Integer> percentages;

    public TestScrollConfig(List<String> toolTips, boolean title, String chatRewardMessage, List<ScrollDefinition> scrolls, Map<Rarity, Integer> percentages) {
        this.toolTips = toolTips;
        this.title = title;
        this.chatRewardMessage = chatRewardMessage;
        this.scrolls = scrolls;
        this.percentages = percentages;
    }

    @Override
    public List<String> getToolTip() {
        return toolTips;
    }

    @Override
    public boolean showTitle() {
        return title;
    }

    @Override
    public String getChatRewardMessage() {
        return chatRewardMessage;
    }

    @Override
    public List<ScrollDefinition> getScrolls() {
        return scrolls;
    }

    @Override
    public Map<Rarity, Integer> getPercentages() {
        return percentages;
    }

    @Override
    public void reload() {

    }
}
