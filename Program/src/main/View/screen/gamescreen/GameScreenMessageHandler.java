package main.View.screen.gamescreen;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages game messages displayed to the player.
 * This class maintains a history of game messages such as combat results,
 * item pickups, room descriptions, and other gameplay notifications.
 *
 * Features:
 * - Message history with automatic cleanup
 * - Thread-safe message operations
 * - Configurable maximum message limit
 * - Easy access to current messages for UI display
 *
 * @author Jacob Hilliker
 * @author Emanuel Feria
 * @author Vladyslav Glavatskyi
 * @version 6/13/2025
 */
class GameScreenMessageHandler {

    /** List storing the message history */
    private final List<String> messageHistory;

    /** Maximum number of messages to keep in history */
    private static final int MAX_MESSAGES = 20;

    /**
     * Constructs a new message handler with an empty message history.
     */
    public GameScreenMessageHandler() {
        this.messageHistory = new ArrayList<>();
    }

    /**
     * Adds a new message to the history.
     * If the message history exceeds the maximum limit, the oldest message
     * will be automatically removed to maintain the size constraint.
     *
     * @param message The message to add to the history
     */
    public void addMessage(String message) {
        messageHistory.add(message);
        if (messageHistory.size() > MAX_MESSAGES) {
            messageHistory.remove(0);
        }
    }

    /**
     * Gets a copy of all current messages.
     * Returns a new list to prevent external modification of the internal message history.
     *
     * @return A new list containing all current messages
     */
    public List<String> getMessages() {
        return new ArrayList<>(messageHistory);
    }

    /**
     * Clears all messages from the history.
     * This will remove all stored messages, resulting in an empty message history.
     */
    public void clearMessages() {
        messageHistory.clear();
    }

    /**
     * Gets the current number of messages in the history.
     *
     * @return The number of messages currently stored
     */
    public int getMessageCount() {
        return messageHistory.size();
    }
}