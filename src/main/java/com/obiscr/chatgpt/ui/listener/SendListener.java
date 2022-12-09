package com.obiscr.chatgpt.ui.listener;

import com.obiscr.chatgpt.core.DataFactory;
import com.obiscr.chatgpt.message.ChatGPTBundle;
import com.obiscr.chatgpt.settings.SettingsState;
import com.obiscr.chatgpt.ui.MainPanel;
import com.obiscr.chatgpt.ui.notifier.MyNotifier;
import com.obiscr.chatgpt.util.HttpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.event.*;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Wuzi
 */
public class SendListener implements ActionListener,KeyListener {
    private static final Logger LOG = LoggerFactory.getLogger(SendListener.class);

    private final MainPanel mainPanel;

    public SendListener(MainPanel mainPanel) {
        this.mainPanel = mainPanel;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        doActionPerformed();
    }

    public void doActionPerformed() {
        String accessToken = Objects.requireNonNull(SettingsState.getInstance()
                .getState()).getAccessToken();
        if (accessToken== null|| accessToken.isEmpty()) {
            MyNotifier.notifyError(DataFactory.getInstance().getProject(),
                    ChatGPTBundle.message("notify.config.title"),
                    ChatGPTBundle.message("notify.config.text"));
            return;
        }

        JButton button = mainPanel.getButton();
        button.setEnabled(false);
        String text = mainPanel.getSearchTextArea().
                getTextArea().getText();
        LOG.info("ChatGPT Search: {}", text);
        if (text.isEmpty()) {
            return;
        }
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        executorService.submit(() -> {
            try {
                HttpUtil.sse(text, accessToken, mainPanel.getContentPanel());
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            } finally {
                button.setEnabled(true);
            }
        });
        executorService.shutdown();
        if (!executorService.isShutdown()) {
            executorService.shutdownNow();
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_ENTER){
            e.consume();
            mainPanel.getButton().doClick();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}