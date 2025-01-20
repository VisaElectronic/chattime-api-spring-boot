package com.chattime.chattime_api.config;
import com.chattime.chattime_api.dto.SocketToken;
import com.chattime.chattime_api.interceptor.AuthWSInterceptor;
import com.chattime.chattime_api.model.UserPrincipal;
import com.chattime.chattime_api.service.AuthUserDetailsService;
import com.chattime.chattime_api.service.JWTService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.messaging.access.intercept.MessageMatcherDelegatingAuthorizationManager;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.security.Principal;
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
        config.enableSimpleBroker("/channel");
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
                    try {
                        ObjectMapper objectMapper = new ObjectMapper();
                        wsToken = objectMapper.readValue(jsonToken, SocketToken.class);
                    } catch (JsonProcessingException e) {
                        return null;
                    }
                    String username = jwtService.extractUserName(wsToken.getAuthToken());
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
