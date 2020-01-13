package org.vaadin.marcus.spring.service.impl;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.vaadin.marcus.spring.model.Message;
import org.vaadin.marcus.spring.repository.MessageRepository;
import org.vaadin.marcus.spring.service.MessageService;

import javax.transaction.Transactional;
import java.util.Collections;
import java.util.Comparator;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.web.bind.annotation.RequestBody;
import org.vaadin.marcus.spring.model.InputMessage;
import org.vaadin.marcus.spring.model.MessageStatus;

@Service
@Transactional

    
public class MessageServiceImpl implements MessageService {
    private final MessageRepository repository;
    private final PageRequest lastRequest;

    private List<Long> chekedMessages = new ArrayList<>();


    @Autowired
    public MessageServiceImpl(MessageRepository repository) {
        this.repository = repository;
        lastRequest = new PageRequest(0, 10, Sort.Direction.DESC, "id");
    }

    @Override
    public MessageStatus add(@RequestBody  Message message) {
        if(message == null) {
            System.out.println("Пришел пустой запрос на сохранение данных");

        }
        MessageStatus status = new MessageStatus();
        try {
            repository.save(message);
            status.setMessage("Сообщение успешно сохранено");
        }
        catch (Exception e) {
            status.setMessage("Во время сохранения сообщения произошла ошибка");
        }
        return status;
    }

    @Override
    public List<Message> getLast() {
        return repository.getLastMessages();
    }

    @Override
    public List<Message> getAllMessages() {
        return repository.getAllfromTable();
    }

  


    @Override
    public List<Message> getUnreadById(InputMessage message) {
         return repository.getUnreadById(message.getMessageId());
    }



    // Тут реализация метода, который проверяет каждое сообщение с ранее выдаными на уникальность

    @Override
    public String getUnreadMessages() {
        List<Message> out = new ArrayList<>();
        List<Message> unchekedMessages = repository.findAll();
        for (Message message: unchekedMessages) {
            if (!chekedMessages.contains(message.getId())) {
                chekedMessages.add(message.getId());
                out.add(message);
            }
        }
        return new Gson().toJson(out);
    }

    @Override
    public void deleteMessages() {
        repository.clearBase();
    }

  
}