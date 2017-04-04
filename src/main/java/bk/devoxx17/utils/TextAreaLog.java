package bk.devoxx17.utils;

import javafx.application.Platform;
import javafx.scene.control.TextArea;
import org.apache.log4j.Appender;
import org.apache.log4j.WriterAppender;
import org.apache.log4j.spi.LoggingEvent;

public class TextAreaLog extends WriterAppender {
    private static volatile TextArea textArea = null;

    public static void setTextArea(final TextArea textArea) {
        TextAreaLog.textArea = textArea;
    }

    @Override
    public void append(final LoggingEvent loggingEvent){
        final String message = this.layout.format(loggingEvent);

        // Append formatted message to text area using the Thread.
        try {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (textArea != null) {
                            if (textArea.getText().length() == 0) {
                                textArea.setText(message);
                            } else {
                                textArea.selectEnd();
                                textArea.insertText(textArea.getText().length(),
                                        message);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (final IllegalStateException e) {
            // ignore case when the platform hasn't yet been iniitialized
        }
    }

}
