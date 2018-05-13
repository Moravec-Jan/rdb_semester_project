package cz.tul.hbase.model;

import cz.tul.model.generic.InvalidRecords;

public class InvalidHbaseRecords implements InvalidRecords {
    int pocet;

    public InvalidHbaseRecords(int pocet) {
        this.pocet = pocet;
    }

    @Override
    public int getId() {
        return 0;
    }

    @Override
    public int getPocet() {
        return pocet;
    }
}
