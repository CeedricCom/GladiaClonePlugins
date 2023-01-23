package me.deltaorion.siegestats.view.report;

import me.deltaorion.siegestats.model.StatSiege;

import java.io.IOException;
import java.io.Writer;

public interface ReportGenerator {

    void generate(Writer writer, StatSiege siege, int ordinal) throws IOException;
}
