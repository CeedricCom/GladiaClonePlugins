package com.ceedric.event.eventmobs.view.report;

import com.ceedric.event.eventmobs.model.Event;

import java.io.IOException;
import java.io.Writer;

public interface ReportGenerator {

    void generate(Writer writer, Event world) throws IOException;
}
