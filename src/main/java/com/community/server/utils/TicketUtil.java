package com.community.server.utils;

import com.community.server.gson.Ticket;
import com.community.server.gson.TicketAnswerGson;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class TicketUtil {

    public Ticket getTicket(File file) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
        //
        StringBuilder stringBuffer = new StringBuilder();
        String lineForBuffer = "";
        while ((lineForBuffer = bufferedReader.readLine()) != null) {
            stringBuffer.append(lineForBuffer);
        }

        return new Gson().fromJson(stringBuffer.toString(), Ticket.class);
    }

}
