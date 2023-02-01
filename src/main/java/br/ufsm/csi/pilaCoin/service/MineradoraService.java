package br.ufsm.csi.pilaCoin.service;


import br.ufsm.csi.pilaCoin.repository.PilaCoinRepository;
import br.ufsm.csi.pilaCoin.model.PilaCoin;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.SneakyThrows;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyPair;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Date;

import static java.math.BigInteger.valueOf;





@Service
public  class MineradoraService {


    @Autowired
    private PilaCoinRepository pilaCoinRepository;

    public static BigInteger dificuldade = BigInteger.ZERO;
    public static boolean mineiracaoAtiva = false;

    public static KeyPair keyPair;

    @Value("${endereco.server}")
    private static String enderecoServer;

    @PostConstruct
    private void init() {

    }

    @SneakyThrows
    public void minerar( ){
    RegistraUsuarioService registraUsuarioService = new RegistraUsuarioService();
    KeyPair keyPair = registraUsuarioService.leKeyPair();

            BigInteger numTentativas = valueOf(0);
            if (mineiracaoAtiva){
                System.out.println("+======================================+");
                System.out.println("+                                      +");
                System.out.println("+          COMEÇOU A MINEIRAR          +");
                System.out.println("+                                      +");
                System.out.println("+======================================+");
            }

                while (mineiracaoAtiva) {
                dificuldade= WebSocketClient.sessionHandler.getDificuldade();

                if(dificuldade != null){

                    SecureRandom sr = new SecureRandom();
                    BigInteger mNumber = new BigInteger(128, sr);

                    PilaCoin pilaCoin = PilaCoin.builder()
                            .dataCriacao(new Date())
                            .chaveCriador(keyPair.getPublic().getEncoded())
                            .nonce(mNumber.toString()).build();

                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);


                    String pilaJson = objectMapper.writeValueAsString(pilaCoin);


                    MessageDigest md = MessageDigest.getInstance("SHA-256");

                    byte[] hash = md.digest(pilaJson.getBytes("UTF-8"));

                    BigInteger numHash = new BigInteger(hash).abs();





                    if (numHash.compareTo(dificuldade) < 0) {
                       System.out.println(Base64.encodeBase64String(keyPair.getPublic().getEncoded()));
                        System.out.println("Minerou");
                        printHash(numHash,dificuldade);

                        registraPila(pilaJson , pilaCoin.getNonce());

                    } else {
                        numTentativas =  numTentativas.add(BigInteger.ONE);
                    }

                }


            }
        System.out.println("+======================================+");
        System.out.println("+                                      +");
        System.out.println("+          PAROU DE MINEIRAR           +");
        System.out.println("+                                      +");
        System.out.println("+======================================+");

    }




    private static void printHash(BigInteger numHash, BigInteger dificuldade) {
//            System.out.println("NumHash____: " + numHash);
//            System.out.println("Dificuldade: " + dificuldade);
    }


    @SneakyThrows
    private void registraPila(String pilaJson,String mNumber){


        byte[] publicKeyBytes = Files.readAllBytes(Path.of("pub.key"));



        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<PilaCoin> resp = null;


        try {

            RequestEntity<String> requestEntity = RequestEntity.post(new URL(
                            "http://"+ "srv-ceesp.proj.ufsm.br:8097" + "/pilacoin/").toURI())
                    .contentType(MediaType.APPLICATION_JSON).body(pilaJson);

            resp = restTemplate.exchange(requestEntity, PilaCoin.class);



            if (resp.getStatusCode() == HttpStatus.OK){
                System.out.println("***SUCESSO NA MINERAÇÃO***");
                validaPila(mNumber, resp.getBody());
            }



        } catch (Exception e) {
            System.out.println("Erro ao registrar pila: "+e.getMessage());


        }

    }



    @SneakyThrows
    public void validaPila(String nonce, PilaCoin pilaCoin){

        System.out.println();
        ResponseEntity<String> resp = null;
        RestTemplate restTemplate = new RestTemplate();
        System.out.println(Base64.encodeBase64String(pilaCoin.getChaveCriador()));

        try {


            resp= restTemplate.getForEntity("http://"+ "srv-ceesp.proj.ufsm.br:8097" + "/pilacoin/?nonce="+nonce,String.class);



            if (resp.getStatusCode() == HttpStatus.OK){

                System.out.println("***SUCESSO NA VALIDAÇÃO***");
                File arquivo;
                FileWriter escritorArquivo;
                BufferedWriter escritorBuffer;

                try {

                    this.pilaCoinRepository.save(pilaCoin);

                    arquivo = new File("meusPilasMineirados.txt");

                    if (!arquivo.exists()) {
                        arquivo.createNewFile();
                    }

                    escritorArquivo = new FileWriter(arquivo, true);
                    escritorBuffer = new BufferedWriter(escritorArquivo);

                    escritorBuffer.write(resp.getBody().toString() + "\n");
                    escritorBuffer.newLine();

                    escritorBuffer.flush();
                    escritorArquivo.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }



        } catch (Exception e) {
            System.out.println("Erro ao registrar pila: "+e.getMessage());


        }
    }

}
