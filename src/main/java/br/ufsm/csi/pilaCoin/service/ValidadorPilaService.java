package br.ufsm.csi.pilaCoin.service;

import br.ufsm.csi.pilaCoin.model.PilaCoin;

import br.ufsm.csi.pilaCoin.model.PilaValidado;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Cipher;
import java.math.BigInteger;
import java.net.URL;
import java.security.KeyPair;
import java.security.MessageDigest;
import java.security.SecureRandom;

public class ValidadorPilaService {

    public static BigInteger dificuldade = BigInteger.ZERO;
    public static KeyPair keyPair;

    @SneakyThrows
    public static boolean validarPilaColega(PilaCoin pilaDoColega) {

        RegistraUsuarioService registraUsuarioService = new RegistraUsuarioService();
        KeyPair keyPair = registraUsuarioService.leKeyPair();


        dificuldade = WebSocketClient.sessionHandler.getDificuldade();

        if (dificuldade != null) {

            SecureRandom sr = new SecureRandom();
            BigInteger mNumber = new BigInteger(128, sr);

            PilaCoin pilaCoin = PilaCoin.builder()
                    .dataCriacao(pilaDoColega.getDataCriacao())
                    .chaveCriador(pilaDoColega.getChaveCriador())
                    //.chaveCriador(null)
                    .nonce(pilaDoColega.getNonce()).build();

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);


            String pilaJson = objectMapper.writeValueAsString(pilaCoin);


            MessageDigest md = MessageDigest.getInstance("SHA-256");

            byte[] hash = md.digest(pilaJson.getBytes("UTF-8"));

            BigInteger numHash = new BigInteger(hash).abs();


            if (numHash.compareTo(dificuldade) < 0) {
                System.out.println("=============================SUCESS Pila válido======================");
                confirmaPilaValidado(pilaDoColega,hash);
                return true;


            } else {
                System.out.println("=============================WARNING pila inválido======================");
            }


        }

        return false;
    }

    @SneakyThrows
    public static void confirmaPilaValidado(PilaCoin pilaDoColega, byte[]  hashDoPilaColega){

        RegistraUsuarioService registraUsuarioService = new RegistraUsuarioService();

        KeyPair keyPair = registraUsuarioService.leKeyPair();

        PilaValidado pilaValidado = new PilaValidado();
        pilaValidado.setChavePublica(keyPair.getPublic().getEncoded());
        pilaValidado.setNonce(pilaDoColega.getNonce());
        pilaValidado.setTipo("PILA");
        pilaValidado.setHashPilaBloco(Base64.encodeBase64String(hashDoPilaColega));

        //TODO
        // Concluir a validação do pila do coleguinha, ou seja, fazer o post que eu validei corretamente

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        String pilaJsonValidado = objectMapper.writeValueAsString(pilaValidado);

        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hashPilaValidado = md.digest(pilaJsonValidado.getBytes("UTF-8"));



        Cipher cipherRSA = Cipher.getInstance("RSA");
        cipherRSA.init(Cipher.ENCRYPT_MODE, keyPair.getPrivate());

        byte[] hashCriptografada = cipherRSA.doFinal(hashPilaValidado);
        pilaValidado.setAssinatura(Base64.encodeBase64String(hashCriptografada));


        String pilaJsonValidadoCompleto = objectMapper.writeValueAsString(pilaValidado);


        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> resp = null;

        try{

            RequestEntity<String> requestEntity = RequestEntity.post(new URL(
                            "http://"+ "srv-ceesp.proj.ufsm.br:8097" + "/pilacoin/validaPilaOutroUsuario").toURI())
                    .contentType(MediaType.APPLICATION_JSON).body(pilaJsonValidadoCompleto);
            resp = restTemplate.exchange(requestEntity, String.class);




            if (resp.getStatusCode() == HttpStatus.OK){
                System.out.println("***SUCESSO NA VALIDAÇÃO DO COLEGA***");
            }
        }
        catch(Exception e){
            System.out.println("Erro ao validar pila do colega: " + e.getMessage());
            e.printStackTrace();
        }


    }

}
