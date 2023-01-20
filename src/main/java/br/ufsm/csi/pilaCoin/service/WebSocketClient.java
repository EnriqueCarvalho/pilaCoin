package br.ufsm.csi.pilaCoin.service;


import br.ufsm.csi.pilaCoin.model.Bloco;
import br.ufsm.csi.pilaCoin.model.DificuldadeRet;
import br.ufsm.csi.pilaCoin.model.PilaCoin;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;


import java.lang.reflect.Type;
import java.math.BigInteger;
import java.util.Objects;

@Service
public class WebSocketClient {


    @Getter
    public static final MyStompSessionHandler sessionHandler  =  new MyStompSessionHandler();
    //@Value("${endereco.server}")
    @Value("${endereco.server}")
    private String enderecoServer;

    @PostConstruct
    private void init() {

        System.out.println("iniciou");
        StandardWebSocketClient client = new StandardWebSocketClient();
        WebSocketStompClient stompClient = new WebSocketStompClient(client);
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
        //stompClient.connect("ws://" + enderecoServer + "/websocket/websocket", sessionHandler);
        stompClient.connect("ws://" + enderecoServer + "/websocket/websocket", sessionHandler);

        System.out.println("conectou");

    }

    @Scheduled(fixedRate = 9000)
    private void printDificuldade() {

        if (sessionHandler.dificuldade != null) {

            System.out.println("Dificuldade Atual: " + sessionHandler.dificuldade);
        }
    }

     public static class MyStompSessionHandler implements StompSessionHandler {


        @Getter
        public BigInteger dificuldade;

        @Override
        public void afterConnected(StompSession stompSession,
                                   StompHeaders stompHeaders)
        {
            stompSession.subscribe("/topic/dificuldade", this);
            stompSession.subscribe("/topic/validaMineracao", this);
            stompSession.subscribe("/topic/descobrirNovoBloco", this);
        }

        @Override
        public void handleException(StompSession stompSession, StompCommand stompCommand, StompHeaders stompHeaders, byte[] bytes, Throwable throwable) {
        }

        @Override
        public void handleTransportError(StompSession stompSession, Throwable throwable) {
        }

        @Override
        public Type getPayloadType(StompHeaders stompHeaders) {
            //System.out.println(stompHeaders);
            if (Objects.equals(stompHeaders.getDestination(), "/topic/dificuldade")) {
                return DificuldadeRet.class;
            }else if (Objects.equals(stompHeaders.getDestination(), "/topic/validaMineracao")) {
                System.out.println("CHEGOU PILA PARA MINEIRAR");
                return PilaCoin.class;
            }else if (Objects.equals(stompHeaders.getDestination(), "/topic/descobrirNovoBloco")) {
                System.out.println("CHEGOU BLOCO PARA MINEIRAR");
                return Bloco.class;
            }

            return null;

        }

        @Override
        public void handleFrame(StompHeaders stompHeaders, Object o) {
            //System.out.println("Received : " + o);
            if (o.getClass().equals(DificuldadeRet.class)){
                assert o != null;
                dificuldade = new BigInteger(((DificuldadeRet) o).getDificuldade(), 16);

            } else if (o.getClass().equals(PilaCoin.class)) {
                PilaCoin pila = PilaCoin.class.cast(o);
                ValidadorPilaService.validarPilaColega(pila);
                //System.out.println(pila.getNonce());
            } else if (o.getClass().equals(Bloco.class)) {
                Bloco bloco = Bloco.class.cast(o);
                ValidadorBlocoService.confirmaBloco(bloco);
                System.out.println(bloco.getNonce());
            }


        }
    }





}
