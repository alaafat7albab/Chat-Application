package com.example.demo3;

import javafx.fxml.Initializable;
import javafx.scene.layout.VBox;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;


public class ChatHandler implements Runnable {
    public static ArrayList<ChatHandler> chatHandlers = new ArrayList<>();
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String playerUserName;

    public ChatHandler(Socket socket){
        try {
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.playerUserName = bufferedReader.readLine();
            chatHandlers.add(this);
            bradCastMessage("Server " + playerUserName + " has entered the chat!");
        }catch (IOException e){
            closeEveryThing(socket, bufferedWriter, bufferedReader);
        }
    }

    @Override
        public void run () {
        String messageFromPlayer;
        while (socket.isConnected()) {
            try {
                messageFromPlayer = bufferedReader.readLine();
                bradCastMessage(messageFromPlayer);
            } catch (IOException e) {
                closeEveryThing(socket, bufferedWriter, bufferedReader);
                break;
            }
        }
    }

    public void bradCastMessage(String messageToSend){
        for (ChatHandler chatHandler : chatHandlers){
            try {
                if (!chatHandler.playerUserName.equals(playerUserName)){
                    chatHandler.bufferedWriter.write(messageToSend);
                    chatHandler.bufferedWriter.newLine();
                    chatHandler.bufferedWriter.flush();
                }
            }catch (IOException e){
                closeEveryThing(socket, bufferedWriter, bufferedReader);
            }
        }

    }
    public void removeChatHandler(){
        chatHandlers.remove(this);
        bradCastMessage("Server" + playerUserName + "has left the chat!");
    }
    public void closeEveryThing(Socket socket, BufferedWriter bufferedWriter, BufferedReader bufferedReader){
        removeChatHandler();
        try {
            if (socket != null){
              socket.close();
            }
            if (bufferedWriter != null){
                bufferedWriter.close();
            }
            if (bufferedReader != null){
                bufferedReader.close();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
