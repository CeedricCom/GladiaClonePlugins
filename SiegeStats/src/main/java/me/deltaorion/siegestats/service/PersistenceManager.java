package me.deltaorion.siegestats.service;

import me.deltaorion.siegestats.model.SiegeRepository;
import me.deltaorion.siegestats.model.SiegeTown;
import me.deltaorion.siegestats.model.StatSiege;

import java.util.Map;

public class PersistenceManager {

    private final SiegeRepository repository;
    private final SiegeService service;

    public PersistenceManager(SiegeRepository repository, SiegeService service) {
        this.repository = repository;
        this.service = service;
    }


    public void saveAll() {
        for(SiegeTown town : service.getTowns()) {
            repository.createTown(town);
        }
    }

    public void loadAll() {
        for(SiegeTown town : repository.getAllTowns()) {
            service.addTown(town);
        }
    }
}
