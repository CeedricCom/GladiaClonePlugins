package me.deltaorion.siegecommandblacklist;

import java.util.List;

public interface BlackListConfig {

    List<String> getBlackList();

    void reload();
}
