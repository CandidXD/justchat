package config;

import com.candidxd.justchat.socket.WsHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * @author yzk
 * @Title: WebSocketConfig
 * @ProjectName justchat
 * @Description: TODO
 * @date 2018/9/274:28 PM
 */
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer
{
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry)
    {
        System.out.println("==========================");
        registry.addHandler(myHandler(), "/websocket/{uid}").setAllowedOrigins("*");
    }

    @Bean
    public WsHandler myHandler()
    {
        return new WsHandler();
    }
}
