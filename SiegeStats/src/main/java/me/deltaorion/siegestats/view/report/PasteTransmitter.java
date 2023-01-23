package me.deltaorion.siegestats.view.report;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

public interface PasteTransmitter {

    public String send(InputStream stream) throws IOException;
}
