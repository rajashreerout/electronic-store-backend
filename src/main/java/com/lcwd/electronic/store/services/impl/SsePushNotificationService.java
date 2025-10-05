package com.lcwd.electronic.store.services.impl;

import com.lcwd.electronic.store.dtos.NotificationDto;
import com.lcwd.electronic.store.services.PushNotificationService;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SsePushNotificationService implements PushNotificationService {
    
    private final Map<String, List<SseEmitter>> userEmitters = new ConcurrentHashMap<>();

    @Override
    public SseEmitter subscribe(String userId) {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        
        List<SseEmitter> userEmitterList = userEmitters.computeIfAbsent(userId, k -> new ArrayList<>());
        userEmitterList.add(emitter);

        emitter.onCompletion(() -> removeEmitter(userId, emitter));
        emitter.onTimeout(() -> removeEmitter(userId, emitter));

        return emitter;
    }

    @Override
    public void sendNotification(String userId, NotificationDto notification) {
        List<SseEmitter> emitters = userEmitters.get(userId);
        if (emitters != null) {
            List<SseEmitter> deadEmitters = new ArrayList<>();
            
            emitters.forEach(emitter -> {
                try {
                    emitter.send(SseEmitter.event()
                            .name("notification")
                            .data(notification));
                } catch (IOException e) {
                    deadEmitters.add(emitter);
                }
            });
            
            emitters.removeAll(deadEmitters);
        }
    }

    private void removeEmitter(String userId, SseEmitter emitter) {
        List<SseEmitter> userEmitterList = userEmitters.get(userId);
        if (userEmitterList != null) {
            userEmitterList.remove(emitter);
            if (userEmitterList.isEmpty()) {
                userEmitters.remove(userId);
            }
        }
    }
}