package br.ufsm.csi.pilaCoin.service;

import br.ufsm.csi.pilaCoin.model.Transferencia;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Cipher;
import java.net.URL;
import java.security.KeyPair;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@Service
public class TransferenciaService {

    @SneakyThrows
    public boolean tranferePila(Transferencia transferencia){

        transferencia.setDataTransacao(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").format(Calendar.getInstance().getTime()));

        RegistraUsuarioService registraUsuarioService = new RegistraUsuarioService();

        KeyPair keyPair = registraUsuarioService.leKeyPair();

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        String transferenciaJson = objectMapper.writeValueAsString(transferencia);

        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hashTransferencia = md.digest(transferenciaJson.getBytes("UTF-8"));

        Cipher cipherRSA = Cipher.getInstance("RSA");
        cipherRSA.init(Cipher.ENCRYPT_MODE, keyPair.getPrivate());
        byte[] hashCriptografadaTransferenciaJson = cipherRSA.doFinal(hashTransferencia);

        Transferencia transferenciaAssinada = new Transferencia();
        transferenciaAssinada.setDataTransacao(transferencia.getDataTransacao());
        transferenciaAssinada.setNoncePila(transferencia.getNoncePila());
        transferenciaAssinada.setChaveUsuarioOrigem(transferencia.getChaveUsuarioOrigem());
        transferenciaAssinada.setChaveUsuarioDestino(transferencia.getChaveUsuarioDestino());
        transferenciaAssinada.setAssinatura(Base64.encodeBase64String(hashCriptografadaTransferenciaJson));
        transferenciaAssinada.setId(0l);
        transferenciaAssinada.setIdBloco("0");
        transferenciaAssinada.setStatus("");

        String transferenciaAssinadaJson = objectMapper.writeValueAsString(transferenciaAssinada);

        System.out.println(transferenciaAssinadaJson);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> resp = null;


        try{

            RequestEntity<String> requestEntity = RequestEntity.post(new URL(
                            "http://"+ "srv-ceesp.proj.ufsm.br:8097" + "/pilacoin/transfere").toURI())
                    .contentType(MediaType.APPLICATION_JSON).body(transferenciaAssinadaJson);
            resp = restTemplate.exchange(requestEntity, String.class);

            if (resp.getStatusCode() == HttpStatus.OK){
                System.out.println("***SUCESSO NA TRANSFERÊNCIA PARA O  COLEGA***");
                return true;
            }
        }
        catch(Exception e){
            System.out.println("Erro ao transferir para o colega: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }
}
