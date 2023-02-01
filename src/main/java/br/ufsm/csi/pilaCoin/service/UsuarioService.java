package br.ufsm.csi.pilaCoin.service;

import aj.org.objectweb.asm.TypeReference;
import br.ufsm.csi.pilaCoin.model.Usuario;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.JSONPObject;
import lombok.SneakyThrows;
import org.apache.tomcat.util.json.JSONParser;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service

public class UsuarioService {

    @SneakyThrows
    public List<Usuario> getUsuarios(){
        List<Usuario> usuarios = null;

        ResponseEntity<String> resp = null;
        RestTemplate restTemplate = new RestTemplate();

        try {

        }catch (Exception e ){
            e.printStackTrace();
        }

        resp= restTemplate.getForEntity("http://"+ "srv-ceesp.proj.ufsm.br:8097" + "/usuario/all",String.class);

        if (resp.getStatusCode() == HttpStatus.OK){
            ObjectMapper mapper = new ObjectMapper();
            usuarios  = Arrays.asList( mapper.readValue(resp.getBody() , Usuario[].class));

        }
        return usuarios;
    }

}
