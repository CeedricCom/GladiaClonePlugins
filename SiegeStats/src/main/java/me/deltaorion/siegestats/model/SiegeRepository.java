package me.deltaorion.siegestats.model;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface SiegeRepository {

    void createTown(SiegeTown town);

    SiegeTown getTown(UUID townId);

    void updateTown(SiegeTown town);

    Collection<SiegeTown> getAllTowns();

}
