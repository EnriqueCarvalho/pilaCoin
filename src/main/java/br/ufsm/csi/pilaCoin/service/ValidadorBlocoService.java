package br.ufsm.csi.pilaCoin.service;


import br.ufsm.csi.pilaCoin.model.Bloco;
import br.ufsm.csi.pilaCoin.model.BlocoValidado;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.math.BigInteger;
import java.net.URL;
import java.security.KeyPair;
import java.security.MessageDigest;
import java.security.SecureRandom;

public class ValidadorBlocoService {

    public static BigInteger dificuldade = BigInteger.ZERO;
    public static KeyPair keyPair;

       @SneakyThrows
    public static void confirmaBloco(Bloco bloco){

        RegistraUsuarioService registraUsuarioService = new RegistraUsuarioService();
        KeyPair keyPair = registraUsuarioService.leKeyPair();


       SecureRandom sr = new SecureRandom();
       String blocoJson;


       BlocoValidado blocoValidado = new BlocoValidado();
       blocoValidado.setHashBlocoAnterior(bloco.getHashBlocoAnterior());
       blocoValidado.setId(bloco.getId());
       blocoValidado.setTransacoes(bloco.getTransacoes());
       blocoValidado.setChaveUsuarioMinerador(keyPair.getPublic().getEncoded());
       ObjectMapper objectMapper = new ObjectMapper();



        while (true){

            BigInteger mNumber = new BigInteger(128, sr);
            blocoValidado.setNonce(mNumber.toString());

            objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            blocoJson = objectMapper.writeValueAsString(blocoValidado);

            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(blocoJson.getBytes("UTF-8"));
            BigInteger numHash = new BigInteger(hash).abs();

            if (numHash.compareTo(dificuldade) < 0) {
                System.out.println("Minerou o bloco");
                break;
            }
        }


        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> resp = null;

        try{

            RequestEntity<String> requestEntity = RequestEntity.post(new URL(
                            "http://"+ "srv-ceesp.proj.ufsm.br:8097" + "/bloco/").toURI())
                    .contentType(MediaType.APPLICATION_JSON).body(blocoJson);
            resp = restTemplate.exchange(requestEntity, String.class);


            if (resp.getStatusCode() == HttpStatus.OK){
                System.out.println("***SUCESSO NA POSTAGEM DO BLOCO***");
            }
        }
        catch(Exception e){
            System.out.println("Erro ao validar bloco: " + e.getMessage());
            e.printStackTrace();
        }


    }

}
