package com.chattime.chattime_api.config;
import com.chattime.chattime_api.dto.SocketToken;
import com.chattime.chattime_api.service.AuthUserDetailsService;
import com.chattime.chattime_api.service.JWTService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import java.util.Map;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Autowired
    private JWTService jwtService;

    @Autowired
    private AuthUserDetailsService userDetailsService;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws") // Add the interceptor here
                .setAllowedOrigins("*");
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Configure a TaskScheduler for heartbeats
        ThreadPoolTaskScheduler heartbeatScheduler = new ThreadPoolTaskScheduler();
        heartbeatScheduler.setPoolSize(1); // Usually 1 is enough for heartbeats
        heartbeatScheduler.setThreadNamePrefix("ws-heartbeat-scheduler-");
        heartbeatScheduler.initialize();

        config.enableSimpleBroker("/channel")
                .setHeartbeatValue(new long[]{10000, 20000}) // Server pings every 10s, expects client pong within 20s
                .setTaskScheduler(heartbeatScheduler); // Assign the scheduler
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
                Map<String, Object> sessionAttributes = accessor.getSessionAttributes();
                Object user = sessionAttributes.get("user");
                if (user == null && StompCommand.SEND.equals(accessor.getCommand())) {
                    // Access authentication header(s) and invoke accessor.setUser(user)
                    byte[] payload = (byte[]) message.getPayload();
                    String jsonToken = new String(payload);
                    SocketToken wsToken;
                    String username = null;
                    try {
                        ObjectMapper objectMapper = new ObjectMapper();
                        wsToken = objectMapper.readValue(jsonToken, SocketToken.class);
                        username = jwtService.extractUserName(wsToken.getAuthToken());
                    } catch (JsonProcessingException e) {
                        return null;
                    } catch (ExpiredJwtException e) {
                        accessor.setMessage("Token expired");
                        accessor.setMessageId("token_expired");
                        return message;
                    }
                    if (username != null) {
                        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                        sessionAttributes.put("user", userDetails);
                        accessor.setSessionAttributes(sessionAttributes);
                        return message;
                    }
                    return null;
                }
                return message;
            }
        });
    }
}
