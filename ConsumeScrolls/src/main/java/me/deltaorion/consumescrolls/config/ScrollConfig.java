package me.deltaorion.consumescrolls.config;

import me.deltaorion.consumescrolls.ScrollDefinition;

import java.util.List;

public interface ScrollConfig {

    List<String> getToolTip();

    boolean showTitle();

    String getChatRewardMessage();

    List<ScrollDefinition> getScrolls();
}
