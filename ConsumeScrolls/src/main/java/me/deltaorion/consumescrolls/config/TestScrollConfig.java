package me.deltaorion.consumescrolls.config;

import me.deltaorion.consumescrolls.ScrollDefinition;

import java.util.List;

public class TestScrollConfig implements ScrollConfig {

    private final List<String> toolTips;
    private final boolean title;
    private String chatRewardMessage;
    private final List<ScrollDefinition> scrolls;

    public TestScrollConfig(List<String> toolTips, boolean title, String chatRewardMessage, List<ScrollDefinition> scrolls) {
        this.toolTips = toolTips;
        this.title = title;
        this.chatRewardMessage = chatRewardMessage;
        this.scrolls = scrolls;
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
}
