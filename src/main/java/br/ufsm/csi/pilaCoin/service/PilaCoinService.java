package br.ufsm.csi.pilaCoin.service;

import br.ufsm.csi.pilaCoin.model.PilaCoin;
import br.ufsm.csi.pilaCoin.model.Usuario;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.security.KeyPair;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class PilaCoinService {

    @SneakyThrows
    public List<PilaCoin>getPilas(){
        List<PilaCoin> pilas = null;
        List<PilaCoin> pilasFiltrados = new ArrayList<PilaCoin>();
        RegistraUsuarioService registraUsuarioService = new RegistraUsuarioService();
        KeyPair keyPair = registraUsuarioService.leKeyPair();

        ResponseEntity<String> resp = null;
        RestTemplate restTemplate = new RestTemplate();

        try {

        }catch (Exception e ){
            e.printStackTrace();
        }

        resp= restTemplate.getForEntity("http://"+ "srv-ceesp.proj.ufsm.br:8097" + "/pilacoin/all",String.class);

        if (resp.getStatusCode() == HttpStatus.OK){
            ObjectMapper mapper = new ObjectMapper();
            pilas  = Arrays.asList( mapper.readValue(resp.getBody() , PilaCoin[].class));



            for (PilaCoin p: pilas) {
                String minhaChave =  Base64.encodeBase64String(keyPair.getPublic().getEncoded());
                String base64 = Base64.encodeBase64String(p.getChaveCriador());
                if(base64.equals(minhaChave)){
                    pilasFiltrados.add(p);

                }
            }


            System.out.println("Todos os pilas: " + pilas.size());
            System.out.println("Meus pilas: " + pilasFiltrados.size());

        }
        return pilasFiltrados;
    }
}
