package com.ceedric.event.eventmobs.model.boss;

import com.ceedric.event.eventmobs.model.Side;

import java.util.Objects;

public class BossSide implements Side {

    private final BossSideEnum side;

    public BossSide(BossSideEnum side) {
        this.side = side;
    }

    @Override
    public String getFormattedName() {
        return side.getFormattedName();
    }

    @Override
    public String getName() {
        return side.name();
    }

    @Override
    public Side getOpposite() {
        if(side.equals(BossSideEnum.PLAYERS))
            return new BossSide(BossSideEnum.BOSS);

        if(side.equals(BossSideEnum.BOSS))
            return new BossSide(BossSideEnum.PLAYERS);

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
