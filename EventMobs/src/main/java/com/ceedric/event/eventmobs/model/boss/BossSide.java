package com.ceedric.event.eventmobs.model.boss;

import com.ceedric.event.eventmobs.model.Side;

import java.util.Map;
import java.util.Objects;

public class BossSide implements Side {

    private final BossSideEnum side;
    private final Map<BossSideEnum,String> names;

    public BossSide(BossSideEnum side, Map<BossSideEnum, String> names) {
        this.side = side;
        this.names = names;
    }

    @Override
    public String getFormattedName() {
        return names.get(side);
    }

    @Override
    public String getName() {
        return side.name();
    }

    @Override
    public Side getOpposite() {
        if(side.equals(BossSideEnum.PLAYERS))
            return new BossSide(BossSideEnum.BOSS, names);

        if(side.equals(BossSideEnum.BOSS))
            return new BossSide(BossSideEnum.PLAYERS, names);

        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BossSide bossSide = (BossSide) o;
        return side == bossSide.side;
    }

    @Override
    public int hashCode() {
        return Objects.hash(side);
    }
}
