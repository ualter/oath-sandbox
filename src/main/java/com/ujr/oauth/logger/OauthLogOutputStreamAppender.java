package com.ujr.oauth.logger;

import java.io.FilterOutputStream;
import java.io.OutputStream;

import ch.qos.logback.core.OutputStreamAppender;

/**
 * 
 * @author Ualter
 *
 * @param <E>
 */
public class OauthLogOutputStreamAppender<E> extends OutputStreamAppender<E> {


    private static final DelegatingOutputStream DELEGATING_OUTPUT_STREAM = new DelegatingOutputStream(null);

    @Override
    public void start() {
        setOutputStream(DELEGATING_OUTPUT_STREAM);
        super.start();
    }

    public static void setStaticOutputStream(OutputStream outputStream) {
        DELEGATING_OUTPUT_STREAM.setOutputStream(outputStream);
    }

    private static class DelegatingOutputStream extends FilterOutputStream {

        public DelegatingOutputStream(OutputStream out) {
			super(out);
		}

		void setOutputStream(OutputStream outputStream) {
            this.out = outputStream;
        }
    }

}