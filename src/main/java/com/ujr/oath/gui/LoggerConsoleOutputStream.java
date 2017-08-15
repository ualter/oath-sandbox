package com.ujr.oath.gui;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import javafx.application.Platform;
import javafx.scene.control.TextArea;

public class LoggerConsoleOutputStream extends OutputStream {
	
	private TextArea textArea;
	
	public LoggerConsoleOutputStream(TextArea textArea) {
		super();
		this.textArea = textArea;
	}

	@Override
	public void write(int bite) throws IOException {
		byte[] oneByte = {(byte) bite};
		Platform.runLater(() -> {
			try {
				textArea.appendText(new String(oneByte,"UTF-8"));
			} catch (UnsupportedEncodingException e) {
				throw new RuntimeException(e);
			}
		});
	}
	

}
