package cse360Project.service;

import cse360Project.model.HelpMessage;
import cse360Project.model.MessageType;
import cse360Project.repository.HelpMessageRepository;
import javafx.scene.control.TableView;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Service class for handling help messages.
 */
public class HelpMessageService {
    private HelpMessageRepository helpMessageRepo;

    /**
     * Constructor to initialize the HelpMessageRepository.
     *
     * @param helpMessageRepo Repository for HelpMessage operations
     */
    public HelpMessageService(HelpMessageRepository helpMessageRepo) {
        this.helpMessageRepo = helpMessageRepo;
    }

    /**
     * Sends a generic help message.
     *
     * @param userId  ID of the user sending the message
     * @param content Content of the message
     * @throws SQLException If a database access error occurs
     */
    public void sendGenericMessage(int userId, String content) throws SQLException {
        HelpMessage message = new HelpMessage(userId, MessageType.GENERIC, content);
        helpMessageRepo.insertHelpMessage(message);
    }

    /**
     * Sends a specific help message.
     *
     * @param userId  ID of the user sending the message
     * @param content Content of the message
     * @throws SQLException If a database access error occurs
     */
    public void sendSpecificMessage(int userId, String content) throws SQLException {
        HelpMessage message = new HelpMessage(userId, MessageType.SPECIFIC, content);
        helpMessageRepo.insertHelpMessage(message);
    }

    /**
     * Retrieves all help messages.
     *
     * @return List of all help messages
     * @throws SQLException If a database access error occurs
     */
    public List<HelpMessage> getAllHelpMessages() throws SQLException {
        return helpMessageRepo.getAllHelpMessages();
    }

    /**
     * Retrieves help messages sent by a specific user.
     *
     * @param userId ID of the user
     * @throws SQLException If a database access error occurs
     */
    public List<HelpMessage> getHelpMessagesByUser(int userId) throws SQLException {
        return helpMessageRepo.getHelpMessagesByUserId(userId);
    }

    public void loadHelpMessagesIntoTable(TableView<List<String>> tableView) {
        try {
            List<HelpMessage> messages = helpMessageRepo.getAllHelpMessages();
            List<List<String>> tableData = new ArrayList<>();
            for (HelpMessage message : messages) {
                tableData.add(List.of(
                    String.valueOf(message.getId()),
                    String.valueOf(message.getUserId()),
                    message.getMessageType().name(),
                    message.getContent(),
                    message.getTimestamp().toString()
                ));
            }
            tableView.getItems().setAll(tableData);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}