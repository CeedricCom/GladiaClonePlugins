package com.ceedric.event.eventmobs.model.koth;

import com.ceedric.event.eventmobs.model.Side;
import com.gmail.nossr50.datatypes.party.Party;

import java.util.Objects;

public class KothSide implements Side {

    private final Party deathParty;
    private final Party killParty;

    public KothSide(Party deathParty, Party killParty) {
        this.deathParty = deathParty;
        this.killParty = killParty;
    }

    @Override
    public String getFormattedName() {
        return deathParty.getName();
    }

    @Override
    public String getName() {
        return deathParty.getName();
    }

    @Override
    public Side getOpposite() {
        return new KothSide(killParty,deathParty);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        KothSide kothSide = (KothSide) o;
        return Objects.equals(deathParty.getName(), kothSide.deathParty.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(deathParty);
    }
}
