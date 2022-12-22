package me.deltaorion.consumescrolls.config;

import me.deltaorion.consumescrolls.Rarity;
import me.deltaorion.consumescrolls.ScrollDefinition;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface ScrollConfig {

    List<String> getToolTip();

    boolean showTitle();

    String getChatRewardMessage();

    List<ScrollDefinition> getScrolls();

    Map<Rarity,Integer> getPercentages();

    void reload() throws ConfigurationException;
}
