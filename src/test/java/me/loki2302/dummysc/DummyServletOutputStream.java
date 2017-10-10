package me.loki2302.dummysc;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class DummyServletOutputStream extends ServletOutputStream {
    private final ByteArrayOutputStream baos;

    public DummyServletOutputStream(ByteArrayOutputStream baos) {
        this.baos = baos;
    }

    @Override
    public boolean isReady() {
        return false;
    }

    @Override
    public void setWriteListener(WriteListener writeListener) {
    }

    @Override
    public void write(int i) throws IOException {
        baos.write(i);
    }
}
